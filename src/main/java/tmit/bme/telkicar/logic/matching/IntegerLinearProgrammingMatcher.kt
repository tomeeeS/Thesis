package tmit.bme.telkicar.logic.matching

import net.sf.javailp.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import tmit.bme.telkicar.domain.advert.Fuvar
import tmit.bme.telkicar.domain.advert.Igeny
import tmit.bme.telkicar.logic.helpers.AppContextHelper
import tmit.bme.telkicar.logic.repository.SpatialRepository
import tmit.bme.telkicar.logic.routing.Route
import java.sql.Connection


// ActionPoint: start/destination or pickup/drop-off point
// with the help of the paper 'Optimization of Dynamic Ridesharing Systems' (Febbraro et al., 2013)
class IntegerLinearProgrammingMatcher @Autowired constructor(
    private val spatialRepository: SpatialRepository,
) : ParticipantMatcher {

    private lateinit var possiblePassengersForDrivers: Map<Fuvar, Pair<Route, List<Igeny>>>
    private var fromSpecialLocation: Boolean = true

    private val logger = LoggerFactory.getLogger(javaClass.name)

    @Autowired
    private lateinit var spatialDb: Connection

    @Autowired
    private lateinit var appContextHelper: AppContextHelper
    private val specialNodeActionPoint = SpecialNodeActionPoint()

    private lateinit var passengerPlans: List<Igeny>
    private lateinit var userActionPoints: List<ActionPoint>
    private lateinit var drivers: List<Fuvar>
    private lateinit var paths: Map<Pair<ActionPoint, ActionPoint>, Route> // paths for drivers and passengers

    private lateinit var pairings: Map<Fuvar, Map<Igeny, String>> // omega in the expressions in the paper
    private lateinit var usersPaths: Map<
        ActionPoint, // user
        Map< // which paths should this user take
            Pair<ActionPoint, ActionPoint>,
            String
            >
        >
    private lateinit var lpModel: Problem
    private lateinit var solver: Solver
    private lateinit var cost: Linear

    override fun getMatches(
        fromSpecialLocation: Boolean,
        possiblePassengersForDrivers: Map<Fuvar, Pair<Route, List<Igeny>>>,
        passengers: List<Igeny>,
        drivers: List<Fuvar>,
        paths: Map<Pair<ActionPoint, ActionPoint>, Route>
    ): Map<Fuvar, List<Igeny>> {
        init(fromSpecialLocation, possiblePassengersForDrivers, passengers, drivers, paths)
        assembleVariables()
        addConstraints()
        return assembleResult(solve())
    }

    private fun assembleResult(solution: Result): Map<Fuvar, List<Igeny>> =
        pairings.mapValues { (driver, driversPassengers) ->
            driversPassengers.keys.filter {
                solution.get(pairings[driver]!![it]) == 1
            }
        }

    private fun addConstraints() {
        addPassengersCannotTravelByThemselvesConstraint()
        addPassengersCanOnlyPartnerWithOneDriverConstraint()
        addVehicleCapacitiesConstraint()
        addPathValidityConstraint()
        addDriverRouteToleranceConstraint()
    }

    private fun assembleVariables() {
        assemblePairingVariables()
        assembleUsersPathVariables()
    }

    private fun addDriverRouteToleranceConstraint() {
        usersPaths.forEach { (user, usersPaths) ->
            if (user is Fuvar)
                Linear().apply {
                    usersPaths.forEach { (endPoints, variable) ->
                        val pathCost = paths[endPoints]!!.lengthInMeters
                        add(pathCost, variable)
                    }
                    val maxDistanceForDriver = getDriverSoloRouteCost(user) * user.sofor.maxDistanceMultiplier
                    lpModel.add(this, "<=", maxDistanceForDriver)
                }
        }
    }

    private fun getDriverSoloRouteCost(driver: ActionPoint): Int {
        val startPoint = getStartPointForUser(driver)
        val destinationPoint = getDestinationPointForUser(driver)
        return paths[Pair(startPoint, destinationPoint)]!!.lengthInMeters
    }

    private fun solve(): Result {
        lpModel.setObjective(cost, OptType.MAX)
        logger.info("variables count: ${lpModel.variablesCount}")
        logger.info("constraints count: ${lpModel.constraintsCount}")

        val result = solver.solve(lpModel)

        saveRoutes(result)

        logger.info("result: $result")
        return result
    }

    private fun saveRoutes(result: Result) {
        if (appContextHelper.isMatchingToBeSavedToDb)
            assembleUsersRoutes(result).forEach { (user, routeAndActionPoints: Pair<Route, MutableList<ActionPoint>>) ->
                spatialRepository.saveMatchingRoute(
                    fromSpecialLocation,
                    routeAndActionPoints.first,
                    routeAndActionPoints.second,
                    user is Fuvar,
                    paths[getSpecialNodeActionPointPair(user)]!!.lengthInMeters,
                    if (user is Fuvar) user.sofor.distanceFlexibilityPercentage else 0.0,
                    true
                )
                if ((user is Fuvar) && routeAndActionPoints.second.size > 2)
                    spatialRepository.saveMatchingRoute(
                        fromSpecialLocation,
                        paths[getSpecialNodeActionPointPair(user)]!!,
                        getSpecialNodeActionPointPair(user).toList(),
                        true,
                        paths[getSpecialNodeActionPointPair(user)]!!.lengthInMeters,
                        user.sofor.distanceFlexibilityPercentage,
                        false
                    )
            }
    }

    private fun getSpecialNodeActionPointPair(actionPoint: ActionPoint) =
        if (fromSpecialLocation)
            Pair(specialNodeActionPoint, actionPoint)
        else
            Pair(actionPoint, specialNodeActionPoint)

    private fun assembleUsersRoutes(result: Result) = usersPaths.mapValues { (user, pathsForUser) ->
        val startPoint = getStartPointForUser(user)
        val destinationPoint = getDestinationPointForUser(user)
        var currentPoint = startPoint
        val route = Route()
        val actionPoints = mutableListOf(startPoint)
        while (currentPoint != destinationPoint) {
            val actionPair = pathsForUser.filter { (actionPointPair, variable) ->
                actionPointPair.first == currentPoint && result.get(variable) == 1
            }.keys.toList().firstOrNull()
                ?: break
            val nextPoint = actionPair.second
            route += paths[Pair(currentPoint, nextPoint)]!!
            actionPoints.add(nextPoint)
            currentPoint = nextPoint
        }
        Pair(route, actionPoints)
    }.filter { it.value.first != Route() }

    private fun init(
        fromSpecialLocation: Boolean,
        possiblePassengersForDrivers: Map<Fuvar, Pair<Route, List<Igeny>>>,
        passengers: List<Igeny>,
        drivers: List<Fuvar>,
        paths: Map<Pair<ActionPoint, ActionPoint>, Route>
    ) {
        this.fromSpecialLocation = fromSpecialLocation
        this.possiblePassengersForDrivers = possiblePassengersForDrivers
        this.passengerPlans = passengers
        this.drivers = drivers
        this.paths = paths
        lpModel = Problem()
        solver = SolverFactoryLpSolve().get()
        cost = Linear()
    }

    private fun addPathValidityConstraint() {
        usersPaths.forEach { (user, paths) ->
            val startPoint = getStartPointForUser(user)
            val destinationPoint = getDestinationPointForUser(user)
            (userActionPoints + specialNodeActionPoint).forEach { actionPoint ->
                if (user is Igeny)
                    pathValidityForPassenger(actionPoint, startPoint, destinationPoint, user, paths)
                else
                    pathValidityForDriver(actionPoint, startPoint, destinationPoint, paths)
            }
        }
    }

    private fun getDestinationPointForUser(user: ActionPoint) =
        if (fromSpecialLocation) user else specialNodeActionPoint

    private fun getStartPointForUser(user: ActionPoint) =
        if (fromSpecialLocation) specialNodeActionPoint else user

    private fun pathValidityForPassenger(
        actionPoint: ActionPoint,
        startPoint: ActionPoint,
        destinationPoint: ActionPoint,
        passenger: ActionPoint,
        paths: Map<Pair<ActionPoint, ActionPoint>, String>
    ) {
        Linear().apply {
            paths.filter { it.key.first == actionPoint }.values.forEach { // outgoing edges
                add(1, it)
            }
            paths.filter { it.key.second == actionPoint }.values.forEach { // incoming edges
                add(-1, it)
            }
            if (actionPoint == startPoint)
                tieWithPairingVariables(this, passenger, -1)
            if (actionPoint == destinationPoint)
                tieWithPairingVariables(this, passenger, 1)
            lpModel.add(this, "=", 0)
        }
    }

    private fun tieWithPairingVariables(constraint: Linear, passenger: ActionPoint, coefficient: Long) {
        pairings.forEach { (_, passengers) ->
            passengers[passenger]?.let { constraint.add(coefficient, it) }
        }
    }

    private fun pathValidityForDriver(
        actionPoint: ActionPoint,
        startPoint: ActionPoint,
        destinationPoint: ActionPoint,
        paths: Map<Pair<ActionPoint, ActionPoint>, String>
    ) {
        val outDegreeMax1 = Linear()
        val degree = when (actionPoint) {
            startPoint -> 1
            destinationPoint -> -1
            else -> 0
        }
        Linear().apply {
            paths.filter { it.key.first == actionPoint }.values.forEach { // outgoing edges
                add(1, it)
                outDegreeMax1.add(1, it)
            }
            paths.filter { it.key.second == actionPoint }.values.forEach { // incoming edges
                add(-1, it)
            }
            lpModel.add(this, "=", degree)
        }
        lpModel.add(outDegreeMax1, "<=", 1)
    }

    private fun addVehicleCapacitiesConstraint() {
        pairings.entries.forEachIndexed { i, driversPairings ->
            Linear().apply {
                driversPairings.value.entries.forEach { (igeny, variable) ->
                    add(igeny.utasokSzama, variable)
                }
                lpModel.add(this, "<=", drivers[i].szabadHely)
            }
        }
    }

    private fun addPassengersCanOnlyPartnerWithOneDriverConstraint() {
        passengerPlans.forEach { passenger ->
            Linear().apply {
                pairings.values.forEach { driversPairings ->
                    if (driversPairings.containsKey(passenger))
                        add(1, driversPairings[passenger])
                }
                lpModel.add(this, "<=", 1)
            }
        }
    }

    private fun addPassengersCannotTravelByThemselvesConstraint() {
        pairings.forEach { (driver, driversPairings) ->
            driversPairings.forEach { (passenger, areTheyPaired) ->
                val driversPaths = usersPaths[driver]!!
                val passengersPaths = usersPaths[passenger]!!

                passengersPaths.keys.forEach { passengerActionPoints ->
                    // omega_d,p + x^p_i,j - x^d_i,j <= 1
                    Linear().apply {
                        add(1, areTheyPaired)
                        add(1, passengersPaths[passengerActionPoints])
                        if (driversPaths.containsKey(passengerActionPoints))
                            add(-1, driversPaths[passengerActionPoints])
                        lpModel.add(this, "<=", 1)
                    }
                }
            }
        }
    }

    private fun assembleUsersPathVariables() {
        usersPaths = userActionPoints.associateWith { user ->
            val pathWeight = if (user is Igeny) 0 else -1
            paths
                .filterNot { filterOutActionPointPairsForUsersPaths(user, it.key) }
                .mapValues {
                    val pathVar = "usersPaths of ${user.lpInfo()}: ${actionPointPairToVariable(it.key)}"
                    cost.add(pathWeight * (paths[it.key]?.lengthInMeters ?: 0), pathVar)
                    lpModel.setVarType(pathVar, VarType.BOOL)
                    pathVar
                }
        }
    }

    private fun passengerIsNeverAtDriverEndpoint(actionPoints: Pair<ActionPoint, ActionPoint>) =
        if (fromSpecialLocation)
            actionPoints.second is Fuvar
        else
            actionPoints.first is Fuvar

    private fun hasDifferentDriver(
        key: Pair<ActionPoint, ActionPoint>,
        driver: ActionPoint
    ) = key.first is Fuvar && key.first != driver ||
        key.second is Fuvar && key.second != driver

    private fun filterOutActionPointPairsForUsersPaths(
        user: ActionPoint,
        actionPoints: Pair<ActionPoint, ActionPoint>
    ) =
        if (user is Igeny)
            passengerIsNeverAtDriverEndpoint(actionPoints) ||
                fromToPassengersOwnEndpoint(actionPoints, user)
        else
            hasDifferentDriver(actionPoints, user) ||
                hasIncompatiblePassenger(actionPoints, user)

    private fun hasIncompatiblePassenger(actionPoints: Pair<ActionPoint, ActionPoint>, driver: ActionPoint) =
        hasIncompatiblePassenger(actionPoints.first, driver) ||
            hasIncompatiblePassenger(actionPoints.second, driver)

    private fun hasIncompatiblePassenger(actionPoint: ActionPoint, driver: ActionPoint) =
        actionPoint is Igeny && !possiblePassengersForDrivers[driver]!!.second.contains(actionPoint)

    private fun fromToPassengersOwnEndpoint(actionPoints: Pair<ActionPoint, ActionPoint>, user: ActionPoint) =
        if (fromSpecialLocation)
            actionPoints.first == user
        else
            actionPoints.second == user

    private fun actionPointPairToVariable(actionPoints: Pair<ActionPoint, ActionPoint>) =
        "${actionPoints.first.lpInfo()} - ${actionPoints.second.lpInfo()}"

    private fun assemblePairingVariables() {
        pairings =
            possiblePassengersForDrivers.entries.associate { passengersForDriver ->
                passengersForDriver.key to
                    passengersForDriver.value.second.associateWith {
                        val pairingVar = "pairings ${actionPointPairToVariable(Pair(passengersForDriver.key, it))}"
                        cost.add(pairingWeight + it.utasokSzama * numberOfPeoplePerPassengerRequestWeight, pairingVar)
                        lpModel.setVarType(pairingVar, VarType.BOOL)
                        pairingVar
                    }
            }
        userActionPoints = pairings.keys.toList() + passengerPlans
    }

    companion object {
        private const val numberOfPeoplePerPassengerRequestWeight = 1e6
        private const val pairingWeight = numberOfPeoplePerPassengerRequestWeight * 1e2
    }

}
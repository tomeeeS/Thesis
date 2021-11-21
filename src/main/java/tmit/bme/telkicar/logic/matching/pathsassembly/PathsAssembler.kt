package tmit.bme.telkicar.logic.matching.pathsassembly

import tmit.bme.telkicar.domain.advert.Fuvar
import tmit.bme.telkicar.domain.advert.Igeny
import tmit.bme.telkicar.domain.advert.TravelPlan
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNetwork
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNode
import tmit.bme.telkicar.logic.matching.ActionPoint
import tmit.bme.telkicar.logic.matching.SpecialNodeActionPoint
import tmit.bme.telkicar.logic.routing.Route
import tmit.bme.telkicar.logic.routing.routeplanner.pointtopoint.RoutePlanner

abstract class PathsAssembler(
    private val routePlanner: RoutePlanner,
    private val roadNetwork: RoadNetwork,
) {

    private lateinit var possiblePassengersForDrivers: Map<Fuvar, Pair<Route, List<Igeny>>>

    protected val specialNodeActionPoint = SpecialNodeActionPoint()

    protected lateinit var passengers: List<Igeny>
    protected lateinit var drivers: List<Fuvar>
    private lateinit var paths: MutableMap<Pair<ActionPoint, ActionPoint>, Route> // paths for drivers and passengers
    protected lateinit var specialLocationNode: RoadNode

    protected lateinit var passengersDriversFromList: List<TravelPlan>
    protected lateinit var passengersDriversToList: List<TravelPlan>

    fun assemblePaths(
        possiblePassengersForDrivers: Map<Fuvar, Pair<Route, List<Igeny>>>,
        passengers: List<Igeny>,
        drivers: List<Fuvar>
    ): Map<Pair<ActionPoint, ActionPoint>, Route> {
        init(possiblePassengersForDrivers, passengers, drivers)

        assembleSpecialNodeDriverEndPointPaths()
        assembleSpecialNodePassengerEndPointPaths()
        assemblePassengersDriversPaths()
        return paths
    }

    protected open fun init(
        possiblePassengersForDrivers: Map<Fuvar, Pair<Route, List<Igeny>>>,
        passengers: List<Igeny>,
        drivers: List<Fuvar>
    ) {
        this.possiblePassengersForDrivers = possiblePassengersForDrivers
        this.passengers = passengers
        this.drivers = drivers
        specialLocationNode = roadNetwork.specialLocationNode
    }

    private fun assemblePassengersDriversPaths() {
        passengersDriversFromList.forEach { fromPlan ->
            passengersDriversToList.forEach { toPlan ->
                if (fromPlan != toPlan) // no path for the same point
                    paths[Pair(fromPlan, toPlan)] =
                        computeRouteLength(fromPlan.definingEndpointRoadNode, toPlan.definingEndpointRoadNode)
            }
        }
    }


    private fun assembleSpecialNodePassengerEndPointPaths() {
        passengers.forEach {
            paths[getSpecialNodeActionPointPair(it)] = computeSpecialNodePassengerEndPointPath(it)
        }
    }

    protected fun computeRouteLength(node1: RoadNode, node2: RoadNode): Route =
        routePlanner.planRoute(
            node1,
            node2
        )

    private fun assembleSpecialNodeDriverEndPointPaths() {
        paths =
            possiblePassengersForDrivers
                .map {
                    getSpecialNodeActionPointPair(it.key) to
                        it.value.first
                }.toMap().toMutableMap()
    }

    abstract fun computeSpecialNodePassengerEndPointPath(igeny: Igeny): Route
    abstract fun getSpecialNodeActionPointPair(actionPoint: ActionPoint): Pair<ActionPoint, ActionPoint>

}

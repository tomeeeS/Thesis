package tmit.bme.telkicar.logic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import org.springframework.test.context.junit4.SpringRunner
import tmit.bme.telkicar.domain.advert.Fuvar
import tmit.bme.telkicar.domain.advert.Igeny
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNetwork
import tmit.bme.telkicar.logic.helpers.AppContextHelper
import tmit.bme.telkicar.logic.matching.ActionPoint
import tmit.bme.telkicar.logic.matching.ParticipantMatcher
import tmit.bme.telkicar.logic.matching.partnerfilter.FilterType
import tmit.bme.telkicar.logic.matching.partnerfilter.PartnerFilterer
import tmit.bme.telkicar.logic.matching.pathsassembly.FromSpecialNodePathsAssembler
import tmit.bme.telkicar.logic.matching.pathsassembly.ToSpecialNodePathsAssembler
import tmit.bme.telkicar.logic.repository.SpatialRepository
import tmit.bme.telkicar.logic.routing.Route
import tmit.bme.telkicar.logic.routing.routeplanner.pointtopoint.RoutePlanner
import tmit.bme.telkicar.service.FuvarService
import tmit.bme.telkicar.service.IgenyService
import java.sql.Connection
import java.sql.SQLException
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@Component
@RunWith(SpringRunner::class)
@SpringBootTest
class ParticipantMatcherTest @Autowired constructor(
    private val fuvarService: FuvarService,
    private val igenyService: IgenyService,
    private val partnerFilterer: PartnerFilterer,
    private val routePlanner: RoutePlanner,
    private val participantMatcher: ParticipantMatcher,
    private val spatialDb: Connection,
    private val roadNetwork: RoadNetwork,
    private val appContextHelper: AppContextHelper,
    private val spatialRepository: SpatialRepository,
) {

    @OptIn(ExperimentalTime::class)
    @Test
    fun testMatching() {
        if (appContextHelper.isMatchingToBeSavedToDb)
            deleteMatchingData()

        val isFromSpecialLocation = true
        val fuvarok = // only constructing the RoadNode with id was not enough, the path finder uses this object's
            // coordinates too, not the one from the graph with the same id
            fuvarService.getAktivFuvarok(isFromSpecialLocation)
//                .drop(5)
//                .take(4)
                .associateWith {
                    val route = routePlanner.planRoute(
                        it.startRoadNode,
                        it.destinationRoadNode,
                        // only constructing the RoadNode with id was not enough, the path finder uses this object's
                        // coordinates too, not the one from the graph with the same id
                    )
                    spatialRepository.saveRoute(
                        route,
                        isDriversRoute = true,
                        isFilteredOut = false,
                        isBeeLine = false,
                        it.definingEndpointGeoPoint
                    )
                    route
                }
                .onEach {
                    spatialRepository.saveEndpoint(
                        it.key.definingEndpointGeoPoint,
                        isDrivers = true,
                        it.key.capacity
                    )
                }
        val igenyek = igenyService.getAktivIgenyek(isFromSpecialLocation)
//            .drop(2)
//            .take(4)
//            .onEach { logger.info("${it.lpInfo()} number of passengers: ${it.utasokSzama}") }
            .onEach { spatialRepository.saveEndpoint(it.definingEndpointGeoPoint, isDrivers = false, it.utasokSzama) }
        val possiblePassengersForDrivers = partnerFilterer.prefilter(
            fuvarok,
            igenyek,
            FilterType.CAPACITY,
            FilterType.BEE_LINE,
            FilterType.ROUTE_LENGTH,
        )
//        savePassengerBeeLines(igenyek, fuvarok, possiblePassengersForDrivers, isFromSpecialLocation)

        val passengers = initPassengers(possiblePassengersForDrivers)
        val drivers = possiblePassengersForDrivers.keys.toList()
        val paths = assemblePaths(isFromSpecialLocation, possiblePassengersForDrivers, passengers, drivers)

        val time = measureTime {
            val matches = participantMatcher.getMatches(
                isFromSpecialLocation,
                possiblePassengersForDrivers,
                passengers,
                drivers,
                paths
            )
        }
        println(time)
    }

    private fun deleteMatchingData() {
        val deleteQuery =
            "delete from routes where 1=1; delete from plan_endpoints where 1=1; delete from matches where 1=1;"
        try {
            spatialDb.prepareStatement(deleteQuery).use { statement ->
                statement.execute()
            }
        } catch (ex: SQLException) {
            println(ex)
        }
    }

    private fun savePassengerBeeLines(
        igenyek: List<Igeny>,
        fuvarok: Map<Fuvar, Route>,
        possiblePassengersForDrivers: Map<Fuvar, Pair<Route, List<Igeny>>>,
        isFromSpecialLocation: Boolean
    ) {
        val fuvar = fuvarok.keys.first()
        igenyek.forEach {
            val actionPointsList = if (isFromSpecialLocation)
                listOf(
                    it.departureGeoPoint,
                    it.destinationGeoPoint,
                    fuvar.destinationGeoPoint,
                )
            else
                listOf(
                    fuvar.departureGeoPoint,
                    it.departureGeoPoint,
                    it.destinationGeoPoint,
                )
            val route = Route(actionPointsList)
            val isFilteredOut = !possiblePassengersForDrivers[fuvar]!!.second.contains(it)
            spatialRepository.saveRoute(
                route,
                isDriversRoute = false,
                isFilteredOut = isFilteredOut,
                isBeeLine = true,
                it.definingEndpointGeoPoint
            )
        }
    }

    private fun assemblePaths(
        fromSpecialNode: Boolean,
        possiblePassengersForDrivers: Map<Fuvar, Pair<Route, List<Igeny>>>,
        passengers: List<Igeny>,
        drivers: List<Fuvar>
    ): Map<Pair<ActionPoint, ActionPoint>, Route> {
        val pathsAssembler = if (fromSpecialNode)
            FromSpecialNodePathsAssembler(routePlanner, roadNetwork)
        else
            ToSpecialNodePathsAssembler(routePlanner, roadNetwork)
        return pathsAssembler.assemblePaths(
            possiblePassengersForDrivers,
            passengers,
            drivers
        )
    }

    private fun initPassengers(possiblePassengersForDrivers: Map<Fuvar, Pair<Route, List<Igeny>>>) =
        possiblePassengersForDrivers.values.map { it.second }.flatten().toSet().toList()

}
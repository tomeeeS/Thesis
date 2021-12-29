package tmit.bme.telkicar.logic.matching.partnerfilter.filter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import tmit.bme.telkicar.domain.advert.Fuvar
import tmit.bme.telkicar.domain.advert.Igeny
import tmit.bme.telkicar.logic.matching.partnerfilter.PartnerFilter
import tmit.bme.telkicar.logic.repository.SpatialRepository
import tmit.bme.telkicar.logic.routing.Route
import tmit.bme.telkicar.logic.routing.routeplanner.pointtopoint.RoutePlanner

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON) // 1 for the whole program
class RouteLengthPartnerFilter @Autowired constructor(
    private val routePlanner: RoutePlanner,
    private val spatialRepository: SpatialRepository,
) : PartnerFilter() {

    override fun isPairingPossible(pair: Pair<Fuvar, Route>, passengerRequest: Igeny): Boolean {
        val routeWithPassengerPickedUp = routePlanner.planRouteForNodeString(
            pair.first.startRoadNode,
            passengerRequest.startRoadNode,
            passengerRequest.destinationRoadNode,
            pair.first.destinationRoadNode,
        )
        val maxTravelLengthOfSofor =
            pair.second.lengthInMeters * pair.first.sofor.maxDistanceMultiplier
        val isPossible = routeWithPassengerPickedUp.lengthInMeters < maxTravelLengthOfSofor
        spatialRepository.saveRoute(
            routeWithPassengerPickedUp,
            isDriversRoute = false,
            isFilteredOut = !isPossible,
            isBeeLine = false,
            passengerRequest.definingEndpointGeoPoint
        )
        return isPossible
    }
}
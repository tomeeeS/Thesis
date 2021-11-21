package tmit.bme.telkicar.logic.matching.pathsassembly

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import tmit.bme.telkicar.domain.advert.Fuvar
import tmit.bme.telkicar.domain.advert.Igeny
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNetwork
import tmit.bme.telkicar.logic.matching.ActionPoint
import tmit.bme.telkicar.logic.routing.Route
import tmit.bme.telkicar.logic.routing.routeplanner.pointtopoint.RoutePlanner

@Component
@Scope("prototype") // new instance every time
class FromSpecialNodePathsAssembler @Autowired constructor(
    routePlanner: RoutePlanner,
    roadNetwork: RoadNetwork,
) : PathsAssembler(routePlanner, roadNetwork) {

    override fun init(
        possiblePassengersForDrivers: Map<Fuvar, Pair<Route, List<Igeny>>>,
        passengers: List<Igeny>,
        drivers: List<Fuvar>
    ) {
        super.init(possiblePassengersForDrivers, passengers, drivers)
        passengersDriversFromList = passengers
        passengersDriversToList = passengers + drivers
    }

    override fun computeSpecialNodePassengerEndPointPath(igeny: Igeny) =
        computeRouteLength(specialLocationNode, igeny.destinationRoadNode)

    override fun getSpecialNodeActionPointPair(actionPoint: ActionPoint) =
        Pair(specialNodeActionPoint, actionPoint)

}
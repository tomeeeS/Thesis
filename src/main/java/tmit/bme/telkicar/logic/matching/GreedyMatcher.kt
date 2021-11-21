package tmit.bme.telkicar.logic.matching

import tmit.bme.telkicar.domain.advert.Fuvar
import tmit.bme.telkicar.domain.advert.Igeny
import tmit.bme.telkicar.logic.routing.Route

class GreedyMatcher : ParticipantMatcher {

    override fun getMatches(
        fromSpecialLocation: Boolean,
        possiblePassengersForDrivers: Map<Fuvar, Pair<Route, List<Igeny>>>,
        passengers: List<Igeny>,
        drivers: List<Fuvar>,
        paths: Map<Pair<ActionPoint, ActionPoint>, Route>
    ): Map<Fuvar, List<Igeny>> {
        return emptyMap()
    }
}
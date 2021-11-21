package tmit.bme.telkicar.logic.matching

import tmit.bme.telkicar.domain.advert.Fuvar
import tmit.bme.telkicar.domain.advert.Igeny
import tmit.bme.telkicar.logic.routing.Route

interface ParticipantMatcher {

    /**
     * @return lists of passenger travel plans as possible passenger matches for every driver travel plan.
     * The passengers are in decreasing order of global optimality.
     * The keys of the returned map are the same passenger postings as the input parameter's.
     */
    fun getMatches(
        fromSpecialLocation: Boolean = true,
        possiblePassengersForDrivers: Map<Fuvar, Pair<Route, List<Igeny>>>,
        passengers: List<Igeny>,
        drivers: List<Fuvar>,
        paths: Map<Pair<ActionPoint, ActionPoint>, Route> // paths for drivers and passengers
    ): Map<Fuvar, List<Igeny>>
}
package tmit.bme.telkicar.logic.matching.partnerfilter

import tmit.bme.telkicar.domain.advert.Fuvar
import tmit.bme.telkicar.domain.advert.Igeny
import tmit.bme.telkicar.logic.matching.ActionPoint
import tmit.bme.telkicar.logic.routing.Route

data class FilterDataOfDriver(
    val driverRoute: Route,
    val possiblePassengersForDriver: List<Igeny>
)

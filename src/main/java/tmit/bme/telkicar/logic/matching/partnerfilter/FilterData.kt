package tmit.bme.telkicar.logic.matching.partnerfilter

import tmit.bme.telkicar.domain.advert.Fuvar
import tmit.bme.telkicar.logic.matching.ActionPoint
import tmit.bme.telkicar.logic.routing.Route

// can be used to fill this when filtering partners and reuse paths (routes) from it at matching (for some passenger paths)
data class FilterData(
    val driverDatas: Map<Fuvar, FilterDataOfDriver>,
    val otherComputedRoutes: Map<Pair<ActionPoint, ActionPoint>, Route>
)

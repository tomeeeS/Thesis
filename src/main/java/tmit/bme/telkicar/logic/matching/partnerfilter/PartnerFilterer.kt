package tmit.bme.telkicar.logic.matching.partnerfilter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import tmit.bme.telkicar.domain.advert.Fuvar
import tmit.bme.telkicar.domain.advert.Igeny
import tmit.bme.telkicar.logic.matching.partnerfilter.filter.BeeLineDistancePartnerFilter
import tmit.bme.telkicar.logic.matching.partnerfilter.filter.CapacityPartnerFilter
import tmit.bme.telkicar.logic.matching.partnerfilter.filter.RouteLengthPartnerFilter
import tmit.bme.telkicar.logic.routing.Route

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON) // 1 for the whole program
class PartnerFilterer @Autowired constructor(
    private val capacityPartnerFilter: CapacityPartnerFilter,
    private val beeLineDistancePartnerFilter: BeeLineDistancePartnerFilter,
    private val routeLengthPartnerFilter: RouteLengthPartnerFilter,
) {

    fun prefilter(
        fuvarokWithLengthInMeters: Map<Fuvar, Route>,
        igenyek: List<Igeny>,
        vararg filterTypes: FilterType
    ): Map<Fuvar, Pair<Route, List<Igeny>>> =
        filterTypes.sorted() // sorting the filters first because they are in increasing order of computational intensity
            .fold(initFiltering(fuvarokWithLengthInMeters, igenyek)) { acc, filterType ->
                getFilter(filterType).prefilter(acc)
            }

    private fun getFilter(filterType: FilterType): PartnerFilter {
        return when (filterType) {
            FilterType.CAPACITY -> capacityPartnerFilter
            FilterType.BEE_LINE -> beeLineDistancePartnerFilter
            FilterType.ROUTE_LENGTH -> routeLengthPartnerFilter
        }
    }

    private fun initFiltering(
        fuvarokWithLengthInMeters: Map<Fuvar, Route>,
        igenyek: List<Igeny>
    ): Map<Fuvar, Pair<Route, List<Igeny>>> =
        fuvarokWithLengthInMeters.map { fuvarWithRoute: Map.Entry<Fuvar, Route> ->
            fuvarWithRoute.key to
                Pair(
                    fuvarWithRoute.value,
                    igenyek
                )
        }.toMap()

}
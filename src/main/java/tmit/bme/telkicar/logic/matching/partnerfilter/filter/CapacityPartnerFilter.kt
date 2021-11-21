package tmit.bme.telkicar.logic.matching.partnerfilter.filter

import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import tmit.bme.telkicar.domain.advert.Fuvar
import tmit.bme.telkicar.domain.advert.Igeny
import tmit.bme.telkicar.logic.matching.partnerfilter.PartnerFilter
import tmit.bme.telkicar.logic.routing.Route

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON) // 1 for the whole program
class CapacityPartnerFilter: PartnerFilter() {

    override fun isPairingPossible(pair: Pair<Fuvar, Route>, passengerRequest: Igeny) =
        pair.first.capacity >= passengerRequest.utasokSzama
}
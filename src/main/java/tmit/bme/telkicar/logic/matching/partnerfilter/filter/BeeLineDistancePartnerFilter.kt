package tmit.bme.telkicar.logic.matching.partnerfilter.filter

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import tmit.bme.telkicar.domain.advert.Fuvar
import tmit.bme.telkicar.domain.advert.Igeny
import tmit.bme.telkicar.logic.geography.distance.DistanceMetric
import tmit.bme.telkicar.logic.helpers.AppContextHelper
import tmit.bme.telkicar.logic.matching.partnerfilter.PartnerFilter
import tmit.bme.telkicar.logic.routing.Route

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON) // 1 for the whole program
class BeeLineDistancePartnerFilter @Autowired constructor(
    appContext: AppContextHelper
) : PartnerFilter() {

    var distanceMetric: DistanceMetric = appContext.getAccurateDistanceMetric()

    override fun isPairingPossible(pair: Pair<Fuvar, Route>, passengerRequest: Igeny): Boolean {
        val routeLengthMinBoundWithPassengerPickedUp =
            distanceMetric.getPointListDistanceInMeters(
                pair.first.departureGeoPoint,
                passengerRequest.departureGeoPoint,
                passengerRequest.destinationGeoPoint,
                pair.first.destinationGeoPoint,
            )
        val maxTravelLengthOfSofor = pair.second.lengthInMeters * pair.first.sofor.maxDistanceMultiplier
        return routeLengthMinBoundWithPassengerPickedUp < maxTravelLengthOfSofor
    }
}
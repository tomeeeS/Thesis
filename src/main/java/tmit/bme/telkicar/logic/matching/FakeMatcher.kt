package tmit.bme.telkicar.logic.matching

import tmit.bme.telkicar.domain.advert.Fuvar
import tmit.bme.telkicar.domain.advert.Igeny
import kotlin.math.ceil

//class FakeMatcher : ParticipantMatcher {
//
//    override fun getMatches(
//        fuvarok: List<Fuvar>,
//        igenyek: List<Igeny>
//    ): Map<Fuvar, List<Igeny>> =
//        possiblePassengersForDrivers.mapValues {
//            it.value.subList(0, ceil(it.value.size /*/ it.key.id*/.toDouble()).toInt())
//        }.also { Thread.sleep(2500) } // simulate computation time
//}
package tmit.bme.telkicar.logic.matching.partnerfilter

import org.slf4j.LoggerFactory
import tmit.bme.telkicar.domain.advert.Fuvar
import tmit.bme.telkicar.domain.advert.Igeny
import tmit.bme.telkicar.logic.routing.Route

abstract class PartnerFilter {

    protected val logger = LoggerFactory.getLogger(javaClass.name)

    /**
     * Filters the participants based on quickly and easily testable conditions before running an optimality matching.
     * @param driversWithPassengers The map of considered driver travel plans with their routes and the
     *   list of considered passenger travel plans
     * @return lists of possible passenger travel plans for every driver travel plan.
     */
    fun prefilter(
        driversWithPassengers: Map<Fuvar, Pair<Route, List<Igeny>>>,
    ): Map<Fuvar, Pair<Route, List<Igeny>>> =
        driversWithPassengers.mapValues { (driver, routeAndPassengers) ->
            Pair(
                routeAndPassengers.first,
                routeAndPassengers.second.filter { isPairingPossible(Pair(driver, routeAndPassengers.first), it) }
                    .also {
                        logger.info(
                            "${driver.lpInfo()} passenger count after prefilter: ${it.size}," +
                                " capacity: ${driver.capacity}"
                        )
                    }
            )
        }.also {
            logger.info(
                "${this.javaClass.simpleName} filter finished"
            )
        }

    abstract fun isPairingPossible(pair: Pair<Fuvar, Route>, passengerRequest: Igeny): Boolean

}
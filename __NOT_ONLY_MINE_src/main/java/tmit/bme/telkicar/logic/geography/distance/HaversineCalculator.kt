package tmit.bme.telkicar.logic.geography.distance

import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import kotlin.math.*

/**
 * Jason Winn
 * http://jasonwinn.org
 * Created July 10, 2013
 *
 * Description: Small class that provides approximate distance between
 * two points using the Haversine formula.
 *
 * Call in a static context:
 * Haversine.distance(47.6788206, -122.3271205,
 * 47.6788206, -122.5271205)
 * --> 14.973190481586224 [km]
 *
 */
class HaversineCalculator {

    fun distance(startLat: Double, startLong: Double,
                 endLat: Double, endLong: Double): Double {
        var startLat = startLat
        var endLat = endLat
        val dLat = Math.toRadians(endLat - startLat)
        val dLong = Math.toRadians(endLong - startLong)
        startLat = Math.toRadians(startLat)
        endLat = Math.toRadians(endLat)
        val a = haversine(dLat) + cos(startLat) * cos(endLat) * haversine(dLong)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return Companion.EARTH_RADIUS * c // <-- d
    }

    private fun haversine(`val`: Double): Double {
        return sin(`val` / 2).pow(2.0)
    }

    companion object {
        private const val EARTH_RADIUS = 6371 // Approx Earth radius in KM
    }
}
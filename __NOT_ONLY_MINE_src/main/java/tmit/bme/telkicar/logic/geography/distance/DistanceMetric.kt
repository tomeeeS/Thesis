package tmit.bme.telkicar.logic.geography.distance

import tmit.bme.telkicar.logic.geography.GeoPoint

abstract class DistanceMetric {

    abstract fun getDistanceInMeters(point1: GeoPoint, point2: GeoPoint): Double

    fun getPointListDistanceInMeters(vararg points: GeoPoint) =
        points.mapIndexed { index, geoPoint ->
            if (index > 0)
                getDistanceInMeters(geoPoint, points[index - 1])
            else
                0.0
        }.sumByDouble { it }
}
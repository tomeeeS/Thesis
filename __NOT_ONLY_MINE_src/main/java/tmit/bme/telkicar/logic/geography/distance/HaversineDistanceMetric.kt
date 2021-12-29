package tmit.bme.telkicar.logic.geography.distance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import tmit.bme.telkicar.logic.geography.GeoPoint

// measures spherical distance
class HaversineDistanceMetric(
    private val haversine: HaversineCalculator
) : DistanceMetric() {
    override fun getDistanceInMeters(point1: GeoPoint, point2: GeoPoint) =
        haversine.distance(
            point1.latitude,
            point1.longitude,
            point2.latitude,
            point2.longitude
        ) * 1000 // convert to meters from km
}
package tmit.bme.telkicar.logic.geography.distance

import org.gavaghan.geodesy.Ellipsoid
import org.gavaghan.geodesy.GeodeticCalculator
import org.gavaghan.geodesy.GlobalPosition
import tmit.bme.telkicar.logic.geography.GeoPoint

class VincentyDistanceMetric : DistanceMetric() {

    override fun getDistanceInMeters(point1: GeoPoint, point2: GeoPoint) =
        GeodeticCalculator().calculateGeodeticMeasurement(
            Ellipsoid.WGS84,
            GlobalPosition(point1.latitude, point1.longitude, 0.0),
            GlobalPosition(point2.latitude, point2.longitude, 0.0),
        ).pointToPointDistance
}
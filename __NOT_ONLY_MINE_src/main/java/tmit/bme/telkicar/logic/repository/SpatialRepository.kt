package tmit.bme.telkicar.logic.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import tmit.bme.telkicar.domain.advert.TravelPlan
import tmit.bme.telkicar.logic.geography.GeoPoint
import tmit.bme.telkicar.logic.geography.roadnetwork.JGraphTRoadNetwork
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNetwork
import tmit.bme.telkicar.logic.helpers.AppContextHelper
import tmit.bme.telkicar.logic.matching.ActionPoint
import tmit.bme.telkicar.logic.matching.SpecialNodeActionPoint
import tmit.bme.telkicar.logic.routing.Route
import java.sql.Connection
import java.sql.SQLException

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON) // 1 for the whole program
class SpatialRepository @Autowired constructor(
    private val spatialDb: Connection,
    private val appContextHelper: AppContextHelper,
    private val roadNetwork: RoadNetwork
) {

    fun saveRoute(
        route: Route,
        isDriversRoute: Boolean,
        isFilteredOut: Boolean,
        isBeeLine: Boolean,
        actionPoint: GeoPoint
    ) {
        if (!appContextHelper.isMatchingToBeSavedToDb)
            return
        val insertQuery =
            "INSERT INTO public.routes(geom, is_drivers, is_filtered, is_beeline, action_point) VALUES (" +
                "GeomFromEWKT('SRID=${JGraphTRoadNetwork.epsgNumber};" +
                "LINESTRING(${route.wkt})'), $isDriversRoute, $isFilteredOut, $isBeeLine, " +
                "GeomFromEWKT('SRID=${JGraphTRoadNetwork.epsgNumber};" +
                "Point(${actionPoint.longitude} ${actionPoint.latitude})'));"
        try {
            spatialDb.prepareStatement(insertQuery).use { statement ->
                statement.execute()
            }
        } catch (ex: SQLException) {
            println(ex)
        }
    }

    fun saveEndpoint(definingEndpointGeoPoint: GeoPoint, isDrivers: Boolean, headCount: Int) {
        if (!appContextHelper.isMatchingToBeSavedToDb)
            return
        val insertQuery =
            "INSERT INTO public.plan_endpoints(geom, is_drivers, head_count) VALUES " +
                "(GeomFromEWKT('SRID=${JGraphTRoadNetwork.epsgNumber};" +
                "Point(${definingEndpointGeoPoint.longitude} ${definingEndpointGeoPoint.latitude})')" +
                ", $isDrivers, $headCount);"
        try {
            spatialDb.prepareStatement(insertQuery).use { statement ->
                statement.execute()
            }
        } catch (ex: SQLException) {
            println(ex)
        }
    }

    fun saveMatchingRoute(
        fromSpecialLocation: Boolean,
        route: Route,
        actionPoints: List<ActionPoint>,
        isDriversRoute: Boolean,
        soloRouteLength: Int,
        distanceTolerance: Double,
        isFinal: Boolean,
    ) {
        val actionPointsWkt = actionPoints.map {
            val point = when {
                it is SpecialNodeActionPoint -> roadNetwork.specialLocationNode.point
                fromSpecialLocation -> (it as TravelPlan).destinationGeoPoint
                else -> (it as TravelPlan).departureGeoPoint
            }
            "(${point.longitude} ${point.latitude})"
        }.fold("") { acc, s ->
            if (acc != "")
                "$acc, $s"
            else s
        }
        val insertQuery =
            "INSERT INTO public.matches(route, action_points, is_drivers, route_length, has_partner, solo_route_length," +
                " dist_flex, is_final) " +
                "VALUES (GeomFromEWKT('SRID=${JGraphTRoadNetwork.epsgNumber};" +
                "LineString(${route.wkt})'), " +
                "GeomFromEWKT('SRID=${JGraphTRoadNetwork.epsgNumber};MultiPoint($actionPointsWkt)'), " +
                "$isDriversRoute, ${route.lengthInMeters}, ${actionPoints.size > 2}, $soloRouteLength," +
                " $distanceTolerance, $isFinal);"
        try {
            spatialDb.prepareStatement(insertQuery).use { statement ->
                statement.execute()
            }
        } catch (ex: SQLException) {
            println(ex)
        }
    }

}
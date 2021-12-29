package tmit.bme.telkicar.logic.helpers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import tmit.bme.telkicar.logic.geography.distance.DistanceMetric
import tmit.bme.telkicar.logic.geography.roadnetwork.JGraphTRoadNetwork
import tmit.bme.telkicar.logic.routing.routeplanner.pointtopoint.RoutePlanner
import java.io.File

@Component
abstract class AppContextHelper(
    private val applicationContext: ApplicationContext
) {
    val isMatchingToBeSavedToDb: Boolean = true

    abstract val specialLatitude: Double
    abstract val specialLongitude: Double
    abstract val specialNodeId: Long

    abstract val specialLatitudeMaxBound: Double
    abstract val specialLongitudeMaxBound: Double
    abstract val specialLatitudeMinBound: Double
    abstract val specialLongitudeMinBound: Double

    abstract val specialLocationTestKey: String

    protected abstract val roadNetworkBinaryFilePathName: String
    val roadNetworkBinaryFile by lazy { File(roadNetworkBinaryFilePathName) }

    fun getPointToPointRoutePlanner(): RoutePlanner =
        applicationContext.getBean("routePlanner", RoutePlanner::class.java)

    // for graph building, routing heuristic
    fun getAccurateDistanceMetric(): DistanceMetric =
        applicationContext.getBean("accurateDistance", DistanceMetric::class.java)

    // for map matching
    fun getFastDistanceMetric(): DistanceMetric =
        applicationContext.getBean("fastDistance", DistanceMetric::class.java)


}
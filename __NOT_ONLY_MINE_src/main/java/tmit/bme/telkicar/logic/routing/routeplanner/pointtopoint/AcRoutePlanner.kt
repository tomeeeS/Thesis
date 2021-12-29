package tmit.bme.telkicar.logic.routing.routeplanner.pointtopoint

import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.AStarShortestPath
import org.jgrapht.graph.GraphWalk
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import tmit.bme.telkicar.logic.geography.GeoPoint
import tmit.bme.telkicar.logic.geography.distance.DistanceMetric
import tmit.bme.telkicar.logic.geography.roadnetwork.JGraphTRoadNetwork
import tmit.bme.telkicar.logic.geography.roadnetwork.Road
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNetwork
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNode
import tmit.bme.telkicar.logic.helpers.AppContextHelper
import tmit.bme.telkicar.logic.routing.Route
import java.lang.Exception
import kotlin.random.Random

class AcRoutePlanner @Autowired constructor(
    appContext: AppContextHelper,
    roadNetworkGraph: RoadNetwork
) : RoutePlanner() {

    private val logger = LoggerFactory.getLogger(javaClass.name)

    private var distanceMetric: DistanceMetric = appContext.getFastDistanceMetric()
    private val router = AStarShortestPath( // faster than BidirectionalAStarShortestPath,
        // here in this lambda we can not give the preferred bidirectional heuristic with only these 2 params
        (roadNetworkGraph as JGraphTRoadNetwork).graph
    ) { start, end ->
        distanceMetric.getDistanceInMeters(start.point, end.point)
    }

    override fun planRoute(startPoint: RoadNode, destinationPoint: RoadNode): Route {
        var path: GraphPath<RoadNode, Road>? = null
        var nodes: List<GeoPoint> = emptyList()
        var lengthInMeters = 0.0
        try {
            path = router.getPath(startPoint, destinationPoint)
            nodes = path!!.vertexList.map { it.point }
            lengthInMeters = path.edgeList  // distanceMetric.getPointListDistanceInMeters(*nodes.toTypedArray())
                .sumByDouble { it.lengthInMeters }
        } catch (e: Exception) {
            logger.info("path null for: $startPoint, $destinationPoint. Forgot to run strong connectivity maker?");
        }
        return Route(
            nodes,
            lengthInMeters.toInt(),
            estimatedTravelTimeMinutes = Random.nextInt(3)..(Random.nextInt(10) + 4),
        )
    }
}
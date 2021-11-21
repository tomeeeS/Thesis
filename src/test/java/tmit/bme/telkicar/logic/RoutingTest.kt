package tmit.bme.telkicar.logic

import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import org.springframework.test.context.junit4.SpringRunner
import tmit.bme.telkicar.logic.geography.distance.DistanceMetric
import tmit.bme.telkicar.logic.geography.roadnetwork.JGraphTRoadNetwork
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNetwork
import tmit.bme.telkicar.logic.helpers.AppContextHelper
import java.lang.Exception

@Component
@RunWith(SpringRunner::class)
@SpringBootTest
class RoutingTest @Autowired constructor(
    private val appContext: AppContextHelper,
    private val roadNetworkGraph: RoadNetwork,
) {
    private val graph = (roadNetworkGraph as JGraphTRoadNetwork).graph
    private val fromDummy = graph.vertexSet().first()
    private val toDummy = graph.vertexSet().last()

    // also tests concurrency for the single road network graph
    @Test
    fun testPlanPointToPointRoute() {
//        Thread {
        val routePlanner = appContext.getPointToPointRoutePlanner()
        val route = routePlanner.planRoute(fromDummy, toDummy)
//        }.run()
//        Thread {
//            val routePlanner = appContext.getPointToPointRoutePlanner()
//            val route = routePlanner.planRoute(fromDummy, toDummy)
//        }.run()
        val a = 5
    }

}
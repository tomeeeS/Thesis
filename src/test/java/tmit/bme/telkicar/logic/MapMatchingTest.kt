package tmit.bme.telkicar.logic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import org.springframework.test.context.junit4.SpringRunner
import tmit.bme.telkicar.logic.geography.GeoPoint
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNetwork
import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNode

@Component
@RunWith(SpringRunner::class)
@SpringBootTest
class MapMatchingTest @Autowired constructor(
    private val roadNetworkGraph: RoadNetwork
) {

    @Test
    fun testGetClosestNode() {
        val floralParkNode = roadNetworkGraph.getClosestPointInNetwork(floralPark)
        Assertions.assertEquals(
            roadNetworkGraph.specialLocationNode,
            floralParkNode
        )
    }

    companion object {
        private val tunderHegy = GeoPoint(47.5162381, 18.9642997)
        private val floralPark = GeoPoint(40.7234, -73.7014)
        private const val tunderHegyClosesNodeId = 278654509L
    }
}
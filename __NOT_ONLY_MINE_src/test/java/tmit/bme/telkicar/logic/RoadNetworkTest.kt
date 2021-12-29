package tmit.bme.telkicar.logic

import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import org.springframework.test.context.junit4.SpringRunner
import tmit.bme.telkicar.logic.geography.roadnetwork.JGraphTRoadNetwork

@Component
@RunWith(SpringRunner::class)
@SpringBootTest
class RoadNetworkTest @Autowired constructor(
    private val roadNetworkGraph: JGraphTRoadNetwork
) {

    @Test
    fun testLoadGraph() {
    }
}
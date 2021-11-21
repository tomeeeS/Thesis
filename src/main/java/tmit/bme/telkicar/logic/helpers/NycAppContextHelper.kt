package tmit.bme.telkicar.logic.helpers

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

class NycAppContextHelper(
    @Autowired applicationContext: ApplicationContext
) : AppContextHelper(applicationContext) {

    private val logger = LoggerFactory.getLogger(javaClass.name)

    init {
        logger.info("starting New York")
    }

    override val specialLatitude = 40.723544
    override val specialLongitude = -73.701542
    override val specialNodeId = 261365157L

    override val specialLatitudeMaxBound = specialLatitude + 0.001
    override val specialLongitudeMaxBound = specialLatitude + 0.001
    override val specialLatitudeMinBound = specialLongitude - 0.001
    override val specialLongitudeMinBound = specialLongitude - 0.001

    override val specialLocationTestKey = "nycSpecial"

    override val roadNetworkBinaryFilePathName = "src/main/resources/static/graph/nycRoadNetwork.binary"
}
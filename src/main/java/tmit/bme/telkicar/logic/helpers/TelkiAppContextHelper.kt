package tmit.bme.telkicar.logic.helpers

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

class TelkiAppContextHelper(
    @Autowired applicationContext: ApplicationContext
) : AppContextHelper(applicationContext) {

    private val logger = LoggerFactory.getLogger(javaClass.name)

    init {
        logger.info("starting Telki")
    }

    override val specialLatitude = 47.5494957
    override val specialLongitude = 18.8231733
    override val specialNodeId = 1834480018L

    override val specialLatitudeMaxBound = 47.560537
    override val specialLongitudeMaxBound = 18.838288
    override val specialLatitudeMinBound = 47.538571
    override val specialLongitudeMinBound = 18.812233

    override val specialLocationTestKey = "telki"

    override val roadNetworkBinaryFilePathName = "src/main/resources/static/graph/roadNetwork.binary"
}
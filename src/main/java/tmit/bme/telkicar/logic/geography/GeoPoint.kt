package tmit.bme.telkicar.logic.geography

import tmit.bme.telkicar.domain.advert.Location
import java.io.Serializable

data class GeoPoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) : Serializable {

    override fun toString(): String {
        return "$latitude, $longitude"
    }
}
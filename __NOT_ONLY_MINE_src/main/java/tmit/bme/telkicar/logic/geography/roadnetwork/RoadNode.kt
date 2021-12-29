package tmit.bme.telkicar.logic.geography.roadnetwork

import tmit.bme.telkicar.logic.geography.GeoPoint
import java.io.Serializable

class RoadNode(
    val id: Long,
    val point: GeoPoint
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoadNode
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "RoadNode(id=$id, point=$point)"
    }

}

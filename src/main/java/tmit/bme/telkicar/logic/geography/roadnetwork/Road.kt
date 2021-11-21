package tmit.bme.telkicar.logic.geography.roadnetwork

import org.jgrapht.graph.DefaultWeightedEdge
import java.io.Serializable

class Road(
    val id: Int,
    val lengthInMeters: Double,
    val roadProperties: RoadProperties,
) : DefaultWeightedEdge(), Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Road

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}
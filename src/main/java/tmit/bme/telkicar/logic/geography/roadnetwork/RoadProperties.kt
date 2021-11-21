package tmit.bme.telkicar.logic.geography.roadnetwork

import java.io.Serializable

data class RoadProperties(
    val estimatedMaxSpeed: Int,
    val type: RoadType,
    val streetName: String = "",
) : Serializable
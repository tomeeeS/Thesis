package tmit.bme.telkicar.logic.matching

import tmit.bme.telkicar.logic.geography.roadnetwork.JGraphTRoadNetwork

interface ActionPoint{
    fun lpInfo(): String
}

class SpecialNodeActionPoint : ActionPoint {

    override fun lpInfo() = "special"

    override fun equals(other: Any?) = javaClass == other?.javaClass
    override fun hashCode() = javaClass.hashCode()
}

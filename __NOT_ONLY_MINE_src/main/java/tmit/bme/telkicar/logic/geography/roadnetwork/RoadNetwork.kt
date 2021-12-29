package tmit.bme.telkicar.logic.geography.roadnetwork

import tmit.bme.telkicar.logic.geography.GeoPoint

interface RoadNetwork {
    val specialLocationNode: RoadNode

    fun getClosestPointInNetwork(geoPoint: GeoPoint): RoadNode
    fun getDistanceFromSpecialNode(geoPoint: GeoPoint): Int
}
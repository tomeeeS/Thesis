package tmit.bme.telkicar.logic.routing

import tmit.bme.telkicar.logic.geography.GeoPoint

interface TransferPoint {
    val geoPoint: GeoPoint
    val transferType: TransferType
    val passengerId: Int
}
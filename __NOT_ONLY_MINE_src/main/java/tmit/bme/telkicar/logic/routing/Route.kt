package tmit.bme.telkicar.logic.routing

import tmit.bme.telkicar.logic.geography.GeoPoint

data class Route(
    var points: List<GeoPoint> = emptyList(),
    var lengthInMeters: Int = 0,
    var estimatedTravelTimeMinutes: IntRange = 0..0,
//    var passengerTransfers: List<TransferPoint> = emptyList()
) {

    // wkt: well-known text format
    val wkt
        get() =
            points.map {
                "${it.longitude} ${it.latitude}"
            }.fold("") { acc, s ->
                if (acc != "")
                    "$acc, $s"
                else s
            }

    operator fun plus(other: Route): Route {
        if (this == Route())
            return other
        if (other == Route())
            return this
        val result = this
        result.points += other.points.drop(1)
        result.lengthInMeters += other.lengthInMeters
        result.estimatedTravelTimeMinutes = (estimatedTravelTimeMinutes.first + other.estimatedTravelTimeMinutes.first)..
            (estimatedTravelTimeMinutes.last + other.estimatedTravelTimeMinutes.last)
        return result
    }

    operator fun plusAssign(other: Route) {
        when {
            this == Route() -> points = other.points
            other == Route() -> null
            else -> points += other.points.drop(1)
        }
        lengthInMeters += other.lengthInMeters
        estimatedTravelTimeMinutes = (estimatedTravelTimeMinutes.first + other.estimatedTravelTimeMinutes.first)..
            (estimatedTravelTimeMinutes.last + other.estimatedTravelTimeMinutes.last)
    }
}
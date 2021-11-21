package tmit.bme.telkicar.logic.geography.roadnetwork

enum class RoadType {

    MOTORWAY,
    MOTORWAY_LINK,
    TRUNK,
    TRUNK_LINK,
    PRIMARY,
    PRIMARY_LINK,
    SECONDARY,
    SECONDARY_LINK,
    TERTIARY,
    TERTIARY_LINK,
    SERVICE,
    RESIDENTIAL,
    UNCLASSIFIED,
    LIVING_STREET,
    PEDESTRIAN,
    ;

    val maxSpeed
        get() = (legalSpeedLimit * maxSpeedEmpiricalMultiplier).toInt()

    // in km   TODO for USA we need miles!
    private val legalSpeedLimit
        get() = when(this) {
            MOTORWAY, MOTORWAY_LINK -> 130
            TRUNK, TRUNK_LINK -> 110
            PRIMARY, PRIMARY_LINK, SECONDARY, SECONDARY_LINK -> 90
            else -> 50
        }

    companion object {

        fun fromString(txt: String?): RoadType =
            when(txt) {
                "motorway" -> MOTORWAY
                "motorway_link" -> MOTORWAY_LINK
                "trunk" -> TRUNK
                "trunk_link" -> TRUNK_LINK
                "primary" -> PRIMARY
                "primary_link" -> PRIMARY_LINK
                "secondary" -> SECONDARY
                "secondary_link" -> SECONDARY_LINK
                "tertiary" -> TERTIARY
                "tertiary_link" -> TERTIARY_LINK
                "service" -> SERVICE
                "residential" -> RESIDENTIAL
                "unclassified" -> UNCLASSIFIED
                "living_street" -> LIVING_STREET
                "pedestrian" -> PEDESTRIAN
                else -> RESIDENTIAL
            }

        private const val maxSpeedEmpiricalMultiplier = 1.1

    }
}
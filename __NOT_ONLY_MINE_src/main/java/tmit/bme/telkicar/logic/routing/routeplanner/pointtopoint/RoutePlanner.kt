package tmit.bme.telkicar.logic.routing.routeplanner.pointtopoint

import tmit.bme.telkicar.logic.geography.roadnetwork.RoadNode
import tmit.bme.telkicar.logic.routing.Route

abstract class RoutePlanner {

    /**
     * Returns a planned route from a geographic (lat,lon) coordinate to another.
     */
    abstract fun planRoute(startPoint: RoadNode, destinationPoint: RoadNode): Route

    fun planRouteForNodeString(vararg nodes: RoadNode) =
        nodes.mapIndexed { index, node ->
            if (index > 0)
                planRoute(nodes[index - 1], node)
            else
                Route()
        }.fold(Route()) { acc, route ->
            acc + route
        }
}
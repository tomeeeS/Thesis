package tmit.bme.telkicar.logic.geography.roadnetwork

import org.apache.commons.lang3.SerializationUtils
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector
import org.jgrapht.graph.SimpleDirectedWeightedGraph
import org.jgrapht.graph.concurrent.AsSynchronizedGraph
import org.postgis.PGgeometry
import org.postgis.Point
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import tmit.bme.telkicar.logic.geography.GeoPoint
import tmit.bme.telkicar.logic.helpers.AppContextHelper
import java.io.File
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import kotlin.system.measureTimeMillis

/**
Road network based on OSM geographical data and the JGraphT graph library.

The property {@link #specialEndNodeId} is the id for a single node for the Hungarian town Telki.
The property is named so to be more general.
When a user travels, they always have to have this node as one of the end points.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON) // 1 for the whole program
// concurrent
class JGraphTRoadNetwork @Autowired constructor(
    private val spatialData: Connection,
    private val appContext: AppContextHelper,
) : RoadNetwork {

    private val logger = LoggerFactory.getLogger(javaClass)

    var graph: AsSynchronizedGraph<RoadNode, Road>
        private set
    private var backingGraph: SimpleDirectedWeightedGraph<RoadNode, Road> // we need this for serialization,
    // AsSynchronizedGraph would have null vertice and edge sets after deserialization.

    private var nextRoadRefId = 0

    override val specialLocationNode = RoadNode(appContext.specialNodeId, GeoPoint(appContext.specialLatitude, appContext.specialLongitude))

    init {
        backingGraph = SimpleDirectedWeightedGraph(Road::class.java)
        graph = AsSynchronizedGraph(backingGraph)
        initGraph()
    }

    private fun deleteUnconnectedGraph() {
        logger.info("Running strong connectivity inspector")
        val components = KosarajuStrongConnectivityInspector(graph).stronglyConnectedSets()
        logger.info("number of components: ${components.size}")
        if (components.size > 1) {
            val errorMsg = "There are more than 1 strong components in the graph! Need to delete the appropriate " +
                "roadNetwork.binary from src/main/resources/static/graph and rerun the application to rebuild the graph!"
            logger.error(errorMsg)
            throw java.lang.Exception(errorMsg)
        }

        val sorted = components.sortedByDescending { it.size }
        logger.info("sorted_components[0] size: ${sorted[0].size}")
        val nodesToDelete = sorted.subList(1, sorted.size).flatten()
        deleteNodes(nodesToDelete)
    }

    // delete nodes from our spatial db taht are not part of the main (biggest) component
    private fun deleteNodes(nodesToDelete: List<RoadNode>) {
        val deleteQuery = ("DELETE FROM public.nodes WHERE id = ?")
        try {
            spatialData.prepareStatement(deleteQuery).use { statement ->
                var count = 0
                for (node in nodesToDelete) {
                    statement.setLong(1, node.id)
                    statement.addBatch()
                    count++
                    // execute every 100 rows or less
                    if (count % 100 == 0 || count == nodesToDelete.size) {
                        statement.executeBatch()
                    }
                }
            }
            logger.info("__________________ RoadNetworkGraph number of nodes deleted: ${nodesToDelete.size}")
        } catch (ex: SQLException) {
            println(ex.message)
        }
    }

    private fun initGraph() {
        try {
            deserializeGraph()
            logger.info("__________________ RoadNetworkGraph load success")
            deleteUnconnectedGraph() // uncomment when we acquire new osm data into our spatial db
            // and we need to delete residual components again
        } catch (e: Exception) {
            // if we tried to load and got a SerializationException then probably the implementations of edges or nodes
            // have changed and the serialized binary is not valid anymore, we cannot deserialize that.
            logger.info("RoadNetworkGraph loadGraph failed, starting to build graph from db. exception: $e")
            buildGraph()
        }
    }

    private fun buildGraph() {
        try {
            spatialData.use {
                it.createStatement().use {
                    buildGraph(it)
                    saveGraph()
                }
            }
        } catch (e: Exception) {
            run { e.printStackTrace() }
            logger.info("RoadNetworkGraph load error $e")
        }
    }

    private fun deserializeGraph() {
        val loadGraphTimeMs = measureTimeMillis {
            val graphBinary = appContext.roadNetworkBinaryFile.readBytes()
            backingGraph = SerializationUtils.deserialize(graphBinary)
            graph = AsSynchronizedGraph(backingGraph)
        }
        logger.info("RoadNetworkGraph deserialize graph time ms: $loadGraphTimeMs")
    }

    private fun saveGraph() {
        val graphBinary = SerializationUtils.serialize(backingGraph)
        appContext.roadNetworkBinaryFile.writeBytes(graphBinary)
    }

    private fun buildGraph(it: Statement) {
        addNodes(
            it.executeQuery(nodesQuery)
        )
        buildEdges(
            it.executeQuery(waysQuery)
        )
    }

    private fun buildEdges(queryResult: ResultSet) {
        while (queryResult.next()) {
            val (isOneWay, road) = buildRoad(queryResult)

            val nodeIds = queryResult.getArray(2).array as Array<Long>
            var nodeA = backingGraph.vertexSet().find { it.id == nodeIds[0] }
            for (i in 0..nodeIds.size - 2) {
                val nodeB = backingGraph.vertexSet().find { it.id == nodeIds[i + 1] }
                nodeA?.let {
                    nodeB?.let {
                        addEdges(nodeA!!, nodeB, road, isOneWay)
                    }
                }
                nodeA = nodeB
            }
        }
    }

    private fun buildRoad(queryResult: ResultSet): Pair<Boolean, RoadProperties> {
        val tags = queryResult.getObject(1) as HashMap<String, String>
        val roadType: RoadType = RoadType.fromString(
            tags["highway"]
                ?: ""
        )
        var maxSpeed = tags["maxspeed"]?.let { it.filter { it.isDigit() }.toInt() }
            ?: roadType.maxSpeed
        tags["traffic_calming"]?.let {
            maxSpeed = (maxSpeed * trafficCalmingSpeedMultiplier).toInt()
        }
        val isOneWay = tags.containsKey("oneway")
        val name = tags["name"]
            ?: ""
        val road = RoadProperties(maxSpeed, roadType, name)
        return Pair(isOneWay, road)
    }

    private fun addEdges(
        nodeA: RoadNode,
        nodeB: RoadNode,
        roadProperties: RoadProperties,
        isOneWay: Boolean
    ) {
        addEdge(nodeA, nodeB, roadProperties)
        if (!isOneWay)
            addEdge(nodeB, nodeA, roadProperties)
    }

    private fun addEdge(
        nodeA: RoadNode,
        nodeB: RoadNode,
        roadProperties: RoadProperties
    ) {
        val road = nextRoad(nodeA, nodeB, roadProperties)
        backingGraph.addEdge(
            nodeA,
            nodeB,
            road
        )
        backingGraph.setEdgeWeight(road, road.lengthInMeters)
    }

    private fun nextRoad(
        nodeA: RoadNode,
        nodeB: RoadNode,
        roadProperties: RoadProperties
    ) =
        Road(
            nextRoadId(),
            appContext.getAccurateDistanceMetric().getDistanceInMeters(nodeA.point, nodeB.point),
            roadProperties
        )

    private fun nextRoadId() = nextRoadRefId++

    private fun addNodes(queryResult: ResultSet) {
        while (queryResult.next()) {
            val id = queryResult.getObject(1) as Long
            val geom = queryResult.getObject(2) as PGgeometry
            val p = (geom.geometry) as Point
            val point = GeoPoint(p.y, p.x)
            backingGraph.addVertex(
                RoadNode(
                    id,
                    point
                )
            )
        }
    }

    override fun getClosestPointInNetwork(geoPoint: GeoPoint): RoadNode =
        backingGraph.vertexSet().minByOrNull {
            appContext.getFastDistanceMetric().getDistanceInMeters(it.point, geoPoint)
        }!!

    override fun getDistanceFromSpecialNode(geoPoint: GeoPoint): Int =
        appContext.getFastDistanceMetric().getDistanceInMeters(specialLocationNode.point, geoPoint).toInt()

    companion object {
        const val epsgNumber = 4326

        private const val nodesQuery = "select id, geom from nodes" // ' limit 10000' for testing
        private const val waysQuery = "select tags, nodes from ways"
        private const val trafficCalmingSpeedMultiplier = 0.75
    }

}
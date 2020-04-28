package org.janusgraph.core.server

import org.apache.tinkerpop.gremlin.driver.MessageSerializer.logger
import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor
import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource
import org.apache.tinkerpop.gremlin.server.GraphManager
import org.apache.tinkerpop.gremlin.server.Settings
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.Transaction
import org.janusgraph.core.ConfiguredGraphFactory
import org.janusgraph.core.JanusGraph
import org.janusgraph.core.JanusGraphFactory
import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.graphdb.database.StandardJanusGraph
import org.janusgraph.graphdb.management.ConfigurationManagementGraph
import org.janusgraph.grpc.JanusGraphContext
import org.janusgraph.grpc.server.ContextManager
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.function.Function
import javax.script.Bindings
import javax.script.SimpleBindings


class DefaultJanusGraphManager(settings: Settings) : GraphManager,
    ContextManager {
    private val graphs: MutableMap<String, Graph?> = ConcurrentHashMap()
    private val traversalSources: MutableMap<String, TraversalSource> = ConcurrentHashMap()
    private var configurationManagementGraph: ConfigurationManagementGraph? = null
    private val instantiateGraphLock = Any()
    private var gremlinExecutor: GremlinExecutor? = null

    fun configureGremlinExecutor(gremlinExecutor: GremlinExecutor?) {
        this.gremlinExecutor = gremlinExecutor
        val bindExecutor = Executors.newScheduledThreadPool(1)
        // Dynamically created graphs created with the ConfiguredGraphFactory are
        // bound across all nodes in the cluster and in the face of server restarts
        bindExecutor.scheduleWithFixedDelay(
            GremlinExecutorGraphBinder(this, this.gremlinExecutor),
            0,
            20L,
            TimeUnit.SECONDS
        )
    }

    private inner class GremlinExecutorGraphBinder(
        val graphManager: DefaultJanusGraphManager,
        val gremlinExecutor: GremlinExecutor?
    ) :
        Runnable {
        override fun run() {
            graphManager.graphNames
                .forEach(Consumer { it: String ->
                    try {
                        val graph: Graph = ConfiguredGraphFactory.open(it)
                        updateTraversalSource(it, graph, this.gremlinExecutor, graphManager)
                    } catch (e: Exception) {
                        // cannot open graph, do nothing
                        log.error(
                            String.format(
                                """Failed to open graph %s with the following error: %s. Thus, it and its traversal will not be bound on this server.""", it, e.toString()
                            )
                        )
                    }
                })
        }

    }

    override fun getGraphNames(): Set<String> {
        val list = configurationManagementGraph
            ?.configurations
            ?.mapNotNull { it.getOrDefault(ConfigurationManagementGraph.PROPERTY_GRAPH_NAME, null) as String }
            ?.toSet() ?: emptySet()
        return graphs.keys + list
    }

    override fun getGraph(gName: String): Graph {
        return graphs[gName]!!
    }

    override fun putGraph(gName: String, g: Graph) {
        graphs[gName] = g
    }

    override fun getTraversalSourceNames(): Set<String> {
        return traversalSources.keys
    }

    override fun getTraversalSource(tsName: String): TraversalSource {
        return traversalSources[tsName]!!
    }

    override fun putTraversalSource(tsName: String, ts: TraversalSource) {
        traversalSources[tsName] = ts
    }

    override fun removeTraversalSource(tsName: String): TraversalSource? {
        return traversalSources.remove(tsName)!!
    }

    /**
     * Get the [Graph] and [TraversalSource] list as a set of bindings.
     */
    override fun getAsBindings(): Bindings {
        val bindings: Bindings = SimpleBindings()
        graphs.forEach { (s: String?, o: Graph?) ->
            bindings[s] = o
        }
        traversalSources.forEach { (s: String?, o: TraversalSource?) ->
            bindings[s] = o
        }
        return bindings
    }

    override fun rollbackAll() {
        closeTx(graphNames, Transaction.Status.ROLLBACK);
    }

    override fun rollback(graphSourceNamesToCloseTxOn: Set<String>) {
        closeTx(graphSourceNamesToCloseTxOn, Transaction.Status.ROLLBACK);
    }

    override fun commitAll() {
        closeTx(graphNames, Transaction.Status.COMMIT);
    }

    override fun commit(graphSourceNamesToCloseTxOn: Set<String>) {
        closeTx(graphSourceNamesToCloseTxOn, Transaction.Status.COMMIT)
    }

    private fun closeTx(
        graphSourceNamesToCloseTxOn: Set<String>,
        tx: Transaction.Status
    ) {
        val graphsToCloseTxOn: MutableSet<Graph?> =
            HashSet()

        // by the time this method has been called, it should be validated that the source/graph is present.
        // might be possible that it could have been removed dynamically, but that i'm not sure how one would do
        // that as of right now unless they were embedded in which case they'd need to know what they were doing
        // anyway
        graphSourceNamesToCloseTxOn.forEach(Consumer { r: String? ->
            if (graphs.containsKey(                    r                )
            ) graphsToCloseTxOn.add(graphs[r]) else graphsToCloseTxOn.add(traversalSources[r]!!.graph)
        })
        graphsToCloseTxOn
            .filterNotNull()
            .filter { it.features().graph().supportsTransactions() && it.tx().isOpen }
            .forEach(Consumer { graph: Graph ->
                when (tx) {
                    Transaction.Status.COMMIT -> graph.tx().commit()
                    else -> graph.tx().rollback()
                }
            })
    }

    override fun openGraph(
        gName: String,
        thunk: Function<String, Graph>
    ): Graph {
        var graph = graphs[gName]
        return if (graph != null && !(graph as StandardJanusGraph).isClosed) {
            updateTraversalSource(gName, graph)
            graph
        } else {
            synchronized(instantiateGraphLock) {
                graph = graphs[gName]
                if (graph == null || (graph as StandardJanusGraph).isClosed) {
                    graph = thunk.apply(gName)
                    graphs[gName] = graph
                }
            }
            updateTraversalSource(gName, graph)
            graph!!
        }
    }

    override fun removeGraph(gName: String): Graph? {
        return graphs.remove(gName)
    }

    private fun updateTraversalSource(
        graphName: String,
        graph: Graph?
    ) {
        if (null != gremlinExecutor) {
            updateTraversalSource(graphName, graph, gremlinExecutor, this)
        }
    }

    private fun updateTraversalSource(
        graphName: String,
        graph: Graph?,
        gremlinExecutor: GremlinExecutor?,
        graphManager: DefaultJanusGraphManager
    ) {
        gremlinExecutor!!.scriptEngineManager.put(graphName, graph)
        val traversalName = graphName + "_traversal"
        val traversalSource: TraversalSource = graph!!.traversal()
        gremlinExecutor.scriptEngineManager.put(traversalName, traversalSource)
        graphManager.putTraversalSource(traversalName, traversalSource)
    }

    override fun contexts(): List<JanusGraphContext> =
        graphNames.map { JanusGraphContext.newBuilder().setGraphName(it).build() }

    override fun getManagement(context: JanusGraphContext?): JanusGraphManagement?{
        if(context == null)
            return null
        val graph = getGraph(context.graphName)
        if(graph is JanusGraph)
            return graph.openManagement()
        return null
    }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultJanusGraphManager::class.java)
        private val CONFIGURATION_MANAGEMENT_GRAPH_KEY = ConfigurationManagementGraph::class.java.simpleName

    }

    init {
        // Open graphs defined at server start in settings.graphs
        settings.graphs.forEach { (key: String, value: String?) ->
            try {
                val graph = JanusGraphFactory.open(value) as StandardJanusGraph
                if (key.toLowerCase() == CONFIGURATION_MANAGEMENT_GRAPH_KEY.toLowerCase()) {
                    configurationManagementGraph = ConfigurationManagementGraph(graph)
                } else {
                    graphs[key] = graph
                }
                logger.info("Graph [{}] was successfully configured via [{}].", key, value)
            } catch (re: RuntimeException) {
                logger.warn(
                    java.lang.String.format(
                        "Graph [%s] configured at [%s] could not be instantiated and will not be available in Gremlin Server.  GraphFactory message: %s",
                        key, value, re.message
                    ), re
                )
                if (re.cause != null) logger.debug("JanusGraphFactory exception", re.cause)
            }
        }
    }
}


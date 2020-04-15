package org.janusgraph.grpc.server

import org.apache.tinkerpop.gremlin.process.traversal.TraversalSource
import org.apache.tinkerpop.gremlin.server.GraphManager
import org.apache.tinkerpop.gremlin.structure.Graph
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function
import javax.script.Bindings

class DefaultJanusGraphManager : GraphManager {
    private val graphs: Map<String, Graph> = ConcurrentHashMap()
    private val traversalSources: Map<String, TraversalSource> = ConcurrentHashMap()

    override fun getGraphNames(): MutableSet<String> {
        TODO("not implemented")
    }

    override fun getGraph(graphName: String?): Graph {
        TODO("not implemented")
    }

    override fun putGraph(graphName: String?, g: Graph?) {
        TODO("not implemented")
    }

    override fun rollbackAll() {
        TODO("not implemented")
    }

    override fun getAsBindings(): Bindings {
        TODO("not implemented")
    }

    override fun rollback(graphSourceNamesToCloseTxOn: MutableSet<String>?) {
        TODO("not implemented")
    }

    override fun putTraversalSource(tsName: String?, ts: TraversalSource?) {
        TODO("not implemented")
    }

    override fun commit(graphSourceNamesToCloseTxOn: MutableSet<String>?) {
        TODO("not implemented")
    }

    override fun removeTraversalSource(tsName: String?): TraversalSource {
        TODO("not implemented")
    }

    override fun getTraversalSourceNames(): MutableSet<String> {
        TODO("not implemented")
    }

    override fun openGraph(graphName: String?, supplier: Function<String, Graph>?): Graph {
        TODO("not implemented")
    }

    override fun removeGraph(graphName: String?): Graph {
        TODO("not implemented")
    }

    override fun commitAll() {
        TODO("not implemented")
    }

    override fun getTraversalSource(traversalSourceName: String?): TraversalSource {
        TODO("not implemented")
    }
}
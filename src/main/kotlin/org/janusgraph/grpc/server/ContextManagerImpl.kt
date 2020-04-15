package org.janusgraph.grpc.server

import org.janusgraph.core.JanusGraph
import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.grpc.JanusGraphContext

class ContextManagerImpl(private val hashMap: HashMap<String, JanusGraph>) : ContextManager {
    override fun contexts(): List<JanusGraphContext> =
        hashMap.map { JanusGraphContext.newBuilder().setGraphName(it.key).build() }

    override fun getGraph(context: JanusGraphContext): JanusGraph? =
        hashMap.get(context.graphName)

    override fun getManagement(context: JanusGraphContext): JanusGraphManagement? =
        getGraph(context)?.openManagement()
}
package org.janusgraph.grpc.server

import org.janusgraph.core.JanusGraph
import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.grpc.JanusGraphContext

interface ContextManager {
    fun contexts(): List<JanusGraphContext>
    fun getGraph(context: JanusGraphContext): JanusGraph?
    fun getManagement(context: JanusGraphContext): JanusGraphManagement?
}
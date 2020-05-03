package org.janusgraph.grpc.server

import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.grpc.JanusGraphContext

interface ContextManager {
    fun contexts(): List<JanusGraphContext>
    fun contextByName(graphName: String): JanusGraphContext?
    fun getManagement(context: JanusGraphContext?): JanusGraphManagement?
}

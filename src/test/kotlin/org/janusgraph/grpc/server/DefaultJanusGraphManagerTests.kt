package org.janusgraph.grpc.server

import org.apache.tinkerpop.gremlin.server.GremlinServer
import org.apache.tinkerpop.gremlin.server.Settings
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DefaultJanusGraphManagerTests {

    @Test
    fun `init`(){
        val settings = Settings()
        settings.graphManager = "org.janusgraph.grpc.server.DefaultJanusGraphManager"
        settings.graphs = mapOf("graph" to "src/test/resources/janusgraph-inmemory.properties")

        val gremlinServer = GremlinServer(settings)

        val graphManager = gremlinServer.serverGremlinExecutor.graphManager
        assertEquals(1, graphManager.graphNames.size)
    }

}

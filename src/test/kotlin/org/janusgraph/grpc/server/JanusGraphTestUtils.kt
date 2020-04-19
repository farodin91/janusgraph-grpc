package org.janusgraph.grpc.server

import org.janusgraph.core.JanusGraph
import org.janusgraph.core.JanusGraphFactory
import org.janusgraph.diskstorage.configuration.ModifiableConfiguration
import org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration

class JanusGraphTestUtils {
    companion object {
        private fun getConfiguration(): ModifiableConfiguration? {
            val config = GraphDatabaseConfiguration.buildGraphConfiguration()
            config.set(GraphDatabaseConfiguration.STORAGE_BACKEND, "inmemory")
            config.set(GraphDatabaseConfiguration.AUTO_TYPE, "none")
            config.set(GraphDatabaseConfiguration.SCHEMA_CONSTRAINTS, true)
            return config
        }

        fun getJanusGraph(): JanusGraph = JanusGraphFactory.open(getConfiguration())
    }
}
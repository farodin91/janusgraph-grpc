package org.janusgraph.grpc.server

import org.janusgraph.core.JanusGraphFactory
import org.janusgraph.diskstorage.configuration.ModifiableConfiguration
import org.janusgraph.graphdb.configuration.GraphDatabaseConfiguration
import org.janusgraph.graphdb.database.StandardJanusGraph
import java.nio.file.Path

class JanusGraphTestUtils {
    companion object {
        private fun getConfiguration(tempDir: Path?): ModifiableConfiguration? {
            val config = GraphDatabaseConfiguration.buildGraphConfiguration()
            config.set(GraphDatabaseConfiguration.STORAGE_BACKEND, "inmemory")
            config.set(GraphDatabaseConfiguration.AUTO_TYPE, "none")
            config.set(GraphDatabaseConfiguration.SCHEMA_CONSTRAINTS, true)
            if (tempDir != null) {
                config.set(GraphDatabaseConfiguration.INDEX_BACKEND, "lucene", "index")
                config.set(GraphDatabaseConfiguration.INDEX_DIRECTORY, tempDir.toString(), "index")
            }
            return config
        }

        fun getJanusGraph(tempDir: Path?): StandardJanusGraph =
            JanusGraphFactory.open(getConfiguration(tempDir)) as StandardJanusGraph
    }
}

package org.janusgraph.core.server

import org.janusgraph.grpc.server.JanusGraphSettings
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JanusGraphSettingsTests {

    @Test
    fun `read grpcServer settings`() {
        val settings =
            JanusGraphSettings.read("src/test/resources/janusgraph-server.yaml")

        assertEquals(10182, settings.grpcServer.port)
        assertTrue(settings.grpcServer.enabled)
    }

    @Test
    fun `autoImport ensure JanusGraphGremlinPlugin is included`() {
        val settings =
            JanusGraphSettings.read("src/test/resources/janusgraph-server.yaml")

        assertNotNull(
            settings.scriptEngines["gremlin-groovy"]
                ?.plugins
                ?.get("org.janusgraph.graphdb.tinkerpop.plugin.JanusGraphGremlinPlugin")
        )
    }

    @Test
    fun `autoImport ensure graphManager is DefaultJanusGraphManager`() {
        val settings =
            JanusGraphSettings.read("src/test/resources/janusgraph-server.yaml")

        assertEquals(DefaultJanusGraphManager::class.java.name, settings.graphManager)
    }

    @Test
    fun `autoImport ensure GryoMessageSerializerV3d0 is fully configured with JanusGraphIoRegistry`() {
        val settings =
            JanusGraphSettings.read("src/test/resources/janusgraph-server.yaml")

        val gryo =
            settings.serializers
                .firstOrNull {
                    it.className == "org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV3d0" &&
                        it.config["ioRegistries"] != null
                }
        assertNotNull(gryo)
        assertTrue((gryo?.config?.get("ioRegistries") as List<*>).contains("org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry"))
    }

    @Test
    fun `autoImport ensure GraphSONMessageSerializerV3d0 is fully configured with JanusGraphIoRegistry`() {
        val settings =
            JanusGraphSettings.read("src/test/resources/janusgraph-server.yaml")

        val gryo =
            settings.serializers
                .firstOrNull {
                    it.className == "org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerV3d0" &&
                        it.config["ioRegistries"] != null
                }
        assertNotNull(gryo)
        assertTrue((gryo?.config?.get("ioRegistries") as List<*>).contains("org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry"))
    }
}

package org.janusgraph.grpc.server

import org.janusgraph.grpc.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


class ManagementServerTests {

    private fun createContextManager(): ContextManager =
        ContextManagerImpl(
            hashMapOf(
                "first" to JanusGraphTestUtils.getJanusGraph(),
                "second" to JanusGraphTestUtils.getJanusGraph()
            )
        )

    private fun createContext(graphName: String = "first") =
        JanusGraphContext.newBuilder().setGraphName(graphName).build()

    private fun createManagementServer(): ManagementServer =
        ManagementServer(createContextManager())

    private fun ensureBasicVertexLabelRequest(graphName: String, name: String = "test"): EnsureVertexLabelRequest {
        return EnsureVertexLabelRequest.newBuilder()
            .setContext(createContext(graphName))
            .setLabel(VertexLabel.newBuilder().setName(name).build())
            .build()
    }

    private fun ensureBasicEdgeLabelRequest(graphName: String, name: String = "test"): EnsureEdgeLabelRequest {
        return EnsureEdgeLabelRequest.newBuilder()
            .setContext(createContext(graphName))
            .setLabel(EdgeLabel.newBuilder().setName(name).build())
            .build()
    }

    @ParameterizedTest
    @ValueSource(strings = ["first", "second"])
    fun `ensureVertexLabel create basic vertexLabel`(graphName: String) {
        val managementServer = createManagementServer()
        val request = ensureBasicVertexLabelRequest(graphName)

        val vertexLabel = managementServer.ensureVertexLabel(request)

        assertEquals("test", vertexLabel?.name);
    }

    @ParameterizedTest
    @ValueSource(strings = ["first", "second"])
    fun `ensureVertexLabel can run multiple times`(graphName: String) {
        val managementServer = createManagementServer()
        val request = ensureBasicVertexLabelRequest(graphName)

        managementServer.ensureVertexLabel(request)
        managementServer.ensureVertexLabel(request)
        val vertexLabel = managementServer.ensureVertexLabel(request)

        assertEquals("test", vertexLabel?.name);
    }

    @ParameterizedTest
    @ValueSource(strings = ["first", "second"])
    fun `getVertexLabelsByName no vertexLabel exists`(graphName: String) {
        val managementServer = createManagementServer()
        val request = GetVertexLabelsByNameRequest.newBuilder()
            .setContext(createContext(graphName))
            .setName("test").build()

        val vertexLabel = managementServer.getVertexLabelsByName(request).firstOrNull()

        assertNull(vertexLabel)
    }

    @ParameterizedTest
    @ValueSource(strings = ["first", "second"])
    fun `getVertexLabelsByName vertexLabel exists`(graphName: String) {
        val managementServer = createManagementServer()
        val label = "test"
        managementServer.ensureVertexLabel(ensureBasicVertexLabelRequest(graphName, label))
        val request = GetVertexLabelsByNameRequest.newBuilder()
            .setContext(createContext(graphName))
            .setName(label).build()

        val vertexLabel = managementServer.getVertexLabelsByName(request).firstOrNull()

        assertEquals(label, vertexLabel?.name)
    }

    @Test
    fun `getVertexLabelsByName vertexLabel exists not on different context`() {
        val managementServer = createManagementServer()
        val label = "test"
        managementServer.ensureVertexLabel(ensureBasicVertexLabelRequest("first", label));
        val request = GetVertexLabelsByNameRequest.newBuilder()
            .setContext(createContext("second"))
            .setName(label).build()

        val vertexLabel = managementServer.getVertexLabelsByName(request).firstOrNull()

        assertNull(vertexLabel)
    }

    @ParameterizedTest
    @ValueSource(strings = ["first", "second"])
    fun `getVertexLabels return multiple vertexLabels`(graphName: String) {
        val managementServer = createManagementServer()
        managementServer.ensureVertexLabel(ensureBasicVertexLabelRequest(graphName, "test1"))
        managementServer.ensureVertexLabel(ensureBasicVertexLabelRequest(graphName, "test2"))
        val request = GetVertexLabelsRequest.newBuilder().setContext(createContext(graphName)).build()

        val vertexLabels = managementServer.getVertexLabels(request)

        assertEquals(2, vertexLabels.size);
    }

    @ParameterizedTest
    @ValueSource(strings = ["first", "second"])
    fun `getVertexLabels return multiple vertexLabels contains elements`(graphName: String) {
        val managementServer = createManagementServer()
        managementServer.ensureVertexLabel(ensureBasicVertexLabelRequest(graphName, "test1"))
        managementServer.ensureVertexLabel(ensureBasicVertexLabelRequest(graphName, "test2"))
        val request = GetVertexLabelsRequest.newBuilder().setContext(createContext(graphName)).build()

        val vertexLabels = managementServer.getVertexLabels(request)

        assertNotNull(vertexLabels.firstOrNull { it.name == "test1" });
        assertNotNull(vertexLabels.firstOrNull { it.name == "test2" });
    }


    @ParameterizedTest
    @ValueSource(strings = ["first", "second"])
    fun `ensureEdgeLabel create basic edgeLabel`(graphName: String) {
        val managementServer = createManagementServer()
        val request = ensureBasicEdgeLabelRequest(graphName)

        val edgeLabel = managementServer.ensureEdgeLabel(request)

        assertEquals("test", edgeLabel?.name);
    }

    @ParameterizedTest
    @ValueSource(strings = ["first", "second"])
    fun `ensureEdgeLabel can run multiple times`(graphName: String) {
        val managementServer = createManagementServer()
        val request = ensureBasicEdgeLabelRequest(graphName)

        managementServer.ensureEdgeLabel(request)
        managementServer.ensureEdgeLabel(request)
        val edgeLabel = managementServer.ensureEdgeLabel(request)

        assertEquals("test", edgeLabel?.name);
    }

    @ParameterizedTest
    @ValueSource(strings = ["first", "second"])
    fun `getEdgeLabelsByName no edgeLabel exists`(graphName: String) {
        val managementServer = createManagementServer()
        val request = GetEdgeLabelsByNameRequest.newBuilder()
            .setContext(createContext(graphName))
            .setName("test").build()

        val vertexLabel = managementServer.getEdgeLabelsByName(request).firstOrNull()

        assertNull(vertexLabel)
    }

    @ParameterizedTest
    @ValueSource(strings = ["first", "second"])
    fun `getEdgeLabelsByName edgeLabel exists`(graphName: String) {
        val managementServer = createManagementServer()
        val label = "test"
        managementServer.ensureEdgeLabel(ensureBasicEdgeLabelRequest(graphName, label));
        val request = GetEdgeLabelsByNameRequest.newBuilder()
            .setContext(createContext(graphName))
            .setName(label).build()

        val vertexLabel = managementServer.getEdgeLabelsByName(request).firstOrNull()

        assertEquals(label, vertexLabel?.name)
    }

    @Test
    fun `getEdgeLabelsByName edgeLabel exists not on different context`() {
        val managementServer = createManagementServer()
        val label = "test"
        managementServer.ensureEdgeLabel(ensureBasicEdgeLabelRequest("first", label));
        val request = GetEdgeLabelsByNameRequest.newBuilder()
            .setContext(createContext("second"))
            .setName(label).build()

        val vertexLabel = managementServer.getEdgeLabelsByName(request).firstOrNull()

        assertNull(vertexLabel)
    }

    @ParameterizedTest
    @ValueSource(strings = ["first", "second"])
    fun `getEdgeLabels return multiple edgeLabels test size`(graphName: String) {
        val managementServer = createManagementServer()
        managementServer.ensureEdgeLabel(ensureBasicEdgeLabelRequest(graphName, "test1"))
        managementServer.ensureEdgeLabel(ensureBasicEdgeLabelRequest(graphName, "test2"))
        val request = GetEdgeLabelsRequest.newBuilder().setContext(createContext(graphName)).build()

        val edgeLabels = managementServer.getEdgeLabels(request)

        assertEquals(2, edgeLabels.size);
    }

    @ParameterizedTest
    @ValueSource(strings = ["first", "second"])
    fun `getEdgeLabels return multiple edgeLabels contains elements`(graphName: String) {
        val managementServer = createManagementServer()
        managementServer.ensureEdgeLabel(ensureBasicEdgeLabelRequest(graphName, "test1"))
        managementServer.ensureEdgeLabel(ensureBasicEdgeLabelRequest(graphName, "test2"))
        val request = GetEdgeLabelsRequest.newBuilder().setContext(createContext(graphName)).build()

        val edgeLabels = managementServer.getEdgeLabels(request)

        assertNotNull(edgeLabels.firstOrNull { it.name == "test1" });
        assertNotNull(edgeLabels.firstOrNull { it.name == "test2" });
    }

}
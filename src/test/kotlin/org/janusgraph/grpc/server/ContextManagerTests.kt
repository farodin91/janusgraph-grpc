package org.janusgraph.grpc.server

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


class ContextManagerTests {
    @Test
    fun `contexts has 1 element`() {
        val hashMap = hashMapOf("default" to JanusGraphTestUtils.getJanusGraph())
        val contextManager = ContextManagerImpl(hashMap)

        val context = contextManager.contexts()

        assertEquals(1, context.size)
    }

    @Test
    fun `contexts has 2 elements`() {
        val contextManager = createContextManager()

        val context = contextManager.contexts()

        assertEquals(2, context.size)
    }

    @ParameterizedTest
    @ValueSource(strings = ["default", "test"])
    fun `contexts first element has label default`(graphName: String) {
        val contextManager = createContextManager()

        val context = contextManager.contexts().find { it.graphName == graphName }

        assertNotNull(context)
    }

    @Test
    fun `getGraph first element get JanusGraph of it`() {
        val janusGraph = JanusGraphTestUtils.getJanusGraph()
        val hashMap = hashMapOf("default" to janusGraph)
        val contextManager = ContextManagerImpl(hashMap)
        val context = contextManager.contexts()[0]

        val graph = contextManager.getGraph(context)

        assertEquals(janusGraph, graph)
    }

    @Test
    fun `getManagement first element get Manager of it`() {
        val janusGraph = JanusGraphTestUtils.getJanusGraph()
        val hashMap = hashMapOf("default" to janusGraph)
        val contextManager = ContextManagerImpl(hashMap)
        val context = contextManager.contexts()[0]

        val management = contextManager.getManagement(context)

        assertNotNull(management)
    }

    private fun createContextManager(): ContextManager {
        val hashMap = hashMapOf(
            "default" to JanusGraphTestUtils.getJanusGraph(),
            "test" to JanusGraphTestUtils.getJanusGraph()
        )
        return ContextManagerImpl(hashMap)
    }
}
package org.janusgraph.grpc.server

import com.google.protobuf.Int64Value
import org.janusgraph.grpc.EdgeLabel
import org.janusgraph.grpc.EdgeProperty
import org.janusgraph.grpc.PropertyDataType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class ManagementForEdgeLabelsTests {

    private fun createDefaults() =
        ManagementForEdgeLabels() to JanusGraphTestUtils.getJanusGraph()

    private fun buildEdgeLabel(
        name: String = "test",
        edgeId: Long? = null,
        properties: List<EdgeProperty> = emptyList()
    ): EdgeLabel {
        val edgeLabelBuilder = EdgeLabel.newBuilder().setName(name).addAllProperties(properties)
        if (edgeId != null) {
            edgeLabelBuilder.id = Int64Value.of(edgeId)
        }
        return edgeLabelBuilder.build()
    }

    @Test
    fun `ensureEdgeLabel create basic edgeLabel`() {
        val (managementServer, graph) = createDefaults()
        val request = buildEdgeLabel()

        val edgeLabel = managementServer.ensureEdgeLabel(graph.openManagement(), request)

        Assertions.assertEquals("test", edgeLabel?.name)
    }

    @Test
    fun `ensureEdgeLabel can run multiple times`() {
        val (managementServer, graph) = createDefaults()
        val request = buildEdgeLabel()

        managementServer.ensureEdgeLabel(graph.openManagement(), request)
        managementServer.ensureEdgeLabel(graph.openManagement(), request)
        val edgeLabel = managementServer.ensureEdgeLabel(graph.openManagement(), request)

        Assertions.assertEquals("test", edgeLabel?.name)
    }

    @Test
    fun `ensureEdgeLabel update name`() {
        val (managementServer, graph) = createDefaults()
        val request1 = buildEdgeLabel("test1")
        val ensureEdgeLabel = managementServer.ensureEdgeLabel(graph.openManagement(), request1)
        val request2 = buildEdgeLabel("test2", ensureEdgeLabel?.id?.value)

        val edgeLabel = managementServer.ensureEdgeLabel(graph.openManagement(), request2)

        Assertions.assertEquals("test1", ensureEdgeLabel?.name)
        Assertions.assertEquals("test2", edgeLabel?.name)
        Assertions.assertEquals(ensureEdgeLabel?.id, edgeLabel?.id)
    }

    @ParameterizedTest
    @EnumSource(PropertyDataType::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNRECOGNIZED"])
    fun `ensureEdgeLabel creates property`(propertyDataType: PropertyDataType) {
        val (managementServer, graph) = createDefaults()
        val propertyName = "propertyName"
        val property = EdgeProperty.newBuilder().setName(propertyName).setDataType(propertyDataType).build()
        val request = buildEdgeLabel(name = "edgeName", properties = listOf(property))

        val edgeLabel = managementServer.ensureEdgeLabel(graph.openManagement(), request)

        Assertions.assertEquals(1, edgeLabel?.propertiesCount)
        Assertions.assertEquals(
            propertyName,
            edgeLabel?.propertiesList?.firstOrNull()?.name
        )
        Assertions.assertEquals(
            propertyDataType,
            edgeLabel?.propertiesList?.firstOrNull()?.dataType
        )
    }

    @Test
    fun `ensureEdgeLabel can run multiple times with same properties`() {
        val (managementServer, graph) = createDefaults()
        val propertyName = "propertyName"
        val property = EdgeProperty.newBuilder()
            .setName(propertyName).setDataType(PropertyDataType.Boolean).build()
        val request = buildEdgeLabel(name = "edgeName", properties = listOf(property))

        managementServer.ensureEdgeLabel(graph.openManagement(), request)

        managementServer.ensureEdgeLabel(graph.openManagement(), request)
        managementServer.ensureEdgeLabel(graph.openManagement(), request)
        val edgeLabel = managementServer.ensureEdgeLabel(graph.openManagement(), request)

        Assertions.assertEquals(1, edgeLabel?.propertiesCount)
        Assertions.assertEquals(
            propertyName,
            edgeLabel?.propertiesList?.firstOrNull()?.name
        )
        Assertions.assertEquals(
            PropertyDataType.Boolean,
            edgeLabel?.propertiesList?.firstOrNull()?.dataType
        )
    }

    @Test
    fun `getEdgeLabelsByName update name works`() {
        val (managementServer, graph) = createDefaults()
        val request1 = buildEdgeLabel("test1")
        val ensureEdgeLabel = managementServer.ensureEdgeLabel(graph.openManagement(), request1)
        val request2 = buildEdgeLabel("test2", ensureEdgeLabel?.id?.value)
        managementServer.ensureEdgeLabel(graph.openManagement(), request2)

        val edgeLabel = managementServer.getEdgeLabelsByName(graph.openManagement(), "test2").firstOrNull()

        Assertions.assertEquals("test2", edgeLabel?.name)
        Assertions.assertEquals(ensureEdgeLabel?.id, edgeLabel?.id)
    }

    @Test
    fun `getEdgeLabelsByName no edgeLabel exists`() {
        val (managementServer, graph) = createDefaults()

        val edgeLabel = managementServer.getEdgeLabelsByName(graph.openManagement(), "test").firstOrNull()

        Assertions.assertNull(edgeLabel)
    }

    @Test
    fun `getEdgeLabelsByName edgeLabel exists`() {
        val (managementServer, graph) = createDefaults()
        val label = "test"
        managementServer.ensureEdgeLabel(graph.openManagement(), buildEdgeLabel(label))

        val edgeLabel = managementServer.getEdgeLabelsByName(graph.openManagement(), label).firstOrNull()

        Assertions.assertEquals(label, edgeLabel?.name)
    }

    @ParameterizedTest
    @EnumSource(PropertyDataType::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNRECOGNIZED"])
    fun `getEdgeLabelsByName returns property`(propertyDataType: PropertyDataType) {
        val (managementServer, graph) = createDefaults()
        val propertyName = "propertyName"
        val property = EdgeProperty.newBuilder().setName(propertyName).setDataType(propertyDataType).build()
        managementServer.ensureEdgeLabel(
            graph.openManagement(),
            buildEdgeLabel(name = "edgeName", properties = listOf(property))
        )

        val edgeLabel = managementServer.getEdgeLabelsByName(graph.openManagement(), "edgeName").firstOrNull()

        Assertions.assertEquals(1, edgeLabel?.propertiesCount)
        Assertions.assertEquals(
            propertyName,
            edgeLabel?.propertiesList?.firstOrNull()?.name
        )
        Assertions.assertEquals(
            propertyDataType,
            edgeLabel?.propertiesList?.firstOrNull()?.dataType
        )
    }

    @Test
    fun `getEdgeLabels return multiple edgeLabels test size`() {
        val (managementServer, graph) = createDefaults()
        managementServer.ensureEdgeLabel(graph.openManagement(), buildEdgeLabel("edgeName1"))
        managementServer.ensureEdgeLabel(graph.openManagement(), buildEdgeLabel("edgeName2"))

        val edgeLabels = managementServer.getEdgeLabels(graph.openManagement())

        Assertions.assertEquals(2, edgeLabels.size)
    }

    @Test
    fun `getEdgeLabels return multiple edgeLabels contains elements`() {
        val (managementServer, graph) = createDefaults()
        managementServer.ensureEdgeLabel(graph.openManagement(), buildEdgeLabel("edgeName1"))
        managementServer.ensureEdgeLabel(graph.openManagement(), buildEdgeLabel("edgeName2"))

        val edgeLabels = managementServer.getEdgeLabels(graph.openManagement())

        Assertions.assertNotNull(edgeLabels.firstOrNull { it.name == "edgeName1" })
        Assertions.assertNotNull(edgeLabels.firstOrNull { it.name == "edgeName2" })
    }

}

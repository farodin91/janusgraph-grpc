package org.janusgraph.grpc.server

import com.google.protobuf.Int64Value
import org.janusgraph.grpc.CompositeVertexIndex
import org.janusgraph.grpc.PropertyDataType
import org.janusgraph.grpc.VertexLabel
import org.janusgraph.grpc.VertexProperty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource


class ManagementForVertexLabelsTests {

    private fun createDefaults() =
        ManagementForVertexLabels() to JanusGraphTestUtils.getJanusGraph()

    private fun buildVertexLabel(
        name: String = "test",
        vertexId: Long? = null,
        properties: List<VertexProperty> = emptyList(),
        readOnly: Boolean = false,
        partitioned: Boolean = false
    ): VertexLabel {
        val vertexLabelBuilder = VertexLabel.newBuilder()
            .setName(name)
            .addAllProperties(properties)
            .setReadOnly(readOnly)
            .setPartitioned(partitioned)
        if (vertexId != null) {
            vertexLabelBuilder.id = Int64Value.of(vertexId)
        }
        return vertexLabelBuilder.build()
    }

    @Test
    fun `ensureVertexLabel create basic vertexLabel`() {
        val (managementServer, graph) = createDefaults()
        val request = buildVertexLabel()

        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertEquals("test", vertexLabel?.name)
    }

    @Test
    fun `ensureVertexLabel vertexLabel marked as readOnly`() {
        val (managementServer, graph) = createDefaults()
        val request = buildVertexLabel(readOnly = true)

        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertTrue(vertexLabel?.readOnly!!)
    }

    @Test
    fun `ensureVertexLabel vertexLabel marked as partitioned`() {
        val (managementServer, graph) = createDefaults()
        val request = buildVertexLabel(partitioned = true)

        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertTrue(vertexLabel?.partitioned!!)
    }

    @Test
    fun `ensureVertexLabel can run multiple times`() {
        val (managementServer, graph) = createDefaults()
        val request = buildVertexLabel()

        managementServer.ensureVertexLabel(graph.openManagement(), request)
        managementServer.ensureVertexLabel(graph.openManagement(), request)
        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertEquals("test", vertexLabel?.name)
    }

    @Test
    fun `ensureVertexLabel update name`() {
        val (managementServer, graph) = createDefaults()
        val request1 = buildVertexLabel("test1")
        val ensureVertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request1)
        val request2 = buildVertexLabel("test2", ensureVertexLabel?.id?.value)

        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request2)

        assertEquals("test1", ensureVertexLabel?.name)
        assertEquals("test2", vertexLabel?.name)
        assertEquals(ensureVertexLabel?.id, vertexLabel?.id)
    }

    @ParameterizedTest
    @EnumSource(PropertyDataType::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNRECOGNIZED"])
    fun `ensureVertexLabel creates property`(propertyDataType: PropertyDataType) {
        val (managementServer, graph) = createDefaults()
        val propertyName = "name"
        val property = VertexProperty.newBuilder().setName(propertyName).setDataType(propertyDataType).build()
        val request = buildVertexLabel(name = "test", properties = listOf(property))

        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertEquals(1, vertexLabel?.propertiesCount)
        assertEquals(propertyName, vertexLabel?.propertiesList?.firstOrNull()?.name)
        assertEquals(propertyDataType, vertexLabel?.propertiesList?.firstOrNull()?.dataType)
    }

    @ParameterizedTest
    @EnumSource(VertexProperty.Cardinality::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNRECOGNIZED"])
    fun `ensureVertexLabel creates property`(propertyCardinality: VertexProperty.Cardinality) {
        val (managementServer, graph) = createDefaults()
        val propertyName = "propertyName"
        val property = VertexProperty.newBuilder()
            .setName(propertyName)
            .setDataType(PropertyDataType.String)
            .setCardinality(propertyCardinality)
            .build()
        val request = buildVertexLabel(name = "vertexName", properties = listOf(property))

        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertEquals(propertyCardinality, vertexLabel?.propertiesList?.firstOrNull()?.cardinality)
    }

    @Test
    fun `ensureVertexLabel can run multiple times with same properties`() {
        val (managementServer, graph) = createDefaults()
        val propertyName = "name"
        val property = VertexProperty.newBuilder().setName(propertyName).setDataType(PropertyDataType.Boolean).build()
        val request = buildVertexLabel(name = "test", properties = listOf(property))

        managementServer.ensureVertexLabel(graph.openManagement(), request)

        managementServer.ensureVertexLabel(graph.openManagement(), request)
        managementServer.ensureVertexLabel(graph.openManagement(), request)
        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertEquals(1, vertexLabel?.propertiesCount)
        assertEquals(propertyName, vertexLabel?.propertiesList?.firstOrNull()?.name)
        assertEquals(PropertyDataType.Boolean, vertexLabel?.propertiesList?.firstOrNull()?.dataType)
    }

    @Test
    fun `getVertexLabelsByName no vertexLabel exists`() {
        val (managementServer, graph) = createDefaults()

        val vertexLabel = managementServer.getVertexLabelsByName(graph.openManagement(), "test").firstOrNull()

        assertNull(vertexLabel)
    }

    @Test
    fun `getVertexLabelsByName vertexLabel exists`() {
        val (managementServer, graph) = createDefaults()
        val label = "test"
        managementServer.ensureVertexLabel(graph.openManagement(), buildVertexLabel(label))

        val vertexLabel = managementServer.getVertexLabelsByName(graph.openManagement(), label).firstOrNull()

        assertEquals(label, vertexLabel?.name)
    }

    @Test
    fun `getVertexLabelsByName vertexLabel marked as readOnly`() {
        val (managementServer, graph) = createDefaults()
        managementServer.ensureVertexLabel(graph.openManagement(), buildVertexLabel(readOnly = true))

        val vertexLabel = managementServer.getVertexLabelsByName(graph.openManagement(), "test").firstOrNull()

        assertTrue(vertexLabel?.readOnly!!)
    }

    @Test
    fun `getVertexLabelsByName vertexLabel marked as partitioned`() {
        val (managementServer, graph) = createDefaults()
        managementServer.ensureVertexLabel(graph.openManagement(), buildVertexLabel(partitioned = true))

        val vertexLabel = managementServer.getVertexLabelsByName(graph.openManagement(), "test").firstOrNull()

        assertTrue(vertexLabel?.partitioned!!)
    }

    @Test
    fun `getVertexLabelsByName update name works`() {
        val (managementServer, graph) = createDefaults()
        val ensureEdgeLabel = managementServer.ensureVertexLabel(graph.openManagement(), buildVertexLabel("test1"))
        managementServer.ensureVertexLabel(
            graph.openManagement(),
            buildVertexLabel("test2", ensureEdgeLabel?.id?.value)
        )

        val vertexLabel = managementServer.getVertexLabelsByName(graph.openManagement(), "test2").firstOrNull()

        assertEquals("test2", vertexLabel?.name)
        assertEquals(ensureEdgeLabel?.id, vertexLabel?.id)
    }

    @ParameterizedTest
    @EnumSource(PropertyDataType::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNRECOGNIZED"])
    fun `getVertexLabelsByName returns property`(propertyDataType: PropertyDataType) {
        val (managementServer, graph) = createDefaults()
        val propertyName = "name"
        val property = VertexProperty.newBuilder().setName(propertyName).setDataType(propertyDataType).build()
        managementServer.ensureVertexLabel(
            graph.openManagement(),
            buildVertexLabel(name = "test", properties = listOf(property))
        )

        val vertexLabel = managementServer.getVertexLabelsByName(graph.openManagement(), "test").firstOrNull()

        assertEquals(1, vertexLabel?.propertiesCount)
        assertEquals(propertyName, vertexLabel?.propertiesList?.firstOrNull()?.name)
        assertEquals(propertyDataType, vertexLabel?.propertiesList?.firstOrNull()?.dataType)
    }

    @ParameterizedTest
    @EnumSource(VertexProperty.Cardinality::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNRECOGNIZED"])
    fun `getVertexLabelsByName returns property`(propertyCardinality: VertexProperty.Cardinality) {
        val (managementServer, graph) = createDefaults()
        val propertyName = "propertyName"
        val property = VertexProperty.newBuilder().setName(propertyName).setCardinality(propertyCardinality).build()
        managementServer.ensureVertexLabel(
            graph.openManagement(),
            buildVertexLabel(name = "vertexName", properties = listOf(property))
        )

        val vertexLabel = managementServer.getVertexLabelsByName(graph.openManagement(), "vertexName").firstOrNull()

        assertEquals(propertyCardinality, vertexLabel?.propertiesList?.firstOrNull()?.cardinality)
    }

    @Test
    fun `getVertexLabels return multiple vertexLabels`() {
        val (managementServer, graph) = createDefaults()
        managementServer.ensureVertexLabel(graph.openManagement(), buildVertexLabel("test1"))
        managementServer.ensureVertexLabel(graph.openManagement(), buildVertexLabel("test2"))

        val vertexLabels = managementServer.getVertexLabels(graph.openManagement())

        assertEquals(2, vertexLabels.size)
    }

    @Test
    fun `getVertexLabels return multiple vertexLabels contains elements`() {
        val (managementServer, graph) = createDefaults()
        managementServer.ensureVertexLabel(graph.openManagement(), buildVertexLabel("test1"))
        managementServer.ensureVertexLabel(graph.openManagement(), buildVertexLabel("test2"))

        val vertexLabels = managementServer.getVertexLabels(graph.openManagement())

        assertNotNull(vertexLabels.firstOrNull { it.name == "test1" })
        assertNotNull(vertexLabels.firstOrNull { it.name == "test2" })
    }

    @Test
    fun `getVertexLabels returns multiple vertexLabels with property`() {
        val (managementServer, graph) = createDefaults()
        val propertyName = "name"
        val property = VertexProperty.newBuilder()
            .setName(propertyName)
            .setDataType(PropertyDataType.Boolean)
            .setCardinality(VertexProperty.Cardinality.List)
            .build()
        managementServer.ensureVertexLabel(
            graph.openManagement(),
            buildVertexLabel(name = "test1", properties = listOf(property))
        )
        managementServer.ensureVertexLabel(
            graph.openManagement(),
            buildVertexLabel(name = "test2", properties = listOf(property))
        )

        val vertexLabels = managementServer.getVertexLabels(graph.openManagement())
        val vertexLabel1 = vertexLabels.firstOrNull { it.name == "test1" }
        val vertexLabel2 = vertexLabels.firstOrNull { it.name == "test2" }
        assertEquals(1, vertexLabel1?.propertiesCount)
        assertEquals(1, vertexLabel2?.propertiesCount)
        assertEquals(propertyName, vertexLabel1?.propertiesList?.firstOrNull()?.name)
        assertEquals(propertyName, vertexLabel2?.propertiesList?.firstOrNull()?.name)
        assertEquals(PropertyDataType.Boolean, vertexLabel1?.propertiesList?.firstOrNull()?.dataType)
        assertEquals(PropertyDataType.Boolean, vertexLabel2?.propertiesList?.firstOrNull()?.dataType)
        assertEquals(VertexProperty.Cardinality.List, vertexLabel1?.propertiesList?.firstOrNull()?.cardinality)
        assertEquals(VertexProperty.Cardinality.List, vertexLabel2?.propertiesList?.firstOrNull()?.cardinality)
    }

    @Test
    fun `getVertexLabels vertexLabel marked as readOnly`() {
        val (managementServer, graph) = createDefaults()
        managementServer.ensureVertexLabel(graph.openManagement(), buildVertexLabel(readOnly = true))

        val vertexLabel = managementServer.getVertexLabels(graph.openManagement()).firstOrNull()

        assertTrue(vertexLabel?.readOnly!!)
    }

    @Test
    fun `getVertexLabels vertexLabel marked as partitioned`() {
        val (managementServer, graph) = createDefaults()
        managementServer.ensureVertexLabel(graph.openManagement(), buildVertexLabel(partitioned = true))

        val vertexLabel = managementServer.getVertexLabels(graph.openManagement()).firstOrNull()

        assertTrue(vertexLabel?.partitioned!!)
    }

    @Test
    fun `ensureCompositeIndexByVertexLabel create index`(){
        val (mangement, graph) = createDefaults()
        val property = VertexProperty.newBuilder().setName("").setDataType(PropertyDataType.String).build()
        val label = mangement.ensureVertexLabel(graph.openManagement(), buildVertexLabel(partitioned = true, properties = listOf(property)))
        val index = CompositeVertexIndex.newBuilder()
            .setName("test")
            .addProperties(label?.propertiesList?.first())
            .build()

        val compositeIndex = mangement.ensureCompositeIndexByVertexLabel(graph.openManagement(), label!!, index)

        assertEquals("test", compositeIndex?.name)
        assertEquals(1, compositeIndex?.propertiesCount)
        assertNotNull(compositeIndex?.id)
    }
}

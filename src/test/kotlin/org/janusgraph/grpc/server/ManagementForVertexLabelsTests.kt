package org.janusgraph.grpc.server

import com.google.protobuf.Int64Value
import org.janusgraph.core.JanusGraph
import org.janusgraph.grpc.CompositeVertexIndex
import org.janusgraph.grpc.PropertyDataType
import org.janusgraph.grpc.VertexLabel
import org.janusgraph.grpc.VertexProperty
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource


class ManagementForVertexLabelsTests {

    private fun createDefaults() =
        ManagementForVertexLabels() to JanusGraphTestUtils.getJanusGraph()

    private fun buildLabel(
        name: String = "test",
        vertexId: Long? = null,
        properties: List<VertexProperty> = emptyList(),
        readOnly: Boolean = false,
        partitioned: Boolean = false,
        managementServer: IManagementForVertexLabels? = null,
        graph: JanusGraph? = null
    ): VertexLabel {
        val builder = VertexLabel.newBuilder()
            .setName(name)
            .addAllProperties(properties)
            .setReadOnly(readOnly)
            .setPartitioned(partitioned)
        if (vertexId != null) {
            builder.id = Int64Value.of(vertexId)
        }
        return if (managementServer != null && graph != null) {
            managementServer.ensureVertexLabel(graph.openManagement(), builder.build())!!
        } else {
            builder.build()
        }
    }

    private fun buildProperty(
        name: String = "propertyName",
        id: Long? = null,
        dataType: PropertyDataType = PropertyDataType.Int32,
        cardinality: VertexProperty.Cardinality = VertexProperty.Cardinality.Single
    ): VertexProperty {
        val builder = VertexProperty.newBuilder()
            .setName(name)
            .setDataType(dataType)
            .setCardinality(cardinality)
        if (id != null) {
            builder.id = Int64Value.of(id)
        }
        return builder.build()
    }

    @Test
    fun `ensureVertexLabel create basic vertexLabel`() {
        val (managementServer, graph) = createDefaults()
        val request = buildLabel()

        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertEquals("test", vertexLabel?.name)
    }

    @Test
    fun `ensureVertexLabel vertexLabel marked as readOnly`() {
        val (managementServer, graph) = createDefaults()
        val request = buildLabel(readOnly = true)

        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertTrue(vertexLabel?.readOnly!!)
    }

    @Test
    fun `ensureVertexLabel vertexLabel marked as partitioned`() {
        val (managementServer, graph) = createDefaults()
        val request = buildLabel(partitioned = true)

        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertTrue(vertexLabel?.partitioned!!)
    }

    @Test
    fun `ensureVertexLabel can run multiple times`() {
        val (managementServer, graph) = createDefaults()
        val request = buildLabel()

        managementServer.ensureVertexLabel(graph.openManagement(), request)
        managementServer.ensureVertexLabel(graph.openManagement(), request)
        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertEquals("test", vertexLabel?.name)
    }

    @Test
    fun `ensureVertexLabel update name`() {
        val (managementServer, graph) = createDefaults()
        val ensureLabel = buildLabel("test1", managementServer= managementServer, graph = graph)
        val request2 = buildLabel("test2", ensureLabel.id?.value)

        val label = managementServer.ensureVertexLabel(graph.openManagement(), request2)

        assertEquals("test1", ensureLabel.name)
        assertEquals("test2", label?.name)
        assertEquals(ensureLabel.id, label?.id)
    }

    @ParameterizedTest
    @EnumSource(PropertyDataType::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNRECOGNIZED"])
    fun `ensureVertexLabel creates property`(propertyDataType: PropertyDataType) {
        val (managementServer, graph) = createDefaults()
        val propertyName = "name"
        val property = buildProperty(propertyName, dataType = propertyDataType)
        val request = buildLabel(name = "test", properties = listOf(property))

        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertEquals(1, vertexLabel?.propertiesCount)
        assertEquals(propertyName, vertexLabel?.propertiesList?.firstOrNull()?.name)
        assertEquals(propertyDataType, vertexLabel?.propertiesList?.firstOrNull()?.dataType)
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 8, 16])
    fun `ensureVertexLabel creates property`(numberOfProperties: Int) {
        val (managementServer, graph) = createDefaults()
        val properties = (1..numberOfProperties).map { buildProperty("propertyName$it") }
        val request = buildLabel(name = "test", properties = properties)

        val label = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertEquals(numberOfProperties, label?.propertiesCount)
    }

    @ParameterizedTest
    @EnumSource(VertexProperty.Cardinality::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNRECOGNIZED"])
    fun `ensureVertexLabel creates property`(propertyCardinality: VertexProperty.Cardinality) {
        val (managementServer, graph) = createDefaults()
        val propertyName = "propertyName"
        val property = buildProperty(propertyName, cardinality = propertyCardinality)
        val request = buildLabel(name = "vertexName", properties = listOf(property))

        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertEquals(propertyCardinality, vertexLabel?.propertiesList?.firstOrNull()?.cardinality)
    }

    @Test
    fun `ensureVertexLabel can run multiple times with same properties`() {
        val (managementServer, graph) = createDefaults()
        val propertyName = "name"
        val property = buildProperty(propertyName)
        val request = buildLabel(name = "test", properties = listOf(property))

        managementServer.ensureVertexLabel(graph.openManagement(), request)

        managementServer.ensureVertexLabel(graph.openManagement(), request)
        managementServer.ensureVertexLabel(graph.openManagement(), request)
        val vertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertEquals(1, vertexLabel?.propertiesCount)
        assertEquals(propertyName, vertexLabel?.propertiesList?.firstOrNull()?.name)
        assertEquals(PropertyDataType.Int32, vertexLabel?.propertiesList?.firstOrNull()?.dataType)
    }

    @Test
    fun `ensureVertexLabel with property update label name`() {
        val (managementServer, graph) = createDefaults()
        val request1 = buildLabel("test1")
        val propertyName = "propertyName"
        val ensureVertexLabel = managementServer.ensureVertexLabel(graph.openManagement(), request1)
        val property = buildProperty(propertyName)
        val request2 = buildLabel("test2", ensureVertexLabel?.id?.value, properties = listOf(property))

        val label = managementServer.ensureVertexLabel(graph.openManagement(), request2)

        assertEquals(1, label?.propertiesCount)
        assertEquals(propertyName, label?.propertiesList?.firstOrNull()?.name)
    }

    @ParameterizedTest
    @EnumSource(PropertyDataType::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNRECOGNIZED"])
    fun `ensureVertexLabel add property as update`(propertyDataType: PropertyDataType) {
        val (managementServer, graph) = createDefaults()
        val propertyName = "propertyName"
        val request1 = buildLabel(name = "test")
        managementServer.ensureVertexLabel(graph.openManagement(), request1)
        val property = buildProperty(propertyName, dataType = propertyDataType)
        val request = buildLabel(name = "test", properties = listOf(property))

        val label = managementServer.ensureVertexLabel(graph.openManagement(), request)

        assertEquals(1, label?.propertiesCount)
        assertEquals(propertyName, label?.propertiesList?.firstOrNull()?.name)
        assertEquals(propertyDataType, label?.propertiesList?.firstOrNull()?.dataType)
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
        managementServer.ensureVertexLabel(graph.openManagement(), buildLabel(label))

        val vertexLabel = managementServer.getVertexLabelsByName(graph.openManagement(), label).firstOrNull()

        assertEquals(label, vertexLabel?.name)
    }

    @Test
    fun `getVertexLabelsByName vertexLabel marked as readOnly`() {
        val (managementServer, graph) = createDefaults()
        managementServer.ensureVertexLabel(graph.openManagement(), buildLabel(readOnly = true))

        val vertexLabel = managementServer.getVertexLabelsByName(graph.openManagement(), "test").firstOrNull()

        assertTrue(vertexLabel?.readOnly!!)
    }

    @Test
    fun `getVertexLabelsByName vertexLabel marked as partitioned`() {
        val (managementServer, graph) = createDefaults()
        managementServer.ensureVertexLabel(graph.openManagement(), buildLabel(partitioned = true))

        val vertexLabel = managementServer.getVertexLabelsByName(graph.openManagement(), "test").firstOrNull()

        assertTrue(vertexLabel?.partitioned!!)
    }

    @Test
    fun `getVertexLabelsByName update name works`() {
        val (managementServer, graph) = createDefaults()
        val ensureEdgeLabel = managementServer.ensureVertexLabel(graph.openManagement(), buildLabel("test1"))
        managementServer.ensureVertexLabel(
            graph.openManagement(),
            buildLabel("test2", ensureEdgeLabel?.id?.value)
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
        val property = buildProperty(propertyName, dataType = propertyDataType)
        managementServer.ensureVertexLabel(
            graph.openManagement(),
            buildLabel(name = "test", properties = listOf(property))
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
        val property = buildProperty(propertyName, cardinality = propertyCardinality)
        managementServer.ensureVertexLabel(
            graph.openManagement(),
            buildLabel(name = "vertexName", properties = listOf(property))
        )

        val vertexLabel = managementServer.getVertexLabelsByName(graph.openManagement(), "vertexName").firstOrNull()

        assertEquals(propertyCardinality, vertexLabel?.propertiesList?.firstOrNull()?.cardinality)
    }

    @Test
    fun `getVertexLabelsByName can run multiple times with same properties`() {
        val (managementServer, graph) = createDefaults()
        val propertyName = "name"
        val property = buildProperty(propertyName)
        val request = buildLabel(name = "vertexName", properties = listOf(property))

        managementServer.ensureVertexLabel(graph.openManagement(), request)
        managementServer.ensureVertexLabel(graph.openManagement(), request)
        managementServer.ensureVertexLabel(graph.openManagement(), request)

        val label = managementServer.getVertexLabelsByName(graph.openManagement(), "vertexName").firstOrNull()

        assertEquals(1, label?.propertiesCount)
        assertEquals(propertyName, label?.propertiesList?.firstOrNull()?.name)
        assertEquals(PropertyDataType.Int32, label?.propertiesList?.firstOrNull()?.dataType)
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 8, 16])
    fun `getVertexLabelsByName creates property`(numberOfProperties: Int) {
        val (managementServer, graph) = createDefaults()
        val properties = (1..numberOfProperties).map { buildProperty("propertyName$it") }
        val request = buildLabel(name = "vertexName", properties = properties)
        managementServer.ensureVertexLabel(graph.openManagement(), request)

        val label = managementServer.getVertexLabelsByName(graph.openManagement(), "vertexName").firstOrNull()

        assertEquals(numberOfProperties, label?.propertiesCount)
    }

    @Test
    fun `getVertexLabels return multiple vertexLabels`() {
        val (managementServer, graph) = createDefaults()
        managementServer.ensureVertexLabel(graph.openManagement(), buildLabel("test1"))
        managementServer.ensureVertexLabel(graph.openManagement(), buildLabel("test2"))

        val vertexLabels = managementServer.getVertexLabels(graph.openManagement())

        assertEquals(2, vertexLabels.size)
    }

    @Test
    fun `getVertexLabels return multiple vertexLabels contains elements`() {
        val (managementServer, graph) = createDefaults()
        buildLabel("test1", managementServer= managementServer, graph = graph)
        buildLabel("test2", managementServer= managementServer, graph = graph)

        val vertexLabels = managementServer.getVertexLabels(graph.openManagement())

        assertNotNull(vertexLabels.firstOrNull { it.name == "test1" })
        assertNotNull(vertexLabels.firstOrNull { it.name == "test2" })
    }

    @Test
    fun `getVertexLabels returns multiple vertexLabels with property`() {
        val (managementServer, graph) = createDefaults()
        val propertyName = "name"
        val property = buildProperty(
            propertyName,
            dataType = PropertyDataType.Boolean,
            cardinality = VertexProperty.Cardinality.List
        )
        buildLabel("test1", properties = listOf(property), managementServer= managementServer, graph = graph)
        buildLabel("test2", properties = listOf(property), managementServer= managementServer, graph = graph)

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
        buildLabel(readOnly = true, managementServer= managementServer, graph = graph)

        val vertexLabel = managementServer.getVertexLabels(graph.openManagement()).firstOrNull()

        assertTrue(vertexLabel?.readOnly!!)
    }

    @Test
    fun `getVertexLabels vertexLabel marked as partitioned`() {
        val (managementServer, graph) = createDefaults()
        buildLabel(partitioned = true, managementServer = managementServer, graph = graph)

        val vertexLabel = managementServer.getVertexLabels(graph.openManagement()).firstOrNull()

        assertTrue(vertexLabel?.partitioned!!)
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 8, 16])
    fun `getVertexLabels creates property`(numberOfProperties: Int) {
        val (managementServer, graph) = createDefaults()
        val properties = (1..numberOfProperties).map { buildProperty("propertyName$it") }
        val request = buildLabel(name = "test", properties = properties)
        managementServer.ensureVertexLabel(graph.openManagement(), request)

        val vertexLabel = managementServer.getVertexLabels(graph.openManagement()).firstOrNull()

        assertEquals(numberOfProperties, vertexLabel?.propertiesCount)
    }

    private fun buildCompositeIndex(
        name: String = "byCompositeIndex",
        id: Long? = null,
        properties: List<VertexProperty> = emptyList()
    ): CompositeVertexIndex {
        val builder = CompositeVertexIndex.newBuilder()
            .setName(name)
            .addAllProperties(properties)
        if (id != null) {
            builder.id = Int64Value.of(id)
        }
        return builder.build()
    }

    @Test
    fun `ensureCompositeIndexByVertexLabel create basic index`() {
        val (managementServer, graph) = createDefaults()
        val property = buildProperty(dataType = PropertyDataType.String)
        val label = buildLabel(properties = listOf(property), managementServer = managementServer, graph = graph)
        val index = buildCompositeIndex("test", properties = listOf(label.propertiesList!!.first()))

        val compositeIndex = managementServer.ensureCompositeIndexByVertexLabel(graph.openManagement(), label, index)

        assertEquals("test", compositeIndex?.name)
        assertEquals(1, compositeIndex?.propertiesCount)
        assertNotNull(compositeIndex?.id)
    }

    @Test
    fun `ensureCompositeIndexByVertexLabel create index with two properties`() {
        val (managementServer, graph) = createDefaults()
        val property1 = buildProperty("property1", dataType = PropertyDataType.String)
        val property2 = buildProperty("property2", dataType = PropertyDataType.String)
        val property3 = buildProperty("property3", dataType = PropertyDataType.String)
        val label =
            buildLabel(properties = listOf(property1, property2, property3), managementServer = managementServer, graph = graph)
        val index = buildCompositeIndex("test", properties = listOf(property1, property2))

        val compositeIndex = managementServer.ensureCompositeIndexByVertexLabel(graph.openManagement(), label, index)!!

        assertEquals(2, compositeIndex.propertiesCount)
        assertTrue(compositeIndex.propertiesList.any { it.name == property1.name })
        assertTrue(compositeIndex.propertiesList.any { it.name == property2.name })
    }

    @Test
    fun `getCompositeIndicesByVertexLabel get no index`() {
        val (managementServer, graph) = createDefaults()
        val property = buildProperty(dataType = PropertyDataType.String)
        val label = buildLabel(properties = listOf(property), managementServer = managementServer, graph = graph)

        val compositeIndex = managementServer.getCompositeIndicesByVertexLabel(graph, label).firstOrNull()

        assertNull(compositeIndex)
    }

    @Test
    fun `getCompositeIndicesByVertexLabel basic index`() {
        val (managementServer, graph) = createDefaults()
        val property = buildProperty(dataType = PropertyDataType.String)
        val label = buildLabel(properties = listOf(property), managementServer = managementServer, graph = graph)
        val index = buildCompositeIndex("test", properties = listOf(label.propertiesList!!.first()))
        managementServer.ensureCompositeIndexByVertexLabel(graph.openManagement(), label, index)

        val compositeIndex = managementServer.getCompositeIndicesByVertexLabel(graph, label).first()

        assertEquals("test", compositeIndex.name)
        assertEquals(1, compositeIndex.propertiesCount)
        assertNotNull(compositeIndex.id)
    }
}

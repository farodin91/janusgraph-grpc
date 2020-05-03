package org.janusgraph.grpc.server

import com.google.protobuf.Int64Value
import org.janusgraph.core.JanusGraph
import org.janusgraph.grpc.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource

class ManagementForEdgeLabelsTests {

    private fun createDefaults() =
        ManagementForEdgeLabels() to JanusGraphTestUtils.getJanusGraph()

    private fun buildLabel(
        name: String = "test",
        edgeId: Long? = null,
        properties: List<EdgeProperty> = emptyList(),
        managementServer: IManagementForEdgeLabels? = null,
        graph: JanusGraph? = null
    ): EdgeLabel {
        val builder = EdgeLabel.newBuilder().setName(name).addAllProperties(properties)
        if (edgeId != null) {
            builder.id = Int64Value.of(edgeId)
        }
        return if (managementServer != null && graph != null) {
            managementServer.ensureEdgeLabel(graph.openManagement(), builder.build())!!
        } else {
            builder.build()
        }
    }

    private fun buildProperty(
        name: String = "propertyName",
        id: Long? = null,
        dataType: PropertyDataType = PropertyDataType.Int32
    ): EdgeProperty {
        val builder = EdgeProperty.newBuilder()
            .setName(name)
            .setDataType(dataType)
        if (id != null) {
            builder.id = Int64Value.of(id)
        }
        return builder.build()
    }

    @Test
    fun `ensureEdgeLabel create basic edgeLabel`() {
        val (managementServer, graph) = createDefaults()
        val request = buildLabel()

        val edgeLabel = managementServer.ensureEdgeLabel(graph.openManagement(), request)

        Assertions.assertEquals("test", edgeLabel?.name)
    }

    @Test
    fun `ensureEdgeLabel can run multiple times`() {
        val (managementServer, graph) = createDefaults()
        val request = buildLabel()

        managementServer.ensureEdgeLabel(graph.openManagement(), request)
        managementServer.ensureEdgeLabel(graph.openManagement(), request)
        val edgeLabel = managementServer.ensureEdgeLabel(graph.openManagement(), request)

        Assertions.assertEquals("test", edgeLabel?.name)
    }

    @Test
    fun `ensureEdgeLabel update name`() {
        val (managementServer, graph) = createDefaults()
        val request1 = buildLabel("test1")
        val ensureEdgeLabel = managementServer.ensureEdgeLabel(graph.openManagement(), request1)
        val request2 = buildLabel("test2", ensureEdgeLabel?.id?.value)

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
        val property = buildProperty(propertyName, dataType = propertyDataType)
        val request = buildLabel(name = "edgeName", properties = listOf(property))

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
        val property = buildProperty(propertyName)
        val request = buildLabel(name = "edgeName", properties = listOf(property))

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
            PropertyDataType.Int32,
            edgeLabel?.propertiesList?.firstOrNull()?.dataType
        )
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 8, 16])
    fun `ensureEdgeLabel creates property`(numberOfProperties: Int) {
        val (managementServer, graph) = createDefaults()
        val properties = (1..numberOfProperties).map { buildProperty("propertyName$it") }
        val request = buildLabel(name = "test", properties = properties)

        val label = managementServer.ensureEdgeLabel(graph.openManagement(), request)

        Assertions.assertEquals(numberOfProperties, label?.propertiesCount)
    }

    @Test
    fun `ensureEdgeLabel with property update label name`() {
        val (managementServer, graph) = createDefaults()
        val request1 = buildLabel("test1")
        val propertyName = "propertyName"
        val ensureLabel = managementServer.ensureEdgeLabel(graph.openManagement(), request1)
        val property = buildProperty(propertyName)
        val request2 = buildLabel("test2", ensureLabel?.id?.value, properties = listOf(property))

        val label = managementServer.ensureEdgeLabel(graph.openManagement(), request2)

        Assertions.assertEquals(1, label?.propertiesCount)
        Assertions.assertEquals(propertyName, label?.propertiesList?.firstOrNull()?.name)
    }

    @ParameterizedTest
    @EnumSource(PropertyDataType::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNRECOGNIZED"])
    fun `ensureEdgeLabel add property as update`(propertyDataType: PropertyDataType) {
        val (managementServer, graph) = createDefaults()
        val propertyName = "propertyName"
        val request1 = buildLabel(name = "test")
        managementServer.ensureEdgeLabel(graph.openManagement(), request1)
        val property = buildProperty(propertyName, dataType = propertyDataType)
        val request = buildLabel(name = "test", properties = listOf(property))

        val label = managementServer.ensureEdgeLabel(graph.openManagement(), request)

        Assertions.assertEquals(1, label?.propertiesCount)
        Assertions.assertEquals(propertyName, label?.propertiesList?.firstOrNull()?.name)
        Assertions.assertEquals(propertyDataType, label?.propertiesList?.firstOrNull()?.dataType)
    }

    @Test
    fun `getEdgeLabelsByName update name works`() {
        val (managementServer, graph) = createDefaults()
        val request1 = buildLabel("test1")
        val ensureEdgeLabel = managementServer.ensureEdgeLabel(graph.openManagement(), request1)
        val request2 = buildLabel("test2", ensureEdgeLabel?.id?.value)
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
        managementServer.ensureEdgeLabel(graph.openManagement(), buildLabel(label))

        val edgeLabel = managementServer.getEdgeLabelsByName(graph.openManagement(), label).firstOrNull()

        Assertions.assertEquals(label, edgeLabel?.name)
    }

    @ParameterizedTest
    @EnumSource(PropertyDataType::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNRECOGNIZED"])
    fun `getEdgeLabelsByName returns property`(propertyDataType: PropertyDataType) {
        val (managementServer, graph) = createDefaults()
        val propertyName = "propertyName"
        val property = buildProperty(propertyName, dataType = propertyDataType)
        managementServer.ensureEdgeLabel(
            graph.openManagement(),
            buildLabel(name = "edgeName", properties = listOf(property))
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
    fun `getEdgeLabelsByName can run multiple times with same properties`() {
        val (managementServer, graph) = createDefaults()
        val propertyName = "name"
        val property = buildProperty(propertyName)
        val request = buildLabel(name = "edgeName", properties = listOf(property))

        managementServer.ensureEdgeLabel(graph.openManagement(), request)
        managementServer.ensureEdgeLabel(graph.openManagement(), request)
        managementServer.ensureEdgeLabel(graph.openManagement(), request)

        val label = managementServer.getEdgeLabelsByName(graph.openManagement(), "edgeName").firstOrNull()

        Assertions.assertEquals(1, label?.propertiesCount)
        Assertions.assertEquals(propertyName, label?.propertiesList?.firstOrNull()?.name)
        Assertions.assertEquals(PropertyDataType.Int32, label?.propertiesList?.firstOrNull()?.dataType)
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 8, 16])
    fun `getEdgeLabelsByName creates property`(numberOfProperties: Int) {
        val (managementServer, graph) = createDefaults()
        val properties = (1..numberOfProperties).map { buildProperty("propertyName$it") }
        val request = buildLabel(name = "edgeName", properties = properties)
        managementServer.ensureEdgeLabel(graph.openManagement(), request)

        val label = managementServer.getEdgeLabelsByName(graph.openManagement(), "edgeName").firstOrNull()

        Assertions.assertEquals(numberOfProperties, label?.propertiesCount)
    }

    @Test
    fun `getEdgeLabels return multiple edgeLabels test size`() {
        val (managementServer, graph) = createDefaults()
        managementServer.ensureEdgeLabel(graph.openManagement(), buildLabel("edgeName1"))
        managementServer.ensureEdgeLabel(graph.openManagement(), buildLabel("edgeName2"))

        val edgeLabels = managementServer.getEdgeLabels(graph.openManagement())

        Assertions.assertEquals(2, edgeLabels.size)
    }

    @Test
    fun `getEdgeLabels return multiple edgeLabels contains elements`() {
        val (managementServer, graph) = createDefaults()
        managementServer.ensureEdgeLabel(graph.openManagement(), buildLabel("edgeName1"))
        managementServer.ensureEdgeLabel(graph.openManagement(), buildLabel("edgeName2"))

        val edgeLabels = managementServer.getEdgeLabels(graph.openManagement())

        Assertions.assertNotNull(edgeLabels.firstOrNull { it.name == "edgeName1" })
        Assertions.assertNotNull(edgeLabels.firstOrNull { it.name == "edgeName2" })
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 8, 16])
    fun `getEdgeLabels creates property`(numberOfProperties: Int) {
        val (managementServer, graph) = createDefaults()
        val properties = (1..numberOfProperties).map { buildProperty("propertyName$it") }
        val request = buildLabel(name = "test", properties = properties)
        managementServer.ensureEdgeLabel(graph.openManagement(), request)

        val label = managementServer.getEdgeLabels(graph.openManagement()).firstOrNull()

        Assertions.assertEquals(numberOfProperties, label?.propertiesCount)
    }

    private fun buildCompositeIndex(
        name: String = "byCompositeIndex",
        id: Long? = null,
        properties: List<EdgeProperty> = emptyList()
    ): CompositeEdgeIndex {
        val builder = CompositeEdgeIndex.newBuilder()
            .setName(name)
            .addAllProperties(properties)
        if (id != null) {
            builder.id = Int64Value.of(id)
        }
        return builder.build()
    }

    @Test
    fun `ensureCompositeIndexByEdgeLabel create basic index`() {
        val (managementServer, graph) = createDefaults()
        val property = buildProperty(dataType = PropertyDataType.String)
        val label = buildLabel(properties = listOf(property), managementServer = managementServer, graph = graph)
        val index = buildCompositeIndex("test", properties = listOf(label.propertiesList!!.first()))

        val compositeIndex = managementServer.ensureCompositeIndexByEdgeLabel(graph.openManagement(), label, index)

        Assertions.assertEquals("test", compositeIndex?.name)
        Assertions.assertEquals(1, compositeIndex?.propertiesCount)
        Assertions.assertNotNull(compositeIndex?.id)
    }

    @Test
    fun `ensureCompositeIndexByEdgeLabel create index with two properties`() {
        val (managementServer, graph) = createDefaults()
        val property1 = buildProperty("property1", dataType = PropertyDataType.String)
        val property2 = buildProperty("property2", dataType = PropertyDataType.String)
        val property3 = buildProperty("property3", dataType = PropertyDataType.String)
        val label =
            buildLabel(properties = listOf(property1, property2, property3), managementServer = managementServer, graph = graph)
        val index = buildCompositeIndex("test", properties = listOf(property1, property2))

        val compositeIndex = managementServer.ensureCompositeIndexByEdgeLabel(graph.openManagement(), label, index)!!

        Assertions.assertEquals(2, compositeIndex.propertiesCount)
        Assertions.assertTrue(compositeIndex.propertiesList.any { it.name == property1.name })
        Assertions.assertTrue(compositeIndex.propertiesList.any { it.name == property2.name })
    }

    @Test
    fun `getCompositeIndicesByEdgeLabel get no index`() {
        val (managementServer, graph) = createDefaults()
        val property = buildProperty(dataType = PropertyDataType.String)
        val label = buildLabel(properties = listOf(property), managementServer = managementServer, graph = graph)

        val compositeIndex = managementServer.getCompositeIndicesByEdgeLabel(graph, label).firstOrNull()

        Assertions.assertNull(compositeIndex)
    }

    @Test
    fun `getCompositeIndicesByEdgeLabel basic index`() {
        val (managementServer, graph) = createDefaults()
        val property = buildProperty(dataType = PropertyDataType.String)
        val label = buildLabel(properties = listOf(property), managementServer = managementServer, graph = graph)
        val index = buildCompositeIndex("test", properties = listOf(label.propertiesList!!.first()))
        managementServer.ensureCompositeIndexByEdgeLabel(graph.openManagement(), label, index)

        val compositeIndex = managementServer.getCompositeIndicesByEdgeLabel(graph, label).first()

        Assertions.assertEquals("test", compositeIndex.name)
        Assertions.assertEquals(1, compositeIndex.propertiesCount)
        Assertions.assertNotNull(compositeIndex.id)
    }

}

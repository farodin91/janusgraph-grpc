package org.janusgraph.grpc.server

import com.google.protobuf.Int64Value
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.attribute.Geoshape
import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.grpc.*
import java.util.*


class ManagementServer(private val contextManager: ContextManager) {
    private fun convertDataTypeToJavaClass(dataType: PropertyDataType): Class<*> =
        when (dataType) {
            PropertyDataType.String -> java.lang.String::class.java
            PropertyDataType.Character -> java.lang.Character::class.java
            PropertyDataType.Boolean -> java.lang.Boolean::class.java
            PropertyDataType.Int8 -> java.lang.Byte::class.java
            PropertyDataType.Int16 -> java.lang.Short::class.java
            PropertyDataType.Int32 -> java.lang.Integer::class.java
            PropertyDataType.Int64 -> java.lang.Long::class.java
            PropertyDataType.Float32 -> java.lang.Float::class.java
            PropertyDataType.Float64 -> java.lang.Double::class.java
            PropertyDataType.Date -> Date::class.java
            PropertyDataType.JavaObject -> Any::class.java
            PropertyDataType.GeoShape -> Geoshape::class.java
            PropertyDataType.Uuid -> UUID::class.java
            PropertyDataType.UNRECOGNIZED -> TODO()
        }

    private fun convertJavaClassToDataType(dataType: Class<*>): PropertyDataType? {
        return when (dataType) {
            java.lang.String::class.java -> PropertyDataType.String
            java.lang.Character::class.java -> PropertyDataType.Character
            java.lang.Boolean::class.java -> PropertyDataType.Boolean
            java.lang.Byte::class.java -> PropertyDataType.Int8
            java.lang.Short::class.java -> PropertyDataType.Int16
            java.lang.Integer::class.java -> PropertyDataType.Int32
            java.lang.Long::class.java -> PropertyDataType.Int64
            java.lang.Float::class.java -> PropertyDataType.Float32
            java.lang.Double::class.java -> PropertyDataType.Float64
            Date::class.java -> PropertyDataType.Date
            Any::class.java -> PropertyDataType.JavaObject
            Geoshape::class.java -> PropertyDataType.GeoShape
            UUID::class.java -> PropertyDataType.Uuid
            else -> PropertyDataType.UNRECOGNIZED
        }
    }

    private fun createVertexPropertyProto(property: PropertyKey): VertexProperty =
        VertexProperty.newBuilder()
            .setName(property.name())
            .setDataType(convertJavaClassToDataType(property.dataType()))
            .build()

    private fun createVertexLabelProto(vertexLabel: org.janusgraph.core.VertexLabel, properties: List<PropertyKey>) =
        VertexLabel.newBuilder()
            .setId(Int64Value.of(vertexLabel.longId()))
            .setName(vertexLabel.name())
            .addAllProperties(properties.map { createVertexPropertyProto(it) })
            .build()

    private fun createEdgePropertyProto(property: PropertyKey): EdgeProperty =
        EdgeProperty.newBuilder()
            .setName(property.name())
            .setDataType(convertJavaClassToDataType(property.dataType()))
            .build()

    private fun createEdgeLabelProto(edgeLabel: org.janusgraph.core.EdgeLabel, properties: List<PropertyKey>) =
        EdgeLabel.newBuilder()
            .setId(Int64Value.of(edgeLabel.longId()))
            .setName(edgeLabel.name())
            .addAllProperties(properties.map { createEdgePropertyProto(it) })
            .build()

    fun getVertexLabelsByName(request: GetVertexLabelsByNameRequest): List<VertexLabel> {
        val vertexLabel = contextManager
            .getManagement(request.context)
            ?.getVertexLabel(request.name) ?: return emptyList()

        return listOf(createVertexLabelProto(vertexLabel, vertexLabel.mappedProperties().toList()))
    }

    fun getVertexLabels(request: GetVertexLabelsRequest): List<VertexLabel> {
        return contextManager
            .getManagement(request.context)
            ?.vertexLabels?.map { createVertexLabelProto(it, it.mappedProperties().toList()) } ?: return emptyList()
    }

    private fun getVertexLabel(
        management: JanusGraphManagement,
        vertexLabel: VertexLabel
    ): org.janusgraph.core.VertexLabel? =
        if (vertexLabel.hasId()) {
            management.vertexLabels.first { it.longId() == vertexLabel.id.value }
                ?: throw IllegalArgumentException("No vertexLabel found with id")
        } else {
            management.getVertexLabel(vertexLabel.name)
        }

    fun ensureVertexLabel(request: EnsureVertexLabelRequest): VertexLabel? {
        val management = contextManager.getManagement(request.context) ?: return null
        val requestLabel = request.label
        val label = getVertexLabel(management, requestLabel)
        if (label?.name() == requestLabel.name) {
            return createVertexLabelProto(label!!, label.mappedProperties().toList())
        }
        val (vertexLabel, properties) = if (label != null) {
            management.changeName(label, requestLabel.name)
            label to emptyList()
        } else {
            val vertexLabel = management.makeVertexLabel(requestLabel.name).make()
            vertexLabel to requestLabel.propertiesList
                .map { getOrCreateVertexProperty(management, vertexLabel, it) }
        }
        val response = createVertexLabelProto(vertexLabel, properties)
        management.commit()
        return response
    }

    private fun getOrCreateVertexProperty(
        management: JanusGraphManagement,
        vertexLabel: org.janusgraph.core.VertexLabel,
        property: VertexProperty
    ): PropertyKey {
        val propertyKey =
            management.getPropertyKey(property.name) ?: management.makePropertyKey(property.name).dataType(
                convertDataTypeToJavaClass(property.dataType)
            ).make()

        management.addProperties(vertexLabel, propertyKey)
        return propertyKey
    }

    fun getEdgeLabelsByName(request: GetEdgeLabelsByNameRequest): List<EdgeLabel> {
        val edgeLabel = contextManager
            .getManagement(request.context)
            ?.getEdgeLabel(request.name) ?: return emptyList()

        return listOf(createEdgeLabelProto(edgeLabel, edgeLabel.mappedProperties().toList()))
    }

    fun getEdgeLabels(request: GetEdgeLabelsRequest): List<EdgeLabel> {
        return contextManager
            .getManagement(request.context)
            ?.getRelationTypes(org.janusgraph.core.EdgeLabel::class.java)
            ?.map { createEdgeLabelProto(it, it.mappedProperties().toList()) } ?: return emptyList()
    }

    private fun getEdgeLabel(
        management: JanusGraphManagement,
        edgeLabel: EdgeLabel
    ): org.janusgraph.core.EdgeLabel? =
        if (edgeLabel.hasId()) {
            management
                .getRelationTypes(org.janusgraph.core.EdgeLabel::class.java)
                .first { it.longId() == edgeLabel.id.value }
                ?: throw IllegalArgumentException("No vertexLabel found with id")
        } else {
            management.getEdgeLabel(edgeLabel.name)
        }

    fun ensureEdgeLabel(request: EnsureEdgeLabelRequest): EdgeLabel? {
        val management = contextManager.getManagement(request.context) ?: return null
        val requestLabel = request.label
        val label = getEdgeLabel(management, requestLabel)
        if (label?.name() == requestLabel.name) {
            return createEdgeLabelProto(label!!, label.mappedProperties().toList())
        }
        val (edgeLabel, properties) = if (label != null) {
            management.changeName(label, requestLabel.name)
            label to emptyList()
        } else {
            val vertexLabel = management.makeEdgeLabel(requestLabel.name).make()
            vertexLabel to requestLabel.propertiesList
                .map { getOrCreateEdgeProperty(management, vertexLabel, it) }
        }
        val response = createEdgeLabelProto(edgeLabel, properties)
        management.commit()
        return response
    }

    private fun getOrCreateEdgeProperty(
        management: JanusGraphManagement,
        vertexLabel: org.janusgraph.core.EdgeLabel,
        property: EdgeProperty
    ): PropertyKey {
        val propertyKey =
            management.getPropertyKey(property.name) ?: management.makePropertyKey(property.name).dataType(
                convertDataTypeToJavaClass(property.dataType)
            ).make()

        management.addProperties(vertexLabel, propertyKey)
        return propertyKey
    }
}
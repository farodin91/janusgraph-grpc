package org.janusgraph.grpc.server

import org.janusgraph.core.PropertyKey
import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.grpc.CompositeVertexIndex
import org.janusgraph.grpc.VertexLabel
import org.janusgraph.grpc.VertexProperty

class ManagementForVertexLabels : IManagementForVertexLabels {

    override fun getVertexLabelsByName(management: JanusGraphManagement, name: String): List<VertexLabel> {
        val vertexLabel = management.getVertexLabel(name) ?: return emptyList()

        return listOf(
            createVertexLabelProto(
                vertexLabel,
                vertexLabel.mappedProperties().toList()
            )
        )
    }

    override fun getVertexLabels(management: JanusGraphManagement): List<VertexLabel> {
        return management.vertexLabels?.map {
            createVertexLabelProto(
                it,
                it.mappedProperties().toList()
            )
        } ?: return emptyList()
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

    private fun getOrCreateVertexProperty(
        management: JanusGraphManagement,
        vertexLabel: org.janusgraph.core.VertexLabel,
        property: VertexProperty
    ): PropertyKey {
        val propertyKey =
            management.getPropertyKey(property.name) ?: management
                .makePropertyKey(property.name)
                .dataType(convertDataTypeToJavaClass(property.dataType))
                .cardinality(convertCardinalityToJavaClass(property.cardinality))
                .make()

        management.addProperties(vertexLabel, propertyKey)
        return propertyKey
    }

    override fun ensureVertexLabel(management: JanusGraphManagement, requestLabel: VertexLabel): VertexLabel? {
        val label = getVertexLabel(management, requestLabel)
        if (label?.name() == requestLabel.name) {
            return createVertexLabelProto(
                label!!,
                label.mappedProperties().toList()
            )
        }
        val (vertexLabel, properties) = if (label != null) {
            management.changeName(label, requestLabel.name)
            label to emptyList()
        } else {
            val vertexLabelMaker = management.makeVertexLabel(requestLabel.name)
            if (requestLabel.readOnly)
                vertexLabelMaker.setStatic()
            if (requestLabel.partitioned)
                vertexLabelMaker.partition()
            val vertexLabel = vertexLabelMaker.make()
            vertexLabel to requestLabel.propertiesList
                .map { getOrCreateVertexProperty(management, vertexLabel, it) }
        }
        val response = createVertexLabelProto(vertexLabel, properties)
        management.commit()
        return response
    }

    override fun ensureCompositeIndexByVertexLabel(
        management: JanusGraphManagement,
        vertexLabel: VertexLabel,
        index: CompositeVertexIndex
    ): CompositeVertexIndex? {
        TODO("not implemented")
    }
}

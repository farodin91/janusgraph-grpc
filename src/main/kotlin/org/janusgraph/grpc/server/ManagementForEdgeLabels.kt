package org.janusgraph.grpc.server

import org.janusgraph.core.PropertyKey
import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.grpc.EdgeLabel
import org.janusgraph.grpc.EdgeProperty


class ManagementForEdgeLabels: IManagementForEdgeLabels {

    override fun getEdgeLabelsByName(management: JanusGraphManagement, name: String): List<EdgeLabel> {
        val edgeLabel = management.getEdgeLabel(name) ?: return emptyList()

        return listOf(createEdgeLabelProto(edgeLabel, edgeLabel.mappedProperties().toList()))
    }

    override fun getEdgeLabels(management: JanusGraphManagement): List<EdgeLabel> {
        return management.getRelationTypes(org.janusgraph.core.EdgeLabel::class.java)
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

    override fun ensureEdgeLabel(management: JanusGraphManagement, requestLabel: EdgeLabel): EdgeLabel? {
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
            management.getPropertyKey(property.name) ?: management
                .makePropertyKey(property.name)
                .dataType(convertDataTypeToJavaClass(property.dataType))
                .make()

        management.addProperties(vertexLabel, propertyKey)
        return propertyKey
    }
}

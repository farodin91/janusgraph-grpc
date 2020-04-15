package org.janusgraph.grpc.server

import org.janusgraph.grpc.*

class ManagementServer(private val contextManager: ContextManager) {
    fun getVertexLabelsByName(request: GetVertexLabelsByNameRequest): List<VertexLabel> {
        val vertexLabel = contextManager
            .getManagement(request.context)
            ?.getVertexLabel(request.name) ?: return emptyList()

        return listOf(createVertexLabelProto(vertexLabel))
    }

    fun getVertexLabels(request: GetVertexLabelsRequest): List<VertexLabel> {
        return contextManager
            .getManagement(request.context)
            ?.vertexLabels?.map { createVertexLabelProto(it) } ?: return emptyList()
    }

    fun ensureVertexLabel(request: EnsureVertexLabelRequest): VertexLabel? {
        val management = contextManager.getManagement(request.context) ?: return null
        val label = management.getVertexLabel(request.label.name)
        if (label != null) {
            return createVertexLabelProto(label)
        }
        val vertexLabel = management.makeVertexLabel(request.label.name).make()
        val response = createVertexLabelProto(vertexLabel)
        management.commit()

        return response
    }

    private fun createVertexLabelProto(vertexLabel: org.janusgraph.core.VertexLabel) =
        VertexLabel.newBuilder()
            .setId(vertexLabel.longId())
            .setName(vertexLabel.name())
            .build()

    fun getEdgeLabelsByName(request: GetEdgeLabelsByNameRequest): List<EdgeLabel> {
        val edgeLabel = contextManager
            .getManagement(request.context)
            ?.getEdgeLabel(request.name) ?: return emptyList()

        return listOf(createEdgeLabelProto(edgeLabel))
    }

    fun getEdgeLabels(request: GetEdgeLabelsRequest): List<EdgeLabel> {
        return contextManager
            .getManagement(request.context)
            ?.getRelationTypes(org.janusgraph.core.EdgeLabel::class.java)
            ?.map { createEdgeLabelProto(it) } ?: return emptyList()
    }

    fun ensureEdgeLabel(request: EnsureEdgeLabelRequest): EdgeLabel? {
        val management = contextManager.getManagement(request.context) ?: return null
        val label = management.getEdgeLabel(request.label.name)
        if (label != null) {
            return createEdgeLabelProto(label)
        }
        val edgeLabel = management.makeEdgeLabel(request.label.name).make()
        val response = createEdgeLabelProto(edgeLabel)
        management.commit()

        return response
    }

    private fun createEdgeLabelProto(edgeLabel: org.janusgraph.core.EdgeLabel) =
        EdgeLabel.newBuilder()
            .setId(edgeLabel.longId())
            .setName(edgeLabel.name())
            .build()
}
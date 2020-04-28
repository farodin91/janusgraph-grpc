package org.janusgraph.grpc.server

import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.grpc.CompositeVertexIndex
import org.janusgraph.grpc.VertexLabel

interface IManagementForVertexLabels {
    fun getVertexLabelsByName(management: JanusGraphManagement, name: String): List<VertexLabel>
    fun getVertexLabels(management: JanusGraphManagement): List<VertexLabel>
    fun ensureVertexLabel(management: JanusGraphManagement, requestLabel: VertexLabel): VertexLabel?
    fun ensureCompositeIndexByVertexLabel(management: JanusGraphManagement, vertexLabel: VertexLabel, index: CompositeVertexIndex): CompositeVertexIndex?
}

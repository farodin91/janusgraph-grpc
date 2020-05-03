package org.janusgraph.grpc.server

import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.graphdb.database.StandardJanusGraph
import org.janusgraph.grpc.CompositeVertexIndex
import org.janusgraph.grpc.VertexLabel

interface IManagementForVertexLabels {
    fun getVertexLabelsByName(management: JanusGraphManagement, name: String): List<VertexLabel>
    fun getVertexLabels(management: JanusGraphManagement): List<VertexLabel>
    fun ensureVertexLabel(management: JanusGraphManagement, requestLabel: VertexLabel): VertexLabel?
    fun ensureCompositeIndexByVertexLabel(management: JanusGraphManagement, requestLabel: VertexLabel, requestIndex: CompositeVertexIndex): CompositeVertexIndex?
    fun getCompositeIndicesByVertexLabel(graph: StandardJanusGraph, requestLabel: VertexLabel): List<CompositeVertexIndex>
}

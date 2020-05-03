package org.janusgraph.grpc.server

import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.graphdb.database.StandardJanusGraph
import org.janusgraph.grpc.CompositeEdgeIndex
import org.janusgraph.grpc.CompositeVertexIndex
import org.janusgraph.grpc.EdgeLabel
import org.janusgraph.grpc.VertexLabel

interface IManagementForEdgeLabels {
    fun getEdgeLabelsByName(management: JanusGraphManagement, name: String): List<EdgeLabel>
    fun getEdgeLabels(management: JanusGraphManagement): List<EdgeLabel>
    fun ensureEdgeLabel(management: JanusGraphManagement, requestLabel: EdgeLabel): EdgeLabel?
    fun ensureCompositeIndexByEdgeLabel(management: JanusGraphManagement, requestLabel: EdgeLabel, requestIndex: CompositeEdgeIndex): CompositeEdgeIndex?
    fun getCompositeIndicesByEdgeLabel(graph: StandardJanusGraph, requestLabel: EdgeLabel): List<CompositeEdgeIndex>
}

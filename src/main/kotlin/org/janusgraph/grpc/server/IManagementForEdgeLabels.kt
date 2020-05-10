package org.janusgraph.grpc.server

import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.graphdb.database.StandardJanusGraph
import org.janusgraph.grpc.*

interface IManagementForEdgeLabels {
    fun getEdgeLabelsByName(management: JanusGraphManagement, name: String): List<EdgeLabel>
    fun getEdgeLabels(management: JanusGraphManagement): List<EdgeLabel>
    fun ensureEdgeLabel(management: JanusGraphManagement, requestLabel: EdgeLabel): EdgeLabel?
    fun ensureCompositeIndexByEdgeLabel(management: JanusGraphManagement, requestLabel: EdgeLabel, requestIndex: CompositeEdgeIndex): CompositeEdgeIndex?
    fun getCompositeIndicesByEdgeLabel(graph: StandardJanusGraph, requestLabel: EdgeLabel): List<CompositeEdgeIndex>

    fun getCompositeIndicesForEdge(graph: StandardJanusGraph): List<CompositeEdgeIndex>

    fun ensureMixedIndexByEdgeLabel(management: JanusGraphManagement, requestLabel: EdgeLabel, requestIndex: MixedEdgeIndex): MixedEdgeIndex?
    fun getMixedIndicesByEdgeLabel(graph: StandardJanusGraph, requestLabel: EdgeLabel): List<MixedEdgeIndex>
}

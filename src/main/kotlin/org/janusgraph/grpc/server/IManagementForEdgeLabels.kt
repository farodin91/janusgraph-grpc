package org.janusgraph.grpc.server

import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.grpc.EdgeLabel

interface IManagementForEdgeLabels {
    fun getEdgeLabelsByName(management: JanusGraphManagement, name: String): List<EdgeLabel>
    fun getEdgeLabels(management: JanusGraphManagement): List<EdgeLabel>
    fun ensureEdgeLabel(management: JanusGraphManagement, requestLabel: EdgeLabel): EdgeLabel?
}

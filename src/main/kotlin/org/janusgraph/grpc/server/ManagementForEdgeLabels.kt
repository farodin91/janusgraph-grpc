package org.janusgraph.grpc.server

import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.Cardinality
import org.janusgraph.core.Multiplicity
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.graphdb.database.StandardJanusGraph
import org.janusgraph.graphdb.transaction.StandardJanusGraphTx
import org.janusgraph.graphdb.types.CompositeIndexType
import org.janusgraph.graphdb.types.MixedIndexType
import org.janusgraph.grpc.*
import java.lang.NullPointerException


class ManagementForEdgeLabels : IManagementForEdgeLabels {

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
        val name = requestLabel.name ?: throw NullPointerException("name should not be null")
        val edgeLabel = when {
            label?.name() == name -> label
            label != null -> {
                management.changeName(label, name)
                label
            }
            else -> {
                val edgeLabelMaker = management.makeEdgeLabel(name)

                if (requestLabel.multiplicity != null)
                    edgeLabelMaker.multiplicity(Multiplicity.valueOf(requestLabel.multiplicity.toString()))

                if (requestLabel.directed != null) {
                    if (requestLabel.directed)
                        edgeLabelMaker.directed()
                    else
                        edgeLabelMaker.unidirected()
                }
                edgeLabelMaker.make()
            }
        }
        val properties = requestLabel.propertiesList.map { getOrCreateEdgeProperty(management, edgeLabel, it) }
        val response = createEdgeLabelProto(edgeLabel, properties)
        management.commit()
        return response
    }

    private fun getOrCreateEdgeProperty(
        management: JanusGraphManagement,
        label: org.janusgraph.core.EdgeLabel,
        property: EdgeProperty
    ): PropertyKey {
        val propertyKey =
            management.getPropertyKey(property.name) ?: management
                .makePropertyKey(property.name)
                .dataType(convertDataTypeToJavaClass(property.dataType))
                .make()

        val connections = label.mappedProperties()
        if (!connections.contains(propertyKey)) {
            management.addProperties(label, propertyKey)
        }
        return propertyKey
    }

    override fun ensureCompositeIndexByEdgeLabel(
        management: JanusGraphManagement,
        requestLabel: EdgeLabel,
        requestIndex: CompositeEdgeIndex
    ): CompositeEdgeIndex? {
        val label = getEdgeLabel(management, requestLabel) ?: throw NullPointerException("vertex should exists")

        val keys = requestIndex.propertiesList.map { management.getPropertyKey(it.name) }
        val builder = management.buildIndex(requestIndex.name, Edge::class.java)
            .indexOnly(label)

        keys.forEach { builder.addKey(it) }

        val graphIndex = builder.buildCompositeIndex()
        val properties = graphIndex.fieldKeys.map { createEdgePropertyProto(it) }

        val compositeIndex = CompositeEdgeIndex.newBuilder()
            .setName(graphIndex.name())
            .addAllProperties(properties)
            .build()
        management.commit()
        return compositeIndex
    }

    private fun getEdgeLabelTx(
        tx: StandardJanusGraphTx,
        label: EdgeLabel
    ): org.janusgraph.core.EdgeLabel? =
        if (label.hasId()) {
            getEdgeLabels(tx).firstOrNull { it.longId() == label.id.value }
                ?: throw IllegalArgumentException("No vertexLabel found with id")
        } else {
            tx.getEdgeLabel(label.name)
        }

    override fun getCompositeIndicesByEdgeLabel(
        graph: StandardJanusGraph,
        requestLabel: EdgeLabel
    ): List<CompositeEdgeIndex> {
        val tx = graph.buildTransaction().disableBatchLoading().start() as StandardJanusGraphTx
        val label = getEdgeLabelTx(tx, requestLabel)
        val graphIndexes = getGraphIndices(tx, Edge::class.java)
        val indices = graphIndexes
            .filterIsInstance<CompositeIndexType>()
            .filter { it.schemaTypeConstraint == label }
            .map {
                CompositeEdgeIndex.newBuilder()
                    .setName(it.name)
                    .addAllProperties(it.fieldKeys.map { property -> createEdgePropertyProto(property.fieldKey) })
                    .build()
            }
        tx.rollback()
        return indices
    }

    override fun getCompositeIndicesForEdge(
        graph: StandardJanusGraph
    ): List<CompositeEdgeIndex> {
        val tx = graph.buildTransaction().disableBatchLoading().start() as StandardJanusGraphTx
        val graphIndexes = getGraphIndices(tx, Edge::class.java)
        val indices = graphIndexes
            .filterIsInstance<CompositeIndexType>()
            .map {
                CompositeEdgeIndex.newBuilder()
                    .setName(it.name)
                    .addAllProperties(it.fieldKeys.map { property -> createEdgePropertyProto(property.fieldKey) })
                    .build()
            }
        tx.rollback()
        return indices
    }

    override fun ensureMixedIndexByEdgeLabel(
        management: JanusGraphManagement,
        requestLabel: EdgeLabel,
        requestIndex: MixedEdgeIndex
    ): MixedEdgeIndex? {
        val label = getEdgeLabel(management, requestLabel) ?: throw NullPointerException("vertex should exists")

        val keys = requestIndex.propertiesList.map { management.getPropertyKey(it.name) }
        val builder = management.buildIndex(requestIndex.name, Edge::class.java)
            .indexOnly(label)

        keys.forEach { builder.addKey(it) }

        val graphIndex = builder.buildMixedIndex(requestIndex.backend)
        val properties = graphIndex.fieldKeys.map { createEdgePropertyProto(it) }

        val compositeIndex = MixedEdgeIndex.newBuilder()
            .setName(graphIndex.name())
            .addAllProperties(properties)
            .setBackend(graphIndex.backingIndex)
            .build()
        management.commit()
        return compositeIndex
    }

    override fun getMixedIndicesByEdgeLabel(graph: StandardJanusGraph, requestLabel: EdgeLabel): List<MixedEdgeIndex> {
        val tx = graph.buildTransaction().disableBatchLoading().start() as StandardJanusGraphTx
        val label = getEdgeLabelTx(tx, requestLabel)
        val graphIndexes = getGraphIndices(tx, Edge::class.java)
        val indices = graphIndexes
            .filterIsInstance<MixedIndexType>()
            .filter { it.schemaTypeConstraint == label }
            .map {
                MixedEdgeIndex.newBuilder()
                    .setName(it.name)
                    .addAllProperties(it.fieldKeys.map { property -> createEdgePropertyProto(property.fieldKey) })
                    .setBackend(it.backingIndexName)
                    .build()
            }
        tx.rollback()
        return indices
    }
}

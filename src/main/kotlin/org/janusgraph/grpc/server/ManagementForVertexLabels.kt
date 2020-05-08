package org.janusgraph.grpc.server

import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.Cardinality
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.graphdb.database.StandardJanusGraph
import org.janusgraph.graphdb.transaction.StandardJanusGraphTx
import org.janusgraph.graphdb.types.CompositeIndexType
import org.janusgraph.graphdb.types.MixedIndexType
import org.janusgraph.grpc.CompositeVertexIndex
import org.janusgraph.grpc.MixedVertexIndex
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
        label: VertexLabel
    ): org.janusgraph.core.VertexLabel? =
        if (label.hasId()) {
            management.vertexLabels.first { it.longId() == label.id.value }
                ?: throw IllegalArgumentException("No vertexLabel found with id")
        } else {
            print("I'm retrieving vertex label with name " + label.name)
            management.getVertexLabel(label.name)
        }

    private fun getOrCreateVertexProperty(
        management: JanusGraphManagement,
        label: org.janusgraph.core.VertexLabel,
        property: VertexProperty
    ): PropertyKey {
        val propertyKey =
            management.getPropertyKey(property.name) ?: management
                .makePropertyKey(property.name)
                .dataType(convertDataTypeToJavaClass(property.dataType))
                .cardinality(convertCardinalityToJavaClass(property.cardinality))
                .make()
        val connections = label.mappedProperties()
        if (!connections.contains(propertyKey)) {
            management.addProperties(label, propertyKey)
        }
        return propertyKey
    }

    override fun ensureVertexLabel(management: JanusGraphManagement, requestLabel: VertexLabel): VertexLabel? {
        val label = getVertexLabel(management, requestLabel)
        val name = requestLabel.name ?: throw NullPointerException("name should not be null")
        val vertexLabel = when {
            label?.name() == name -> label
            label != null -> {
                management.changeName(label, name)
                label
            }
            else -> {
                val vertexLabelMaker = management.makeVertexLabel(name)
                if (requestLabel.readOnly)
                    vertexLabelMaker.setStatic()
                if (requestLabel.partitioned)
                    vertexLabelMaker.partition()
                val vertexLabel = vertexLabelMaker.make()
                vertexLabel
            }
        }
        val properties = requestLabel.propertiesList.map { getOrCreateVertexProperty(management, vertexLabel, it) }
        val response = createVertexLabelProto(vertexLabel, properties)
        management.commit()
        return response
    }

    override fun ensureCompositeIndexByVertexLabel(
        management: JanusGraphManagement,
        requestLabel: VertexLabel,
        requestIndex: CompositeVertexIndex
    ): CompositeVertexIndex? {
        val label = getVertexLabel(management, requestLabel) ?: throw NullPointerException("vertex should exists")

        val keys = requestIndex.propertiesList.map { management.getPropertyKey(it.name) }
        val builder = management.buildIndex(requestIndex.name, Vertex::class.java)
            .indexOnly(label)

        if (requestIndex.unique)
            builder.unique()

        keys.forEach { builder.addKey(it) }

        val graphIndex = builder.buildCompositeIndex()
        val properties = graphIndex.fieldKeys.map { createVertexPropertyProto(it) }

        val compositeVertexIndex = CompositeVertexIndex.newBuilder()
            .setName(graphIndex.name())
            .addAllProperties(properties)
            .setUnique(graphIndex.isUnique)
            .build()
        management.commit()
        return compositeVertexIndex
    }

    private fun getVertexLabelTx(
        tx: StandardJanusGraphTx,
        label: VertexLabel
    ): org.janusgraph.core.VertexLabel? =
        if (label.hasId()) {
            getVertexLabels(tx).firstOrNull { it.longId() == label.id.value }
                ?: throw IllegalArgumentException("No vertexLabel found with id")
        } else {
            tx.getVertexLabel(label.name)
        }

    override fun getCompositeIndicesByVertexLabel(
        graph: StandardJanusGraph,
        requestLabel: VertexLabel
    ): List<CompositeVertexIndex> {
        val tx = graph.buildTransaction().disableBatchLoading().start() as StandardJanusGraphTx
        val label = getVertexLabelTx(tx, requestLabel)
        val graphIndexes = getGraphIndices(tx, Vertex::class.java)
        val indices = graphIndexes
            .filterIsInstance<CompositeIndexType>()
            .filter { it.schemaTypeConstraint == label }
            .map {
                CompositeVertexIndex.newBuilder()
                    .setName(it.name)
                    .addAllProperties(it.fieldKeys.map { property -> createVertexPropertyProto(property.fieldKey) })
                    .setUnique(it.cardinality == Cardinality.SINGLE)
                    .build()
            }
        tx.rollback()
        return indices
    }

    override fun ensureMixedIndexByVertexLabel(
        management: JanusGraphManagement,
        requestLabel: VertexLabel,
        requestIndex: MixedVertexIndex
    ): MixedVertexIndex? {
        val label = getVertexLabel(management, requestLabel) ?: throw NullPointerException("vertex should exists")

        val keys = requestIndex.propertiesList.map { management.getPropertyKey(it.name) }
        val builder = management.buildIndex(requestIndex.name, Vertex::class.java)
            .indexOnly(label)

        keys.forEach { builder.addKey(it) }

        val graphIndex = builder.buildMixedIndex(requestIndex.backend)
        val properties = graphIndex.fieldKeys.map { createVertexPropertyProto(it) }

        val compositeVertexIndex = MixedVertexIndex.newBuilder()
            .setName(graphIndex.name())
            .addAllProperties(properties)
            .setBackend(graphIndex.backingIndex)
            .build()
        management.commit()
        return compositeVertexIndex
    }

    override fun getMixedIndicesByVertexLabel(
        graph: StandardJanusGraph,
        requestLabel: VertexLabel
    ): List<MixedVertexIndex> {
        val tx = graph.buildTransaction().disableBatchLoading().start() as StandardJanusGraphTx
        val label = getVertexLabelTx(tx, requestLabel)
        val graphIndexes = getGraphIndices(tx, Vertex::class.java)
        val indices = graphIndexes
            .filterIsInstance<MixedIndexType>()
            .filter { it.schemaTypeConstraint == label }
            .map {
                MixedVertexIndex.newBuilder()
                    .setName(it.name)
                    .setBackend(it.backingIndexName)
                    .addAllProperties(it.fieldKeys.map { property -> createVertexPropertyProto(property.fieldKey) })
                    .build()
            }
        tx.rollback()
        return indices
    }
}

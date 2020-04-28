package org.janusgraph.grpc.server

import com.google.protobuf.Int64Value
import org.janusgraph.core.Cardinality
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.attribute.Geoshape
import org.janusgraph.grpc.*
import java.util.*


internal fun convertDataTypeToJavaClass(dataType: PropertyDataType): Class<*> =
    when (dataType) {
        PropertyDataType.String -> java.lang.String::class.java
        PropertyDataType.Character -> java.lang.Character::class.java
        PropertyDataType.Boolean -> java.lang.Boolean::class.java
        PropertyDataType.Int8 -> java.lang.Byte::class.java
        PropertyDataType.Int16 -> java.lang.Short::class.java
        PropertyDataType.Int32 -> java.lang.Integer::class.java
        PropertyDataType.Int64 -> java.lang.Long::class.java
        PropertyDataType.Float32 -> java.lang.Float::class.java
        PropertyDataType.Float64 -> java.lang.Double::class.java
        PropertyDataType.Date -> Date::class.java
        PropertyDataType.JavaObject -> Any::class.java
        PropertyDataType.GeoShape -> Geoshape::class.java
        PropertyDataType.Uuid -> UUID::class.java
        PropertyDataType.UNRECOGNIZED -> TODO()
    }

internal fun convertJavaClassToDataType(dataType: Class<*>): PropertyDataType? =
    when (dataType) {
        java.lang.String::class.java -> PropertyDataType.String
        java.lang.Character::class.java -> PropertyDataType.Character
        java.lang.Boolean::class.java -> PropertyDataType.Boolean
        java.lang.Byte::class.java -> PropertyDataType.Int8
        java.lang.Short::class.java -> PropertyDataType.Int16
        java.lang.Integer::class.java -> PropertyDataType.Int32
        java.lang.Long::class.java -> PropertyDataType.Int64
        java.lang.Float::class.java -> PropertyDataType.Float32
        java.lang.Double::class.java -> PropertyDataType.Float64
        Date::class.java -> PropertyDataType.Date
        Any::class.java -> PropertyDataType.JavaObject
        Geoshape::class.java -> PropertyDataType.GeoShape
        UUID::class.java -> PropertyDataType.Uuid
        else -> PropertyDataType.UNRECOGNIZED
    }

internal fun convertCardinalityToJavaClass(cardinality: VertexProperty.Cardinality): Cardinality =
    when (cardinality) {
        VertexProperty.Cardinality.Single -> Cardinality.SINGLE
        VertexProperty.Cardinality.List -> Cardinality.LIST
        VertexProperty.Cardinality.Set -> Cardinality.SET
        VertexProperty.Cardinality.UNRECOGNIZED -> TODO()
    }

internal fun convertJavaClassToCardinality(cardinality: Cardinality): VertexProperty.Cardinality? {
    return when (cardinality) {
        Cardinality.SINGLE -> VertexProperty.Cardinality.Single
        Cardinality.LIST -> VertexProperty.Cardinality.List
        Cardinality.SET -> VertexProperty.Cardinality.Set
    }
}

internal fun createVertexPropertyProto(property: PropertyKey): VertexProperty =
    VertexProperty.newBuilder()
        .setName(property.name())
        .setDataType(convertJavaClassToDataType(property.dataType()))
        .setCardinality(convertJavaClassToCardinality(property.cardinality()))
        .build()

internal fun createVertexLabelProto(vertexLabel: org.janusgraph.core.VertexLabel, properties: List<PropertyKey>) =
    VertexLabel.newBuilder()
        .setId(Int64Value.of(vertexLabel.longId()))
        .setName(vertexLabel.name())
        .addAllProperties(properties.map { createVertexPropertyProto(it) })
        .setPartitioned(vertexLabel.isPartitioned)
        .setReadOnly(vertexLabel.isStatic)
        .build()

internal fun createEdgePropertyProto(property: PropertyKey): EdgeProperty =
    EdgeProperty.newBuilder()
        .setName(property.name())
        .setDataType(convertJavaClassToDataType(property.dataType()))
        .build()

internal fun createEdgeLabelProto(edgeLabel: org.janusgraph.core.EdgeLabel, properties: List<PropertyKey>) =
    EdgeLabel.newBuilder()
        .setId(Int64Value.of(edgeLabel.longId()))
        .setName(edgeLabel.name())
        .addAllProperties(properties.map { createEdgePropertyProto(it) })
        .build()

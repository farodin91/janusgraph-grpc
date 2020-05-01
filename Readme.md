# Potential Replacement of JanusGraph ManagementSystem

## How to use this Project?

1. Download [JanusGraph 0.5.1](https://github.com/JanusGraph/janusgraph/releases/download/v0.5.1/janusgraph-0.5.1.zip) 
2. Unpack `janusgraph-0.5.1.zip`
3. Run ./gradlew build
5. Unpack `build/distributions/janusgraph-grpc-1.0-SNAPSHOT.zip`
6. Replace/add all files from `janusgraph-grpc-1.0-SNAPSHOT.zip` into the unpacked files from `janusgraph-0.5.1.zip`
7. Execute ./bin/janusgraph-server.sh
8. Now you are ready to go (You have a running janusgraph instance with a gRPC server on port 10182 and a gremlin server on port 8128 as normal.)
9. ToDo: You can use any gRPC client

## Current State of a test Client

Not started yet.

## Current State of the Server

### Vertices

* [x] Get Vertex
    * [x] id
    * [x] name
    * [x] readonly -> static
    * [x] partitioned
    * [ ] edges
    * [x] properties
        * [x] name
        * [x] dataType
        * [ ] cardinality
        * [ ] multiple properties
    * [ ] composite indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
    * [ ] mixed indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
        * [ ] backend
* [x] Get Vertices
    * [x] id
    * [x] name
    * [x] readonly -> static
    * [x] partitioned
    * [ ] edges
    * [x] properties
        * [x] name
        * [ ] dataType
        * [ ] cardinality
        * [ ] multiple properties
    * [ ] composite indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
    * [ ] mixed indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
        * [ ] backend
* [x] Ensure Vertices (create)
    * [x] name
    * [x] readonly -> static
    * [x] partitioned
    * [ ] edges
    * [x] properties
        * [x] name
        * [x] dataType
        * [ ] cardinality
        * [ ] multiple properties
    * [ ] composite indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
    * [ ] mixed indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
        * [ ] backend
* [x] Ensure Vertices (update)
    * [x] id
    * [x] name
    * [ ] partitioned
    * [ ] edges
    * [ ] properties
        * [ ] name
        * [ ] dataType
        * [ ] cardinality
        * [ ] multiple properties
    * [ ] composite indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
    * [ ] mixed indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
        * [ ] backend

### Edges

* [x] Get Edge
    * [x] id
    * [x] name
    * [ ] direction
    * [ ] multiplicity
    * [x] properties
        * [x] name
        * [x] dataType
        * [ ] multiple properties
    * [ ] composite indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
    * [ ] mixed indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
        * [ ] backend
* [x] Get Edges
    * [x] id
    * [x] name
    * [ ] direction
    * [ ] multiplicity
    * [x] properties
        * [x] name
        * [ ] dataType
        * [ ] multiple properties
    * [ ] composite indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
    * [ ] mixed indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
        * [ ] backend
* [x] Ensure Edges (create)
    * [x] name
    * [ ] direction
    * [ ] multiplicity
    * [x] properties
        * [x] name
        * [x] dataType
        * [ ] multiple properties
    * [ ] composite indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
    * [ ] mixed indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
        * [ ] backend
* [x] Ensure Edges (update)
    * [x] name
    * [ ] direction
    * [ ] multiplicity
    * [ ] properties
        * [ ] name
        * [ ] dataType
        * [ ] multiple properties
    * [ ] composite indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
    * [ ] mixed indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] unique
        * [ ] backend

### Indices

* [ ] Get Relation Indices
* [ ] Ensure Relation Indices (create)
* [ ] Ensure Relation Indices (update)
* [ ] Index Repair's

### Instances

* [ ] Management Instances (status, config, close)
* [ ] global Configuration

### Integrations 

* [ ] Distributed tests 
    * [ ] vertexLabel changes
    * [ ] edgeLabel changes
    * [ ] propertyKey changes
    * [ ] index changes
* [x] DefaultJanusGraphManager as ContextManager
* [ ] DefaultJanusGraphManager with ConfigureGraphFactory
* [x] Default values gremlin server settings
* [x] Grpc server on start JanusGraphServer

### Unsorted

* [ ] ConsistencyModifier

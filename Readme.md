# Potential Replacement of JanusGraph ManagementSystem

## How to use this Project?

1. Download [JanusGraph 0.5.1](https://github.com/JanusGraph/janusgraph/releases/download/v0.5.1/janusgraph-0.5.1.zip) 
2. Unpack `janusgraph-0.5.1.zip`
3. Run `./gradlew build`
5. Unpack `build/distributions/janusgraph-grpc-1.0-SNAPSHOT.zip`
6. Replace/add all files from `janusgraph-grpc-1.0-SNAPSHOT.zip` into the unpacked files from `janusgraph-0.5.1.zip`
7. Remove `./lib/protobuf-java-2.5.0.jar`
7. Execute `./bin/janusgraph-server.sh`
8. Now you are ready to go (You have a running janusgraph instance with a gRPC server on port 10182 and a gremlin server on port 8128 as normal.)
9. Client test: `cd client && cargo run -- --host 127.0.0.1 vertex-label list`(client just can list vertexlabels)

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
        * [x] cardinality
        * [x] multiple properties
    * [x] composite indices
        * [x] name
        * [x] properties
        * [x] unique
        * [x] multiple properties
        * [ ] multiple indices
        * [ ] status
    * [x] mixed indices
        * [x] name
        * [x] backend
        * [x] properties
        * [x] multiple properties
        * [ ] multiple indices
        * [ ] status
* [x] Get Vertices
    * [x] id
    * [x] name
    * [x] readonly -> static
    * [x] partitioned
    * [ ] edges
    * [x] properties
        * [x] name
        * [x] dataType
        * [x] cardinality
        * [x] multiple properties
* [x] Ensure Vertices (create)
    * [x] name
    * [x] readonly -> static
    * [x] partitioned
    * [ ] edges
    * [x] properties
        * [x] name
        * [x] dataType
        * [x] cardinality
        * [x] multiple properties
    * [x] composite indices
        * [x] name
        * [x] properties
        * [x] unique
        * [x] multiple properties
        * [ ] ensure properties are part of vertex
        * [ ] multiple indices
    * [x] mixed indices
        * [x] name
        * [x] backend
        * [x] properties
        * [x] multiple properties
        * [ ] ensure properties are part of vertex
        * [ ] multiple indices
* [x] Ensure Vertices (update)
    * [x] id
    * [x] name
    * [ ] partitioned
    * [ ] edges
    * [x] properties
        * [ ] name
        * [ ] dataType
        * [ ] cardinality
        * [ ] multiple properties
    * [ ] composite indices
        * [ ] name
        * [ ] properties
        * [ ] multiple indices
        * [ ] multiple properties
        * [ ] unique
    * [ ] mixed indices
        * [ ] name
        * [ ] backend
        * [ ] properties
        * [ ] multiple indices
        * [ ] multiple properties

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
    * [x] composite indices
        * [x] name
        * [x] properties
        * [x] multiple properties
        * [ ] multiple indices
    * [x] mixed indices
        * [x] name
        * [x] backend
        * [x] properties
        * [x] multiple properties
        * [ ] multiple indices
* [x] Get Edges
    * [x] id
    * [x] name
    * [ ] direction
    * [ ] multiplicity
    * [x] properties
        * [x] name
        * [x] dataType
        * [x] multiple properties
* [x] Ensure Edges (create)
    * [x] name
    * [ ] direction
    * [ ] multiplicity
    * [x] properties
        * [x] name
        * [x] dataType
        * [x] multiple properties
    * [x] composite indices
        * [x] name
        * [x] properties
        * [x] multiple properties
        * [ ] ensure properties are part of vertex
        * [ ] multiple indices
    * [x] mixed indices
        * [x] name
        * [x] backend
        * [x] properties
        * [x] multiple properties
        * [ ] ensure properties are part of vertex
        * [ ] multiple indices
* [x] Ensure Edges (update)
    * [x] name
    * [ ] direction
    * [ ] multiplicity
    * [x] properties
        * [ ] name
        * [ ] dataType
        * [ ] multiple properties
    * [ ] composite indices
        * [ ] name
        * [ ] properties
        * [ ] multiple properties
        * [ ] ensure properties are part of vertex
        * [ ] multiple indices
    * [ ] mixed indices
        * [ ] name
        * [ ] properties
        * [ ] multiple properties
        * [ ] ensure properties are part of vertex
        * [ ] multiple indices
        * [ ] backend

### Indices

* [ ] Get Relation Indices
* [ ] Ensure Relation Indices (create)
* [ ] Ensure Relation Indices (update)
* [ ] Index Repair's

### Instances

* [ ] Management Instances (status, config, close)
* [ ] Global Configuration
* [x] Get a list of contexts
    * [x] name
    * [ ] basic infos (storage backend, index backend, host, configuredGraphFactory)

### Integrations 

* [ ] Distributed tests 
    * [ ] vertexLabel changes
    * [ ] edgeLabel changes
    * [ ] propertyKey changes
    * [ ] index changes
* [x] DefaultJanusGraphManager as ContextManager
    * [ ] tests
* [ ] DefaultJanusGraphManager with ConfigureGraphFactory
    * [ ] tests
* [x] Default values gremlin server settings
    * [ ] tests
* [x] Grpc server on start JanusGraphServer
    * [ ] tests
* [ ] logging

### Unsorted

* [ ] ConsistencyModifier

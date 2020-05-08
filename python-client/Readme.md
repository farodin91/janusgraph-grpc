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

## Current State of a Python Client


### Vertices

* [x] Get Vertex
    * [x] object
    * [x] id
    * [x] name
    * [ ] readonly -> static
    * [ ] partitioned
    * [ ] edges
    * [ ] properties
        * [ ] name
        * [ ] dataType
        * [ ] cardinality
        * [ ] multiple properties
    * [x] composite indices
        * [x] object
        * [x] name
        * [ ] properties
        * [ ] unique
        * [ ] multiple properties
        * [ ] multiple indices
        * [ ] status
    * [ ] mixed indices
        * [ ] name
        * [ ] backend
        * [ ] properties
        * [ ] multiple properties
        * [ ] multiple indices
        * [ ] status
* [x] Get Vertices
    * [x] object
    * [x] id
    * [x] name
    * [ ] readonly -> static
    * [ ] partitioned
    * [ ] edges
    * [ ] properties
        * [ ] name
        * [ ] dataType
        * [ ] cardinality
        * [ ] multiple properties
    * [x] composite indices
        * [x] object
        * [x] name
        * [ ] properties
        * [ ] unique
        * [ ] multiple properties
        * [ ] multiple indices
        * [ ] status
    * [ ] mixed indices
        * [ ] name
        * [ ] backend
        * [ ] properties
        * [ ] multiple properties
        * [ ] multiple indices
        * [ ] status
* [ ] Ensure Vertices (create)
    * [ ] name
    * [ ] readonly -> static
    * [ ] partitioned
    * [ ] edges
    * [ ] properties
        * [ ] name
        * [ ] dataType
        * [ ] cardinality
        * [ ] multiple properties
    * [x] composite indices
        * [x] name
        * [x] properties
        * [x] unique
        * [x] multiple properties
        * [ ] multiple labels
        * [ ] ensure properties are part of vertex
        * [ ] multiple indices
    * [ ] mixed indices
        * [ ] name
        * [ ] backend
        * [ ] properties
        * [ ] multiple properties
        * [ ] ensure properties are part of vertex
        * [ ] multiple indices
* [ ] Ensure Vertices (update)
    * [ ] id
    * [ ] name
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
    * [x] object
    * [x] id
    * [x] name
    * [ ] direction
    * [ ] multiplicity
    * [ ] properties
        * [ ] name
        * [ ] dataType
        * [ ] multiple properties
    * [x] composite indices
        * [x] name
        * [ ] properties
        * [ ] multiple properties
        * [ ] multiple indices
    * [ ] mixed indices
        * [ ] name
        * [ ] backend
        * [ ] properties
        * [ ] multiple properties
        * [ ] multiple indices
* [x] Get Edges
    * [x] object
    * [x] id
    * [x] name
    * [ ] direction
    * [ ] multiplicity
    * [ ] properties
        * [ ] name
        * [ ] dataType
        * [ ] multiple properties
    * [x] composite indices
        * [x] object
        * [x] name
        * [ ] properties
        * [ ] unique
        * [ ] multiple properties
        * [ ] multiple indices
        * [ ] status
    * [ ] mixed indices
        * [ ] name
        * [ ] backend
        * [ ] properties
        * [ ] multiple properties
        * [ ] multiple indices
        * [ ] status
* [ ] Ensure Edges (create)
    * [ ] name
    * [ ] direction
    * [ ] multiplicity
    * [ ] properties
        * [ ] name
        * [ ] dataType
        * [ ] multiple properties
    * [x] composite indices
        * [x] name
        * [x] properties
        * [x] multiple properties
        * [ ] multiple labels
        * [ ] ensure properties are part of vertex
        * [ ] multiple indices
    * [ ] mixed indices
        * [ ] name
        * [ ] backend
        * [ ] properties
        * [ ] multiple properties
        * [ ] ensure properties are part of vertex
        * [ ] multiple indices
* [ ] Ensure Edges (update)
    * [ ] name
    * [ ] direction
    * [ ] multiplicity
    * [ ] properties
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


## Current State of the Server

Please refer to README of parent project

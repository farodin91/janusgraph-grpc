# Potential Replacement of JanusGraph ManagementSystem


# Current State
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
* [ ] Get Indices
    * vertex centric index
        * composite
        * mixed
    * edge centric index
        * composite
        * mixed
* [ ] Ensure Indices (create)
    * vertex centric index
        * composite
        * mixed
    * edge centric index
        * composite
        * mixed
* [ ] Ensure Indices (update)
    * vertex centric index
        * composite
        * mixed
    * edge centric index
        * composite
        * mixed
* [ ] Index Repair's
* [ ] Management Instances (status, config, close)
* [ ] global Configuration
* [ ] ConsistencyModifier
* [ ] Distributed tests 
    * [ ] vertexLabel changes
    * [ ] edgeLabel changes
    * [ ] propertyKey changes
    * [ ] index changes
* [ ] DefaultJanusGraphManager with Grpc server
* [ ] ContextManager should support ConfigureGraphFactory

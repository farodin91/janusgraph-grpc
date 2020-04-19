# Potential replacement of The JanusGraph ManagementSystem


# Current State
* [x] Get Vertices
    * [x] id
    * [x] name
    * [ ] readonly -> static
    * [ ] partitioned
    * [ ] edges
    * [x] properties
        * [x] name
        * [ ] cardinality
* [x] Ensure Vertices (create)
    * [x] name
    * [ ] readonly -> static
    * [ ] partitioned
    * [ ] edges
    * [x] properties
        * [x] name
        * [ ] cardinality
* [x] Ensure Vertices (update)
    * [x] id
    * [x] name
    * [ ] partitioned
    * [ ] edges
    * [ ] properties
        * [ ] name
        * [ ] cardinality
* [x] Get Edges
    * [x] id
    * [x] name
    * [ ] direction
    * [ ] multiplicity
    * [x] properties
        * [x] name
        * [ ] cardinality
* [x] Ensure Edges (create)
    * [x] name
    * [ ] direction
    * [ ] multiplicity
    * [x] properties
        * [x] name
        * [ ] cardinality
* [x] Ensure Edges (update)
    * [x] name
    * [ ] direction
    * [ ] multiplicity
    * [ ] properties
        * [ ] name
        * [ ] cardinality
* [ ] Get Indices
    * graph index
        * composite
        * mixed
    * vertex centric index
        * composite
        * mixed
    * edge centric index
        * composite
        * mixed
* [ ] Ensure Indices (create)
    * graph index
        * composite
        * mixed
    * vertex centric index
        * composite
        * mixed
    * edge centric index
        * composite
        * mixed
* [ ] Ensure Indices (update) for example: name
    * graph index
        * composite
        * mixed
    * vertex centric index
        * composite
        * mixed
    * edge centric index
        * composite
        * mixed
* [ ] Index Repair's
* [ ] Management Instances (status, config, close)
* [ ] global Configuration
* [ ] Distributed tests 
    * [ ] vertexLabel changes
    * [ ] edgeLabel changes
    * [ ] propertyKey changes
    * [ ] index changes
# Potential Replacement of JanusGraph ManagementSystem

## How to use this Project?

For Building the Server and how to use it, please refer to [Parent Readme](../Readme.md)

1. Tested with Anaconda3. If not present, download and install it.
2. Create a Conda env for Python 3.6 `conda create -n my_env python=3.6`
3. Activate the environment `conda activate my_env`
4. Install dependencies, `python -m pip install -r requirements.txt`
5. Generate gRPC Stubs and Classes from `management.porto` file as follows:
    
    5.1. `cd python-client`
    
    5.2. `python -m grpc_tools.protoc -I src/main/proto/ --python_out=src/main/python/janusgraph_grpc_python/management/ --grpc_python_out=src/main/python/janusgraph_grpc_python/management/ src/main/proto/management.proto`
    
    5.3. The above command generated 2 files named `management_pb2.py` and `management_pb2_grpc.py` under `src/main/python/janusgarph_grpc_python/management`
    
    5.4. Edit the file `src/main/python/janusgraph_grpc_python/management_pb2_grpc.py` (Import statement) from `import management_pb2 as management__pb2` to `from . import management_pb2 as management__pb2`
    
6. Build the client now. From parent folder `python-client` run command `pyb` to build the client.
7. Once the Client is build, its time now to test the client. Move to client package `cd src/main/python/janusgraph_grpc_python/client` . Some sample commands are listed bellow:
8. NOTE: The Client works against BerkleyDB Graph backend [HardCoded] which is configured in `../dist/conf/gremlin-server.yaml` and `../dist/scripts/multi-graph.groovy` which will be exposed once you run `janusgraph-server.sh`

    8.1. Get Vertex ALL: `python management_client.py --host localhost --op GET --arg VertexLabel ALL`
    
    8.2. Get Vertex by name: `python management_client.py --host localhost --op GET --arg VertexLabel god`
    
    8.3. Create Vertex by name (Adding new default properties and making it constrained): `python management_client.py --host localhost --op PUT --arg VertexLabel newVertex properties=prop1,prop2 partitioned=true readOnly=false`
    
    8.4. Create Vertex by name with all defaults: `python management_client.py --host localhost --op PUT --arg VertexLabel newVertex1`
    
    8.5. NO SUPPORT YET FOR DEFINING **dataType** **cardinality** while creating new properties and constraint it to a VertexLabel
    
    8.6. Adding only properties constraints: `python management_client.py --host localhost --op PUT --arg VertexLabel newVertex2 properties=prop3,prop4`
    
    8.7. Adding Composite Index to Vertex restricted to vertexLabel specified: `python management_client.py --host localhost --op PUT --arg VertexLabel newVertex INDEX index_type=CompositeIndex index_on=prop1,prop2 index_name=byMultiPropComposite`
    
    8.8. Adding Composite Index to all Vertex: `python management_client.py --host localhost --op PUT --arg VertexLabel ALL INDEX index_type=CompositeIndex index_on=prop1,prop2 index_name=byMultiPropComposite1`
    
    8.9. Get all Composite Index for Vertex: `python management_client.py --host localhost --op GET --arg VertexLabel ALL INDEX index_type=CompositeIndex`
    
9. Similarly above mentioned steps can be done for `EdgeLabel` also, only thing needed to change will be:

    9.1. When doing `PUT` on `EdgeLabel` we don't use `readOnly` and `partitioned`. 
    
    9.2. Sample Usage: `python management_client.py --host localhost --op PUT --arg EdgeLabel newEdge properties=p1,p2 multiplicity=Many2One directed=true`
    
    
## TODO

1. Generalize the usage of Index and Properties. Like getProperties, makeProperties, getIndex and makeIndex. They use deep nesting structure for now. We may need to create new elements like `VertexLabel` and `EdgeLbael` for those
2. Take a call on how to expose the above APIs. We need to encapsulate with `janusgraph-python` so create a `Management Instance` within it?
3. Write unittest and integration tests

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
* [x] Ensure Vertices (create)
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

For status of Server please refer to [Parent Project Readme](../Readme.md)

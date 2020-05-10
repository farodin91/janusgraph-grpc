import random
import logging

import grpc

import grpc._channel as ch
from collections.abc import Iterable
# import route_guide_pb2
# import route_guide_pb2_grpc
# import route_guide_resources
from typing import Union, List
import argparse
from enum import Enum
from operator import itemgetter

import janusgraph_grpc_python.management.management_pb2 as management_pb2
import janusgraph_grpc_python.management.management_pb2_grpc as management_pb2_grpc


class GraphIndexer:
    supported_parameters = ["index_type", "index_name", "index_on", "index_only", "unique_index"]

    CONTEXT = None
    SERVICE = None
    ELEMENT = None

    GET_OPERATION_PARAMS = ["index_type"]

    def __init__(self, **kwargs):
        # Defaults for an Index
        self.index_type = None
        self.index_name = None
        self.index_on = None
        self.element_to_index = None
        self.index_only = False
        self.unique_index = False

        self.__are_valid_parameters_passed__(**kwargs)

        for property_name, property_value in kwargs.items():
            setattr(self, property_name, property_value)

    def set_element(self, element):
        """

        Args:
            element (GraphElement):

        Returns:

        """
        self.ELEMENT = element

        if isinstance(element, management_pb2.VertexLabel):
            self.element_to_index = "VertexLabel"
        elif isinstance(element, management_pb2.EdgeLabel):
            self.element_to_index = "EdgeLabel"
        else:
            raise ValueError("Invalid element accessed in setter() method. Expecting class to be "
                             "EdgeLabel or VertexLabel for " + str(type(element)))

    def set_context(self, context):
        self.CONTEXT = context

    def set_service(self, stub):
        self.SERVICE = stub

    # def set_channel(self, channel):
    #     self.CHANNEL = channel

    def get_indexer(self):

        # print("========= I'm inside get_indexer() ==========")
        # print(self.supported_parameters)
        # print(self.__dict__)
        # print(self.__dict__.keys())
        # print(self.__dict__.values())
        # print(self.ELEMENT)
        # print(self.element_to_index)
        # print(self.ELEMENT.name)
        # print({k: self.__dict__.get(k) for k in self.supported_parameters})
        # print("========= I'm inside get_indexer() ==========")

        if self.element_to_index is None:
            raise ValueError("Please call set_element() to identify the element to be Indexed before calling get_indexer()")

        if self.index_type == "CompositeIndex":
            idx = CompositeIndex(**{k: self.__dict__.get(k) for k in self.supported_parameters})
        elif self.index_type == "MixedIndex":
            idx = MixedIndex(**{k: self.__dict__.get(k) for k in self.supported_parameters})
        else:
            raise AttributeError(f"Invalid index type defined | {self.index_type} |. "
                                 f"Expecting either CompositeIndex or MixedIndex")

        # if self.OPERATION is None:
        #     raise ValueError("Please call set_operation() before get_indexer()")

        if self.CONTEXT is None:
            raise ValueError("Please call set_context() before calling get_indexer()")

        if self.SERVICE is None:
            raise ValueError("Please call set_service() before calling get_indexer()")

        # if self.CHANNEL is None:
        #     raise ValueError("Please call set_channel() before calling get_indexer()")

        # idx.set_operation(self.OPERATION)
        idx.set_context(self.CONTEXT)
        idx.set_service(self.SERVICE)
        idx.set_element(self.ELEMENT)
        # idx.set_channel(self.CHANNEL)
        return idx

    def put_index(self):
        raise NotImplementedError(f"{str(self)} is not subclassed by any other class yet so no put_index() implemented")

    def get_indices_by_label(self):
        raise NotImplementedError(f"{str(self)} is not subclassed by any other class yet so no get_index() implemented")

    def get_all_indices(self):
        raise NotImplementedError(f"{str(self)} is not subclassed by any other class yet so no get_index() implemented")

    def __are_valid_parameters_passed__(self, **kwargs):
        valid_params_passed = all([x in self.supported_parameters for x in kwargs.keys()])

        if not valid_params_passed:
            invalid_param_idx = [i for i, x in enumerate([x in self.supported_parameters
                                                          for x in kwargs.keys()]) if x is False]
            invalid_params = itemgetter(*invalid_param_idx)(kwargs.keys())

            raise LookupError(f"Invalid parameter passed. The passed parameter {invalid_params} "
                              f"is not part of supported parameter list ${self.supported_parameters}")

        if isinstance(self.index_on, dict):
            raise NotImplementedError("Implemented index_on with String attribute only. "
                    f"TODO for dict with key as propertyKey and value as Mapping parameter. Got {type(self.index_on)}")

    def __are_required_parameters_set__(self, parameters=None):
        # The compulsory parameters are the ones which are initialized as either None or as empty object
        # Optional parameters are already defaulted to a value other than None or Empty object
        if parameters is None:
            parameters = self.supported_parameters

        print("Verifying validity of params intialized")
        print(parameters)
        print("====================")

        for parameter in parameters:
            if getattr(self, parameter) is None:
                raise AttributeError(f"{parameter} needs to be defined while initializing class. Got None")
            else:
                if isinstance(getattr(self, parameter), Iterable) and len(getattr(self, parameter)) == 0:
                    raise AttributeError(f"{parameter} length is 0. At-least one property needs to be "
                                         "defined while initializing class.")


class CompositeIndex(GraphIndexer):
    REQUEST = None

    def __init__(self, **kwargs):
        super().__init__(**kwargs)

    def __str__(self):
        return "CompositeIndex"

    def create_get_indices_by_name_request(self):
        if str(self.element_to_index) == "VertexLabel":
            self.REQUEST = management_pb2.GetCompositeIndicesByVertexLabelRequest(context=self.CONTEXT, vertexLabel=self.ELEMENT)
        elif str(self.element_to_index) == "EdgeLabel":
            self.REQUEST = management_pb2.GetCompositeIndicesByEdgeLabelRequest(context=self.CONTEXT, edgeLabel=self.ELEMENT)
        else:
            raise ValueError(f"Invalid element_to_index parameter. "
                             f"Expecting VertexLabel/EdgeLabel for {str(self.element_to_index)}")
        return self

    def create_get_all_indices_request(self):
        if str(self.element_to_index) == "VertexLabel":
            self.REQUEST = management_pb2.GetCompositeIndicesForVertexRequest(context=self.CONTEXT)
        elif str(self.element_to_index) == "EdgeLabel":
            self.REQUEST = management_pb2.GetCompositeIndicesForEdgeRequest(context=self.CONTEXT)
        else:
            raise ValueError(f"Invalid element_to_index parameter. "
                             f"Expecting VertexLabel/EdgeLabel for {str(self.element_to_index)}")
        return self

    def __generate_vertex_properties__(self):
        vertex_properties = []
        for elem in self.index_on:
            vertex_properties.append(management_pb2.VertexProperty(name=elem))
        return vertex_properties

    def __generate_edge_properties__(self):
        edge_properties = []
        for elem in self.index_on:
            edge_properties.append(management_pb2.EdgeProperty(name=elem))
        return edge_properties

    def create_put_index_request(self):
        if str(self.element_to_index) == "VertexLabel":
            vp = self.__generate_vertex_properties__()
            index = management_pb2.CompositeVertexIndex(name=self.index_name, properties=vp, unique=self.unique_index)
            self.REQUEST = management_pb2.EnsureCompositeIndexByVertexLabelRequest(context=self.CONTEXT, vertexLabel=self.ELEMENT, index=index)

        elif str(self.element_to_index) == "EdgeLabel":
            ep = self.__generate_edge_properties__()
            index = management_pb2.CompositeEdgeIndex(name=self.index_name, properties=ep, unique=self.unique_index)
            self.REQUEST = management_pb2.EnsureCompositeIndexByEdgeLabelRequest(context=self.CONTEXT, edgeLabel=self.ELEMENT, index=index)

        else:
            raise ValueError(f"Invalid element_to_index parameter. "
                             f"Expecting VertexLabel/EdgeLabel for {str(self.element_to_index)}")
        return self

    def put_index(self):
        self.__are_required_parameters_set__()

        self.create_put_index_request()

        if self.element_to_index == "VertexLabel":
            return self.SERVICE.EnsureCompositeIndexByVertexLabel(self.REQUEST)
        elif self.element_to_index == "EdgeLabel":
            return self.SERVICE.EnsureCompositeIndexByEdgeLabel(self.REQUEST)
        else:
            raise ValueError(f"Invalid element_to_index parameter. "
                             f"Expecting VertexLabel/EdgeLabel for {self.element_to_index}")

    def get_indices_by_label(self):
        self.__are_required_parameters_set__(self.GET_OPERATION_PARAMS)

        self.create_get_indices_by_name_request()

        if self.element_to_index == "VertexLabel":
            return self.SERVICE.GetCompositeIndicesByVertexLabel(self.REQUEST)
        elif self.element_to_index == "EdgeLabel":
            return self.SERVICE.GetCompositeIndicesByEdgeLabel(self.REQUEST)
        else:
            raise ValueError(f"Invalid element_to_index parameter. "
                             f"Expecting VertexLabel/EdgeLabel for {self.element_to_index}")

    def get_all_indices(self):
        self.__are_required_parameters_set__(self.GET_OPERATION_PARAMS)

        self.create_get_all_indices_request()

        if self.element_to_index == "VertexLabel":
            return self.SERVICE.GetCompositeIndicesForVertex(self.REQUEST)
        elif self.element_to_index == "EdgeLabel":
            return self.SERVICE.GetCompositeIndicesForEdge(self.REQUEST)
        else:
            raise ValueError(f"Invalid element_to_index parameter. "
                             f"Expecting VertexLabel/EdgeLabel for {self.element_to_index}")


class MixedIndex(GraphIndexer):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)

    def __str__(self):
        return "MixedIndex"

    def create(self):
        self.index_type = str(self)
        return


class GraphOperationMetadata:
    RESERVED_KEYWORD = "INDEX"
    # Metadata can take the form or either ALL or INDEX (For Indexing purpose) or String which means
    # retrieval of Graph Element which can either be VertexLabel, EdgeLabel or Property

    # Metadata will be populated only during PUT requests.
    # It can be either PUT->VertexLabel/EdgeLabel/CompositeIndex/MixedIndex/RelationIndex

    INDEXER = None
    ADDER = None
    METADATA = None

    def __init__(self):
        pass

    def set(self, metadata):

        if metadata is not None:
            operation_type = metadata[0]

            if operation_type == self.RESERVED_KEYWORD:
                self.METADATA = {x.split("=")[0].strip():
                                    x.split("=")[1].strip().split(",")
                                        if len(x.split("=")[1].strip().split(",")) > 1
                                        else x.split("=")[1].strip().split(",")[0]
                                    for x in metadata[1:]}

                self.INDEXER = GraphIndexer(**self.METADATA)
            else:
                self.METADATA = {x.split("=")[0].strip():
                                     x.split("=")[1].strip().split(",")
                                         if len(x.split("=")[1].strip().split(",")) > 1
                                         else x.split("=")[1].strip().split(",")[0]
                                    for x in metadata}

                self.ADDER = GraphElementAdder().set(**self.METADATA)

        print(f"Metadata is after parsing: {self.METADATA}")

        return self

    def get_metadata(self):
        return self.METADATA

    def get_operator(self):
        if self.INDEXER is None:
            return self.ADDER
        else:
            return self.INDEXER


class GraphElementAdder:
    SINGLE = False
    ALL = False
    name = None

    def __init__(self):
        pass

    def set(self, retriever):
        if retriever == "ALL":
            self.ALL = True
        else:
            self.SINGLE = True
            self.name = retriever
        return self


class GraphOperationAction(argparse.Action):
    def __init__(self, *args, **kwargs):
        super(GraphOperationAction, self).__init__(*args, **kwargs)
        self.nargs = "*"

    def __call__(self, parser, namespace, values, option_string=None):
        """

        Args:
            parser (ArgumentParser):
            namespace (Namespace):
            values Union[_Text, Sequence[Any], None]:
            option_string (Optional[_Text]):

        Returns:

        """
        lst = getattr(namespace, self.dest, []) or []
        print("--------------")
        print(values)

        element_type, *element_name_and_metadata = values

        if len(element_name_and_metadata) == 1:
            element_name = element_name_and_metadata[0]
            element_metadata = None
        else:
            element_name = element_name_and_metadata[0]
            element_metadata = element_name_and_metadata[1:]

        print(element_type, element_name, element_metadata)
        print(element_metadata)
        print("--------------")

        lst.append(GraphOperation(GraphElementType().set(element_type), str(element_name),
                                  GraphOperationMetadata().set(element_metadata)))
        setattr(namespace, self.dest, lst)


class GraphElement(object):
    def __init__(self, element, operation, metadata, optional_metadata):
        self.element = element
        self.operation = operation
        self.metadata = metadata
        self.service = None

        self.OPTIONAL_METADATA = optional_metadata
        pass

    def get_element(self):
        raise NotImplementedError("Not implemented GraphElement without being subclassed and get_element() being called")

    def set_service(self, stub):
        self.service = stub

    def operate(self):

        if self.service is None:
            raise ValueError("Please call set_service(stub) before calling operate()")

        print("I'm operating on " + self.element + " with operation " + self.operation)

        if self.operation == "GET":
            return self.__get__()
        elif self.operation == "PUT":
            return self.__put__()
        else:
            raise NotImplementedError("Implemented only GET and PUT operations till now")

    def __get__(self):
        pass

    def __put__(self):
        return

    def __str__(self):
        print("I'm getting string representation of GraphElement and the element is " + self.element)
        return self.element


class Vertex(GraphElement):
    def __init__(self, operation, label, optional_metadata=None):
        super().__init__("VertexLabel", operation, label, optional_metadata)

        self.operation = operation
        self.element_label = label

        self.CONTEXT = None
        self.REQUEST = None
        self.ELEMENT = None
        self.OPTIONAL_OPERATOR = None
        self.OPTIONAL_METADATA = optional_metadata

        if self.element_label is not "ALL":
            self.ELEMENT = management_pb2.VertexLabel(name=self.element_label)

    def get_element(self):
        print(f"Getting the get_element() with value = {self.ELEMENT} and the class is {type(self.ELEMENT)}")
        return self.ELEMENT

    def set_optional_operator(self, addtnl_operator):
        self.OPTIONAL_OPERATOR = addtnl_operator

    def __generate_context__(self):
        self.CONTEXT = management_pb2.JanusGraphContext(graphName=GRAPH_NAME)
        return self

    def __generate_request__(self):
        if self.operation == "GET":
            if self.element_label == "ALL":
                self.REQUEST = management_pb2.GetVertexLabelsRequest(context=self.CONTEXT)
            else:
                self.REQUEST = management_pb2.GetVertexLabelsByNameRequest(context=self.CONTEXT, name=self.element_label)
        else:
            if self.element_label is not "ALL":
                self.REQUEST = management_pb2.EnsureVertexLabelRequest(context=self.CONTEXT, label=self.ELEMENT)
            else:
                raise NotImplementedError("Implemented PUT operation on VertexLabel when "
                                          "a vertexLabel name is provided not when ALL")
        return self

    def __get__(self):
        self.__generate_context__()
        self.__generate_request__()

        if self.OPTIONAL_METADATA is None:
            if self.element_label == "ALL":
                return self.service.GetVertexLabels(self.REQUEST)
            else:
                return self.service.GetVertexLabelsByName(self.REQUEST)
        else:
            self.OPTIONAL_OPERATOR.set_context(self.CONTEXT)

            if isinstance(self.OPTIONAL_OPERATOR, GraphIndexer):
                if self.element_label == "ALL":
                    indexer = self.OPTIONAL_OPERATOR.get_indexer()
                    return indexer.get_all_indices()

                else:
                    indexer = self.OPTIONAL_OPERATOR.get_indexer()
                    return indexer.get_indices_by_label()

            else:
                raise NotImplementedError("Not implemented GET method for GraphAdder instance. "
                                          "Because logically different")

    def __put__(self):
        self.__generate_context__()
        self.__generate_request__()
        if self.OPTIONAL_METADATA is None:
            return self.service.EnsureVertexLabel(self.REQUEST)
        else:
            self.OPTIONAL_OPERATOR.set_context(self.CONTEXT)

            if isinstance(self.OPTIONAL_OPERATOR, GraphIndexer):
                if self.element_label == "ALL":
                    raise NotImplementedError("Not yet implemented PUT operation on index with ALL VertexLabel. TODO")
                    pass
                else:
                    indexer = self.OPTIONAL_OPERATOR.get_indexer()

                    return indexer.put_index()
            else:
                raise NotImplementedError("Not yet implemented PUT method for GraphAdder instance in VertexLabel. "
                                          "--TODO--")


class Edge(GraphElement):
    def __init__(self, operation, label, optional_metadata=None):
        super().__init__("EdgeLabel", operation, label, optional_metadata)

        self.operation = operation
        self.element_label = label

        self.CONTEXT = None
        self.REQUEST = None
        self.ELEMENT = None
        self.OPTIONAL_OPERATOR = None
        self.OPTIONAL_METADATA = optional_metadata

        if self.element_label is not "ALL":
            self.ELEMENT = management_pb2.EdgeLabel(name=self.element_label)

    def get_element(self):
        return self.ELEMENT

    def set_optional_operator(self, addtnl_operator):
        self.OPTIONAL_OPERATOR = addtnl_operator

    def __generate_context__(self):
        self.CONTEXT = management_pb2.JanusGraphContext(graphName=GRAPH_NAME)
        return self

    def __generate_request__(self):
        if self.operation == "GET":
            if self.element_label == "ALL":
                self.REQUEST = management_pb2.GetEdgeLabelsRequest(context=self.CONTEXT)
            else:
                self.REQUEST = management_pb2.GetEdgeLabelsByNameRequest(context=self.CONTEXT, name=self.element_label)
        else:
            if self.element_label is not "ALL":
                self.REQUEST = management_pb2.EnsureEdgeLabelRequest(context=self.CONTEXT, label=self.ELEMENT)
            else:
                raise NotImplementedError("Implemented PUT operation on VertexLabel when "
                                          "a vertexLabel name is provided not when ALL")
        return self

    def __get__(self):
        self.__generate_context__()
        self.__generate_request__()

        if self.OPTIONAL_METADATA is None:
            if self.element_label == "ALL":
                return self.service.GetEdgeLabels(self.REQUEST)
            else:
                return self.service.GetEdgeLabelsByName(self.REQUEST)
        else:
            self.OPTIONAL_OPERATOR.set_context(self.CONTEXT)

            if isinstance(self.OPTIONAL_OPERATOR, GraphIndexer):
                if self.element_label == "ALL":
                    indexer = self.OPTIONAL_OPERATOR.get_indexer()
                    return indexer.get_all_indices()

                else:
                    indexer = self.OPTIONAL_OPERATOR.get_indexer()
                    return indexer.get_indices_by_label()

            else:
                raise NotImplementedError("Not implemented GET method for GraphAdder instance. "
                                          "Because logically different")

    def __put__(self):
        self.__generate_context__()
        self.__generate_request__()
        if self.OPTIONAL_METADATA is None:
            return self.service.EnsureEdgeLabel(self.REQUEST)
        else:
            self.OPTIONAL_OPERATOR.set_context(self.CONTEXT)

            if isinstance(self.OPTIONAL_OPERATOR, GraphIndexer):
                if self.element_label == "ALL":
                    raise NotImplementedError("Not yet implemented PUT operation on index with ALL EdgeLabel. TODO")
                    pass
                else:
                    indexer = self.OPTIONAL_OPERATOR.get_indexer()

                    return indexer.put_index()
            else:
                raise NotImplementedError("Not yet implemented PUT method for GraphAdder instance in EdgeLabel. "
                                          "--TODO--")


class Contexts(GraphElement):
    def __init__(self, operation, label, optional_metadata=None):
        super().__init__("ContextAction", operation, label, optional_metadata)

        self.operation = operation
        self.element_label = label

        self.context_name = GRAPH_NAME

        self.CONTEXT = None
        self.REQUEST = None
        self.ELEMENT = self.get_element()

    def __generate_context__(self):
        self.CONTEXT = management_pb2.JanusGraphContext(graphName=self.context_name) if self.ELEMENT is None else self.ELEMENT
        return self

    def __generate_request__(self):
        if self.operation == "GET":
            if self.element_label == "ALL":
                self.REQUEST = management_pb2.GetContextsRequest()
            else:
                self.REQUEST = management_pb2.GetContextByGraphNameRequest(name=self.element_label)
        else:
            raise ValueError("ContextAction can only be performed with GET operation")
        return self

    def get_element(self):
        return management_pb2.JanusGraphContext(graphName=self.context_name) if self.CONTEXT is None else self.CONTEXT

    def __get__(self):
        self.__generate_context__()
        self.__generate_request__()

        if self.metadata == "ALL":
            return self.service.GetContexts(self.REQUEST)
        else:
            return self.service.GetContextByGraphName(self.REQUEST)


class GraphElementType:

    command_types = ["VertexLabel", "EdgeLabel", "ContextAction"]

    def __init__(self):
        self.VertexLabel = None
        self.EdgeLabel = None
        self.ContextAction = None

    def __get__(self, name):
        if name in self.command_types and getattr(self, name) is not None:
            return getattr(self, name)
        else:
            if name not in self.command_types:
                raise NotImplementedError("Implemented only VertexLabel/EdgeLabel/ContextAction support "
                                          "for CommandType got " + name)
            else:
                raise KeyError(f"Command type initialized as something but not {name}")

    def get(self):
        """ Returns the object which isn't null

        Returns:
            GraphElement
        """

        for name in self.command_types:
            if getattr(self, name) is not None:
                return getattr(self, name)

    def set(self, instance):
        if instance in self.command_types:
            if instance == "VertexLabel":
                setattr(self, instance, Vertex)
            elif instance == "EdgeLabel":
                setattr(self, instance, Edge)
            else:
                setattr(self, instance, Contexts)
        else:
            raise NotImplementedError("Implemented only VertexLabel/EdgeLabel/ContextAction support for CommandType "
                                      "got " + instance)

        return self


class GraphOperation:

    def __init__(self, graph_operation_on, element_name, command_metadata):
        """

        Args:
            graph_operation_on(GraphElementType):
            element_name (str)
            command_metadata (GraphOperationMetadata):
        """
        self.graph_element = graph_operation_on.get()
        self.element_name = element_name
        self.metadata = command_metadata

        self.OPERATION = None
        self.CHANNEL = None
        self.SERVICE = None

        self.processor = GraphElement

    def __repr__(self):
        return 'Command(%s, %s)' % (str(self.graph_element), self.element_name)

    def set_operation(self, op):
        if op in ["GET", "PUT"]:
            self.OPERATION = op
        else:
            raise NotImplementedError("Implemented only GET and PUT operation.")

    def set_channel(self, channel):
        self.CHANNEL = channel

    def get_processor(self):
        """This method gets the processor. A Processor is a Class which
        specifies weather its processing Vertex, Edge, Context etc

        Returns:

        """

        if self.OPERATION is None:
            raise ValueError("Call set_operation to set the OPERATION type before calling apply(stub) method")
        if self.CHANNEL is None:
            raise ValueError("Call set_channel to set the CHANNEL type before calling get_processor() method")
        if not isinstance(self.element_name, str):
            raise ValueError("String dataType expected for metadata either ALL or name of element")

        self.processor = self.graph_element(self.OPERATION, self.element_name, self.metadata.get_metadata())
        self.__generate_service__()
        self.processor.set_service(self.SERVICE)

        if self.metadata.get_metadata() is not None:
            operator = self.metadata.get_operator()

            operator.set_element(self.processor.get_element())
            operator.set_service(self.SERVICE)

            self.processor.set_optional_operator(operator)

        return self.processor

    def __generate_service__(self):

        if self.CHANNEL is None:
            raise ValueError("Call set_channel(channel) before calling get_processor")

        if str(self.processor) == "VertexLabel":
            self.SERVICE = management_pb2_grpc.ManagementForVertexLabelsStub(self.CHANNEL)

        elif str(self.processor) == "EdgeLabel":
            self.SERVICE = management_pb2_grpc.ManagementForEdgeLabelsStub(self.CHANNEL)

        elif str(self.processor) == "ContextAction":
            self.SERVICE = management_pb2_grpc.AccessContextStub(self.CHANNEL)

        else:
            raise NotImplementedError(f"Implemented only Service for VertexLabel/EdgeLabel/ContextAction got {self.processor}")


# class JanusGraphArguments(Enum):
#     host = "localhost"
#     port = 10182
#     op = "GET"
#     cmd = GraphOperation
#
#     def __str__(self):
#         return self.value


def switcher(element, data):

    if element == "VertexLabel":
        return f"name={data.name}",  # ", readOnly={data.readOnly}, partitioned={data.partitioned}",
    elif element == "EdgeLabel":
        return f"name={data.name}",  # direction: {data.direction}, multiplicity={data.multiplicity}",
    elif element == "ContextAction":
        return f"name={data.graphName}, storage={data.storageBackend}"
    else:
        return None


if __name__ == '__main__':
    # Input format
    # python -m grpc_tools.protoc -I src/main/proto/ --python_out=src/main/python/janusgraph_grpc_python/management/ --grpc_python_out=src/main/python/janusgraph_grpc_python/management/ src/main/proto/management.proto
    # python sample_client.py --host localhost --port 10182 --op GET --arg ContextAction
    # python sample_client.py --host localhost --port 10182 --op GET --arg VertexLabel ALL
    # python sample_client.py --host localhost --port 10182 --op GET --arg VertexLabel 'name'
    # python sample_client.py --host localhost --port 10182 --op PUT --arg VertexLabel 'name' <To Define how to do PUT>
    # python sample_client.py --host localhost --port 10182 --op PUT --arg VertexLabel god INDEX index_type=CompositeIndex index_on=age,name index_name=byAgeAndNameComposite
    # python sample_client.py --host localhost --port 10182 --op GET --arg VertexLabel god INDEX index_type=CompositeIndex index_name=byAgeAndNameComposite

    parser = argparse.ArgumentParser()

    parser.add_argument('--host', type=str)
    parser.add_argument('--port', default=10182, type=int)
    parser.add_argument('--op', type=str, default="GET")
    parser.add_argument('--arg', action=GraphOperationAction)

    args = parser.parse_args()

    host = args.host
    port = args.port
    op = args.op
    action = args.arg[0]

    GRAPH_NAME = "graph_berkleydb"

    print(host)
    print(port)
    print(op)
    print(action)
    print(type(action))

    print("=======================")

    channel = grpc.insecure_channel(f'{host}:{port}')

    action.set_operation(op)
    action.set_channel(channel)

    processor = action.get_processor()

    print("================")

    response_it = processor.operate()

    print(50*"-")
    if isinstance(response_it, Iterable):
        for resp in response_it:
            print(switcher(str(processor), resp))

    else:
        print(response_it)
    print(50*"-")

    channel.close()

    logging.basicConfig()
    # run()

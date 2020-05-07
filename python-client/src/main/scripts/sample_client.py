import random
import logging

import grpc

import grpc._channel as ch

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
    # Defaults for an Index
    index_type = None
    index_name = None
    index_on = {}
    element_to_index = None
    index_only = False
    unique_index = False

    supported_parameters = ["index_type", "index_name", "index_on", "element_to_index", "index_only", "unique_index"]

    # OPERATION = None
    CONTEXT = None
    SERVICE = None
    # CHANNEL = None
    ELEMENT = None

    def __init__(self, **kwargs):

        self.__are_valid_parameters_passed__(**kwargs)

        for property_name, property_value in kwargs.items():
            setattr(self, property_name, property_value)

        self.__are_required_parameters_set__()
    #
    # def set_operation(self, op):
    #     self.OPERATION = op
    #

    def set_element(self, element):
        """

        Args:
            element (GraphElement):

        Returns:

        """
        self.ELEMENT = element
        self.element_to_index = str(element)

    def set_context(self, context):
        self.CONTEXT = context

    def set_service(self, stub):
        self.SERVICE = stub

    # def set_channel(self, channel):
    #     self.CHANNEL = channel

    def get_indexer(self):
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

    def get_index(self):
        raise NotImplementedError(f"{str(self)} is not subclassed by any other class yet so no get_index() implemented")

    def __get_element__(self):
        if self.element_to_index == "VertexLabel":
            self.ELEMENT = management_pb2.VertexLabel()
        return

    def __are_valid_parameters_passed__(self, **kwargs):
        valid_params_passed = all([x in self.supported_parameters for x in kwargs.keys()])

        if not valid_params_passed:
            invalid_param_idx = [i for i, x in enumerate([x in self.supported_parameters
                                                          for x in kwargs.keys()]) if x is False]
            invalid_params = itemgetter(*invalid_param_idx)(kwargs.keys())

            raise LookupError(f"Invalid parameter passed. The passed parameter {invalid_params} "
                              f"is not part of supported parameter list ${self.supported_parameters}")

    def __are_required_parameters_set__(self):
        # The compulsory parameters are the ones which are initialized as either None or as empty object
        # Optional parameters are already defaulted to a value other than None or Empty object
        for parameter in self.supported_parameters:
            if getattr(self, parameter) is None:
                raise AttributeError(f"{parameter} needs to be defined while initializing class. Got None")
            else:
                if len(getattr(self, parameter)) == 0:
                    raise AttributeError(f"{parameter} length is 0. At-least one property needs to be "
                                         "defined while initializing class.")


class CompositeIndex(GraphIndexer):
    REQUEST = None

    def __init__(self, **kwargs):
        super().__init__(**kwargs)

    def __str__(self):
        return "CompositeIndex"

    def create_get_index_request(self):
        if str(self.ELEMENT) == "VertexLabel":
            self.REQUEST = management_pb2.GetCompositeIndicesByVertexLabelRequest(context=self.CONTEXT, vertexLabel=self.ELEMENT)
        elif str(self.ELEMENT) == "EdgeLabel":
            self.REQUEST = management_pb2.GetCompositeIndicesByEdgeLabelRequest(context=self.CONTEXT, edgeLabel=self.ELEMENT)
        else:
            raise ValueError(f"Invalid element_to_index parameter. "
                             f"Expecting VertexLabel/EdgeLabel for {str(self.ELEMENT)}")
        return self

    def create_put_index_request(self):
        if str(self.ELEMENT) == "VertexLabel":
            index = management_pb2.CompositeVertexIndex(name=self.index_name, properties=self.index_on.keys(), unique=self.unique_index)
            self.REQUEST = management_pb2.EnsureCompositeIndexByVertexLabelRequest(context=self.CONTEXT, vertexLabel=self.ELEMENT, index=index)

        elif str(self.ELEMENT) == "EdgeLabel":
            index = management_pb2.CompositeEdgeIndex(name=self.index_name, properties=self.index_on.keys(), unique=self.unique_index)
            self.REQUEST = management_pb2.EnsureCompositeIndexByEdgeLabelRequest(context=self.CONTEXT, edgeLabel=self.ELEMENT, index=index)

        else:
            raise ValueError(f"Invalid element_to_index parameter. "
                             f"Expecting VertexLabel/EdgeLabel for {str(self.ELEMENT)}")
        return self

    def put_index(self):
        self.create_put_index_request()

        if self.element_to_index == "VertexLabel":
            return self.SERVICE.EnsureCompositeIndexByVertexLabel(self.REQUEST)
        elif self.element_to_index == "EdgeLabel":
            return self.SERVICE.EnsureCompositeIndexByEdgeLabel(self.REQUEST)
        else:
            raise ValueError(f"Invalid element_to_index parameter. "
                             f"Expecting VertexLabel/EdgeLabel for {self.element_to_index}")

    def get_index(self):
        self.create_get_index_request()

        if self.element_to_index == "VertexLabel":
            return self.SERVICE.GetCompositeIndicesByVertexLabel(self.REQUEST)
        elif self.element_to_index == "EdgeLabel":
            return self.SERVICE.GetCompositeIndicesByEdgeLabel(self.REQUEST)
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
                self.METADATA = {x.split("=")[0].strip(): x.split("=")[1].strip().split(",")
                                    for x in metadata[1:]}

                self.INDEXER = GraphIndexer(**self.METADATA)
            else:
                self.METADATA = {x.split("=")[0].strip(): x.split("=")[1].strip().split(",")
                                    for x in metadata}

                self.ADDER = GraphElementAdder().set(**self.METADATA)

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
        print(lst)
        print(values)
        element_type, *element_name_and_metadata = values

        if len(element_name_and_metadata) == 1:
            element_name = element_name_and_metadata[0]
            element_metadata = None
        else:
            element_name = element_name_and_metadata[0]
            element_metadata = element_name_and_metadata[1:]

        print(element_type, element_name, element_metadata)
        print(values)
        if element_metadata is not None:
            element_metadata = {x.split("=")[0].strip(): x.split("=")[1].strip().split(",")
                                for x in element_metadata[1:]}
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
        return self.ELEMENT

    def set_optional_operator(self, addtnl_operator):
        self.OPTIONAL_OPERATOR = addtnl_operator

    def __generate_context__(self):
        self.CONTEXT = management_pb2.JanusGraphContext(graphName="graph_berkleydb")
        return self

    def __generate_request__(self):
        if self.operation == "GET":
            if self.element_label == "ALL":
                print("Getting GetVertexLabels")
                self.REQUEST = management_pb2.GetVertexLabelsRequest(context=self.CONTEXT)
            else:
                print("Getting GetVertexLabelByName")
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
                print("Getting GetEdgeLabelByName")
                return self.service.GetVertexLabelsByName(self.REQUEST)
        else:
            if isinstance(self.OPTIONAL_OPERATOR, GraphIndexer):
                if self.element_label == "ALL":
                    raise NotImplementedError("Not yet implemented GET operation on index with ALL VertexLabel. TODO")
                    pass
                else:
                    indexer = self.OPTIONAL_OPERATOR.get_indexer()

                    return indexer.get_index()
            else:
                raise NotImplementedError("Not implemented GET method for GraphAdder instance. "
                                          "Because logically different")

    def __put__(self):
        self.__generate_context__()
        self.__generate_request__()
        if self.OPTIONAL_METADATA is None:
            return self.service.EnsureVertexLabel(self.REQUEST)
        else:
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
        self.CONTEXT = management_pb2.JanusGraphContext(graphName="graph_berkleydb")
        return self

    def __generate_request__(self):
        if self.operation == "GET":
            if self.element_label == "ALL":
                print("Getting GetVertexLabels")
                self.REQUEST = management_pb2.GetEdgeLabelsRequest(context=self.CONTEXT)
            else:
                print("Getting GetVertexLabelByName")
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
                print("Getting GetEdgeLabelByName")
                return self.service.GetEdgeLabelsByName(self.REQUEST)
        else:
            if isinstance(self.OPTIONAL_OPERATOR, GraphIndexer):
                if self.element_label == "ALL":
                    raise NotImplementedError("Not yet implemented GET operation on index with ALL EdgeLabel. TODO")
                    pass
                else:
                    indexer = self.OPTIONAL_OPERATOR.get_indexer()

                    return indexer.get_index()
            else:
                raise NotImplementedError("Not implemented GET method for GraphAdder instance. "
                                          "Because logically different")

    def __put__(self):
        self.__generate_context__()
        self.__generate_request__()
        if self.OPTIONAL_METADATA is None:
            return self.service.EnsureEdgeLabel(self.REQUEST)
        else:
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
    def __init__(self, operation, label):
        super().__init__("ContextAction", operation, label)

        self.operation = operation
        self.element_label = label

        self.context_name = "graph_berkleydb"

        self.CONTEXT = None
        self.REQUEST = None
        self.ELEMENT = self.get_element()

    def __generate_context__(self):
        self.CONTEXT = management_pb2.JanusGraphContext(graphName=self.context_name) if self.ELEMENT is None else self.ELEMENT
        return self

    def __generate_request__(self):
        if self.operation == "GET":
            if self.element_label == "ALL":
                print("Getting GetContext")
                self.REQUEST = management_pb2.GetContextsRequest(context=self.CONTEXT)
            else:
                print("Getting GetContextByName")
                self.REQUEST = management_pb2.GetContextByGraphNameRequest(context=self.CONTEXT, name=self.element_label)
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
            self.processor.set_optional_operator(operator)

        return self.processor

    def __generate_service__(self):

        if self.CHANNEL is None:
            raise ValueError("Call set_channel(channel) before calling get_processor")

        if str(self.processor) == "VertexLabel":
            return management_pb2_grpc.ManagementForVertexLabelsStub(self.CHANNEL)

        elif str(self.processor) == "EdgeLabel":
            return management_pb2_grpc.ManagementForEdgeLabelsStub(self.CHANNEL)

        elif str(self.processor) == "ContextAction":
            return management_pb2_grpc.AccessContextStub(self.CHANNEL)

        else:
            raise NotImplementedError(f"Implemented only Service for VertexLabel/EdgeLabel/ContextAction got {self.processor}")


class JanusGraphArguments(Enum):
    host = "localhost"
    port = 10182
    op = "GET"
    cmd = GraphOperation

    def __str__(self):
        return self.value


if __name__ == '__main__':
    # Input format
    # python sample_client.py --host localhost --port 10182 --op GET --arg ContextAction
    # python sample_client.py --host localhost --port 10182 --op GET --arg VertexLabel ALL
    # python sample_client.py --host localhost --port 10182 --op GET --arg VertexLabel 'name'
    # python sample_client.py --host localhost --port 10182 --op PUT --arg VertexLabel 'name' <To Define how to do PUT>
    from collections.abc import Iterable

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
    print(processor)

    response_it = processor.operate()

    print(type(response_it))

    if isinstance(response_it, Iterable):
        for resp in response_it:
            print("I'm inside loop")
            if str(processor) != "ContextAction":
                print(f'response from server: ID=${resp.id} name=${resp.name}')
                print(f"Response is ${resp}")
            else:
                print(resp)

    else:
        print(response_it)

    channel.close()

    logging.basicConfig()
    # run()

import random
import logging

import grpc

import grpc._channel as ch

# import route_guide_pb2
# import route_guide_pb2_grpc
# import route_guide_resources
from typing import Union, Optional
import argparse
from enum import Enum

import janusgraph_grpc_python.management.management_pb2 as management_pb2
import janusgraph_grpc_python.management.management_pb2_grpc as management_pb2_grpc

#
# def make_route_note(message, latitude, longitude):
#     return route_guide_pb2.RouteNote(
#         message=message,
#         location=route_guide_pb2.Point(latitude=latitude, longitude=longitude))
#
#
# def guide_get_one_feature(stub, point):
#     feature = stub.GetFeature(point)
#     if not feature.location:
#         print("Server returned incomplete feature")
#         return
#
#     if feature.name:
#         print("Feature called %s at %s" % (feature.name, feature.location))
#     else:
#         print("Found no feature at %s" % feature.location)
#
#
# def guide_get_feature(stub):
#     guide_get_one_feature(
#         stub, route_guide_pb2.Point(latitude=409146138, longitude=-746188906))
#     guide_get_one_feature(stub, route_guide_pb2.Point(latitude=0, longitude=0))
#
#
# def guide_list_features(stub):
#     rectangle = route_guide_pb2.Rectangle(
#         lo=route_guide_pb2.Point(latitude=400000000, longitude=-750000000),
#         hi=route_guide_pb2.Point(latitude=420000000, longitude=-730000000))
#     print("Looking for features between 40, -75 and 42, -73")
#
#     features = stub.ListFeatures(rectangle)
#
#     for feature in features:
#         print("Feature called %s at %s" % (feature.name, feature.location))
#
#
# def generate_route(feature_list):
#     for _ in range(0, 10):
#         random_feature = feature_list[random.randint(0, len(feature_list) - 1)]
#         print("Visiting point %s" % random_feature.location)
#         yield random_feature.location
#
#
# def guide_record_route(stub):
#     feature_list = route_guide_resources.read_route_guide_database()
#
#     route_iterator = generate_route(feature_list)
#     route_summary = stub.RecordRoute(route_iterator)
#     print("Finished trip with %s points " % route_summary.point_count)
#     print("Passed %s features " % route_summary.feature_count)
#     print("Travelled %s meters " % route_summary.distance)
#     print("It took %s seconds " % route_summary.elapsed_time)
#
#
# def generate_messages():
#     messages = [
#         make_route_note("First message", 0, 0),
#         make_route_note("Second message", 0, 1),
#         make_route_note("Third message", 1, 0),
#         make_route_note("Fourth message", 0, 0),
#         make_route_note("Fifth message", 1, 0),
#     ]
#     for msg in messages:
#         print("Sending %s at %s" % (msg.message, msg.location))
#         yield msg
#
#
# def guide_route_chat(stub):
#     responses = stub.RouteChat(generate_messages())
#     for response in responses:
#         print("Received message %s at %s" %
#               (response.message, response.location))
#
#
# def run():
#     # NOTE(gRPC Python Team): .close() is possible on a channel and should be
#     # used in circumstances in which the with statement does not fit the needs
#     # of the code.
#
#     host = "localhost"
#     port = "10182"
#
#     channel = grpc.insecure_channel(f'{host}:{port}')
#
#     content_stub = management_pb2_grpc.AccessContextStub(channel)
#     edge_management_stub = management_pb2_grpc.ManagementForEdgeLabelsStub(channel)
#     stub = management_pb2_grpc.ManagementForVertexLabelsStub(channel)
#
#     context = management_pb2.JanusGraphContext()
#     context.set_graphName("graph")
#     request = management_pb2.GetVertexLabelsRequest()
#     request.set_context(context)
#
#     response = stub.GetVertexLabels(request)
#
#     #
#     # with grpc.insecure_channel(f'{host}:{port}') as channel:
#     #
#     #     content_stub = management_pb2_grpc.AccessContextStub(channel)
#     #     edge_management_stub = management_pb2_grpc.ManagementForEdgeLabelsStub(channel)
#     #     vertex_management_stub = management_pb2_grpc.ManagementForVertexLabelsStub(channel)
#     #
#     #     stub = route_guide_pb2_grpc.RouteGuideStub(channel)
#     #     print("-------------- GetFeature --------------")
#     #     guide_get_feature(stub)
#     #     print("-------------- ListFeatures --------------")
#     #     guide_list_features(stub)
#     #     print("-------------- RecordRoute --------------")
#     #     guide_record_route(stub)
#     #     print("-------------- RouteChat --------------")
#     #     guide_route_chat(stub)
#


class GraphIndexer:
    def __init__(self):
        pass


class GraphOperationMetadata:
    RESERVED_KEYWORDS = ["ALL", "INDEX"]
    # Metadata can take the form or either ALL or INDEX (For Indexing purpose) or String which means
    # retrieval of Graph Element which can either be VertexLabel, EdgeLabel or Property

    RETRIEVER = None
    INDEXER = None

    def __init__(self):
        pass

    def set(self, metadata):
        if metadata not in self.RESERVED_KEYWORDS:
            self.RETRIEVER = GraphElementRetriever().set(metadata)
        else:
            if metadata == "ALL":
                self.RETRIEVER = GraphElementRetriever().set(metadata)
            else:
                pass
        return self


class GraphElementRetriever:
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
        a, *b = values
        print(a, b)
        print(values)
        print("--------------")
        lst.append(GraphOperation(GraphOperationType().set(a), str(b)))
        setattr(namespace, self.dest, lst)


class GraphElement(object):
    def __init__(self, element, operation, metadata):
        self.element = element
        self.operation = operation
        self.metadata = metadata
        self.service = None
        pass

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
    def __init__(self, operation, metadata):
        super().__init__("VertexLabel", operation, metadata)

        self.operation = operation
        self.metadata = metadata

    def __get__(self):
        context = management_pb2.JanusGraphContext(graphName="graph_berkleydb")

        if self.metadata == "ALL":
            print("Getting GetVertexLabels")
            request = management_pb2.GetVertexLabelsRequest(context=context)
            return self.service.GetVertexLabels(request)
        else:
            print("Getting GetVertexLabelByName")
            request = management_pb2.GetVertexLabelsByNameRequest(context=context, name=self.metadata)
            return self.service.GetVertexLabelsByName(request)

    def __put__(self):
        context = management_pb2.JanusGraphContext(graphName="graph_berkleydb")
        label = management_pb2.VertexLabel(name=self.metadata, readOnly=False, partitioned=False)
        request = management_pb2.EnsureVertexLabelRequest(context=context, label=label)

        return self.service.EnsureVertexLabel(request)


class Edge(GraphElement):
    def __init__(self, operation, metadata):
        super().__init__("EdgeLabel", operation, metadata)

        self.operation = operation
        self.metadata = metadata

    def __get__(self):
        context = management_pb2.JanusGraphContext(graphName="graph_berkleydb")

        if self.metadata == "ALL":
            request = management_pb2.GetEdgeLabelsRequest(context=context)
            return self.service.GetEdgeLabels(request)
        else:
            request = management_pb2.GetEdgeLabelsByNameRequest(context=context, name=self.metadata)
            return self.service.GetEdgeLabelsByName(request)


class Contexts(GraphElement):
    def __init__(self, operation, metadata):
        super().__init__("ContextAction", operation, metadata)

        self.operation = operation
        self.metadata = metadata

    def __get__(self):
        context = management_pb2.JanusGraphContext(graphName="graph_berkleydb")

        if self.metadata == "ALL":
            request = management_pb2.GetContextsRequest()
            return self.service.GetContexts(request)
        else:
            request = management_pb2.GetContextByGraphNameRequest(name="graph_berkleydb")
            return self.service.GetContextByGraphName(request)


class GraphOperationType:

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

    def get(self) -> Union[Vertex, Edge, Contexts]:
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

    def __init__(self, graph_operation_type, command_metadata):
        """

        Args:
            graph_operation_type (GraphOperationType):
            command_metadata (str):
        """
        self.graph_operation = graph_operation_type.get()
        self.metadata = command_metadata

        self.operation = None
        self.channel = None

        self.processor = None

    def __repr__(self):
        return 'Command(%s, %s)' % (str(self.graph_operation), self.metadata)

    def set_operation(self, op):
        if op in ["GET", "PUT"]:
            self.operation = op
        else:
            raise NotImplementedError("Implemented only GET and PUT operation.")

    def set_channel(self, channel):
        self.channel = channel

    def get_processor(self):
        """This method gets the processor. A Processor is a Class which
        specifies weather its processing Vertex, Edge, Context etc

        Returns:

        """

        if self.operation is None:
            raise ValueError("Call set_operation to set the OPERATION type before calling apply(stub) method")
        if self.channel is None:
            raise ValueError("Call set_channel to set the CHANNEL type before calling get_processor() method")
        if not isinstance(self.metadata, str):
            raise ValueError("String dataType expected for metadata either ALL or name of element")

        self.processor = self.graph_operation(self.operation, self.metadata)
        self.processor.set_service(self.__generate_service__())
        return self.processor

    def __generate_service__(self):

        assert self.processor is not None

        if self.channel is None:
            raise ValueError("Call set_channel(channel) before calling get_processor")

        if str(self.processor) == "VertexLabel":
            return management_pb2_grpc.ManagementForVertexLabelsStub(self.channel)

        elif str(self.processor) == "EdgeLabel":
            return management_pb2_grpc.ManagementForEdgeLabelsStub(self.channel)

        elif str(self.processor) == "ContextAction":
            return management_pb2_grpc.AccessContextStub(self.channel)

        else:
            raise NotImplementedError("Implemented only Service for VertexLabel/EdgeLabel/ContextAction got " + self.processor)

    def apply(self, stub):

        if self.operation is None:
            raise ValueError("Call set_operation to set the OPERATION type before calling apply(stub) method")

        obj = self.graph_operation(self.operation, self.metadata)

        if isinstance(self.metadata, str):
            return obj.operate()
        else:
            raise ValueError("String dataType expected for metadata either ALL or name of element")


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

    # if isinstance(response_it, ch._MultiThreadedRendezvous):
    #     # Its a Streaming response
    #     for resp in response_it:
    #         if str(processor) != "ContextAction":
    #             print(f'response from server: ID=${resp.id} name=${resp.name} property=${resp.properties.name}')
    #         else:
    #             print(resp)
    # else:
    #     print(response_it)

    logging.basicConfig()
    # run()

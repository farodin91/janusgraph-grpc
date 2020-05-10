from management import management_pb2_grpc
from structure.element.graph_element import GraphElement
from graph_operation.graph_indexer import GraphIndexer


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

            if isinstance(operator, GraphIndexer):
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

from .graph_element import GraphElement
from management import management_pb2

GRAPH_NAME = "graph_berkleydb"


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

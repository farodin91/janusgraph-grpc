from .graph_element import GraphElement
from ...management import management_pb2
from ...graph_operation.graph_indexer import GraphIndexer
from ...client.management_client import GRAPH_NAME


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

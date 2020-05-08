from graph_operation.graph_indexer import GraphIndexer
from management import management_pb2


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

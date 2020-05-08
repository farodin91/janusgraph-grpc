from ..structure.element.edge import Edge
from ..structure.element.vertex import Vertex
from ..structure.element.context_action import Contexts


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

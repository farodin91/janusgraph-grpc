from management import management_pb2
from operator import itemgetter


class GraphElementAdder:
    SINGLE = False
    ALL = False
    name = None

    ELEMENT = None
    element_to_update = None

    supported_parameters = ["properties", "readOnly", "partitioned", "direction", "multiplicity"]

    def __init__(self, **kwargs):
        self.properties = None
        self.readOnly = None
        self.partitioned = None
        self.direction = None
        self.multiplicity = None

        for property_name, property_value in kwargs.items():
            setattr(self, property_name, property_value)

    def __are_valid_parameters_passed__(self, **kwargs):
        valid_params_passed = all([x in self.supported_parameters for x in kwargs.keys()])

        if not valid_params_passed:
            invalid_param_idx = [i for i, x in enumerate([x in self.supported_parameters
                                                          for x in kwargs.keys()]) if x is False]
            invalid_params = itemgetter(*invalid_param_idx)(kwargs.keys())

            raise LookupError(f"Invalid parameter passed. The passed parameter {invalid_params} "
                              f"is not part of supported parameter list ${self.supported_parameters}")

    def set_element(self, element):
        """

        Args:
            element (GraphElement):

        Returns:

        """

        self.ELEMENT = element

        if isinstance(element, management_pb2.VertexLabel):
            self.element_to_update = "VertexLabel"
        elif isinstance(element, management_pb2.EdgeLabel):
            self.element_to_update = "EdgeLabel"
        else:
            raise ValueError("Invalid element accessed in setter() method. Expecting class to be "
                             "EdgeLabel or VertexLabel for " + str(type(element)))

    def __build_element__(self):
        for property_name in self.supported_parameters:
            value = getattr(self, property_name, None)
            if value is not None:

                if property_name == "properties":
                    if self.element_to_update == "VertexLabel":
                        if isinstance(value, str):
                            vp = [management_pb2.VertexProperty(name=value)]
                        else:
                            vp = []
                            for prop in value:
                                vp.append(management_pb2.VertexProperty(name=prop))
                        self.ELEMENT.properties = vp

                    else:
                        if isinstance(value, str):
                            ep = [management_pb2.EdgeProperty(name=value)]
                        else:
                            ep = []
                            for prop in value:
                                ep.append(management_pb2.EdgeProperty(name=prop))
                        self.ELEMENT.properties = ep

                elif property_name == "multiplicity":
                    raise NotImplementedError("Not implemented custom multiplicity for Edges PUT request")
                elif property_name == "direction":
                    raise NotImplementedError("Not implemented custom direction for Edges PUT request")

                elif property_name == "readOnly":
                    self.ELEMENT.readOnly = value if isinstance(value, bool) else (True if value == "true" else False)

                elif property_name == "partitioned":
                    self.ELEMENT.partitioned = value if isinstance(value, bool) else (True if value == "true" else False)

                else:
                    raise ValueError(f"Expecting the keys to be either of {self.supported_parameters} "
                                     f"but got {property_name}")

        return self

    def get_element(self):
        self.__build_element__()
        return self.ELEMENT

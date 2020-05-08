from operator import itemgetter
from collections.abc import Iterable

from ..management import management_pb2
from ..structure.index.composite_index import CompositeIndex
from ..structure.index.mixed_index import MixedIndex


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

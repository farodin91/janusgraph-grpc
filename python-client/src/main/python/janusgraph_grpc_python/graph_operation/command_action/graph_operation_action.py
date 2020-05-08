import argparse
from .graph_operation import GraphOperation
from .graph_operation_metadata import GraphOperationMetadata
from ...type_class.graph_element_type import GraphElementType


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

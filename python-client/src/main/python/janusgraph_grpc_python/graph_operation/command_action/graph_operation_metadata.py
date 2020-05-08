from ..graph_indexer import GraphIndexer
from ..graph_adder import GraphElementAdder


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
                self.METADATA = {x.split("=")[0].strip():
                                     x.split("=")[1].strip().split(",")
                                     if len(x.split("=")[1].strip().split(",")) > 1
                                     else x.split("=")[1].strip().split(",")[0]
                                 for x in metadata[1:]}

                self.INDEXER = GraphIndexer(**self.METADATA)
            else:
                self.METADATA = {x.split("=")[0].strip():
                                     x.split("=")[1].strip().split(",")
                                     if len(x.split("=")[1].strip().split(",")) > 1
                                     else x.split("=")[1].strip().split(",")[0]
                                 for x in metadata}

                self.ADDER = GraphElementAdder().set(**self.METADATA)

        print(f"Metadata is after parsing: {self.METADATA}")

        return self

    def get_metadata(self):
        return self.METADATA

    def get_operator(self):
        if self.INDEXER is None:
            return self.ADDER
        else:
            return self.INDEXER

from ...graph_operation.graph_indexer import GraphIndexer


class MixedIndex(GraphIndexer):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)

    def __str__(self):
        return "MixedIndex"

    def create(self):
        self.index_type = str(self)
        return

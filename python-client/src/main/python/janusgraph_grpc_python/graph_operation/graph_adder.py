

class GraphElementAdder:
    SINGLE = False
    ALL = False
    name = None

    def __init__(self):
        pass

    def set(self, retriever):
        if retriever == "ALL":
            self.ALL = True
        else:
            self.SINGLE = True
            self.name = retriever
        return self

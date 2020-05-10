class GraphElement(object):
    def __init__(self, element, operation, metadata, optional_metadata):
        self.element = element
        self.operation = operation
        self.metadata = metadata
        self.service = None

        self.OPTIONAL_METADATA = optional_metadata
        pass

    def get_element(self):
        raise NotImplementedError("Not implemented GraphElement without being subclassed and get_element() being called")

    def set_service(self, stub):
        self.service = stub

    def operate(self):

        if self.service is None:
            raise ValueError("Please call set_service(stub) before calling operate()")

        print("I'm operating on " + self.element + " with operation " + self.operation)

        if self.operation == "GET":
            return self.__get__()
        elif self.operation == "PUT":
            return self.__put__()
        else:
            raise NotImplementedError("Implemented only GET and PUT operations till now")

    def __get__(self):
        pass

    def __put__(self):
        return

    def __str__(self):
        print("I'm getting string representation of GraphElement and the element is " + self.element)
        return self.element

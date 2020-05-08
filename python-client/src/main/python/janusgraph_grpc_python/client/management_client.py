import argparse
from collections.abc import Iterable
import grpc
import sys, os
sys.path.append(os.path.abspath(os.getcwd() + "../../"))
print(sys.path[-2:])

from graph_operation.command_action.graph_operation_action import GraphOperationAction


def switcher(element, data):

    if element == "VertexLabel":
        return f"name={data.name}",  # ", readOnly={data.readOnly}, partitioned={data.partitioned}",
    elif element == "EdgeLabel":
        return f"name={data.name}",  # direction: {data.direction}, multiplicity={data.multiplicity}",
    elif element == "ContextAction":
        return f"name={data.graphName}, storage={data.storageBackend}"
    else:
        return None


if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    parser.add_argument('--host', type=str)
    parser.add_argument('--port', default=10182, type=int)
    parser.add_argument('--op', type=str, default="GET")
    parser.add_argument('--arg', action=GraphOperationAction)

    args = parser.parse_args()

    host = args.host
    port = args.port
    op = args.op
    action = args.arg[0]

    print(host)
    print(port)
    print(op)
    print(action)
    print(type(action))

    print("=======================")

    channel = grpc.insecure_channel(f'{host}:{port}')

    action.set_operation(op)
    action.set_channel(channel)

    processor = action.get_processor()

    print("================")

    response_it = processor.operate()

    print(50*"-")
    if isinstance(response_it, Iterable):
        for resp in response_it:
            print(switcher(str(processor), resp))

    else:
        print(response_it)
    print(50*"-")

    channel.close()
    pass

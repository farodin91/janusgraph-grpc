package org.janusgraph.grpc.server

import io.grpc.stub.StreamObserver
import org.janusgraph.grpc.AccessContextGrpc
import org.janusgraph.grpc.GetContextByGraphNameRequest
import org.janusgraph.grpc.GetContextsRequest
import org.janusgraph.grpc.JanusGraphContext

class AccessContextImpl(private val contextManager: ContextManager) : AccessContextGrpc.AccessContextImplBase() {
    override fun getContexts(
        request: GetContextsRequest?,
        responseObserver: StreamObserver<JanusGraphContext?>?
    ) {
        if (request == null) {
            responseObserver?.onError(Throwable("Incorrect request"))
            return
        }
        val contexts = contextManager.contexts()
        contexts.forEach { responseObserver?.onNext(it) }
        responseObserver?.onCompleted()
    }

    override fun getContextByGraphName(
        request: GetContextByGraphNameRequest?,
        responseObserver: StreamObserver<JanusGraphContext?>?
    ) {
        if (request?.name == null) {
            responseObserver?.onError(Throwable("Incorrect request"))
            return
        }
        val context = contextManager.contextByName(request.name)
        responseObserver?.onNext(context)
        responseObserver?.onCompleted()
    }
}

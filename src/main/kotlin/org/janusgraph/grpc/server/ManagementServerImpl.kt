package org.janusgraph.grpc.server

import io.grpc.stub.StreamObserver
import org.janusgraph.grpc.*

class ManagementServerImpl(private val managementServer: ManagementServer, private val contextManager: ContextManager) :
    ManagementServerGrpc.ManagementServerImplBase() {

    override fun getVertexLabelsByName(
        request: GetVertexLabelsByNameRequest?,
        responseObserver: StreamObserver<VertexLabel>?
    ) {
        val management = contextManager.getManagement(request?.context)
        if (management == null) {
            responseObserver?.onError(Throwable("Incorrect context"))
            return
        }
        if (request?.name == null) {
            responseObserver?.onError(Throwable("Incorrect context"))
            return
        }
        val vertexLabelsByName = managementServer.getVertexLabelsByName(management, request.name)
        vertexLabelsByName.forEach { responseObserver?.onNext(it) }
        responseObserver?.onCompleted()
    }

    override fun ensureVertexLabel(request: EnsureVertexLabelRequest?, responseObserver: StreamObserver<VertexLabel>?) {
        val management = contextManager.getManagement(request?.context)
        if (management == null) {
            responseObserver?.onError(Throwable("Incorrect context"))
            return
        }
        if (request?.label == null) {
            responseObserver?.onError(Throwable("Incorrect context"))
            return
        }
        val vertexLabel = managementServer.ensureVertexLabel(management, request.label)
        responseObserver?.onNext(vertexLabel)
        responseObserver?.onCompleted()
    }

    override fun getVertexLabels(request: GetVertexLabelsRequest?, responseObserver: StreamObserver<VertexLabel>?) {
        val management = contextManager.getManagement(request?.context)
        if (management == null) {
            responseObserver?.onError(Throwable("Incorrect context"))
            return
        }
        val vertexLabelsByName = managementServer.getVertexLabels(management)
        vertexLabelsByName.forEach { responseObserver?.onNext(it) }
        responseObserver?.onCompleted()
    }
    override fun getEdgeLabelsByName(
        request: GetEdgeLabelsByNameRequest?,
        responseObserver: StreamObserver<EdgeLabel>?
    ) {
        val management = contextManager.getManagement(request?.context)
        if (management == null) {
            responseObserver?.onError(Throwable("Incorrect context"))
            return
        }
        if (request?.name == null) {
            responseObserver?.onError(Throwable("Incorrect context"))
            return
        }
        val vertexLabelsByName = managementServer.getEdgeLabelsByName(management, request.name)
        vertexLabelsByName.forEach { responseObserver?.onNext(it) }
        responseObserver?.onCompleted()
    }

    override fun getEdgeLabels(request: GetEdgeLabelsRequest?, responseObserver: StreamObserver<EdgeLabel>?) {
        val management = contextManager.getManagement(request?.context)
        if (management == null) {
            responseObserver?.onError(Throwable("Incorrect context"))
            return
        }
        val vertexLabelsByName = managementServer.getEdgeLabels(management)
        vertexLabelsByName.forEach { responseObserver?.onNext(it) }
        responseObserver?.onCompleted()
    }

    override fun ensureEdgeLabel(request: EnsureEdgeLabelRequest?, responseObserver: StreamObserver<EdgeLabel>?) {
        val management = contextManager.getManagement(request?.context)
        if (management == null) {
            responseObserver?.onError(Throwable("Incorrect context"))
            return
        }
        if (request?.label == null) {
            responseObserver?.onError(Throwable("Incorrect context"))
            return
        }
        val edgeLabel = managementServer.ensureEdgeLabel(management, request.label)
        responseObserver?.onNext(edgeLabel)
        responseObserver?.onCompleted()
    }
}

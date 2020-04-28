package org.janusgraph.grpc.server

import io.grpc.stub.StreamObserver
import org.janusgraph.grpc.*

class ManagementForEdgeLabelsImpl(
    private val managementServer: IManagementForEdgeLabels,
    private val contextManager: ContextManager
) :
    ManagementForEdgeLabelsGrpc.ManagementForEdgeLabelsImplBase() {

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

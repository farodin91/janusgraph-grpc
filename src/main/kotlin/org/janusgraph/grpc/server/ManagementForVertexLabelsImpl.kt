package org.janusgraph.grpc.server

import io.grpc.stub.StreamObserver
import org.janusgraph.grpc.*

class ManagementForVertexLabelsImpl(
    private val managementServer: IManagementForVertexLabels,
    private val contextManager: ContextManager
) :
    ManagementForVertexLabelsGrpc.ManagementForVertexLabelsImplBase() {

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
            responseObserver?.onError(Throwable("Not set name"))
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
            responseObserver?.onError(Throwable("Not set label"))
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

    override fun ensureCompositeIndexByVertexLabel(
        request: EnsureCompositeIndexByVertexLabelRequest?,
        responseObserver: StreamObserver<CompositeVertexIndex>?
    ) {
        val management = contextManager.getManagement(request?.context)
        if (management == null) {
            responseObserver?.onError(Throwable("Incorrect context"))
            return
        }
        if (request?.vertexLabel == null) {
            responseObserver?.onError(Throwable("Not set vertexLabel"))
            return
        }
        if (request.index == null) {
            responseObserver?.onError(Throwable("Not set index"))
            return
        }
        val index = managementServer.ensureCompositeIndexByVertexLabel(management, request.vertexLabel, request.index)
        responseObserver?.onNext(index)
        responseObserver?.onCompleted()
    }
}

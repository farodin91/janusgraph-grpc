package org.janusgraph.core.server

import io.grpc.Server
import io.grpc.ServerBuilder
import org.apache.tinkerpop.gremlin.server.GremlinServer
import org.apache.tinkerpop.gremlin.server.util.ServerGremlinExecutor
import org.janusgraph.grpc.server.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function

class JanusGraphServer(settings: JanusGraphSettings) {
    val gremlinServer : GremlinServer = GremlinServer(settings)
    var grpcServer: Server? = null

    companion object {
        val logger: Logger = LoggerFactory.getLogger(JanusGraphServer::class.java)
    }

    fun start() {
        grpcServer?.start()
        gremlinServer.start()?.exceptionally(Function<Throwable, ServerGremlinExecutor?> { t: Throwable ->
            logger.error("Gremlin Server was unable to start and will now begin shutdown: {}", t.message)
            gremlinServer.stop()?.join()
            gremlinServer.stop()?.join()
            null
        })?.join()
        grpcServer?.awaitTermination()
    }

    init {
        val graphManager: ContextManager? = gremlinServer.serverGremlinExecutor?.graphManager as ContextManager

        if(settings.grpcServer.enabled && graphManager != null){
            grpcServer = ServerBuilder
                .forPort(settings.grpcServer.port)
                .addService(AccessContextImpl(graphManager))
                .addService(
                    ManagementForEdgeLabelsImpl(
                        ManagementForEdgeLabels(),
                        graphManager
                    )
                )
                .addService(
                    ManagementForVertexLabelsImpl(
                        ManagementForVertexLabels(),
                        graphManager
                    )
                )
                .build()
        }
    }
}

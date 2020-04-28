package org.janusgraph.core.server

import org.apache.tinkerpop.gremlin.server.Settings
import org.janusgraph.grpc.server.JanusGraphSettings

fun main(args: Array<String>) {
    // add to vm options: -Dlog4j.configuration=file:conf/log4j.properties
    JanusGraphServer.logger.info("Start JanusGraph Server");
    val file: String = if (args.isNotEmpty()) args[0] else "conf/janusgraph-server.yaml"

    val settings: Settings
    settings = try {
        JanusGraphSettings.read(file)
    } catch (ex: Exception) {
        JanusGraphServer.logger.error(
            "Configuration file at {} could not be found or parsed properly. [{}]",
            file,
            ex.message
        )
        return
    }

    JanusGraphServer.logger.info("Configuring Gremlin Server from {}", file)
    val server = JanusGraphServer(settings)
    server.start()
}

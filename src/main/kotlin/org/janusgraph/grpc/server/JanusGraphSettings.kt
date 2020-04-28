package org.janusgraph.grpc.server

import org.apache.tinkerpop.gremlin.server.Settings
import org.apache.tinkerpop.gremlin.server.util.DefaultGraphManager
import org.janusgraph.core.server.DefaultJanusGraphManager
import org.yaml.snakeyaml.TypeDescription
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*

class JanusGraphSettings : Settings() {
    var grpcServer = GrpcServerSettings()

    class GrpcServerSettings {
        var enabled = true

        /**
         * Port to bind the server to.  Defaults to 10182.
         */
        var port = 10182
    }

    companion object {
        /**
         * Read configuration from a file into a new [Settings] object.
         *
         * @param file the location of a Gremlin Server YAML configuration file
         * @return a new [Optional] object wrapping the created [Settings]
         */
        @Throws(Exception::class)
        fun read(file: String?): JanusGraphSettings {
            val input: InputStream = FileInputStream(File(file))
            return read(input)
        }

        private fun autoImport(settings: JanusGraphSettings): JanusGraphSettings {
            settings.scriptEngines["gremlin-groovy"]
                ?.plugins
                ?.putIfAbsent("org.janusgraph.graphdb.tinkerpop.plugin.JanusGraphGremlinPlugin", mapOf())

            setSerializer(
                settings,
                "org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV3d0",
                "org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry"
            )
            setSerializer(
                settings,
                "org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerV3d0",
                "org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry"
            )

            if (DefaultGraphManager::class.java.name == settings.graphManager) {
                settings.graphManager = DefaultJanusGraphManager::class.java.name
            }

            return settings
        }

        private fun setSerializer(settings: JanusGraphSettings, className: String, registryClassName: String) {
            val serializerSettings = settings.serializers.firstOrNull {
                it.className == className && it.config["ioRegistries"] != null
            }
            if (serializerSettings == null) {
                val newSerializerSettings = SerializerSettings()
                newSerializerSettings.className = className
                newSerializerSettings.config = mapOf("ioRegistries" to listOf(registryClassName))
                settings.serializers.add(newSerializerSettings)
            } else {
                val putIfAbsent = serializerSettings.config.putIfAbsent("ioRegistries", emptyList<String>())
                if (putIfAbsent is List<*> && !putIfAbsent.contains(registryClassName)) {
                    val toMutableList = putIfAbsent.map { it as String }.toMutableSet()
                    toMutableList.add(registryClassName)
                    serializerSettings.config["ioRegistries"] = toMutableList
                }
            }
        }

        /**
         * Read configuration from a file into a new [Settings] object.
         *
         * @param stream an input stream containing a Gremlin Server YAML configuration
         * @return a new [Optional] object wrapping the created [Settings]
         */
        fun read(stream: InputStream): JanusGraphSettings {
            Objects.requireNonNull(stream)
            val constructor = Constructor(JanusGraphSettings::class.java)

            val settingsDescription = TypeDescription(Settings::class.java)
            settingsDescription.putMapPropertyType("graphs", String::class.java, String::class.java)
            settingsDescription.putMapPropertyType(
                "scriptEngines",
                String::class.java,
                ScriptEngineSettings::class.java
            )
            settingsDescription.putListPropertyType("serializers", SerializerSettings::class.java)
            settingsDescription.putListPropertyType("plugins", String::class.java)
            settingsDescription.putListPropertyType("processors", ProcessorSettings::class.java)
            settingsDescription.putListPropertyType("grcServer", GrpcServerSettings::class.java)
            constructor.addTypeDescription(settingsDescription)

            val serializerSettingsDescription = TypeDescription(SerializerSettings::class.java)
            serializerSettingsDescription.putMapPropertyType(
                "config",
                String::class.java,
                Any::class.java
            )
            constructor.addTypeDescription(serializerSettingsDescription)

            val scriptEngineSettingsDescription = TypeDescription(ScriptEngineSettings::class.java)
            scriptEngineSettingsDescription.putListPropertyType("imports", String::class.java)
            scriptEngineSettingsDescription.putListPropertyType("staticImports", String::class.java)
            scriptEngineSettingsDescription.putListPropertyType("scripts", String::class.java)
            scriptEngineSettingsDescription.putMapPropertyType(
                "config",
                String::class.java,
                Any::class.java
            )
            scriptEngineSettingsDescription.putMapPropertyType(
                "plugins",
                String::class.java,
                Any::class.java
            )
            constructor.addTypeDescription(scriptEngineSettingsDescription)

            val sslSettings = TypeDescription(SslSettings::class.java)
            constructor.addTypeDescription(sslSettings)

            val authenticationSettings = TypeDescription(AuthenticationSettings::class.java)
            constructor.addTypeDescription(authenticationSettings)

            val serverMetricsDescription = TypeDescription(ServerMetrics::class.java)
            constructor.addTypeDescription(serverMetricsDescription)

            val consoleReporterDescription = TypeDescription(ConsoleReporterMetrics::class.java)
            constructor.addTypeDescription(consoleReporterDescription)

            val csvReporterDescription = TypeDescription(CsvReporterMetrics::class.java)
            constructor.addTypeDescription(csvReporterDescription)

            val jmxReporterDescription = TypeDescription(JmxReporterMetrics::class.java)
            constructor.addTypeDescription(jmxReporterDescription)

            val slf4jReporterDescription = TypeDescription(Slf4jReporterMetrics::class.java)
            constructor.addTypeDescription(slf4jReporterDescription)

            val gangliaReporterDescription = TypeDescription(GangliaReporterMetrics::class.java)
            constructor.addTypeDescription(gangliaReporterDescription)

            val graphiteReporterDescription = TypeDescription(GraphiteReporterMetrics::class.java)
            constructor.addTypeDescription(graphiteReporterDescription)

            val grpcSettingsDescription = TypeDescription(GrpcServerSettings::class.java)
            constructor.addTypeDescription(grpcSettingsDescription)

            val yaml = Yaml(constructor)
            return autoImport(yaml.loadAs(stream, JanusGraphSettings::class.java))
        }
    }
}

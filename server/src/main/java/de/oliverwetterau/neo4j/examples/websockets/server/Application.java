package de.oliverwetterau.neo4j.examples.websockets.server;

import org.neo4j.backup.OnlineBackupSettings;
import org.neo4j.cluster.ClusterSettings;
import org.neo4j.graphdb.config.Setting;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.kernel.ha.HaSettings;
import org.neo4j.server.configuration.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import java.util.HashMap;
import java.util.Map;

/**
 * Starting point of this Spring Boot application.
 *
 * @author Oliver Wetterau
 */
@SpringBootApplication
@ComponentScan(basePackages = {"de.oliverwetterau.neo4j.examples.websockets.server", "de.oliverwetterau.neo4j.websockets"})
public class Application extends SpringBootServletInitializer {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] arguments) throws Throwable {
        new ExampleStarter(ExampleEmbeddedNeo4j.class, new ExampleNeo4jConfigurer(), getNeo4jSettings(arguments)).run();

        SpringApplication.run(Application.class, arguments);
    }

    protected static Map<Setting,String> getNeo4jSettings(final String[] arguments) {
        Map<Setting,String> settings = new HashMap<>();
        String[] argumentParts;
        String value;

        Integer highAvailabilityId = 1;

        for (String argument : arguments) {
            argumentParts = argument.split("=");
            value = argumentParts[1];

            switch (argumentParts[0]) {
                case "storagePath":
                    settings.put(GraphDatabaseSettings.store_dir, value);
                    logger.debug("[getNeo4jSettings] set storage path = {}", value);
                    break;

                case "ha.server_id":
                    settings.put(ClusterSettings.server_id, value);
                    highAvailabilityId = Integer.parseInt(value);
                    logger.debug("[getNeo4jSettings] set high availability id = {}", value);
                    break;

                case "ha.server":
                    settings.put(HaSettings.ha_server, value);
                    logger.debug("[getNeo4jSettings] set high availability server = {}", value);
                    break;

                case "ha.cluster_server":
                    settings.put(ClusterSettings.cluster_server, value);
                    logger.debug("[getNeo4jSettings] set high availability cluster server = {}", value);
                    break;

                case "online_backup_server":
                    settings.put(OnlineBackupSettings.online_backup_server, value);
                    logger.debug("[getNeo4jSettings] set online backup server = {}", value);
                    break;

                case "ha.initial_hosts":
                    settings.put(ClusterSettings.initial_hosts, value);
                    logger.debug("[getNeo4jSettings] set high availability initial hosts = {}", value);
                    break;

                case "http_port":
                    settings.put(ServerSettings.webserver_port, value);
                    logger.debug("[getNeo4jSettings] set http port = {}", value);
                    break;

                case "https_port":
                    settings.put(ServerSettings.webserver_https_port, value);
                    logger.debug("[getNeo4jSettings] set https port = {}", value);
                    break;
            }

            logger.debug("[getNeo4jSettings] parameter '{}' = '{}'", argumentParts[0], value);
        }

        settings.put(ServerSettings.auth_enabled, Boolean.FALSE.toString());
        settings.put(ServerSettings.webserver_address, "0.0.0.0");
        settings.put(GraphDatabaseSettings.pagecache_memory, "8G");
        settings.put(GraphDatabaseSettings.cache_type, "hpc");

        if (!settings.containsKey(GraphDatabaseSettings.store_dir)) {
            value = System.getProperty("user.dir") + "/data";
            settings.put(GraphDatabaseSettings.store_dir, value);
            logger.debug("[getNeo4jSettings] storage path not set => is now {}", value);
        }

        if (!settings.containsKey(ClusterSettings.server_id)) {
            settings.put(ClusterSettings.server_id, highAvailabilityId.toString());
            logger.debug("[getNeo4jSettings] high availability id not set => is now {}", highAvailabilityId.toString());
        }

        if (!settings.containsKey(HaSettings.ha_server)) {
            value = "localhost:" + new Integer(6362 + highAvailabilityId);
            settings.put(HaSettings.ha_server, value);
            logger.debug("[getNeo4jSettings] high availability server not set => is now {}", value);
        }

        if (!settings.containsKey(ClusterSettings.cluster_server)) {
            value = "localhost:" + new Integer(5000 + highAvailabilityId);
            settings.put(ClusterSettings.cluster_server, value);
            logger.debug("[getNeo4jSettings] high availability cluster server not set => is now {}", value);
        }

        if (!settings.containsKey(OnlineBackupSettings.online_backup_server)) {
            value = "localhost:" + new Integer(6365 + highAvailabilityId);
            settings.put(OnlineBackupSettings.online_backup_server, value);
            logger.debug("[getNeo4jSettings] online backup server not set => is now {}", value);
        }

        if (!settings.containsKey(ClusterSettings.initial_hosts)) {
            settings.put(ClusterSettings.initial_hosts, "localhost:5001");
            logger.debug("[getNeo4jSettings] initial hosts not set => is now {}", "localhost:5001");
        }

        if (!settings.containsKey(ServerSettings.webserver_port)) {
            value = Integer.toString(7473 + highAvailabilityId);
            settings.put(ServerSettings.webserver_port, value);
            logger.debug("[getNeo4jSettings] http port not set => is now {}", value);
        }

        if (!settings.containsKey(ServerSettings.webserver_https_port)) {
            value = Integer.toString(7483 + highAvailabilityId);
            settings.put(ServerSettings.webserver_https_port, value);
            logger.debug("[getNeo4jSettings] https port not set => is now {}", value);
        }

        return settings;
    }
}

package de.oliverwetterau.neo4j.examples.websockets.server;

import de.oliverwetterau.neo4j.websockets.server.neo4j.Configurer;
import org.neo4j.cluster.ClusterSettings;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.config.Setting;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.factory.HighlyAvailableGraphDatabaseFactory;
import org.neo4j.kernel.ha.HighlyAvailableGraphDatabase;
import org.neo4j.server.configuration.ServerConfigurator;
import org.neo4j.server.configuration.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Neo4j configuration for production use.
 *
 * @author Oliver Wetterau
 */
public class ExampleNeo4jConfigurer implements Configurer {
    protected static final Logger logger = LoggerFactory.getLogger(ExampleNeo4jConfigurer.class);

    /** Neo4j database instance */
    protected static HighlyAvailableGraphDatabase database = null;

    protected static Map<Setting,String> settings;
    /** storage path of the database files */
    protected static String storagePath = null;
    /** internal high availibity id in a cluster */
    protected static Integer highAvailabilityId = null;

    /** http port for the web gui */
    protected static Integer httpPort = null;
    /** https port for the web gui */
    protected static Integer httpsPort = null;

    @Override
    public void init(final Map<Setting,String> settings) {
        ExampleNeo4jConfigurer.settings = settings;

        highAvailabilityId = Integer.parseInt(settings.get(ClusterSettings.server_id));
        storagePath = settings.get(GraphDatabaseSettings.store_dir);

        httpPort = Integer.parseInt(settings.get(ServerSettings.webserver_port));
        httpsPort = Integer.parseInt(settings.get(ServerSettings.webserver_https_port));
    }

    @Override
    public GraphDatabaseService getGraphDatabase() {
        if (database == null) {
            logger.debug("[getGraphDatabase] storagePath == {}", storagePath);

            GraphDatabaseBuilder graphDatabaseBuilder = new HighlyAvailableGraphDatabaseFactory()
                    .newEmbeddedDatabaseBuilder(settings.get(GraphDatabaseSettings.store_dir));

            for (Map.Entry<Setting,String> parameter : settings.entrySet()) {
                if (parameter.getKey().equals(GraphDatabaseSettings.store_dir)) continue;

                graphDatabaseBuilder.setConfig(parameter.getKey(), parameter.getValue());

                logger.debug("[getGraphDatabase] {} = {}", parameter.getKey(), parameter.getValue());
            }

            database = (HighlyAvailableGraphDatabase) graphDatabaseBuilder.newGraphDatabase();
        }

        return database;
    }

    @Override
    public void dropGraphDatabase() {
        database.shutdown();
        database = null;
    }

    @Override
    public ServerConfigurator getServerConfigurator(final HighlyAvailableGraphDatabase databaseService) {
        ServerConfigurator configurator = new ServerConfigurator(databaseService);
        configurator.configuration().addProperty(ServerConfigurator.WEBSERVER_PORT_PROPERTY_KEY, httpPort);
        configurator.configuration().addProperty(ServerConfigurator.WEBSERVER_HTTPS_PORT_PROPERTY_KEY, httpsPort);
        configurator.configuration().addProperty(ServerConfigurator.WEBSERVER_ADDRESS_PROPERTY_KEY, "0.0.0.0");

        return configurator;
    }

    @Override
    public Integer getHighAvailabilityId() {
        return highAvailabilityId;
    }

    @Override
    public boolean isProductionDatabase() {
        return true;
    }
}

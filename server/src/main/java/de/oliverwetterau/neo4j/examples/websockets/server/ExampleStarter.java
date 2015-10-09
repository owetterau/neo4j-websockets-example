package de.oliverwetterau.neo4j.examples.websockets.server;

import de.oliverwetterau.neo4j.websockets.server.neo4j.Configurer;
import de.oliverwetterau.neo4j.websockets.server.neo4j.EmbeddedNeo4j;
import de.oliverwetterau.neo4j.websockets.server.neo4j.Starter;
import org.neo4j.graphdb.config.Setting;

import java.util.Map;

/**
 * Initialises the application by reading all annotations and by starting up the database.
 *
 * @author Oliver Wetterau
 */
public class ExampleStarter extends Starter {
    public ExampleStarter(Class embeddedNeo4jClass, Configurer configurer, Map<Setting,String> settings) throws Exception{
        super(embeddedNeo4jClass, configurer, settings);
    }

    @Override
    public void after(EmbeddedNeo4j embeddedNeo4j) {
        ((ExampleEmbeddedNeo4j) embeddedNeo4j).warmUp();
    }
}

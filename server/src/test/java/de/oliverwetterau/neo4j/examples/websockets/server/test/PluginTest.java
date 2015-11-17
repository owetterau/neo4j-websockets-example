package de.oliverwetterau.neo4j.examples.websockets.server.test;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.junit.Neo4jRule;

/**
 * Created by oliver on 16.11.15.
 */
public class PluginTest {
    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withConfig("websocket_packages", "de.oliverwetterau.neo4j.examples.websockets");

    @Test
    public void justATest() {
    }
}

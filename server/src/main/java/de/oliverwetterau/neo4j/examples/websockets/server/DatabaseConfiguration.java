package de.oliverwetterau.neo4j.examples.websockets.server;

import de.oliverwetterau.neo4j.websockets.server.neo4j.EmbeddedNeo4j;
import de.oliverwetterau.neo4j.websockets.server.neo4j.EmbeddedNeo4jBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {
    @Bean(destroyMethod = "stop")
    public EmbeddedNeo4j neo4jDatabase() {
        return EmbeddedNeo4jBuilder.getNeo4j();
    }
}

package de.oliverwetterau.neo4j.examples.websockets.client;

import de.oliverwetterau.neo4j.websockets.client.ApplicationSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan(basePackages = {"de.oliverwetterau.neo4j.examples.websockets.client", "de.oliverwetterau.neo4j.websockets"})
@EnableWebMvc
public class Application {
    public static void main(String[] arguments) throws Exception {
        ApplicationSettings.setServerURIs(System.getProperty("database.server.uris").split(","));
        ApplicationSettings.setBinaryCommunication(false);

        SpringApplication.run(Application.class, arguments);
    }
}

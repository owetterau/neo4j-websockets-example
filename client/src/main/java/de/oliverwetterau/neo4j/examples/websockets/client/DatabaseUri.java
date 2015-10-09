package de.oliverwetterau.neo4j.examples.websockets.client;

import de.oliverwetterau.neo4j.websockets.client.ServerUri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUri implements ServerUri {
    protected String[] uris;

    @Autowired
    public DatabaseUri(@Value("${database.server.uris}") String uri) {
        uris = uri.split(",");
    }

    public String[] getServerUris() {
        return uris;
    }
}

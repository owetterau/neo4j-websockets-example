package de.oliverwetterau.neo4j.examples.websockets.client;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import de.oliverwetterau.neo4j.websockets.core.data.json.JsonObjectSerializers;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExampleJsonSerializers implements JsonObjectSerializers {
    @Override
    public Map<Class, JsonSerializer> getSerializers() {
        return new HashMap<>();
    }

    @Override
    public Map<Class, JsonDeserializer> getDeserializers() {
        return new HashMap<>();
    }
}

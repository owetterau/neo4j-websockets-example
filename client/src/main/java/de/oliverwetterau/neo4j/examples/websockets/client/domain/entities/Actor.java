package de.oliverwetterau.neo4j.examples.websockets.client.domain.entities;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by oliver on 06.10.15.
 */
public class Actor {
    public final static String LASTNAME = "lastname";
    public final static String FIRSTNAME = "firstname";

    private String lastname;
    private String firstname;

    public Actor(String lastname, String firstname) {
        this.lastname = lastname;
        this.firstname = firstname;
    }

    public static Actor readFromJson(JsonNode jsonNode) {
        return new Actor(jsonNode.get(LASTNAME).asText(), jsonNode.get(FIRSTNAME).asText());
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }
}

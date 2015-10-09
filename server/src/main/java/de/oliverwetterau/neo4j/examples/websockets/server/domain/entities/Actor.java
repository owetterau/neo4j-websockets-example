package de.oliverwetterau.neo4j.examples.websockets.server.domain.entities;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

public class Actor {
    public static final Label ACTOR = DynamicLabel.label("actor");
    public static final String LASTNAME = "lastname";
    public static final String FIRSTNAME = "firstname";

    protected Node node;

    public Actor(Node node, String lastname, String firstname) {
        this.node = node;
        node.addLabel(ACTOR);
        node.setProperty(LASTNAME, lastname);
        node.setProperty(FIRSTNAME, firstname);
    }

    protected Actor(Node node) throws Exception {
        this.node = node;
        if (!node.hasLabel(ACTOR) || !node.hasProperty(LASTNAME) || !node.hasProperty(FIRSTNAME)) {
            throw new Exception("not an actor");
        }
    }

    public static Actor readFromNode(Node node) {
        Actor actor = null;

        try {
            actor = new Actor(node);
        }
        catch (Exception ignored) {}

        return actor;
    }

    public long getId() {
        return node.getId();
    }

    public String getLastname() {
        return (String) node.getProperty(LASTNAME);
    }

    public String getFirstname() {
        return (String) node.getProperty(FIRSTNAME);
    }
}

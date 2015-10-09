package de.oliverwetterau.neo4j.examples.websockets.server.domain.entities;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import java.util.Locale;

public class Movie {
    public static final Label MOVIE = DynamicLabel.label("movie");
    public static final String TITLE = "title";
    public static final String YEAR = "year";

    protected Node node;

    public Movie(Node node, String name, Integer year) {
        this.node = node;
        node.addLabel(MOVIE);
        node.setProperty(TITLE, name);
        node.setProperty(YEAR, year);
    }

    protected Movie(Node node) throws Exception {
        this.node = node;
        if (!node.hasLabel(MOVIE) || !node.hasProperty(TITLE) || !node.hasProperty(YEAR)) {
            throw new Exception("not a movie");
        }
    }

    public static Movie readMovie(Node node) {
        Movie movie = null;

        try {
            movie = new Movie(node);
        }
        catch (Exception ignored) {}

        return movie;
    }

    public long getId() {
        return node.getId();
    }

    public void setTitle(String title) {
        node.setProperty(TITLE, title);
    }

    public String getTitle() {
        return (String) node.getProperty(TITLE);
    }

    public void setTitle(Locale locale, String title) {
        node.setProperty(TITLE + "-" + locale.toString().toLowerCase(), title);
    }

    public String getTitle(Locale locale) {
        return (String) node.getProperty(TITLE + "-" + locale.toString().toLowerCase());
    }

    public Integer getYear() {
        return (Integer) node.getProperty(YEAR);
    }
}

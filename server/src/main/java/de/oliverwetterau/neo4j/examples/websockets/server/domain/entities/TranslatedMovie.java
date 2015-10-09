package de.oliverwetterau.neo4j.examples.websockets.server.domain.entities;

import org.neo4j.graphdb.Node;

import java.util.Locale;

public class TranslatedMovie {
    private Long id;
    private String title;
    private Integer year;

    protected TranslatedMovie(Node node) throws Exception {
        if (!node.hasLabel(Movie.MOVIE) || !node.hasProperty(Movie.TITLE) || !node.hasProperty(Movie.YEAR)) {
            throw new Exception("not a movie");
        }

        id = node.getId();
        year = (Integer) node.getProperty(Movie.YEAR);
    }

    public static TranslatedMovie readMovie(Node node, Locale locale) {
        TranslatedMovie movie;
        String localeString = locale.toString().toLowerCase();

        try {
            movie = new TranslatedMovie(node);
        }
        catch (Exception ignored) {
            return null;
        }

        if (node.hasProperty(Movie.TITLE + "-" + localeString)) {
            movie.title = (String) node.getProperty(Movie.TITLE + "-" + localeString);
        }
        else {
            movie.title = (String) node.getProperty(Movie.TITLE);
        }

        return movie;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getYear() {
        return year;
    }
}

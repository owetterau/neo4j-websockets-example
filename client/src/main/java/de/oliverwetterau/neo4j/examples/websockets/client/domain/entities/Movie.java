package de.oliverwetterau.neo4j.examples.websockets.client.domain.entities;

import com.fasterxml.jackson.databind.JsonNode;

public class Movie {
    public final static String TITLE = "title";
    public final static String YEAR = "year";

    private Long id;
    private String title;
    private Integer year;

    public Movie(String title, Integer year) {
        this.title = title;
        this.year = year;
    }

    public static Movie readFromJson(JsonNode jsonNode) {
        Movie movie = new Movie(jsonNode.get(TITLE).asText(), jsonNode.get(YEAR).asInt());
        movie.id = jsonNode.get("id").asLong();

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

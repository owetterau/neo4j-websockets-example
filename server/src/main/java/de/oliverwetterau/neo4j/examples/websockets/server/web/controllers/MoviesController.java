package de.oliverwetterau.neo4j.examples.websockets.server.web.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import de.oliverwetterau.neo4j.examples.websockets.server.domain.entities.Movie;
import de.oliverwetterau.neo4j.examples.websockets.server.domain.entities.TranslatedMovie;
import de.oliverwetterau.neo4j.examples.websockets.server.services.MoviesService;
import de.oliverwetterau.neo4j.websockets.core.data.Result;
import de.oliverwetterau.neo4j.websockets.server.annotations.MessageController;
import de.oliverwetterau.neo4j.websockets.server.annotations.MessageMethod;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

@MessageController("movies")
public class MoviesController {
    protected final MoviesService moviesService;

    @Autowired
    public MoviesController(MoviesService moviesService) {
        this.moviesService = moviesService;
    }

    @MessageMethod
    public Result<Movie> create(JsonNode jsonNode) {
        Map<Locale,String> translations = new HashMap<>();

        Iterator<Map.Entry<String,JsonNode>> iterator = jsonNode.fields();
        Map.Entry<String,JsonNode> field;

        while (iterator.hasNext()) {
            field = iterator.next();

            if (field.getKey().startsWith(Movie.TITLE + "-")) {
                String[] fieldName = field.getKey().split("-");
                translations.put(new Locale(fieldName[1]), field.getValue().asText());
            }
        }

        return moviesService.create(jsonNode.get(Movie.TITLE).asText(), jsonNode.get(Movie.YEAR).asInt(), translations);
    }

    @MessageMethod
    public Result<TranslatedMovie> getByYear(JsonNode jsonNode) {
        return moviesService.getByYear(jsonNode.get("year").asInt());
    }
}

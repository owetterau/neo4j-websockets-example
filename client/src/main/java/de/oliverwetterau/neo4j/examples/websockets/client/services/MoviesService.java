package de.oliverwetterau.neo4j.examples.websockets.client.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.oliverwetterau.neo4j.examples.websockets.client.domain.entities.Movie;
import de.oliverwetterau.neo4j.websockets.client.DatabaseService;
import de.oliverwetterau.neo4j.websockets.core.data.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class MoviesService {
    protected final static ObjectMapper objectMapper = new ObjectMapper();

    protected final DatabaseService databaseService;

    @Autowired
    public MoviesService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public Movie create(String title, Integer year, Map<Locale,String> translations) {
        Movie movie;
        ObjectNode parameters = objectMapper.createObjectNode();
        parameters.put(Movie.TITLE, title);
        parameters.put(Movie.YEAR, year);

        for (Map.Entry<Locale,String> translation : translations.entrySet()) {
            parameters.put(Movie.TITLE + "-" + translation.getKey(), translation.getValue());
        }

        Result<JsonNode> movieJsonResult = databaseService.writeDataWithResult("movies", "create", parameters);

        if (movieJsonResult.isOk()) {
            movie = Movie.readFromJson(movieJsonResult.getData().get(0));
        }
        else {
            return null;
        }

        return movie;
    }

    public List<Movie> getByYear(Integer year) {
        List<Movie> movies = new ArrayList<>();
        ObjectNode parameters = objectMapper.createObjectNode();
        parameters.put(Movie.YEAR, year);

        Result<JsonNode> actorJsonResult = databaseService.writeDataWithResult("movies", "getByYear", parameters);

        if (actorJsonResult.isOk()) {
            for (JsonNode jsonNode : actorJsonResult.getData()) {
                movies.add(Movie.readFromJson(jsonNode));
            }
        }
        else {
            return null;
        }

        return movies;
    }
}

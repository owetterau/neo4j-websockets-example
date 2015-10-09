package de.oliverwetterau.neo4j.examples.websockets.client.web.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import de.oliverwetterau.neo4j.examples.websockets.client.ExampleThreadLocale;
import de.oliverwetterau.neo4j.examples.websockets.client.domain.entities.Movie;
import de.oliverwetterau.neo4j.examples.websockets.client.services.MoviesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("movies")
public class MoviesController {
    protected MoviesService moviesService;
    protected ExampleThreadLocale exampleThreadLocale;

    @Autowired
    public MoviesController(MoviesService moviesService, ExampleThreadLocale exampleThreadLocale) {
        this.moviesService = moviesService;
        this.exampleThreadLocale = exampleThreadLocale;
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public Movie create(@RequestBody JsonNode jsonNode) {
        Map<Locale,String> translations = new HashMap<>();

        jsonNode.fields().forEachRemaining(field -> {
            if (field.getKey().startsWith(Movie.TITLE + "-")) {
                String[] fieldName = field.getKey().split("-");
                translations.put(new Locale(fieldName[1]), field.getValue().asText());
            }
        });

        return moviesService.create(jsonNode.get(Movie.TITLE).asText(), jsonNode.get(Movie.YEAR).asInt(), translations);
    }

    @RequestMapping(value = "getByYear/{year}/{locale}", method = RequestMethod.GET)
    public List<Movie> getByYear(@PathVariable Integer year, @PathVariable String locale) {
        exampleThreadLocale.setLocale(new Locale(locale));

        return moviesService.getByYear(year);
    }
}

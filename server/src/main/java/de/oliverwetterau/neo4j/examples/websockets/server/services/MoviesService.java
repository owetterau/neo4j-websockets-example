package de.oliverwetterau.neo4j.examples.websockets.server.services;

import de.oliverwetterau.neo4j.examples.websockets.server.ExampleThreadLocale;
import de.oliverwetterau.neo4j.examples.websockets.server.domain.entities.Movie;
import de.oliverwetterau.neo4j.examples.websockets.server.domain.entities.TranslatedMovie;
import de.oliverwetterau.neo4j.websockets.core.data.Result;
import de.oliverwetterau.neo4j.websockets.server.annotations.Transactional;
import de.oliverwetterau.neo4j.websockets.server.neo4j.EmbeddedNeo4j;
import de.oliverwetterau.neo4j.websockets.server.neo4j.ExceptionToErrorConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
public class MoviesService {
    protected final EmbeddedNeo4j neo4j;
    protected final ExceptionToErrorConverter exceptionToErrorConverter;
    protected final ExampleThreadLocale exampleThreadLocale;

    @Autowired
    public MoviesService(final EmbeddedNeo4j neo4J, final ExceptionToErrorConverter exceptionToErrorConverter, final ExampleThreadLocale exampleThreadLocale) {
        this.neo4j = neo4J;
        this.exceptionToErrorConverter = exceptionToErrorConverter;
        this.exampleThreadLocale = exampleThreadLocale;
    }

    @Transactional
    public Result<Movie> create(String name, Integer year, Map<Locale,String> translations) {
        Result<Movie> movieResult = new Result<>();
        Movie movie;

        movie = new Movie(neo4j.getDatabase().createNode(), name, year);

        for (Map.Entry<Locale,String> translation : translations.entrySet()) {
            movie.setTitle(translation.getKey(), translation.getValue());
        }

        movieResult.add(movie);

        return movieResult;
    }

    @Transactional
    public Result<TranslatedMovie> getByYear(Integer year) {
        Result<TranslatedMovie> movieResult = new Result<>();

        neo4j.getDatabase().findNodes(Movie.MOVIE, Movie.YEAR, year)
                .forEachRemaining(node -> movieResult.add(TranslatedMovie.readMovie(node, exampleThreadLocale.getLocale())));

        return movieResult;
    }
}

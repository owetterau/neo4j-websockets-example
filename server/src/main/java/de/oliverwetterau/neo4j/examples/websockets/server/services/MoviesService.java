package de.oliverwetterau.neo4j.examples.websockets.server.services;

import de.oliverwetterau.neo4j.examples.websockets.server.domain.entities.Movie;
import de.oliverwetterau.neo4j.examples.websockets.server.domain.entities.TranslatedMovie;
import de.oliverwetterau.neo4j.examples.websockets.server.web.ExampleThreadLocale;
import de.oliverwetterau.neo4j.websockets.core.data.Result;
import de.oliverwetterau.neo4j.websockets.server.annotations.Transactional;
import de.oliverwetterau.neo4j.websockets.server.neo4j.ExceptionToErrorConverter;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service
public class MoviesService {
    protected final GraphDatabaseService graphDatabaseService;
    protected final ExceptionToErrorConverter exceptionToErrorConverter;
    protected final ExampleThreadLocale exampleThreadLocale;

    @Autowired
    public MoviesService(final GraphDatabaseService graphDatabaseService,
                         final ExceptionToErrorConverter exceptionToErrorConverter,
                         final ExampleThreadLocale exampleThreadLocale)
    {
        this.graphDatabaseService = graphDatabaseService;
        this.exceptionToErrorConverter = exceptionToErrorConverter;
        this.exampleThreadLocale = exampleThreadLocale;
    }

    @Transactional
    public Result<Movie> create(String name, Integer year, Map<Locale,String> translations) {
        Result<Movie> movieResult = new Result<>();
        Movie movie;

        movie = new Movie(graphDatabaseService.createNode(), name, year);

        for (Map.Entry<Locale,String> translation : translations.entrySet()) {
            movie.setTitle(translation.getKey(), translation.getValue());
        }

        movieResult.add(movie);

        return movieResult;
    }

    @Transactional
    public Result<TranslatedMovie> getByYear(Integer year) {
        Result<TranslatedMovie> movieResult = new Result<>();

        ResourceIterator<Node> nodes = graphDatabaseService.findNodes(Movie.MOVIE, Movie.YEAR, year);
        Node node;

        while (nodes.hasNext()) {
            node = nodes.next();
            movieResult.add(TranslatedMovie.readMovie(node, exampleThreadLocale.getLocale()));
        }

        return movieResult;
    }
}

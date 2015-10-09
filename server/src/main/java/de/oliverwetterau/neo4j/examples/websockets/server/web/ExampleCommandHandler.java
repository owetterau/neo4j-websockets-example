package de.oliverwetterau.neo4j.examples.websockets.server.web;

import de.oliverwetterau.neo4j.examples.websockets.server.web.controllers.ActorsController;
import de.oliverwetterau.neo4j.examples.websockets.server.web.controllers.MoviesController;
import de.oliverwetterau.neo4j.websockets.core.data.json.JsonObjectMapper;
import de.oliverwetterau.neo4j.websockets.core.i18n.ThreadLocale;
import de.oliverwetterau.neo4j.websockets.server.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExampleCommandHandler extends CommandHandler {
    @Autowired
    public ExampleCommandHandler(JsonObjectMapper jsonObjectMapper, ThreadLocale threadLocale,
                                 MoviesController moviesController, ActorsController actorsController)
            throws Exception
    {
        super(ExampleCommandHandler.class.getPackage().getName(), jsonObjectMapper, threadLocale, true);

        setControllerInstance(moviesController);
        setControllerInstance(actorsController);
    }
}

package de.oliverwetterau.neo4j.examples.websockets.server.web.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import de.oliverwetterau.neo4j.examples.websockets.server.domain.entities.Actor;
import de.oliverwetterau.neo4j.examples.websockets.server.services.ActorsService;
import de.oliverwetterau.neo4j.websockets.core.data.Result;
import de.oliverwetterau.neo4j.websockets.server.annotations.MessageController;
import de.oliverwetterau.neo4j.websockets.server.annotations.MessageMethod;
import org.springframework.beans.factory.annotation.Autowired;

@MessageController("actors")
public class ActorsController {
    protected final ActorsService actorsService;

    @Autowired
    public ActorsController(ActorsService actorsService) {
        this.actorsService = actorsService;
    }

    @MessageMethod
    public Result<Actor> create(JsonNode jsonNode) {
        return actorsService.create(jsonNode.get(Actor.LASTNAME).asText(), jsonNode.get(Actor.FIRSTNAME).asText());
    }

    @MessageMethod
    public Result<Boolean> addToMovie(JsonNode jsonNode) {
        return actorsService.addToMovie(jsonNode.get("actorId").asLong(), jsonNode.get("movieId").asLong());
    }

    @MessageMethod
    public Result<Actor> getByMovie(JsonNode jsonNode) {
        return actorsService.getByMovie(jsonNode.get("movieId").asLong());
    }
}

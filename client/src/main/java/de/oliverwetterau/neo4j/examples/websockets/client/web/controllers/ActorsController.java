package de.oliverwetterau.neo4j.examples.websockets.client.web.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import de.oliverwetterau.neo4j.examples.websockets.client.domain.entities.Actor;
import de.oliverwetterau.neo4j.examples.websockets.client.services.ActorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("actors")
public class ActorsController {
    protected ActorsService actorsService;

    @Autowired
    public ActorsController(ActorsService actorsService) {
        this.actorsService = actorsService;
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(@RequestBody JsonNode jsonNode) throws Exception {
        return actorsService.create(jsonNode.get(Actor.LASTNAME).asText(), jsonNode.get(Actor.FIRSTNAME).asText()).toJsonString();
    }

    @RequestMapping(value = "addToMovie", method = RequestMethod.POST)
    public String addToMovie(@RequestBody JsonNode jsonNode) throws Exception {
        return actorsService.addToMovie(jsonNode.get("actorId").asLong(), jsonNode.get("movieId").asLong()).toJsonString();
    }

    @RequestMapping(value = "getByMovie/{id}", method = RequestMethod.GET)
    public String getByMovie(@PathVariable Long id) throws Exception {
        return actorsService.getByMovie(id).toJsonString();
    }
}

package de.oliverwetterau.neo4j.examples.websockets.client.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.oliverwetterau.neo4j.examples.websockets.client.domain.entities.Actor;
import de.oliverwetterau.neo4j.websockets.client.DatabaseService;
import de.oliverwetterau.neo4j.websockets.core.data.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActorsService {
    protected final static ObjectMapper objectMapper = new ObjectMapper();

    protected final DatabaseService databaseService;

    @Autowired
    public ActorsService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public Result<Actor> create(String lastname, String firstname) {
        Result<Actor> actorResult = new Result<>();
        ObjectNode parameters = objectMapper.createObjectNode();
        parameters.put(Actor.LASTNAME, lastname);
        parameters.put(Actor.FIRSTNAME, firstname);

        Result<JsonNode> actorJsonResult = databaseService.writeDataWithResult("actors", "create", parameters);

        if (actorJsonResult.isOk()) {
            actorResult.add(Actor.readFromJson(actorJsonResult.getData().get(0)));
        }
        else {
            actorResult.addErrors(actorJsonResult.getErrors());
        }

        return actorResult;
    }

    public Result<Boolean> addToMovie(long actorId, long movieId) {
        ObjectNode parameters = objectMapper.createObjectNode();
        parameters.put("actorId", actorId);
        parameters.put("movieId", movieId);

        Result<JsonNode> addToMovieResult = databaseService.writeDataWithResult("actors", "addToMovie", parameters);

        if (addToMovieResult.isOk()) {
            return new Result<>(true);
        }

        return new Result<>(false);
    }

    public Result<Actor> getByMovie(long movieId) {
        Result<Actor> actorResult = new Result<>();
        ObjectNode parameters = objectMapper.createObjectNode();
        parameters.put("movieId", movieId);

        Result<JsonNode> getByMovieJsonResult = databaseService.getData("actors", "getByMovie", parameters);

        if (getByMovieJsonResult.isOk()) {
            for (JsonNode jsonNode : getByMovieJsonResult.getData()) {
                actorResult.add(Actor.readFromJson(jsonNode));
            }
        }
        else {
            actorResult.addErrors(getByMovieJsonResult.getErrors());
        }

        return actorResult;
    }
}

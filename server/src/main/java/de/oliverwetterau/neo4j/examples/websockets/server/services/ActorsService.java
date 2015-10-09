package de.oliverwetterau.neo4j.examples.websockets.server.services;

import de.oliverwetterau.neo4j.examples.websockets.server.domain.entities.Actor;
import de.oliverwetterau.neo4j.examples.websockets.server.domain.entities.Movie;
import de.oliverwetterau.neo4j.websockets.core.data.Result;
import de.oliverwetterau.neo4j.websockets.server.neo4j.EmbeddedNeo4j;
import de.oliverwetterau.neo4j.websockets.server.neo4j.ExceptionToErrorConverter;
import org.neo4j.graphdb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActorsService {
    private final static RelationshipType ACTED_IN = DynamicRelationshipType.withName("ACTED_IN");

    protected final EmbeddedNeo4j neo4j;
    protected final ExceptionToErrorConverter exceptionToErrorConverter;

    @Autowired
    public ActorsService(final EmbeddedNeo4j neo4J, final ExceptionToErrorConverter exceptionToErrorConverter) {
        this.neo4j = neo4J;
        this.exceptionToErrorConverter = exceptionToErrorConverter;
    }

    public Result<Actor> create(String lastname, String firstname) {
        Result<Actor> actorResult = new Result<>();

        try (Transaction tx = neo4j.getDatabase().beginTx()) {
            actorResult.add(new Actor(neo4j.getDatabase().createNode(), lastname, firstname));
            actorResult.close();    // generate final json string before end of transaction

            tx.success();
        }
        catch (Exception e) {
            actorResult.add(exceptionToErrorConverter.convert(e));
        }

        return actorResult;
    }

    public Result<Boolean> addToMovie(long actorId, long movieId) {
        try (Transaction tx = neo4j.getDatabase().beginTx()) {
            Node actorNode = neo4j.getDatabase().getNodeById(actorId);
            Actor actor = Actor.readFromNode(actorNode);    // just to check that actor is valid

            Node movieNode = neo4j.getDatabase().getNodeById(movieId);
            Movie movie = Movie.readMovie(movieNode);   // just to check that movie is valid

            actorNode.createRelationshipTo(movieNode, ACTED_IN);

            tx.success();
        }
        catch (Exception e) {
            return new Result<>(exceptionToErrorConverter.convert(e));
        }

        return new Result<>(true);
    }

    public Result<Actor> getByMovie(long movieId) {
        Result<Actor> actorResult = new Result<>();

        try (Transaction tx = neo4j.getDatabase().beginTx()) {
            Node movieNode = neo4j.getDatabase().getNodeById(movieId);
            Movie movie = Movie.readMovie(movieNode);
            Actor actor;

            for (Relationship actedIn : movieNode.getRelationships(ACTED_IN)) {
                actor = Actor.readFromNode(actedIn.getEndNode());
                actorResult.add(actor);
            }

            actorResult.close();    // generate final json string before end of transaction

            tx.success();
        }
        catch (Exception e) {
            actorResult.add(exceptionToErrorConverter.convert(e));
        }

        return actorResult;
    }
}

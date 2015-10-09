package de.oliverwetterau.neo4j.examples.websockets.server;

import de.oliverwetterau.neo4j.websockets.server.neo4j.EmbeddedNeo4j;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.tooling.GlobalGraphOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for Neo4j database.
 *
 * @author Oliver Wetterau
 */
public class ExampleEmbeddedNeo4j extends EmbeddedNeo4j {
    protected static final Logger logger = LoggerFactory.getLogger(ExampleEmbeddedNeo4j.class);

    @Override
    public void startSchemaCreation(GraphDatabaseService databaseService) {
        try (Transaction tx = databaseService.beginTx()) {
            IndexManager indexManager = databaseService.index();

            if (!indexManager.existsForNodes("movies-fulltext")) {
                indexManager.forNodes(
                        "movieFulltextIndex",
                        MapUtil.stringMap(
                                IndexManager.PROVIDER, "lucene",
                                "type", "fulltext"
                        )
                );
            }

            tx.success();
        }
    }

    public void warmUp() {
        final int batchSize = 1000000;

        logger.debug("[warmUp] begin");

        int nodesCount = 0, relationshipsCount = 0;

        try (Transaction tx = getDatabase().beginTx()) {
            for (Node node : GlobalGraphOperations.at(getDatabase()).getAllNodes()) {
                if ((++nodesCount + relationshipsCount) % batchSize == 0) {
                    logger.debug("[warmUp] n = {}, r = {}", String.format("%,d", nodesCount), String.format("%,d", relationshipsCount));
                }

                for (String key : node.getPropertyKeys()) {
                    //noinspection ResultOfMethodCallIgnored
                    node.getProperty(key).toString();
                }

                node.getLabels();

                for (Relationship relationship : node.getRelationships(Direction.OUTGOING)) {
                    if ((++relationshipsCount + nodesCount) % batchSize == 0) {
                        logger.debug("[warmUp] n = {}, r = {}", String.format("%,d", nodesCount), String.format("%,d", relationshipsCount));
                    }

                    relationship.getOtherNode(node);
                    relationship.getType();

                    for (String key : relationship.getPropertyKeys()) {
                        //noinspection ResultOfMethodCallIgnored
                        relationship.getProperty(key).toString();
                    }
                }
            }

            tx.success();
        }

        logger.debug("[warmUp] finished => {} nodes, {} relationships", nodesCount, relationshipsCount);
    }
}

Readme
======

This example uses an embedded Undertow web server and produces a fat Jar containing all needed dependencies.

Sources for the framework used can be found here: [github - neo4j-websockets](https://github.com/owetterau/neo4j-websockets)

Jar files for the framework are available from Maven Central Repository:
[maven.org - neo4j-websockets](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.oliverwetterau.neo4j%22)

Setup for Your Application
--------------------------
### Configuration and Startup
This framework is a plugin for the server side. Hence, when using the provided example pom for maven, a fat Jar will be created that can be copied into the `plugins` directory of a Neo4j (2.3) server (community or enterprise).

To be able to pickup the functionality added by yourself, you must let the framework know which packages need to be scanned for Spring beans. To do this, simply add a line like the following to `conf/neo4j.properties`

```bash
websocket_packages=de.oliverwetterau.neo4j.examples.websockets.server
```

Incoming Messages
-----------------
All incoming messages should to be replied with a `Result` so that can easily do some error handling on the client
side.

```
@MessageMethod
public Result<TranslatedMovie> getByYear(JsonNode jsonNode) {
    return moviesService.getByYear(jsonNode.get("year").asInt());
}
```

The websocket framework provides a `@Transactional` annotation which creates a new Neo4j transaction if none is
active and closes that transaction after the message call. Any uncaught exceptions in method surrounded with that
annotation will be converted into an error that will be put into the `Result` (provided that method returns a
`Result` object).

```java
@Transactional
public Result<TranslatedMovie> getByYear(Integer year) {
    Result<TranslatedMovie> movieResult = new Result<>();

    graphDatabaseService.findNodes(Movie.MOVIE, Movie.YEAR, year)
            .forEachRemaining(node -> movieResult.add(TranslatedMovie.readMovie(node, exampleThreadLocale.getLocale())));

    return movieResult;
}
```

To use that functionality the application needs Aspectj compile time weaving as shown in the `pom.xml`.

If you do not want to use `@Transactional` you can use plain Neo4j transaction management:

```java
public Result<Actor> create(String lastname, String firstname) {
    Result<Actor> actorResult = new Result<>();

    try (Transaction tx = neo4j.getDatabase().beginTx()) {
        actorResult.add(new Actor(graphDatabaseService.createNode(), lastname, firstname));
        actorResult.close();    // generate final json string before end of transaction

        tx.success();
    }
    catch (Exception e) {
        actorResult.add(exceptionToErrorConverter.convert(e));
    }

    return actorResult;
}
```


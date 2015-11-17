Readme
======

This example uses Spring Boot with an embedded Undertow web server and produces an executable Jar.

Sources for the framework used can be found here: [github - neo4j-websockets](https://github.com/owetterau/neo4j-websockets)

Jar files for the framework are available from Maven Central Repository:
[maven.org - neo4j-websockets](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.oliverwetterau.neo4j%22)

Requirements
------------
To run this example to need at least one example server available.

Setup for Your Application
--------------------------
### Startup: Application
You need an entry point for an executable jar which includes the start up methods for Spring Boot.

```java
@SpringBootApplication
@ComponentScan(basePackages = {"de.oliverwetterau.neo4j.examples.websockets.client", "de.oliverwetterau.neo4j.websockets"})
@EnableWebMvc
public class Application {
    public static void main(String[] arguments) throws Exception {
        ApplicationSettings.setServerURIs(System.getProperty("database.server.uris").split(","));
        SpringApplication.run(Application.class, arguments);
    }
}
```

In this example the list of available servers is passed using a Java VM Option "database.server.uris".

### Result
The `Result` class is used for all results being returned from a Neo4j server. Its serialization behaviour can be changed by overwriting the default `ThreadLocale` and `JsonObjectSerializer` implementations.

#### Managing Locales for Results: ThreadLocale
The default locale for all request is `Locale.US` and is provided through `DefaultThreadLocale`. This default behaviour can be changed by creating a new class implementing the `ThreadLocale` interface. To override the default bean the new class must be annotated with `@Component` and `@Primary`.

Here is a simple example that changes the default locale from `Local.US` to `Locale.GERMAN`:

```java
@Primary
@Component
public class ExampleThreadLocale implements ThreadLocale {
    protected static ThreadLocal<Locale> threadLocal = new ThreadLocal<>();
    protected static Locale defaultLocale = Locale.GERMAN;

    @Override
    public void setLocale(Locale locale) {
        threadLocal.set(locale);
    }

    @Override
    public Locale getLocale() {
        Locale locale = threadLocal.get();

        return (locale == null) ? defaultLocale : locale;
    }
}
```

#### Custom Json Serialization for Results: JsonSerializers
The default serialization does not include any specific serializers of your application. If you are happy with that, then there is no need for change. Otherwise, you have the option to add custom serializers and deserializers.

Just create a class implementing the `JsonObjectSerializers` interface and override the default bean by annotating your class with `@Component` and `@Primary`:

```java
@Primary
@Component
public class ExampleJsonSerializers implements JsonObjectSerializers {
    @Override
    public Map<Class, JsonSerializer> getSerializers() {
        return new HashMap<>();
    }

    @Override
    public Map<Class, JsonDeserializer> getDeserializers() {
        return new HashMap<>();
    }
}
```

Calling the Server
------------------
In a Neo4j cluster only one server should be used for write access to get the best possible overall performance.

To allow the websocket framework to automatically chose the correct server, `DatabaseService` provides
different functions to access the database servers.

#### Call With Write Access

```java
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
```

#### Call With Read Access
```java
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
```

Settings
--------
#### Available Program Arguments
When executing the example Jar you can pass the following command line parameters to the call:

HTTP Port the web server shall listen to:
```
--server.port=8080
```

#### Available Java VM Options
Database server URI's that this client shall connect to using web sockets:
```
-Ddatabase.server.uris=ws://localhost:9090,ws://localhost:9191,ws://localhost:9292
```

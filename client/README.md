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
#### Start up: Application
You need an entry point for an executable jar which includes the start up methods for Spring Boot and for the embedded
Neo4j server.

```java
@SpringBootApplication
@ComponentScan(basePackages = {"de.oliverwetterau.neo4j.examples.websockets.client", "de.oliverwetterau.neo4j.websockets"})
@EnableWebMvc
public class Application {
    public static void main(String[] arguments) throws Exception {
        SpringApplication.run(Application.class, arguments);
    }
}
```

#### DatabaseUri
A bean implementing ```ServerUri``` must be provided so that the client knows to which servers it shall connect to.

```getServerUris``` must return a list of URIs pointing to a websocket (e.g. ws://localhost:9090).

```java
@Service
public class DatabaseUri implements ServerUri {
    protected String[] uris;

    @Autowired
    public DatabaseUri(@Value("${database.server.uris}") String uri) {
        uris = uri.split(",");
    }

    public String[] getServerUris() {
        return uris;
    }
}
```

In this example the list of available servers is passed using a Java VM Option "database.server.uris" being passed into
the constructor.

#### Managing Locales for Results: ExampleThreadLocale
A bean implementing the ```ThreadLocale``` interface must be provided.

Here is a simple example of how the locale that shall be used by the ```Result``` class.

If you don't need different locales just implement a class return your default locale all the time...

```java
@Service
public class ExampleThreadLocale implements ThreadLocale {
    protected static ThreadLocal<Locale> threadLocal = new ThreadLocal<>();
    protected static Locale defaultLocale = Locale.ENGLISH;

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

#### Custom Json Serialization for Results: ExampleJsonSerializers
A bean implementing the ```JsonObjectSerializers``` interface must be provided.

If you don't need custom (de-)serializers to be used by the ```Result``` class just return empty maps as shown in this
example.

```java
@Service
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

To allow the websocket framework to automatically chose the correct server, the ```DatabaseServer``` provides
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

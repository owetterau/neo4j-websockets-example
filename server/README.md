Readme
======

This example uses Spring Boot with an embedded Undertow web server and produces an executable Jar.

Sources for the framework used can be found here: [github - neo4j-websockets](https://github.com/owetterau/neo4j-websockets)

Jar files for the framework are available from Maven Central Repository:
[maven.org - neo4j-websockets](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.oliverwetterau.neo4j%22)

Setup for Your Application
----------------------------------
#### Start up: Application
You need an entry point for an executable jar which includes the start up methods for Spring Boot and for the embedded
Neo4j server.

```java
@SpringBootApplication
@ComponentScan(basePackages = {"de.oliverwetterau.neo4j.examples.websockets.server", "de.oliverwetterau.neo4j.websockets"})
public class Application extends SpringBootServletInitializer {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] arguments) throws Throwable {
        new ExampleStarter(ExampleEmbeddedNeo4j.class, new ExampleNeo4jConfigurer(), getNeo4jSettings(arguments)).run();

        SpringApplication.run(Application.class, arguments);
    }
}
```

SpringApplication.run is needed for the Spring Boot start up. For Neo4j this framework provides a ```Starter``` class
that must be called to create the Neo4j instance used by the websocket framework.

If needed, you can derive your own starter class from the provided ```Starter``` which in this example is the
```ExampleStarter``` class. Here it is necessary as this example also uses a custom ```EmbeddedNeo4j``` class
(```ExampleEmbeddedNeo4j```).

The ```Starter``` class needs an ```EmbeddedNeo4j```, ```Neo4jConfigurer``` and a
```Map<org.neo4j.graphdb.config.Setting,String>``` containing the settings for the Neo4j instance to be created. The
settings map in this example will be created by reading the program arguments given when executing the Jar
(see ```getNeo4jSettings()``` in ```Application.java```).

#### Neo4j Spring Bean: DatabaseConfiguration
As ```EmbeddedNeo4j``` must be available as a bean for the framework an easy way to accomplish this is using
```@Coniguration``` and ```@Bean``` e.g. with a class called DatabaseConfiguration.

To create a Neo4j instance based on the configuration classes provided and needed by the websocket framework you must
call ```getNeo4j()``` from the ```EmbeddedNeo4jBuilder``` class.

```java
@Configuration
public class DatabaseConfiguration {
    @Bean(destroyMethod = "stop")
    public EmbeddedNeo4j neo4jDatabase() {
        return EmbeddedNeo4jBuilder.getNeo4j();
    }
}
```

#### Neo4j Initialization: ExampleEmbeddedNeo4j
You can either use a vanilla ```EmbeddedNeo4j``` class or enrich an ```EmbeddedNeo4j``` class by deriving and e.g.
overriding the ```startSchemaCreation``` method which will be called the first time the Neo4j instance was started up.

You can also add additional methods to your derived class which can be called by a ```Starter``` class in the
constructor or in the ```before``` / ```after``` methods.

If you derive a class from ```EmbeddedNeo4j``` that class must be passed to the ```Starter``` class:
```new Starter(ExampleEmbbededNeo4j.class, new Neo4jConfigurer(), new HashMap<>())```. If not the call must use the
plain ```EmbeddedNeo4j``` class: ```new Starter(EmbbededNeo4j.class, new Neo4jConfigurer(), new HashMap<>())```.

```java
public class ExampleEmbeddedNeo4j extends EmbeddedNeo4j {
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
       // do something here
    }
}
```

#### Neo4j Start up: ExampleStarter
Like with ```EmbeddedNeo4j``` you can either use a vanilla ```Starter``` class or enrich it by deriving and overriding
the constructor or the ```before``` / ```after``` methods.

```java
public class ExampleStarter extends Starter {
    public ExampleStarter(Class embeddedNeo4jClass, Configurer configurer, Map<Setting,String> settings) throws Exception{
        super(embeddedNeo4jClass, configurer, settings);
    }

    @Override
    public void before(final Class embeddedNeo4jClass) {
    }

    @Override
    public void after(EmbeddedNeo4j embeddedNeo4j) {
        ((ExampleEmbeddedNeo4j) embeddedNeo4j).warmUp();
    }
}
```

In this example the ```warmUp``` method of the derived ```EmbeddedNeo4j``` class is being called after the creation of
the Neo4j instance.

#### Neo4j Instance: ExampleNeo4jConfigurer
This class is used by the websocket framework to create a Neo4j instance. It must be created by implementing the
```Configurer``` interface and must be passed to the ```Starter``` class as shown before.

```getGraphdatabase``` is used to the a link to the Neo4j instance throughout the framework. Hence, the actual Neo4j
instance should only created the first time this method is being called.

```dropDatabase``` is used to shutdown the Neo4j instance and therefore should also be called when the
```EmbbededNeo4j``` bean is destroyed (as shown in ```DatabaseConfiguration```).

```getServerConfigurator``` is used to create a Neo4j ServerConfigurator class that is needed to start the internal
web gui of Neo4j.

```getHighAvailability``` must return the cluster id of the Neo4j instance which is necessary for the client to identify
the server.

```ìsProductionDatabase``` must return true if you are not using an in-memory non-highly-available instance.

Basically, you can copy this class into your application as shown here if you don't have any special requirements for
the instance creation.

```java
public class ExampleNeo4jConfigurer implements Configurer {
    /** Neo4j database instance */
    protected static HighlyAvailableGraphDatabase database = null;

    protected static Map<Setting,String> settings;
    /** storage path of the database files */
    protected static String storagePath = null;
    /** internal high availibity id in a cluster */
    protected static Integer highAvailabilityId = null;

    /** http port for the web gui */
    protected static Integer httpPort = null;
    /** https port for the web gui */
    protected static Integer httpsPort = null;

    @Override
    public void init(final Map<Setting,String> settings) {
        ExampleNeo4jConfigurer.settings = settings;

        highAvailabilityId = Integer.parseInt(settings.get(ClusterSettings.server_id));
        storagePath = settings.get(GraphDatabaseSettings.store_dir);

        httpPort = Integer.parseInt(settings.get(ServerSettings.webserver_port));
        httpsPort = Integer.parseInt(settings.get(ServerSettings.webserver_https_port));
    }

    @Override
    public GraphDatabaseService getGraphDatabase() {
        if (database == null) {
            GraphDatabaseBuilder graphDatabaseBuilder = new HighlyAvailableGraphDatabaseFactory()
                    .newEmbeddedDatabaseBuilder(settings.get(GraphDatabaseSettings.store_dir));

            for (Map.Entry<Setting,String> parameter : settings.entrySet()) {
                if (parameter.getKey().equals(GraphDatabaseSettings.store_dir)) continue;

                graphDatabaseBuilder.setConfig(parameter.getKey(), parameter.getValue());
            }

            database = (HighlyAvailableGraphDatabase) graphDatabaseBuilder.newGraphDatabase();
        }

        return database;
    }

    @Override
    public void dropGraphDatabase() {
        database.shutdown();
        database = null;
    }

    @Override
    public ServerConfigurator getServerConfigurator(final HighlyAvailableGraphDatabase databaseService) {
        ServerConfigurator configurator = new ServerConfigurator(databaseService);
        configurator.configuration().addProperty(ServerConfigurator.WEBSERVER_PORT_PROPERTY_KEY, httpPort);
        configurator.configuration().addProperty(ServerConfigurator.WEBSERVER_HTTPS_PORT_PROPERTY_KEY, httpsPort);
        configurator.configuration().addProperty(ServerConfigurator.WEBSERVER_ADDRESS_PROPERTY_KEY, "0.0.0.0");

        return configurator;
    }

    @Override
    public Integer getHighAvailabilityId() {
        return highAvailabilityId;
    }

    @Override
    public boolean isProductionDatabase() {
        return true;
    }
}
```

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

If you don't need custom (de)serializers to be used by the ```Result``` class just return empty maps as shown in this
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

#### Handling Messages Coming in From Client: ExampleCommandHandler
For the server side to be able to pass messages from a client to the correct method you must create a Spring Service
by deriving from ```CommandHandler``` and overriding it's constructor.

You must make each service that can be called from the client known to the ```CommandHandler``` by calling ```setControllerInstance```.

Furthermore you must pass the base package name where your services reside in to ```CommandHandler``` for it to be able
to add all services annotated with ```@MessageController``` and all methods annotated with ```@MessageMethod``` to it's
internal list. Otherwise these methods will not be called.

```java
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
```

Incoming Messages
-----------------
All incoming messages should to be replied with a ```Result``` so that can easily do some error handling on the client
side.

```
@MessageMethod
public Result<TranslatedMovie> getByYear(JsonNode jsonNode) {
    return moviesService.getByYear(jsonNode.get("year").asInt());
}
```

The websocket framework provides a ```@Transactional``` annotation which creates a new Neo4j transaction if none is
active and closes that transaction after the message call. Any uncaught exceptions in method surrounded with that
annotation will be converted into an error that will be put into the ```Result``` (provided that method returns a
```Result``` object).

```java
@Transactional
public Result<TranslatedMovie> getByYear(Integer year) {
    Result<TranslatedMovie> movieResult = new Result<>();

    neo4j.getDatabase().findNodes(Movie.MOVIE, Movie.YEAR, year)
            .forEachRemaining(node -> movieResult.add(TranslatedMovie.readMovie(node, exampleThreadLocale.getLocale())));

    return movieResult;
}
```

To use that functionality the application needs to Aspectj compile time weaving as shown in the ```pom.xml```.

If you do not want to use ```@Transactional``` you can use plain Neo4j transaction management:

```java
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
```

Start Parameters
----------------
When executing the example Jar you can pass the following command line parameters to the call:

Port to be used for the websocket connection:
```
--server.port=9090
```

Path where the Neo4j database files shall be created:
```
storagePath=/opt/websockets/neo4j
```

High availability id of the Neo4j instance within a cluster:
```
ha.server_id=1
```

Communication URI's of all Neo4j instances in the cluster:
```
ha.initial_hosts=localhost:5001,localhost:5002,localhost:5003
```

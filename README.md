## Creating Elasticsearch instances for integration testing

In my latest project I have implemented all communication with my Elasticsearch cluster using the high level REST client. 
My next step was to setup and teardown an Elasticsearch instance automatically in order to facilitate proper integration testing. 
This article describes three different ways of doing so and discusses some of the pros and cons. Please refer to [this repository](https://gitlab.com/mrkrijgsman/elasticsearch-integration-testing) for implementations of all three methods.  

### [docker-maven-plugin](https://github.com/fabric8io/docker-maven-plugin)

This generic Docker plugin allows you to bind the starting and stopping of Docker containers to [Maven lifecycles](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html). 
You specify two blocks within the plugin; `configuration` and `executions`. 
In the `configuration` block, you choose the image that you want to run (Elasticsearch 6.3.0 in this case), the ports that you want to expose, a health check and any environment variables. See the snippet below for a complete example:

```
<plugin>
    <groupId>io.fabric8</groupId>
    <artifactId>docker-maven-plugin</artifactId>
    <version>${version.io.fabric8.docker-maven-plugin}</version>
    <configuration>
        <imagePullPolicy>always</imagePullPolicy>
        <images>
            <image>
                <alias>docker-elasticsearch-integration-test</alias>
                <name>docker.elastic.co/elasticsearch/elasticsearch:6.3.0</name>
                <run>
                    <namingStrategy>alias</namingStrategy>
                    <ports>
                        <port>9299:9200</port>
                        <port>9399:9300</port>
                    </ports>
                    <env>
                        <cluster.name>integration-test-cluster</cluster.name>
                    </env>
                    <wait>
                        <http>
                            <url>http://localhost:9299</url>
                            <method>GET</method>
                            <status>200</status>
                        </http>
                        <time>60000</time>
                    </wait>
                </run>
            </image>
        </images>
    </configuration>
    <executions>
        <execution>
            <id>docker:start</id>
            <phase>pre-integration-test</phase>
            <goals>
                <goal>start</goal>
            </goals>
        </execution>
        <execution>
            <id>docker:stop</id>
            <phase>post-integration-test</phase>
            <goals>
                <goal>stop</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
You can see that I've bound the plugin to the pre- and post-integration-test lifecycle phases. 
By doing so, the Elasticsearch container will be started just before any integration tests are ran and will be stopped after the integration tests have finished. 
I've used the [maven-failsafe-plugin](http://maven.apache.org/surefire/maven-failsafe-plugin/integration-test-mojo.html#includes) in order to trigger the execution of tests ending with `*IT.java` in the integration-test lifecycle. 

Since this is a generic Docker plugin, there is no special functionality to easily install Elasticsearch plugins that may be needed during your integration tests. 
You could however [create your own image with the required plugins](https://hub.docker.com/r/markkrijgsman/elasticsearch-analysis-plugins/~/dockerfile/) and pull that image during your integration tests.

The integration with IntelliJ is also not optimal. 
When running an `*IT.java` class, IntelliJ will not trigger the correct lifecycle phases and will attempt to run your integration test without creating the required Docker container. 
Before running an integration test from IntelliJ, you need to manually start the container from the "Maven projects" view by running the `docker:start` commando:  

[IMAGE]
  
After running, you will also need to run the `docker:stop` commando to kill the container that is still running. 
If you forget to kill the running container and want to run a `mvn clean install` later on it will fail, since the build will attempt to create a container on the same port - as far as I know, the plugin does not allow for random ports to be chosen.

**Pros:**
- Little setup, only requires configuration of one Maven plugin

**Cons:**
- No out of the box functionality to start the Elasticsearch instance on a random port
- No out of the box functionality to install extra Elasticsearch plugins
- Extra dependency in your build pipeline (Docker)
- IntelliJ does not trigger the correct lifecycle phases


### [elasticsearch-maven-plugin](https://github.com/alexcojocaru/elasticsearch-maven-plugin)
This second plugin does not require Docker and only needs some Maven configuration to get started. See the snippet below for a complete example:

```
<plugin>
    <groupId>com.github.alexcojocaru</groupId>
    <artifactId>elasticsearch-maven-plugin</artifactId>
    <version>${version.com.github.alexcojocaru.elasticsearch-maven-plugin}</version>
    <configuration>
        <version>${version.org.elastic}</version>
        <clusterName>integration-test-cluster</clusterName>
        <transportPort>9399</transportPort>
        <httpPort>9299</httpPort>
    </configuration>
    <executions>
        <execution>
            <id>start-elasticsearch</id>
            <phase>pre-integration-test</phase>
            <goals>
                <goal>runforked</goal>
            </goals>
        </execution>
        <execution>
            <id>stop-elasticsearch</id>
            <phase>post-integration-test</phase>
            <goals>
                <goal>stop</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Again, I've bound the plugin to the pre- and post-integration-test lifecycle phases in combination with the maven-failsafe-plugin.

This plugin provides a way of starting the Elasticsearch instance from IntelliJ in much the same way as the docker-maven-plugin. 
You can run the `elasticsearch:runforked` commando from the "Maven projects" view. 
However in my case, this started the container and then immediately exited. 
There is also no out of the box possibility of setting a random port for your instance. 
Of course, [there are solutions to this](https://github.com/alexcojocaru/elasticsearch-maven-plugin/issues/11#issuecomment-169175954) at the expense of having a somewhat more complex Maven configuration.

Overall, this is a plugin that seems to provide almost everything we need with a lot of configuration options. 
You can automatically [install Elasticsearch plugins](https://github.com/alexcojocaru/elasticsearch-maven-plugin#plugins) or even [bootstrap your instance with data](https://github.com/alexcojocaru/elasticsearch-maven-plugin#initScript).  

In practice I did have problems using the plugin in my build pipeline. 
Upon downloading the Elasticsearch zip the build would sometimes fail, or in other cases when attempting to download a plugin. 
Your mileage may vary, but this was reason for me to keep looking for another solution. Which brings me to plugin number three.

**Pros:**
- Little setup, only requires configuration of one Maven plugin
- No extra external dependencies
- High amount of configuration possible

**Cons:**
- No out of the box functionality to start the Elasticsearch instance on a random port
- Poor integration with IntelliJ
- Seems unstable


### [testcontainers-elasticsearch](https://github.com/dadoonet/testcontainers-java-module-elasticsearch)
This plugin is different from the other two. It uses a [Java testcontainer](https://www.testcontainers.org/) that you can configure through Java code. 
This gives you a lot of flexibility and requires no Maven configuration. 
Since there is no Maven configuration, it does require some work to make sure the Elasticsearch container is started and stopped at the correct moments. 

I have extended the standard `SpringJUnit4ClassRunner` class with my own `ElasticsearchSpringRunner`. 
In this runner, I have added a new JUnit RunListener named `JUnitExecutionListener`. 
This listener defines two methods `testRunStarted` and `testRunFinished` that enable me to start and stop the Elasticsearch container at the same points in time that the pre- and post-integration-test Maven phases would. 
See the snippet below for the implementation of the listener:

```
public class JUnitExecutionListener extends RunListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JUnitExecutionListener.class);
    private static final String ELASTICSEARCH_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch";
    private static final String ELASTICSEARCH_VERSION = "6.3.0";
    private static final String ELASTICSEARCH_HOST_PROPERTY = "nl.luminis.articles.maven.elasticsearch.host";
    private static final int ELASTICSEARCH_PORT = 9200;

    private ElasticsearchContainer container;

    @Override
    public void testRunStarted(Description description) {
        // Create a Docker Elasticsearch container when there is no existing host defined in default-test.properties.
        // Spring will use this property to configure the application when it starts.
        if (System.getProperty(ELASTICSEARCH_HOST_PROPERTY) == null) {
            LOGGER.debug("Create Elasticsearch container");
            int mappedPort = createContainer();
            System.setProperty(ELASTICSEARCH_HOST_PROPERTY, "localhost:" + mappedPort);
            String host = System.getProperty(ELASTICSEARCH_HOST_PROPERTY);
            RestAssured.basePath = "";
            RestAssured.baseURI = "http://" + host.split(":")[0];
            RestAssured.port = Integer.parseInt(host.split(":")[1]);
            LOGGER.debug("Created Elasticsearch container at {}", host);
        }
    }

    @Override
    public void testRunFinished(Result result) {
        if (container != null) {
            String host = System.getProperty(ELASTICSEARCH_HOST_PROPERTY);
            LOGGER.debug("Removing Elasticsearch container at {}", host);
            container.stop();
        }
    }

    private int createContainer() {
        container = new ElasticsearchContainer();
        container.withBaseUrl(ELASTICSEARCH_IMAGE);
        container.withVersion(ELASTICSEARCH_VERSION);
        container.withEnv("cluster.name", "integration-test-cluster");
        container.start();
        return container.getMappedPort(ELASTICSEARCH_PORT);
    }
}
```

It will create an Elasticsearch Docker container on a random port for use by the integration tests. 
The best thing about having this runner is that it works perfectly fine in IntelliJ. 
Simply right-click and run your `*IT.java` classes annotated with  `@RunWith(ElasticsearchSpringRunner.class)` and IntelliJ will use the listener to setup the Elasticsearch container.
This allows you to automate your build pipeline while still keeping developers happy.  

**Pros:**
- Neat integration with both Java and therefore your IDE

**Cons:**
- More complex initial setup
- Extra dependency in your build pipeline (Docker)

In summary, all three of the above plugins are able to realize the goal of starting an Elasticsearch instance for your integration testing. 
For me personally, I will be using the testcontainers-elasticsearch plugin going forward. 
The extra Docker dependency is not a problem since I use Docker in most of my build pipelines anyway. 
Furthermore, the integration with Java allows me to configure things in such a way that it works perfectly fine from both the command line and the IDE.  

Feel free to [checkout the code behind this article](https://gitlab.com/mrkrijgsman/elasticsearch-integration-testing), play around with the integration tests that I've setup there and decide for yourself which plugin suits your needs best.





package nl.luminis.articles.elasticsearch.integration.testcontainers;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pilato.elasticsearch.containers.ElasticsearchContainer;
import io.restassured.RestAssured;

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

package nl.luminis.articles.elasticsearch.integration.testcontainers;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

@Slf4j
public class JUnitExecutionListener extends RunListener {

    private static final String ELASTICSEARCH_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:6.5.3";
    private static final String ELASTICSEARCH_HOST_PROPERTY = "spring.elasticsearch.rest.uris";

    private ElasticsearchContainer container;
    private RunNotifier notifier;

    public JUnitExecutionListener(RunNotifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void testRunStarted(Description description) {
        try {
            if (System.getProperty(ELASTICSEARCH_HOST_PROPERTY) == null) {
                log.debug("Create Elasticsearch container");
                createContainer();
                container = createContainer();
                System.setProperty(ELASTICSEARCH_HOST_PROPERTY, container.getHttpHostAddress());
                String host = System.getProperty(ELASTICSEARCH_HOST_PROPERTY);
                RestAssured.basePath = "";
                RestAssured.baseURI = "http://" + host.split(":")[0];
                RestAssured.port = Integer.parseInt(host.split(":")[1]);
                log.debug("Created Elasticsearch container at {}", host);
            }
        } catch (Exception e) {
            notifier.pleaseStop();
            throw e;
        }
    }

    @Override
    public void testRunFinished(Result result) {
        if (container != null) {
            String host = System.getProperty(ELASTICSEARCH_HOST_PROPERTY);
            log.debug("Removing Elasticsearch container at {}", host);
            container.stop();
        }
    }

    private ElasticsearchContainer createContainer() {
        ElasticsearchContainer container = new ElasticsearchContainer(ELASTICSEARCH_IMAGE);
        container.withEnv("cluster.name", "integration-test-cluster");
        container.start();
        return container;
    }
}

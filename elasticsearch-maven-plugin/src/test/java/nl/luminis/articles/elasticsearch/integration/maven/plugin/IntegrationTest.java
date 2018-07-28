package nl.luminis.articles.elasticsearch.integration.maven.plugin;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestElasticSearchApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource({ "classpath:default-test.properties" })
public abstract class IntegrationTest {

    @Value("${nl.luminis.articles.maven.elasticsearch.host:#{\"localhost:9200\"}}")
    public void setHost(String host) {
        RestAssured.basePath = "";
        RestAssured.baseURI = "http://" + host.split(":")[0];
        RestAssured.port = Integer.parseInt(host.split(":")[1]);
    }
}

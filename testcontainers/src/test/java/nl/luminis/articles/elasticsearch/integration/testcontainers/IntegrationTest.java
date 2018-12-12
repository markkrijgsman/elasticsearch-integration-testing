package nl.luminis.articles.elasticsearch.integration.testcontainers;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@RunWith(ElasticsearchSpringRunner.class)
@SpringBootTest(classes = ContainersApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource({ "classpath:application-test.properties" })
public abstract class IntegrationTest {

}

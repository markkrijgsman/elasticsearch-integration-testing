package nl.luminis.articles.elasticsearch.integration.testcontainers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("nl.luminis.articles.elasticsearch.integration")
public class ContainersApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContainersApplication.class, args);
    }
}

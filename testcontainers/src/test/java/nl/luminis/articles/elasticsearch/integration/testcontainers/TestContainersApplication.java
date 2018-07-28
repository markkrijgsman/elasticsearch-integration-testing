package nl.luminis.articles.elasticsearch.integration.testcontainers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan("nl.luminis.articles.elasticsearch.integration")
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ContainersApplication.class))
public class TestContainersApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContainersApplication.class, args);
    }
}

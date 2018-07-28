package nl.luminis.articles.elasticsearch.integration.docker.plugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan("nl.luminis.articles.elasticsearch.integration")
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = DockerPluginApplication.class))
public class TestDockerPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerPluginApplication.class, args);
    }
}

package nl.luminis.articles.elasticsearch.integration.docker.plugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan("nl.luminis.articles.elasticsearch.integration")
@PropertySource(value = "classpath:default.properties")
public class DockerPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerPluginApplication.class, args);
    }
}

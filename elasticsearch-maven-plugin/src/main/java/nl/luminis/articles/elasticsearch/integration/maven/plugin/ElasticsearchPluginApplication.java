package nl.luminis.articles.elasticsearch.integration.maven.plugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan("nl.luminis.articles.elasticsearch.integration")
public class ElasticsearchPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchPluginApplication.class, args);
    }
}

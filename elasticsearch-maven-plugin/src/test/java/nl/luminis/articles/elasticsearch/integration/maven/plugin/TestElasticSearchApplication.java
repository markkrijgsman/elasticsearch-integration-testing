package nl.luminis.articles.elasticsearch.integration.maven.plugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan("nl.luminis.articles.elasticsearch.integration")
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ElasticsearchPluginApplication.class))
public class TestElasticSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchPluginApplication.class, args);
    }
}

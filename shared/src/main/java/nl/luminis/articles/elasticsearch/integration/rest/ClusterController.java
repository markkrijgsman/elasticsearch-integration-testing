package nl.luminis.articles.elasticsearch.integration.rest;

import java.io.IOException;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cluster")
public class ClusterController {

    private final RestHighLevelClient client;

    @Autowired
    public ClusterController(RestHighLevelClient client) {
        this.client = client;
    }

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClusterHealthResponse health() throws IOException {
        return client.cluster().health(new ClusterHealthRequest(), RequestOptions.DEFAULT);
    }
}

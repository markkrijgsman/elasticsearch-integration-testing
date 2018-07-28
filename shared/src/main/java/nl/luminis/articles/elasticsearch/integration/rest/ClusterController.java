package nl.luminis.articles.elasticsearch.integration.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;
import nl.luminis.articles.elasticsearch.integration.dto.ClusterHealth;

import org.apache.http.HttpEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/cluster")
public class ClusterController {

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    @Autowired
    public ClusterController(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClusterHealth health() throws IOException {
        Response response = client.getLowLevelClient().performRequest(GET.name(), "/_cluster/health");
        HttpEntity entity = response.getEntity();
        return objectMapper.readValue(entity.getContent(), ClusterHealth.class);

    }
}

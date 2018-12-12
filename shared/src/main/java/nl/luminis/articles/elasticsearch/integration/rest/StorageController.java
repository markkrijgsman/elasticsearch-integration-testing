package nl.luminis.articles.elasticsearch.integration.rest;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.luminis.articles.elasticsearch.integration.dto.Document;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    public static final String INDEX_NAME = "test_index";
    public static final String INDEX_TYPE = "doc";
    private static final TypeReference<Map<String, Object>> JSON_MAP = new TypeReference<Map<String, Object>>() {};

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;

    @Autowired
    public StorageController(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @PutMapping(value = "store", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void store(@RequestBody Document document) throws IOException {
        Map<String, Object> jsonMap = objectMapper.readValue(objectMapper.writeValueAsString(document), JSON_MAP);

        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest(INDEX_NAME, INDEX_TYPE, document.getId()).source(jsonMap));
        client.bulk(request, RequestOptions.DEFAULT);
    }
}

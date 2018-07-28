package nl.luminis.articles.elasticsearch.integration.maven.plugin.rest;

import static io.restassured.RestAssured.given;
import static nl.luminis.articles.elasticsearch.integration.rest.StorageController.INDEX_NAME;
import static nl.luminis.articles.elasticsearch.integration.rest.StorageController.INDEX_TYPE;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.restassured.http.ContentType;
import nl.luminis.articles.elasticsearch.integration.maven.plugin.IntegrationTest;
import nl.luminis.articles.elasticsearch.integration.rest.StorageController;

public class StorageControllerIT extends IntegrationTest {

    @Autowired
    private StorageController storageController;

    @Test
    public void testStore() throws Exception {
        String document = "{ \"id\": \"1\", \"description\":\"description text\"}";

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(storageController).build();
        mockMvc.perform(put("/api/storage/store").content(document).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(() -> contains(INDEX_NAME + "/" + INDEX_TYPE + "/" + 1, document));
    }

    private static boolean contains(String path, String payload) {
        try {
            given().contentType(ContentType.JSON).accept(ContentType.JSON).body(payload).when().get(path).then().body("found", is(true));
            return true;
        } catch (AssertionError ex) {
            return false;
        }
    }
}

package nl.luminis.articles.elasticsearch.integration.maven.plugin.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.gson.Gson;

import nl.luminis.articles.elasticsearch.integration.dto.ClusterHealth;
import nl.luminis.articles.elasticsearch.integration.maven.plugin.IntegrationTest;
import nl.luminis.articles.elasticsearch.integration.rest.ClusterController;

public class ClusterControllerIT extends IntegrationTest {

    @Autowired
    private ClusterController clusterController;

    @Test
    public void testClusterHealth() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(clusterController).build();

        MvcResult result = mockMvc.perform(get("/api/cluster/health").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        String content = result.getResponse().getContentAsString();
        ClusterHealth health = new Gson().fromJson(content, ClusterHealth.class);

        assertThat(health.getStatus()).isEqualTo("green");
    }
}

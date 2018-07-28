package nl.luminis.articles.elasticsearch.integration.testcontainers.rest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.luminis.articles.elasticsearch.integration.dto.ClusterHealth;
import nl.luminis.articles.elasticsearch.integration.rest.ClusterController;

@RunWith(MockitoJUnitRunner.class)
public class ClusterControllerTest {

    @InjectMocks
    private ClusterController clusterController;
    @Mock
    private RestHighLevelClient client;
    @Mock
    private ObjectMapper mapper;

    @Test
    public void testClusterHealth() throws Exception {
        RestClient lowLevelClient = mock(RestClient.class);
        Response response = mock(Response.class);
        HttpEntity entity = mock(HttpEntity.class);
        InputStream stream = mock(InputStream.class);

        when(client.getLowLevelClient()).thenReturn(lowLevelClient);
        when(lowLevelClient.performRequest(anyString(), anyString())).thenReturn(response);
        when(response.getEntity()).thenReturn(entity);
        when(entity.getContent()).thenReturn(stream);

        clusterController.health();

        verify(lowLevelClient, times(1)).performRequest(anyString(), anyString());
        verify(mapper, times(1)).readValue(stream, ClusterHealth.class);
    }
}

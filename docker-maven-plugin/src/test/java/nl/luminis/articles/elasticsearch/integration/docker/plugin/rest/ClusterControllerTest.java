package nl.luminis.articles.elasticsearch.integration.docker.plugin.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.ClusterClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import nl.luminis.articles.elasticsearch.integration.rest.ClusterController;

@RunWith(MockitoJUnitRunner.class)
public class ClusterControllerTest {

    @InjectMocks
    private ClusterController clusterController;
    @Mock
    private RestHighLevelClient client;

    @Test
    public void testClusterHealth() throws Exception {
        ClusterClient clusterClient = mock(ClusterClient.class);
        ClusterHealthResponse expectedResponse = mock(ClusterHealthResponse.class);

        when(client.cluster()).thenReturn(clusterClient);
        when(clusterClient.health(any(ClusterHealthRequest.class), eq(RequestOptions.DEFAULT))).thenReturn(expectedResponse);

        ClusterHealthResponse actualResponse = clusterController.health();

        verify(clusterClient, times(1)).health(any(ClusterHealthRequest.class), eq(RequestOptions.DEFAULT));
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}

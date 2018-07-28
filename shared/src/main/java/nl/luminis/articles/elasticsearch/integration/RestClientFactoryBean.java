package nl.luminis.articles.elasticsearch.integration;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Service;

@Service
public class RestClientFactoryBean extends AbstractFactoryBean<RestHighLevelClient> {

    private static final String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    private static final String DEFAULT_HEADER_CONTENT_TYPE = "application/json";

    private String[] hostnames;

    @Override
    public Class<?> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    protected RestHighLevelClient createInstance() {
        HttpHost[] hosts = new HttpHost[hostnames.length];
        for (int i = 0; i < hosts.length; i++) {
            logger.info("Create Elasticsearch bean for host " + hostnames[i]);
            hosts[i] = HttpHost.create(hostnames[i]);
        }
        Header[] defaultHeaders = new Header[]{new BasicHeader(HEADER_CONTENT_TYPE_KEY, DEFAULT_HEADER_CONTENT_TYPE)};

        RestClientBuilder lowLevelClient = RestClient.builder(hosts).setDefaultHeaders(defaultHeaders).setFailureListener(new LoggingFailureListener());
        return new RestHighLevelClient(lowLevelClient);
    }

    @Override
    protected void destroyInstance(RestHighLevelClient instance) {
        try {
            logger.info("Closing instance");
            instance.close();
        } catch (IOException e) {
            logger.warn("Failed to close instance", e);
        }
    }

    @Value("${nl.luminis.articles.maven.elasticsearch.host:#{\"localhost:9200\"}}")
    public void setHostnames(String[] hostnames) {
        this.hostnames = hostnames;
    }

    private class LoggingFailureListener extends RestClient.FailureListener {

        @Override
        public void onFailure(HttpHost host) {
            logger.warn("The following host just failed " + host.getHostName() + ":" + host.getPort());
        }
    }
}


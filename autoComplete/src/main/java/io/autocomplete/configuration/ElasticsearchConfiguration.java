package io.autocomplete.configuration;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static io.autocomplete.constants.AppConstants.*;
/**
 * It initializes the RestHighLevelClient object with the Elasticsearch host, port, scheme, username and password
 */
@Slf4j
@Configuration
@SuppressWarnings("deprecation")
public class ElasticsearchConfiguration {

    public RestHighLevelClient restHighLevelClient;
    public SearchSourceBuilder builder;
    public SearchRequest request = new SearchRequest(ELASTIC_INDEX);
    public Gson gson;

    /**
     * The above function initializes the RestHighLevelClient object with the Elasticsearch host, port, scheme, username
     * and password
     */
    @PostConstruct
    public void init() {
        log.info("RestHighLevelClient: Host: " + ELASTIC_HOST + " Port: " + ELASTIC_PORT);

            log.info("localhost is not Elastic_Host");
            RestClientBuilder clientBuilder = RestClient
                    .builder(new HttpHost(ELASTIC_HOST, ELASTIC_PORT, ELASTIC_SCHEME));


            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(ELASTIC_USER, ELASTIC_PASSWORD));

            clientBuilder.setHttpClientConfigCallback(
                    httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
            clientBuilder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(40000).setSocketTimeout(50000));

            this.restHighLevelClient = new RestHighLevelClient(clientBuilder);

        try {
            log.info("Pinging the elasticsearch Database");
            boolean pingResult = restHighLevelClient.ping(RequestOptions.DEFAULT);
            log.info("PingResult:: " + pingResult);
        } catch (Exception e) {
            log.error("Exception while pinging:: ",e);
        }
        builder = new SearchSourceBuilder();
        gson = new Gson();
    }
}
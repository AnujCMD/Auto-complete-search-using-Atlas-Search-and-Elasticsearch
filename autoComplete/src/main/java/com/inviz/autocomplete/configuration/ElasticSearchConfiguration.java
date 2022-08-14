package com.inviz.autocomplete.configuration;

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

import static com.inviz.autocomplete.constants.AppConstants.*;
@SuppressWarnings("deprecation")
@Slf4j
/**
 * It initializes the RestHighLevelClient object with the Elasticsearch host and port. If the host is not localhost, then
 * it sets the credentials for the Elasticsearch server
 */
@Configuration
public class ElasticSearchConfiguration {

    public RestHighLevelClient restHighLevelClient;
    public SearchSourceBuilder searchSourceBuilder;
    public SearchRequest request = new SearchRequest(ELASTIC_INDEX);
    public Gson gson;

    /**
     * The above function initializes the RestHighLevelClient object with the Elasticsearch host and port. If the host is
     * not localhost, then it sets the credentials for the Elasticsearch server
     */
    @PostConstruct
    public void init() {
        log.info("[Elasticsearch][Config]RestHighLevelClient: Host: " + ELASTIC_HOST + " Port: " + ELASTIC_PORT);
            RestClientBuilder builder = RestClient
                    .builder(new HttpHost(ELASTIC_HOST, ELASTIC_PORT, ELASTIC_SCHEME));
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(ELASTIC_USER, ELASTIC_PASSWORD));

            builder.setHttpClientConfigCallback(
                    httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
            builder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(40000).setSocketTimeout(50000));

            this.restHighLevelClient = new RestHighLevelClient(builder);
        try {
            log.info("[Elasticsearch][Config]Pinging the elasticsearch Database");
            boolean pingResult = restHighLevelClient.ping(RequestOptions.DEFAULT);
            log.info("[Elasticsearch][Config]PingResult:: " + pingResult);
        } catch (Exception e) {
            log.error("[Elasticsearch][Config]Exception while pinging:: ",e);
        }
        searchSourceBuilder = new SearchSourceBuilder();
        gson = new Gson();
    }
}

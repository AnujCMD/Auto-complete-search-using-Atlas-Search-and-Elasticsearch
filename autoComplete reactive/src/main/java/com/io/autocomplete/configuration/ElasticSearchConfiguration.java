package com.io.autocomplete.configuration;

import com.google.gson.Gson;
import com.io.autocomplete.constants.AppConstants;
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
    public SearchRequest request = new SearchRequest(AppConstants.ELASTIC_INDEX);
    public Gson gson;

    /**
     * The above function initializes the RestHighLevelClient object with the Elasticsearch host and port. If the host is
     * not localhost, then it sets the credentials for the Elasticsearch server
     */
    @PostConstruct
    public void init() {
        log.info("[Elasticsearch][Config]RestHighLevelClient: Host: " + AppConstants.ELASTIC_HOST + " Port: " + AppConstants.ELASTIC_PORT);
            RestClientBuilder builder = RestClient
                    .builder(new HttpHost(AppConstants.ELASTIC_HOST, AppConstants.ELASTIC_PORT, AppConstants.ELASTIC_SCHEME));
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(AppConstants.ELASTIC_USER, AppConstants.ELASTIC_PASSWORD));

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

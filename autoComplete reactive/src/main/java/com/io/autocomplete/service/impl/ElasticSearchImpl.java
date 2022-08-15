package com.io.autocomplete.service.impl;

import com.io.autocomplete.configuration.ElasticSearchConfiguration;
import com.io.autocomplete.model.AutoCompleteResponse;
import com.io.autocomplete.model.Keyword;
import com.io.autocomplete.model.SearchDB;
import com.io.autocomplete.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * It's a service class that implements the SearchDB interface. It has a getKeywords method that takes a string as input
 * and returns a Mono<Keyword> object
 */
/**
 * It takes a string as input, and returns a list of strings as output
 */
@ConditionalOnProperty(prefix = "search", name = "engine", havingValue = "elastic")
@Slf4j
@Service
public class ElasticSearchImpl extends SearchDB {

    @Autowired
    ElasticSearchConfiguration elasticsearchConfiguration;
    private final ReactiveHashOperations<String, String, AutoCompleteResponse> hashOperations;
    ElasticSearchImpl(ReactiveRedisOperations<String, AutoCompleteResponse> redisOperations) {
        this.hashOperations = redisOperations.opsForHash();
    }
    /**
     * It takes a string as input, and returns a list of strings as output
     *
     * @param term The search term that the user has entered.
     * @return A list of suggestions for the given term.
     */
    @Override
    public Mono<AutoCompleteResponse> getKeywords(String term){
        List<Keyword> data = new ArrayList<>();
        try{
            if(term.length()==0){
                log.info("[ElasticSearchDB] String length is 0");
                throw new IOException();
            }
            if(term.length()<2){
                AutoCompleteResponse ans =new AutoCompleteResponse(term,new ArrayList<>());
                return  hashOperations.put(AppConstants.REDIS_KEY_ELASTIC, term, ans).map(isSaved ->ans).thenReturn(ans);
            }
            try {
                boolean hasKey = Boolean.TRUE.equals(hashOperations.hasKey(AppConstants.REDIS_KEY_ELASTIC, term).block());
                if (hasKey) {
                    log.info("Used Redis in Mongo for " + term);
                    return hashOperations.get(AppConstants.REDIS_KEY_ELASTIC, term);
                }
            }
            catch(Exception e){
                log.info("[ElasticsearchDB][REDIS] Error caught while fetching data in redis "+e);
            }
            getResponse(AppConstants.ELASTIC_TEMPLATE, data, term, "searchTerm", "match_phrase_prefix");
            log.info("[ElasticSearchDB][PrefixMatch] Suggestion found from Prefix:: "+data.size());
        if(data.size()<10){
            int termFound = data.size();
            log.info("[ElasticSearchDB][WildCard] Suggestions to be found from WildCard Query:: "+(10-data.size()));
            log.info("[ElasticSearchDB][WildCard]Term passed in Wildcard query:: " +"*"+term+"*");
                getResponse(AppConstants.ELASTIC_TEMPLATE, data, "*"+term+"*", "searchTerm", "wildcard");
            log.info("[ElasticSearchDB][WildCard] Suggestions found from WildCard Query:: "+(data.size()-termFound));
        }
        if(data.size()<10){
                int termFound = data.size();
                log.info("[ElasticSearchDB][Fuzziness] Suggestions to be found from Fuzzy Query "+(10-data.size()));
                getResponse(AppConstants.ELASTIC_TEMPLATE_FUZZY,data, term, "searchTerm", "fuzzy");
                log.info("[ElasticSearchDB][Fuzziness] Suggestions found from Fuzzy Query "+(data.size()-termFound));
            }
    }
        catch(Exception exception){
            log.warn("[ElasticSearchDB] Exception is found:: " + exception);
        }
        List<String> suggestions = new ArrayList<>();
        for (Keyword datum : data) {
            suggestions.add(datum.getSearchTerm());
        }
        log.info("[ElasticSearchDB] Response Found:: "+suggestions);
        AutoCompleteResponse results=new AutoCompleteResponse(term, suggestions);
        return hashOperations.put(AppConstants.REDIS_KEY_ELASTIC, term, results).map(isSaved ->results).thenReturn(results);
    }
    /**
     * It takes a template name, a list of Keyword objects, and a search term as input and returns a list of Keyword
     * objects as output
     *
     * @param template The name of the template that you want to use.
     * @param data This is the list of Keyword objects that will be returned to the caller.
     * @param term The search term that the user has entered.
     */
    public void getResponse(String template, List<Keyword> data, String term, String field, String queryType) {
        try {
            SearchTemplateRequest searchTemplateRequest = new SearchTemplateRequest();
            searchTemplateRequest.setScriptType(ScriptType.STORED);
            searchTemplateRequest.setScript(template);
            searchTemplateRequest.setRequest(elasticsearchConfiguration.request);
            Map<String, Object> scriptParams = new HashMap<>();
            scriptParams.put("query_type", queryType);
            scriptParams.put("field_type", field);
            scriptParams.put("field", term);
            log.info("[ElasticSearchDB][insideGetResponse] Script Params Map :: " + scriptParams);
            searchTemplateRequest.setScriptParams(scriptParams);
            SearchTemplateResponse searchTemplateResponse = this.elasticsearchConfiguration.restHighLevelClient.searchTemplate(searchTemplateRequest, RequestOptions.DEFAULT);
            SearchResponse response = searchTemplateResponse.getResponse();
            SearchHits responseHits;
            responseHits = response.getHits();
            SearchHit[] hits;
            hits = responseHits.getHits();
            for (SearchHit hit : hits) {
                if(data.size()==10)
                    break;
                String sourceAsString = hit.getSourceAsString();
                log.info("[ElasticSearchDB][insideGetResponse] Json Response:: " + sourceAsString);
                Keyword fromJson = elasticsearchConfiguration.gson.fromJson(sourceAsString, Keyword.class);
                log.info("[ElasticSearchDB][insideGetResponse] Json to Object:: " + fromJson);
                data.add(fromJson);
            }
            log.info("Final data is:: "+data);
        }
        catch (Exception e){
            log.warn("[Elasticsearch][insideGetResponse] Exception Found: "+e);
        }
    }
}
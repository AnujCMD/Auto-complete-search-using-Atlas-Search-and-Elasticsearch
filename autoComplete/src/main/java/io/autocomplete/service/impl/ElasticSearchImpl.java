package io.autocomplete.service.impl;

import io.autocomplete.configuration.ElasticsearchConfiguration;
import io.autocomplete.model.AutoCompleteResponse;
import io.autocomplete.model.Keyword;
import io.autocomplete.model.SearchDB;
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
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static io.autocomplete.constants.AppConstants.*;

/**
 * It takes a search term as input and returns a list of suggestions as output
 */
@ConditionalOnProperty(prefix = "search", name = "engine", havingValue = "elastic")
@Slf4j
@Service
public class ElasticSearchImpl extends SearchDB {

    @Autowired
    ElasticsearchConfiguration elasticsearchConfiguration;
    /**
     * The function takes a string as input and returns a list of strings as output
     *
     * @param term The search term that the user has entered.
     * @return A list of suggestions for the given term.
     */
    @Override
    public AutoCompleteResponse getKeywords(String term) {
        List<Keyword> data = new ArrayList<>();
        try{
            if(term.length()==0){
                log.info("[ElasticSearchDB] String length is 0");
                throw new IOException();
            }
            if(term.length()<2){
                return new AutoCompleteResponse(new ArrayList<>(), term);
            }
            getResponse(ELASTIC_TEMPLATE, data, term, "searchTerm", "match_phrase_prefix");
            log.info("[ElasticSearchDB][PrefixMatch] Suggestion found from Prefix:: "+data.size());
        if(data.size()<10){
            int termFound = data.size();
            log.info("[ElasticSearchDB][WildCard] Suggestions to be found from WildCard Query:: "+(10-data.size()));
            getResponse(ELASTIC_TEMPLATE, data, term, "searchTerm", "wildcard");
            log.info("[ElasticSearchDB][WildCard] Suggestions found from WildCard Query:: "+(data.size()-termFound));
        }
        if(data.size()<10){
            int termFound = data.size();
            log.info("[ElasticSearchDB][Fuzziness] Suggestions to be found from Fuzzy Query "+(10-data.size()));
            getResponse(ELASTIC_TEMPLATE_FUZZY,data, term, "searchTerm", "fuzzy");
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
        return new AutoCompleteResponse(suggestions, term);
    }
    /**
     * It takes a template name, a list of Keyword objects, a search term and a field name as parameters and returns a list
     * of Keyword objects
     *
     * @param TEMPLATE The name of the template that you want to use.
     * @param data This is the list of Keyword objects that will be returned to the caller.
     * @param term The term to be searched
     * @param field The field in the index that you want to search.
     */
    public void getResponse(String TEMPLATE, List<Keyword> data, String term, String field, String queryType) {
        try {
            SearchTemplateRequest searchTemplateRequest = new SearchTemplateRequest();
            searchTemplateRequest.setScriptType(ScriptType.STORED);
            searchTemplateRequest.setScript(TEMPLATE);
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
                if(data.size()==10){
                    break;
                }
                String sourceAsString = hit.getSourceAsString();
                log.info("[ElasticSearchDB][insideGetResponse] Json Response:: " + sourceAsString);
                Keyword fromJson = elasticsearchConfiguration.gson.fromJson(sourceAsString, Keyword.class);
                log.info("[ElasticSearchDB][insideGetResponse] Json to Object:: " + fromJson);
                data.add(fromJson);
            }
        }
        catch (Exception e){
            log.warn("[Elasticsearch][insideGetResponse] Exception Found: "+e);
        }
    }
}
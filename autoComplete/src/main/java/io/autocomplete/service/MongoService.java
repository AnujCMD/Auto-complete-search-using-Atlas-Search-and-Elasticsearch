package io.autocomplete.service;

import io.autocomplete.model.Keyword;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@ConditionalOnProperty(prefix = "search", name = "engine", havingValue = "atlas")
@Repository
public interface MongoService extends MongoRepository<Keyword, String> {
    @Aggregation(pipeline = {"{'$search':{'index':'refined', 'autocomplete':{'query': '?0','path':'search_term', 'tokenOrder': 'sequential', 'score': {'function': {'path': {'value': 'v1_score'}}}}}}",
            "{'$limit':50}",
            "{$group: {'_id':{'normalized_term':'$normalized_term'},  'max_v1': {'$max': '$v1_score'},'search_term':{'$first': '$search_term'}}}",
            "{'$sort': {'max_v1':  -1, 'search_term':  1}}",
            "{'$limit': 10}",
            "{'$project':{'_id':0, 'search_term':1}}"}
    )
    List<Keyword> getSuggestionsWithoutFuzzy(String query) throws IOException;

    @Aggregation(pipeline = {"{'$search':{'index':'refined', 'autocomplete':{'query': '?0','path':'search_term', 'fuzzy':{'maxEdits': ?1, 'prefixLength':  1, 'maxExpansions': 256},'tokenOrder': 'sequential', 'score': {'function': {'path': {'value': 'v1_score'}}}}}}",
            "{'$limit':50}",
            "{$group: {'_id':{'normalized_term':'$normalized_term'},  'max_v1': {'$max': '$v1_score'},'search_term':{'$first': '$search_term'}}}",
            "{'$sort': {'max_v1':  -1}}",
            "{'$limit': 10}",
            "{'$project':{'_id':0, 'search_term':1}}"}
    )
    List<Keyword> getSuggestionsFromDB(String query, int fuzzy) throws IOException;
}
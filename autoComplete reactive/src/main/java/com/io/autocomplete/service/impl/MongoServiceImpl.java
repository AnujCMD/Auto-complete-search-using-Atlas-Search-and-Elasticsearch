package com.io.autocomplete.service.impl;

import com.io.autocomplete.model.AutoCompleteResponse;
import com.io.autocomplete.model.Keyword;
import com.io.autocomplete.model.SearchDB;
import com.io.autocomplete.service.MongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

import static com.io.autocomplete.constants.AppConstants.REDIS_KEY_MONGO;

/**
 * The function first checks if the query is less than 2 characters, if it is, it returns an empty list. If it is not, it
 * checks if the query is in the Redis cache, if it is, it returns the cached value. If it is not, it checks if the query
 * is in the MongoDB database, if it is, it returns the value. If it is not, it checks if the query is in the MongoDB
 * database with fuzziness, if it is, it returns the value. If it is not, it returns an empty list
 */
@ConditionalOnProperty(prefix = "search", name = "engine", havingValue = "atlas")
@Service
@Slf4j
public class MongoServiceImpl extends SearchDB { 
    
    @Autowired
    private MongoService mongoService;
    private final ReactiveHashOperations<String, String, AutoCompleteResponse> hashOperations;
    MongoServiceImpl(ReactiveRedisOperations<String, AutoCompleteResponse> redisOperations) {
        this.hashOperations = redisOperations.opsForHash();
    }
    /**
     * The function first checks if the query is less than 2 characters, if it is, it returns an empty list. If it is not,
     * it checks if the query is in the Redis cache, if it is, it returns the cached value. If it is not, it checks if the
     * query is in the MongoDB database, if it is, it returns the value. If it is not, it checks if the query is in the
     * MongoDB database with fuzziness, if it is, it returns the value. If it is not, it returns an empty list
     *
     * @param query The query string that the user has entered.
     * @return A list of suggestions
     */
    @Override
    public Mono<AutoCompleteResponse> getKeywords(String query) {
        List<String> suggestions = new ArrayList<>();
        try {
            log.info("[MongoDB]" + query + " " +
                    "!");
            if (query.length() < 2) {
                log.info("[MongoDB]String: " + query + " length is less than 2");
                AutoCompleteResponse ans =new AutoCompleteResponse(query, new ArrayList<>());
                return  hashOperations.put(REDIS_KEY_MONGO, query, ans).map(isSaved ->ans).thenReturn(ans);
            }
            try {
                boolean hasKey = Boolean.TRUE.equals(hashOperations.hasKey(REDIS_KEY_MONGO, query).block());
                if (hasKey) {
                    log.info("Used Redis in Mongo for " + query);
                    return hashOperations.get(REDIS_KEY_MONGO, query);
                }
            }
            catch (Exception e){
                log.info("[MongoDB][Redis] Error caught while caching:: "+e);
            }
            for (Keyword keyword : mongoService.getSuggestionsWithoutFuzzy(query)) {
                log.info(keyword.getSearchTerm());
                suggestions.add(keyword.getSearchTerm());
            }

            if (suggestions.size() < 10) {
                log.info("[MongoDB][Prefix] match is less than 10, Fuzziness is included, prefix search size:: " + (suggestions.size()));
                List<String> res = new ArrayList<>();
                    int fuzzy = 2;
                    if(query.length()<4)
                        fuzzy = 1;

                    for (Keyword keyword : mongoService.getSuggestionsFromDB(query, fuzzy)) {
                        if (suggestions.size() == 10) {
                            log.info("[MongoDB][Fuzziness]Q Query result achieved max size(10) returning list");
                            AutoCompleteResponse results=new AutoCompleteResponse(query, suggestions);
                            return hashOperations.put(REDIS_KEY_MONGO, query, results).map(isSaved ->results).thenReturn(results);
                        }
                        suggestions.add(keyword.getSearchTerm());
                    }

            }
        } catch (Exception exception) {
            log.warn("[MongoDB][Exception]Exception is caught in MongoSearch:: " + exception);
        }
        log.info("[MongoDB]Returning Suggestions(End of Function): " + suggestions);
        AutoCompleteResponse results=new AutoCompleteResponse(query, suggestions);
        return hashOperations.put(REDIS_KEY_MONGO, query, results).map(isSaved ->results).thenReturn(results);
    }
}

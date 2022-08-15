package io.autocomplete.service.impl;

import io.autocomplete.model.AutoCompleteResponse;
import io.autocomplete.model.Keyword;
import io.autocomplete.model.SearchDB;
import io.autocomplete.service.MongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@ConditionalOnProperty(prefix = "search", name = "engine", havingValue = "atlas")
@Service
@Slf4j
public class MongoServiceImpl extends SearchDB {

    @Autowired
    private MongoService mongoService;

    @Override
    public AutoCompleteResponse getKeywords(String query) {
        List<String> suggestions = new ArrayList<>();
        try {
            log.info("[MongoDB]" + query + " is the string!");
            if (query.length() < 2) {
                log.info("[MongoDB]String: " + query + " length is less than 2");
                return new AutoCompleteResponse(new ArrayList<>(), query);
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
                            log.info("[MongoDB][Fuzziness] Query result achieved max size(10) returning list");
                            return new AutoCompleteResponse(suggestions, query);
                        }
                        suggestions.add(keyword.getSearchTerm());
                    }

            }
        } catch (Exception exception) {
            log.warn("[MongoDB][Exception]Exception is caught in MongoSearch:: " + exception);
        }
        log.info("[MongoDB]Returning Suggestions(End of Function): " + suggestions);
        return new AutoCompleteResponse(suggestions, query);
    }
}

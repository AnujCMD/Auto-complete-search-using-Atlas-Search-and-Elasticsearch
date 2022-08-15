package com.io.autocomplete.model;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * The `SearchDB` class is an abstract class that has a method called `getKeywords` that returns a `Mono<Keyword>` object
 */
@Component
public abstract class SearchDB {
    public abstract Mono<AutoCompleteResponse> getKeywords(String query) throws IOException;
}


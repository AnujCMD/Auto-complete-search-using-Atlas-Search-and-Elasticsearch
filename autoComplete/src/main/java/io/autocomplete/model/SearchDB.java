package io.autocomplete.model;

import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * SearchDB is an abstract class that defines a method called getKeywords() that returns an AutoCompleteResponse object.
 */
@Component
public abstract class SearchDB {
    public abstract AutoCompleteResponse getKeywords(String query) throws IOException;
}
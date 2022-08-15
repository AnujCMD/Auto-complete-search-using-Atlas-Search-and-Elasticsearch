package com.io.autocomplete.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * It's a class that returns the response from the AutoComplete API
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoCompleteResponse extends Keyword {
    private String query;
    private List<String> data;
}


package io.autocomplete.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * It's a Java class that has two fields, one of type List<String> and one of type String
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoCompleteResponse {
    private List<String> data;
    private String query;
}
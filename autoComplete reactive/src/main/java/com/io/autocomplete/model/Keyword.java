package com.io.autocomplete.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * The class is annotated with @Document(collection = "Refined_Term") to tell Spring Data MongoDB to store the data in the
 * Refined_Term collection and with @Document(collection = "clean") to tell Spring Data MongoDB to store the data in the
 *  * Refined_Term collection
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Refined_Term")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "clean")
public class Keyword {
    @Id
    private String id;

    @Field("pk")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private String pk;
    @Field("search_term")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private String searchTerm;

    @Field("normalized_term")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private String normalizedTerm;

    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Double)
    @Field("v1_score")
    private double v1Score;

    @Field("updated_date")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Date)
    @JsonSerialize(using = LocalDateSerializer.class)
    private Date updatedDate;
}
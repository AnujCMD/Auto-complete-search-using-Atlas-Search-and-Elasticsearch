package io.autocomplete.constants;

// This is a constant file which is used to store all the constants used in the project.
public interface AppConstants {
        String ELASTIC_HOST = "elastic.es.ap-south-1.aws.elastic-cloud.com";
        int ELASTIC_PORT = 9243;
        String ELASTIC_USER = "elastic";
        String ELASTIC_PASSWORD = "2t5JpUKXle0VT9CQKWnL95HE";

        String ELASTIC_SCHEME = "https";

        String ELASTIC_INDEX = "searchdb";
        String ELASTIC_TEMPLATE = "autocomplete_temp";
        String ELASTIC_TEMPLATE_FUZZY = "autocomplete_temp_fuzzy";
}

package com.io.autocomplete.constants;
// A constant file.
public interface AppConstants {
        String ELASTIC_HOST = "elastic.es.ap-south-1.aws.elastic-cloud.com";
        int ELASTIC_PORT = 9243;
        String ELASTIC_USER = "elastic";
        String ELASTIC_PASSWORD = "2t5JpUKXle0VT9CQKWnL95HE";
        String ELASTIC_SCHEME = "https";
        String ELASTIC_INDEX = "searchdb";
        String ELASTIC_TEMPLATE = "autocomplete_temp";
        String ELASTIC_TEMPLATE_FUZZY = "autocomplete_temp_fuzzy";
        String REDIS_KEY_ELASTIC = "KEYELASTIC";
        String REDIS_KEY_MONGO = "KEYMONGO";
        String REDIS_HOST = "redis-19682.c305.ap-south-1-1.ec2.cloud.redislabs.com";
        int REDIS_PORT = 19682;
        String REDIS_PASSWORD = "1xVGfyMwSbS9moViujYFtycfzH9cd5iP";
}

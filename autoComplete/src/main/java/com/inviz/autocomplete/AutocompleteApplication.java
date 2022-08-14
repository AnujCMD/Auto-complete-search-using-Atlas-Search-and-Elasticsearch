package com.inviz.autocomplete;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**  AutoComplete using ElasticSearch and MongoDb's Atlas Search
 * It's a Spring Boot application that implements autocomplete feature which displays top 10 suggestions for the input text.
 * Enables caching and Swagger.
 *
 * @author Anuj Joshi & Ashish Kumar Sharma
 * @version 1.0
 */
@SpringBootApplication
@EnableCaching
@EnableSwagger2
public class AutocompleteApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutocompleteApplication.class, args);
    }

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("io.autocomplete")).build();
    }
}
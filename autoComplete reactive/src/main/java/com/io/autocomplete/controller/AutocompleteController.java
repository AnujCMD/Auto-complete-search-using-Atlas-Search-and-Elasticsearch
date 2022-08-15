package com.io.autocomplete.controller;

import com.io.autocomplete.model.AutoCompleteResponse;
import com.io.autocomplete.model.SearchDB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
/**
 * The AutocompleteController class is a controller class that handles the requests that are made to the application
 */
public class AutocompleteController {
    @Autowired
    private SearchDB searchDB;

    /**
     * The function takes in a string as a query parameter, and returns a list of keywords that match the query
     *
     * @param query The query string that is passed to the controller.
     * @return A Mono of Keyword object.
     */
    @CrossOrigin
    @RequestMapping("/autocomplete")
    public Mono<AutoCompleteResponse> getSuggestion(@RequestParam String query) throws IOException {
        if (query == null) {
            log.warn("[Controller][getSuggestion] Null String is passed");
            throw new IOException("[Controller][getSuggestion] Null input string is passed");
        }
        log.info("String is:: "+query);
        log.info("String is not cached, Querying Database");
        return searchDB.getKeywords(query);
    }

    /**
     * It takes a query string as a parameter, and returns a list of strings that are the suggestions for the query
     *
     * @param query The query string that the user types in the search bar.
     * @return A list of strings
     */
    @GetMapping("/ui")
    public List<String> giveResponseUI(@RequestParam String query) throws IOException{
        Mono<AutoCompleteResponse> getMono = searchDB.getKeywords(query);
        return Objects.requireNonNull(getMono.block()).getData();
    }
    /**
     * This function is called when an error occurs in the application
     *
     * @return ModelAndView
     */
    @GetMapping("/error")
    public ModelAndView error() {
        log.warn("[Controller][Error] Error caught in mapping");
        log.info("[Controller][Error] Redirecting:: to Error.html page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        return modelAndView;
    }

    /**
     * This function is called when the user enters a query in the search bar and hits enter. The query is passed as a
     * parameter to the function. The function then calls the getKeywords() function of the SearchDB class and passes the
     * query as a parameter. The getKeywords() function returns a list of suggestions. The list of suggestions is then
     * attached to the model and the model is attached to the home.html file
     *
     * @param model This is the model object that is used to pass data from the controller to the view.
     * @param query The query string that the user has entered.
     * @return ModelAndView object is being returned.
     */
    @GetMapping("/web")
    public ModelAndView html(Model model, @RequestParam String query) throws IOException {
        log.info("Query is:: " + query);
        if (query == null) {
            log.warn("[Controller][html] Null String is passed");
            throw new IOException("[Controller][html] String Input is null");
        }
        query=query.trim();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        log.info("[Controller][html] model is attached to Home.html");
        model.addAttribute("query", query);
        model.addAttribute("suggestionsList", Objects.requireNonNull(searchDB.getKeywords(query).block()).getData());
        return modelAndView;
    }

}

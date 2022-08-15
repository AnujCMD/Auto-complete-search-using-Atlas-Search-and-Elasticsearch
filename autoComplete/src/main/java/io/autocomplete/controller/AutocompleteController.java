package io.autocomplete.controller;

import io.autocomplete.model.SearchDB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@CacheConfig(cacheNames = {"searchDB"})
public class AutocompleteController {
    @Autowired
    private SearchDB searchDB;

    @CrossOrigin
    @RequestMapping("/autocomplete")
    @Cacheable(key = "#query")
    public List<String> getSuggestion(@RequestParam String query) throws IOException {
        if (query == null) {
            log.warn("[Controller][getSuggestion] Null String is passed");
            throw new IOException("[Controller][getSuggestion] Null input string is passed");
        }
        log.info("String is not cached, Querying Database");
        return searchDB.getKeywords(query).getData();
    }

    @GetMapping("/error")
    public ModelAndView error() {
        log.warn("[Controller][Error] Error caught in mapping");
        log.info("[Controller][Error] Redirecting:: to Error.html page");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        return modelAndView;
    }

    @GetMapping("/web")
    public ModelAndView html(Model model, @RequestParam String query) throws IOException {
        log.info("Query is:: " + query);
        if (query == null) {
            log.warn("[Controller][html] Null String is passed");
            throw new IOException("[Controller][html] String Input is null");
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        log.info("[Controller][html] model is attached to Home.html");
        List<String> search = searchDB.getKeywords(query).getData();
        model.addAttribute("query", query);
        model.addAttribute("suggestionsList", searchDB.getKeywords(query));
        return modelAndView;
    }
}

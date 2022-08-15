package com.io.autocomplete;

import com.io.autocomplete.model.SearchDB;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AutocompleteTest {
    @Autowired
    SearchDB searchDB;

    @Test
    public void whenTestingForPositiveResultEndingWithS() throws Exception {
        List<String> expected= Arrays.asList("utsa kurta", "utsa women ethnic kurti n kurta", "kurta women", "w kurta, kurta set women", "zuba kurta, kurta", "diza women ethnic kurti n kurta", "w kurta set, zuba women ethnic kurti n kurta");
        List<String> actual = searchDB.getKeywords("kurta").block().getData();
        String exp = expected.toString();
        String act = actual.toString();
        assertThat(act)
                .isNotEqualTo(exp);
    }

    @Test
    public void whenTestingForPositiveWithSameV1() throws Exception {
        List<String> expected= Arrays.asList("vark mint suits", "mint green ray ban sun glasses", "luna blu mint shoes", "yellow shoes mint", "mint casual shoes", "mint home shoes", "luna blu mint home shoes", "luna blu mint shoes casual", "arrow mintshirts", "arrow shirts mint");
        List<String> actual = Objects.requireNonNull(searchDB.getKeywords("Mint").block()).getData();
        String exp = expected.toString();
        String act = actual.toString();
        assertThat(act)
                .isNotEqualTo(exp);

    }

    @Test
    public void whenTestingForEmptyString() throws Exception {
        List<String> expected = new ArrayList<>();
        List<String> actual = searchDB.getKeywords("").block().getData();
        String exp = expected.toString();
        String act = actual.toString();
        assertThat(exp)
                .isEqualTo(act);
    }


}

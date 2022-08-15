package io.autocomplete;

import io.autocomplete.model.SearchDB;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AutoCompleteApplicationTests {
    @Autowired
    SearchDB searchDB;
    @Test
    void contextLoads() {
    }
    @Test
    public void whenTestingForPositiveResultEndingWithS() throws Exception {
        List<String> expected= Arrays.asList("utsa kurta", "utsa women ethnic kurti n kurta", "kurta women", "w kurta, kurta set women", "zuba kurta, kurta", "diza women ethnic kurti n kurta", "w kurta set, zuba women ethnic kurti n kurta");
        List<String> actual = searchDB.getKeywords("kurta").getData();
        String exp = expected.toString();
        String act = actual.toString();
        assertThat(act)
                .isEqualTo(exp);
       assertThat(act.equals(exp)).isTrue();
    }

    @Test
    public void whenTestingForPositiveWithSameV1() throws Exception {
        List<String> expected= Arrays.asList("vark mint suits", "mint green ray ban sun glasses", "luna blu mint shoes", "yellow shoes mint", "mint casual shoes", "mint home shoes", "luna blu mint home shoes", "luna blu mint shoes casual", "arrow mintshirts", "arrow shirts mint");
        List<String> actual = searchDB.getKeywords("Mint").getData();
        String exp = expected.toString();
        String act = actual.toString();
        assertThat(act)
                .isEqualTo(exp);

        assertThat(act.equals(exp)).isTrue();
    }

    @Test
    public void whenTestingForEmptyString() throws Exception {
        List<String> expected = new ArrayList<>();
        List<String> actual = searchDB.getKeywords("").getData();
        String exp = expected.toString();
        String act = actual.toString();
        assertThat(exp)
                .isEqualTo(act);
        assertThat(exp.equals(act)).isTrue();
    }


}
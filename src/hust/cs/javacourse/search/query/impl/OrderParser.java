package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.text.ParseException;
import java.util.*;

public class OrderParser {

    static final Set<String> wordSet = new HashSet<String>() {{
        add("AND");
        add("OR");
    }};

    public static List<String> parseOrder(String order) throws ParseException {
        if (order == null) {
            return new ArrayList<>();
        }
        StringSplitter splitter = new StringSplitter();
        splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
        List<String> list = splitter.splitByRegex(order);
        if (list.size() % 2 == 0) {
            throw new ParseException("order list size can be divided by 2", -1);
        }
        for (int i = 0; i < list.size(); ++i) {
            if (i % 2 == 1 && !wordSet.contains(list.get(i))) {
                throw new ParseException("expected OR or AND", i);
            }
        }
        return list;
    }
}

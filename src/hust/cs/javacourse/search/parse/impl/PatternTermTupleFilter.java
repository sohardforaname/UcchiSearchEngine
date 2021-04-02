package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.Config;

import java.util.regex.Pattern;

public class PatternTermTupleFilter extends AbstractTermTupleFilter {

    String pattern = Config.TERM_FILTER_PATTERN;

    public PatternTermTupleFilter() {

    }

    public PatternTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
    }

    public void setStream(AbstractTermTupleStream input) {
        super.input = input;
    }

    @Override
    public AbstractTermTuple next() {
        AbstractTermTuple currentTermTuple;
        do {
            currentTermTuple = input.next();
        } while (currentTermTuple != null && !Pattern.matches(pattern, currentTermTuple.term.getContent()));
        return currentTermTuple;
    }
}

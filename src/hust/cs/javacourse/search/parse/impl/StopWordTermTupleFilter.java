package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.StopWords;

import java.util.Arrays;
import java.util.HashSet;

public class StopWordTermTupleFilter extends AbstractTermTupleFilter {

    HashSet<String> stopWordsList = new HashSet<>(Arrays.asList(StopWords.STOP_WORDS));

    public StopWordTermTupleFilter() {
        
    }

    public StopWordTermTupleFilter(AbstractTermTupleStream input) {
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
        } while (currentTermTuple != null && stopWordsList.contains(currentTermTuple.term.getContent()));
        return currentTermTuple;
    }

    @Override
    public void close() {
        super.close();
    }
}

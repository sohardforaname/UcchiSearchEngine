package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.Config;


public class LengthTermTupleFilter extends AbstractTermTupleFilter {

    int shortRange = Config.TERM_FILTER_MINLENGTH, longRange = Config.TERM_FILTER_MAXLENGTH;

    public LengthTermTupleFilter() {

    }

    public LengthTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
    }

    public LengthTermTupleFilter(AbstractTermTupleStream input, int shortRange, int longRange) {
        super(input);
        this.longRange = longRange;
        this.shortRange = shortRange;
    }

    public void setStream(AbstractTermTupleStream input) {
        super.input = input;
    }

    @Override
    public AbstractTermTuple next() {
        AbstractTermTuple currentTermTuple;
        do {
            currentTermTuple = input.next();
        } while (currentTermTuple != null &&
                (shortRange > currentTermTuple.term.getContent().length()
                        || longRange < currentTermTuple.term.getContent().length()));
        return currentTermTuple;
    }
}

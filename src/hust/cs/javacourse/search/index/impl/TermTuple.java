package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.AbstractTermTuple;

public class TermTuple extends AbstractTermTuple {

    public TermTuple() {

    }

    public TermTuple(AbstractTerm term, int curPos) {
        this.term = term;
        this.curPos = curPos;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof AbstractTermTuple)
                && this.curPos == ((AbstractTermTuple) obj).curPos
                && this.term.equals(((AbstractTermTuple) obj).term);
    }

    @Override
    public String toString() {
        return "{Term: " + term.toString() + ", CurPos:" + curPos + ", Freq:" + freq + "}";
    }
}

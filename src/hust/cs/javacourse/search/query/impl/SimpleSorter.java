package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.Sort;

import java.util.*;

public class SimpleSorter implements Sort {
    @Override
    public void sort(List<AbstractHit> hits) {
        for (AbstractHit hit : hits) {
            hit.setScore(score(hit));
        }
        hits.sort((o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));
    }

    @Override
    public double score(AbstractHit hit) {
        int res = 0;
        for (AbstractPosting posting : hit.getTermPostingMapping().values()) {
            res += posting.getFreq();
        }
        return res;
    }
}

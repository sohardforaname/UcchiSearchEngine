package hust.cs.javacourse.search.query.impl;

import com.sun.istack.internal.NotNull;
import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;

import java.io.File;
import java.util.*;

public class IndexSearcher extends AbstractIndexSearcher {
    @Override
    public void open(String indexFile) {
        index.load(new File(indexFile));
    }

    @Override
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {
        if (queryTerm == null) {
            return new AbstractHit[0];
        }
        AbstractPostingList postingList = index.search(queryTerm);
        if (postingList == null) {
            return new AbstractHit[0];
        }

        this.sorter.updateTerm(queryTerm);

        int size = postingList.size();
        List<AbstractHit> hitList = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            AbstractPosting posting = postingList.get(i);
            int docId = posting.getDocId();
            AbstractHit tempHit = new Hit(docId, index.getDocName(docId), new TreeMap<AbstractTerm, AbstractPosting>());
            tempHit.getTermPostingMapping().put(queryTerm, posting);
            hitList.add(tempHit);
        }
        //return optimize(hitList, null);
        return optimize(hitList, sorter);
    }

    @Override
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, @NotNull Sort sorter, LogicalCombination combine) {
        if (combine == LogicalCombination.OR) {
            return search(new ArrayList<String>() {{
                add(queryTerm1.getContent());
                add("OR");
                add(queryTerm2.getContent());
            }}, sorter);
        } else {
            return search(new ArrayList<String>() {{
                add(queryTerm1.getContent());
                add("AND");
                add(queryTerm2.getContent());
            }}, sorter);
        }
    }

    AbstractHit[] calculateAndGroup(List<AbstractHit[]> list) {
        if (list.size() == 1) {
            return list.get(0);
        } else {
            Iterator<AbstractHit[]> iterator = list.iterator();
            AbstractHit[] tempArray = SetOperationUtil.getIntersectionReturnArray(iterator.next(), iterator.next());
            while (iterator.hasNext()) {
                tempArray = SetOperationUtil.getIntersectionReturnArray(tempArray, iterator.next());
            }
            return tempArray;
        }
    }

    AbstractHit[] calculateOrGroup(List<AbstractHit[]> list) {
        if (list.size() == 1) {
            return list.get(0);
        } else {
            Iterator<AbstractHit[]> iter = list.iterator();
            AbstractHit[] tempArray = SetOperationUtil.getUnionReturnArray(iter.next(), iter.next());
            while (iter.hasNext()) {
                tempArray = SetOperationUtil.getUnionReturnArray(tempArray, iter.next());
            }
            return tempArray;
        }
    }

    public AbstractHit[] search(List<String> orderList, Sort sorter) {
        int i = 0;
        List<AbstractHit[]> hitArrayList = new ArrayList<>(), hitAndArrayList = new ArrayList<>();
        while (i < orderList.size()) {
            hitAndArrayList.clear();
            int next = i;
            while (next < orderList.size() && !orderList.get(next).equals("OR")) {
                String str = orderList.get(next);
                if (!str.equals("AND")) {
                    hitAndArrayList.add(search(new Term(str), null));
                }
                ++next;
            }
            hitArrayList.add(calculateAndGroup(hitAndArrayList));
            i = next + 1;
        }
        return optimize(Arrays.asList(calculateOrGroup(hitArrayList)), sorter);
    }

    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, @NotNull Sort sorter) {
        AbstractHit[] leftHitList = search(queryTerm1, sorter);
        AbstractHit[] rightHitList = search(queryTerm2, sorter);
        List<AbstractPosting> postings1 = new ArrayList<>(), postings2 = new ArrayList<>();
        for (AbstractHit hit : leftHitList) {
            postings1.addAll(hit.getTermPostingMapping().values());
        }
        for (AbstractHit hit : rightHitList) {
            postings2.addAll(hit.getTermPostingMapping().values());
        }
        postings1.sort(Comparator.comparingInt(AbstractPosting::getDocId));
        postings2.sort(Comparator.comparingInt(AbstractPosting::getDocId));

        List<AbstractHit> hitList = new ArrayList<>();

        int i = 0, j = 0;
        while (i < postings1.size() && j < postings2.size()) {
            AbstractPosting posting1 = postings1.get(i), posting2 = postings2.get(j);
            if (posting1.getDocId() == posting2.getDocId()) {
                for (Integer position : posting1.getPositions()) {
                    if (posting2.getPositions().contains(position + 1)) {
                        int docID = posting1.getDocId();
                        hitList.add(new Hit(docID, index.getDocName(docID), new TreeMap<AbstractTerm, AbstractPosting>() {{
                            put(queryTerm1, posting1);
                            put(queryTerm2, posting2);
                        }}));
                    }
                }
            } else if (posting1.getDocId() < posting2.getDocId()) {
                ++i;
            } else {
                ++j;
            }
        }

        return optimize(hitList, sorter);
    }

    public List<AbstractTerm> getRecommendTerm(AbstractTerm term, int size) {
        return index.getSimilarTerm(term, size);
    }

    TermSorter sorter = new TermSorter();

    public TermSorter getSorter() {
        return this.sorter;
    }

    public void setSorter(@NotNull TermSorter sorter) {
        this.sorter = sorter;
    }

    public AbstractHit[] optimize(List<AbstractHit> hitList, Sort sorter) {
        if(sorter != null ) {
            sorter.sort(hitList);
        }
        AbstractHit[] hitArray = new Hit[hitList.size()];
        return hitList.toArray(hitArray);
    }

}


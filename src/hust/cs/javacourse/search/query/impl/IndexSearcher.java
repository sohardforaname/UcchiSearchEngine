package hust.cs.javacourse.search.query.impl;

import com.sun.istack.internal.NotNull;
import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.impl.Posting;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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
            AbstractHit tempHit = new Hit(docId, index.getDocName(docId), new TreeMap<>());
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

        List<AbstractHit> hitList = SetOperationUtil.getIntersection(leftHitList, rightHitList), resList = new ArrayList<>();
        for (AbstractHit hit : hitList) {
            List<Integer> integers = new ArrayList<>();
            Map<AbstractTerm, AbstractPosting> postingListMap = hit.getTermPostingMapping();
            for (Integer position : postingListMap.get(queryTerm1).getPositions()) {
                if (postingListMap.get(queryTerm2).getPositions().contains(position + 1)) {
                    integers.add(position);
                }
            }
            resList.add(new Hit(hit.getDocId(), hit.getDocPath(), new TreeMap<AbstractTerm, AbstractPosting>() {{
                put(queryTerm1, new Posting(hit.getDocId(), integers.size(), integers));
                put(queryTerm2, new Posting(hit.getDocId(), integers.size(), integers.stream().map(x -> x + 1).collect(Collectors.toList())));
            }}));
        }

        return optimize(resList, sorter);
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
        if (sorter != null) {
            sorter.sort(hitList);
        }
        AbstractHit[] hitArray = new Hit[hitList.size()];
        return hitList.toArray(hitArray);
    }

}


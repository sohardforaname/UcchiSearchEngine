package hust.cs.javacourse.search.query.impl;

import com.sun.istack.internal.NotNull;
import hust.cs.javacourse.search.index.AbstractTerm;

import java.io.*;
import java.util.*;

public class TermSorter {
    TreeMap<AbstractTerm, Integer> termToVisitTimesMap = new TreeMap<>();

    public TermSorter() {

    }

    public TermSorter(@NotNull TreeMap<AbstractTerm, Integer> termToVisitTimesMap) {
        this.termToVisitTimesMap = termToVisitTimesMap;
    }

    public void setTermToVisitTimesMap(TreeMap<AbstractTerm, Integer> termToVisitTimesMap) {
        this.termToVisitTimesMap = termToVisitTimesMap;
    }

    public TreeMap<AbstractTerm, Integer> getTermToVisitTimesMap() {
        return this.termToVisitTimesMap;
    }

    public Integer getVisitTimes(AbstractTerm term) {
        return this.termToVisitTimesMap.get(term);
    }

    public void updateTerm(AbstractTerm term) {
        Integer times = this.getVisitTimes(term);
        if (times == null) {
            this.termToVisitTimesMap.put(term, 1);
        } else {
            this.termToVisitTimesMap.put(term, times + 1);
        }
    }

    List<Map.Entry<AbstractTerm, Integer>> getListOfMap() {
        return new ArrayList<>(this.termToVisitTimesMap.entrySet());
    }

    public List<Map.Entry<AbstractTerm, Integer>> sortByDefault() {
        List<Map.Entry<AbstractTerm, Integer>> entryList = getListOfMap();
        entryList.sort(Map.Entry.comparingByValue((o1, o2) -> Integer.compare(o2, o1)));
        return entryList;
    }

    public List<Map.Entry<AbstractTerm, Integer>> sortByTerm() {
        return getListOfMap();
    }

    public void clear() {
        termToVisitTimesMap.clear();
    }

    public List<Map.Entry<AbstractTerm, Integer>> sortBySorter(
            Comparator<Map.Entry<AbstractTerm, Integer>> comp) {
        List<Map.Entry<AbstractTerm, Integer>> entryList = getListOfMap();
        entryList.sort(comp);
        return entryList;
    }

    static public List<Map.Entry<AbstractTerm, Integer>> getTopTerm(
            @NotNull List<Map.Entry<AbstractTerm, Integer>> searchResult, int topRange) {
        return searchResult.subList(0, Integer.min(topRange, searchResult.size()));
    }

    public void store(File file) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(this.termToVisitTimesMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void load(File file) {
        ObjectInputStream in = null;
        if (file.exists()) {
            try {
                in = new ObjectInputStream(new FileInputStream(file));
                this.termToVisitTimesMap =
                        (TreeMap<AbstractTerm, Integer>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.impl.IndexSearcher;
import hust.cs.javacourse.search.query.impl.OrderParser;
import hust.cs.javacourse.search.query.impl.SimpleSorter;
import hust.cs.javacourse.search.util.Config;

import java.io.File;
import java.text.ParseException;
import java.util.Arrays;

/**
 * 测试搜索
 */
public class TestSearchIndex {
    /**
     * 搜索程序入口
     *
     * @param args ：命令行参数
     */
    public static void main(String[] args) {
        IndexSearcher searcher = new IndexSearcher();
        searcher.open(Config.INDEX_DIR + "index.dat");
        searcher.getSorter().load(new File(Config.INDEX_DIR + "statistics.dat"));
        try {
            AbstractHit[] hits = searcher.search(
                    OrderParser.parseOrder("according AND pollution AND death OR action AND action"),
                    new SimpleSorter()
            );
            //AbstractHit[] hits = searcher.search(new Term("according"), new Term("pollution"), new Sorter(), AbstractIndexSearcher.LogicalCombination.OR);
            if (hits.length == 0) {
                System.out.println("Not Text, Recommend Words: "
                        + searcher.getRecommendTerm(new Term("pollution"), 5));
            }
            System.out.println("Search records: " +
                    searcher.getSorter().sortByDefault());
            System.out.println("Search result:");
            for (AbstractHit hit : hits) {
                System.out.print("{docId:" + hit.getDocId() + ", docPath:" + hit.getDocPath()
                        + ", posting:" + hit.getTermPostingMapping() + ", scores:" + hit.getScore() + "}\n");
            }
            searcher.getSorter().store(new File(Config.INDEX_DIR + "statistics.dat"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AbstractHit[] hits = searcher.search(new Term("coronavirus"), new Term("death"), new SimpleSorter());
        System.out.println(Arrays.toString(hits));
    }
}

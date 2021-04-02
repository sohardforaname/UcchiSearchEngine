package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.index.impl.DocumentBuilder;
import hust.cs.javacourse.search.index.impl.Index;
import hust.cs.javacourse.search.index.impl.IndexBuilder;
import hust.cs.javacourse.search.parse.impl.TermTupleFilterLoader;
import hust.cs.javacourse.search.util.Config;

import java.io.File;

/**
 * 测试索引构建
 */
public class TestBuildIndex {
    /**
     * 索引构建程序入口
     *
     * @param args : 命令行参数
     */
    public static void main(String[] args) {
        TermTupleFilterLoader loader = new TermTupleFilterLoader();
        loader.loadConfig(new File(Config.PROJECT_HOME_DIR + "\\config"));
        AbstractIndexBuilder builder = new IndexBuilder(new DocumentBuilder(loader));
        String rootDir = Config.DOC_DIR;
        AbstractIndex index = builder.buildIndex(rootDir);
        System.out.print(index);
        index.save(new File(Config.INDEX_DIR + "index.dat"));
        AbstractIndex index2 = new Index();
        index2.load(new File(Config.INDEX_DIR + "index.dat"));
        System.out.print(index2);
    }
}

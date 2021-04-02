package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.util.FileUtil;

import java.io.File;
import java.util.List;

public class IndexBuilder extends AbstractIndexBuilder {

    public IndexBuilder(AbstractDocumentBuilder builder) {
        super(builder);
    }

    @Override
    public AbstractIndex buildIndex(String rootDirectory) {
        AbstractIndex index = new Index();
        List<String> paths = FileUtil.list(rootDirectory);
        // Collections.reverse(paths);
        for(String path : paths) {
            index.addDocument(docBuilder.build(docId, path, new File(path)));
            ++docId;
        }
        index.optimize();
        return index;
    }
}

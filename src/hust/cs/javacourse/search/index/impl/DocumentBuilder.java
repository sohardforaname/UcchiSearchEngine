package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocument;
import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.parse.impl.*;


import java.io.*;
import java.util.ArrayList;

public class DocumentBuilder extends AbstractDocumentBuilder {

    TermTupleFilterLoader loader = new TermTupleFilterLoader();

    public DocumentBuilder() {
        super();
    }

    public DocumentBuilder(TermTupleFilterLoader loader) {
        super();
        this.loader = loader;
    }

    @Override
    public AbstractDocument build(int docId, String docPath, File file) {
        AbstractTermTupleStream termTupleStream = null;
        AbstractDocument document = null;
        try {
            termTupleStream = new TermTupleScanner(
                    new BufferedReader(
                            new InputStreamReader(
                                    new FileInputStream(file)
                            )
                    )
            );
            //termTupleStream = loader.setFilter(termTupleStream);
            termTupleStream = new PatternTermTupleFilter(new LengthTermTupleFilter(new StopWordTermTupleFilter(termTupleStream)));
            document = build(docId, docPath, termTupleStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (termTupleStream != null) {
                termTupleStream.close();
            }
        }
        return document;
    }

    @Override
    public AbstractDocument build(int docId, String docPath, AbstractTermTupleStream termTupleStream) {
        AbstractDocument document = new Document(docId, docPath, new ArrayList<>());
        if (termTupleStream == null) {
            return document;
        }
        for (; ; ) {
            AbstractTermTuple tuple = termTupleStream.next();
            if (tuple == null) {
                termTupleStream.close();
                return document;
            }
            document.addTuple(tuple);
        }
    }
}

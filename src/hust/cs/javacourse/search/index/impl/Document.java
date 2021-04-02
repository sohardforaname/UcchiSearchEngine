package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocument;
import hust.cs.javacourse.search.index.AbstractTermTuple;

import java.util.List;

public class Document extends AbstractDocument {

    public Document() {
        super();
    }

    public Document(int docId, String docPath) {
        super(docId, docPath);
    }

    public Document(int docId, String docPath, List<AbstractTermTuple> tuples) {
        super(docId, docPath, tuples);
    }

    @Override
    public int getDocId() {
        return this.docId;
    }

    @Override
    public void setDocId(int docId) {
        this.docId = docId;
    }

    @Override
    public String getDocPath() {
        return this.docPath;
    }

    @Override
    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

    @Override
    public void addTuple(AbstractTermTuple tuple) {
        if(!this.contains(tuple)) {
            this.tuples.add(tuple);
        }
    }

    @Override
    public AbstractTermTuple getTuple(int index) {
        return this.tuples == null ? null : this.tuples.get(index);
    }

    @Override
    public int getTupleSize() {
        return this.tuples.size();
    }

    @Override
    public boolean contains(AbstractTermTuple tuple) {
        return tuple != null && this.tuples.contains(tuple);
    }

    @Override
    public List<AbstractTermTuple> getTuples() {
        return this.tuples;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for(AbstractTermTuple tuple : tuples) {
            buffer.append(tuple.toString());
        }
        return "{docID:"+this.docId+", docPath:"+docPath+",tuples:"+buffer.toString()+"}";
    }
}

package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.util.FileUtil;

import java.util.Map;

public class Hit extends AbstractHit {

    public Hit() {
        super();
    }

    public Hit(int docId, String docPath) {
        super(docId, docPath);
    }

    public Hit(int docId, String docPath, Map<AbstractTerm, AbstractPosting> termPostingMapping) {
        super(docId, docPath, termPostingMapping);
    }

    public Hit(AbstractHit hit) {
        this.docId = hit.getDocId();
        this.content = hit.getContent();
        this.score = hit.getScore();
        this.termPostingMapping = hit.getTermPostingMapping();
        this.docPath = hit.getDocPath();
    }

    @Override
    public int getDocId() {
        return this.docId;
    }

    @Override
    public String getDocPath() {
        return this.docPath;
    }

    @Override
    public String getContent() {
        if (this.content == null) {
            this.content = FileUtil.read(this.docPath);
        }
        return this.content;
    }

    @Override
    public double getScore() {
        return this.score;
    }

    @Override
    public Map<AbstractTerm, AbstractPosting> getTermPostingMapping() {
        return this.termPostingMapping;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "{docId:" + docId + ", docPath" + docPath + ", index:" + termPostingMapping + ", content:" + content + "}\n";
    }

    @Override
    public int compareTo(AbstractHit o) {
        return (int)(this.getScore() - o.getScore());
    }

    public boolean equals(Object obj) {
        return obj instanceof AbstractHit && ((AbstractHit) obj).getDocId() == this.getDocId();
    }
}

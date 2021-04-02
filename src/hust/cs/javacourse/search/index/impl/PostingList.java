package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class PostingList extends AbstractPostingList {

    @Override
    public void add(List<AbstractPosting> postings) {
        for (AbstractPosting posting : postings) {
            posting.sort();
            this.add(posting);
        }
    }

    @Override
    public void add(AbstractPosting posting) {
        posting.sort();
        if(this.list != null && !this.list.contains(posting)) {
            this.list.add(posting);
        }
    }

    @Override
    public String toString() {
        if (this.list == null || this.list.isEmpty()) {
            return "";
        }
        Iterator<AbstractPosting> iterator = this.list.iterator();
        StringBuilder str = new StringBuilder("->" + iterator.next().toString());
        while (iterator.hasNext()) {
            str.append("->");
            str.append(iterator.next().toString());
        }
        return str.toString();
    }

    @Override
    public AbstractPosting get(int index) {
        return this.list == null ? null : this.list.get(index);
    }

    @Override
    public int indexOf(int docId) {
        int position = 0;
        for (AbstractPosting posting : this.list) {
            if (posting.getDocId() == docId) {
                return position;
            }
            ++position;
        }
        return -1;
    }

    @Override
    public int indexOf(AbstractPosting posting) {
        return list.indexOf(posting);
    }

    @Override
    public boolean contains(AbstractPosting posting) {
        return this.list != null && this.list.contains(posting);
    }

    @Override
    public void remove(int index) {
        this.list.remove(index);
    }

    @Override
    public void remove(AbstractPosting posting) {
        this.list.remove(posting);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public void sort() {
        Collections.sort(list);
    }

    @Override
    public void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(this.list.size());
            for (AbstractPosting posting : this.list) {
                posting.writeObject(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readObject(ObjectInputStream in) {
        try {
            int size = (Integer) in.readObject();
            for (int i = 0; i < size; ++i) {
                AbstractPosting posting = new Posting();
                posting.readObject(in);
                this.list.add(posting);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

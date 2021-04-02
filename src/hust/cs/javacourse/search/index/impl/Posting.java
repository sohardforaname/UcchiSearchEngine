package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractPosting;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class Posting extends AbstractPosting {

    public Posting() {

    }

    public Posting(int docId, int freq, List<Integer> positions) {
        super(docId, freq, positions);
        sort();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof AbstractPosting)
                && this.getDocId() == ((AbstractPosting) obj).getDocId()
                && this.getFreq() == ((AbstractPosting) obj).getFreq()
                && this.getPositions().equals(((AbstractPosting) obj).getPositions());
    }

    @Override
    public String toString() {
        return "{\"docId\":" + docId + ",\"freq\":" + freq + ",\"positions\":" + positions + "}";
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
    public int getFreq() {
        return this.freq;
    }

    @Override
    public void setFreq(int freq) {
        this.freq = freq;
    }

    @Override
    public List<Integer> getPositions() {
        return this.positions;
    }

    @Override
    public void setPositions(List<Integer> positions) {
        this.positions = positions;
        sort();
    }

    @Override
    public int compareTo(AbstractPosting o) {
        return this.getDocId() - o.getDocId();
    }

    @Override
    public void sort() {
        Collections.sort(this.positions);
    }

    @Override
    public void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(this.docId);
            out.writeObject(this.freq);
            out.writeObject(this.positions.size());
            for (Integer position : positions) {
                out.writeObject(position);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readObject(ObjectInputStream in) {
        try {
            this.docId = (Integer) in.readObject();
            this.freq = (Integer) in.readObject();
            int size = (Integer) in.readObject();
            for (int i = 0; i < size; ++i) {
                this.positions.add((Integer) in.readObject());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Queue;
import java.util.LinkedList;

public class TermTupleScanner extends AbstractTermTupleScanner {
    public TermTupleScanner(BufferedReader bufferedReader) {
        super(bufferedReader);
    }

    Queue<AbstractTermTuple> termTupleQueue = new LinkedList<>();

    int currentPos = 0;

    @Override
    public AbstractTermTuple next() {
        if (termTupleQueue.isEmpty()) {
            String currentLine = null;
            try {
                do {
                    currentLine = input.readLine();
                    if (currentLine == null) {
                        return null;
                    }
                } while (currentLine.trim().length() == 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringSplitter stringSplitter = new StringSplitter();
            stringSplitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);

            for (String currentWord : stringSplitter.splitByRegex(currentLine)) {
                termTupleQueue.add(
                        new TermTuple(
                                new Term(Config.IGNORE_CASE ? currentWord.toLowerCase() : currentWord), currentPos
                        )
                );
                ++currentPos;
            }
        }
        return termTupleQueue.poll();
    }

    @Override
    public void close() {
        super.close();
    }
}

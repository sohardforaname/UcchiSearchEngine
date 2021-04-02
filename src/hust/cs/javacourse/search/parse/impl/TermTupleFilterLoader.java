package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TermTupleFilterLoader {

    List<String> filterList = new ArrayList<>();

    public TermTupleFilterLoader() {

    }

    public TermTupleFilterLoader(List<String> filterList) {
        this.filterList = filterList;
    }

    public void loadConfig(File file) {
        try {
            InputStream stream = new FileInputStream(file);
            Properties config = new Properties();
            config.load(stream);
            int size = Integer.parseInt(config.getProperty("filterSize"));
            for (int i = 0; i < size; ++i) {
                filterList.add(config.getProperty("f" + i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AbstractTermTupleStream setFilter(AbstractTermTupleStream stream) {
        AbstractTermTupleStream res = stream;
        for (String filter : filterList) {
            try {
                AbstractTermTupleFilter filter1 = (AbstractTermTupleFilter) Class.forName("hust.cs.javacourse.search.parse.impl." + filter).newInstance();
                filter1.setStream(res);
                res = filter1;
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
}

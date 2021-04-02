package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;
import hust.cs.javacourse.search.util.Pair;

import java.io.*;
import java.util.*;

/**
 * AbstractIndex的具体实现类
 */
public class Index extends AbstractIndex {
    /**
     * 返回索引的字符串表示
     *
     * @return 索引的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("docId->docPath mapping: \n");
        for (Map.Entry<Integer, String> entry : docIdToDocPathMapping.entrySet()) {
            builder.append(entry.getKey());
            builder.append("->");
            builder.append(entry.getValue());
            builder.append("\n");
        }
        builder.append("PostingList: \n");
        for (Map.Entry<AbstractTerm, AbstractPostingList> entry : termToPostingListMapping.entrySet()) {
            builder.append(entry.getKey().toString());//获得所有posting
            builder.append(entry.getValue().toString());
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * 添加文档到索引，更新索引内部的HashMap
     *
     * @param document ：文档的AbstractDocument子类型表示
     */
    @Override
    public void addDocument(AbstractDocument document) {
        this.docIdToDocPathMapping.put(document.getDocId(), document.getDocPath());
        for (AbstractTermTuple termTuple : document.getTuples()) {
            if (termToPostingListMapping.containsKey(termTuple.term)) {
                AbstractPostingList list = termToPostingListMapping.get(termTuple.term);
                int position = list.indexOf(document.getDocId());
                if (position != -1) {
                    AbstractPosting posting = list.get(position);
                    posting.setFreq(list.get(position).getFreq() + 1);
                    posting.getPositions().add(termTuple.curPos);
                } else {
                    list.add(new Posting(document.getDocId(), 1, new ArrayList<Integer>() {{
                        add(termTuple.curPos);
                    }}));
                }
            } else {
                AbstractPostingList list = new PostingList();
                termToPostingListMapping.put(termTuple.term, list);
                list.add(new Posting(document.getDocId(), 1, new ArrayList<Integer>() {{
                    add(termTuple.curPos);
                }}));
            }
        }
        optimize();
    }

    /**
     * <pre>
     * 从索引文件里加载已经构建好的索引.内部调用FileSerializable接口方法readObject即可
     * @param file ：索引文件
     * </pre>
     */
    @Override
    public void load(File file) throws NullPointerException {
        if (file == null) {
            throw new NullPointerException();
        }
        try {
            this.readObject(new ObjectInputStream(new FileInputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * 将在内存里构建好的索引写入到文件. 内部调用FileSerializable接口方法writeObject即可
     * @param file ：写入的目标索引文件
     * </pre>
     */
    @Override
    public void save(File file) throws NullPointerException {
        if (file == null) {
            throw new NullPointerException();
        }
        try {
            this.writeObject(new ObjectOutputStream(new FileOutputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回指定单词的PostingList
     *
     * @param term : 指定的单词
     * @return ：指定单词的PostingList;如果索引字典没有该单词，则返回null
     */
    @Override
    public AbstractPostingList search(AbstractTerm term) {
        return termToPostingListMapping == null ? null : termToPostingListMapping.get(term);
    }

    /**
     * 返回索引的字典.字典为索引里所有单词的并集
     *
     * @return ：索引中Term列表
     */
    @Override
    public Set<AbstractTerm> getDictionary() {
        return this.termToPostingListMapping == null ? null : this.termToPostingListMapping.keySet();
    }

    /**
     * <pre>
     * 对索引进行优化，包括：
     *      对索引里每个单词的PostingList按docId从小到大排序
     *      同时对每个Posting里的positions从小到大排序
     * 在内存中把索引构建完后执行该方法
     * </pre>
     */
    @Override
    public void optimize() {
        for (Map.Entry<AbstractTerm, AbstractPostingList> entry : termToPostingListMapping.entrySet()) {
            entry.getValue().sort();
        }
    }

    /**
     * 根据docId获得对应文档的完全路径名
     *
     * @param docId ：文档id
     * @return : 对应文档的完全路径名
     */
    @Override
    public String getDocName(int docId) {
        return this.docIdToDocPathMapping == null ? null : this.docIdToDocPathMapping.get(docId);
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(docIdToDocPathMapping.size());
            for (Map.Entry<Integer, String> entry : docIdToDocPathMapping.entrySet()) {
                out.writeObject(entry.getKey());
                out.writeObject(entry.getValue());
            }
            out.writeObject(termToPostingListMapping.size());
            for (Map.Entry<AbstractTerm, AbstractPostingList> entry : termToPostingListMapping.entrySet()) {
                entry.getKey().writeObject(out);
                entry.getValue().writeObject(out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从二进制文件读
     *
     * @param in ：输入流对象
     */
    @Override
    public void readObject(ObjectInputStream in) {
        try {
            int size = (Integer) in.readObject();
            for (int i = 0; i < size; ++i) {
                docIdToDocPathMapping.put((Integer) in.readObject(), (String) in.readObject());
            }
            size = (Integer) in.readObject();
            for (int i = 0; i < size; ++i) {
                AbstractTerm term = new Term();
                term.readObject(in);
                AbstractPostingList postingList = new PostingList();
                postingList.readObject(in);
                termToPostingListMapping.put(term, postingList);
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public List<AbstractTerm> getSimilarTerm(AbstractTerm term, int size) {
        PriorityQueue<Map.Entry<AbstractTerm, Integer>> entryHeap =
                new PriorityQueue<>((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<Integer> integers = new ArrayList<>();

        for (AbstractTerm term1 : termToPostingListMapping.keySet()) {
            int currentTermDistance = term.calculateDistance(term1);
            integers.add(currentTermDistance);

            if (entryHeap.size() < size) {
                entryHeap.add(new Pair<>(term1, currentTermDistance));
            } else if (entryHeap.element().getValue() > currentTermDistance) {
                entryHeap.poll();
                entryHeap.add(new Pair<>(term1, currentTermDistance));
            }
        }

        List<AbstractTerm> list = new ArrayList<>();
        entryHeap.forEach(entry -> list.add(entry.getKey()));
        Collections.reverse(list);
        return list;
    }
}


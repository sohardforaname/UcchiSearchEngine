package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractTerm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Term extends AbstractTerm {

    public Term() {

    }

    public Term(String content) {
        super(content);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof AbstractTerm)
                && this.content.equals(((AbstractTerm) obj).getContent());
    }

    @Override
    public String toString() {
        return this.content;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo(AbstractTerm o) {
        return this.content.compareTo(o.getContent());
    }

    @Override
    public void readObject(ObjectInputStream in) {
        try {
            this.setContent((String) in.readObject());
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeObject(ObjectOutputStream out) {
        try {
            out.writeObject(this.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int calculateDistance(AbstractTerm term) {
        int length1 = this.content.length(), length2 = term.getContent().length();
        if (length1 == 0 || length2 == 0) {
            return Integer.max(length1, length2);
        }
        int[][] dp = new int[this.getContent().length() + 1][term.getContent().length() + 1];
        for (int i = 0; i <= length1; ++i) {
            for (int j = 0; j <= length2; ++j) {
                dp[i][j] = Integer.MAX_VALUE / 2;
            }
        }
        byte[] bytes1 = this.content.getBytes(), bytes2 = term.getContent().getBytes();
        for (int i = 0; i <= length1; ++i) {
            for (int j = 0; j <= length2; ++j) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Integer.min(dp[i][j], dp[i - 1][j] + 1);
                    dp[i][j] = Integer.min(dp[i][j], dp[i][j - 1] + 1);
                    dp[i][j] = Integer.min(dp[i][j], dp[i - 1][j - 1] + (Byte.compare(bytes1[i - 1], bytes2[j - 1]) == 0 ? 0 : 1));
                }
            }
        }
        return dp[bytes1.length][bytes2.length];
    }
}

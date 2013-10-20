package org.societies.orchestration.cpa.impl.comparison.model;

/**
 * Created with IntelliJ IDEA.
 * User: bjornmagnus
 * Date: 13/09/13
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public class Trend {
    private String word="";
    private int rank;
    private int count;

    public Trend(String word, int rank, int count){
        this.word = word; this.rank = rank; this.count = count;
    }
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}


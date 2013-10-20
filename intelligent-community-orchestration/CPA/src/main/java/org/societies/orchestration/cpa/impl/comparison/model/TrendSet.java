package org.societies.orchestration.cpa.impl.comparison.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: epic
 * Date: 13/09/13
 * Time: 14:17
 * To change this template use File | Settings | File Templates.
 */
public class TrendSet {
    private List<Trend> trends = new ArrayList<Trend>();
    private String id = "";
    private int returnCount = 2;

    public TrendSet(String id)
    {
        this.id = id;
    }

    public List<Trend> getTrends() {
        return trends;
    }

    public void setTrends(List<Trend> trends) {
        this.trends = trends;
    }

    public String getId() {
        return id;
    }

    public void addTrend(Trend trend){
        trends.add(trend);
    }

    public void setId(String id) {
        this.id = id;
    }
    public int getReturnCount(){
        return returnCount;
    }

    public void setReturnCount(int returnCount) {
        this.returnCount = returnCount;
    }

}

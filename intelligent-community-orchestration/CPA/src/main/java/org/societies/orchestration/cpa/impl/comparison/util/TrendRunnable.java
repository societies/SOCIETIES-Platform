package org.societies.orchestration.cpa.impl.comparison.util;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivity;
import org.societies.api.internal.orchestration.ICisDataCollector;
import org.societies.api.internal.orchestration.IDataCollectorSubscriber;
import org.societies.orchestration.cpa.impl.CPA;
import org.societies.orchestration.cpa.impl.comparison.model.Trend;
import org.societies.orchestration.cpa.impl.comparison.model.TrendSet;
import org.societies.orchestration.cpa.impl.comparison.model.TrendStats;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: epic
 * Date: 08/10/13
 * Time: 23:04
 * To change this template use File | Settings | File Templates.
 */
public class TrendRunnable implements Runnable,IDataCollectorSubscriber {
    List<IActivity> workList = null;

    private HashMap<String,TrendStats> trends = null;
    private ICisDataCollector collector;
    private boolean wordTrends = true;
    private int called = 0;
    protected static Logger LOG = LoggerFactory.getLogger(TrendRunnable.class);
    public TrendRunnable(){
        trends = new HashMap<String,TrendStats>();
        workList = new ArrayList<IActivity>();
    }
    public void setWorkList(List<IActivity> list){
        workList = CPA.deepCopyActivities(list);
    }
    MaxentTagger tagger = new MaxentTagger(
            "english-left3words-distsim.tagger");
    public synchronized void handleTrends (String inp, Long timestamp){
        called++;
        String[] finalText = {inp};
        HashSet<String> hashSet = new HashSet<String>();
        String deUrlifyTmp[] = inp.split(" ");
        inp = "";
        for(String s : deUrlifyTmp){
            if(s.contains("http")){
                hashSet.add(s);
            }else{
                inp += " "+s;
            }
        }
        inp = inp.trim();
        inp = inp.replaceAll("\'", "");
        String sample = inp.replaceAll("\\W", " ");
        if(sample.trim().length() == 0 ) //just whitespaces.
                return;
        // The tagged string
        String tagged = null;
        try{
            tagged = tagger.tagTokenizedString(sample);
        }catch (IndexOutOfBoundsException e){
            LOG.error("IndexOutOfBoundsException on sample : \""+sample+"\"",e);
        }
        if(tagged == null)
            return;

        String[] x = tagged.split(" ");

        ArrayList<String> list = new ArrayList<String>();

        for(int i=0; i<x.length; i++)
        {
            if (x[i].substring(x[i].lastIndexOf("_")+1).startsWith("N"))
            {
                if(x[i].split("_")[0].length()!=0)
                    list.add(x[i].split("_")[0]);
            }
        }

        for(String text: list)
            if(!hashSet.contains(text))
                hashSet.add(text.toLowerCase());

        for(String text : hashSet){
            if(text.length()<=2)
                continue;
            if(getTrends().containsKey(text)){ //cannot be incremented twice in one activity!
                getTrends().get(text).increment(timestamp);
            } else {
                TrendStats ts = new TrendStats(timestamp);
                ts.setTrendText(text);
                getTrends().put(text,ts);
            }

        }
        //cleanup
        String tmpKey;
        for(Iterator<String> it = getTrends().keySet().iterator() ; it.hasNext();) {
            tmpKey = it.next();
            if(getTrends().get(tmpKey).tooOld()){
                it.remove();
            }
        }
    }
    @Override
    public void run() {
        while (true) {
            try {
                synchronized (workList){
                    for(IActivity iActivity : workList)
                        handleTrends(iActivity.getObject(),Long.parseLong(iActivity.getPublished()));
                    workList.clear();
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public HashMap<String, TrendStats> getTrends() {
        return trends;
    }

    public void setTrends(HashMap<String, TrendStats> trends) {
        this.trends = trends;
    }

    public ICisDataCollector getCollector() {
        return collector;
    }

    public void setCollector(ICisDataCollector collector) {
        this.collector = collector;
        if(this.collector != null)
            this.collector.subscribe("id",this); //TODO change id
    }

    @Override
    public void receiveNewData(List<?> objects) {
        this.setWorkList(safeCast(objects));
    }
    public List<IActivity> safeCast(List<?> inp){
        List<IActivity> ret = new ArrayList<IActivity>();
        try{
            List<IActivity> castTry = (List<IActivity>) inp;
            ret.addAll(castTry);
        } catch (ClassCastException e){

        }
        return ret;
    }
    public class TrendSorter implements Comparator<TrendStats> {

        @Override
        public int compare(TrendStats o1, TrendStats o2) {
            return (o1.getCount()==o2.getCount()) ? 0 : ( (o1.getCount()>o2.getCount()) ? -1 : 1) ; //returns 0 if they are equal..
        }

    }

    public List<String> topTrends(int n)
    {
        List<String> ret = new ArrayList<String>();
        HashMap<String,TrendStats> trends = getTrends();
        int m = (n>trends.keySet().size()) ? trends.keySet().size() : n;
        List<TrendStats> values = new ArrayList<TrendStats>();
        values.addAll(trends.values());
        System.out.println("m: "+m);
        Collections.sort(values,new TrendSorter());
        for(int i=0;i<m;i++){
            ret.add(values.get(i).getTrendText());
        }
        return ret;

    }
    public TrendSet getTrendSet()
    {
        TrendSet ret = new TrendSet(Integer.toString(this.hashCode()));
        HashMap<String,TrendStats> trends = getTrends();
        List<TrendStats> values = new ArrayList<TrendStats>();
        values.addAll(trends.values());
        Collections.sort(values,new TrendSorter());
        TrendStats ts = null;
        for(int i=0;i<trends.size();i++){
            ts = values.get(i);
            ret.addTrend(new Trend(ts.getTrendText(),i+1,ts.getCount()));
        }
        return ret;

    }
    public List<TrendStats> getTrendStats(int n)
    {
        List<TrendStats> ret = new ArrayList<TrendStats>();
        HashMap<String,TrendStats> trends = getTrends();
        List<TrendStats> values = new ArrayList<TrendStats>();
        values.addAll(trends.values());
        Collections.sort(values,new TrendSorter());
        for(int i=0;i<n && i<values.size();i++)
            ret.add(values.get(i));
        return ret;

    }
}

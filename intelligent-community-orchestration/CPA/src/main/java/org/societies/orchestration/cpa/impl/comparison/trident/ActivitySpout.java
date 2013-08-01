package org.societies.orchestration.cpa.impl.comparison.trident;

import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import org.societies.activity.model.Activity;
import storm.trident.operation.TridentCollector;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: epic
 * Date: 5/16/13
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
public class ActivitySpout implements storm.trident.spout.IBatchSpout {
    private List<Activity> acts=null;
    public ActivitySpout(ArrayList<Activity> startActs){
        acts = new ArrayList<Activity>();
        acts.addAll(startActs);
    }
    public void pushActivities(List<Activity> newActs){
        acts.addAll(newActs);
    }

    @Override
    public void open(Map map, TopologyContext topologyContext) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void emitBatch(long l, TridentCollector tridentCollector) {
        ArrayList emitList = new ArrayList();
        //make sure emitlist is
        Iterator<Activity> it = acts.iterator();
        while(!acts.isEmpty() && emitList.size()<l){ //TODO: hmm seems to easy
            emitList.add(it.next());
            it.remove();
        }
        tridentCollector.emit(emitList);
    }

    @Override
    public void ack(long l) {
        //TODO: what should this do...?
    }

    @Override
    public void close() {
        //TODO: what should this do...? I think this is moot for my spout
    }

    @Override
    public Map getComponentConfiguration() {
        return new HashMap();
    }

    @Override
    public Fields getOutputFields() {
        //Fields ret = new Fields();
        return new Fields("text");  //To change body of implemented methods use File | Settings | File Templates.
    }
}

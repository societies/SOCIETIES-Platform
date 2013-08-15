package org.societies.orchestration.cpa.impl.comparison;

import backtype.storm.tuple.Fields;
import com.github.pmerienne.trident.ml.nlp.TwitterSentimentClassifier;
import org.societies.api.activity.IActivity;
import org.societies.orchestration.cpa.impl.SocialGraphVertex;
import org.societies.orchestration.cpa.impl.comparison.trident.ActivitySpout;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Count;
import storm.trident.testing.MemoryMapState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: bjornmagnus.mathisen@sintef.no
 * Date: 5/15/13
 * Time: 11:04
 */
public class SentimentComparator implements ActorComparator {
    private ActivitySpout activitySpout = new ActivitySpout(null);
    public void init(ArrayList<String> strings){
        //FixedBatchSpout heh = new FixedBatchSpout(new Fields("actitivies"),3,new);
    }
    @Override
    public double compare(SocialGraphVertex member1, SocialGraphVertex member2, List<IActivity> activityDiff) {
        TridentTopology topology = new TridentTopology();
        TridentState wordCounts =
                topology.newStream("spout1", activitySpout)
                        .each(new Fields("sentence"), new TwitterSentimentClassifier(), new Fields("word"))
                        //.groupBy(new Fields("word"))
                        .persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("sentiment"))
                        .parallelismHint(6);
// Classification stream
/*        toppology.newDRPCStream("classify", localDRPC)
                // Query classifier with text instance
                .each(new Fields("args"), new TwitterSentimentClassifier(), new Fields("sentiment"));*/
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

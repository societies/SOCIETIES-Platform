package org.societies.activity;

import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.schema.activityfeed.AddActivityResponse;
import org.societies.api.schema.activityfeed.CleanUpActivityFeedResponse;
import org.societies.api.schema.activityfeed.DeleteActivityResponse;
import org.societies.api.schema.activityfeed.GetActivitiesResponse;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: epic
 * Date: 3/13/13
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public class ActivityFeedWorker implements Callable<Void>  {
    final IActivityFeedCallback c;
    final ActivityFeed activityFeed;
    public ActivityFeedWorker(final IActivityFeedCallback c, final ActivityFeed activityFeed) {
        this.c = c;
        this.activityFeed = activityFeed;
    }

    @Override
    public Void call() throws Exception {

        return null;
    }
    public void asyncAddActivity(final IActivity activity){
        Thread t = new Thread(new Runnable(){

            @Override
            public void run() {
                org.societies.api.schema.activityfeed.MarshaledActivityFeed result = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
                AddActivityResponse r = new AddActivityResponse();

                activityFeed.addActivity(activity);
                r.setResult(true); //TODO. add a return on the activity feed method


                result.setAddActivityResponse(r);
                c.receiveResult(result);
            }
        });
        t.start();

    }
    public void asyncCleanupFeed(final String criteria){
        Thread t = new Thread(new Runnable(){

            @Override
            public void run() {
                org.societies.api.schema.activityfeed.MarshaledActivityFeed result = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
                CleanUpActivityFeedResponse r = new CleanUpActivityFeedResponse();

                r.setResult(activityFeed.cleanupFeed(criteria));


                result.setCleanUpActivityFeedResponse(r);
                c.receiveResult(result);
            }

        });
        t.start();
    }
    public void asyncDeleteActivity(final IActivity activity){
        Thread t = new Thread(new Runnable(){

            @Override
            public void run() {
                org.societies.api.schema.activityfeed.MarshaledActivityFeed result = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
                DeleteActivityResponse r = new DeleteActivityResponse();

                r.setResult(activityFeed.deleteActivity(activity));

                result.setDeleteActivityResponse(r);
                c.receiveResult(result);
            }

        });
        t.start();
    }
    public void asyncGetActivities(final String query, final String timePeriod){
        Thread t = new Thread(new Runnable(){

            @Override
            public void run() {
                List<IActivity> iActivityList = activityFeed.getActivities(query, timePeriod);
                org.societies.api.schema.activityfeed.MarshaledActivityFeed ac = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
                GetActivitiesResponse g = new GetActivitiesResponse();
                ac.setGetActivitiesResponse(g);

                List<org.societies.api.schema.activity.MarshaledActivity> marshalledActivList = new ArrayList<org.societies.api.schema.activity.MarshaledActivity>();

                ActivityFeedWorker.iactivToMarshActvList(iActivityList, marshalledActivList);

                g.setMarshaledActivity(marshalledActivList);

                c.receiveResult(ac);
            }

        });
        t.start();
    }
    public void asyncGetActivities(final String timePeriod, final long n){
        Thread t = new Thread(new Runnable(){

            @Override
            public void run() {
                List<IActivity> iActivityList = activityFeed.getActivities(timePeriod);
                List<IActivity> ret = new ArrayList<IActivity>();
                Collections.sort(iActivityList, new Comparator<IActivity>() {
                    @Override
                    public int compare(IActivity iActivity, IActivity iActivity1) {
                        return (Long.parseLong(iActivity.getPublished()) > Long.parseLong(iActivity1.getPublished())) ? 1 : -1;
                    }
                });
                long tempN = n;
                if(iActivityList.size()<tempN)
                    tempN = iActivityList.size();
                for(int i=0;i<tempN;i++)
                    ret.add(iActivityList.get(i));

                org.societies.api.schema.activityfeed.MarshaledActivityFeed ac = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
                GetActivitiesResponse g = new GetActivitiesResponse();
                ac.setGetActivitiesResponse(g);

                List<org.societies.api.schema.activity.MarshaledActivity> marshalledActivList = new ArrayList<org.societies.api.schema.activity.MarshaledActivity>();

                ActivityFeedWorker.iactivToMarshActvList(ret, marshalledActivList);

                g.setMarshaledActivity(marshalledActivList);

                c.receiveResult(ac);
            }

        });
        t.start();

    }
    public void asyncGetActivities(final String timePeriod) {
        Thread t = new Thread(new Runnable(){

            @Override
            public void run() {
                List<IActivity> iActivityList = activityFeed.getActivities(timePeriod);
                org.societies.api.schema.activityfeed.MarshaledActivityFeed ac = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
                GetActivitiesResponse g = new GetActivitiesResponse();
                ac.setGetActivitiesResponse(g);

                List<org.societies.api.schema.activity.MarshaledActivity> marshalledActivList = new ArrayList<org.societies.api.schema.activity.MarshaledActivity>();

                ActivityFeedWorker.iactivToMarshActvList(iActivityList, marshalledActivList);

                g.setMarshaledActivity(marshalledActivList);

                c.receiveResult(ac);

            }
        });
        t.start();
    }
    public void asyncGetActivities(final String query, final String timePeriod, final long n) {
        Thread t = new Thread(new Runnable(){

            @Override
            public void run() {
                List<IActivity> iActivityList = activityFeed.getActivities(query,timePeriod);
                List<IActivity> ret = new ArrayList<IActivity>();
                Collections.sort(iActivityList,new Comparator<IActivity>() {
                    @Override
                    public int compare(IActivity iActivity, IActivity iActivity1) {
                        return (Long.parseLong(iActivity.getPublished())>Long.parseLong(iActivity1.getPublished())) ? 1 : -1;
                    }
                });
                for(int i=0;i<n;i++)
                    ret.add(iActivityList.get(i));

                org.societies.api.schema.activityfeed.MarshaledActivityFeed ac = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
                GetActivitiesResponse g = new GetActivitiesResponse();
                ac.setGetActivitiesResponse(g);

                List<org.societies.api.schema.activity.MarshaledActivity> marshalledActivList = new ArrayList<org.societies.api.schema.activity.MarshaledActivity>();

                ActivityFeedWorker.iactivToMarshActvList(ret, marshalledActivList);

                g.setMarshaledActivity(marshalledActivList);

                c.receiveResult(ac);
            }
        });
        t.start();
    }
    public static org.societies.api.schema.activity.MarshaledActivity iactivToMarshActiv(IActivity iActivity){
        org.societies.api.schema.activity.MarshaledActivity a = new org.societies.api.schema.activity.MarshaledActivity();
        a.setActor(iActivity.getActor());
        a.setVerb(iActivity.getVerb());
        if(iActivity.getObject()!=null && iActivity.getObject().isEmpty() == false )
            a.setObject(iActivity.getObject());
        if(iActivity.getPublished()!=null && iActivity.getPublished().isEmpty() == false )
            a.setPublished(iActivity.getPublished());

        if(iActivity.getTarget()!=null && iActivity.getTarget().isEmpty() == false )
            a.setTarget(iActivity.getTarget());
        return a;
    }
    public static void iactivToMarshActvList(List<IActivity> iActivityList, List<org.societies.api.schema.activity.MarshaledActivity> marshalledActivList){

        Iterator<IActivity> it = iActivityList.iterator();

        while(it.hasNext()){
            IActivity element = it.next();
            marshalledActivList.add(iactivToMarshActiv(element));
        }
    }
}

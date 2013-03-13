/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.activity;

import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.model.Activity;
import org.societies.activity.model.ActivityString;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.activity.ILocalActivityFeed;
import org.societies.api.schema.activityfeed.AddActivityResponse;
import org.societies.api.schema.activityfeed.CleanUpActivityFeedResponse;
import org.societies.api.schema.activityfeed.DeleteActivityResponse;
import org.societies.api.schema.activityfeed.GetActivitiesResponse;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@Table(name = "org_societies_activity_ActivityFeed")
public class ActivityFeed implements IActivityFeed, ILocalActivityFeed {

    protected String owner;
    @Id
    private String id;// represents the owner of the activity feed
    @OneToMany
    protected
    Set<Activity> list;
    @Transient
    private SessionFactory sessionFactory;
    @Transient
    protected static Logger LOG = LoggerFactory.getLogger(ActivityFeed.class);

    @Transient
    private PubsubClient pubSubcli;
    @Transient
    private IIdentity ownerCSS;
    public ActivityFeed(String id, String owner){
        this.owner = owner; this.setId(id);
        initClass();
    }
    public ActivityFeed(){
        initClass();
    }
    public void initClass(){
        list = new HashSet<Activity>();// from Thomas

    }


    private final static List<String> classList = Collections
            .unmodifiableList( Arrays.asList("org.societies.api.schema.activity.MarshaledActivity"));


    // version with PubSub
    synchronized public void startUp(SessionFactory sessionFactory){
        this.setSessionFactory(sessionFactory);
        this.setPubSubcli(pubSubcli);
    }
    public void connectPubSub(IIdentity ownerCSS){ //ASSUME PUBSUB NODE PERSISTING (CONFIGURATION), CHECK IF IT EXISTS
        this.ownerCSS = ownerCSS;
        // pubsub code
        LOG.debug("starting pubsub at activityfeed pubsub");
        if(null != pubSubcli && null != ownerCSS){
//        	try {
//				pubSubcli.addSimpleClasses(classList);
//			} catch (ClassNotFoundException e1) {
//				LOG.warn("error adding classes at pubsub at activityfeed pubsub");
//				e1.printStackTrace();
//
//			}
            List<String> l = null;
            try {
                l = pubSubcli.discoItems(ownerCSS, null);
            } catch (XMPPError e) {
                LOG.warn("XMPPError at activityfeed pubsub");
                e.printStackTrace();
                return;
            } catch (CommunicationException e) {
                LOG.warn("Com at activityfeed pubsub");
                e.printStackTrace();
                return;
            }
            boolean nodeExists = false;
            if(l.size() == 0)
                LOG.warn("empty disco item list");

            if(l != null && l.size()>0){
                for(String temp : l){
                    LOG.warn("Existing node is " + temp);
                    if (temp.equals(this.getId()))
                        nodeExists=true;
                }

            }
            if(false == nodeExists){
                try {
                    LOG.warn("going to create a pubsub node");
                    pubSubcli.ownerCreate(ownerCSS, this.getId());
                } catch (XMPPError e) {
                    LOG.warn("XMPPError at activityfeed pubsub");
                    e.printStackTrace();
                } catch (CommunicationException e) {
                    LOG.warn("Com at activityfeed pubsub");
                    e.printStackTrace();
                }
            }else{
                LOG.warn("node exists");
            }
        }
    }



    public int count(){
        Session session = null;
        LOG.info("in persistedactivityfeedcount");
        int ret = -1;
        try {
            session = this.getSessionFactory().openSession();
            ret = session.createCriteria(Activity.class).add(Restrictions.eq("ownerId",this.getId())).list().size();
        }catch  (Exception e)
        {
            LOG.error("Error while trying to count activities");
            e.printStackTrace();
        } finally {
            if (session != null)
                session.close();
        }
        return ret;
    }
    @Override
    public void addActivity(IActivity activity) {
//        LOG.error("In addActivity for PeristedActivityFeed published:"+activity.getPublished()+" time: "+activity.getTime());
        boolean err = false;
        Activity newAct = new Activity(activity);
        newAct.setPublished(Long.toString(new Date().getTime())); // NOTICE THAT THE TIME IS BEING SET IN THE SERVER
        LOG.info("adding activity with id: "+ this.getId());
        newAct.setOwnerId(this.getId());
        long actv_id = 0;
        Session session = null;
        Transaction t = null;
        try{
            session = this.getSessionFactory().openSession();
            t = session.beginTransaction();
            actv_id = (Long) session.save(newAct);
            t.commit();
        }catch(Exception e){
            e.printStackTrace();
            if (t != null)
                t.rollback();
            LOG.warn("Saving activity failed, rolling back");
            err = true;
        }finally{
            if (session != null)
                session.close();

        }

        // Publishing TO PUBSUB
        if(false == err && getPubSubcli() !=null){
            try {
                LOG.info("going to call pubsub");
                getPubSubcli().publisherPublish(this.ownerCSS, this.getId(), Long.toString(actv_id), iactivToMarshActiv(newAct));
            } catch (XMPPError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (CommunicationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        LOG.info("done publishing activity on pubsub");

    }

    @Override
    public void addActivity(IActivity activity, IActivityFeedCallback c) {
        org.societies.api.schema.activityfeed.MarshaledActivityFeed result = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
        AddActivityResponse r = new AddActivityResponse();

        this.addActivity(activity);
        r.setResult(true); //TODO. add a return on the activity feed method

        result.setAddActivityResponse(r);
        LOG.debug("going to call callback from addActivity with result " + r.isResult());
        c.receiveResult(result);

    }

    synchronized public int cleanupFeed(String criteria) {
        int ret = 0;
        String forever = "0 "+Long.toString(System.currentTimeMillis());
        List<IActivity> toBeDeleted = getActivities(criteria,forever);
        Session session = null;
        Transaction t = null;
        try{
            session = getSessionFactory().openSession();
            t = session.beginTransaction();

            for(IActivity act : toBeDeleted){
                session.delete((Activity)act);
            }

            t.commit();
        }catch(Exception e){
            if (t != null)
                t.rollback();
            e.printStackTrace();
            LOG.warn("deleting activities failed, rolling back");
        }finally{
            if (session != null)
                session.close();

        }

        return ret;
    }

    @Override
    public void cleanupFeed(String criteria, IActivityFeedCallback c) {
        org.societies.api.schema.activityfeed.MarshaledActivityFeed result = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
        CleanUpActivityFeedResponse r = new CleanUpActivityFeedResponse();

        r.setResult(this.cleanupFeed(criteria));


        result.setCleanUpActivityFeedResponse(r);
        c.receiveResult(result);


    }

    public void init()
    {
        LOG.info("in activityfeed init");
    }

    public void close()
    {
        LOG.info("in activityfeed close");
    }

    public boolean deleteActivity(IActivity activity) {
        if(!list.contains(activity))
            return false;
        boolean ret = list.remove(activity);
        Session session = null;
        Transaction t = null;
        try{
            session = getSessionFactory().openSession();
            t = session.beginTransaction();

            session.delete(activity);
            t.commit();
        }catch (Exception e){
            if (t != null)
                t.rollback();
            e.printStackTrace();
            LOG.error("Error when trying to delete activity");
        }finally {
            if(session!=null)
                session.close();
        }
        return ret;
    }

    @Override
    public void deleteActivity(IActivity activity, IActivityFeedCallback c) {
        org.societies.api.schema.activityfeed.MarshaledActivityFeed result = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
        DeleteActivityResponse r = new DeleteActivityResponse();

        r.setResult(this.deleteActivity(activity));

        result.setDeleteActivityResponse(r);
        c.receiveResult(result);

    }

    @Override
    synchronized public long importActivityEntries(List<?> activityEntries) {
        long ret = 0;
        if(activityEntries.size() == 0){
            LOG.error("list is empty, exiting");
            return ret;
        }
        if(!ActivityEntry.class.isInstance(activityEntries.get(0))){ //just checking the first entry.
            LOG.error("first instance in the given list is not of type ActivityEntry, exiting");
            return ret;
        }
        List<ActivityEntry> castedList = (List<ActivityEntry>) activityEntries;
        Activity newAct = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        ParsePosition pp = new ParsePosition(0);
        Session session = null;
        Transaction t = null;
        try{

            session = getSessionFactory().openSession();
            t = session.beginTransaction();

            for(ActivityEntry act : castedList){
                pp.setIndex(0);
                newAct = new Activity();
                newAct.setActor(getContentIfNotNull(act.getActor()));
                newAct.setOwnerId(this.getId());
                newAct.setObject(getContentIfNotNull(act.getObject()));
                newAct.setPublished(Long.toString(df.parse(act.getPublished(),pp).getTime()));
                newAct.setTarget(getContentIfNotNull(act.getTarget()));
                newAct.setVerb(act.getVerb());
                ret++;
                session.save(newAct);
            }
            t.commit();
        }catch(Exception e){
            if (t != null)
                t.rollback();
            LOG.warn("Importing of activities from social data failed..");
            e.printStackTrace();

        }finally{
            if(session!=null)
                session.close();
        }

        return ret;
    }

    @Override
    public IActivity getEmptyIActivity(){
        Activity a = new Activity();
        return a;
    }

    //timeperiod: "millisecondssinceepoch millisecondssinceepoch+n"
    //where n has to be equal to or greater than 0
    public List<IActivity> getActivities(String timePeriod) {
        LOG.info("in persisted activityfeed getActivities, gettin data from DB ownerID:"+this.getId());
        ArrayList<IActivity> ret = new ArrayList<IActivity>();
        String times[] = timePeriod.split(" ",2);
        if(times.length < 2){
            LOG.error("timeperiod string was malformed: "+timePeriod);
            return ret;
        }
        long fromTime = 0;long toTime = 0;
        try{
            fromTime = Long.parseLong(times[0]);
            toTime = Long.parseLong(times[1]);
        }catch(Exception e){
            LOG.error("time period string was malformed, could not parse long");
            return ret;
        }
        Session session = null;
        List<Activity> retList = null;
        try{
            session = this.getSessionFactory().openSession();

            retList = session.createCriteria(Activity.class).add(Restrictions.gt("time", new Long(fromTime))).add(Restrictions.lt("time", new Long(toTime))).add(Restrictions.eq("ownerId",this.getId())).list();
            LOG.info(" list size: "+retList.size()+" no criteria: "+session.createCriteria(Activity.class).list().size());
            LOG.info(" FISK "+session.createCriteria(Activity.class).add(Restrictions.gt("time", new Long(fromTime))).add(Restrictions.lt("time", new Long(toTime))).list().size());
        } catch (Exception e) {
            LOG.error("getting activities query failed: ");
            e.printStackTrace();

        } finally {
                if (session != null)
                session.close();
        }

        LOG.info("time period: "+fromTime+" - " + toTime);
        if(retList != null){
            for(Activity act : retList){
                if(Long.parseLong(act.getPublished())>=fromTime && Long.parseLong(act.getPublished())<=toTime){
                    act.repopHash();
                    ret.add(act);
/*                    System.out.println("adding: actor: "+act
                            .getActor()+" time: "+act.getPublished());*/
                }
            }

        }
        return ret;
    }

    public List<IActivity> getActivities(String query, String timePeriod) {
        ArrayList<IActivity> ret = new ArrayList<IActivity>();
        List<IActivity> tmp = this.getActivities(timePeriod);
        if(tmp.size()==0) {
            LOG.error("time period did not contain any activities");
            return ret;
        }
        //start parsing query..
        JSONObject arr = null;
        try {
            arr = new JSONObject(query);
        } catch (JSONException e) {
            LOG.error("Error parsing JSON");
            e.printStackTrace();
            return ret;
        }
        LOG.info("loaded JSON");
        String methodName; String filterBy; String filterValue;
        try {
            methodName = (new JSONArray(arr.getString("filterOp"))).getString(0);
            filterBy = (new JSONArray(arr.getString("filterBy"))).getString(0);
            filterValue = (new JSONArray(arr.getString("filterValue"))).getString(0);
        } catch (JSONException e1) {
            LOG.error("Error parsing JSON");
            e1.printStackTrace();
            return ret;
        }
        LOG.info("loaded JSON values");
        Method method = null;
        try {
            method = ActivityString.class.getMethod(methodName, String.class);
        } catch (SecurityException e) {
            LOG.error("Security error getting filtering method for string");
            return ret;
        } catch (NoSuchMethodException e) {
            LOG.error("No such filterOp: "+methodName+ " we do however have: ");
            for(Method m : ActivityString.class.getMethods()){
                LOG.error(m.getName());
            }
            return ret;
        }
        LOG.info("created method");
        //filter..
        try {
            for(IActivity act : tmp){
                if((Boolean)method.invoke(((Activity)act).getValue(filterBy),filterValue) ){

                    ret.add(act);
                }
            }
            LOG.error("((Activity)tmp.get(0)).getValue(filterBy).toString(): "+((Activity)tmp.get(0)).getValue(filterBy).toString());
            LOG.error("((Activity)tmp.get(0)):"+((Activity)tmp.get(0)));
            LOG.error("((Activity)tmp.get(0)).getValue(\"actor\"):" + ((Activity)tmp.get(0)).getValue("actor"));
            LOG.error(((Activity)tmp.get(0)).hashCode()+": ((Activity)tmp.get(0)).getActor():" + ((Activity)tmp.get(0)).getActor());
        } catch (IllegalArgumentException e) {
            LOG.error("Illegal argument for the filterOp");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LOG.error("Illegal access for the filterOp");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            LOG.error("Invocation target exception for the filterOp");
            e.printStackTrace();
        }
        return ret;
    }

    synchronized public List<IActivity> getActivities(String CssId, String query,
                                                      String timePeriod) {
        return this.getActivities(query,timePeriod);
    }

    @Override
    public void getActivities(String query, String timePeriod,
                              IActivityFeedCallback c) {

        LOG.debug("local get activities using query WITH CALLBACK called");

        List<IActivity> iActivityList = this.getActivities(query,timePeriod);
        org.societies.api.schema.activityfeed.MarshaledActivityFeed ac = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
        GetActivitiesResponse g = new GetActivitiesResponse();
        ac.setGetActivitiesResponse(g);

        List<org.societies.api.schema.activity.MarshaledActivity> marshalledActivList = new ArrayList<org.societies.api.schema.activity.MarshaledActivity>();

        this.iactivToMarshActvList(iActivityList, marshalledActivList);

        g.setMarshaledActivity(marshalledActivList);

        c.receiveResult(ac);

    }

    @Override
    public void getActivities(String timePeriod, long n, IActivityFeedCallback c) {
        LOG.debug("local get activities WITH CALLBACK called");

        List<IActivity> iActivityList = this.getActivities(timePeriod);
        List<IActivity> ret = new ArrayList<IActivity>();
        Collections.sort(iActivityList,new Comparator<IActivity>() {
            @Override
            public int compare(IActivity iActivity, IActivity iActivity1) {
                return (Long.parseLong(iActivity.getPublished())>Long.parseLong(iActivity1.getPublished())) ? 1 : -1;
            }
        });
        if(iActivityList.size()<n)
            n = iActivityList.size();
        for(int i=0;i<n;i++)
            ret.add(iActivityList.get(i));

        org.societies.api.schema.activityfeed.MarshaledActivityFeed ac = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
        GetActivitiesResponse g = new GetActivitiesResponse();
        ac.setGetActivitiesResponse(g);

        List<org.societies.api.schema.activity.MarshaledActivity> marshalledActivList = new ArrayList<org.societies.api.schema.activity.MarshaledActivity>();

        this.iactivToMarshActvList(ret, marshalledActivList);

        g.setMarshaledActivity(marshalledActivList);

        c.receiveResult(ac);
    }
    public void getActivities(String timePeriod, IActivityFeedCallback c) {
        LOG.debug("local get activities WITH CALLBACK called");

        List<IActivity> iActivityList = this.getActivities(timePeriod);
        org.societies.api.schema.activityfeed.MarshaledActivityFeed ac = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
        GetActivitiesResponse g = new GetActivitiesResponse();
        ac.setGetActivitiesResponse(g);

        List<org.societies.api.schema.activity.MarshaledActivity> marshalledActivList = new ArrayList<org.societies.api.schema.activity.MarshaledActivity>();

        this.iactivToMarshActvList(iActivityList, marshalledActivList);

        g.setMarshaledActivity(marshalledActivList);

        c.receiveResult(ac);

    }
    @Override
    public void getActivities(String query, String timePeriod, long n, IActivityFeedCallback c) {
        LOG.debug("local get activities WITH CALLBACK called");

        List<IActivity> iActivityList = this.getActivities(query,timePeriod);
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

        this.iactivToMarshActvList(ret, marshalledActivList);

        g.setMarshaledActivity(marshalledActivList);

        c.receiveResult(ac);
    }

    public void iactivToMarshActvList(List<IActivity> iActivityList, List<org.societies.api.schema.activity.MarshaledActivity> marshalledActivList){

        Iterator<IActivity> it = iActivityList.iterator();

        while(it.hasNext()){
            IActivity element = it.next();
            marshalledActivList.add(iactivToMarshActiv(element));
        }
    }

    public String getContentIfNotNull(ActivityObject a){
        if(a == null || a.getObjectType() == null) return null;
        if(a.getObjectType().contains("person"))
            return a.getDisplayName();
        if(a.getObjectType().contains("note"))
            return "note";
        if(a.getObjectType().contains("bookmark"))
            return a.getUrl();
        return a.getContent();
    }

    public org.societies.api.schema.activity.MarshaledActivity iactivToMarshActiv(IActivity iActivity){
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

    // TODO: dont we need to use a transaction here
    public void clear(){
        Session session = null;
        Transaction t = null;
        try{

            session = getSessionFactory().openSession();
            t = session.beginTransaction();

            List<Activity> l = session.createCriteria(Activity.class).list();
            for(Activity a : l)
            {
                session.delete(a);
            }
            t.commit();

        } catch (Exception e) {
            if (t != null)
                t.rollback();
            LOG.warn("clear of activities failed");
            e.printStackTrace();
        } finally {
            if(session!=null)
                session.close();
        }
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PubsubClient getPubSubcli() {
        return pubSubcli;
    }

    public void setPubSubcli(PubsubClient pubSubcli) {
        this.pubSubcli = pubSubcli;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

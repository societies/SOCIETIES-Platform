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
import org.hibernate.criterion.Property;
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
import org.societies.api.schema.activityfeed.AddActivityResponse;
import org.societies.api.schema.activityfeed.CleanUpActivityFeedResponse;
import org.societies.api.schema.activityfeed.DeleteActivityResponse;
import org.societies.api.schema.activityfeed.GetActivitiesResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

public class ActivityFeed implements IActivityFeed{//, Subscriber {
	/**
	 * 
	 */


    protected String id;// represents the CIS which owns the activity feed
    protected
	Set<Activity> list;
	public ActivityFeed()
	{
		list = new HashSet<Activity>();
	}
	public ActivityFeed(String id){
		this.id = id;
		list = new HashSet<Activity>();// from Thomas
	}
	protected SessionFactory sessionFactory;
    protected static Logger LOG = LoggerFactory.getLogger(ActivityFeed.class);
    //protected Session session;

	public int count(){
		return list.size();
	}
	//timeperiod: "millisecondssinceepoch millisecondssinceepoch+n" 
	//where n has to be equal to or greater than 0

	public List<IActivity> getActivities(String timePeriod) {
		ArrayList<IActivity> ret = new ArrayList<IActivity>();
		String times[] = timePeriod.split(" ",2);
		if(times.length < 2){
			LOG.error("time period string was malformed: "+timePeriod);
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
		LOG.info("time period: "+fromTime+" - " + toTime);
		if(list != null){
			LOG.info(" list size: "+list.size());
			for(Activity act : list){
				if(Long.parseLong(act.getPublished())>=fromTime && Long.parseLong(act.getPublished())<=toTime){
					ret.add(act);
				}
			}
				
		}
		return ret;
	}
	//query can be e.g. 'object,contains,"programming"'
	//TODO: Needs to support specifying that a attribute needs to empty!

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


	public void addActivity(IActivity activity) {
		Activity newact = new Activity(activity);
		newact.setOwnerId(this.id);
		list.add(newact);
		Session session = null;
		Transaction t = null;
		try{
			session = sessionFactory.openSession();
			t = session.beginTransaction();
			session.save(newact);
			t.commit();
		}catch(Exception e){
			e.printStackTrace();
			if (t != null)
				t.rollback();
			LOG.warn("Saving activity failed, rolling back");
		}finally{
            if(session!=null)
                session.close();
		}		
	}

	// TODO: be aware that this code is never committing!!
	synchronized public int cleanupFeed(String criteria) {
		int ret = 0;
		String forever = "0 "+Long.toString(System.currentTimeMillis());
		List<IActivity> toBeDeleted = getActivities(criteria,forever);
		Session session = null;
		Transaction t = null;
		try{
			session = sessionFactory.openSession();
			t = session.beginTransaction();
			
			for(IActivity act : toBeDeleted){
				this.list.remove(act);
				session.delete((Activity)act);
			}
			t.commit();
		}catch(Exception e){
			e.printStackTrace();
			if (t != null)
				t.rollback();
			LOG.warn("deleting activities failed, rolling back");
		} finally {
            if(session!=null)
                session.close();
        }
		return ret;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
        LOG.info("sessionFactory injected");
		this.sessionFactory = sessionFactory;
	}
	
	synchronized public void startUp(SessionFactory sessionFactory, String id){
        this.id = id;
        list = new HashSet<Activity>();
        LOG.info("starting loading activities from db with ownerId: "+ id );
        Session session = null;
        try{
        	session = sessionFactory.openSession();
            list.addAll(session.createCriteria(Activity.class).add(Property.forName("ownerId").eq(id)).list());
            if(list.size() == 0){
                LOG.error("did not find actitivties with ownerId: "+ id ) ;
            }else if(list.size() > 1){
                LOG.error("activityfeed startup with ownerId: "+id+" gave more than one activityfeed!! ");
            }
        }catch(Exception e){
        	e.printStackTrace();
            LOG.warn("Query for actitvies failed..");
        }finally{
            if(session!=null)
                session.close();
        }
        
        LOG.info("loaded activityfeed with ownerId: " + id + " with "+list.size()+" activities.");
        for(Activity act : list){
            act.repopHash();
            LOG.info("act actor: " + act.getActor());
            LOG.info("act verb: " + act.getVerb());
        }
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<Activity> getList() {
		return list;
	}

	public void setList(Set<Activity> list) {
		this.list = list;
	}
	public void init()
	{
		LOG.info("in activityfeed init");
	}
	
	public void close()
	{
		LOG.info("in activityfeed close");
	}

	synchronized public List<IActivity> getActivities(String CssId, String query,
			String timePeriod) {
		return this.getActivities(query,timePeriod);
	}

	public boolean deleteActivity(IActivity activity) {
		if(!list.contains(activity))
			return false;
		boolean ret = list.remove(activity);
		Session session = null;
		Transaction t = null;
        try {
        	session = sessionFactory.openSession();
    		t = session.beginTransaction();
            session.delete(activity);
            t.commit();
        } catch (Exception e){
        	e.printStackTrace();
			if (t != null)
				t.rollback();
			LOG.warn("delete activity failed, rolling back");
			ret = false;
        } finally {
            if(session!=null)
                session.close();
        }
		return ret;
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
        Session session = null;
		Transaction t = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		ParsePosition pp = new ParsePosition(0);
		try{
			
			session = sessionFactory.openSession();
			t = session.beginTransaction();
				
			for(ActivityEntry act : castedList){
				pp.setIndex(0);
				newAct = new Activity();
				newAct.setActor(getContentIfNotNull(act.getActor()));
				newAct.setOwnerId(this.id);
				newAct.setObject(getContentIfNotNull(act.getObject()));
				newAct.setPublished(Long.toString(df.parse(act.getPublished(),pp).getTime()));
				newAct.setTarget(getContentIfNotNull(act.getTarget()));
				newAct.setVerb(act.getVerb());
				ret++;
				this.list.add(newAct);
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
	public String getContentIfNotNull(ActivityObject a){
		if(a == null) return null;
		if(a.getObjectType().contains("person"))
			return a.getDisplayName();
		if(a.getObjectType().contains("note"))
			return "note";
		if(a.getObjectType().contains("bookmark"))
			return a.getUrl();
		return a.getContent();
	}
	public void clear(){
        Session session = null;
        Transaction t = null;
        try{
        	session = sessionFactory.openSession();
        	t = session.beginTransaction();
            for(Activity act : list){
                session.delete(act);
            }
            t.commit();
        } catch (Exception e){
			if(null!= t) 
				t.rollback();
			LOG.warn("clear failed, rolling back");
			e.printStackTrace();
		} finally {
            if(session!=null)
                session.close();
        }
		list.clear();
	}

	/**
	 * This method just translates an existing list of iActivities (IActivity) into a new list of
	 *  marshaledActivities org.societies.api.schema.activity.MarshaledActivity
	 *  
	 *  
	 * @param iActivityList list with iactivities (IActivity) objects
	 * @param marshalledActivList but already created list (we will fill it up) that will receive the marshalled objects
	 * 
	 */
	
	// TODO: perhaps change to static
	

	public void iactivToMarshActvList(List<IActivity> iActivityList, List<org.societies.api.schema.activity.MarshaledActivity> marshalledActivList){
		
		Iterator<IActivity> it = iActivityList.iterator();
		
		while(it.hasNext()){
			IActivity element = it.next();
			marshalledActivList.add(iactivToMarshActiv(element));
	     }
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
	
	
	
	@Override
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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void getActivities(String query, String timePeriod, long n, IActivityFeedCallback c) {
        //To change body of implemented methods use File | Settings | File Templates.
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
	@Override
	public void cleanupFeed(String criteria, IActivityFeedCallback c) {
		org.societies.api.schema.activityfeed.MarshaledActivityFeed result = new org.societies.api.schema.activityfeed.MarshaledActivityFeed();
		CleanUpActivityFeedResponse r = new CleanUpActivityFeedResponse();
		
		r.setResult(this.cleanupFeed(criteria)); 
		
		
		result.setCleanUpActivityFeedResponse(r);		
		c.receiveResult(result);

		
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
	public IActivity getEmptyIActivity(){
		Activity a = new Activity();
		return a;
	}
	
}

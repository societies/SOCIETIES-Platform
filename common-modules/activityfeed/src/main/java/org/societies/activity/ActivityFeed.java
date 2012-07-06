package org.societies.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import org.apache.shindig.social.core.model.ActivityEntryImpl;
import org.apache.shindig.social.opensocial.model.ActivityEntry;
import org.apache.shindig.social.opensocial.model.ActivityObject;
import org.hibernate.Criteria;
import org.hibernate.Query;
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
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@Table(name = "org_societies_activity_ActivityFeed")
public class ActivityFeed implements IActivityFeed, Subscriber {
	/**
	 * 
	 */
	
	@Id
	private String id;
	@OneToMany(cascade=CascadeType.ALL)
	private
	Set<Activity> list;
	public ActivityFeed()
	{
		list = new HashSet<Activity>();
	}
	public ActivityFeed(String id){
		this.id = id;
		list = new HashSet<Activity>();// from Thomas
	}
	@Autowired 
	private static SessionFactory sessionFactory;
	private static Logger LOG = LoggerFactory.getLogger(ActivityFeed.class);
	
	//timeperiod: "millisecondssinceepoch millisecondssinceepoch+n" 
	//where n has to be equal to or greater than 0
	@Override
	public List<IActivity> getActivities(String timePeriod) {
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
			LOG.error("timeperiod string was malformed, could not parse long");
			return ret;
		}
		LOG.info("timeperiod: "+fromTime+" - " + toTime);
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
	@Override
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

	@Override
	public void addCisActivity(IActivity activity) {

		//persist.
		Session session = sessionFactory.openSession();//getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		Activity newact = new Activity(activity);
		try{
			session.save(newact);
			t.commit();
		}catch(Exception e){
			e.printStackTrace();
			t.rollback();
			LOG.warn("Saving activity failed, rolling back");
			e.printStackTrace();
		}finally{
			list.add(newact);
			if(session!=null)
				session.close();
		}		
	}

	@Override
	public int cleanupFeed(String criteria) {
		int ret = 0;
		String forever = "0 "+Long.toString(System.currentTimeMillis());
		List<IActivity> toBeDeleted = getActivities(criteria,forever);
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try{
			for(IActivity act : toBeDeleted){
				this.list.remove(act);
				session.delete((Activity)act);
			}
		}catch(Exception e){
			t.rollback();
			LOG.warn("deleting activities failed, rolling back");
		}
		return ret;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public static SessionFactory getStaticSessionFactory() {
		return sessionFactory;
	}
	public static void setStaticSessionFactory(SessionFactory isessionFactory) {
		sessionFactory = isessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public static ActivityFeed startUp(String id){
		ActivityFeed ret = null;
		Session session = sessionFactory.openSession();
		try{
			List l = session.createCriteria(ActivityFeed.class).add(Property.forName("id").eq(id)).list();
			if(l.size() == 0)
				return new ActivityFeed(id);
			if(l.size() > 1){
				LOG.error("activityfeed startup with id: "+id+" gave more than one activityfeed!! ");
				return null;
			}
			ret = (ActivityFeed) l.get(0);
		}catch(Exception e){
			LOG.warn("Query for actitvies failed..");

		}finally{
			if(session!=null)
				session.close();
		}
		return ret;
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
	@Override
	public void pubsubEvent(IIdentity pubsubService, String node,
			String itemId, Object item) {
		if(item.getClass().equals(Activity.class)){
			Activity act = (Activity)item;
			this.addCisActivity(act);
		}
	}
	@Override
	public List<IActivity> getActivities(String CssId, String query,
			String timePeriod) {
		return this.getActivities(query,timePeriod);
	}
	@Override
	public boolean deleteActivity(IActivity activity) {
		// x
		return false;
	}
	@Override
	public long importActivtyEntries(List<?> activityEntries) {
		long ret = 0;
		if(activityEntries.size() == 0){
			LOG.error("list is empty, exiting");
			return ret;
		}
		if(!ActivityEntry.class.isInstance(activityEntries.get(0))){ //just checking the first entry.
			LOG.error("first instance in the given list is not of type ActivityEntry, exiting");
			return ret;
		}
		LOG.info("starting importing of "+activityEntries.size()+ " activityentries");
		List<ActivityEntry> castedList = (List<ActivityEntry>) activityEntries;
		Activity newAct = null;
		Session session = sessionFactory.openSession();//getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		ParsePosition pp = new ParsePosition(0);
		try{
			for(ActivityEntry act : castedList){
				pp.setIndex(0);
				LOG.info("actor: "+getContentIfNotNull(act.getActor()) + " raw: "+act.getActor());
				LOG.info("object: "+getContentIfNotNull(act.getObject())+" raw: "+act.getObject());
				LOG.info("published: "+act.getPublished());
				LOG.info("target: "+getContentIfNotNull(act.getTarget())+" raw: "+act.getTarget());
				LOG.info("verb: "+act.getVerb());
				newAct = new Activity();
				newAct.setActor(getContentIfNotNull(act.getActor()));
				newAct.setFeed(this);
				newAct.setObject(getContentIfNotNull(act.getObject()));
				newAct.setPublished(Long.toString(df.parse(act.getPublished(),pp).getTime()));
				LOG.info("published after parsing:" + newAct.getPublished());
				newAct.setTarget(getContentIfNotNull(act.getTarget()));
				newAct.setVerb(act.getVerb());
				ret++;
				this.list.add(newAct);
				session.save(newAct);
			}
			session.save(this);
			t.commit();
		}catch(Exception e){
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
}

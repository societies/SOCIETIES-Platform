package org.societies.cis.activity;
import java.util.ArrayList;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.cis.activity.model.Activity;

@Entity
@Table(name = "ActivityFeed")
public class ActivityFeed implements IActivityFeed {
	@Id
	private String id;
	@OneToMany(cascade=CascadeType.ALL)
	private
	Set<Activity> list;
	
	
	private static SessionFactory sessionFactory;
	private static Logger log = LoggerFactory.getLogger(ActivityFeed.class);
	
	
	@Override
	public void getActivities(String CssId, String timePeriod) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getActivities(String CssId, String query, String timePeriod) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCisActivity(IActivity activity) {
		
		//persist.
		Session session = getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		Activity newact = new Activity(activity);
		session.save(newact);
		t.commit();
		
	}

	@Override
	public void cleanupFeed(String criteria) {
		// TODO Auto-generated method stub
		
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public static SessionFactory getStaticSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public static ActivityFeed startUp(String id){
		ActivityFeed ret = null;
		Session session = sessionFactory.openSession();
		Query q = session.createQuery("select a from ActivityFeed a where a.id = ?");
		q.setString(0, id);
		ret = (ActivityFeed) q.uniqueResult();
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

}

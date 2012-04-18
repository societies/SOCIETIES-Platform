package org.societies.cis.activity;
import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisActivity;
import org.societies.api.cis.management.ICisActivityFeed;
import org.societies.cis.activity.model.CisActivity;


public class ActivityFeed implements ICisActivityFeed {
	private SessionFactory sessionFactory;
	private static Logger log = LoggerFactory.getLogger(ActivityFeed.class);
	
	
	ArrayList<CisActivity> list;
	@Override
	public void getActivities(String CssId, String timePeriod) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getActivities(String CssId, String query, String timePeriod) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCisActivity(ICisActivity activity) {
		
		//persist.
		Session session = getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		CisActivity newact = new CisActivity(activity);
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

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}

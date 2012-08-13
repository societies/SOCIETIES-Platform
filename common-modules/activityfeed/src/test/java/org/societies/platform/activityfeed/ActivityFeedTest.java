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
package org.societies.platform.activityfeed;


import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.ActivityFeed;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.platform.FacebookConn.impl.FacebookConnectorImpl;
import org.societies.platform.socialdata.SocialData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * 
 * 
 * @author bjornmagnus adopted from solutanet
 * 
 */
@ContextConfiguration(locations = { "../../../../META-INF/ActivityFeedTest-context.xml" })
public class ActivityFeedTest extends
AbstractTransactionalJUnit4SpringContextTests {
	private static Logger LOG = LoggerFactory
			.getLogger(ActivityFeedTest.class);
	@Autowired
	private ActivityFeed actFeed;
	private SessionFactory sessionFactory=null;
	private Session session=null;
	static {
		ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
	}
	int feedid=0;
	@Before
	public void setupBefore() throws Exception {
		if(sessionFactory==null){
			sessionFactory = actFeed.getSessionFactory();
		}
		if(session==null){
			session = sessionFactory.openSession();
			actFeed.setSession(session);
		}
		if(!session.isOpen())
			session = sessionFactory.openSession();
		LOG.info("i startup ");
		
	}
	@After
	public void tearDownAfter() throws Exception {
		
		actFeed.clear();
		session.close();
		actFeed = null;
	}
	@Test
	@Rollback(false)
	public void testAddCisActivity() {
		LOG.info("@@@@@@@ IN TESTADDACTIVITY @@@@@@@");
		actFeed.startUp(session, Integer.toString(1));
		String actor="testUsertestAddCisActivity";
		String verb="published";
		IActivity iact = new Activity();
		iact.setActor(actor);
		iact.setPublished(Long.toString(System.currentTimeMillis()));
		iact.setVerb(verb);
		iact.setObject("message");
		iact.setTarget("testTarget");
		actFeed.addActivity(iact);

		List<IActivity> results = null;
		try {
			JSONObject searchQuery = new JSONObject();
			String timeSeries = "0 "+Long.toString(System.currentTimeMillis());
			try {
				searchQuery.append("filterBy", "actor");
				searchQuery.append("filterOp", "equals");
				searchQuery.append("filterValue", actor);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			LOG.info("sending timeSeries: "+timeSeries+ " act published: "+iact.getPublished());
			results = actFeed.getActivities(searchQuery.toString(), timeSeries);
			LOG.info("testing filtering filter result: "+results.size());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertNotNull(results);
		assert(results.size()>0);
		assert(results.get(0).getActor().equals(actor));
	}

	@Test
	@Rollback(false)
	public void testCleanupFeed() {
		LOG.info("@@@@@@@ IN TESTCLEANUPFEED @@@@@@@");
		actFeed.startUp(session, Integer.toString(2));
		JSONObject searchQuery = new JSONObject();
		String timeSeries = "0 "+Long.toString(System.currentTimeMillis());
		try {
			searchQuery.append("filterBy", "actor");
			searchQuery.append("filterOp", "present");
			searchQuery.append("filterValue", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try{
			actFeed.cleanupFeed(searchQuery.toString());
		}catch(Exception e){

		}

	}

	//@Ignore // this test runs when running on junit in eclipse but fails when testing with maven
	@Test
	@Rollback(false)
	public void testFilter(){
		LOG.info("@@@@@@@ IN TESTFILTER @@@@@@@");
		actFeed.startUp(session, Integer.toString(3));
		String actor="testFilterUser";
		Activity act1 = new Activity(); act1.setActor(actor); act1.setPublished(Long.toString(System.currentTimeMillis()-100));
		String timeSeries = Long.toString(System.currentTimeMillis()-1000)+" "+Long.toString(System.currentTimeMillis());
		actFeed.addActivity(act1);
		JSONObject searchQuery = new JSONObject();
		try {
			searchQuery.append("filterBy", "actor");
			searchQuery.append("filterOp", "equals");
			searchQuery.append("filterValue", actor);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		LOG.info("sending timeSeries: "+timeSeries+ " act published: "+act1.getPublished());
		List<IActivity> results = actFeed.getActivities(searchQuery.toString(), timeSeries);
		LOG.info("testing filtering filter result: "+results.size());
		assert(results.size() > 0);
	}

//	@Test
//	@Rollback(true)
//	public void testBootStrap(){
//		LOG.info("@@@@@@@ IN TESTBOOTSTRAP @@@@@@@");
//		SessionFactory ses = ActivityFeed.getStaticSessionFactory();
//		Session s = ses.openSession();
//		Transaction t = s.beginTransaction();
//		t.begin();
//		for(int i=0;i<10;i++){
//			ActivityFeed feed = new ActivityFeed(Integer.toString(i));
//
//			s.save(feed);
//		}
//		t.commit();
//		ActivityFeed queryFeed = ActivityFeed.startUp("0");
//		assert(queryFeed != null);
//	}
	//@Ignore
	@Test
	@Rollback(false)
	public void testSNImporter(){
		LOG.info("@@@@@@@ IN TESTSNIMPORTER @@@@@@@");
		actFeed.startUp(session, Integer.toString(4));
		LOG.info("actFeedcontent: "+ actFeed.getActivities("0 " + Long.toString(System.currentTimeMillis())).size());
		ISocialConnector mockedSocialConnector; 
		mockedSocialConnector = mock(ISocialConnector.class);
		stub(mockedSocialConnector.getConnectorName()).toReturn("facebook");
		stub(mockedSocialConnector.getID()).toReturn("facebook_0001");
		try {
			stub(mockedSocialConnector.getUserFriends()).toReturn(readFileAsString("mocks/friends.txt"));
			stub(mockedSocialConnector.getUserActivities()).toReturn(readFileAsString("mocks/activities.txt"));
			stub(mockedSocialConnector.getUserGroups()).toReturn(readFileAsString("mocks/groups.txt"));
			stub(mockedSocialConnector.getUserProfile()).toReturn(readFileAsString("mocks/profile.txt"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LOG.info("@@@@@@@ IN TESTSNIMPORTER 3 @@@@@@@");
		SocialData data = new SocialData();
		try {
			data.addSocialConnector(mockedSocialConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		data.updateSocialData();
		actFeed.importActivtyEntries(data.getSocialActivity());
		LOG.info("testing importing from facebook, raw activities: " + mockedSocialConnector.getUserActivities());
		LOG.info("testing importing from facebook, activities: " + data.getSocialActivity().size() );
		
		LOG.info("feed-hash: "+actFeed.hashCode()+"  feed.getActivities(\"0 \" + Long.toString(System.currentTimeMillis())).size(): " +  actFeed.getActivities("0 " + Long.toString(System.currentTimeMillis())).size());
		LOG.info("comparing with: "+data.getSocialActivity().size());
		assert(data.getSocialActivity().size() == actFeed.getActivities("0 " + Long.toString(System.currentTimeMillis())).size());
	}

	//@Ignore
	@Test
	@Rollback(true)
	public void testReboot() {
		LOG.info("@@@@@@@ IN TESTREBOOT @@@@@@@");
		String actor="testRebootActor";
		String verb="published";
		actFeed.startUp(session, Integer.toString(5));
		IActivity iact = new Activity();
		iact.setActor(actor);
		iact.setPublished(Long.toString(System.currentTimeMillis()));
		iact.setVerb(verb);
		iact.setObject("message");
		iact.setTarget("testTarget");

		actFeed.addActivity(iact);
//				Session session = ActivityFeed.getStaticSessionFactory().openSession();//getSessionFactory().openSession();
//				Transaction t = session.beginTransaction();
//				session.update(actFeed);
//				t.commit();
		//actFeed.startUp(session,"1");

		List<IActivity> results = null;
		try {
			JSONObject searchQuery = new JSONObject();
			String timeSeries = "0 "+Long.toString(System.currentTimeMillis());
			try {
				searchQuery.append("filterBy", "actor");
				searchQuery.append("filterOp", "equals");
				searchQuery.append("filterValue", actor);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			LOG.info("sending timeSeries: "+timeSeries+ " act published: "+iact.getPublished());
			results = actFeed.getActivities(searchQuery.toString(), timeSeries);
			LOG.info("testing filtering filter result: "+results.size());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//assert(results!=null);
		assert(results.size()>0);
		assert(results.get(0).getActor().equals(actor));
	}
	private static String readFileAsString(String filePath)
			throws java.io.IOException{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(
				new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}


}

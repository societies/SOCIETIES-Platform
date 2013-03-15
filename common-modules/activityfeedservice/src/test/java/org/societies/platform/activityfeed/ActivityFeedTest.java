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


import org.hibernate.SessionFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.ActivityFeed;
import org.societies.activity.ActivityFeedManager;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;
import org.societies.platform.socialdata.SocialData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;

/**
 * 
 * 
 * @author bjornmagnus adopted from solutanet
 * 
 */
@ContextConfiguration(locations = { "classpath:META-INF/ActivityFeedTest-context.xml"})
public class ActivityFeedTest extends
AbstractTransactionalJUnit4SpringContextTests {
	private static Logger LOG = LoggerFactory
			.getLogger(ActivityFeedTest.class);

    @Autowired
    private SessionFactory sessionFactory;
    private static ActivityFeedManager activityFeedManager;

	private ActivityFeed actFeed;

    //mocks
    private static ICommManager mockCSSendpoint = mock(ICommManager.class);
    private static IIdentityManager mockIdentityManager = mock(IIdentityManager.class);
    private static IIdentity mockIdentity = mock(IIdentity.class);
    private static final String FEED_ID="1";
    private static final String FEED_JID="sintef";
    private static PubsubClient mockPubsubClient = mock(PubsubClient.class);
    private static List<String> mockDicoItems = new ArrayList<String>();
    private final static int callBackTimeout=1000;


    static {
		ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
	}
	int feedid=0;

    @BeforeClass
    public static void setupBeforeClass() throws InvalidFormatException, CommunicationException, XMPPError {
        mockDicoItems.add(FEED_ID);
        when(mockCSSendpoint.getIdManager()).thenReturn(mockIdentityManager);
        when(mockIdentityManager.fromJid(FEED_JID)).thenReturn(mockIdentity);
        when(mockPubsubClient.discoItems(mockIdentity,FEED_ID)).thenReturn(mockDicoItems);
        activityFeedManager = new ActivityFeedManager();
        activityFeedManager.setCommManager(mockCSSendpoint);

        activityFeedManager.setPubSubClient(mockPubsubClient);
    }

    @Before
	public void setupBefore() throws Exception {
        activityFeedManager.setSessionFactory(this.sessionFactory);
        actFeed = (ActivityFeed) activityFeedManager.getOrCreateFeed(FEED_JID,FEED_ID, true);
	}
	@After
	public void tearDownAfter() throws Exception {
		
		actFeed.clear();
		actFeed = null;
	}
	@Test
	@Rollback(false)
	public void testAddCisActivity() {
		LOG.info("@@@@@@@ IN TESTADDACTIVITY @@@@@@@");
        actFeed.setId("testAddCisActivity");
		actFeed.startUp(sessionFactory);
		String actor="testUsertestAddCisActivity";
		String verb="published";
		IActivity iact = new Activity();
		iact.setActor(actor);
		iact.setPublished(Long.toString(System.currentTimeMillis()));
		iact.setVerb(verb);
		iact.setObject("message");
		iact.setTarget("testTarget");
        final CountDownLatch latch = new CountDownLatch(1);
        final Boolean resultArr[] = {false};
		actFeed.addActivity(iact, new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                latch.countDown();
                resultArr[0] = activityFeedObject.getAddActivityResponse().isResult();
            }
        });
        try {
            latch.await(callBackTimeout,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert resultArr[0];
        final List<MarshaledActivity> results = new ArrayList<MarshaledActivity>();
        final CountDownLatch latch2 = new CountDownLatch(1);
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
			actFeed.getActivities(searchQuery.toString(), timeSeries, new IActivityFeedCallback() {
                @Override
                public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                    results.addAll(activityFeedObject.getGetActivitiesResponse().getMarshaledActivity());
                    latch2.countDown();
                }
            });
			LOG.info("testing filtering filter result: "+results.size());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
            latch2.await(callBackTimeout,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotNull(results);
		assert(results.size()>0);
		assert(results.get(0).getActor().equals(actor));
	}
    @Test
    @Rollback(false)
    public void testAsyncAddCisActivity() {
        LOG.info("@@@@@@@ IN TESTADDACTIVITY @@@@@@@");
        actFeed.setId("testAsyncAddCisActivity");
        actFeed.startUp(sessionFactory);
        final String actor="testUsertestAddCisActivity";
        String verb="published";
        final IActivity iact = new Activity();
        iact.setActor(actor);
        iact.setPublished(Long.toString(System.currentTimeMillis()));
        iact.setVerb(verb);
        iact.setObject("message");
        iact.setTarget("testTarget");
        final CountDownLatch latch = new CountDownLatch(1); final Boolean resultArr[] = {false};
        actFeed.addActivity(iact, new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                resultArr[0] = activityFeedObject.getAddActivityResponse().isResult();
                latch.countDown();
            }
        });
        try {
            latch.await(callBackTimeout,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final List<MarshaledActivity> results = new ArrayList<MarshaledActivity>();
        final CountDownLatch latch2 = new CountDownLatch(1);
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
            actFeed.getActivities(searchQuery.toString(), timeSeries, new IActivityFeedCallback() {
                @Override
                public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                    results.addAll(activityFeedObject.getGetActivitiesResponse().getMarshaledActivity());
                    latch2.countDown();
                }
            });
            LOG.info("testing filtering filter result: "+results.size());
            latch2.await(callBackTimeout,TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertNotNull(results);
        assert(results.size()>0);
        assert(results.get(0).getActor().equals(actor));

    }


	//@Ignore // this test runs when running on junit in eclipse but fails when testing with maven
	@Test
	@Rollback(false)
	public void testFilter(){
		LOG.info("@@@@@@@ IN TESTFILTER @@@@@@@");
        actFeed.setId("testFilter");
		actFeed.startUp(sessionFactory);
		String actor="testFilterUser";
		Activity act1 = new Activity(); act1.setActor(actor); act1.setPublished(Long.toString(System.currentTimeMillis()-100));
		actFeed.addActivity(act1);
		String timeSeries = Long.toString(System.currentTimeMillis()-1000)+" "+Long.toString(System.currentTimeMillis());
		JSONObject searchQuery = new JSONObject();
		try {
			searchQuery.append("filterBy", "actor");
			searchQuery.append("filterOp", "equals");
			searchQuery.append("filterValue", actor);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		LOG.info("sending timeSeries: "+timeSeries+ " act published: "+act1.getPublished());
        final List<MarshaledActivity> results = new ArrayList<MarshaledActivity>();
        final CountDownLatch latch = new CountDownLatch(1);
        actFeed.getActivities(searchQuery.toString(), timeSeries,new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                results.addAll(activityFeedObject.getGetActivitiesResponse().getMarshaledActivity());
                latch.countDown();
            }
        });
        try {
            latch.await(callBackTimeout,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        LOG.info("testing filtering filter result: "+results.size());
		assert(results.size() > 0);
	}

	@Test
	@Rollback(false)
	public void testSNImporter(){
		LOG.info("@@@@@@@ IN TESTSNIMPORTER @@@@@@@");
        actFeed.setId("testSNImporter");
		actFeed.startUp(sessionFactory);
		LOG.info("actFeedcontent: "+ actFeed.getActivities("0 " + Long.toString(System.currentTimeMillis())).size());
		ISocialConnector mockedSocialConnector; 
		mockedSocialConnector = mock(ISocialConnector.class);
		stub(mockedSocialConnector.getConnectorName()).toReturn("facebook");
		stub(mockedSocialConnector.getID()).toReturn("facebook_0001");
        stub(mockedSocialConnector.getSocialNetwork()).toReturn(SocialNetwork.FACEBOOK);
		try {
			stub(mockedSocialConnector.getUserFriends()).toReturn(readFileAsString("mocks/friends.txt"));
			stub(mockedSocialConnector.getUserActivities()).toReturn(readFileAsString("mocks/activities.txt"));
			stub(mockedSocialConnector.getUserGroups()).toReturn(readFileAsString("mocks/groups.txt"));
			stub(mockedSocialConnector.getUserProfile()).toReturn(readFileAsString("mocks/profile.txt"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SocialData data = new SocialData();
		try {
			data.addSocialConnector(mockedSocialConnector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		data.updateSocialData();
		actFeed.importActivityEntries(data.getSocialActivity());
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
        actFeed.setId("testReboot");
		actFeed.startUp(sessionFactory);
		IActivity iact = new Activity();
		iact.setActor(actor);
		iact.setPublished(Long.toString(System.currentTimeMillis()));
		iact.setVerb(verb);
		iact.setObject("message");
		iact.setTarget("testTarget");

		actFeed.addActivity(iact);

        final List<MarshaledActivity> results = new ArrayList<MarshaledActivity>();
        final CountDownLatch latch = new CountDownLatch(1);
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
			actFeed.getActivities(searchQuery.toString(), timeSeries, new IActivityFeedCallback() {
                @Override
                public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                    results.addAll(activityFeedObject.getGetActivitiesResponse().getMarshaledActivity());
                    latch.countDown();
                }
            });
			LOG.info("testing filtering filter result: "+results.size());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
            latch.await(callBackTimeout,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		assert(results.size()>0);
		assert(results.get(0).getActor().equals(actor));
	}

    public void testCleanup(){
        IActivity act = new Activity();
        actFeed.setId("testCleanup");
        final String actor = "testActor";
        act.setActor(actor);
        act.setObject("testObject");
        act.setTarget("testTarget");
        act.setVerb("testVerb");
        final Boolean[] correctArr = {false};
        final CountDownLatch latch = new CountDownLatch(1);
        actFeed.addActivity(act, new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                 latch.countDown();
            }
        });

        try {
            latch.await(callBackTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final CountDownLatch latch2 = new CountDownLatch(1);
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
            actFeed.cleanupFeed(searchQuery.toString(), new IActivityFeedCallback() {
                @Override
                public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                    latch2.countDown();
                    correctArr[0] = activityFeedObject.getCleanUpActivityFeedResponse().getResult() > 0 ;
                }
            });

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            latch2.await(callBackTimeout,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert correctArr[0];

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

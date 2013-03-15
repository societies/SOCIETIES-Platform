/*
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp.,
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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.ActivityFeed;
import org.societies.activity.ActivityFeedManager;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: bjornmagnus
 * Date: 2/11/13
 * Time: 20:25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/ActivityFeedManagerTest-context.xml"})
public class  ActivityFeedManagerTest {
    public static final String FEED_ID = "ActivityFeedManagerUnitTests-";
    private static Logger LOG = LoggerFactory
            .getLogger(ActivityFeedManagerTest.class);
    //@Autowired
    private ActivityFeedManager activityFeedManagerUnderTest;
    private IActivityFeedManager iActivityFeedManagerUnderTest;

    @Autowired
    private SessionFactory sessionFactory;
    private ICISCommunicationMgrFactory mockCcmFactory;
    //mocks
    private static ICommManager mockCSSendpoint = mock(ICommManager.class);
    private static IIdentityManager mockIdentityManager = mock(IIdentityManager.class);
    private static IIdentity mockIdentity = mock(IIdentity.class);
    private static String mockJid = "mockJid";
    private static PubsubClient mockPubsubClient = mock(PubsubClient.class);
    private static List<String> mockDicoItems = new ArrayList<String>();

    @BeforeClass
    public static void setupBeforeClass() throws InvalidFormatException, CommunicationException, XMPPError {
        mockDicoItems.add(FEED_ID);
        when(mockCSSendpoint.getIdManager()).thenReturn(mockIdentityManager);
        when(mockIdentityManager.fromJid(mockJid)).thenReturn(mockIdentity);
        when(mockPubsubClient.discoItems(mockIdentity,FEED_ID)).thenReturn(mockDicoItems);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void beforeTest(){
        activityFeedManagerUnderTest = new ActivityFeedManager();
        activityFeedManagerUnderTest.setCommManager(mockCSSendpoint);
        LOG.info("setting sessionManager: "+this.sessionFactory);
        activityFeedManagerUnderTest.setSessionFactory(this.sessionFactory);
        activityFeedManagerUnderTest.setPubSubClient(this.mockPubsubClient);
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Test
    public void testGetNewActivityFeed(){
        IActivityFeed feed = activityFeedManagerUnderTest.getOrCreateFeed(this.mockJid,FEED_ID+"testGetNewActivityFeed", true );
        assert (((ActivityFeed)feed).getOwner().contentEquals(this.mockJid));
    }
    @Test
    public void testGetOldActivityFeed(){
        IActivityFeed oldFeed = activityFeedManagerUnderTest.getOrCreateFeed(this.mockJid,FEED_ID+"testGetOldActivityFeed", true );
        IActivityFeed checkFeed = activityFeedManagerUnderTest.getOrCreateFeed(this.mockJid,FEED_ID+"testGetOldActivityFeed", true );
        String oldOwner = ((ActivityFeed)oldFeed).getOwner();
        String checkOwner = ((ActivityFeed)checkFeed).getOwner();
        assert (oldOwner.contentEquals(checkOwner));
    }
    @Test
    public void testGetNotMyOwnActivityFeed(){
        activityFeedManagerUnderTest.getOrCreateFeed(this.mockJid+"something",FEED_ID+"testGetNotMyOwnActivityFeed", true );
        IActivityFeed checkFeed = activityFeedManagerUnderTest.getOrCreateFeed(this.mockJid, FEED_ID+"testGetNotMyOwnActivityFeed", true );
        assert (checkFeed == null);
    }
    @Test
    public void testDeleteOwnActivityFeed(){
        IActivityFeed checkFeed = activityFeedManagerUnderTest.getOrCreateFeed(this.mockJid,FEED_ID+"testDeleteOwnActivityFeed", true );
        int checkHash = checkFeed.hashCode();
        boolean ret = activityFeedManagerUnderTest.deleteFeed(this.mockJid,FEED_ID+"testDeleteOwnActivityFeed");
        assert (ret);
        checkFeed = activityFeedManagerUnderTest.getOrCreateFeed(this.mockJid,FEED_ID+"testDeleteOwnActivityFeed", true ); //this should create a NEW object containing the same data..
        assert (checkHash!=checkFeed.hashCode());
    }
    @Test
    public void testDeleteNotMyOwnActivityFeed(){
        IActivityFeed checkFeed = activityFeedManagerUnderTest.getOrCreateFeed(this.mockJid,FEED_ID+"testDeleteNotMyOwnActivityFeed", true );
        int checkHash = checkFeed.hashCode();
        boolean ret = activityFeedManagerUnderTest.deleteFeed(this.mockJid+"something",FEED_ID+"testDeleteNotMyOwnActivityFeed");
        assert (!ret);
    }
    @Test
    public void testDeleteNonExistentActivityFeed(){
        boolean ret = activityFeedManagerUnderTest.deleteFeed(this.mockJid,FEED_ID+"testDeleteNonExistentActivityFeed");
        assert (!ret);
    }
    @Test
    public void testReboot(){
        IActivityFeed feed = activityFeedManagerUnderTest.getOrCreateFeed(this.mockJid,FEED_ID+"testReboot", true);
        IActivity act = new Activity();
        final String actor = "testActor";
        act.setActor(actor);
        act.setObject("testObject");
        act.setTarget("testTarget");
        act.setVerb("testVerb");
        final Boolean[] boolArr = {false};
        feed.addActivity(act, new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                boolArr[0] = true;
            }
        });

        //need to wait for the activity to actually be added before deleting/shutting down the activityfeed.
        while(!boolArr[0]){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        activityFeedManagerUnderTest = null;
        beforeTest();
        activityFeedManagerUnderTest.init();
        feed = activityFeedManagerUnderTest.getOrCreateFeed(this.mockJid,FEED_ID+"testReboot", true );
        feed.getActivities("0 "+Long.toString(System.currentTimeMillis()+1),new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                assert(activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().get(0).getActor().contains(actor));
            }
        });

    }

    public ICommManager getMockCSSendpoint() {
        return mockCSSendpoint;
    }

    public void setMockCSSendpoint(ICommManager mockCSSendpoint) {
        this.mockCSSendpoint = mockCSSendpoint;
    }
}

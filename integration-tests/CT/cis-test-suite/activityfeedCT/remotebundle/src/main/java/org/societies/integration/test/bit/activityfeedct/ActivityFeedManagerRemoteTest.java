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
package org.societies.integration.test.bit.activityfeedct;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;


/**
 * Test list:
 *
 * @author Olivier Maridat (Trialog)
 *
 */
public class ActivityFeedManagerRemoteTest {
    private static Logger LOG = LoggerFactory.getLogger(ActivityFeedManagerRemoteTest.class.getSimpleName());
    public static Integer testCaseNumber;

    private String privacyPolicyWithoutRequestor;
    private String cssId;
    private List<String> cisIds;
    private String cssPassword;
    private String cisName;
    private String cisDescription;
    private String cisType;
    private int numCIS = 6;
    private Hashtable<String, MembershipCriteria> cisMembershipCriteria;
    private static final int defaultActs = 2; //created, joined.

    @Before
    public void setUp() {
        LOG.info("[#"+testCaseNumber+"] setUp");
        cisIds = new ArrayList<String>();
        cssId = TestCase109612.commManager.getIdManager().getThisNetworkNode().getJid();
        cssPassword = "password.societies.local";
        cisName = "CisTest";
        cisDescription = "CIS to Test ActivityFeedManager";
        cisType = "testCis";
    }

    @After
    public void tearDown() {
        LOG.info("[#"+testCaseNumber+"] tearDown");
    }

    //Write/Read to multiple activities feeds with the container and test that what is written for a cis can be read only for that cis.
    @Test
    public void testActivityFeedManager() {
        LOG.info("[#" + testCaseNumber + "] creating cis1");
        final CisAdvertisementRecord[] advertisementRecord = {null};
        LOG.info("[#"+testCaseNumber+"] getting all CIS advertisements.");
        TestCase109612.cisDirectory.findAllCisAdvertisementRecords(new ICisDirectoryCallback() {
            @Override
            public void getResult(List<CisAdvertisementRecord> cisAdvertisementRecords) {
                advertisementRecord[0] = cisAdvertisementRecords.get(0);
            }
        });
        int maxCounter = 20000,counter=0;
        try {
            while(advertisementRecord[0] == null) {
                Thread.sleep(100);
                counter += 100;
                if (counter > maxCounter) {
                    LOG.info("[#"+testCaseNumber+"] giving up waiting (10s) for response from findallcisadvertisements.., this test will fail.");
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assert ( advertisementRecord[0] != null);
        if(advertisementRecord[0] != null)
            LOG.info("[#"+testCaseNumber+"] found one advertisementRecord[0].getName(): " + advertisementRecord[0].getName());
        final ArrayList<Community> communities = new ArrayList<Community>();
        CisAdvertisementRecord rightrecord = null;
        if(advertisementRecord.length > 1){
            for(CisAdvertisementRecord record : advertisementRecord)
                if (record.getName().contentEquals(ActivityFeedManagerHostingTest.cisName))
                    rightrecord = record;
            assert (rightrecord != null); // if there is more than one cis, atleas one should be the one created in the hosting bundle
        } else
            rightrecord = advertisementRecord[0];
        LOG.info("[#"+testCaseNumber+"] trying to join cis "+ rightrecord.getName());
        final String cisname = rightrecord.getName();
        long start = System.currentTimeMillis();
        TestCase109612.cisManager.joinRemoteCIS(rightrecord,new ICisManagerCallback() {
            @Override
            public void receiveResult(CommunityMethods communityResultObject) {
                communities.add(communityResultObject.getJoinResponse().getCommunity());
            }
        });
        counter = 0;
        try {
            while(communities.size() < 1 ) {
                Thread.sleep(100);
                counter += 100;
                if (counter > maxCounter)  {
                    LOG.info("[#"+testCaseNumber+"] giving up waiting (10s) for response from joinRemoteCIS, this test will fail.");
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        LOG.info("[#"+testCaseNumber+"] joined cis took " + (System.currentTimeMillis()-start) + " ms ");
        assert (communities.size() != 0);
        assert (communities.get(0) != null);
        LOG.info("[#"+testCaseNumber+"] joined community with jid: "+communities.get(0).getCommunityJid());
        ICis cis1 = TestCase109612.cisManager.getCis(communities.get(0).getCommunityJid());
        assert (cis1 != null);
        //inserting 1 activity into cis1

        LOG.info("[#"+testCaseNumber+"] checking that cis1 has one activity");
        final ArrayList<MarshaledActivity> activities = new ArrayList<MarshaledActivity>();
        cis1.getActivityFeed().getActivities("0 "+Long.toString(System.currentTimeMillis()),new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                LOG.info("[#"+testCaseNumber+"] cis " + cisname + " had " + activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().size() + " activities (should be "+(1+ActivityFeedManagerRemoteTest.defaultActs)+")");
                assert (activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().size()==(1+ActivityFeedManagerRemoteTest.defaultActs));
                activities.add(activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().get(0));
            }
        });
        counter = 0;
        try {
            while(activities.size() > 0 ) {
                Thread.sleep(100);
                counter += 100;
                if (counter > maxCounter)  {
                    LOG.info("[#"+testCaseNumber+"] giving up waiting (10s) for response from getactivities, this test will fail.");
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        LOG.info("[#"+testCaseNumber+"] inserting another activity into cis1, then checking if cis1 has "+(2+ActivityFeedManagerRemoteTest.defaultActs)+" activities..");
        final Boolean[] done = {null};
        cis1.getActivityFeed().addActivity(makeMessage("heh", "heh", "nonsense", "0"), new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                if(activityFeedObject.getAddActivityResponse().isResult()!=null) {
                    done[0] = new Boolean(activityFeedObject.getAddActivityResponse().isResult());
                    LOG.info("done[0] not null");
                } else {
                    done[0] = new Boolean(false);
                    LOG.info("done[0] null");
                }
                LOG.info("[#"+testCaseNumber+"] added an activity to cis " + cisname + " result: "
                        + activityFeedObject.getAddActivityResponse().isResult()
                        + " done[0] = "+done[0]);
            }
        });
        LOG.info("entering while loop to wait for done[0]");
        try {
            while(done[0] == null || !done[0]) {
                if(done[0]!=null)
                    LOG.info("in while loop to wait for done[0]: " + done[0]);
                else
                    LOG.info("in while loop to wait for done[0] it is still null.. ");
                Thread.sleep(100);
                counter += 100;
                if (counter > maxCounter)  {
                    LOG.info("[#"+testCaseNumber+"] giving up waiting (10s) for response from addActivity, this test will fail.");
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        activities.clear();
        cis1.getActivityFeed().getActivities("0 "+Long.toString(System.currentTimeMillis()),new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                LOG.info("[#"+testCaseNumber+"] cis " + cisname + " had " + activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().size() + " activities (should be "+(2+ActivityFeedManagerRemoteTest.defaultActs)+")");
                assert (activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().size()==2);
                activities.add(activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().get(0));
                activities.add(activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().get(1));
            }
        });
        try {
            while(activities.size() < 1) {
                Thread.sleep(100);
                counter += 100;
                if (counter > maxCounter)  {
                    LOG.info("[#"+testCaseNumber+"] giving up waiting (10s) for response from getActivities, this test will fail.");
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        assert (activities.size() == (2+ActivityFeedManagerRemoteTest.defaultActs));
        LOG.info("[#"+testCaseNumber+"] has been run successfully");

    }


    //util methods

    public Activity makeMessage(String user1, String user2, String message, String published){
        Activity ret = new Activity();
        ret.setActor(user1);
        ret.setObject(message);
        ret.setTarget(user2);
        ret.setPublished(published);
        return ret;
    }

}

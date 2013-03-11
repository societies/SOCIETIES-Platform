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
package org.societies.integration.test.bit.activityfeedmanager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author  Bjørn Magnus based on work from Rafik and Olivier
 */
public class NominalTestCase {
    private static Logger LOG = LoggerFactory.getLogger(NominalTestCase.class.getSimpleName());
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


    @Before
    public void setUp() {
        LOG.info("[#"+testCaseNumber+"] setUp");
        cisIds = new ArrayList<String>();
        cssId = TestCase10961.commManager.getIdManager().getThisNetworkNode().getJid();
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
    public void testActivityFeedManagerThroughCISManager() {
        LOG.info("[#"+testCaseNumber+"] creating cis1");
        Future<ICisOwned> cis1 = TestCase10961.cisManager.createCis(cisName+"1", cisType, cisMembershipCriteria, cisDescription);
        LOG.info("[#"+testCaseNumber+"] creating cis2");
        Future<ICisOwned> cis2 = TestCase10961.cisManager.createCis(cisName+"2", cisType, cisMembershipCriteria, cisDescription);
        IActivityFeed activityFeed1 = null; IActivityFeed activityFeed2 = null;
        try {
            LOG.info("[#"+testCaseNumber+"] inserting 1 activity into cis1");
            //inserting 1 activity into cis1
            activityFeed1 = cis1.get().getActivityFeed();
            activityFeed2 = cis2.get().getActivityFeed();
            activityFeed1.addActivity(makeMessage("heh", "heh", "nonsense", "0"), new IActivityFeedCallback() {
                @Override
                public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                }
            });
            LOG.info("[#"+testCaseNumber+"] checking that cis1 has one activity");
            //checking that cis1 has one activity
            activityFeed1.getActivities("0 " + Long.toString(System.currentTimeMillis()), new IActivityFeedCallback() {
                @Override
                public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                    assert (activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().size() == 1);
                }
            });
            LOG.info("[#"+testCaseNumber+"] checking that cis2 has zero activities");
            //checking that cis2 has zero activities
            activityFeed2.getActivities("0 "+Long.toString(System.currentTimeMillis()),new IActivityFeedCallback() {
                @Override
                public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                    assert (activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().size()==0);
                }
            });
            LOG.info("[#"+testCaseNumber+"] inserting two activities into cis2");
            //inserting two activities into cis2
            activityFeed2.addActivity(makeMessage("heh", "heh", "nonsense", "0"), new IActivityFeedCallback() {
                @Override
                public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                }
            });
            activityFeed2.addActivity(makeMessage("heh","heh","nonsense","0"),new IActivityFeedCallback() {
                @Override
                public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                }
            });
            LOG.info("[#"+testCaseNumber+"] checking that cis1 still only has one activity");
            //checking that cis1 still only has one activity
            activityFeed1.getActivities("0 "+Long.toString(System.currentTimeMillis()),new IActivityFeedCallback() {
                @Override
                public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                    assert (activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().size()==1);
                }
            });
            LOG.info("[#"+testCaseNumber+"] checking that cis2 now has two activities");
            //checking that cis2 now has two activities
            activityFeed2.getActivities("0 "+Long.toString(System.currentTimeMillis()),new IActivityFeedCallback() {
                @Override
                public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                    assert (activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().size()==2);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ExecutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void testActivityFeedManager() {
        IActivityFeed activityFeed1 = null; IActivityFeed activityFeed2 = null;
        LOG.info("[#"+testCaseNumber+"] creating activityFeed1");
        activityFeed1 = TestCase10961.activityFeedManager.getOrCreateFeed("admin.societies.local", "onlyFeedId1");
        LOG.info("[#"+testCaseNumber+"] creating activityFeed2");
        activityFeed2 = TestCase10961.activityFeedManager.getOrCreateFeed("admin.societies.local", "onlyFeedId2");

        LOG.info("[#"+testCaseNumber+"] inserting 1 activity into cis1");
        //inserting 1 activity into cis1

        activityFeed1.addActivity(makeMessage("heh", "heh", "nonsense", "0"), new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
            }
        });
        LOG.info("[#"+testCaseNumber+"] checking that cis1 has one activity");
        //checking that cis1 has one activity
        activityFeed1.getActivities("0 " + Long.toString(System.currentTimeMillis()), new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                assert (activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().size() == 1);
            }
        });
        LOG.info("[#"+testCaseNumber+"] checking that cis2 has zero activities");
        //checking that cis2 has zero activities
        activityFeed2.getActivities("0 "+Long.toString(System.currentTimeMillis()),new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                assert (activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().size()==0);
            }
        });
        LOG.info("[#"+testCaseNumber+"] inserting two activities into cis2");
        //inserting two activities into cis2
        activityFeed2.addActivity(makeMessage("heh", "heh", "nonsense", "0"), new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
            }
        });
        activityFeed2.addActivity(makeMessage("heh","heh","nonsense","0"),new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
            }
        });
        LOG.info("[#"+testCaseNumber+"] checking that cis1 still only has one activity");
        //checking that cis1 still only has one activity
        activityFeed1.getActivities("0 "+Long.toString(System.currentTimeMillis()),new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                assert (activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().size()==1);
            }
        });
        LOG.info("[#"+testCaseNumber+"] checking that cis2 now has two activities");
        //checking that cis2 now has two activities
        activityFeed2.getActivities("0 "+Long.toString(System.currentTimeMillis()),new IActivityFeedCallback() {
            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                assert (activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().size()==2);
            }
        });
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

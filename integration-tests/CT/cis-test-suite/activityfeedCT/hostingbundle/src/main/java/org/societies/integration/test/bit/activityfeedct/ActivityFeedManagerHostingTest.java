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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.directory.ICisAdvertisementRecord;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.identity.DataIdentifierScheme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * Test list:
 * 
 * @author Bjørn Magnus Mathisen (SINTEF)
 *
 */
public class ActivityFeedManagerHostingTest {
    private static Logger LOG = LoggerFactory.getLogger(ActivityFeedManagerHostingTest.class.getSimpleName());
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
        cssId = TestCase109611.commManager.getIdManager().getThisNetworkNode().getJid();
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
        LOG.info("[#"+testCaseNumber+"] creating cis1");
        PrivacyPolicy privacyPolicy = new PrivacyPolicy();
        Future<ICisOwned> cis1 = TestCase109611.cisManager.createCis(cisName+"1", cisType, cisMembershipCriteria, cisDescription);

        try {
            //ICisOwned cisOwned = cis1.get();
            //CisAdvertisementRecord cisAdvertisementRecord = new CisAdvertisementRecord();
            //cisAdvertisementRecord.setName(cisOwned.getName()); cisAdvertisementRecord.setId(cisOwned.getCisId()); cisAdvertisementRecord.setCssownerid(cisOwned.getOwnerId());
            //TestCase109611.cisDirectory.addCisAdvertisementRecord(cisAdvertisementRecord);
            LOG.info("[#"+testCaseNumber+"] inserting 1 activity into cis1");
            //inserting 1 activity into cis1
            cis1.get().getActivityFeed().addActivity(makeMessage("heh", "heh", "nonsense", "0"), new IActivityFeedCallback() {
                @Override
                public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ExecutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        LOG.info("[#"+testCaseNumber+"] has been run successfully");
        assert(cisIds.size()==this.numCIS);
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
    private List<RequestItem> createTestPrivacyPolicy() {
        List<Action> actionsRw = new ArrayList<Action>();
        actionsRw.add(new Action(ActionConstants.READ));
        actionsRw.add(new Action(ActionConstants.WRITE, true));
        List<Action> actionsR = new ArrayList<Action>();
        actionsR.add(new Action(ActionConstants.READ));
        List<Condition> conditionsMembersOnly = new ArrayList<Condition>();
        conditionsMembersOnly.add(new Condition(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY, "1"));
        conditionsMembersOnly.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));
        List<Condition> conditionsPublic = new ArrayList<Condition>();
        conditionsPublic.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
        conditionsPublic.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));
        List<Condition> conditionsPrivate = new ArrayList<Condition>();
        conditionsPrivate.add(new Condition(ConditionConstants.SHARE_WITH_CIS_OWNER_ONLY, "1"));
        conditionsPrivate.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));
        conditionsPrivate.add(new Condition(ConditionConstants.MAY_BE_INFERRED, "1"));
        List<RequestItem> requests = new ArrayList<RequestItem>();
        requests.add(new RequestItem(new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_SYMBOLIC), actionsRw, conditionsMembersOnly));
        requests.add(new RequestItem(new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.BIRTHDAY), actionsR, conditionsPublic));
        requests.add(new RequestItem(new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LAST_ACTION), actionsR, conditionsPrivate));
        requests.add(new RequestItem(new Resource(DataIdentifierScheme.CIS, "cis-member-list"), actionsRw, conditionsMembersOnly));
        return requests;
    }
}

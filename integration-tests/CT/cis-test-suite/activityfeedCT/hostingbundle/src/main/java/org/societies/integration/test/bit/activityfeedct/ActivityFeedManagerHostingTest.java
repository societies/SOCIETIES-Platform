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
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.*;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.*;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.privacytrust.privacy.util.privacypolicy.PrivacyPolicyUtil;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import java.net.URI;
import java.net.URISyntaxException;
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
    public static String cisName;
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

 /*       try {
            privacyPolicy = getRequestPolicy();
        } catch (InvalidFormatException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedCtxIdentifierException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }*/
        //LOG.info("[#"+testCaseNumber+"] creating cis with policy: "+privacyPolicy.toXMLString());
        Future<ICisOwned> cis1 = TestCase109611.cisManager.createCis(cisName, cisType, cisMembershipCriteria, cisDescription,"<RequestPolicy></RequestPolicy>");//,"<RequestPolicy></RequestPolicy>");

        try {
            RequestorCis requestor = getRequestorCis("admin.societies.local",cis1.get().getCisId());
            RequestPolicy privacyPolicy = new RequestPolicy(requestor,new ArrayList<RequestItem>());
            TestCase109611.privacyPolicyManager.updatePrivacyPolicy(privacyPolicy);
            LOG.info("[#"+testCaseNumber+"#] setting new policy: for the pair (admin.societies.local,"+cis1.get().getCisId()+") "+privacyPolicy.toXMLString());
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
        } catch (InvalidFormatException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (PrivacyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        LOG.info("[#"+testCaseNumber+"] has been run successfully");
        assert(cisIds.size()==this.numCIS);
    }


    //util methods
/*    public RequestPolicy makePolicy(){
        List<PrivacyConditionForm> conditionsCisMemberList = new ArrayList<PrivacyConditionForm>();
        List<PrivacyConditionForm> conditionsCisMembershipCriteria = new ArrayList<PrivacyConditionForm>();
        List<PrivacyConditionForm> conditionsCisCommunityContext = new ArrayList<PrivacyConditionForm>();
        conditionsCisMemberList.add(new PrivacyConditionForm(ConditionConstants.RIGHT_TO_OPTOUT, "1", false));
        conditionsCisMemberList.add(new PrivacyConditionForm(ConditionConstants.STORE_IN_SECURE_STORAGE, "1", false));
        conditionsCisMemberList.add(new PrivacyConditionForm(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1", false));


        conditionsCisCommunityContext.add(new PrivacyConditionForm(ConditionConstants.RIGHT_TO_OPTOUT, "1", false));
        conditionsCisCommunityContext.add(new PrivacyConditionForm(ConditionConstants.STORE_IN_SECURE_STORAGE, "1", false));
        conditionsCisCommunityContext.add(new PrivacyConditionForm(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1", false));
    }*/
    public RequestPolicy getRequestPolicy() throws InvalidFormatException, MalformedCtxIdentifierException {

        // -- Requestor
        RequestorCis requestor = null;
        // -- Resources
        List<RequestItem> requestItems = new ArrayList<RequestItem>();
        //RequestItem item = new RequestItem(new Resource(DataTypeFactory.fromUri()))
        RequestPolicy privacyPolicy = new RequestPolicy(requestItems);
        privacyPolicy.setRequestor(requestor);
        return privacyPolicy;
    }
    public Activity makeMessage(String user1, String user2, String message, String published){
        Activity ret = new Activity();
        ret.setActor(user1);
        ret.setObject(message);
        ret.setTarget(user2);
        ret.setPublished(published);
        return ret;
    }
    private RequestPolicy getRequestPolicy(Requestor requestor) {
        List<RequestItem> requestItems = getRequestItems();
        RequestPolicy requestPolicy = new RequestPolicy(requestor, requestItems);
        return requestPolicy;
    }
    private List<RequestItem> getRequestItems() {
        List<RequestItem> items = new ArrayList<RequestItem>();
        Resource locationResource = new Resource(DataIdentifierScheme.CONTEXT, CtxAttributeTypes.LOCATION_SYMBOLIC);
        List<Condition> conditions = new ArrayList<Condition>();
        conditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES,"YES"));
        List<Action> actions = new ArrayList<Action>();
        actions.add(new Action(ActionConstants.READ));
        RequestItem rItem = new RequestItem(locationResource, actions, conditions, false);
        items.add(rItem);
        Resource someResource = new Resource(DataIdentifierScheme.CONTEXT, "someResource");
        List<Condition> extendedConditions = new ArrayList<Condition>();
        extendedConditions.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES,"YES"));
        extendedConditions.add(new Condition(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA, "YES"));
        List<Action> extendedActions = new ArrayList<Action>();
        extendedActions.add(new Action(ActionConstants.READ));
        extendedActions.add(new Action(ActionConstants.CREATE));
        extendedActions.add(new Action(ActionConstants.WRITE));
        extendedActions.add(new Action(ActionConstants.DELETE));
        RequestItem someItem = new RequestItem(someResource, extendedActions, extendedConditions, false);
        items.add(someItem);
        return items;
    }

    private RequestorService getRequestorService() throws InvalidFormatException{
        IIdentity requestorId = TestCase109611.commManager.getIdManager().fromJid("red.societies.local");
        ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
        serviceId.setServiceInstanceIdentifier("css://red@societies.local/HelloEarth");
        try {
            serviceId.setIdentifier(new URI("css://red@societies.local/HelloEarth"));
        } catch (URISyntaxException e) {
            LOG.error("Can't create the service ID", e);
        }
        return new RequestorService(requestorId, serviceId);
    }

    private RequestorCis getRequestorCis(String cssIdString, String cisIdString) throws InvalidFormatException{
        IIdentity otherCssId = TestCase109611.commManager.getIdManager().fromJid(cssIdString);
        IIdentity cisId = TestCase109611.commManager.getIdManager().fromJid(cisIdString);
        return new RequestorCis(otherCssId, cisId);
    }
}

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
package org.societies.integration.tests.bit.webapp;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyNegotiationManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.FailedNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.NegotiationDetails;
import org.societies.api.osgi.event.*;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.integration.api.selenium.SeleniumTest;
import org.societies.integration.api.selenium.components.UFNotificationPopup;
import org.societies.integration.api.selenium.pages.IndexPage;
import org.societies.integration.api.selenium.pages.PrivacyPolicyNegotiationRequestPage;

import java.io.File;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;

public class TestWebappCisJoin extends SeleniumTest {

    private static final Logger log = LoggerFactory.getLogger(TestWebappCisJoin.class);

    private class InternalEventListener extends EventListener {

        private final Hashtable<RequestorBean, InternalEvent> results;

        public InternalEventListener(Hashtable<RequestorBean, InternalEvent> results) {
            this.results = results;
        }

        @Override
        public void handleInternalEvent(InternalEvent event) {
            if (event.geteventInfo() instanceof FailedNegotiationEvent) {
                FailedNegotiationEvent failedNegEvent = (FailedNegotiationEvent) event.geteventInfo();
                RequestorBean req = RequestorUtils.toRequestorBean(failedNegEvent.getDetails().getRequestor());
                log.debug("Received FailedNegotiationEvent");

                synchronized (results) {
                    results.put(req, event);
                    results.notifyAll();
                }

            } else {

                PPNegotiationEvent negEvent = (PPNegotiationEvent) event.geteventInfo();
                RequestorBean req = RequestorUtils.toRequestorBean(negEvent.getDetails().getRequestor());
                log.debug("Received Successful Negotiation Event");

                synchronized (results) {
                    results.put(req, event);
                    results.notifyAll();
                }
            }
        }

        @Override
        public void handleExternalEvent(CSSEvent event) {

        }
    }

    private static final String USERNAME = "paddy";
    private static final String PASSWORD = "paddy";

    private IndexPage indexPage;

    private InternalEventListener internalEventListener;

    private ICommManager commManager;
    private ICtxBroker ctxBroker;

    private IIdentity thisUserId;
    private RequestorCisBean requestorCisBean;
    private RequestPolicy cisPolicy;
    private IPrivacyPolicyManager privacyPolicyManager;
    private IPrivacyPolicyNegotiationManager privacyPolicyNegotiationManager;
    private IEventMgr eventManager;

    private final Hashtable<RequestorBean, InternalEvent> results = new Hashtable<RequestorBean, InternalEvent>();

    public TestWebappCisJoin() {
        log.debug("TestWebappCisJoin ctor()");
    }

    @Before
    public void setupTest() throws PrivacyException {
        log.debug("Setting up test");

//        cisManager = WebappCISTestsInit.getCisManager();
        commManager = WebappCISTestsInit.getCommManager();
        ctxBroker = WebappCISTestsInit.getCtxBroker();
        privacyPolicyManager = WebappCISTestsInit.getPrivacyPolicyManager();
        privacyPolicyNegotiationManager = WebappCISTestsInit.getPrivacyPolicyNegotiationManager();
        eventManager = WebappCISTestsInit.getEventManager();

        thisUserId = commManager.getIdManager().getThisNetworkNode();

        // register result listener
        synchronized (results){
            results.clear();
        }
        internalEventListener = new InternalEventListener(results);
        eventManager.subscribeInternalEvent(internalEventListener,
                new String[]{EventTypes.FAILED_NEGOTIATION_EVENT, EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT},
                null
        );

        requestorCisBean = new RequestorCisBean();
        requestorCisBean.setRequestorId(thisUserId.getBareJid());
        requestorCisBean.setCisRequestorId("mockCis" + requestorCisBean.getRequestorId());

        try {
            XMLPolicyReader reader = new XMLPolicyReader(ctxBroker, commManager.getIdManager());
            cisPolicy = RequestPolicyUtils.toRequestPolicyBean(reader.readPolicyFromFile(getPolicyFile()));
            cisPolicy.setRequestor(requestorCisBean);

            RequestPolicy updatePrivacyPolicy = privacyPolicyManager.updatePrivacyPolicy(cisPolicy);
            Assert.assertNotNull("RequestPolicy is null", updatePrivacyPolicy);

            log.debug("Updated privacy policy");

            checkCisPolicy();
        } catch (PrivacyException e) {
            log.error("Error setting up privacy", e);
            throw e;
        }

        indexPage = new IndexPage(getDriver());

        log.debug("Finished setting up test");
    }

    @After
    public void tearDown() {
        try {
            boolean deletePrivacyPolicy = privacyPolicyManager.deletePrivacyPolicy(requestorCisBean);
            Assert.assertTrue(deletePrivacyPolicy);

        } catch (PrivacyException e) {
            log.error("Error tearing down", e);
        }

        eventManager.unSubscribeInternalEvent(internalEventListener,
                new String[]{EventTypes.FAILED_NEGOTIATION_EVENT, EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT},
                null
        );
    }

    @Test
    public void temporaryTest_mockCisJoin_andEnsurePpnAppears() throws Exception {
        // TODO: This test must either be replaced, or expanded to use the GUI to join the CIS
        // Currently the test uses the ICisManager to join a CIS and test that the join was successful

        indexPage.doLogin(USERNAME, PASSWORD);

        // verify requests empty
        indexPage.verifyNumberInNotificationsBubble(0);

        // send the request
        NegotiationDetails details = new NegotiationDetails(RequestorUtils.toRequestor(requestorCisBean, commManager.getIdManager()), 123);
        privacyPolicyNegotiationManager.negotiateCISPolicy(details);

        // verify request received by webapp
        indexPage.verifyNumberInNotificationsBubble(1);

        // switch to the notification page
        UFNotificationPopup ufNotificationPopup = indexPage.clickNotificationBubble();
        PrivacyPolicyNegotiationRequestPage ppnPage = ufNotificationPopup.clickFirstPPNLink();

        // accept the negotiation
        ppnPage.clickAcceptPpnButton();

        log.debug("checking whether result has been received");
        while (!results.containsKey(requestorCisBean)) {
            synchronized (results) {
                try {
                    log.debug("Waiting for results.");
                    results.wait();
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }
        }

        log.debug("result received " + results.containsKey(requestorCisBean));
        InternalEvent event = results.get(requestorCisBean);
        if (event.geteventInfo() instanceof FailedNegotiationEvent) {
            Assert.fail("Negotiation has failed");
        }

        indexPage.verifyNumberInNotificationsBubble(0);
    }

    private static File getPolicyFile() {
        Bundle bundle = FrameworkUtil.getBundle(TestWebappCisJoin.class);

        URL fileURL = bundle.getResource("Privacy-Policy.xml");
        return new File(fileURL.getFile());
    }

    private void checkCisPolicy() {
        List<RequestItem> requestItems = this.cisPolicy.getRequestItems();

        for (RequestItem item : requestItems) {
            if (item.isOptional())
                continue;

            try {
                IndividualCtxEntity individualCtxEntity = ctxBroker.retrieveIndividualEntity(thisUserId).get();
                if (individualCtxEntity.getAttributes(item.getResource().getDataType()).size() == 0) {
                    ctxBroker.createAttribute(individualCtxEntity.getId(), item.getResource().getDataType()).get();
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }
}

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
import org.osgi.framework.BundleContext;
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
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class TestWebappCisJoin extends SeleniumTest {

    private static final Logger log = LoggerFactory.getLogger(TestWebappCisJoin.class);
    private static final long TIMEOUT_TIME_MS = 10000;

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
    private static final String PASSWORD = "p";

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

    private BundleContext bundleContext;

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
        bundleContext = WebappCISTestsInit.getBundleContext();

        thisUserId = commManager.getIdManager().getThisNetworkNode();

        // register result listener
        synchronized (results) {
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
    public void temporaryTest_mockCisJoin_ensurePpnAppears_andAcceptsOK() throws Exception {
        // TODO: This test must either be replaced, or expanded to use the GUI to join the CIS
        // Currently the test uses the ICisManager to join a CIS and test that the join was successful
        // When you are creating the T65 pages to manage CISs, you should expand this test

        indexPage.doLogin(USERNAME, PASSWORD);

        // verify requests empty
        indexPage.verifyNumberInNotificationsBubble(0);

        // send the request
        log.debug("Sending join request");
        final NegotiationDetails details = new NegotiationDetails(RequestorUtils.toRequestor(requestorCisBean, commManager.getIdManager()), 123);
        nonBlocking(new Runnable() {
            @Override
            public void run() {
                try {
                    privacyPolicyNegotiationManager.negotiateCISPolicy(details);
                } catch (PrivacyException e) {
                    log.error("Error with CIS policy negotiation", e);
                }
            }
        });

        // verify request received by webapp
        log.debug("Verifying notification displayed");
        indexPage.verifyNumberInNotificationsBubble(1);

        // switch to the notification page
        log.debug("Viewing notification");
        UFNotificationPopup ufNotificationPopup = indexPage.clickNotificationBubble();
        PrivacyPolicyNegotiationRequestPage ppnPage = ufNotificationPopup.clickFirstPPNLink();

        // accept the negotiation
        log.debug("Accepting notification");
        ppnPage.clickAcceptPpnButton();

        Date timeout = new Date(new Date().getTime() + TIMEOUT_TIME_MS);

        log.debug("Waiting for results up to " + TIMEOUT_TIME_MS + "ms...");
        while (!results.containsKey(requestorCisBean)
                && new Date().before(timeout)) {
            synchronized (results) {
                try {
                    results.wait(100);
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }
        }

        if (!results.containsKey(requestorCisBean))
            Assert.fail("PPN results not received within an acceptable time");

        log.debug("Result received " + results.containsKey(requestorCisBean));
        InternalEvent event = results.get(requestorCisBean);
        if (event.geteventInfo() instanceof FailedNegotiationEvent) {
            Assert.fail("Negotiation has failed");
        }

        indexPage.verifyNumberInNotificationsBubble(0);
    }

    @Test
    public void temporaryTest_mockCisJoin_ensurePpnAppears_andFailsOK() throws Exception {
        // TODO: This test must either be replaced, or expanded to use the GUI to join the CIS
        // Currently the test uses the ICisManager to join a CIS and test that the join was successful
        // When you are creating the T65 pages to manage CISs, you should expand this test

        indexPage.doLogin(USERNAME, PASSWORD);

        // verify requests empty
        indexPage.verifyNumberInNotificationsBubble(0);

        // send the request
        log.debug("Sending join request");
        final NegotiationDetails details = new NegotiationDetails(RequestorUtils.toRequestor(requestorCisBean, commManager.getIdManager()), 123);
        nonBlocking(new Runnable() {
            @Override
            public void run() {
                try {
                    privacyPolicyNegotiationManager.negotiateCISPolicy(details);
                } catch (PrivacyException e) {
                    log.error("Error with CIS policy negotiation", e);
                }
            }
        });

        // verify request received by webapp
        log.debug("Verifying notification displayed");
        indexPage.verifyNumberInNotificationsBubble(1);

        // switch to the notification page
        log.debug("Viewing notification");
        UFNotificationPopup ufNotificationPopup = indexPage.clickNotificationBubble();
        PrivacyPolicyNegotiationRequestPage ppnPage = ufNotificationPopup.clickFirstPPNLink();

        // accept the negotiation
        log.debug("Accepting notification");
        ppnPage.clickCancelPpnButton();

        Date timeout = new Date(new Date().getTime() + TIMEOUT_TIME_MS);

        log.debug("Waiting for results up to " + TIMEOUT_TIME_MS + "ms...");
        while (!results.containsKey(requestorCisBean)
                && new Date().before(timeout)) {
            synchronized (results) {
                try {
                    results.wait(100);
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }
        }

        if (!results.containsKey(requestorCisBean))
            Assert.fail("PPN results not received within an acceptable time");

        log.debug("Result received " + results.containsKey(requestorCisBean));
        InternalEvent event = results.get(requestorCisBean);
        if (!(event.geteventInfo() instanceof FailedNegotiationEvent)) {
            Assert.fail("Negotiation has NOT failed");
        }

        indexPage.verifyNumberInNotificationsBubble(0);
    }

    private static void nonBlocking(Runnable method) {
        new Thread(method).start();
    }

    private File getPolicyFile() {
        if (bundleContext == null)
            throw new NullPointerException("Bundle context is null");

        URL fileURL = bundleContext.getBundle().getResource("Privacy-Policy.xml");
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

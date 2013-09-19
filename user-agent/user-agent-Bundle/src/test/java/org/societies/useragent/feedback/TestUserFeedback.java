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

package org.societies.useragent.feedback;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.mockito.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.internal.useragent.feedback.IUserFeedbackResponseEventListener;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalType;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.*;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.FeedbackMethodType;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.societies.useragent.api.model.UserFeedbackEventTopics;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.mockito.Mockito.*;

public class TestUserFeedback extends TestCase {

    private static final Logger log = LoggerFactory.getLogger(TestUserFeedback.class);
    public static final int SYNC_RESPONSE_DELAY = 1000;

    private class EventCallback<T> implements IUserFeedbackResponseEventListener<T> {
        private boolean responseRx = false;
        private T result = null;

        @Override
        public void responseReceived(T result) {
            log.debug("Callback called");
            this.result = result;
            responseRx = true;
        }

        public boolean isResponseRx() {
            return responseRx;
        }

        public T getResult() {
            return result;
        }
    }

    private UserFeedback userFeedback;

    // autowired vars
    private ICommManager mockCommManager;
    private PubsubClient mockPubSubClient;

    // private vars
    private IIdentityManager mockIdentityManager;

    @Override
    public void setUp() throws Exception {
        log.debug("setUp()");

        mockCommManager = mock(ICommManager.class);
        mockPubSubClient = mock(PubsubClient.class);

        mockIdentityManager = mock(IIdentityManager.class);

        userFeedback = new UserFeedback();
        userFeedback.setCommsMgr(mockCommManager);
        userFeedback.setPubsub(mockPubSubClient);

        when(mockCommManager.getIdManager()).thenReturn(mockIdentityManager);

        userFeedback.initialiseUserFeedback();
    }

    public void testGetExplicitFB_sync_fullSequence() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        ExpProposalContent content = new ExpProposalContent(
                "Test proposal",
                new String[]{"Yes", "No"}
        );

        String expectedResult = "Yes";

        final String requestId = UUID.randomUUID().toString();

        final ExpFeedbackResultBean resultBean = new ExpFeedbackResultBean();
        resultBean.setFeedback(new ArrayList<String>());
        resultBean.getFeedback().add(expectedResult);
        resultBean.setRequestId(requestId);

        log.debug("Preparing response thread");
        new Thread(new Runnable() {
            @Override
            public void run() {
                log.debug("Waiting " + SYNC_RESPONSE_DELAY + "ms to send mock pub sub response");

                try {
                    Thread.sleep(SYNC_RESPONSE_DELAY);
                } catch (InterruptedException e) {
                    log.error("Error delaying mock pubsub event", e);
                }

                log.debug("Sending mock pubsub response");
                userFeedback.pubsubEvent(null, UserFeedbackEventTopics.EXPLICIT_RESPONSE, requestId, resultBean);
                log.debug("Mock pubsub response sent");
            }
        }).start();

        log.debug("Requesting EXP (AckNack) user feedback");
        Future<List<String>> result = userFeedback.getExplicitFB(requestId, ExpProposalType.ACKNACK, content);
        log.debug("Method returned");

        // VERIFICATION
        verify(mockPubSubClient).publisherPublish(Matchers.any(IIdentity.class), Matchers.eq(UserFeedbackEventTopics.REQUEST), Matchers.eq(requestId), Matchers.any(UserFeedbackBean.class));

        Assert.assertNotNull("Expected Future to contain result", result.get());
        Assert.assertEquals("Expected result bean to contain 1 item", 1, result.get().size());
        Assert.assertEquals("Expected result bean to contain correct result", expectedResult, result.get().get(0));
    }

    public void testGetExplicitFB_async_fullSequence() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        ExpProposalContent content = new ExpProposalContent(
                "Test proposal",
                new String[]{"Yes", "No"}
        );

        String expectedResult = "Yes";

        final String requestId = UUID.randomUUID().toString();

        final ExpFeedbackResultBean resultBean = new ExpFeedbackResultBean();
        resultBean.setFeedback(new ArrayList<String>());
        resultBean.getFeedback().add(expectedResult);
        resultBean.setRequestId(requestId);

        log.debug("Requesting EXP (AckNack) user feedback");
        Future<List<String>> result = userFeedback.getExplicitFBAsync(requestId, ExpProposalType.ACKNACK, content, null);
        log.debug("Method returned");

        // VERIFICATION
        verify(mockPubSubClient).publisherPublish(Matchers.any(IIdentity.class), Matchers.eq(UserFeedbackEventTopics.REQUEST), Matchers.eq(requestId), Matchers.any(UserFeedbackBean.class));

        Assert.assertNull("Expected Future to be empty", result.get());

        log.debug("Sending mock pubsub response");
        userFeedback.pubsubEvent(null, UserFeedbackEventTopics.EXPLICIT_RESPONSE, requestId, resultBean);
        log.debug("Mock pubsub response sent");

        Assert.assertNotNull("Expected Future to contain result", result.get());
        Assert.assertEquals("Expected result bean to contain 1 item", 1, result.get().size());
        Assert.assertEquals("Expected result bean to contain correct result", expectedResult, result.get().get(0));
    }

    public void testGetExplicitFB_callback_fullSequence() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        ExpProposalContent content = new ExpProposalContent(
                "Test proposal",
                new String[]{"Yes", "No"}
        );

        String expectedResult = "Yes";

        final String requestId = UUID.randomUUID().toString();

        final ExpFeedbackResultBean resultBean = new ExpFeedbackResultBean();
        resultBean.setFeedback(new ArrayList<String>());
        resultBean.getFeedback().add(expectedResult);
        resultBean.setRequestId(requestId);

        EventCallback<List<String>> callback = new EventCallback<List<String>>();

        log.debug("Requesting EXP (AckNack) user feedback");
        userFeedback.getExplicitFBAsync(requestId, ExpProposalType.ACKNACK, content, callback);
        log.debug("Method returned");

        // VERIFICATION
        verify(mockPubSubClient).publisherPublish(Matchers.any(IIdentity.class), Matchers.eq(UserFeedbackEventTopics.REQUEST), Matchers.eq(requestId), Matchers.any(UserFeedbackBean.class));

        Assert.assertFalse(callback.isResponseRx());

        log.debug("Sending mock pubsub response");
        userFeedback.pubsubEvent(null, UserFeedbackEventTopics.EXPLICIT_RESPONSE, requestId, resultBean);
        log.debug("Mock pubsub response sent");

        Assert.assertTrue(callback.isResponseRx());

        Assert.assertNotNull("Expected callback to contain result", callback.getResult());
        Assert.assertEquals("Expected result bean to contain 1 item", 1, callback.getResult().size());
        Assert.assertEquals("Expected result bean to contain correct result", expectedResult, callback.getResult().get(0));
    }

    public void testGetExplicitFB_callback_ignoresWrongResponses() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        ExpProposalContent content = new ExpProposalContent(
                "Test proposal",
                new String[]{"Yes", "No"}
        );

        String expectedResult = "Yes";

        final String requestId = UUID.randomUUID().toString();
        final String wrongID = UUID.randomUUID().toString();

        final ExpFeedbackResultBean resultBean = new ExpFeedbackResultBean();
        resultBean.setFeedback(new ArrayList<String>());
        resultBean.getFeedback().add(expectedResult);
        resultBean.setRequestId(requestId);

        EventCallback<List<String>> callback = new EventCallback<List<String>>();

        log.debug("Requesting EXP (AckNack) user feedback");
        userFeedback.getExplicitFBAsync(requestId, ExpProposalType.ACKNACK, content, callback);
        log.debug("Method returned");


        log.debug("Sending wrong pubsub responses");

        resultBean.setRequestId(wrongID);
        userFeedback.pubsubEvent(null, UserFeedbackEventTopics.EXPLICIT_RESPONSE, wrongID, resultBean);
        Assert.assertFalse(callback.isResponseRx());

        resultBean.setRequestId(requestId);

        try {
            userFeedback.pubsubEvent(null, UserFeedbackEventTopics.IMPLICIT_RESPONSE, requestId, resultBean);
        } catch (Exception ex) {
            // do nothing - this is acceptable behaviour
        }
        Assert.assertFalse(callback.isResponseRx());

        try {
            userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, requestId, resultBean);
        } catch (Exception ex) {
            // do nothing - this is acceptable behaviour
        }
        Assert.assertFalse(callback.isResponseRx());

        try {
            userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE, requestId, resultBean);
        } catch (Exception ex) {
            // do nothing - this is acceptable behaviour
        }
        Assert.assertFalse(callback.isResponseRx());

        log.debug("Sending correct mock pubsub response");
        userFeedback.pubsubEvent(null, UserFeedbackEventTopics.EXPLICIT_RESPONSE, requestId, resultBean);

        Assert.assertTrue(callback.isResponseRx());
    }


    public void testGetImplicitFB_sync_fullSequence() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        ImpProposalContent content = new ImpProposalContent(
                "Test proposal",
                30000
        );

        final String requestId = UUID.randomUUID().toString();

        final ImpFeedbackResultBean resultBean = new ImpFeedbackResultBean();
        resultBean.setAccepted(true);
        resultBean.setRequestId(requestId);

        log.debug("Preparing response thread");
        new Thread(new Runnable() {
            @Override
            public void run() {
                log.debug("Waiting " + SYNC_RESPONSE_DELAY + "ms to send mock pub sub response");

                try {
                    Thread.sleep(SYNC_RESPONSE_DELAY);
                } catch (InterruptedException e) {
                    log.error("Error delaying mock pubsub event", e);
                }

                log.debug("Sending mock pubsub response");
                userFeedback.pubsubEvent(null, UserFeedbackEventTopics.IMPLICIT_RESPONSE, requestId, resultBean);
                log.debug("Mock pubsub response sent");
            }
        }).start();

        log.debug("Requesting IMP (Timed Abort) user feedback");
        Future<Boolean> result = userFeedback.getImplicitFB(requestId, ImpProposalType.TIMED_ABORT, content);
        log.debug("Method returned");

        // VERIFICATION
        verify(mockPubSubClient).publisherPublish(Matchers.any(IIdentity.class), Matchers.eq(UserFeedbackEventTopics.REQUEST), Matchers.eq(requestId), Matchers.any(UserFeedbackBean.class));

        Assert.assertNotNull("Expected Future to contain result", result.get());
        Assert.assertEquals("Expected result bean to contain correct result", Boolean.TRUE, result.get());
    }

    public void testGetImplicitFB_async_fullSequence() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        ImpProposalContent content = new ImpProposalContent(
                "Test proposal",
                30000
        );

        final String requestId = UUID.randomUUID().toString();

        final ImpFeedbackResultBean resultBean = new ImpFeedbackResultBean();
        resultBean.setAccepted(true);
        resultBean.setRequestId(requestId);

        log.debug("Requesting IMP (Timed Abort) user feedback");
        Future<Boolean> result = userFeedback.getImplicitFBAsync(requestId, ImpProposalType.TIMED_ABORT, content, null);
        log.debug("Method returned");

        // VERIFICATION
        verify(mockPubSubClient).publisherPublish(Matchers.any(IIdentity.class), Matchers.eq(UserFeedbackEventTopics.REQUEST), Matchers.eq(requestId), Matchers.any(UserFeedbackBean.class));

        Assert.assertNull("Expected Future to be empty", result.get());

        log.debug("Sending mock pubsub response");
        userFeedback.pubsubEvent(null, UserFeedbackEventTopics.IMPLICIT_RESPONSE, requestId, resultBean);
        log.debug("Mock pubsub response sent");

        Assert.assertNotNull("Expected Future to contain result", result.get());
        Assert.assertEquals("Expected result bean to contain correct result", Boolean.TRUE, result.get());
    }

    public void testGetImplicitFB_callback_fullSequence() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        ImpProposalContent content = new ImpProposalContent(
                "Test proposal",
                30000
        );

        final String requestId = UUID.randomUUID().toString();

        final ImpFeedbackResultBean resultBean = new ImpFeedbackResultBean();
        resultBean.setAccepted(true);
        resultBean.setRequestId(requestId);

        EventCallback<Boolean> callback = new EventCallback<Boolean>();

        log.debug("Requesting IMP (Timed Abort) user feedback");
        userFeedback.getImplicitFBAsync(requestId, ImpProposalType.TIMED_ABORT, content, callback);
        log.debug("Method returned");

        // VERIFICATION
        verify(mockPubSubClient).publisherPublish(Matchers.any(IIdentity.class), Matchers.eq(UserFeedbackEventTopics.REQUEST), Matchers.eq(requestId), Matchers.any(UserFeedbackBean.class));

        Assert.assertFalse(callback.isResponseRx());

        log.debug("Sending mock pubsub response");
        userFeedback.pubsubEvent(null, UserFeedbackEventTopics.IMPLICIT_RESPONSE, requestId, resultBean);
        log.debug("Mock pubsub response sent");

        Assert.assertTrue(callback.isResponseRx());
        Assert.assertNotNull("Expected callback to contain result", callback.getResult());
        Assert.assertEquals("Expected result bean to contain correct result", Boolean.TRUE, callback.getResult());
    }

    public void testGetImplicitFB_callback_ignoresWrongResponses() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        ImpProposalContent content = new ImpProposalContent(
                "Test proposal",
                30000
        );

        final String requestId = UUID.randomUUID().toString();
        final String wrongID = UUID.randomUUID().toString();

        final ImpFeedbackResultBean resultBean = new ImpFeedbackResultBean();
        resultBean.setAccepted(true);
        resultBean.setRequestId(requestId);

        EventCallback<Boolean> callback = new EventCallback<Boolean>();

        log.debug("Requesting IMP (Timed Abort) user feedback");
        userFeedback.getImplicitFBAsync(requestId, ImpProposalType.TIMED_ABORT, content, callback);
        log.debug("Method returned");

        log.debug("Sending wrong pubsub responses");

        resultBean.setRequestId(wrongID);
        userFeedback.pubsubEvent(null, UserFeedbackEventTopics.IMPLICIT_RESPONSE, wrongID, resultBean);
        Assert.assertFalse(callback.isResponseRx());

        resultBean.setRequestId(requestId);

        try {
            userFeedback.pubsubEvent(null, UserFeedbackEventTopics.EXPLICIT_RESPONSE, requestId, resultBean);
        } catch (Exception ex) {
            // do nothing - this is acceptable behaviour
        }
        Assert.assertFalse(callback.isResponseRx());

        try {
            userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, requestId, resultBean);
        } catch (Exception ex) {
            // do nothing - this is acceptable behaviour
        }
        Assert.assertFalse(callback.isResponseRx());

        try {
            userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE, requestId, resultBean);
        } catch (Exception ex) {
            // do nothing - this is acceptable behaviour
        }
        Assert.assertFalse(callback.isResponseRx());

        log.debug("Sending correct mock pubsub response");
        userFeedback.pubsubEvent(null, UserFeedbackEventTopics.IMPLICIT_RESPONSE, requestId, resultBean);

        Assert.assertTrue(callback.isResponseRx());
    }


    public void testShowNotification_fullSequence() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        ExpProposalContent content = new ExpProposalContent(
                "Test proposal",
                new String[]{"Yes", "No"}
        );

        String expectedResult = "Yes";

        final String requestId = UUID.randomUUID().toString();

        final ExpFeedbackResultBean resultBean = new ExpFeedbackResultBean();
        resultBean.setFeedback(new ArrayList<String>());
        resultBean.getFeedback().add(expectedResult);
        resultBean.setRequestId(requestId);

        log.debug("Preparing response thread");
        new Thread(new Runnable() {
            @Override
            public void run() {
                log.debug("Waiting " + SYNC_RESPONSE_DELAY + "ms to send mock pub sub response");

                try {
                    Thread.sleep(SYNC_RESPONSE_DELAY);
                } catch (InterruptedException e) {
                    log.error("Error delaying mock pubsub event", e);
                }

                log.debug("Sending mock pubsub response");
                userFeedback.pubsubEvent(null, UserFeedbackEventTopics.EXPLICIT_RESPONSE, requestId, resultBean);
                log.debug("Mock pubsub response sent");
            }
        }).start();

        log.debug("Requesting EXP (AckNack) user feedback");
        Future<List<String>> result = userFeedback.getExplicitFB(requestId, ExpProposalType.ACKNACK, content);
        log.debug("Method returned");

        // VERIFICATION
        verify(mockPubSubClient).publisherPublish(Matchers.any(IIdentity.class), Matchers.eq(UserFeedbackEventTopics.REQUEST), Matchers.eq(requestId), Matchers.any(UserFeedbackBean.class));

        Assert.assertNotNull("Expected Future to contain result", result.get());
        Assert.assertEquals("Expected result bean to contain 1 item", 1, result.get().size());
        Assert.assertEquals("Expected result bean to contain correct result", expectedResult, result.get().get(0));
    }


    public void testGetPrivacyNegotiationFB_sync_fullSequence() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        SecureRandom random = new SecureRandom();

        final String requestId = UUID.randomUUID().toString();
        final int negotiationId = new BigInteger(130, random).intValue();

        RequestorBean requestorBean = new RequestorBean();
        requestorBean.setRequestorId(requestId);

        NegotiationDetailsBean negotiationDetails = new NegotiationDetailsBean();
        negotiationDetails.setRequestor(requestorBean);
        negotiationDetails.setNegotiationID(negotiationId);

        ResponsePolicy responsePolicy = buildResponsePolicy(requestId, requestorBean);

        final UserFeedbackPrivacyNegotiationEvent resultBean = new UserFeedbackPrivacyNegotiationEvent();
        resultBean.setRequestId(requestId);
        resultBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
        resultBean.setNegotiationDetails(negotiationDetails);
        resultBean.setResponsePolicy(responsePolicy);

        log.debug("Preparing response thread");
        new Thread(new Runnable() {
            @Override
            public void run() {
                log.debug("Waiting " + SYNC_RESPONSE_DELAY + "ms to send mock pub sub response");

                try {
                    Thread.sleep(SYNC_RESPONSE_DELAY);
                } catch (InterruptedException e) {
                    log.error("Error delaying mock pubsub event", e);
                }

                log.debug("Sending mock pubsub response");
                userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, requestId, resultBean);
                log.debug("Mock pubsub response sent");
            }
        }).start();

        log.debug("Requesting PPN feedback");
        Future<ResponsePolicy> result = userFeedback.getPrivacyNegotiationFB(requestId, responsePolicy, negotiationDetails);
        log.debug("Method returned");

        // VERIFICATION
        verify(mockPubSubClient).publisherPublish(Matchers.any(IIdentity.class), Matchers.eq(EventTypes.UF_PRIVACY_NEGOTIATION), Matchers.eq(requestId), Matchers.any(UserFeedbackPrivacyNegotiationEvent.class));

        Assert.assertNotNull("Expected Future to contain result", result.get());
//        Assert.assertEquals("Expected result bean to contain 1 item", 1, result.get().size());
//        Assert.assertEquals("Expected result bean to contain correct result", expectedResult, result.get().get(0));
    }

    public void testGetPrivacyNegotiationFB_async_fullSequence() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        SecureRandom random = new SecureRandom();

        final String requestId = UUID.randomUUID().toString();
        final int negotiationId = new BigInteger(130, random).intValue();

        RequestorBean requestorBean = new RequestorBean();
        requestorBean.setRequestorId(requestId);

        NegotiationDetailsBean negotiationDetails = new NegotiationDetailsBean();
        negotiationDetails.setRequestor(requestorBean);
        negotiationDetails.setNegotiationID(negotiationId);

        ResponsePolicy responsePolicy = buildResponsePolicy(requestId, requestorBean);

        final UserFeedbackPrivacyNegotiationEvent resultBean = new UserFeedbackPrivacyNegotiationEvent();
        resultBean.setRequestId(requestId);
        resultBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
        resultBean.setNegotiationDetails(negotiationDetails);
        resultBean.setResponsePolicy(responsePolicy);

        log.debug("Requesting PPN feedback");
        Future<ResponsePolicy> result = userFeedback.getPrivacyNegotiationFBAsync(requestId, responsePolicy, negotiationDetails, null);
        log.debug("Method returned");

        // VERIFICATION
        verify(mockPubSubClient).publisherPublish(Matchers.any(IIdentity.class), Matchers.eq(EventTypes.UF_PRIVACY_NEGOTIATION), Matchers.eq(requestId), Matchers.any(UserFeedbackPrivacyNegotiationEvent.class));

        Assert.assertNull("Expected Future to be empty", result.get());

        log.debug("Sending mock pubsub response");
        userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, requestId, resultBean);
        log.debug("Mock pubsub response sent");

        Assert.assertNotNull("Expected Future to contain result", result.get());
//        Assert.assertEquals("Expected result bean to contain 1 item", 1, result.get().size());
//        Assert.assertEquals("Expected result bean to contain correct result", expectedResult, result.get().get(0));
    }

    public void testGetPrivacyNegotiationFB_callback_fullSequence() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        SecureRandom random = new SecureRandom();

        final String requestId = UUID.randomUUID().toString();
        final int negotiationId = new BigInteger(130, random).intValue();

        RequestorBean requestorBean = new RequestorBean();
        requestorBean.setRequestorId(requestId);

        NegotiationDetailsBean negotiationDetails = new NegotiationDetailsBean();
        negotiationDetails.setRequestor(requestorBean);
        negotiationDetails.setNegotiationID(negotiationId);

        ResponsePolicy responsePolicy = buildResponsePolicy(requestId, requestorBean);

        final UserFeedbackPrivacyNegotiationEvent resultBean = new UserFeedbackPrivacyNegotiationEvent();
        resultBean.setRequestId(requestId);
        resultBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
        resultBean.setNegotiationDetails(negotiationDetails);
        resultBean.setResponsePolicy(responsePolicy);

        EventCallback<ResponsePolicy> callback = new EventCallback<ResponsePolicy>();

        log.debug("Requesting PPN feedback");
        userFeedback.getPrivacyNegotiationFBAsync(requestId, responsePolicy, negotiationDetails, callback);
        log.debug("Method returned");

        // VERIFICATION
        verify(mockPubSubClient).publisherPublish(Matchers.any(IIdentity.class), Matchers.eq(EventTypes.UF_PRIVACY_NEGOTIATION), Matchers.eq(requestId), Matchers.any(UserFeedbackPrivacyNegotiationEvent.class));

        Assert.assertFalse(callback.isResponseRx());

        log.debug("Sending mock pubsub response");
        userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, requestId, resultBean);
        log.debug("Mock pubsub response sent");

        Assert.assertTrue(callback.isResponseRx());
        Assert.assertNotNull("Expected Future to contain result", callback.getResult());
//        Assert.assertEquals("Expected result bean to contain 1 item", 1, result.get().size());
//        Assert.assertEquals("Expected result bean to contain correct result", expectedResult, result.get().get(0));
    }

    public void testGetPrivacyNegotiationFB_callback_ignoresWrongResponses() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        SecureRandom random = new SecureRandom();

        final String requestId = UUID.randomUUID().toString();
        final int negotiationId = new BigInteger(130, random).intValue();
        final String wrongID = UUID.randomUUID().toString();

        RequestorBean requestorBean = new RequestorBean();
        requestorBean.setRequestorId(requestId);

        NegotiationDetailsBean negotiationDetails = new NegotiationDetailsBean();
        negotiationDetails.setRequestor(requestorBean);
        negotiationDetails.setNegotiationID(negotiationId);

        ResponsePolicy responsePolicy = buildResponsePolicy(requestId, requestorBean);

        final UserFeedbackPrivacyNegotiationEvent resultBean = new UserFeedbackPrivacyNegotiationEvent();
        resultBean.setRequestId(requestId);
        resultBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
        resultBean.setNegotiationDetails(negotiationDetails);
        resultBean.setResponsePolicy(responsePolicy);

        EventCallback<ResponsePolicy> callback = new EventCallback<ResponsePolicy>();

        log.debug("Requesting PPN feedback");
        userFeedback.getPrivacyNegotiationFBAsync(requestId, responsePolicy, negotiationDetails, callback);
        log.debug("Method returned");

        log.debug("Sending wrong pubsub responses");

        resultBean.setRequestId(wrongID);
        userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, wrongID, resultBean);
        Assert.assertFalse(callback.isResponseRx());

        resultBean.setRequestId(requestId);

        try {
            userFeedback.pubsubEvent(null, UserFeedbackEventTopics.IMPLICIT_RESPONSE, requestId, resultBean);
        } catch (Exception ex) {
            // do nothing - this is acceptable behaviour
        }
        Assert.assertFalse(callback.isResponseRx());

        try {
            userFeedback.pubsubEvent(null, UserFeedbackEventTopics.EXPLICIT_RESPONSE, requestId, resultBean);
        } catch (Exception ex) {
            // do nothing - this is acceptable behaviour
        }
        Assert.assertFalse(callback.isResponseRx());

        try {
            userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE, requestId, resultBean);
        } catch (Exception ex) {
            // do nothing - this is acceptable behaviour
        }
        Assert.assertFalse(callback.isResponseRx());

        log.debug("Sending correct mock pubsub response");
        userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, requestId, resultBean);

        Assert.assertTrue(callback.isResponseRx());
    }


    public void testGetAccessControlFB_sync_fullSequence() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        final String requestId = UUID.randomUUID().toString();

        IIdentity identity = mock(IIdentity.class);

        Requestor requestor = new Requestor(identity);

        List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
        responseItems.add(buildResponseItem("ab.cd.ef1", "type1"));
        responseItems.add(buildResponseItem("ab.cd.ef2", "type2"));
        responseItems.add(buildResponseItem("ab.cd.ef3", "type3"));

        final UserFeedbackAccessControlEvent resultBean = new UserFeedbackAccessControlEvent();
        resultBean.setRequestId(requestId);
        resultBean.setResponseItems(responseItems);
        resultBean.setRequestor(RequestorUtils.toRequestorBean(requestor));

        log.debug("Preparing response thread");
        new Thread(new Runnable() {
            @Override
            public void run() {
                log.debug("Waiting " + SYNC_RESPONSE_DELAY + "ms to send mock pub sub response");

                try {
                    Thread.sleep(SYNC_RESPONSE_DELAY);
                } catch (InterruptedException e) {
                    log.error("Error delaying mock pubsub event", e);
                }

                log.debug("Sending mock pubsub response");
                userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE, requestId, resultBean);
                log.debug("Mock pubsub response sent");
            }
        }).start();

        log.debug("Requesting Access Control user feedback");
        Future<List<ResponseItem>> result = userFeedback.getAccessControlFB(requestId, requestor, responseItems);
        log.debug("Method returned");

        // VERIFICATION
//        verify(mockUserFeedbackHistoryRepository).insert(any(UserFeedbackAccessControlEvent.class));
        verify(mockPubSubClient).publisherPublish(Matchers.any(IIdentity.class), Matchers.eq(EventTypes.UF_PRIVACY_ACCESS_CONTROL), Matchers.eq(requestId), Matchers.any(UserFeedbackAccessControlEvent.class));

        Assert.assertNotNull("Expected Future to contain result", result.get());
//        Assert.assertEquals("Expected result bean to contain 1 item", 1, result.get().size());
//        Assert.assertEquals("Expected result bean to contain correct result", expectedResult, result.get().get(0));
    }

    public void testGetAccessControlFB_async_fullSequence() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        final String requestId = UUID.randomUUID().toString();

        IIdentity identity = mock(IIdentity.class);

        Requestor requestor = new Requestor(identity);

        List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
        responseItems.add(buildResponseItem("ab.cd.ef1", "type1"));
        responseItems.add(buildResponseItem("ab.cd.ef2", "type2"));
        responseItems.add(buildResponseItem("ab.cd.ef3", "type3"));

        final UserFeedbackAccessControlEvent resultBean = new UserFeedbackAccessControlEvent();
        resultBean.setRequestId(requestId);
        resultBean.setResponseItems(responseItems);
        resultBean.setRequestor(RequestorUtils.toRequestorBean(requestor));

        log.debug("Requesting Access Control user feedback");
        Future<List<ResponseItem>> result = userFeedback.getAccessControlFBAsync(requestId, requestor, responseItems, null);
        log.debug("Method returned");

        // VERIFICATION
//        verify(mockUserFeedbackHistoryRepository).insert(any(UserFeedbackAccessControlEvent.class));
        verify(mockPubSubClient).publisherPublish(Matchers.any(IIdentity.class), Matchers.eq(EventTypes.UF_PRIVACY_ACCESS_CONTROL), Matchers.eq(requestId), Matchers.any(UserFeedbackAccessControlEvent.class));

        Assert.assertNull("Expected Future to be empty", result.get());

        log.debug("Sending mock pubsub response");
        userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE, requestId, resultBean);
        log.debug("Mock pubsub response sent");

        Assert.assertNotNull("Expected Future to contain result", result.get());
//        Assert.assertEquals("Expected result bean to contain 1 item", 1, result.get().size());
//        Assert.assertEquals("Expected result bean to contain correct result", expectedResult, result.get().get(0));
    }

    public void testGetAccessControlFB_callback_fullSequence() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        final String requestId = UUID.randomUUID().toString();

        IIdentity identity = mock(IIdentity.class);

        Requestor requestor = new Requestor(identity);

        List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
        responseItems.add(buildResponseItem("ab.cd.ef1", "type1"));
        responseItems.add(buildResponseItem("ab.cd.ef2", "type2"));
        responseItems.add(buildResponseItem("ab.cd.ef3", "type3"));

        final UserFeedbackAccessControlEvent resultBean = new UserFeedbackAccessControlEvent();
        resultBean.setRequestId(requestId);
        resultBean.setResponseItems(responseItems);
        resultBean.setRequestor(RequestorUtils.toRequestorBean(requestor));

        EventCallback<List<ResponseItem>> callback = new EventCallback<List<ResponseItem>>();

        log.debug("Requesting Access Control user feedback");
        userFeedback.getAccessControlFBAsync(requestId, requestor, responseItems, callback);
        log.debug("Method returned");

        // VERIFICATION
//        verify(mockUserFeedbackHistoryRepository).insert(any(UserFeedbackAccessControlEvent.class));
        verify(mockPubSubClient).publisherPublish(Matchers.any(IIdentity.class), Matchers.eq(EventTypes.UF_PRIVACY_ACCESS_CONTROL), Matchers.eq(requestId), Matchers.any(UserFeedbackAccessControlEvent.class));

        Assert.assertFalse(callback.isResponseRx());

        log.debug("Sending mock pubsub response");
        userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE, requestId, resultBean);
        log.debug("Mock pubsub response sent");

        Assert.assertTrue(callback.isResponseRx());
        Assert.assertNotNull("Expected Future to contain result", callback.getResult());
//        Assert.assertEquals("Expected result bean to contain 1 item", 1, result.get().size());
//        Assert.assertEquals("Expected result bean to contain correct result", expectedResult, result.get().get(0));
    }

    public void testGetAccessControlFB_callback_ignoresWrongResponses() throws ExecutionException, InterruptedException, CommunicationException, XMPPError {

        final String requestId = UUID.randomUUID().toString();
        final String wrongID = UUID.randomUUID().toString();

        IIdentity identity = mock(IIdentity.class);

        Requestor requestor = new Requestor(identity);

        List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
        responseItems.add(buildResponseItem("ab.cd.ef1", "type1"));
        responseItems.add(buildResponseItem("ab.cd.ef2", "type2"));
        responseItems.add(buildResponseItem("ab.cd.ef3", "type3"));

        final UserFeedbackAccessControlEvent resultBean = new UserFeedbackAccessControlEvent();
        resultBean.setRequestId(requestId);
        resultBean.setResponseItems(responseItems);
        resultBean.setRequestor(RequestorUtils.toRequestorBean(requestor));

        EventCallback<List<ResponseItem>> callback = new EventCallback<List<ResponseItem>>();

        log.debug("Requesting Access Control user feedback");
        userFeedback.getAccessControlFBAsync(requestId, requestor, responseItems, callback);
        log.debug("Method returned");

        log.debug("Sending wrong pubsub responses");

        resultBean.setRequestId(wrongID);
        userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE, wrongID, resultBean);
        Assert.assertFalse(callback.isResponseRx());

        resultBean.setRequestId(requestId);

        try {
            userFeedback.pubsubEvent(null, UserFeedbackEventTopics.IMPLICIT_RESPONSE, requestId, resultBean);
        } catch (Exception ex) {
            // do nothing - this is acceptable behaviour
        }
        Assert.assertFalse(callback.isResponseRx());

        try {
            userFeedback.pubsubEvent(null, UserFeedbackEventTopics.EXPLICIT_RESPONSE, requestId, resultBean);
        } catch (Exception ex) {
            // do nothing - this is acceptable behaviour
        }
        Assert.assertFalse(callback.isResponseRx());

        try {
            userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, requestId, resultBean);
        } catch (Exception ex) {
            // do nothing - this is acceptable behaviour
        }
        Assert.assertFalse(callback.isResponseRx());

        log.debug("Sending correct mock pubsub response");
        userFeedback.pubsubEvent(null, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE, requestId, resultBean);

        Assert.assertTrue(callback.isResponseRx());

    }


    private static ResponsePolicy buildResponsePolicy(String guid, RequestorBean requestorBean) {
        List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
        responseItems.add(buildResponseItem("http://this.is.a.win/", "winning - " + guid));
        responseItems.add(buildResponseItem("http://paddy.rules/", "paddy"));
        responseItems.add(buildResponseItem("http://something.something.something/", "dark side"));

        ResponsePolicy responsePolicy = new ResponsePolicy();
        responsePolicy.setRequestor(requestorBean);
        responsePolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
        responsePolicy.setResponseItems(responseItems);
        return responsePolicy;
    }

    private static ResponseItem buildResponseItem(String uri, String dataType) {
        Action action1 = new Action();
        action1.setActionConstant(ActionConstants.CREATE);
        action1.setOptional(true);
        Action action2 = new Action();
        action2.setActionConstant(ActionConstants.DELETE);
        action2.setOptional(false);
        Action action3 = new Action();
        action3.setActionConstant(ActionConstants.READ);
        action3.setOptional(false);
        Action action4 = new Action();
        action4.setActionConstant(ActionConstants.WRITE);
        action4.setOptional(true);

        Condition condition1 = new Condition();
        condition1.setConditionConstant(ConditionConstants.DATA_RETENTION_IN_HOURS);
        condition1.setValue("1");
        condition1.setOptional(false);
        Condition condition2 = new Condition();
        condition2.setConditionConstant(ConditionConstants.RIGHT_TO_ACCESS_HELD_DATA);
        condition2.setValue("2");
        condition2.setOptional(true);
        Condition condition3 = new Condition();
        condition3.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
        condition3.setValue("3");
        condition3.setOptional(false);
        Condition condition4 = new Condition();
        condition4.setConditionConstant(ConditionConstants.STORE_IN_SECURE_STORAGE);
        condition4.setValue("4");
        condition4.setOptional(true);

        Resource resource = new Resource();
        resource.setDataIdUri(uri);
        resource.setDataType(dataType);

        RequestItem requestItem = new RequestItem();
        requestItem.getActions().add(action1);
        requestItem.getActions().add(action2);
        requestItem.getActions().add(action3);
        requestItem.getActions().add(action4);

        requestItem.getConditions().add(condition1);
        requestItem.getConditions().add(condition2);
        requestItem.getConditions().add(condition3);
        requestItem.getConditions().add(condition4);

        requestItem.setOptional(false);
        requestItem.setResource(resource);

        ResponseItem responseItem = new ResponseItem();
        responseItem.setDecision(Decision.INDETERMINATE);
        responseItem.setRequestItem(requestItem);
        return responseItem;
    }


}

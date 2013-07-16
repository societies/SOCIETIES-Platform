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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.feedback.IUserFeedbackResponseEventListener;
import org.societies.api.internal.useragent.model.*;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.useragent.feedback.*;
import org.societies.useragent.api.feedback.IInternalUserFeedback;
import org.societies.useragent.api.model.UserFeedbackEventTopics;
import org.societies.useragent.feedback.guis.AckNackGUI;
import org.societies.useragent.feedback.guis.CheckBoxGUI;
import org.societies.useragent.feedback.guis.RadioGUI;
import org.societies.useragent.feedback.guis.TimedGUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.*;
import java.util.concurrent.Future;

import static org.societies.api.schema.useragent.feedback.FeedbackMethodType.GET_EXPLICIT_FB;

public class UserFeedback implements IUserFeedback, IInternalUserFeedback, Subscriber {

    private static final Logger log = LoggerFactory.getLogger(UserFeedback.class);

    //pubsub event schemas
    private static final List<String> EVENT_SCHEMA_CLASSES =
            Collections.unmodifiableList(Arrays.asList(
                    "org.societies.api.schema.useragent.feedback.UserFeedbackBean",
                    "org.societies.api.schema.useragent.feedback.UserFeedbackHistoryRequest",
                    "org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean",
                    "org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean",
                    "org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent",
                    "org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent"));

    //GUI types for forms
    private static final String RADIO = "radio";
    private static final String CHECK = "check";
    private static final String ACK = "ack";
    private static final String ABORT = "abort";
    private static final String NOTIFICATION = "notification";
    private static final String PRIVACY_NEGOTIATION = "privacy-negotiation";
    private static final String PRIVACY_ACCESS_CONTROL = "privacy-access-control";
    private static final String UNDEFINED = "undefined";

    @Autowired
    private ICommManager commsMgr;

    @Autowired
    private PubsubClient pubsub;

//    @Autowired
//    private IUserFeedbackHistoryRepository userFeedbackHistoryRepository; // TODO: re-enable me after fixing bug #2096

//    @Autowired
//    private IPrivacyPolicyNegotiationHistoryRepository privacyPolicyNegotiationHistoryRepository; // TODO: re-enable me after fixing bug #2096

    private final Map<String, UserFeedbackResult<List<String>>> expResults = new HashMap<String, UserFeedbackResult<List<String>>>();
    private final Map<String, IUserFeedbackResponseEventListener<List<String>>> expCallbacks = new HashMap<String, IUserFeedbackResponseEventListener<List<String>>>();
    private final Map<String, UserFeedbackResult<Boolean>> impResults = new HashMap<String, UserFeedbackResult<Boolean>>();
    private final Map<String, IUserFeedbackResponseEventListener<Boolean>> impCallbacks = new HashMap<String, IUserFeedbackResponseEventListener<Boolean>>();
    private final Map<String, UserFeedbackResult<ResponsePolicy>> negotiationResults = new HashMap<String, UserFeedbackResult<ResponsePolicy>>();
    private final Map<String, IUserFeedbackResponseEventListener<ResponsePolicy>> negotiationCallbacks = new HashMap<String, IUserFeedbackResponseEventListener<ResponsePolicy>>();
    private final Map<String, UserFeedbackResult<List<ResponseItem>>> accessCtrlResults = new HashMap<String, UserFeedbackResult<List<ResponseItem>>>();
    private final Map<String, IUserFeedbackResponseEventListener<List<ResponseItem>>> accessCtrlCallbacks = new HashMap<String, IUserFeedbackResponseEventListener<List<ResponseItem>>>();

    private RequestManager requestMgr;
    private IIdentity myCloudID;


    public void initialiseUserFeedback() {
        log.debug("User Feedback initialising");

        requestMgr = new RequestManager();
        expResults.clear();
        impResults.clear();
        negotiationResults.clear();
        accessCtrlResults.clear();
        expCallbacks.clear();
        impCallbacks.clear();
        negotiationCallbacks.clear();
        accessCtrlCallbacks.clear();

        //get cloud ID
        myCloudID = commsMgr.getIdManager().getThisNetworkNode();
        log.debug("Got my cloud ID: " + myCloudID);

        //create pubsub node
        try {
            log.debug("Creating user feedback pubsub node");
            pubsub.addSimpleClasses(EVENT_SCHEMA_CLASSES);
            pubsub.ownerCreate(myCloudID, UserFeedbackEventTopics.REQUEST);
            pubsub.ownerCreate(myCloudID, UserFeedbackEventTopics.EXPLICIT_RESPONSE);
            pubsub.ownerCreate(myCloudID, UserFeedbackEventTopics.IMPLICIT_RESPONSE);
            pubsub.ownerCreate(myCloudID, UserFeedbackEventTopics.COMPLETE);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_ACCESS_CONTROL);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_ACCESS_CONTROL_REMOVE_POPUP);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE);
            log.debug("Pubsub node created!");
        } catch (Exception e) {
            log.error("Error creating user feedback pubsub nodes", e);
        }

        //register for events from created pubsub node
        try {
            log.debug("Registering for user feedback pubsub node");
            pubsub.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.REQUEST, this);
            pubsub.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.EXPLICIT_RESPONSE, this);
            pubsub.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.IMPLICIT_RESPONSE, this);
            pubsub.subscriberSubscribe(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, this);
            pubsub.subscriberSubscribe(myCloudID, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE, this);
            log.debug("Pubsub registration complete!");
        } catch (Exception e) {
            log.error("Error registering for user feedback pubsub nodes", e);
        }

        recallStoredUFRequests();

        recallStoredPpnRequests();

        String msg = "User Feedback Initialised\n" +
                " Exp UF requests: %s\n" +
                " Imp UF requests: %s\n" +
                " PPN requests: %s\n" +
                " AC requests: %s";
        log.debug(String.format(msg,
                expResults.size(),
                impResults.size(),
                negotiationResults.size(),
                accessCtrlResults.size()));
    }

    private void recallStoredPpnRequests() {
        try {
            log.debug("Recalling stored PPN requests");

//            List<UserFeedbackPrivacyNegotiationEvent> userFeedbackPrivacyNegotiationEvents = privacyPolicyNegotiationHistoryRepository.listIncomplete();
            List<UserFeedbackPrivacyNegotiationEvent> userFeedbackPrivacyNegotiationEvents = new ArrayList<UserFeedbackPrivacyNegotiationEvent>();// TODO: re-enable me
            for (UserFeedbackPrivacyNegotiationEvent userFeedbackPrivacyNegotiationEvent : userFeedbackPrivacyNegotiationEvents) {
                String requestId = userFeedbackPrivacyNegotiationEvent.getRequestId();

                UserFeedbackResult<ResponsePolicy> result = new UserFeedbackResult<ResponsePolicy>(requestId);
                negotiationResults.put(requestId, result);

                // TODO: there's no way to store the callback for the PPN request
                // If the platform has been restarted, there's a good bet the requesting service will have been restarted
                // too, so a callback would be pointless anyway. It's going to have to resume its operations based on the database records
//                if (callback != null) {
//                    expCallbacks.put(requestId, callback);
//                }
            }

            log.debug("Finished recalling stored PPN requests");
        } catch (Exception ex) {
            log.error("Error recalling stored PPN requests #216 - UserFeedback will continue without database support. \n" + ex.getMessage());
        }
    }

    private void recallStoredUFRequests() {
        try {
            log.debug("Recalling stored UF requests");

//            List<UserFeedbackBean> userFeedbackBeans = userFeedbackHistoryRepository.listIncomplete(); // TODO: re-enable me after fixing bug #2096
            List<UserFeedbackBean> userFeedbackBeans = new ArrayList<UserFeedbackBean>();

            for (UserFeedbackBean userFeedbackBean : userFeedbackBeans) {
                String requestId = userFeedbackBean.getRequestId();
                switch (userFeedbackBean.getMethod()) {
                    case GET_EXPLICIT_FB:
                        UserFeedbackResult<List<String>> expResult = new UserFeedbackResult<List<String>>(requestId);
                        expResults.put(requestId, expResult);
                        break;
                    case GET_IMPLICIT_FB:
                        UserFeedbackResult<Boolean> impResult = new UserFeedbackResult<Boolean>(requestId);
                        impResults.put(requestId, impResult);
                        break;
                }

                // TODO: there's no way to store the callback for the UF request
                // If the platform has been restarted, there's a good bet the requesting service will have been restarted
                // too, so a callback would be pointless anyway. It's going to have to resume its operations based on the database records
//                if (callback != null) {
//                    expCallbacks.put(requestId, callback);
//                }
            }

            log.debug("Finished recalling stored UF requests");
        } catch (Exception ex) {
            log.error("Error recalling stored UF requests #193 - UserFeedback will continue without database support. \n" + ex.getMessage());
        }
    }


    public Future<List<String>> getExplicitFB(String requestId, int type, ExpProposalContent content) {
        Future<List<String>> result = getExplicitFBAsync(requestId, type, content, null);

        // wait until complete, or timeout has expired
        while (!result.isDone()) {
            try {
                synchronized (result) {
                    result.wait(100);
                }
            } catch (InterruptedException e) {
                log.warn("Error waiting for result", e);
            }
        }

        try {
//            return result.get();
            return result;
        } catch (Exception e) {
            log.warn("Error parsing result from Future", e);
            return null;
        }
    }

    @Override
    public Future<List<String>> getExplicitFB(int type, ExpProposalContent content) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getExplicitFB(requestId, type, content);
    }

    @Override
    public Future<List<String>> getExplicitFBAsync(int type, ExpProposalContent content) {
        return getExplicitFBAsync(type, content, null);
    }

    @Override
    public Future<List<String>> getExplicitFBAsync(int type, ExpProposalContent content, IUserFeedbackResponseEventListener<List<String>> callback) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getExplicitFBAsync(requestId, type, content, callback);
    }

    public Future<List<String>> getExplicitFBAsync(String requestId, int type, ExpProposalContent content, IUserFeedbackResponseEventListener<List<String>> callback) {
        if (log.isDebugEnabled()) {
            log.debug("Received request for explicit feedback\n" +
                    "    Content: " + content.getProposalText());
        }

        //create user feedback bean to fire in pubsub event
        UserFeedbackBean ufBean = new UserFeedbackBean();
//        ufBean.setRequestDate(new Date());
        ufBean.setStage(FeedbackStage.PENDING_USER_RESPONSE);
        ufBean.setRequestId(requestId);
        ufBean.setType(type);
        ufBean.setProposalText(content.getProposalText());
        List<String> optionsList = new ArrayList<String>();
        Collections.addAll(optionsList, content.getOptions());
        ufBean.setOptions(optionsList);
        ufBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);

        //add new request to result hashmap
        UserFeedbackResult<List<String>> result = new UserFeedbackResult<List<String>>(requestId);

        synchronized (expResults) {
            expResults.put(requestId, result);
        }

        if (callback != null) {
            synchronized (expCallbacks) {
                expCallbacks.put(requestId, callback);
            }
        }

        // TODO: re-enable me after fixing bug #2096
//        // store in database before sending pubsub event
//        try {
//            if (userFeedbackHistoryRepository == null) {
//                log.warn("userFeedbackHistoryRepository is null - cannot store user feedback request bean in database");
//            } else {
//                if (log.isDebugEnabled())
//                    log.debug("Storing user feedback bean in database");
//
//                userFeedbackHistoryRepository.insert(ufBean);
//            }
//        } catch (Exception ex) {
//            log.error("Error storing user feedback request bean to database #318 - UserFeedback will continue without database support. \n" + ex.getMessage());
//        }

        //send pubsub event to all user agents
        try {
            if (log.isDebugEnabled())
                log.debug("Sending user feedback request event via pubsub with ID " + requestId);

            // HACK: When hibernate persists the ufBean object, it changes the options list to a org.hibernate.collection.PersistentList
            // When this is deserialised at the other side, hibernate gets upset. Really the serialiser should be converting any
            // PersistentList back to an ArrayList
            ufBean.setOptions(optionsList);

            pubsub.publisherPublish(myCloudID,
                    UserFeedbackEventTopics.REQUEST,
                    requestId,
                    ufBean);
        } catch (Exception ex) {
            log.error("Error transmitting user feedback request bean via pubsub", ex);
        }

        return result;
    }


    public Future<Boolean> getImplicitFB(String requestId, int type, ImpProposalContent content) {
        Future<Boolean> result = getImplicitFBAsync(requestId, type, content, null);

        // wait until complete, or timeout has expired
        while (!result.isDone()) {
            try {
                synchronized (result) {
                    result.wait(100);
                }
            } catch (InterruptedException e) {
                log.warn("Error waiting for result", e);
            }
        }

        try {
//            return result.get();
            return result;
        } catch (Exception e) {
            log.warn("Error parsing result from Future", e);
            return null;
        }
    }

    @Override
    public Future<Boolean> getImplicitFB(int type, ImpProposalContent content) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getImplicitFB(requestId, type, content);
    }

    @Override
    public Future<Boolean> getImplicitFBAsync(int type, ImpProposalContent content) {
        return getImplicitFBAsync(type, content, null);
    }

    @Override
    public Future<Boolean> getImplicitFBAsync(int type, ImpProposalContent content, IUserFeedbackResponseEventListener<Boolean> callback) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getImplicitFBAsync(requestId, type, content, callback);
    }

    public Future<Boolean> getImplicitFBAsync(String requestId, int type, ImpProposalContent content, IUserFeedbackResponseEventListener<Boolean> callback) {
        if (log.isDebugEnabled()) {
            log.debug("Received request for implicit feedback\n" +
                    "    Content: " + content.getProposalText());
        }

        //create user feedback bean to fire in pubsub event
        UserFeedbackBean ufBean = new UserFeedbackBean();
//        ufBean.setRequestDate(new Date());
        ufBean.setStage(FeedbackStage.PENDING_USER_RESPONSE);
        ufBean.setRequestId(requestId);
        ufBean.setType(type);
        ufBean.setProposalText(content.getProposalText());
        ufBean.setTimeout(content.getTimeout());
        ufBean.setMethod(FeedbackMethodType.GET_IMPLICIT_FB);

        //add new request to result hashmap
        UserFeedbackResult<Boolean> result = new UserFeedbackResult<Boolean>(requestId);

        synchronized (impResults) {
            impResults.put(requestId, result);
        }

        if (callback != null) {
            synchronized (impCallbacks) {
                impCallbacks.put(requestId, callback);
            }
        }

        // TODO: re-enable me after fixing bug #2096
//        // store in database before sending pubsub event
//        try {
//            if (userFeedbackHistoryRepository == null) {
//                log.warn("userFeedbackHistoryRepository is null - cannot store user feedback request bean in database");
//            } else {
//                if (log.isDebugEnabled())
//                    log.debug("Storing user feedback bean in database");
//
//                userFeedbackHistoryRepository.insert(ufBean);
//            }
//        } catch (Exception ex) {
//            log.error("Error storing user feedback request bean to database #427 - UserFeedback will continue without database support. \n" + ex.getMessage());
//        }

        //send pubsub event to all user agents
        try {
            if (log.isDebugEnabled())
                log.debug("Sending user feedback request event via pubsub with ID " + requestId);

            // HACK: When hibernate persists the ufBean object, it changes the options list to a org.hibernate.collection.PersistentList
            // When this is deserialised at the other side, hibernate gets upset. Really the serialiser should be converting any
            // PersistentList back to an ArrayList
            ufBean.setOptions(new ArrayList<String>()); // list is empty anyway for implicit feedback

            pubsub.publisherPublish(myCloudID,
                    UserFeedbackEventTopics.REQUEST,
                    requestId,
                    ufBean);
        } catch (Exception ex) {
            log.error("Error transmitting user feedback request bean via pubsub", ex);
        }

        return result;
    }


    public Future<ResponsePolicy> getPrivacyNegotiationFB(String requestId, ResponsePolicy policy, NegotiationDetailsBean details) {
        Future<ResponsePolicy> result = getPrivacyNegotiationFBAsync(requestId, policy, details, null);

        // wait until complete, or timeout has expired
        while (!result.isDone()) {
            try {
                synchronized (result) {
                    result.wait(100);
                }
            } catch (InterruptedException e) {
                log.warn("Error waiting for result", e);
            }
        }

        try {
//            return result.get();
            return result;
        } catch (Exception e) {
            log.warn("Error parsing result from Future", e);
            return null;
        }
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFB(ResponsePolicy policy, NegotiationDetailsBean details) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getPrivacyNegotiationFB(requestId, policy, details);
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(ResponsePolicy policy, NegotiationDetailsBean details) {
        return getPrivacyNegotiationFBAsync(policy, details, null);
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(ResponsePolicy policy, NegotiationDetailsBean details, IUserFeedbackResponseEventListener<ResponsePolicy> callback) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getPrivacyNegotiationFBAsync(requestId, policy, details, callback);
    }

    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(String requestId, ResponsePolicy policy, NegotiationDetailsBean details, IUserFeedbackResponseEventListener<ResponsePolicy> callback) {

        if (log.isDebugEnabled()) {
            log.debug("processing negotiationFeedback request");
            if (policy == null) {
                log.debug("Policy parameter is null");
            } else {
                log.debug("Policy contains: " + policy.getResponseItems().size() + " responseItems");
            }
        }

        UserFeedbackPrivacyNegotiationEvent event = new UserFeedbackPrivacyNegotiationEvent();
        event.setStage(FeedbackStage.PENDING_USER_RESPONSE);
        event.setMethod(GET_EXPLICIT_FB);
        event.setType(ExpProposalType.PRIVACY_NEGOTIATION);
        event.setRequestId(requestId);
        event.setNegotiationDetails(details);
        event.setResponsePolicy(policy);


        UserFeedbackResult<ResponsePolicy> result = new UserFeedbackResult<ResponsePolicy>(requestId);
        synchronized (negotiationResults) {
            negotiationResults.put(requestId, result);
        }
        if (callback != null) {
            synchronized (negotiationCallbacks) {
                negotiationCallbacks.put(requestId, callback);
            }
        }

        // TODO: re-enable me after fixing bug #2096
//        // store in database before sending pubsub event
//        try {
//            if (privacyPolicyNegotiationHistoryRepository == null) {
//                log.warn("privacyPolicyNegotiationHistoryRepository is null - cannot store PPN request bean in database");
//            } else {
//                if (log.isDebugEnabled())
//                    log.debug("Storing PPN bean in database");
//
//                privacyPolicyNegotiationHistoryRepository.insert(event);
//            }
//        } catch (Exception ex) {
//            log.error("Error storing PPN request bean to database #537 - UserFeedback will continue without database support. \n" + ex.getMessage());
//        }

        try {
            if (log.isDebugEnabled())
                log.debug("Sending PPN request event via pubsub");


            // HACK: When hibernate persists the ufBean object, it changes the options list to a org.hibernate.collection.PersistentList
            // When this is deserialised at the other side, hibernate gets upset. Really the serialiser should be converting any
            // PersistentList back to an ArrayList
            event.getResponsePolicy().setResponseItems(new ArrayList<ResponseItem>(event.getResponsePolicy().getResponseItems()));
            for (ResponseItem responseItem : event.getResponsePolicy().getResponseItems()) {
                responseItem.getRequestItem().setActions(new ArrayList<Action>(responseItem.getRequestItem().getActions()));
                responseItem.getRequestItem().setConditions(new ArrayList<Condition>(responseItem.getRequestItem().getConditions()));
            }

            pubsub.publisherPublish(myCloudID,
                    EventTypes.UF_PRIVACY_NEGOTIATION,
                    requestId,
                    event);
        } catch (Exception ex) {
            log.error("Error transmitting PPN request bean via pubsub", ex);
        }

        return result;
    }


    public Future<List<ResponseItem>> getAccessControlFB(String requestId, Requestor requestor, List<ResponseItem> items) {
        Future<List<ResponseItem>> result = getAccessControlFBAsync(requestId, requestor, items, null);

        // wait until complete, or timeout has expired
        while (!result.isDone()) {
            try {
                synchronized (result) {
                    result.wait(100);
                }
            } catch (InterruptedException e) {
                log.warn("Error waiting for result", e);
            }
        }

        try {
//            return result.get();
            return result;
        } catch (Exception e) {
            log.warn("Error parsing result from Future", e);
            return null;
        }
    }

    @Override
    public Future<List<ResponseItem>> getAccessControlFB(Requestor requestor, List<ResponseItem> items) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getAccessControlFB(requestId, requestor, items);
    }

    @Override
    public Future<List<ResponseItem>> getAccessControlFBAsync(Requestor requestor, List<ResponseItem> items) {
        return getAccessControlFBAsync(requestor, items, null);
    }

    @Override
    public Future<List<ResponseItem>> getAccessControlFBAsync(Requestor requestor, List<ResponseItem> items, IUserFeedbackResponseEventListener<List<ResponseItem>> callback) {
        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        return getAccessControlFBAsync(requestId, requestor, items, callback);
    }

    public Future<List<ResponseItem>> getAccessControlFBAsync(String requestId, Requestor requestor, List<ResponseItem> items, IUserFeedbackResponseEventListener<List<ResponseItem>> callback) {
        UserFeedbackAccessControlEvent event = new UserFeedbackAccessControlEvent();
        event.setMethod(GET_EXPLICIT_FB);
        event.setType(ExpProposalType.PRIVACY_ACCESS_CONTROL);
        event.setRequestId(requestId);
        event.setRequestor(RequestorUtils.toRequestorBean(requestor));
        event.setResponseItems(items);

        UserFeedbackResult<List<ResponseItem>> result = new UserFeedbackResult<List<ResponseItem>>(requestId);
        synchronized (accessCtrlResults) {
            accessCtrlResults.put(requestId, result);
        }
        if (callback != null) {
            synchronized (accessCtrlCallbacks) {
                accessCtrlCallbacks.put(requestId, callback);
            }
        }

        // TODO: store in database before sending pubsub event
//        try {
//            if (accessCtrlHistoryRepository == null) {
//                log.warn("accessCtrlHistoryRepository is null - cannot store user feedback request bean in database");
//            } else {
//                if (log.isDebugEnabled())
//                    log.debug("Storing user feedback bean in database");
//                accessCtrlHistoryRepository.insert(event);
//            }
//        } catch (Exception ex) {
//            log.error("Error storing user feedback request bean to database #638 - UserFeedback will continue without database support. \n" + ex.getMessage());
//        }


        try {
            if (log.isDebugEnabled())
                log.debug("Sending access control request event via pubsub");

            // HACK: When hibernate persists the ufBean object, it changes the options list to a org.hibernate.collection.PersistentList
            // When this is deserialised at the other side, hibernate gets upset. Really the serialiser should be converting any
            // PersistentList back to an ArrayList
            event.setResponseItems(new ArrayList<ResponseItem>(event.getResponseItems()));
            for (ResponseItem responseItem : event.getResponseItems()) {
                responseItem.getRequestItem().setActions(new ArrayList<Action>(responseItem.getRequestItem().getActions()));
                responseItem.getRequestItem().setConditions(new ArrayList<Condition>(responseItem.getRequestItem().getConditions()));
            }

            this.pubsub.publisherPublish(myCloudID,
                    EventTypes.UF_PRIVACY_ACCESS_CONTROL,
                    requestId,
                    event);
        } catch (Exception ex) {
            log.error("Error transmitting access control request bean via pubsub", ex);
        }

        return result;
    }


    @Override
    public void showNotification(String notificationTxt) {
        log.debug("Received request for notification");
        log.debug("Content: " + notificationTxt);

        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        //create user feedback bean to fire in pubsub event
        UserFeedbackBean ufBean = new UserFeedbackBean();
//        ufBean.setRequestDate(new Date());
        ufBean.setStage(FeedbackStage.COMPLETED);
        ufBean.setRequestId(requestId);
        ufBean.setProposalText(notificationTxt);
        ufBean.setMethod(FeedbackMethodType.SHOW_NOTIFICATION);

        // TODO: re-enable me after fixing bug #2096
//        // store in database before sending pubsub event
//        try {
//            if (userFeedbackHistoryRepository == null) {
//                log.warn("userFeedbackHistoryRepository is null - cannot store user feedback request bean in database");
//
//            } else {
//                if (log.isDebugEnabled())
//                    log.debug("Storing user feedback bean in database");
//
//                userFeedbackHistoryRepository.insert(ufBean);
//                ufBean = userFeedbackHistoryRepository.getByRequestId(requestId);
//            }
//        } catch (Exception ex) {
//            log.error("Error storing user feedback request bean to database #696 - UserFeedback will continue without database support. \n" + ex.getMessage());
//        }

        //send pubsub event to all user agents
        try {
            if (log.isDebugEnabled())
                log.debug("Sending user feedback request event via pubsub");

            pubsub.publisherPublish(myCloudID,
                    UserFeedbackEventTopics.REQUEST,
                    requestId,
                    ufBean);
        } catch (Exception ex) {
            log.error("Error transmitting user feedback request bean via pubsub", ex);
        }

        // NB: No wait for a simple notification
        // NB: No response/completed event for a simple notification
    }


    @Override
    public void pubsubEvent(IIdentity identity, String eventTopic, String itemID, Object item) {
        if (item == null) {
            log.warn(String.format("Received pubsub event with NULL PAYLOAD - topic '%s', ID '%s'",
                    eventTopic,
                    itemID
            ));
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug(String.format("Received pubsub event with topic '%s', ID '%s' and class '%s'",
                    eventTopic,
                    itemID,
                    item.getClass().getSimpleName()
            ));
        }

        if (eventTopic.equalsIgnoreCase(UserFeedbackEventTopics.REQUEST)) {

//            if (item instanceof ElementNSImpl) {
//                log.warn("Pubsub item is ElementNSImpl, using wrapped user data");
//                item = ((ElementNSImpl) item).item(0);
//            }

//            if (item == null || !(item instanceof UserFeedbackBean)) {
//                log.warn(String.format("Received pubsub event with topic '%s', ID '%s' and class '%s' - Required UserFeedbackBean ",
//                        eventTopic,
//                        itemID,
//                        item != null ? item.getClass().getCanonicalName() : "[null item]"
//                ));
//                return;
//            }

            //read from request bean
            UserFeedbackBean ufBean = (UserFeedbackBean) item;
            switch (ufBean.getMethod()) {
                case GET_EXPLICIT_FB:
                    String expRequestID = ufBean.getRequestId();
                    int expType = ufBean.getType();
                    String expProposalText = ufBean.getProposalText();
                    List<String> optionsList = ufBean.getOptions();
                    this.processExpFeedbackRequestEvent(expRequestID, expType, expProposalText, optionsList);
                    break;
                case GET_IMPLICIT_FB:
                    String impRequestID = ufBean.getRequestId();
                    int impType = ufBean.getType();
                    String impProposalText = ufBean.getProposalText();
                    int timeout = ufBean.getTimeout();
                    this.processImpFeedbackRequestEvent(impRequestID, impType, impProposalText, timeout);
                    break;
                case SHOW_NOTIFICATION:
                    String notRequestID = ufBean.getRequestId();
                    String notProposalText = ufBean.getProposalText();
                    this.processNotificationRequestEvent(notRequestID, notProposalText);
                    break;
            }
        } else if (eventTopic.equalsIgnoreCase(UserFeedbackEventTopics.EXPLICIT_RESPONSE)) {
            //read from explicit response bean
            ExpFeedbackResultBean expFeedbackBean = (ExpFeedbackResultBean) item;
            this.processExpResponseEvent(expFeedbackBean);

        } else if (eventTopic.equalsIgnoreCase(UserFeedbackEventTopics.IMPLICIT_RESPONSE)) {
            //read from implicit response bean
            ImpFeedbackResultBean impFeedbackBean = (ImpFeedbackResultBean) item;
            this.processImpResponseEvent(impFeedbackBean);

        } else if (eventTopic.equalsIgnoreCase(EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE)) {
            UserFeedbackPrivacyNegotiationEvent event = (UserFeedbackPrivacyNegotiationEvent) item;
            this.processPrivacyPolicyNegotiationResponseEvent(event);

        } else if (eventTopic.equalsIgnoreCase(EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE)) {
            UserFeedbackAccessControlEvent event = (UserFeedbackAccessControlEvent) item;
            this.processAccessControlResponseEvent(event);

        }
    }


    /*
     * Handle explicit feedback request and response events
     */
    private void processExpFeedbackRequestEvent(String requestId, int type, String proposalText, List<String> optionsList) {
        //create feedback form
        FeedbackForm fbForm = generateExpFeedbackForm(requestId, type, proposalText, optionsList);
        //add new request to queue
        requestMgr.addRequest(fbForm);
    }

    private void processExpResponseEvent(ExpFeedbackResultBean expFeedbackBean) {

        String responseID = expFeedbackBean.getRequestId();

        //remove from request manager list if exists
        synchronized (requestMgr) {
            requestMgr.removeRequest(responseID);
        }

        //set result value in hashmap
        synchronized (expResults) {
            if (!expResults.containsKey(responseID)) {
                if (log.isTraceEnabled())
                    log.trace(String.format("This isn't the node where the exp feedback request ID [%s] originated",
                            responseID));

                if (log.isTraceEnabled()) {
                    StringBuilder bld = new StringBuilder();
                    bld.append("Exp feedback requests outstanding:-\n");
                    for (String s : expResults.keySet()) {
                        bld.append(" - ");
                        bld.append(s);
                        bld.append('\n');
                    }
                    log.trace(bld.toString());
                }

                return;
            }

            if (log.isDebugEnabled())
                log.debug("This is the node where the exp feedback request originated");

            // TODO: re-enable me after fixing bug #2096
//            // update result
//            try {
//                if (userFeedbackHistoryRepository != null) {
//                    userFeedbackHistoryRepository.completeExpFeedback(responseID, expFeedbackBean.getFeedback());
//                }
//            } catch (Exception ex) {
//                log.error("Error updating user feedback stage in database #844 - UserFeedback will continue without database support. \n" + ex.getMessage());
//            }

            // inform clients that UF is complete
            try {
                pubsub.publisherPublish(myCloudID,
                        UserFeedbackEventTopics.COMPLETE,
                        responseID,
                        expFeedbackBean);
            } catch (Exception ex) {
                log.error("Error transmitting user feedback complete via pubsub", ex);
            }

            final UserFeedbackResult<List<String>> userFeedbackResult = expResults.get(responseID);
            synchronized (userFeedbackResult) {
                userFeedbackResult.complete(expFeedbackBean.getFeedback());
                userFeedbackResult.notifyAll();
            }

            if (expCallbacks.containsKey(responseID)) {
                IUserFeedbackResponseEventListener<List<String>> callback = expCallbacks.remove(responseID);
                callback.responseReceived(expFeedbackBean.getFeedback());
            }

            this.expResults.notifyAll();
        }
    }

    private void processImpFeedbackRequestEvent(String requestId, int type, String proposalText, int timeout) {
        //create feedback form
        FeedbackForm fbForm = generateImpFeedbackForm(requestId, type, proposalText, timeout);
        //add new request to queue
        requestMgr.addRequest(fbForm);
    }

    private void processImpResponseEvent(ImpFeedbackResultBean impFeedbackBean) {

        String responseID = impFeedbackBean.getRequestId();

        //remove from request manager list if exists
        synchronized (requestMgr) {
            requestMgr.removeRequest(responseID);
        }

        //set result value in hashmap
        synchronized (impResults) {
            if (!impResults.containsKey(responseID)) {
                if (log.isTraceEnabled())
                    log.trace(String.format("This isn't the node where the imp feedback request ID [%s] originated",
                            responseID));

                if (log.isTraceEnabled()) {
                    StringBuilder bld = new StringBuilder();
                    bld.append("Imp feedback requests outstanding:-\n");
                    for (String s : impResults.keySet()) {
                        bld.append(" - ");
                        bld.append(s);
                        bld.append('\n');
                    }
                    log.trace(bld.toString());
                }

                return;
            }

            if (log.isDebugEnabled())
                log.debug("This is the node where the imp feedback request originated");

            // TODO: re-enable me after fixing bug #2096
//            // update result
//            try {
//                if (userFeedbackHistoryRepository != null) {
//                    userFeedbackHistoryRepository.completeImpFeedback(responseID, impFeedbackBean.isAccepted());
//                }
//            } catch (Exception ex) {
//                log.error("Error updating user feedback stage in database #917 - UserFeedback will continue without database support. \n" + ex.getMessage());
//            }

            // inform clients that UF is complete
            try {
                pubsub.publisherPublish(myCloudID,
                        UserFeedbackEventTopics.COMPLETE,
                        responseID,
                        impFeedbackBean);
            } catch (Exception ex) {
                log.error("Error transmitting user feedback complete via pubsub", ex);
            }

            final UserFeedbackResult<Boolean> userFeedbackResult = impResults.get(responseID);
            synchronized (userFeedbackResult) {
                userFeedbackResult.complete(impFeedbackBean.isAccepted());
                userFeedbackResult.notifyAll();
            }

            if (impCallbacks.containsKey(responseID)) {
                IUserFeedbackResponseEventListener<Boolean> callback = impCallbacks.remove(responseID);
                callback.responseReceived(impFeedbackBean.isAccepted());
            }

            this.impResults.notifyAll();
        }
    }

    /*
     * Handle notification request events
     */
    private void processNotificationRequestEvent(String requestId, String proposalText) {
        //create feedback form
        FeedbackForm fbForm = generateNotificationForm(requestId, proposalText);
        //add new request to queue
        requestMgr.addRequest(fbForm);
    }

    private void processPrivacyPolicyNegotiationResponseEvent(UserFeedbackPrivacyNegotiationEvent result) {

        String responseID = result.getRequestId();

        //remove from request manager list if exists
        synchronized (requestMgr) {
            requestMgr.removeRequest(responseID);
        }

        //set result value in hashmap
        synchronized (negotiationResults) {
            if (!negotiationResults.containsKey(responseID)) {
                if (log.isTraceEnabled())
                    log.trace(String.format("This isn't the node where the PPN request ID [%s] originated",
                            responseID));

                if (log.isTraceEnabled()) {
                    StringBuilder bld = new StringBuilder();
                    bld.append("PPN requests outstanding:-\n");
                    for (String s : negotiationResults.keySet()) {
                        bld.append(" - ");
                        bld.append(s);
                        bld.append('\n');
                    }
                    log.trace(bld.toString());
                }

                return;
            }

            if (log.isDebugEnabled())
                log.debug("This is the node where the PPN request originated");

            // TODO: re-enable me after fixing bug #2096
//            // update result
//            try {
//                if (privacyPolicyNegotiationHistoryRepository != null) {
//                    privacyPolicyNegotiationHistoryRepository.updateStage(responseID, FeedbackStage.COMPLETED);
//                }
//            } catch (Exception ex) {
//                log.error("Error updating PPN stage in database #993 - UserFeedback will continue without database support. \n" + ex.getMessage());
//            }

            // inform clients that negotiation is complete
            try {
                pubsub.publisherPublish(myCloudID,
                        EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP,
                        responseID,
                        negotiationResults.get(responseID));
            } catch (Exception ex) {
                log.error("Error transmitting PPN complete via pubsub", ex);
            }

            final UserFeedbackResult<ResponsePolicy> userFeedbackResult = negotiationResults.get(responseID);
            synchronized (userFeedbackResult) {
                userFeedbackResult.complete(result.getResponsePolicy());
                userFeedbackResult.notifyAll();
            }

            if (negotiationCallbacks.containsKey(responseID)) {
                IUserFeedbackResponseEventListener<ResponsePolicy> callback = negotiationCallbacks.remove(responseID);
                callback.responseReceived(result.getResponsePolicy());
            }


            this.negotiationResults.notifyAll();
        }

    }


    private void processAccessControlResponseEvent(UserFeedbackAccessControlEvent result) {

        String responseID = result.getRequestId();

        //remove from request manager list if exists
        synchronized (requestMgr) {
            requestMgr.removeRequest(responseID);
        }

        //set result value in hashmap
        synchronized (accessCtrlResults) {
            if (!accessCtrlResults.containsKey(responseID)) {
                if (log.isTraceEnabled())
                    log.trace(String.format("This isn't the node where the AC request ID [%s] originated",
                            responseID));

                if (log.isTraceEnabled()) {
                    StringBuilder bld = new StringBuilder();
                    bld.append("AC requests outstanding:-\n");
                    for (String s : accessCtrlResults.keySet()) {
                        bld.append(" - ");
                        bld.append(s);
                        bld.append('\n');
                    }
                    log.trace(bld.toString());
                }

                return;
            }

            if (log.isDebugEnabled())
                log.debug("This is the node where the AC feedback request originated");

            // update result
//            try {
//                if (userFeedbackHistoryRepository != null) {
//                    userFeedbackHistoryRepository.updateStage(responseID, FeedbackStage.COMPLETED);
//                }
//            } catch (Exception ex) {
//                log.error("Error updating access control request stage in database #1062 - UserFeedback will continue without database support. \n" + ex.getMessage());
//            }
            // inform clients that negotiation is complete
            try {
                pubsub.publisherPublish(myCloudID,
                        EventTypes.UF_PRIVACY_ACCESS_CONTROL_REMOVE_POPUP,
                        responseID,
                        accessCtrlResults.get(responseID));
            } catch (Exception ex) {
                log.error("Error transmitting access control complete via pubsub", ex);
            }

            final UserFeedbackResult<List<ResponseItem>> userFeedbackResult = accessCtrlResults.get(responseID);
            synchronized (userFeedbackResult) {
                userFeedbackResult.complete(result.getResponseItems());
                userFeedbackResult.notifyAll();
            }

            if (accessCtrlCallbacks.containsKey(responseID)) {
                IUserFeedbackResponseEventListener<List<ResponseItem>> callback = accessCtrlCallbacks.remove(responseID);
                callback.responseReceived(result.getResponseItems());
            }

            this.accessCtrlResults.notifyAll();
        }
    }


    /*
     * The following methods are called by the UserFeedbackController as part of the platform web-app
     *
     * (non-Javadoc)
     * @see org.societies.api.internal.useragent.feedback.IUserFeedback#getNextRequest()
     */
    @Override
    public FeedbackForm getNextRequest() {
        return requestMgr.getNextRequest();
    }

    @Override
    public void submitExplicitResponse(String requestId, List<String> result) {
        //create user feedback response bean
        ExpFeedbackResultBean resultBean = new ExpFeedbackResultBean();
        resultBean.setRequestId(requestId);
        resultBean.setFeedback(result);

        //fire response pubsub event to all user agents
        try {
            pubsub.publisherPublish(myCloudID, UserFeedbackEventTopics.EXPLICIT_RESPONSE, requestId, resultBean);
        } catch (XMPPError e) {
            log.error("Error submitting explicit response", e);
        } catch (CommunicationException e) {
            log.error("Error submitting explicit response", e);
        }
    }

    @Override
    public void submitExplicitResponse(String requestId, NegotiationDetailsBean negotiationDetails, ResponsePolicy result) {
        //create user feedback response bean
        UserFeedbackPrivacyNegotiationEvent resultBean = new UserFeedbackPrivacyNegotiationEvent();
        resultBean.setMethod(GET_EXPLICIT_FB);
        resultBean.setType(ExpProposalType.PRIVACY_NEGOTIATION);
        resultBean.setRequestId(requestId);
        resultBean.setNegotiationDetails(negotiationDetails);
        resultBean.setResponsePolicy(result);

        //fire response pubsub event to all user agents
        try {
            log.info("####### Publish " + EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE + ": " + ResponseItemUtils.toXmlString(result.getResponseItems()));
            pubsub.publisherPublish(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, requestId, resultBean);
        } catch (XMPPError e) {
            log.error("Error submitting negotiation response", e);
        } catch (CommunicationException e) {
            log.error("Error submitting negotiation response", e);
        }
    }

    @Override
    public void submitImplicitResponse(String requestId, Boolean result) {
        //create user feedback response bean
        ImpFeedbackResultBean resultBean = new ImpFeedbackResultBean();
        resultBean.setRequestId(requestId);
        resultBean.setAccepted(result);

        //fire response pubsub event to all user agents
        try {
            pubsub.publisherPublish(myCloudID, UserFeedbackEventTopics.IMPLICIT_RESPONSE, null, resultBean);
        } catch (XMPPError e) {
            log.error("Error submitting implicit response", e);
        } catch (CommunicationException e) {
            log.error("Error submitting implicit response", e);
        }
    }


    /*
     * Helper methods to generate feedback forms - explicit, implicit and notification
     */
    private static FeedbackForm generateExpFeedbackForm(String requestId, int type, String proposalText, List<String> optionsList) {
        FeedbackForm newFbForm = new FeedbackForm();
        //add unique id
        newFbForm.setID(requestId);
        //add text to show to user
        newFbForm.setText(proposalText);
        //add data
        String[] optionsArray = new String[optionsList.size()];
        for (int i = 0; i < optionsList.size(); i++) {
            optionsArray[i] = optionsList.get(i);
        }
        newFbForm.setData(optionsArray);
        //add type
        if (type == ExpProposalType.RADIOLIST) {
            newFbForm.setType(RADIO);
        } else if (type == ExpProposalType.CHECKBOXLIST) {
            newFbForm.setType(CHECK);
        } else if (type == ExpProposalType.ACKNACK) {
            newFbForm.setType(ACK);
        } else {
            log.error("Could not understand this type of explicit GUI: " + type);
        }
        return newFbForm;
    }

    private static FeedbackForm generateImpFeedbackForm(String requestId, int type, String proposalText, int timeout) {
        FeedbackForm newFbForm = new FeedbackForm();
        //add unique id
        newFbForm.setID(requestId);
        //add text to show user
        newFbForm.setText(proposalText);
        //add data
        String[] data = {Integer.toString(timeout)};
        newFbForm.setData(data);
        //add type
        if (type == ImpProposalType.TIMED_ABORT) {
            newFbForm.setType(ABORT);
        } else {
            log.error("Could not understand this type of implicit GUI: " + type);
        }
        return newFbForm;
    }

    private static FeedbackForm generateNotificationForm(String requestId, String notificationTxt) {
        FeedbackForm newFbForm = new FeedbackForm();
        //add unique id
        newFbForm.setID(requestId);
        //add text to show user
        newFbForm.setText(notificationTxt);
        //add data
        String[] data = {"5000"};
        newFbForm.setData(data);
        //add type
        newFbForm.setType(NOTIFICATION);
        return newFbForm;
    }

    /*
     *Called by UACommsServer to request explicit feedback for remote User Agent
     *
     * (non-Javadoc)
     * @see org.societies.useragent.api.feedback.IInternalUserFeedback#getExplicitFBforRemote(int, org.societies.api.internal.useragent.model.ExpProposalContent)
     */
    @Override
    public Future<List<String>> getExplicitFBforRemote(int type, ExpProposalContent content) {
        log.debug("Request for explicit feedback received from remote User Agent");
        List<String> result;

        //show GUIs on local device
        log.debug("Returning explicit feedback to UACommsServer");
        String proposalText = content.getProposalText();
        String[] options = content.getOptions();
        if (type == ExpProposalType.RADIOLIST) {
            log.debug("Radio list GUI");
            RadioGUI gui = new RadioGUI();
            result = gui.displayGUI(proposalText, options);
        } else if (type == ExpProposalType.CHECKBOXLIST) {
            log.debug("Check box list GUI");
            CheckBoxGUI gui = new CheckBoxGUI();
            result = gui.displayGUI(proposalText, options);
        } else { //ACK-NACK
            log.debug("ACK/NACK GUI");
            result = AckNackGUI.displayGUI(proposalText, options);
        }

        return new AsyncResult<List<String>>(result);
    }

    /*
     * Called by UACommsServer to request implicit feedback for remote User Agent
     *
     * (non-Javadoc)
     * @see org.societies.useragent.api.feedback.IInternalUserFeedback#getImplicitFBforRemote(int, org.societies.api.internal.useragent.model.ImpProposalContent)
     */
    @Override
    public Future<Boolean> getImplicitFBforRemote(int type, ImpProposalContent content) {
        log.debug("Request for implicit feedback received from remote User Agent");
        Boolean result = null;

        //show GUIs on local device
        log.debug("Returning implicit feedback to UACommsServer");
        String proposalText = content.getProposalText();
        int timeout = content.getTimeout();
        if (type == ImpProposalType.TIMED_ABORT) {
            log.debug("Timed Abort GUI");
            TimedGUI gui = new TimedGUI();
            result = gui.displayGUI(proposalText, timeout);
        }

        return new AsyncResult<Boolean>(result);
    }

    @Override
    public List<UserFeedbackBean> listStoredFeedbackBeans(int howMany) {
//        return userFeedbackHistoryRepository.listPrevious(howMany);
        return new ArrayList<UserFeedbackBean>(); // TODO: re-enable me after fixing bug #2096
    }

    @Override
    public List<UserFeedbackBean> listStoredFeedbackBeans(Date sinceWhen) {
//        return userFeedbackHistoryRepository.listSince(sinceWhen);
        return new ArrayList<UserFeedbackBean>(); // TODO: re-enable me after fixing bug #2096
    }

    @Override
    public List<UserFeedbackBean> listIncompleteFeedbackBeans() {
//        return userFeedbackHistoryRepository.listIncomplete();
        return new ArrayList<UserFeedbackBean>(); // TODO: re-enable me after fixing bug #2096
    }

    public void setCommsMgr(ICommManager commsMgr) {
        this.commsMgr = commsMgr;
    }

    public void setPubsub(PubsubClient pubsub) {
        this.pubsub = pubsub;
    }

    // TODO: re-enable me after fixing bug #2096
//    public void setUserFeedbackHistoryRepository(IUserFeedbackHistoryRepository userFeedbackHistoryRepository) {
//        this.userFeedbackHistoryRepository = userFeedbackHistoryRepository;
//    }
//
//    public void setPrivacyPolicyNegotiationHistoryRepository(IPrivacyPolicyNegotiationHistoryRepository privacyPolicyNegotiationHistoryRepository) {
//        this.privacyPolicyNegotiationHistoryRepository = privacyPolicyNegotiationHistoryRepository;
//    }

    /**
     * This is a non-api method which is used by integration tests to clear the internal state of the UF module
     */
    @Override
    public void clear() {
        synchronized (this) {
            expResults.clear();
            expCallbacks.clear();
            impResults.clear();
            impCallbacks.clear();
            negotiationResults.clear();
            negotiationCallbacks.clear();
            accessCtrlResults.clear();
            accessCtrlCallbacks.clear();
        }
    }
}

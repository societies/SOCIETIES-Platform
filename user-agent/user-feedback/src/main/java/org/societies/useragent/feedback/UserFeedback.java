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
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.*;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.privacytrust.privacy.util.privacypolicy.ResponseItemUtils;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.useragent.feedback.*;
import org.societies.useragent.api.feedback.IInternalUserFeedback;
import org.societies.useragent.api.feedback.IPrivacyPolicyNegotiationHistoryRepository;
import org.societies.useragent.api.feedback.IUserFeedbackHistoryRepository;
import org.societies.useragent.api.model.UserFeedbackEventTopics;
import org.societies.useragent.api.remote.IUserAgentRemoteMgr;
import org.societies.useragent.feedback.guis.AckNackGUI;
import org.societies.useragent.feedback.guis.CheckBoxGUI;
import org.societies.useragent.feedback.guis.RadioGUI;
import org.societies.useragent.feedback.guis.TimedGUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.*;
import java.util.concurrent.Future;

public class UserFeedback implements IUserFeedback, IInternalUserFeedback, Subscriber {

    private static final Logger LOG = LoggerFactory.getLogger(UserFeedback.class);

    //pubsub event schemas
    private static final List<String> EVENT_SCHEMA_CLASSES =
            Collections.unmodifiableList(Arrays.asList(
                    "org.societies.api.schema.useragent.feedback.UserFeedbackBean",
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
    private ICtxBroker ctxBroker;

    @Autowired
    private ICommManager commsMgr;

    @Autowired
    private PubsubClient pubsub;

    @Autowired
    private IUserAgentRemoteMgr uaRemote;

    @Autowired
    private IUserFeedbackHistoryRepository userFeedbackHistoryRepository;

    @Autowired
    private IPrivacyPolicyNegotiationHistoryRepository privacyPolicyNegotiationHistoryRepository;

    private final HashMap<String, List<String>> expResults = new HashMap<String, List<String>>();
    private final HashMap<String, Boolean> impResults = new HashMap<String, Boolean>();
    private final HashMap<NegotiationDetailsBean, ResponsePolicy> negotiationResults = new HashMap<NegotiationDetailsBean, ResponsePolicy>();
    private final HashMap<String, List<ResponseItem>> accessCtrlResults = new HashMap<String, List<ResponseItem>>();

    private RequestManager requestMgr;
    private IIdentity myCloudID;


    public void initialiseUserFeedback() {
        LOG.debug("User Feedback initialising");

        requestMgr = new RequestManager();
        expResults.clear();
        impResults.clear();
        negotiationResults.clear();
        accessCtrlResults.clear();

        //get cloud ID
        myCloudID = commsMgr.getIdManager().getThisNetworkNode();
        LOG.debug("Got my cloud ID: " + myCloudID);

        //create pubsub node
        try {
            LOG.debug("Creating user feedback pubsub node");
            pubsub.addSimpleClasses(EVENT_SCHEMA_CLASSES);
            pubsub.ownerCreate(myCloudID, UserFeedbackEventTopics.REQUEST);
            pubsub.ownerCreate(myCloudID, UserFeedbackEventTopics.EXPLICIT_RESPONSE);
            pubsub.ownerCreate(myCloudID, UserFeedbackEventTopics.IMPLICIT_RESPONSE);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_ACCESS_CONTROL);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_ACCESS_CONTROL_REMOVE_POPUP);
            pubsub.ownerCreate(myCloudID, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE);
            LOG.debug("Pubsub node created!");
        } catch (XMPPError e) {
            e.printStackTrace();
        } catch (CommunicationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //register for events from created pubsub node
        try {
            LOG.debug("Registering for user feedback pubsub node");
            pubsub.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.REQUEST, this);
            pubsub.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.EXPLICIT_RESPONSE, this);
            pubsub.subscriberSubscribe(myCloudID, UserFeedbackEventTopics.IMPLICIT_RESPONSE, this);
            pubsub.subscriberSubscribe(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, this);
            pubsub.subscriberSubscribe(myCloudID, EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE, this);
            LOG.debug("Pubsub registration complete!");
        } catch (XMPPError e) {
            e.printStackTrace();
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
        LOG.debug("User Feedback Initialised!!!");
    }

    @Override
    public Future<List<String>> getExplicitFB(int type, ExpProposalContent content) {
        LOG.debug("Received request for explicit feedback");
        LOG.debug("Content: " + content.getProposalText());

        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        //create user feedback bean to fire in pubsub event
        UserFeedbackBean ufBean = new UserFeedbackBean();
        ufBean.setStage(FeedbackStage.PENDING_USER_RESPONSE);
        ufBean.setRequestId(requestId);
        ufBean.setType(type);
        ufBean.setProposalText(content.getProposalText());
        List<String> optionsList = new ArrayList<String>();
        Collections.addAll(optionsList, content.getOptions());
        ufBean.setOptions(optionsList);
        ufBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);

        //add new request to result hashmap
        expResults.put(requestId, null);

        // store in database before sending pubsub event
        try {
            if (userFeedbackHistoryRepository == null) {
                LOG.warn("userFeedbackHistoryRepository is null - cannot store user feedback request bean in database");
            } else {
                if (LOG.isDebugEnabled())
                    LOG.debug("Storing user feedback bean in database");
                userFeedbackHistoryRepository.insert(ufBean);
            }
        } catch (Exception ex) {
            LOG.error("Error storing user feedback request bean to database", ex);
        }

        //send pubsub event to all user agents
        try {
            if (LOG.isDebugEnabled())
                LOG.debug("Sending user feedback request event via pubsub");
            pubsub.publisherPublish(myCloudID,
                    UserFeedbackEventTopics.REQUEST,
                    requestId,
                    ufBean);
        } catch (Exception ex) {
            LOG.error("Error transmitting user feedback request bean via pubsub", ex);
        }

        //wait for result
        while (this.expResults.get(requestId) == null) {
            try {
                synchronized (expResults) {
                    this.expResults.wait();
                }
            } catch (InterruptedException e) {
                // do nothing?
            }
        }

        // update result
        try {
            if (userFeedbackHistoryRepository != null) {
                userFeedbackHistoryRepository.updateStage(requestId, FeedbackStage.COMPLETED);
            }
        } catch (Exception ex) {
            LOG.error("Error updating user feedback stage in database", ex);
        }
        // inform clients that UF is complete
        try {
            pubsub.publisherPublish(myCloudID,
                    UserFeedbackEventTopics.COMPLETE,
                    requestId,
                    null);
        } catch (Exception ex) {
            LOG.error("Error transmitting user feedback complete via pubsub", ex);
        }

        //set result and remove id from hashmap
        List<String> result = this.expResults.get(requestId);
        this.expResults.remove(requestId);

        return new AsyncResult<List<String>>(result);
    }

    @Override
    public Future<Boolean> getImplicitFB(int type, ImpProposalContent content) {
        LOG.debug("Received request for implicit feedback");
        LOG.debug("Content: " + content.getProposalText());

        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        //create user feedback bean to fire in pubsub event
        UserFeedbackBean ufBean = new UserFeedbackBean();
        ufBean.setStage(FeedbackStage.PENDING_USER_RESPONSE);
        ufBean.setRequestId(requestId);
        ufBean.setType(type);
        ufBean.setProposalText(content.getProposalText());
        ufBean.setTimeout(content.getTimeout());
        ufBean.setMethod(FeedbackMethodType.GET_IMPLICIT_FB);

        //add new request to result hashmap
        impResults.put(requestId, null);

        // store in database before sending pubsub event
        try {
            if (userFeedbackHistoryRepository == null) {
                LOG.warn("userFeedbackHistoryRepository is null - cannot store user feedback request bean in database");
            } else {
                if (LOG.isDebugEnabled())
                    LOG.debug("Storing user feedback bean in database");
                userFeedbackHistoryRepository.insert(ufBean);
            }
        } catch (Exception ex) {
            LOG.error("Error storing user feedback request bean to database", ex);
        }

        //send pubsub event to all user agents
        try {
            if (LOG.isDebugEnabled())
                LOG.debug("Sending user feedback request event via pubsub");
            pubsub.publisherPublish(myCloudID,
                    UserFeedbackEventTopics.REQUEST,
                    requestId,
                    ufBean);
        } catch (Exception ex) {
            LOG.error("Error transmitting user feedback request bean via pubsub", ex);
        }
        // update result
        try {
            if (userFeedbackHistoryRepository != null) {
                userFeedbackHistoryRepository.updateStage(requestId, FeedbackStage.COMPLETED);
            }
        } catch (Exception ex) {
            LOG.error("Error updating user feedback stage in database", ex);
        }

        //wait until result is available
        while (this.impResults.get(requestId) == null) {
            try {
                synchronized (impResults) {
                    this.impResults.wait();
                }
            } catch (Exception e) {
                // do nothing?
            }
        }

        // update result
        try {
            if (userFeedbackHistoryRepository != null) {
                userFeedbackHistoryRepository.updateStage(requestId, FeedbackStage.COMPLETED);
            }
        } catch (Exception ex) {
            LOG.error("Error updating user feedback stage in database", ex);
        }
        // inform clients that UF is complete
        try {
            pubsub.publisherPublish(myCloudID,
                    UserFeedbackEventTopics.COMPLETE,
                    requestId,
                    null);
        } catch (Exception ex) {
            LOG.error("Error transmitting user feedback complete via pubsub", ex);
        }

        //set result and remove id from hashmap
        Boolean result = this.impResults.get(requestId);
        this.impResults.remove(requestId);

        return new AsyncResult<Boolean>(result);
    }


    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFB(ResponsePolicy policy, NegotiationDetailsBean details) {

        LOG.debug("processing negotiationFeedback request");
        if (policy == null) {
            LOG.debug("Policy parameter is null");
        } else {
            LOG.debug("Policy contains: " + policy.getResponseItems().size() + " responseItems");
        }
        UserFeedbackPrivacyNegotiationEvent event = new UserFeedbackPrivacyNegotiationEvent();
        event.setStage(FeedbackStage.PENDING_USER_RESPONSE);
        event.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
        event.setType(ExpProposalType.PRIVACY_NEGOTIATION);
        String requestId = UUID.randomUUID().toString();
        event.setRequestId(requestId);
        event.setNegotiationDetails(details);
        event.setResponsePolicy(policy);

        // store in database before sending pubsub event
        try {
            if (privacyPolicyNegotiationHistoryRepository == null) {
                LOG.warn("privacyPolicyNegotiationHistoryRepository is null - cannot store PPN request bean in database");
            } else {
                if (LOG.isDebugEnabled())
                    LOG.debug("Storing PPN bean in database");
                privacyPolicyNegotiationHistoryRepository.insert(event);
            }
        } catch (Exception ex) {
            LOG.error("Error storing PPN request bean to database", ex);
        }

        try {
            if (LOG.isDebugEnabled())
                LOG.debug("Sending PPN request event via pubsub");
            pubsub.publisherPublish(myCloudID,
                    EventTypes.UF_PRIVACY_NEGOTIATION,
                    requestId,
                    event);
        } catch (Exception ex) {
            LOG.error("Error transmitting PPN request bean via pubsub", ex);
        }

        while (!containsKey(negotiationResults, details)) {
            synchronized (this.negotiationResults) {
                try {
                    this.negotiationResults.wait();
                } catch (InterruptedException e) {
                    // do nothing?
                }
            }
        }

        // update result
        try {
            if (privacyPolicyNegotiationHistoryRepository != null) {
                privacyPolicyNegotiationHistoryRepository.updateStage(requestId, FeedbackStage.COMPLETED);
            }
        } catch (Exception ex) {
            LOG.error("Error updating PPN stage in database", ex);
        }
        // inform clients that negotiation is complete
        try {
            pubsub.publisherPublish(myCloudID,
                    EventTypes.UF_PRIVACY_NEGOTIATION_REMOVE_POPUP,
                    requestId,
                    negotiationResults.get(details));
        } catch (Exception ex) {
            LOG.error("Error transmitting PPN complete via pubsub", ex);
        }


        return new AsyncResult<ResponsePolicy>(negotiationResults.get(details));
    }

    @Override
    public Future<List<ResponseItem>> getAccessControlFB(Requestor requestor, List<ResponseItem> items) {
        UserFeedbackAccessControlEvent event = new UserFeedbackAccessControlEvent();
        event.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
        event.setType(ExpProposalType.PRIVACY_ACCESS_CONTROL);
        String requestId = UUID.randomUUID().toString();
        event.setRequestId(requestId);
        event.setRequestor(RequestorUtils.toRequestorBean(requestor));
        event.setResponseItems(items);

        // TODO: store in database before sending pubsub event
//        try {
//            if (userFeedbackHistoryRepository == null) {
//                LOG.warn("userFeedbackHistoryRepository is null - cannot store user feedback request bean in database");
//            } else {
//                if (LOG.isDebugEnabled())
//                    LOG.debug("Storing user feedback bean in database");
//                userFeedbackHistoryRepository.insert(event);
//            }
//        } catch (Exception ex) {
//            LOG.error("Error storing user feedback request bean to database", ex);
//        }


        try {
            if (LOG.isDebugEnabled())
                LOG.debug("Sending access control request event via pubsub");
            this.pubsub.publisherPublish(myCloudID,
                    EventTypes.UF_PRIVACY_ACCESS_CONTROL,
                    requestId,
                    event);
        } catch (Exception ex) {
            LOG.error("Error transmitting access control request bean via pubsub", ex);
        }

        while (!this.accessCtrlResults.containsKey(requestId)) {
            synchronized (this.accessCtrlResults) {
                try {
                    this.accessCtrlResults.wait();
                } catch (InterruptedException e) {
                    // do nothing?
                }
            }
        }

//        // update result
//        try {
//            if (userFeedbackHistoryRepository != null) {
//                userFeedbackHistoryRepository.updateStage(requestId, FeedbackStage.COMPLETED);
//            }
//        } catch (Exception ex) {
//            LOG.error("Error updating user feedback stage in database", ex);
//        }
        // inform clients that negotiation is complete
        try {
            this.pubsub.publisherPublish(myCloudID,
                    EventTypes.UF_PRIVACY_ACCESS_CONTROL_REMOVE_POPUP,
                    requestId,
                    accessCtrlResults.get(requestId));
        } catch (Exception ex) {
            LOG.error("Error transmitting access control complete via pubsub", ex);
        }

        return new AsyncResult<List<ResponseItem>>(this.accessCtrlResults.get(requestId));
    }


    @Override
    public void showNotification(String notificationTxt) {
        LOG.debug("Received request for notification");
        LOG.debug("Content: " + notificationTxt);

        //generate unique ID for this pubsub event and feedback request
        String requestId = UUID.randomUUID().toString();

        //create user feedback bean to fire in pubsub event
        UserFeedbackBean ufBean = new UserFeedbackBean();
        ufBean.setStage(FeedbackStage.COMPLETED);
        ufBean.setRequestId(requestId);
        ufBean.setProposalText(notificationTxt);
        ufBean.setMethod(FeedbackMethodType.SHOW_NOTIFICATION);

        // store in database before sending pubsub event
        try {
            if (userFeedbackHistoryRepository == null) {
                LOG.warn("userFeedbackHistoryRepository is null - cannot store user feedback request bean in database");
            } else {
                if (LOG.isDebugEnabled())
                    LOG.debug("Storing user feedback bean in database");
                userFeedbackHistoryRepository.insert(ufBean);
            }
        } catch (Exception ex) {
            LOG.error("Error storing user feedback request bean to database", ex);
        }

        //send pubsub event to all user agents
        try {
            LOG.debug("Sending user feedback request event via pubsub");
            pubsub.publisherPublish(myCloudID,
                    UserFeedbackEventTopics.REQUEST,
                    requestId,
                    ufBean);
        } catch (Exception ex) {
            LOG.error("Error transmitting user feedback request bean via pubsub", ex);
        }

        // NB: No wait for a simple notification
        // NB: No response/completed event for a simple notification
    }


    @Override
    public void pubsubEvent(IIdentity identity, String eventTopic, String itemID, Object item) {
        LOG.debug("Received pubsub event with topic: " + eventTopic);

        if (eventTopic.equalsIgnoreCase(UserFeedbackEventTopics.REQUEST)) {
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
            String expResponseID = expFeedbackBean.getRequestId();
            List<String> expResult = expFeedbackBean.getFeedback();
            this.processExpResponseEvent(expResponseID, expResult);
        } else if (eventTopic.equalsIgnoreCase(UserFeedbackEventTopics.IMPLICIT_RESPONSE)) {
            //read from implicit response bean
            ImpFeedbackResultBean impFeedbackBean = (ImpFeedbackResultBean) item;
            String impResponseID = impFeedbackBean.getRequestId();
            boolean impResult = impFeedbackBean.isAccepted();
            this.processImpResponseEvent(impResponseID, impResult);
        } else if (eventTopic.equalsIgnoreCase(EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE)) {
            LOG.info("####### Receive event " + EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE);
            UserFeedbackPrivacyNegotiationEvent event = (UserFeedbackPrivacyNegotiationEvent) item;
            this.negotiationResults.put(event.getNegotiationDetails(), event.getResponsePolicy());
            synchronized (this.negotiationResults) {
                this.negotiationResults.notifyAll();
            }
        } else if (eventTopic.equalsIgnoreCase(EventTypes.UF_PRIVACY_ACCESS_CONTROL_RESPONSE)) {
            UserFeedbackAccessControlEvent event = (UserFeedbackAccessControlEvent) item;
            this.accessCtrlResults.put(event.getRequestId(), event.getResponseItems());
            synchronized (this.accessCtrlResults) {
                this.accessCtrlResults.notifyAll();
            }

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

    private void processExpResponseEvent(String responseID, List<String> result) {
        //remove from request manager list if exists
        requestMgr.removeRequest(responseID);
        //set result value in hashmap
        synchronized (expResults) {
            if (expResults.containsKey(responseID)) {
                LOG.debug("this is the node where the exp feedback request originated....adding result to expResults hashmap");
                this.expResults.put(responseID, result);
                this.expResults.notifyAll();
            } else {
                LOG.debug("This isn't the node where the exp feedback request originated...don't need to add result to expResults hashmap");
            }
        }
    }

    /*
     * Handle implicit feedback request and response events
     */
    private void processImpFeedbackRequestEvent(String requestId, int type, String proposalText, int timeout) {
        //create feedback form
        FeedbackForm fbForm = generateImpFeedbackForm(requestId, type, proposalText, timeout);
        //add new request to queue
        requestMgr.addRequest(fbForm);
    }

    private void processImpResponseEvent(String responseID, Boolean result) {
        //remove from request manager list if exists
        requestMgr.removeRequest(responseID);
        //set result value in hashmap
        synchronized (impResults) {
            if (impResults.containsKey(responseID)) {
                LOG.debug("this is the node where the imp feedback request originated....adding result to impResults hashmap");
                this.impResults.put(responseID, result);
                this.impResults.notifyAll();
            } else {
                LOG.debug("This isn't the node where the imp feedback request originated...don't need to add result to impResults hashmap");
            }
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
        } catch (XMPPError e1) {
            e1.printStackTrace();
        } catch (CommunicationException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void submitExplicitResponse(String requestId, NegotiationDetailsBean negotiationDetails, ResponsePolicy result) {
        //create user feedback response bean
        UserFeedbackPrivacyNegotiationEvent resultBean = new UserFeedbackPrivacyNegotiationEvent();
        resultBean.setMethod(FeedbackMethodType.GET_EXPLICIT_FB);
        resultBean.setType(ExpProposalType.PRIVACY_NEGOTIATION);
        resultBean.setRequestId(requestId);
        resultBean.setNegotiationDetails(negotiationDetails);
        resultBean.setResponsePolicy(result);

        //fire response pubsub event to all user agents
        try {
            LOG.info("####### Publish " + EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE + ": " + ResponseItemUtils.toXmlString(result.getResponseItems()));
            pubsub.publisherPublish(myCloudID, EventTypes.UF_PRIVACY_NEGOTIATION_RESPONSE, requestId, resultBean);
        } catch (XMPPError e1) {
            e1.printStackTrace();
        } catch (CommunicationException e1) {
            e1.printStackTrace();
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
            e.printStackTrace();
        } catch (CommunicationException e) {
            e.printStackTrace();
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
            LOG.error("Could not understand this type of explicit GUI: " + type);
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
            LOG.error("Could not understand this type of implicit GUI: " + type);
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
        LOG.debug("Request for explicit feedback received from remote User Agent");
        List<String> result;

        //show GUIs on local device
        LOG.debug("Returning explicit feedback to UACommsServer");
        String proposalText = content.getProposalText();
        String[] options = content.getOptions();
        if (type == ExpProposalType.RADIOLIST) {
            LOG.debug("Radio list GUI");
            RadioGUI gui = new RadioGUI();
            result = gui.displayGUI(proposalText, options);
        } else if (type == ExpProposalType.CHECKBOXLIST) {
            LOG.debug("Check box list GUI");
            CheckBoxGUI gui = new CheckBoxGUI();
            result = gui.displayGUI(proposalText, options);
        } else { //ACK-NACK
            LOG.debug("ACK/NACK GUI");
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
        LOG.debug("Request for implicit feedback received from remote User Agent");
        Boolean result = null;

        //show GUIs on local device
        LOG.debug("Returning implicit feedback to UACommsServer");
        String proposalText = content.getProposalText();
        int timeout = content.getTimeout();
        if (type == ImpProposalType.TIMED_ABORT) {
            LOG.debug("Timed Abort GUI");
            TimedGUI gui = new TimedGUI();
            result = gui.displayGUI(proposalText, timeout);
        }

        return new AsyncResult<Boolean>(result);
    }




	/*private String getCurrentUID(){
        String uid = "";
		try {
			List<CtxIdentifier> attrIDs = ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.UID).get();
			if(attrIDs.size()>0){  //found existing UID
				CtxAttribute uidAttr = (CtxAttribute)ctxBroker.retrieve(attrIDs.get(0)).get();
				uid = uidAttr.getStringValue();
			}else{  //no existing UID
				uid = UNDEFINED;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return uid;
	}*/

    public void setCtxBroker(ICtxBroker ctxBroker) {
        this.ctxBroker = ctxBroker;
    }

    public void setCommsMgr(ICommManager commsMgr) {
        this.commsMgr = commsMgr;
    }

    public void setPubsub(PubsubClient pubsub) {
        this.pubsub = pubsub;
    }

    public void setUaRemote(IUserAgentRemoteMgr uaRemote) {
        this.uaRemote = uaRemote;
    }

    public void setUserFeedbackHistoryRepository(IUserFeedbackHistoryRepository userFeedbackHistoryRepository) {
        this.userFeedbackHistoryRepository = userFeedbackHistoryRepository;
    }

    public void setPrivacyPolicyNegotiationHistoryRepository(IPrivacyPolicyNegotiationHistoryRepository privacyPolicyNegotiationHistoryRepository) {
        this.privacyPolicyNegotiationHistoryRepository = privacyPolicyNegotiationHistoryRepository;
    }


    private static boolean containsKey(Map<NegotiationDetailsBean, ResponsePolicy> negotiationResults, NegotiationDetailsBean details) {
        for (NegotiationDetailsBean next : negotiationResults.keySet()) {
            if (RequestorUtils.equals(next.getRequestor(), details.getRequestor())) {
                if (next.getNegotiationID() == details.getNegotiationID()) {
                    return true;
                }
            }
        }
        return false;

    }

}

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
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.feedback.IUserFeedbackResponseEventListener;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.FeedbackForm;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.societies.useragent.api.feedback.IInternalUserFeedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.*;
import java.util.concurrent.Future;

public class UserFeedback implements IUserFeedback, IInternalUserFeedback {

    private static final Logger log = LoggerFactory.getLogger(UserFeedback.class);


    @Autowired
    private ICommManager commsMgr;

    @Autowired
    private PubsubClient pubsub;


    private IIdentity myCloudID;


    public void initialiseUserFeedback() {
        log.debug("User Feedback initialising");


        //get cloud ID
        myCloudID = commsMgr.getIdManager().getThisNetworkNode();
        log.debug("Got my cloud ID: " + myCloudID);
    }


    public Future<List<String>> getExplicitFB(String requestId, int type, ExpProposalContent content) {

        return new AsyncResult<List<String>>(Arrays.asList(content.getOptions()));

    }

    @Override
    public Future<List<String>> getExplicitFB(int type, ExpProposalContent content) {
        return new AsyncResult<List<String>>(Arrays.asList(content.getOptions()));
    }

    @Override
    public Future<List<String>> getExplicitFBAsync(int type, ExpProposalContent content) {
        return new AsyncResult<List<String>>(new ArrayList<String>());
    }

    @Override
    public Future<List<String>> getExplicitFBAsync(int type, ExpProposalContent content, IUserFeedbackResponseEventListener<List<String>> callback) {
        return new AsyncResult<List<String>>(new ArrayList<String>());
    }

    @Override
    public Future<List<String>> getExplicitFBAsync(String requestId, int type, ExpProposalContent content, IUserFeedbackResponseEventListener<List<String>> callback) {
        ArrayList<String> results = new ArrayList<String>();
        Collections.addAll(results, content.getOptions());
        return new AsyncResult<List<String>>(results);
    }

    @Override
    public Future<Boolean> getImplicitFB(String requestId, int type, ImpProposalContent content) {
        return new AsyncResult<Boolean>(true);
    }


    @Override
    public Future<Boolean> getImplicitFB(int type, ImpProposalContent content) {
        return new AsyncResult<Boolean>(true);
    }

    @Override
    public Future<Boolean> getImplicitFBAsync(int type, ImpProposalContent content) {
        return new AsyncResult<Boolean>(true);
    }

    @Override
    public Future<Boolean> getImplicitFBAsync(int type, ImpProposalContent content, IUserFeedbackResponseEventListener<Boolean> callback) {
        return new AsyncResult<Boolean>(true);
    }

    @Override
    public Future<Boolean> getImplicitFBAsync(String requestId, int type, ImpProposalContent content, IUserFeedbackResponseEventListener<Boolean> callback) {
        return new AsyncResult<Boolean>(true);
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFB(String requestId, ResponsePolicy policy, NegotiationDetailsBean details) {
        return new AsyncResult<ResponsePolicy>(policy);
    }


    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFB(ResponsePolicy policy, NegotiationDetailsBean details) {
        return new AsyncResult<ResponsePolicy>(policy);
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(ResponsePolicy policy, NegotiationDetailsBean details) {
        return new AsyncResult<ResponsePolicy>(policy);
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(ResponsePolicy policy, NegotiationDetailsBean details, IUserFeedbackResponseEventListener<ResponsePolicy> callback) {
        return new AsyncResult<ResponsePolicy>(policy);
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(String requestId, ResponsePolicy policy, NegotiationDetailsBean details, IUserFeedbackResponseEventListener<ResponsePolicy> callback) {
        return new AsyncResult<ResponsePolicy>(policy);
    }

    @Override
    public Future<List<ResponseItem>> getAccessControlFB(String requestId, Requestor requestor, List<ResponseItem> items) {
        return new AsyncResult<List<ResponseItem>>(items);
    }


    @Override
    public Future<List<ResponseItem>> getAccessControlFB(Requestor requestor, List<ResponseItem> items) {
        return new AsyncResult<List<ResponseItem>>(items);
    }

    @Override
    public Future<List<ResponseItem>> getAccessControlFBAsync(Requestor requestor, List<ResponseItem> items) {
        return new AsyncResult<List<ResponseItem>>(items);
    }

    @Override
    public Future<List<ResponseItem>> getAccessControlFBAsync(Requestor requestor, List<ResponseItem> items, IUserFeedbackResponseEventListener<List<ResponseItem>> callback) {
        return new AsyncResult<List<ResponseItem>>(items);
    }

    @Override
    public Future<List<ResponseItem>> getAccessControlFBAsync(String requestId, Requestor requestor, List<ResponseItem> items, IUserFeedbackResponseEventListener<List<ResponseItem>> callback) {
        return new AsyncResult<List<ResponseItem>>(items);
    }


    @Override
    public void showNotification(String notificationTxt) {

    }


    /*
     * The following methods are called by the UserFeedbackController as part of the platform web-app
     *
     * (non-Javadoc)
     * @see org.societies.api.internal.useragent.feedback.IUserFeedback#getNextRequest()
     */
    @Override
    public FeedbackForm getNextRequest() {
        return new FeedbackForm();
    }

    @Override
    public void submitExplicitResponse(String requestId, List<String> result) {
    }

    @Override
    public void submitExplicitResponse(String requestId, NegotiationDetailsBean negotiationDetails, ResponsePolicy result) {

    }

    @Override
    public void submitImplicitResponse(String requestId, Boolean result) {

    }


    /*
     * Helper methods to generate feedback forms - explicit, implicit and notification
     */
    private static FeedbackForm generateExpFeedbackForm(String requestId, int type, String proposalText, List<String> optionsList) {
        return new FeedbackForm();
    }

    private static FeedbackForm generateImpFeedbackForm(String requestId, int type, String proposalText, int timeout) {
        return new FeedbackForm();
    }

    private static FeedbackForm generateNotificationForm(String requestId, String notificationTxt) {
        return new FeedbackForm();
    }



    public void setCommsMgr(ICommManager commsMgr) {
        this.commsMgr = commsMgr;
    }

    public void setPubsub(PubsubClient pubsub) {
        this.pubsub = pubsub;
    }



    /**
     * This is a non-api method which is used by integration tests to clear the internal state of the UF module
     */
    @Override
    public void clear() {
    }


    @Override
    public Future<List<String>> getExplicitFBforRemote(int arg0, ExpProposalContent arg1) {
        ArrayList<String> results = new ArrayList<String>();
        Collections.addAll(results, arg1.getOptions());
        return new AsyncResult<List<String>>(results);
    }

    @Override
    public Future<Boolean> getImplicitFBforRemote(int arg0, ImpProposalContent arg1) {
        return new AsyncResult<Boolean>(true);
    }

    @Override
    public List<UserFeedbackBean> listIncompleteFeedbackBeans() {
        return new ArrayList<UserFeedbackBean>();
    }

    @Override
    public List<UserFeedbackBean> listStoredFeedbackBeans(int arg0) {
        return new ArrayList<UserFeedbackBean>();
    }

    @Override
    public List<UserFeedbackBean> listStoredFeedbackBeans(Date arg0) {
        return new ArrayList<UserFeedbackBean>();
    }
}

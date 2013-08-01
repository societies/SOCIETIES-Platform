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

package org.societies.api.internal.useragent.feedback;

import org.societies.api.identity.Requestor;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.FeedbackForm;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author S.Gallacher@hw.ac.uk, p.skillen@hw.ac.uk
 */
public interface IUserFeedback {
    /**
     * Request explicit user feedback in a synchronous manner - i.e. the method will block, and the {@link Future} will
     * not be returned until the result has been returned from the user
     *
     * @param type {@link org.societies.api.internal.useragent.model.ExpProposalType}
     */
    public Future<List<String>> getExplicitFB(int type, ExpProposalContent content);

    /**
     * Request explicit user feedback in an asynchronous manner - i.e. a {@link Future} will be returned, and you must check
     * {@link java.util.concurrent.Future#isDone()} to see if the result has been returned
     *
     * @param type {@link org.societies.api.internal.useragent.model.ExpProposalType}
     */
    public Future<List<String>> getExplicitFBAsync(int type, ExpProposalContent content);

    /**
     * <p>Request explicit user feedback in an asynchronous manner - i.e. a {@link Future} will be returned, and you must check
     * {@link java.util.concurrent.Future#isDone()} to see if the result has been returned</p>
     * <p>You may also specify a callback to use which will be notified immediately when the result arrives</p>
     *
     * @param type {@link org.societies.api.internal.useragent.model.ExpProposalType}
     */
    public Future<List<String>> getExplicitFBAsync(int type, ExpProposalContent content, IUserFeedbackResponseEventListener<List<String>> callback);

    /**
     * Request implicit user feedback in a synchronous manner - i.e. the method will block, and the {@link Future} will
     * not be returned until the result has been returned from the user
     *
     * @param type {@link org.societies.api.internal.useragent.model.ImpProposalType}
     */
    public Future<Boolean> getImplicitFB(int type, ImpProposalContent content);

    /**
     * Request implicit user feedback in an asynchronous manner - i.e. a {@link Future} will be returned, and you must check
     * {@link java.util.concurrent.Future#isDone()} to see if the result has been returned
     *
     * @param type {@link org.societies.api.internal.useragent.model.ImpProposalType}
     */
    public Future<Boolean> getImplicitFBAsync(int type, ImpProposalContent content);

    /**
     * <p>Request implicit user feedback in an asynchronous manner - i.e. a {@link Future} will be returned, and you must check
     * {@link java.util.concurrent.Future#isDone()} to see if the result has been returned</p>
     * <p>You may also specify a callback to use which will be notified immediately when the result arrives</p>
     *
     * @param type {@link org.societies.api.internal.useragent.model.ImpProposalType}
     */
    public Future<Boolean> getImplicitFBAsync(int type, ImpProposalContent content, IUserFeedbackResponseEventListener<Boolean> callback);

    /**
     * Request a privacy negotiation in a synchronous manner - i.e. the method will block, and the {@link Future} will
     * not be returned until the result has been returned from the user
     */
    public Future<ResponsePolicy> getPrivacyNegotiationFB(ResponsePolicy policy, NegotiationDetailsBean details);

    /**
     * Request a privacy negotiation in an asynchronous manner - i.e. a {@link Future} will be returned, and you must check
     * {@link java.util.concurrent.Future#isDone()} to see if the result has been returned
     */
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(ResponsePolicy policy, NegotiationDetailsBean details);

    /**
     * <p>Request a privacy negotiation in an asynchronous manner - i.e. a {@link Future} will be returned, and you must check
     * {@link java.util.concurrent.Future#isDone()} to see if the result has been returned</p>
     * <p>You may also specify a callback to use which will be notified immediately when the result arrives</p>
     */
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(ResponsePolicy policy, NegotiationDetailsBean details, IUserFeedbackResponseEventListener<ResponsePolicy> callback);

    /**
     * Request access control in a synchronous manner - i.e. the method will block, and the {@link Future} will
     * not be returned until the result has been returned from the user
     */
    public Future<List<ResponseItem>> getAccessControlFB(Requestor requestor, List<ResponseItem> items);

    /**
     * Request access control in an asynchronous manner - i.e. a {@link Future} will be returned, and you must check
     * {@link java.util.concurrent.Future#isDone()} to see if the result has been returned
     */
    public Future<List<ResponseItem>> getAccessControlFBAsync(Requestor requestor, List<ResponseItem> items);

    /**
     * <p>Request access control in an asynchronous manner - i.e. a {@link Future} will be returned, and you must check
     * {@link java.util.concurrent.Future#isDone()} to see if the result has been returned</p>
     * <p>You may also specify a callback to use which will be notified immediately when the result arrives</p>
     */
    public Future<List<ResponseItem>> getAccessControlFBAsync(Requestor requestor, List<ResponseItem> items, IUserFeedbackResponseEventListener<List<ResponseItem>> callback);

    public void showNotification(String notificationText);

    public FeedbackForm getNextRequest();

    public void submitExplicitResponse(String id, List<String> result);

    /**
     * Submit an explicit response for privacy negotiation userfeedback request type
     *
     * @param requestId Id of the userfeedback request
     */
    public void submitExplicitResponse(String requestId, NegotiationDetailsBean negotiationDetails, ResponsePolicy result);

    public void submitImplicitResponse(String id, Boolean result);

    void clear();
}

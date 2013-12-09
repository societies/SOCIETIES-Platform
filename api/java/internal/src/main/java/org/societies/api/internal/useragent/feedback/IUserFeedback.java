/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
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
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.AccessControlResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author S.Gallacher@hw.ac.uk, p.skillen@hw.ac.uk
 */
public interface IUserFeedback {

    /**
     * <p>Request explicit user feedback (Yes/No, Select One, Select Many, Simple Alert Message).</p>
     * <p>This is a blocking method (i.e. it will not be return until the result has been returned from the user). You
     * may retrieve the result using {@link java.util.concurrent.Future#get()}</p>
     *
     * @param requestId A manually generated, unique, ID string to be assigned to this request. (See
     *                  {@link java.util.UUID#randomUUID()})
     * @param type      The type of user feedback request. May be one of {@link org.societies.api.internal.useragent.model.ExpProposalType}
     * @param content   The contents of this request (including the message to be shown to the user, etc)
     */
    public Future<List<String>> getExplicitFB(String requestId, int type, ExpProposalContent content);

    /**
     * <p>Request explicit user feedback (Yes/No, Select One, Select Many, Simple Alert Message).</p>
     * <p>This is a blocking method (i.e. it will not be return until the result has been returned from the user). You
     * may retrieve the result using {@link java.util.concurrent.Future#get()}</p>
     *
     * @param type    {@link org.societies.api.internal.useragent.model.ExpProposalType}
     * @param content The contents of this request (including the message to be shown to the user, etc)
     */
    public Future<List<String>> getExplicitFB(int type, ExpProposalContent content);

    /**
     * <p>Request explicit user feedback (Yes/No, Select One, Select Many, Simple Alert Message).</p>
     * <p>This is a non-blocking method. The method will immediately return the {@link Future} object, which will not be
     * populated until the user has responded. You can use the {@link java.util.concurrent.Future#isDone()} method to
     * check whether the response is complete or not, and retrieve the result using
     * {@link java.util.concurrent.Future#get()}.</p>
     *
     * @param type    The type of user feedback request. May be one of {@link org.societies.api.internal.useragent.model.ExpProposalType}
     * @param content The contents of this request (including the message to be shown to the user, etc)
     */
    public Future<List<String>> getExplicitFBAsync(int type, ExpProposalContent content);

    /**
     * <p>Request explicit user feedback (Yes/No, Select One, Select Many, Simple Alert Message).</p>
     * <p>This is a non-blocking method. The method will immediately return the {@link Future} object, which will not be
     * populated until the user has responded. You can use the {@link java.util.concurrent.Future#isDone()} method to
     * check whether the response is complete or not, and retrieve the result using
     * {@link java.util.concurrent.Future#get()}.</p>
     *
     * @param type     The type of user feedback request. May be one of {@link org.societies.api.internal.useragent.model.ExpProposalType}
     * @param content  The contents of this request (including the message to be shown to the user, etc)
     * @param callback A callback which is notified whenever the user has responded.
     */
    public Future<List<String>> getExplicitFBAsync(int type, ExpProposalContent content, IUserFeedbackResponseEventListener<List<String>> callback);

    /**
     * <p>Request explicit user feedback (Yes/No, Select One, Select Many, Simple Alert Message).</p>
     * <p>This is a non-blocking method. The method will immediately return the {@link Future} object, which will not be
     * populated until the user has responded. You can use the {@link java.util.concurrent.Future#isDone()} method to
     * check whether the response is complete or not, and retrieve the result using
     * {@link java.util.concurrent.Future#get()}.</p>
     *
     * @param requestId A manually generated, unique, ID string to be assigned to this request. (See
     *                  {@link java.util.UUID#randomUUID()})
     * @param type      The type of user feedback request. May be one of {@link org.societies.api.internal.useragent.model.ExpProposalType}
     * @param content   The contents of this request (including the message to be shown to the user, etc)
     * @param callback  A callback which is notified whenever the user has responded.
     */
    public Future<List<String>> getExplicitFBAsync(String requestId, int type, ExpProposalContent content, IUserFeedbackResponseEventListener<List<String>> callback);


    /**
     * <p>Request implicit user feedback (currently only Timed Abort).</p>
     * <p>This is a blocking method (i.e. it will not be return until the result has been returned from the user). You
     * may retrieve the result using {@link java.util.concurrent.Future#get()}</p>
     *
     * @param requestId A manually generated, unique, ID string to be assigned to this request. (See
     *                  {@link java.util.UUID#randomUUID()})
     * @param type      The type of user feedback request. May be one of {@link org.societies.api.internal.useragent.model.ImpProposalType}
     * @param content   The contents of this request (including the message to be shown to the user, timeout in seconds, etc)
     */
    public Future<Boolean> getImplicitFB(String requestId, int type, ImpProposalContent content);

    /**
     * <p>Request implicit user feedback (currently only Timed Abort).</p>
     * <p>This is a blocking method (i.e. it will not be return until the result has been returned from the user). You
     * may retrieve the result using {@link java.util.concurrent.Future#get()}</p>
     *
     * @param type    The type of user feedback request. May be one of {@link org.societies.api.internal.useragent.model.ImpProposalType}
     * @param content The contents of this request (including the message to be shown to the user, timeout in seconds, etc)
     */
    public Future<Boolean> getImplicitFB(int type, ImpProposalContent content);

    /**
     * <p>Request implicit user feedback (currently only Timed Abort).</p>
     * <p>This is a non-blocking method. The method will immediately return the {@link Future} object, which will not be
     * populated until the user has responded. You can use the {@link java.util.concurrent.Future#isDone()} method to
     * check whether the response is complete or not, and retrieve the result using
     * {@link java.util.concurrent.Future#get()}.</p>
     *
     * @param type    The type of user feedback request. May be one of {@link org.societies.api.internal.useragent.model.ImpProposalType}
     * @param content The contents of this request (including the message to be shown to the user, timeout in seconds, etc)
     */
    public Future<Boolean> getImplicitFBAsync(int type, ImpProposalContent content);

    /**
     * <p>Request implicit user feedback (currently only Timed Abort).</p>
     * <p>This is a non-blocking method. The method will immediately return the {@link Future} object, which will not be
     * populated until the user has responded. You can use the {@link java.util.concurrent.Future#isDone()} method to
     * check whether the response is complete or not, and retrieve the result using
     * {@link java.util.concurrent.Future#get()}.</p>
     *
     * @param type     The type of user feedback request. May be one of {@link org.societies.api.internal.useragent.model.ImpProposalType}
     * @param content  The contents of this request (including the message to be shown to the user, timeout in seconds, etc)
     * @param callback A callback which is notified whenever the user has responded.
     */
    public Future<Boolean> getImplicitFBAsync(int type, ImpProposalContent content, IUserFeedbackResponseEventListener<Boolean> callback);

    /**
     * <p>Request implicit user feedback (currently only Timed Abort).</p>
     * <p>This is a non-blocking method. The method will immediately return the {@link Future} object, which will not be
     * populated until the user has responded. You can use the {@link java.util.concurrent.Future#isDone()} method to
     * check whether the response is complete or not, and retrieve the result using
     * {@link java.util.concurrent.Future#get()}.</p>
     *
     * @param requestId A manually generated, unique, ID string to be assigned to this request. (See
     *                  {@link java.util.UUID#randomUUID()})
     * @param type      The type of user feedback request. May be one of {@link org.societies.api.internal.useragent.model.ImpProposalType}
     * @param content   The contents of this request (including the message to be shown to the user, timeout in seconds, etc)
     * @param callback  A callback which is notified whenever the user has responded.
     */
    public Future<Boolean> getImplicitFBAsync(String requestId, int type, ImpProposalContent content, IUserFeedbackResponseEventListener<Boolean> callback);


    /**
     * <p>Send a privacy policy to the user for review.</p>
     * <p>This is a blocking method (i.e. it will not be return until the result has been returned from the user). You
     * may retrieve the result using {@link java.util.concurrent.Future#get()}</p>
     *
     * @param requestId A manually generated, unique, ID string to be assigned to this request. (See
     *                  {@link java.util.UUID#randomUUID()})
     * @param policy    The specifics of the privacy policy.
     * @param details   Meta-information attached to this request.
     */
    public Future<ResponsePolicy> getPrivacyNegotiationFB(String requestId, ResponsePolicy policy, NegotiationDetailsBean details);

    /**
     * <p>Send a privacy policy to the user for review.</p>
     * <p>This is a blocking method (i.e. it will not be return until the result has been returned from the user). You
     * may retrieve the result using {@link java.util.concurrent.Future#get()}</p>
     *
     * @param policy  The specifics of the privacy policy.
     * @param details Meta-information attached to this request.
     */
    public Future<ResponsePolicy> getPrivacyNegotiationFB(ResponsePolicy policy, NegotiationDetailsBean details);

    /**
     * <p>Send a privacy policy to the user for review.</p>
     * <p>This is a non-blocking method. The method will immediately return the {@link Future} object, which will not be
     * populated until the user has responded. You can use the {@link java.util.concurrent.Future#isDone()} method to
     * check whether the response is complete or not, and retrieve the result using
     * {@link java.util.concurrent.Future#get()}.</p>
     *
     * @param policy  The specifics of the privacy policy.
     * @param details Meta-information attached to this request.
     */
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(ResponsePolicy policy, NegotiationDetailsBean details);

    /**
     * <p>Send a privacy policy to the user for review.</p>
     * <p>This is a non-blocking method. The method will immediately return the {@link Future} object, which will not be
     * populated until the user has responded. You can use the {@link java.util.concurrent.Future#isDone()} method to
     * check whether the response is complete or not, and retrieve the result using
     * {@link java.util.concurrent.Future#get()}.</p>
     *
     * @param policy   The specifics of the privacy policy.
     * @param details  Meta-information attached to this request.
     * @param callback A callback which is notified whenever the user has responded.
     */
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(ResponsePolicy policy, NegotiationDetailsBean details, IUserFeedbackResponseEventListener<ResponsePolicy> callback);

    /**
     * <p>Send a privacy policy to the user for review.</p>
     * <p>This is a non-blocking method. The method will immediately return the {@link Future} object, which will not be
     * populated until the user has responded. You can use the {@link java.util.concurrent.Future#isDone()} method to
     * check whether the response is complete or not, and retrieve the result using
     * {@link java.util.concurrent.Future#get()}.</p>
     *
     * @param requestId A manually generated, unique, ID string to be assigned to this request. (See
     *                  {@link java.util.UUID#randomUUID()})
     * @param policy    The specifics of the privacy policy.
     * @param details   Meta-information attached to this request.
     * @param callback  A callback which is notified whenever the user has responded.
     */
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(String requestId, ResponsePolicy policy, NegotiationDetailsBean details, IUserFeedbackResponseEventListener<ResponsePolicy> callback);


    /**
     * <p>Send an access control request to the user for review.</p>
     * <p>This is a blocking method (i.e. it will not be return until the result has been returned from the user). You
     * may retrieve the result using {@link java.util.concurrent.Future#get()}</p>
     *
     * @param requestId A manually generated, unique, ID string to be assigned to this request. (See
     *                  {@link java.util.UUID#randomUUID()})
     * @param requestor Information on the source of this request
     * @param items     Specifics of the information types being requested from the user
     */
    public Future<List<AccessControlResponseItem>> getAccessControlFB(String requestId, Requestor requestor, List<AccessControlResponseItem> items);

    /**
     * <p>Send an access control request to the user for review.</p>
     * <p>This is a blocking method (i.e. it will not be return until the result has been returned from the user). You
     * may retrieve the result using {@link java.util.concurrent.Future#get()}</p>
     *
     * @param requestor Information on the source of this request
     * @param items     Specifics of the information types being requested from the user
     */
    public Future<List<AccessControlResponseItem>> getAccessControlFB(Requestor requestor, List<AccessControlResponseItem> items);

    /**
     * <p>Send an access control request to the user for review.</p>
     * <p>This is a non-blocking method. The method will immediately return the {@link Future} object, which will not be
     * populated until the user has responded. You can use the {@link java.util.concurrent.Future#isDone()} method to
     * check whether the response is complete or not, and retrieve the result using
     * {@link java.util.concurrent.Future#get()}.</p>
     *
     * @param requestor Information on the source of this request
     * @param items     Specifics of the information types being requested from the user
     */
    public Future<List<AccessControlResponseItem>> getAccessControlFBAsync(Requestor requestor, List<AccessControlResponseItem> items);

    /**
     * <p>Send an access control request to the user for review.</p>
     * <p>This is a non-blocking method. The method will immediately return the {@link Future} object, which will not be
     * populated until the user has responded. You can use the {@link java.util.concurrent.Future#isDone()} method to
     * check whether the response is complete or not, and retrieve the result using
     * {@link java.util.concurrent.Future#get()}.</p>
     *
     * @param requestor Information on the source of this request
     * @param items     Specifics of the information types being requested from the user
     * @param callback  A callback which is notified whenever the user has responded.
     */
    public Future<List<AccessControlResponseItem>> getAccessControlFBAsync(Requestor requestor, List<AccessControlResponseItem> items, IUserFeedbackResponseEventListener<List<AccessControlResponseItem>> callback);

    /**
     * <p>Send an access control request to the user for review.</p>
     * <p>This is a non-blocking method. The method will immediately return the {@link Future} object, which will not be
     * populated until the user has responded. You can use the {@link java.util.concurrent.Future#isDone()} method to
     * check whether the response is complete or not, and retrieve the result using
     * {@link java.util.concurrent.Future#get()}.</p>
     *
     * @param requestId A manually generated, unique, ID string to be assigned to this request. (See
     *                  {@link java.util.UUID#randomUUID()})
     * @param requestor Information on the source of this request
     * @param items     Specifics of the information types being requested from the user
     * @param callback  A callback which is notified whenever the user has responded.
     */
    public Future<List<AccessControlResponseItem>> getAccessControlFBAsync(String requestId, Requestor requestor, List<AccessControlResponseItem> items, IUserFeedbackResponseEventListener<List<AccessControlResponseItem>> callback);


    /**
     * <p>Send a Simple Alert Message to the user.</p>
     * <p>This is a non-blocking method and will not return a result. It is expected that the notification will be delivered to the user's
     * device(s), but no response is sent when the user views and subsequently acknowledges the message</p>
     *
     * @param notificationText The text to be displayed in the message popup
     */
    public void showNotification(String notificationText);

    /**
     * ????
     *
     * @return ????
     */
    public FeedbackForm getNextRequest();

    /**
     * <p>Used by the client to respond to an explicit feedback request</p>
     *
     * @param id     The unique ID of the request to which you are responding
     * @param result The user's responses (if Select One, the list will contain only the selected answer; if Select Many, the list will contain all selected answers; etc)
     */
    public void submitExplicitResponse(String id, List<String> result);

    /**
     * <p>Used by the client to respond to an implicit feedback request</p>
     *
     * @param id     The unique ID of the request to which you are responding
     * @param result True if the user has accepted the rest (or ignored it until the timeout time), false if the user has explicitly selected "abort"
     */
    public void submitImplicitResponse(String id, Boolean result);

    /**
     * <p>Used by the client to respond to an privacy negotiation request</p>
     *
     * @param requestId The unique ID of the request to which you are responding
     */
    public void submitPrivacyNegotiationResponse(String requestId, NegotiationDetailsBean negotiationDetails, ResponsePolicy result);

    /**
     * <p>Used by the client to respond to an access control request</p>
     *
     * @param requestId The unique ID of the request to which you are responding
     */
    public void submitAccessControlResponse(String requestId, List<AccessControlResponseItem> responseItems, RequestorBean requestorBean);

    /**
     * <p>Clears the internal state of the UserFeedback component</p>
     * <p>This method should usually only be used for <u>debugging and testing</u></p>
     */
    public void clear();
}
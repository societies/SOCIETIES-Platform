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

package org.societies.useragent.api.feedback;

import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.useragent.model.FeedbackForm;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.AccessControlResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import java.util.List;
import java.util.concurrent.Future;


public interface IInternalUserFeedback {

	public Future<List<String>> getExplicitFBforRemote(int type, ExpProposalContent content);

	public Future<Boolean> getImplicitFBforRemote(int type, ImpProposalContent content);

	List<UserFeedbackBean> listIncompleteFeedbackBeans();

	List<UserFeedbackPrivacyNegotiationEvent> listIncompletePrivacyRequests();

	List<UserFeedbackAccessControlEvent> listIncompleteAccessRequests();

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

}

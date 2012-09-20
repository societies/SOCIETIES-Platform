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
package org.societies.privacytrust.privacyprotection.api.remote;

//TODO : temporary mock package import to solve missing package in API folder

import java.util.concurrent.Future;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.IAgreementEnvelope;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 18:55:01
 */
public interface INegotiationAgentRemote {

	/**
	 * this method is called by the client and asks the provider to acknowledge the
	 * agreement document.
	 * @return				true if acknowledged, false if not acknowledged.
	 * 
	 * @param contract    the agreement to acknowledge
	 */
	public Future<Boolean> acknowledgeAgreement(IAgreementEnvelope contract);

	/**
	 * this method is called by any CSS that wants to read the  RequestPolicy of a service or CIS
	 * 
	 * @return				the policy of the service provider in the format of RequestPolicy
	 * 
	 * @param requestor    the details of the service provider or CIS administrator
	 *
	 */
	public Future<RequestPolicy> getPolicy(Requestor requestor);

	/**
	 * This method is called by any CSS to get the Identity of the service provider or CIS administrator.
	 * This is needed to do trust evaluation and evaluation of privacy preferences
	 * where applicable
	 * @return				the identity of the service provider
	 */
	public Future<IIdentity> getProviderIdentity();

	/**
	 * this method is called by the client and informs the provider that it wants to
	 * initiate a negotiation process in order to start a service or join a CIS and provides its
	 * policy which is a response to the provider's advertised privacy policy. this
	 * method can be called a number of times until the ResponsePolicy.getStatus
	 * method returns SUCCESSFUL or FAILED status.
	 * @return 				the ResponsePolicy to the client's privacy policy.
	 * 
	 * @param requestor    	the details of the service provider or CIS administrator
	 * @param policy   		the ResponsePolicy to the provider's privacy policy
	 */
	public Future<ResponsePolicy> negotiate(Requestor requestor, ResponsePolicy policy);

}
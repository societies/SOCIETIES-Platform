/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.privacytrust.privacyprotection.api.external;


import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.IAgreementEnvelope;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.RequestPolicy;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.ResponsePolicy;
/**
 * This interface defines the methods that should be implemented by the CSS that
 * acts as a client in the Negotiation process. This means that this CSS is the
 * one that initiates the starting of the service or requests to join a CIS.
 * @author Elizabeth
 * @version 1.0
 * @created 11-Nov-2011 17:03:12
 */
public interface INegotiationClient {

	/**
	 * 
	 * @param serviceID
	 * @param providerIdentity
	 * @param envelope
	 * @param b
	 */
	public void acknowledgeAgreement(ServiceResourceIdentifier serviceID, EntityIdentifier providerIdentity, IAgreementEnvelope envelope, boolean b);

	/**
	 * 
	 * @param policy
	 */
	public void receiveNegotiationResponse(ResponsePolicy policy);

	/**
	 * 
	 * @param dpi
	 */
	public void receiveProviderIdentity(EntityIdentifier dpi);

	/**
	 * 
	 * @param policy
	 */
	public void receiveProviderPolicy(RequestPolicy policy);

	/**
	 * 
	 * @param policy
	 * @param serviceIdentifier
	 * @param serviceIdentity
	 */
	public void startPrivacyPolicyNegotiation(RequestPolicy policy, ServiceResourceIdentifier serviceIdentifier, EntityIdentifier serviceIdentity);

}
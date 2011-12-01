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

package org.societies.security.policynegotiator.api;

import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponsePolicy;

/**
 * Interface for invoking the requester.
 * To be used by other components on same node.
 * 
 * @author Mitja Vardjan
 *
 */
public interface INegotiationRequester {

	/**
	 * Get all available options for the policy.
	 * 
	 * @param callback The callback to be invoked to return the result.
	 * 
	 * @return All available options embedded in a single XML document.
	 */
	public void getPolicyOptions(INegotiationRequesterCallback callback);

	/**
	 * Accept given policy option unchanged, as provided by the provider side.
	 * Alternatively, {@link negotiatePolicy(ResponsePolicy)} can be used
	 * to try to negotiate a different policy if none of the options are
	 * acceptable.
	 * 
	 * @param signedPolicyOption The selected policy alternative, accepted and
	 * signed by the requester side. Includes requester identity and signature.
	 */
	public void acceptPolicy(String signedPolicyOption,
			INegotiationRequesterCallback callback);
	
	/**
	 * Further negotiate given policy option. If any of the policy options
	 * given by the provider suits the requester, then {@link acceptPolicy(String)}
	 * should be used instead in order to save bandwidth and increase chances
	 * of successful negotiation.
	 * 
	 * @param policyOptionId ID of the option the requester side chose as a
	 * basis for further negotiation.
	 * 
	 * @param modifiedPolicy Policy modified by requester side. The policy is
	 * to be offered to the provider. It does not include the requester
	 * identity nor signature (TBC). 
	 */
	public void negotiatePolicy(int policyOptionId, ResponsePolicy modifiedPolicy,
			INegotiationRequesterCallback callback);
	
	/**
	 * Reject all options and terminate negotiation.
	 */
	public void reject();

}

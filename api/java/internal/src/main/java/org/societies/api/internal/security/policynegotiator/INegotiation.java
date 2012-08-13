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
package org.societies.api.internal.security.policynegotiator;

import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyNegotiationManager;

/**
 * High-level interface for invoking the secure policy negotiator.
 * Includes high-level interactions with the requester side of policy negotiator.
 * To be used by other components (Service Marketplace ?) locally, from same node.
 *
 * @author Mitja Vardjan
 *
 */
public interface INegotiation {

	/**
	 * Start policy negotiation procedure.
	 * 
	 * @param provider Includes identity of the service provider and service ID comprehendable by the provider.
	 * This should be an instance of either:<br/>
	 * - {@link RequestorCis} if this negotiation is about joining a CIS, or<br/>
	 * - {@link RequestorService} if this negotiation is about using a new service
	 * 
	 * @param includePrivacyPolicyNegotiation True to perform also Privacy Policy Negotiation using
	 * {@link IPrivacyPolicyNegotiationManager}.
	 * 
	 * @param callback The callback to be invoked to receive the result of this method
	 */
	public void startNegotiation(Requestor provider, boolean includePrivacyPolicyNegotiation,
			INegotiationCallback callback);

	/**
	 * Start policy negotiation procedure.
	 * Same as calling
	 * {@link #startNegotiation(Requestor, boolean, INegotiationCallback)}
	 * with parameter set to true.
	 * 
	 * @param provider Includes identity of the service provider and service ID comprehendable by the provider.
	 * This should be an instance of either:<br/>
	 * - {@link RequestorCis} if this negotiation is about joining a CIS, or<br/>
	 * - {@link RequestorService} if this negotiation is about using a new service
	 * 
	 * @param callback The callback to be invoked to receive the result of this method
	 */
	public void startNegotiation(Requestor provider, INegotiationCallback callback);
}

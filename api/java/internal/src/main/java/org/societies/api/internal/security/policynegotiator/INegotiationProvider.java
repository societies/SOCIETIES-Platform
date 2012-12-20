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

import java.util.concurrent.Future;

import org.societies.api.internal.schema.security.policynegotiator.NegotiationType;
import org.societies.api.internal.schema.security.policynegotiator.SlaBean;

/**
 * Interface for invoking the provider.
 * To be used by Security Group Comms Manager locally (on same node) in server mode.
 * 
 * @author Mitja Vardjan
 *
 */
public interface INegotiationProvider {

	/**
	 * Get all available options for the policy.
	 * 
	 * @param serviceId ID of the service or CIS
	 * @param type type of negotiation
	 * 
	 * @return All available options embedded in a single XML document.
	 */
	public Future<SlaBean> getPolicyOptions(String serviceId, NegotiationType type);

	/**
	 * Accept given policy option and get the final legal agreement signed by
	 * both parties.
	 * 
	 * @param sessionId ID of this session
	 * 
	 * @param signedPolicyOption The selected policy alternative, accepted and
	 * signed by the requester side. Includes requester identity and signature.
	 * 
	 * @param modified True if policy option has been changed during the
	 * negotiation process. False if policy is as provided by the provider side.
	 * 
	 * @return The final legal agreement signed by both parties.
	 */
	public Future<SlaBean> acceptPolicyAndGetSla(int sessionId, String signedPolicyOption,
			boolean modified);
	
	/**
	 * Reject all options and terminate negotiation.
	 * 
	 * @param sessionId ID of this session
	 * 
	 * @return success status in {@link SlaBean#isSuccess()}
	 */
	public Future<SlaBean> reject(int sessionId);
}

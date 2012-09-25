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



import java.util.concurrent.Future;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


/**
 * This interface is for use during the communication. It is not to be implemented or consumed (defined in a bean)
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 18:55:01
 */
public interface INegotiationAgent {

	/**
	 * this method is called by the client and asks the provider to acknowledge the
	 * agreement document.
	 * 
	 * 
	 * @param contract    the agreement to acknowledge
	 */
	public Future<Boolean> acknowledgeAgreement(byte[] agreementEnvelope);

	/**
	 * this method is called by any CSS that wants to read the service's provider
	 * RequestPolicy for a specific service it provides
	 * @return				the policy of the service provider in the format of RequestPolicy
	 * 
	 * @param serviceID    the service identifier of the service for which the
	 * negotiation will be performed
	 */
	public Future<byte[]> getPolicy(ServiceResourceIdentifier serviceID);

	/**
	 * This method is called by any CSS to get the Identity of the service provider.
	 * This is needed to do trust evaluation and evaluation of privacy preferences
	 * where applicable
	 * 
	 */
	public Future<String> getProviderIdentity();

	/**
	 * this method is called by the client and informs the provider that it wants to
	 * initiate a negotiation process for the specified serviceID and provides its
	 * policy which is a response to the provider's advertised privacy policy. this
	 * method can be called a number of times until the ResponsePolicy.getStatus
	 * method returns SUCCESSFUL or FAILED status.
	 * 
	 * 
	 * @param serviceID    the service identifier for which the negotiation is going
	 * to be performed
	 * @param policy    the ResponsePolicy to the provider's privacy policy
	 */
	public Future<byte[]> negotiate(ServiceResourceIdentifier serviceID, byte[] responsePolicy);

}
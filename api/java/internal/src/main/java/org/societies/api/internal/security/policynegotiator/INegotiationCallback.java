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

import java.net.URI;
import java.util.List;

import org.societies.api.identity.Requestor;
import org.societies.api.internal.security.storage.ISecureStorage;

/**
 * Callback for {@link INegotiation}
 *
 * @author Mitja Vardjan
 *
 */
public interface INegotiationCallback {
	
	/**
	 * Async return for
	 * {@link INegotiation#startNegotiation(Requestor, INegotiationCallback)}
	 * 
	 * @param agreementKey The key to get Service Level Agreement (SLA) from
	 * {@link ISecureStorage}. If negotiation has not been successful, this
	 * parameter is null.
	 * 
	 * @param fileUris Locations of any files associated with the service, e.g.,
	 * the client jar in case of a service that provides a client,
	 * any videos, images and other resources that the service may want to
	 * download during runtime.
	 * The URIs are in same order as when passed to
	 * {@link INegotiationProviderServiceMgmt#addService(
	 * org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier,
	 * String, URI, List, INegotiationProviderSLMCallback)}.
	 * The parameter should be null or empty if not applicable, e.g.,
	 * in case of a CIS negotiation, or
	 * in case of a service that does not provide a client, nor have any resource files.
	 */
	public void onNegotiationComplete(String agreementKey, List<URI> fileUris);

	/**
	 * Async return for
	 * {@link INegotiation#startNegotiation(Requestor, INegotiationCallback)}
	 * in case of error.
	 * 
	 * @param msg Error message
	 */
	public void onNegotiationError(String msg);
}

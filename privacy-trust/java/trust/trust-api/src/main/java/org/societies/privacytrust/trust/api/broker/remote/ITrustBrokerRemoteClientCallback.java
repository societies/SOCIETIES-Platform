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
package org.societies.privacytrust.trust.api.broker.remote;

import java.util.Set;

import org.societies.api.internal.privacytrust.trust.model.ExtTrustRelationship;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.model.TrustRelationship;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
public interface ITrustBrokerRemoteClientCallback {
	
	/**
	 * 
	 * @param trustRelationships
	 * @since 1.0
	 */
	public void onRetrievedTrustRelationships(Set<TrustRelationship> trustRelationships);
	
	/**
	 * 
	 * @param extTrustRelationships
	 * @since 1.2
	 */
	public void onRetrievedExtTrustRelationships(Set<ExtTrustRelationship> extTrustRelationships);
	
	/**
	 * 
	 * @param trustRelationship
	 * @since 1.0
	 */
	public void onRetrievedTrustRelationship(TrustRelationship trustRelationship);
	
	/**
	 * 
	 * @param trustRelationship
	 * @since 1.2
	 */
	public void onRetrievedExtTrustRelationship(ExtTrustRelationship extTrustRelationship);
	
	/**
	 * 
	 * @param trustValue
	 * @since 1.0
	 */
	public void onRetrievedTrustValue(Double trustValue);
	
	/**
	 * Sets the result of a trust query identifying trust relationships to be
	 * removed. The result is <code>true</code> if any trust relationships
	 * matched the specified query.
	 * 
	 * @param result
	 *            <code>true</code> if any trust relationships matched the
	 *            specified query; <code>false</code> otherwise. 
	 * @since 1.2
	 */
	public void onRemovedTrustRelationships(boolean result);
	
	/**
	 * Associates an exception with this callback.
	 * 
	 * @param exception the exception to associate with this callback.
	 * @since 1.0
	 */
	public void onException(TrustException exception);
}
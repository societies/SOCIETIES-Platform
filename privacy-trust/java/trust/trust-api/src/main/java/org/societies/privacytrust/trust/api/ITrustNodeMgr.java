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
package org.societies.privacytrust.trust.api;

import java.util.Collection;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4.1
 */
public interface ITrustNodeMgr {
	
	/**
	 * Returns the collection of {@link TrustedEntityId TrustedEntityIds} on
	 * whose behalf this node operates. Note that the method returns an
	 * <i>unmodifiable</i> view of this collection.
	 *  
	 * @return the collection of {@link TrustedEntityId TrustedEntityIds} on
	 *         whose behalf this node operates.
	 * @throws TrustedEntityIdMgrException
	 *         if the collection of {@link TrustedEntityId TrustedEntityIds} on
	 *         whose behalf this node operates cannot be retrieved.
	 */
	public Collection<TrustedEntityId> getMyIds();
	
	/**
	 * Returns the local identity on whose behalf this node operates.
	 * 
	 * @return the local identity on whose behalf this node operates.
	 * @since 1.0
	 */
	public IIdentity getLocalIdentity();
	
	/**
	 * Returns the local requestor on whose behalf this node operates.
	 * 
	 * @return the local requestor on whose behalf this node operates.
	 * @since 1.0
	 */
	public Requestor getLocalRequestor();
	
	/**
	 * Returns <code>true</code> if this node is master; <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this node is master; <code>false</code> otherwise.
	 * @since 0.5
	 */
	public boolean isMaster();
	
	/**
	 * Returns the {@link IIdentity} instance of the specified 
	 * {@link TrustedEntityId}. 
	 * 
	 * @param teid 
	 *            the {@link TrustedEntityId} from which to create the
	 *            {@link IIdentity} instance.
	 * @return the {@link IIdentity} instance of the specified 
	 *         {@link TrustedEntityId}.
	 * @throws NullPointerException if the specified teid is <code>null</code>.
	 * @throws IllegalArgumentException if the specified teid is invalid.
	 * @since 1.0
	 */
	public IIdentity fromId(final TrustedEntityId teid);
}
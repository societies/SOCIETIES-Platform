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
package org.societies.api.internal.privacytrust.trust.evidence.remote;

import java.io.Serializable;
import java.util.Date;

import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.3
 */
public interface ITrustEvidenceCollectorRemote {
	
	/**
	 * Adds the specified piece of direct trust evidence. The
	 * {@link TrustedEntityId} this evidence refers to, its type, as well as,
	 * the time the evidence was recorded are also supplied. Finally, depending
	 * on the evidence type, the method allows specifying supplementary
	 * information.
	 *  
	 * @param teid
	 *            the {@link TrustedEntityId} the evidence refers to
	 * @param type
	 *            the type of the evidence to be added
	 * @param timestamp
	 *            the time the evidence was recorded
	 * @param info
	 *            supplementary information if applicable; <code>null</code>
	 *            otherwise
	 * @param callback
	 *            the callback to acknowledge the addition of the direct trust
	 *            evidence
	 * @throws TrustException
	 *            if the specified piece of direct trust evidence cannot be 
	 *            added
	 * @throws NullPointerException
	 *            if any of the teid, type or timestamp parameter is
	 *            <code>null</code>
	 */
	public void addDirectEvidence(final TrustedEntityId teid, 
			final TrustEvidenceType type, final Date timestamp,
			final Serializable info, 
			final ITrustEvidenceCollectorRemoteCallback callback) 
					throws TrustException;
	
	/**
	 * Adds the specified piece of indirect trust evidence which originates
	 * from the given source. The {@link TrustedEntityId} this evidence refers
	 * to, its type, as well as, the time the evidence was recorded are also
	 * supplied. Finally, depending on the evidence type, the method allows
	 * specifying supplementary information. 
	 *  
	 * @param source
	 *            the source this evidence originates from
	 * @param teid
	 *            the {@link TrustedEntityId} this evidence refers to
	 * @param type
	 *            the type of the evidence to be added
	 * @param timestamp
	 *            the time the evidence was recorded
	 * @param info
	 *            supplementary information if applicable; <code>null</code>
	 *            otherwise
	 * @param callback
	 *            the callback to acknowledge the addition of the indirect 
	 *            trust evidence
	 * @throws TrustException
	 *            if the specified piece of indirect trust evidence cannot be 
	 *            added
	 * @throws NullPointerException
	 *            if any of the source, teid, type or timestamp parameter is
	 *            <code>null</code>
	 */
	public void addIndirectEvidence(final String source, 
			final TrustedEntityId teid,	final TrustEvidenceType type,
			final Date timestamp, final Serializable info,
			final ITrustEvidenceCollectorRemoteCallback callback)
					throws TrustException;
}
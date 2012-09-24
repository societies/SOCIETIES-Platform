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
package org.societies.api.internal.privacytrust.trust.evidence;

import java.io.Serializable;
import java.util.Date;

import org.societies.api.identity.IIdentity;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.2
 */
public interface ITrustEvidenceCollector {
	
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
	 * @throws TrustException
	 *            if the specified piece of direct trust evidence cannot be 
	 *            added
	 * @throws NullPointerException
	 *            if any of the teid, type or timestamp parameter is
	 *            <code>null</code>
	 * @since 0.3
	 */
	public void addDirectEvidence(final TrustedEntityId teid, final TrustEvidenceType type,
			final Date timestamp, final Serializable info) throws TrustException;
	
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
	 * @throws TrustException
	 *            if the specified piece of indirect trust evidence cannot be 
	 *            added
	 * @throws NullPointerException
	 *            if any of the source, teid, type or timestamp parameter is
	 *            <code>null</code>
	 * @since 0.3
	 */
	public void addIndirectEvidence(final String source, final TrustedEntityId teid,
			final TrustEvidenceType type, final Date timestamp, final Serializable info)
					throws TrustException;
	
	/**
	 * Assigns the specified trust rating to the identified trustee by the
	 * supplied trustor. The identified trustee can be either a CSS or a CIS,
	 * while the trustor must reference a CSS. The trust rating value should be
	 * in the range of [0,1].
	 * 
	 * @param trustor 
	 *            the CSS that assigns the trust rating
	 * @param trustee
	 *            the CSS or CIS to assign the rating to
	 * @param rating
	 *            the trust rating [0,1]
	 * @param timestamp
	 *            the timestamp of the given rating, or the current time if
	 *            a <code>null</code> value is specified
	 * @throws TrustException
	 *            if the operation fails
	 * @throws NullPointerException
	 *            if any of the trustor or trustee parameters are
	 *            <code>null</code>
	 * @throws IllegalArgumentException
	 *            if the trustor does not identify a CSS; the trustee does not
	 *            identify a CSS or CIS; the trust rating is not in the range
	 *            of [0,1] 
	 */
	public void addTrustRating(final IIdentity trustor, final IIdentity trustee,
			final double rating, final Date timestamp) throws TrustException;

	/**
	 * Assigns the specified trust rating to the identified trustee by the
	 * supplied trustor. The identified trustee is a service, while the trustor
	 * must reference a CSS. The trust rating value should be in the range of
	 * [0,1].
	 * 
	 * @param trustor 
	 *            the CSS that assigns the trust rating
	 * @param trustee
	 *            the service to assign the rating to
	 * @param rating
	 *            the trust rating [0,1]
	 * @param timestamp
	 *            the timestamp of the given rating, or the current time if
	 *            a <code>null</code> value is specified
	 * @throws TrustException
	 *            if the operation fails
	 * @throws NullPointerException
	 *            if any of the trustor or trustee parameters are
	 *            <code>null</code>
	 * @throws IllegalArgumentException
	 *            if the trustor does not identify a CSS or the trust rating is
	 *            not in the range of [0,1] 
	 */
	public void addTrustRating(final IIdentity trustor, 
			final ServiceResourceIdentifier trustee, final double rating,
			final Date timestamp) throws TrustException;
}
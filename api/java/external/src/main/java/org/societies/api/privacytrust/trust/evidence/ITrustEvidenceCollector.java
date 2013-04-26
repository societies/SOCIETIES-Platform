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
package org.societies.api.privacytrust.trust.evidence;

import java.io.Serializable;
import java.util.Date;

import org.societies.api.identity.Requestor;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;

/**
 * This interface is used to add direct and indirect trust evidence with 
 * regards to CSSs, CISs or services. Trust evidence data are the basis for
 * evaluating the {@link TrustValueType#DIRECT direct} and
 * {@link TrustValueType#INDIRECT indirect} trust in these entities. 
 * <p>
 * More specifically, each piece of trust evidence is associated with a 
 * <i>subject</i> and an <i>object</i>, as well as, the 
 * {@link TrustEvidenceType type} characterising that particular piece of
 * evidence. A user (subject) joining/leaving a community (object), using a
 * service (object), interacting with another individual (object) are some
 * examples of trust evidence. Direct trust evidence are collected locally
 * with respect to the CSS owner, while indirect trust originate from other
 * sources. Thus, indirect trust evidence are also accompanied with the
 * <i>source</i> from which they originate.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
public interface ITrustEvidenceCollector {
	
	/**
	 * Adds the specified piece of direct trust evidence. The
	 * {@link TrustedEntityId TrustedEntityIds} of the subject and the object
	 * this piece of evidence refers to, its type, as well as, the time the
	 * evidence was recorded must be supplied. Finally, depending on the
	 * evidence type, the method allows specifying supplementary information.
	 *  
	 * @param requestor
	 *            (required) the {@link Requestor} on whose behalf to add this 
	 *            piece of direct trust evidence.
	 * @param subjectId
	 *            (required) the {@link TrustedEntityId} of the subject this 
	 *            piece of direct trust evidence refers to.
	 * @param objectId
	 *            (required) the {@link TrustedEntityId} of the object this
	 *            piece of direct trust evidence refers to.
	 * @param type
	 *            (required) the type of the piece of direct trust evidence to
	 *            be added.
	 * @param timestamp
	 *            (required) the time this piece of direct trust evidence was
	 *            recorded.
	 * @param info
	 *            (optional) supplementary information if applicable; 
	 *            <code>null</code> otherwise.
	 * @throws TrustException
	 *            if the specified piece of direct trust evidence cannot be
	 *            added.
	 * @throws NullPointerException
	 *            if any of the required parameters is <code>null</code>.
	 * @since 1.1
	 */
	public void addDirectEvidence(final Requestor requestor,
			final TrustedEntityId subjectId, final TrustedEntityId objectId,
			final TrustEvidenceType type, final Date timestamp,
			final Serializable info) throws TrustException;
	
	/**
	 * Adds the specified piece of indirect trust evidence which originates
	 * from the given source. The {@link TrustedEntityId TrustedEntityIds} of
	 * the subject and the object this piece of evidence refers to, its type,
	 * as well as, the time the evidence was recorded are also supplied. 
	 * Finally, depending on the evidence type, the method allows specifying
	 * supplementary information.
	 *  
	 * @param requestor
	 *            (required) the {@link Requestor} on whose behalf to add this 
	 *            piece of indirect trust evidence.
	 * @param subjectId
	 *            (required) the {@link TrustedEntityId} of the subject this 
	 *            piece of indirect trust evidence refers to.
	 * @param objectId
	 *            (required) the {@link TrustedEntityId} of the object this
	 *            piece of indirect trust evidence refers to.
	 * @param type
	 *            (required) the type of the piece of indirect trust evidence
	 *            to be added.
	 * @param timestamp
	 *            (required) the time this piece of indirect trust evidence was
	 *            recorded.
	 * @param info
	 *            (optional) supplementary information if applicable; 
	 *            <code>null</code> otherwise.
	 * @param sourceId
	 *            (required) the {@link TrustedEntityId} of the source this
	 *            piece of indirect trust evidence originates from.
	 * @throws TrustException
	 *            if the specified piece of trust evidence cannot be added.
	 * @throws NullPointerException
	 *            if any of the required parameters is <code>null</code>.
	 * @since 1.1
	 */
	public void addIndirectEvidence(final Requestor requestor,
			final TrustedEntityId subjectId, final TrustedEntityId objectId,
			final TrustEvidenceType type, final Date timestamp,
			final Serializable info, final TrustedEntityId sourceId)
					throws TrustException;
	
	/**
	 * @since 0.5
	 * @deprecated As of 1.1, use 
	 * {@link #addDirectEvidence(Requestor, TrustedEntityId, TrustedEntityId, TrustEvidenceType, Date, Serializable)}.
	 */
	@Deprecated
	public void addDirectEvidence(final TrustedEntityId subjectId, 
			final TrustedEntityId objectId, final TrustEvidenceType type,
			final Date timestamp, final Serializable info) throws TrustException;
	
	/**
	 * @since 0.5
	 * @deprecated As of 1.1, use 
	 * {@link #addIndirectEvidence(TrustedEntityId, TrustedEntityId, TrustEvidenceType, Date, Serializable, TrustedEntityId)}.
	 */
	public void addIndirectEvidence(final TrustedEntityId subjectId, 
			final TrustedEntityId objectId, final TrustEvidenceType type, 
			final Date timestamp, final Serializable info, 
			final TrustedEntityId sourceId)	throws TrustException;
}
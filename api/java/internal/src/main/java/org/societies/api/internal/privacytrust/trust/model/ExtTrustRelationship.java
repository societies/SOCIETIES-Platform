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
package org.societies.api.internal.privacytrust.trust.model;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.societies.api.privacytrust.trust.model.TrustEvidence;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;

/**
 * This class extends {@link TrustRelationship} by including related 
 * {@link TrustEvidence} information.   
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.1
 */
public final class ExtTrustRelationship extends TrustRelationship {
	
	private static final long serialVersionUID = -8456128362369917075L;
	
	/** The trust evidence that were used to evaluate this trust relationship.*/
	private final Set<TrustEvidence> trustEvidence;
	
	/**
	 * Constructs a <code>ExtTrustRelationship</code> with the specified 
	 * attributes.
	 * 
	 * @param trustorId
	 *            (required) the {@link TrustedEntityIdentifier} of the entity
	 *            (trustor) that assigned the trust value.
	 * @param trusteeId
	 *            (required) the {@link TrustedEntityIdentifier} of the entity
	 *            (trustee) that the trust value was assigned to.
	 * @param trustValueType
	 *            (required) the {@link TrustValueType type} of the trust 
	 *            value.
	 * @param trustValue
	 *            (required) the trust value expressing the trustworthiness of
	 *            the identified trustee.
	 * @param timestamp
	 *            (required) the date and time when the trust value was last
	 *            evaluated.
	 * @param trustEvidence
	 *            (required) the {@link TrustEvidence} that were used to
	 *            evaluate this trust relationship.
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>
	 * @throws IllegalArgumentException if the specified trustValue is out of 
	 *         range [0,1]
	 */
	public ExtTrustRelationship(final TrustedEntityId trustorId, 
			final TrustedEntityId trusteeId, 
			final TrustValueType trustValueType, final Double trustValue,
			final Date timestamp, final Set<TrustEvidence> trustEvidence) {
		
		super(trustorId, trusteeId, trustValueType, trustValue, timestamp);
		this.trustEvidence = trustEvidence;
	}
	
	/**
	 * Returns an <i>unmodifiable</i> set containing the {@link TrustEvidence}
	 * that resulted in this trust relationship.
	 * 
	 * @return a set containing the {@link TrustEvidence} that resulted in this
	 *         trust relationship.
	 */
	public Set<TrustEvidence> getTrustEvidence() {
		
		return Collections.unmodifiableSet(this.trustEvidence);
	}
	
	/**
	 * Returns a <code>String</code> representation of this trust relationship.
	 * 
	 * @return a <code>String</code> representation of this trust relationship.
	 */
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("ExtTrustRelationship (trustorId=");
		sb.append(super.getTrustorId());
		sb.append(", trusteeId=");
		sb.append(super.getTrusteeId());
		sb.append(", trustValueType=");
		sb.append(super.getTrustValueType());
		sb.append(", trustValue=");
		sb.append(super.getTrustValue());
		sb.append(", timestamp=");
		sb.append(super.getTimestamp());
		sb.append(", trustEvidence=");
		sb.append(this.trustEvidence);
		sb.append(")");
		
		return sb.toString();
	}

	/*
	 * @see org.societies.api.privacytrust.trust.model.TrustRelationship#hashCode()
	 */
	@Override
	public int hashCode() {
		
		final int prime = 31;
		
		int result = super.hashCode();
		result = prime * result
				+ ((this.trustEvidence == null) ? 0 : this.trustEvidence.hashCode());
		return result;
	}

	/*
	 * @see org.societies.api.privacytrust.trust.model.TrustRelationship#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object that) {
		
		if (this == that)
			return true;
		if (!super.equals(that))
			return false;
		if (this.getClass() != that.getClass())
			return false;
		
		ExtTrustRelationship other = (ExtTrustRelationship) that;
		if (this.trustEvidence == null) {
			if (other.trustEvidence != null)
				return false;
		} else if (!this.trustEvidence.equals(other.trustEvidence))
			return false;
		
		return true;
	}
}
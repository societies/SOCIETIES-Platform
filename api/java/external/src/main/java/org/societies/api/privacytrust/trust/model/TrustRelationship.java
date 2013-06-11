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
package org.societies.api.privacytrust.trust.model;

import java.io.Serializable;
import java.util.Date;

/**
 * This class is used to represent the trust relationship between a trustor and
 * a trustee. More specifically, a trustor is an entity which assigns a trust 
 * value to another entity, i.e. the trustee, in order to express the 
 * trustworthiness of that entity. Such a trust value ranges from <i>0</i>
 * to <i>1</i>, where zero expresses full distrust, while one denotes full 
 * trust. There are three different types of values that can be assigned:
 * {@link TrustValueType#DIRECT DIRECT}, 
 * {@link TrustValueType#INDIRECT INDIRECT}, or 
 * {@link TrustValueType#USER_PERCEIVED USER_PERCEIVED}. A 
 * <code>TrustRelationship</code> also contains the date and time (timestamp)
 * when the trust value was last evaluated.   
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.0
 */
public class TrustRelationship implements Serializable {

	private static final long serialVersionUID = -1925436871890516849L;

	private final TrustedEntityId trustorId;
	
	private final TrustedEntityId trusteeId;
	
	private final TrustValueType trustValueType;
	
	private final Double trustValue;
	
	private final Date timestamp;
	
	/**
	 * Constructs a <code>TrustRelationship</code> with the specified 
	 * attributes.
	 * 
	 * @param trustorId
	 *            (required) the {@link TrustedEntityId} of the entity
	 *            (trustor) that assigned the trust value.
	 * @param trusteeId
	 *            (required) the {@link TrustedEntityId} of the entity
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
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>
	 * @throws IllegalArgumentException if the specified trustValue is out of 
	 *         range [0,1]
	 */
	public TrustRelationship(final TrustedEntityId trustorId, 
			final TrustedEntityId trusteeId, 
			final TrustValueType trustValueType, final Double trustValue,
			final Date timestamp) {
		
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		if (trustValue == null)
			throw new NullPointerException("trustValue can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		if (trustValue < 0.0d || trustValue > 1.0d)
			throw new IllegalArgumentException("trustValue is out of range [0,1]");
		
		this.trustorId = trustorId;
		this.trusteeId = trusteeId;
		this.trustValueType = trustValueType;
		this.trustValue = trustValue;
		this.timestamp = timestamp;
	}

	/**
	 * Returns the {@link TrustedEntityId} of the entity (trustor) that
	 * assigned the trust value.
	 * 
	 * @return the {@link TrustedEntityId} of the entity (trustor) that
	 *         assigned the trust value.
	 */
	public TrustedEntityId getTrustorId() {
		
		return this.trustorId;
	}

	/**
	 * Returns the {@link TrustedEntityId} of the entity (trustee) that
	 * the trust value was assigned to.
	 * 
	 * @return the {@link TrustedEntityId} of the entity (trustee) that
	 *         the trust value was assigned to.
	 */
	public TrustedEntityId getTrusteeId() {
		
		return this.trusteeId;
	}

	/**
	 * Returns the {@link TrustValueType type} of the trust value.
	 *  
	 * @return the {@link TrustValueType type} of the trust value.
	 */
	public TrustValueType getTrustValueType() {
		
		return this.trustValueType;
	}

	/**
	 * Returns the trust value expressing the trustworthiness of the identified
	 * trustee.
	 *  
	 * @return the trust value expressing the trustworthiness of the identified
	 *         trustee.
	 */
	public Double getTrustValue() {
		
		return this.trustValue;
	}

	/**
	 * Returns the date and time when the trust value was last evaluated.
	 * 
	 * @return the date and time when the trust value was last evaluated.
	 */
	public Date getTimestamp() {
		
		return this.timestamp;
	}
	
	/**
	 * Returns a <code>String</code> representation of this trust relationship.
	 * 
	 * @return a <code>String</code> representation of this trust relationship.
	 */
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("TrustRelationship (trustorId=");
		sb.append(this.trustorId);
		sb.append(", trusteeId=");
		sb.append(this.trusteeId);
		sb.append(", trustValueType=");
		sb.append(this.trustValueType);
		sb.append(", trustValue=");
		sb.append(this.trustValue);
		sb.append(", timestamp=");
		sb.append(this.timestamp);
		sb.append(")");
		
		return sb.toString();
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		final int prime = 31;
		
		int result = 1;
		result = prime * result
				+ ((this.trustorId == null) ? 0 : this.trustorId.hashCode());
		result = prime * result
				+ ((this.trusteeId == null) ? 0 : this.trusteeId.hashCode());
		result = prime * result
				+ ((this.trustValueType == null) ? 0 : this.trustValueType.hashCode());
		result = prime * result
				+ ((this.timestamp == null) ? 0 : this.timestamp.hashCode());
		return result;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object that) {
		
		if (this == that)
			return true;
		if (that == null)
			return false;
		if (this.getClass() != that.getClass())
			return false;
		
		TrustRelationship other = (TrustRelationship) that;
		if (this.trustorId == null) {
			if (other.trustorId != null)
				return false;
		} else if (!this.trustorId.equals(other.trustorId))
			return false;
		if (this.trusteeId == null) {
			if (other.trusteeId != null)
				return false;
		} else if (!this.trusteeId.equals(other.trusteeId))
			return false;
		if (this.trustValueType != other.trustValueType)
			return false;
		if (this.timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!this.timestamp.equals(other.timestamp))
			return false;
		
		return true;
	}
}
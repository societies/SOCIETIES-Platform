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
package org.societies.webapp.models;

import java.io.Serializable;

import org.societies.api.privacytrust.trust.model.TrustedEntityId;

/**
 * This class is used to represent an entity trusted by the trustor,
 * i.e. the owner of a CSS. Each trusted entity is associated with the
 * {@link TrustedEntityId} of the referenced trustor and trustee, while the
 * {@link Trust} objects express the trustworthiness of that particular
 * trustee, i.e. direct, indirect and user-perceived trust.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.1 
 */
public class TrustedEntity implements Serializable {

	private static final long serialVersionUID = -577316081483500668L;

	/** The identifier of the trustor. */
	private final TrustedEntityId trustorId;
	
	/** The identifier of the trustee. */
	private final TrustedEntityId trusteeId;
	
	/** The name of the trustee. */
	private final String trusteeName;
	
	/** The direct trust in this entity. */
	private final Trust directTrust = new Trust();
	
	/** The indirect trust in this entity. */
	private final Trust indirectTrust = new Trust();
	
	/** The user-perceived trust in this entity. */
	private final Trust userPerceivedTrust = new Trust();
	
	private Integer rating = 0;
	
	/**
	 * Constructs a <code>TrustedEntity</code> with the specified trustor and 
	 * trustee identifiers.
	 * 
	 * @param trusteeId
	 * @param trustorId
	 */
	public TrustedEntity(final TrustedEntityId trustorId, final TrustedEntityId trusteeId,
			final String trusteeName) {
		
		this.trustorId = trustorId;
		this.trusteeId = trusteeId;
		this.trusteeName = trusteeName;
	}
	
	public TrustedEntityId getTrustorId() {
		
		return this.trustorId;
	}
	
	public TrustedEntityId getTrusteeId() {
		
		return this.trusteeId;
	}
	
	public String getTrusteeName() {
		
		return this.trusteeName;
	}
	
	public Trust getDirectTrust() {
		
		return this.directTrust;
	}
	
	public Trust getIndirectTrust() {
		
		return this.indirectTrust;
	}
	
	public Trust getUserPerceivedTrust() {
		
		return this.userPerceivedTrust;
	}
	
	public Integer getRating() {
		
		return this.rating;
	}
	
	public void setRating(Integer rating) {
		
		this.rating = rating;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		
		sb.append("(");
		sb.append("trustorId=" + this.trustorId);
		sb.append(",");
		sb.append("trusteeId=" + this.trusteeId);
		sb.append(",");
		sb.append("directTrust=" + this.directTrust);
		sb.append(",");
		sb.append("indirectTrust=" + this.indirectTrust);
		sb.append(",");
		sb.append("userPerceivedTrust=" + this.userPerceivedTrust);
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
		
		final TrustedEntity other = (TrustedEntity) that;
		if (this.trustorId == null) {
			if (other.trustorId != null) {
				return false;
			}
		} else if (!this.trustorId.equals(other.trustorId)) {
			return false;
		}
		if (this.trusteeId == null) {
			if (other.trusteeId != null) {
				return false;
			}
		} else if (!this.trusteeId.equals(other.trusteeId)) {
			return false;
		}
		
		return true;
	}
}
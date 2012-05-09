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
package org.societies.privacytrust.trust.impl.evidence.repo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
@MappedSuperclass
public abstract class TrustEvidence implements ITrustEvidence {

	private static final long serialVersionUID = 5024340898894704006L;
	
	/* The surrogate key used by Hibernate. */
	@Id
	@GeneratedValue
	@Column(name = "id")
	@SuppressWarnings("unused")
	private long id;
	
	/** The identifier of the trusted entity this evidence refers to. */
	@Columns(columns={
			@Column(name = "trustorId", nullable = false, updatable = false, length = 256),
			@Column(name = "entityType", nullable = false, updatable = false, length = 3),
			@Column(name = "trusteeId", nullable = false, updatable = false, length = 256)
	})
	@Type(type="org.societies.privacytrust.trust.impl.evidence.repo.model.hibernate.TrustedEntityIdUserType")
	private final TrustedEntityId teid;
	
	@Column(name = "timestamp", nullable = false, updatable = false)
	@Type(type="org.societies.privacytrust.trust.impl.common.hibernate.DateTimeUserType")
	private final Date timestamp;
	
	TrustEvidence(final TrustedEntityId teid, final Date timestamp) {
		
		this.teid = teid;
		this.timestamp = timestamp;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence#getTeid()
	 */
	@Override
	public TrustedEntityId getTeid() {
		
		return this.teid;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence#getTimestamp()
	 */
	@Override
	public Date getTimestamp() {
		
		return this.timestamp;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		final int prime = 31;
		
		int result = 1;
		result = prime * result + ((this.teid == null) ? 0 : this.teid.hashCode());
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
		
		TrustEvidence other = (TrustEvidence) that;
		
		if (this.teid == null) {
			if (other.teid != null)
				return false;
		} else if (!this.teid.equals(other.teid))
			return false;
		if (this.timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!this.timestamp.equals(other.timestamp))
			return false;
		
		return true;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("teid=" + this.teid);
		sb.append(",");
		sb.append("timestamp=" + this.timestamp);
		sb.append("}");
		
		return sb.toString();
	}
}
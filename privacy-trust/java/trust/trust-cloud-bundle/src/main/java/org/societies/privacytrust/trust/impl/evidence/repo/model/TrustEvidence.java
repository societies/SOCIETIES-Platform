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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
@Entity
@org.hibernate.annotations.Entity(
		dynamicInsert=true
)
@Table(
		name = TableName.TRUST_EVIDENCE
)
@Immutable
public class TrustEvidence implements ITrustEvidence {

	private static final long serialVersionUID = 3874421819907517767L;

	/* The surrogate key used by Hibernate. */
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Index(name = "subject_idx")
	@Column(name = "subject_id", nullable = false, updatable = false, length = 255)
	@Type(type = "org.societies.privacytrust.trust.impl.common.hibernate.TrustedEntityIdUserType")
	private TrustedEntityId subjectId;
	
	@Index(name = "object_idx")
	@Column(name = "object_id", nullable = false, updatable = false, length = 255)
	@Type(type = "org.societies.privacytrust.trust.impl.common.hibernate.TrustedEntityIdUserType")
	private TrustedEntityId objectId;
	
	@Index(name = "type_idx")
	@Column(name = "type", nullable = false, updatable = false)
	private TrustEvidenceType type;
	
	@Column(name = "timestamp", nullable = false, updatable = false)
	@Type(type = "org.societies.privacytrust.trust.impl.common.hibernate.DateTimeUserType")
	private Date timestamp;
	
	@Column(name = "info", length = 1023, nullable = true, updatable = false)
	private Serializable info;
	
	@Index(name = "source_idx")
	@Column(name = "source_id", nullable = true, updatable = false, length = 255)
	@Type(type = "org.societies.privacytrust.trust.impl.common.hibernate.TrustedEntityIdUserType")
	private TrustedEntityId sourceId;
	
	/* Empty constructor required by Hibernate */
	@SuppressWarnings("unused")
	private TrustEvidence() {}
	
	/**
	 * Constructs a <code>TrustEvidence</code> object with the specified 
	 * attributes.
	 * 
	 * @param subjectId
	 *            (required) the {@link TrustedEntityId} of the subject
	 *            this piece of trust evidence refers to.
	 * @param objectId
	 *            (required) the {@link TrustedEntityId} of the object
	 *            this piece of trust evidence refers to.
	 * @param type
	 *            (required) the {@link TrustEvidenceType type} of this piece
	 *            of trust evidence.
	 * @param timestamp
	 *            (required) the date and time when this piece of trust 
	 *            evidence was collected.
	 * @param info
	 *            (optional) supplementary information if applicable; otherwise
	 *            <code>null</code>.
	 * @param sourceId
	 *            (optional) if specified, identifies the source of this piece
	 *            of indirect trust evidence; otherwise <code>null</code> to
	 *            denote a piece of direct trust evidence.           
	 * @throws NullPointerException if any of the required parameters is
	 *         <code>null</code>
	 * @throws IllegalArgumentException if the specified info is an out of 
	 *         range Double, i.e. not within [0,1].
	 */
	public TrustEvidence(final TrustedEntityId subjectId, 
			final TrustedEntityId objectId, 
			final TrustEvidenceType type, final Date timestamp,
			final Serializable info, final TrustedEntityId sourceId) {
		
		if (subjectId == null)
			throw new NullPointerException("subjectId can't be null");
		if (objectId == null)
			throw new NullPointerException("objectId can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		if (info instanceof Double && ((Double) info < 0.0d || (Double) info > 1.0d))
			throw new IllegalArgumentException("info (rating) is out of range [0,1]");
		
		this.subjectId = subjectId;
		this.objectId = objectId;
		this.type = type;
		this.timestamp = timestamp;
		this.info = info;
		this.sourceId = sourceId;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence#getSubjectId()
	 */
	@Override
	public TrustedEntityId getSubjectId() {
		
		return this.subjectId;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence#getObjectId()
	 */
	@Override
	public TrustedEntityId getObjectId() {
		
		return this.objectId;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence#getType()
	 */
	@Override
	public TrustEvidenceType getType() {
		
		return this.type;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence#getTimestamp()
	 */
	@Override
	public Date getTimestamp() {
		
		return this.timestamp;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence#getInfo()
	 */
	@Override
	public Serializable getInfo() {
		
		return this.info;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence#getSourceId()
	 */
	@Override
	public TrustedEntityId getSourceId() {
		
		return this.sourceId;
	}
	
	/*
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ITrustEvidence that) {
		
		if (that == null)
			throw new NullPointerException("can't compare with null");
		
		if (this == that)
			return 0;
		
		int comparison = this.timestamp.compareTo(that.getTimestamp());
		if (comparison != 0)
			return comparison;
		
		comparison = this.subjectId.getEntityId().compareTo(that.getSubjectId().getEntityId());
		if (comparison != 0)
			return comparison;
		
		comparison = this.objectId.getEntityId().compareTo(that.getObjectId().getEntityId());
		if (comparison != 0)
			return comparison;
		
		comparison = this.type.compareTo(that.getType());
		if (comparison != 0)
			return comparison;
		
		if (this.info != null) {
			if (that.getInfo() == null) { // this.info != null && that.info == null
				return +1; // empty before evidence with info
			} else { // this.info != null && that.info != null
				if (this.info.hashCode() < that.getInfo().hashCode())
					return -1;
				else if (this.info.hashCode() > that.getInfo().hashCode())
					return +1;
			}
		} else { // this.info == null
			if (that.getInfo() == null) // this.info == null && that.info == null
				return 0;
			else // this.info == null && that.info != null
				return -1; // empty before evidence with info
		}
		
		if (this.sourceId != null) {
			if (that.getSourceId() == null) // this.sourceId != null && that.sourceId == null
				return +1; // direct before indirect evidence
			else // this.sourceId != null && that.sourceId != null
				return this.sourceId.getEntityId().compareTo(that.getSourceId().getEntityId());
		} else { // sourceId == null
			if (that.getSourceId() == null) // this.sourceId == null && that.sourceId == null
				return 0;
			else // this.sourceId == null && that.sourceId != null
				return -1; // direct before indirect evidence
		}
	}
	
	/**
	 * Returns a <code>String</code> representation of this piece of trust
	 * evidence.
	 * 
	 * @return a <code>String</code> representation of this piece of trust
	 *         evidence.
	 */
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("TrustEvidence (subjectId=");
		sb.append(this.subjectId);
		sb.append(", objectId=");
		sb.append(this.objectId);
		sb.append(", type=");
		sb.append(this.type);
		sb.append(", timestamp=");
		sb.append(this.timestamp);
		sb.append(", info=");
		sb.append(this.info);
		sb.append(", sourceId=");
		sb.append(this.sourceId);
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
				+ ((this.subjectId == null) ? 0 : this.subjectId.hashCode());
		result = prime * result
				+ ((this.objectId == null) ? 0 : this.objectId.hashCode());
		result = prime * result
				+ ((this.type == null) ? 0 : this.type.hashCode());
		result = prime * result
				+ ((this.timestamp == null) ? 0 : this.timestamp.hashCode());
		result = prime * result
				+ ((this.info == null) ? 0 : this.info.hashCode());
		result = prime * result
				+ ((this.sourceId == null) ? 0 : this.sourceId.hashCode());
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
		if (this.subjectId == null) {
			if (other.subjectId != null)
				return false;
		} else if (!this.subjectId.equals(other.subjectId))
			return false;
		if (this.objectId == null) {
			if (other.objectId != null)
				return false;
		} else if (!this.objectId.equals(other.objectId))
			return false;
		if (this.type != other.type)
			return false;
		if (this.timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!this.timestamp.equals(other.timestamp))
			return false;
		if (this.info == null) {
			if (other.info != null)
				return false;
		} else if (!this.info.equals(other.info))
			return false;
		if (this.sourceId == null) {
			if (other.sourceId != null)
				return false;
		} else if (!this.sourceId.equals(other.sourceId))
			return false;
		
		return true;
	}
}
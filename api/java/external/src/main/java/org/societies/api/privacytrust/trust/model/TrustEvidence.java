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

import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;

/**
 * This class is used to represent direct and indirect trust evidence with 
 * regards to CSSs, CISs or services. Trust evidence data are the basis for
 * evaluating the {@link TrustValueType#DIRECT direct} and
 * {@link TrustValueType#INDIRECT indirect} trust in these entities. 
 * <p>
 * More specifically, each piece of trust evidence is associated with a 
 * <i>subject</i> and an <i>object</i>, as well as, the 
 * {@link TrustEvidenceType type} characterising that particular piece of
 * evidence. Depending on the type, supplementary <i>information</i> can also 
 * be specified. Some examples of trust evidence follow: 
 * <table>
 * <tr><th>subject</th> <th>object</th> <th>type</th> <th>info</th></tr>
 * <tr><td>CSS</td> <td>CSS/CIS/Service</td> <td>{@link TrustEvidenceType#RATED RATED}</td><td><code>Double</code> [0,1]</td></tr>
 * <tr><td>CSS</td> <td>CSS</td> <td>{@link TrustEvidenceType#FRIENDED_USER FRIENDED_USER}</td> <td><code>null</code></td></tr>
 * <tr><td>CSS</td> <td>CSS</td> <td>{@link TrustEvidenceType#UNFRIENDED_USER UNFRIENDED_USER}</td> <td><code>null</code></td></tr> 
 * <tr><td>CSS</td> <td>CIS</td> <td>{@link TrustEvidenceType#JOINED_COMMUNITY JOINED_COMMUNITY}</td> <td><code>null</code></td></tr>
 * <tr><td>CSS</td> <td>CIS</td> <td>{@link TrustEvidenceType#LEFT_COMMUNITY LEFT_COMMUNITY}</td> <td><code>null</code></td></tr> 
 * <tr><td>CSS</td> <td>Service</td> <td>{@link TrustEvidenceType#USED_SERVICE USED_SERVICE}</td> <td><code>Double</code> [0,1] (optional)</td></tr>
 * </table>
 * <p>
 * Direct trust evidence are collected locally with respect to the CSS owner,
 * while indirect trust evidence originate from other sources. Thus, indirect 
 * trust evidence are also accompanied with the <i>source</i> from which they 
 * originate.   
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.1
 */
public class TrustEvidence implements Serializable, Comparable<TrustEvidence> {

	private static final long serialVersionUID = 3081731373916156074L;

	/** The subject this piece of trust evidence refers to. */
	private final TrustedEntityId subjectId;
	
	/** The object this piece of trust evidence refers to. */
	private final TrustedEntityId objectId;
	
	/** The type of this piece of trust evidence. */
	private final TrustEvidenceType type;
	
	/** The date and time when this piece of trust evidence was collected. */
	private final Date timestamp;
	
	/** Supplementary information if applicable */
	private final Serializable info;
	
	/** The source of this piece of (indirect) trust evidence. */
	private final TrustedEntityId sourceId;
	
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

	/**
	 * Returns the {@link TrustedEntityId} of the subject this piece of
	 * trust evidence refers to.
	 * 
	 * @return the {@link TrustedEntityId} of the subject this piece of
	 *         trust evidence refers to.
	 */
	public TrustedEntityId getSubjectId() {
		
		return this.subjectId;
	}

	/**
	 * Returns the {@link TrustedEntityId} of the object this piece of
	 * trust evidence refers to.
	 * 
	 * @return the {@link TrustedEntityId} of the object this piece of
	 *         trust evidence refers to.
	 */
	public TrustedEntityId getObjectId() {
		
		return this.objectId;
	}

	/**
	 * Returns the {@link TrustEvidenceType type} of this piece of trust
	 * evidence.
	 *  
	 * @return the {@link TrustEvidenceType type} of this piece of trust
	 *         evidence.
	 */
	public TrustEvidenceType getType() {
		
		return this.type;
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
	 * Returns the trust value expressing the trustworthiness of the identified
	 * trustee.
	 *  
	 * @return the trust value expressing the trustworthiness of the identified
	 *         trustee.
	 */
	public Serializable getInfo() {
		
		return this.info;
	}
	
	/**
	 * Returns the {@link TrustedEntityId} of the source this piece of
	 * trust evidence originates from.
	 * 
	 * @return the {@link TrustedEntityId} of the source this piece of
	 *         trust evidence originates from.
	 */
	public TrustedEntityId getSourceId() {
		
		return this.sourceId;
	}
	
	/*
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TrustEvidence that) {
		
		if (that == null)
			throw new NullPointerException("can't compare with null");
		
		if (this == that)
			return 0;
		
		int comparison = this.timestamp.compareTo(that.timestamp);
		if (comparison != 0)
			return comparison;
		
		comparison = this.subjectId.getEntityId().compareTo(that.subjectId.getEntityId());
		if (comparison != 0)
			return comparison;
		
		comparison = this.objectId.getEntityId().compareTo(that.objectId.getEntityId());
		if (comparison != 0)
			return comparison;
		
		comparison = this.type.compareTo(that.type);
		if (comparison != 0)
			return comparison;
		
		if (this.info != null) {
			if (that.info == null) { // this.info != null && that.info == null
				return +1; // empty before evidence with info
			} else { // this.info != null && that.info != null
				if (this.info.hashCode() < that.info.hashCode())
					return -1;
				else if (this.info.hashCode() > that.info.hashCode())
					return +1;
			}
		} else { // this.info == null
			if (that.info == null) // this.info == null && that.info == null
				return 0;
			else // this.info == null && that.info != null
				return -1; // empty before evidence with info
		}
		
		if (this.sourceId != null) {
			if (that.sourceId == null) // this.sourceId != null && that.sourceId == null
				return +1; // direct before indirect evidence
			else // this.sourceId != null && that.sourceId != null
				return this.sourceId.getEntityId().compareTo(that.sourceId.getEntityId());
		} else { // sourceId == null
			if (that.sourceId == null) // this.sourceId == null && that.sourceId == null
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
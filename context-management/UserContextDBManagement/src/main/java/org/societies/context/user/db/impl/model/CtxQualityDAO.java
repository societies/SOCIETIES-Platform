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
package org.societies.context.user.db.impl.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.CtxQuality;

/**
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @see CtxQuality
 * @since 0.5
 */
@Entity
@org.hibernate.annotations.Entity(
		dynamicUpdate=true
)
@Table(
		name = "org_societies_context_quality"
)
public class CtxQualityDAO implements Serializable {

	private static final long serialVersionUID = -7378122470812617379L;

	@Id
	@Column(name="attribute_id")
	private String attributeId;
	
	/** The context attribute this QoC information refers to. */
	@OneToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(
			name = "attribute_id",
			nullable = false,
			updatable = false
	)
	private CtxAttributeDAO attribute;
	
	/** The time the current context attribute value was last updated. */
	@Column(name = "last_updated", nullable = true, updatable = true)
	private Date lastUpdated;
	
	/** The origin type of the current context attribute value. */
	@Column(name = "origin_type", nullable = true, updatable = true)
	private CtxOriginType originType = CtxOriginType.MANUALLY_SET;
	
	/** The precision of the current context attribute value. */
	@Column(name = "precis", nullable = true, updatable = true)
	private Double precision;
	
	/** The update frequency of the current context attribute value. */
	@Column(name = "update_frequency", nullable = true, updatable = true)
	private Double updateFrequency;
	
	CtxQualityDAO() { }
	
	/**
	 * Constructs a <code>CtxQualityDAO</code> object for the specified context
	 * attribute.
	 * 
	 * @param attribute
	 * the context attribute to associate with this QoC information.
	 *
	CtxQualityDAO(CtxAttributeDAO attribute) {
	
		this.attributeId = attribute.getId().toString();
		this.attribute = attribute;
	}*/
	
	public CtxQualityDAO(CtxAttributeIdentifier ctxId) {
		
		this.attributeId = ctxId.toString();
	}
	
	/**
	 * Returns the context attribute associated to this QoC information.
	 * 
	 * @return the context attribute associated to this QoC information.
	 */
	public CtxAttributeDAO getAttribute() {
		
		return this.attribute;
	}
	
	public void setAttribute(CtxAttributeDAO attribute) {
		
		this.attribute = attribute;
		
		if (this.equals(attribute.getQuality()))
			attribute.setQuality(this);
	}
	
	/**
     * Returns the time when the current context attribute value was last updated.
     * 
     * @return the time when the current context attribute value was last updated.
     * @see #getFreshness()
     */
	public Date getLastUpdated() {
		
		return this.lastUpdated;
	}
	
	/**
	 * Sets the time when the current context attribute value was last updated.
	 * 
	 * @param lastUpdated
	 *            the time when the current context attribute value was last updated
	 * @throws NullPointerException if the specified last update time is <code>null</code>
	 */
	void setLastUpdated(Date lastUpdated) {
		
		this.lastUpdated = lastUpdated;
	}
	
	/**
	 * Returns the origin type of the current context attribute value.
     * <p>
     * The method returns <code>null</code> if the origin type has not been set. 
     * 
     * @return the origin type of the current context attribute value.
     * @see CtxOriginType 
	 */
	public CtxOriginType getOriginType() {
		
		return this.originType;
	}
	
	/**
	 * Sets the origin type of the current context attribute value.
     * 
     * @param originType
     *            the origin type of the current context attribute value to set
     * @throws NullPointerException if the specified origin type is <code>null</code>.
     * @see CtxOriginType
	 */
	public void setOriginType(CtxOriginType originType) {
			
		this.originType = originType;
	}

	/**
	 * Returns the precision of the current context attribute value.
     * <p>
     * The method returns <code>null</code> if precision has not been set.
     * 
     * @return the precision of the current context attribute value.
	 */
	public Double getPrecision() {
		
		return this.precision;
	}

	/**
	 * Sets the precision of the current context attribute value.
     * 
     * @param precision
     *            the precision of the current context attribute value to set.
     * @throws NullPointerException if the specified precision is <code>null</code>.
	 */
	public void setPrecision(Double precision){
		
		this.precision = precision;
	}
	
	/**
	 * Returns the update frequency of the current context attribute value.
	 * <p>
	 * The method returns <code>null</code> if the update frequency has not been
	 * set.
	 * 
	 * @return the update frequency of the current context attribute value
	 */
	public Double getUpdateFrequency() {
		
		return this.updateFrequency;
	}
	
	/**
	 * Sets the update frequency of the current context attribute value.
	 * 
	 * @param updateFrequency
	 *            the update frequency of the current context attribute value to set.
	 */
	public void setUpdateFrequency(Double updateFrequency) {
		
		this.updateFrequency = updateFrequency;
	}

	/**
	 * TODO
	 * Returns a String representation of this QoC information.
	 * 
	 * @return a String representation of this QoC information.
	 *
	public String toString() {
	}*/

	/**
	 * @see java.lang.Object#hashCode()
	 * @since 0.0.2
	 */
	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		
		result = prime * result
				+ ((this.attribute == null) ? 0 : this.attribute.hashCode());
		
		return result;
	}

	/**
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
		
		CtxQualityDAO other = (CtxQualityDAO) that;
		if (this.attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!this.attribute.equals(other.attribute))
			return false;
		
		return true;
	}
}
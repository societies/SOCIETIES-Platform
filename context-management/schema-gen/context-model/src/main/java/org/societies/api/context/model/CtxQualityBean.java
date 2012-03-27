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
package org.societies.api.context.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is used to represent the quality characteristics of context
 * attribute values. This information is known as Quality of Context (QoC) and
 * contains the following properties:
 * <ul>
 * <li><b>Freshness</b>: Specifies the age of context information. This is
 * derived from the time that particular piece of context information was last
 * updated.</li>
 * <li><b>Origin</b>: Denotes whether a context attribute value was
 * {@link CtxOriginTypeBean#MANUALLY_SET manually set}, {@link CtxOriginTypeBean#SENSED
 * sensed}, {@link CtxOriginTypeBean#INFERRED inferred}, or 
 * {@link CtxOriginTypeBean#INHERITED inherited}.</li>
 * <li><b>Precision:</b> Denotes the quality of coherence or reproducibility of
 * measured context attribute values. It is usually expressed in terms of the
 * standard deviation of the extended set of measurement results from a well
 * defined measurement process. The standard deviation of the conceptual
 * population is approximated by the standard deviation of an extended set of
 * actual context attribute value measurements. For directly sensed context
 * information this parameter is identical to the precision of the sensor used,
 * e.g. the precision of a GPS device.</li>
 * <li><b>Update Frequency</b>: Defines how often a piece of context information
 * is updated. For directly sensed context information this parameter is
 * identical to the sample rate of the sensor used.</li>
 * </ul>
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @see CtxAttributeBean
 * @see CtxOriginTypeBean
 * @since 0.0.1
 */
@XmlType(namespace="http://societies.org/api/schema/context/model", propOrder = {"attribute", "lastUpdated", "originType", "precision", "updateFrequency"})
@XmlAccessorType(XmlAccessType.FIELD)
public class CtxQualityBean implements Serializable {

	private static final long serialVersionUID = 2596329083367854427L;

	/** The context attribute this QoC information refers to. */
	private CtxAttributeBean attribute;
	
	/** The time the current context attribute value was last updated. */
	@XmlElement(required = true, nillable=true)
	private Date lastUpdated;
	
	/** The origin type of the current context attribute value. */
	@XmlElement(required = true, nillable=true)
	private CtxOriginTypeBean originType;
	
	/** The precision of the current context attribute value. */
	@XmlElement(required = true, nillable=true)
	private Double precision;
	
	/** The update frequency of the current context attribute value. */
	@XmlElement(required = true, nillable=true)
	private Double updateFrequency;
	
	CtxQualityBean() {}
	
	/**
	 * Constructs a <code>CtxQualityBean</code> object for the specified context
	 * attribute.
	 * 
	 * @param attribute
	 * the context attribute to associate with this QoC information.
	 */
	CtxQualityBean(CtxAttributeBean attribute) {
		
		this.attribute = attribute;
	}
	
	/**
	 * Returns the context attribute associated to this QoC information.
	 * 
	 * @return the context attribute associated to this QoC information.
	 */
	public CtxAttributeBean getAttribute() {
		
		return this.attribute;
	}
	
	/**
	 * Returns the time in milliseconds since the last update of the current
	 * context attribute value.
     * 
     * @return the time in milliseconds since the last update of the current
     * context attribute value.
     * @see #getLastUpdated()
	 */
	public long getFreshness() {
		
		return new Date().getTime() - this.getLastUpdated().getTime();
	}
	
	/**
     * Returns the time when the current context attribute value was last updated.
     * 
     * @return the time when the current context attribute value was last updated.
     * @see #getFreshness()
     */
	public Date getLastUpdated() {
		
		return (this.lastUpdated != null) 
				? new Date(this.lastUpdated.getTime())
				: new Date(this.attribute.getLastModified().getTime());
	}
	
	/**
	 * Sets the time when the current context attribute value was last updated.
	 * 
	 * @param lastUpdated
	 *            the time when the current context attribute value was last updated
	 * @throws NullPointerException if the specified last update time is <code>null</code>
	 */
	void setLastUpdated(Date lastUpdated) {
		
		if (lastUpdated == null)
			throw new NullPointerException("lastUpdated can't be null");
		
		this.lastUpdated = new Date(lastUpdated.getTime());
	}
	
	/**
	 * Returns the origin type of the current context attribute value.
     * <p>
     * The method returns <code>null</code> if the origin type has not been set. 
     * 
     * @return the origin type of the current context attribute value.
     * @see CtxOriginTypeBean 
	 */
	public CtxOriginTypeBean getOriginType() {
		
		return this.originType;
	}
	
	/**
	 * Sets the origin type of the current context attribute value.
     * 
     * @param originType
     *            the origin type of the current context attribute value to set
     * @throws NullPointerException if the specified origin type is <code>null</code>.
     * @see CtxOriginTypeBean
	 */
	public void setOriginType(CtxOriginTypeBean originType) {
		
		if (originType == null)
			throw new NullPointerException("originType can't be null");
			
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
		
		return (this.precision != null) ? new Double(this.precision) : null;
	}

	/**
	 * Sets the precision of the current context attribute value.
     * 
     * @param precision
     *            the precision of the current context attribute value to set.
     * @throws NullPointerException if the specified precision is <code>null</code>.
	 */
	public void setPrecision(Double precision){
		
		this.precision = (precision != null) ? new Double(precision) : null;
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
		
		return (this.updateFrequency != null) ? new Double(this.updateFrequency) : null;
	}
	
	/**
	 * Sets the update frequency of the current context attribute value.
	 * 
	 * @param updateFrequency
	 *            the update frequency of the current context attribute value to set.
	 */
	public void setUpdateFrequency(Double updateFrequency) {
		
		this.updateFrequency = (updateFrequency != null) ? new Double(updateFrequency) : null;
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
	 * @since 0.0.2
	 */
	@Override
	public boolean equals(Object that) {
		
		if (this == that)
			return true;
		if (that == null)
			return false;
		if (this.getClass() != that.getClass())
			return false;
		
		CtxQualityBean other = (CtxQualityBean) that;
		if (this.attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!this.attribute.equals(other.attribute))
			return false;
		
		return true;
	}
}
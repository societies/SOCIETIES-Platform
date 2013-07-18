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
package org.societies.context.community.db.impl.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
@NamedQueries({
	@NamedQuery(
			name = "getCommunityCtxAttributeIdsByType",
			query = "select attribute.ctxId from CommunityCtxAttributeDAO as attribute where attribute.ctxId.type = :type"
	),
	@NamedQuery(
			name = "getCommunityCtxAttributeIdsByScopeAndType",
			query = "select attribute.ctxId from CommunityCtxAttributeDAO as attribute where attribute.ctxId.scope = :scope " + 
					"and attribute.ctxId.type = :type"
	),
	@NamedQuery(
			name = "getCommunityCtxAttributeIdsByOwnerIdAndType",
			query = "select distinct attribute.ctxId from CommunityCtxAttributeDAO as attribute inner join attribute.entity as entity where entity.ctxId.owner_id = :ownerId " + 
					"and attribute.ctxId.type = :type"
	)
})
@Entity
@org.hibernate.annotations.Entity(
		dynamicUpdate=true
)
@Table(
		name = "org_societies_context_community_attributes", 
		uniqueConstraints = { @UniqueConstraint(columnNames = {
				"scope", "type", "object_number" }) }
)
public class CommunityCtxAttributeDAO extends CtxModelObjectDAO {

	private static final long serialVersionUID = -2191470401169466190L;

	/** The identifier of this attribute. */
	@Columns(columns = {
			@Column(name = "scope", nullable = false, updatable = false, length = 255),
			@Column(name = "type", nullable = false, updatable = false, length = 63),
			@Column(name = "object_number", nullable = false, updatable = false)
	})
	@Type(type="org.societies.context.community.db.impl.model.hibernate.CtxAttributeIdentifierCompositeType")
	private CtxAttributeIdentifier ctxId;
	
	@ManyToOne(
			fetch = FetchType.EAGER,
			targetEntity = CommunityCtxEntityBaseDAO.class
	)
	@JoinColumn(
			name = "entity_id",
			nullable = false,
			updatable = false
	)
	private CommunityCtxEntityBaseDAO entity;
	
	/** The text value of this context attribute. */
	@Column(name = "string_value", length = 255, nullable = true, updatable = true)
	private String stringValue;
	
	/** The integer value of this context attribute. */
	@Column(name = "integer_value", nullable = true, updatable = true)
	private Integer integerValue;
	
	/** The double-precision floating point numeric value of this context attribute.*/
	@Column(name = "double_value", nullable = true, updatable = true)
	private Double doubleValue;
	
	/** The binary value of this context attribute. */
	@Column(name = "binary_value", nullable = true, updatable = true)
	@Lob
	private byte[] binaryValue;
	
	/** The value type of this context attribute */
	@Column(name = "value_type", nullable = false, updatable = true)
	private CtxAttributeValueType valueType = CtxAttributeValueType.EMPTY;
	
	@Column(name = "value_metric", length = 63)
	/** The metric for the current context attribute value */
	private String valueMetric;
	
	/** The QoC meta-data. */
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "attribute"/*, orphanRemoval = true*/)
	private CommunityCtxQualityDAO quality;
	
	/** The identifier of the context source for the current attribute value. */
	@Column(name = "source_id", length = 255, nullable = true, updatable = true)
	private String sourceId;
	
	/** The history flag of this context attribute. */
	@Column(name = "history_recorded", nullable = false, updatable = true)
	private boolean historyRecorded;

	CommunityCtxAttributeDAO() {
		
		super(null);
	}
	
	public CommunityCtxAttributeDAO(CtxAttributeIdentifier ctxId) {
		
		super(ctxId.toString());
		this.ctxId = ctxId;
	}
	
	/*
	 * @see org.societies.context.user.db.impl.model.CtxModelObjectDAO#getId()
	 */
	@Override
	public CtxAttributeIdentifier getId() {
		
		return this.ctxId;
	}
	
	public CommunityCtxEntityBaseDAO getEntity() {
		
		return this.entity;
	}
	
	public void setEntity(CommunityCtxEntityBaseDAO entity) {
		
		if (!entity.getAttributes().contains(this))
			entity.addAttribute(this);
		
		this.entity = entity;
	}
	
	/**
	 * Returns the value of this context attribute or <code>null</code>
	 * if the value is not a String.
	 * 
	 * @return the value of this context attribute or <code>null</code>
	 *         if the value is not a String.
	 * @see #getIntegerValue()
	 * @see #getDoubleValue()
	 * @see #getBinaryValue()
	 */
	public String getStringValue() {
		
		return this.stringValue;
	}
	
	/**
	 * Sets the value of this context attribute to the specified String.
	 * 
	 * @param value
	 *            the String value to set
	 * @see #setIntegerValue(Integer)
	 * @see #setDoubleValue(Double)
	 * @see #setBinaryValue(byte[])
	 */
	public void setStringValue(String value) {
		
		this.stringValue = value;
	}

	/**
	 * Returns the value of this context attribute or <code>null</code>
	 * if the value is not an Integer.
	 * 
	 * @return the value of this context attribute or <code>null</code>
	 *         if the value is not an Integer.
	 * @see #getStringValue()
	 * @see #getDoubleValue()
	 * @see #getBinaryValue()
	 */
	public Integer getIntegerValue() {
		
		return this.integerValue;
	}
	
	/**
	 * Sets the value of this context attribute to the specified Integer.
	 * 
	 * @param value
	 *            the Integer value to set
	 * @see #setStringValue(String)
	 * @see #setDoubleValue(Double)
	 * @see #setBinaryValue(byte[])
	 */
	public void setIntegerValue(Integer value) {
		
		this.integerValue = value;
	}
	
	/**
	 * Returns the value of this context attribute or <code>null</code>
	 * if the value is not a Double.
	 * 
	 * @return the value of this context attribute or <code>null</code>
	 *         if the value is not a Double.
	 * @see #getStringValue()
	 * @see #getIntegerValue()
	 * @see #getBinaryValue()
	 */
	public Double getDoubleValue() {
		
		return this.doubleValue;
	}
	
	/**
	 * Sets the value of this context attribute to the specified Double.
	 * 
	 * @param value
	 *            the Double value to set
	 * @see #setStringValue(String)
	 * @see #setIntegerValue(Integer)
	 * @see #setBinaryValue(byte[])
	 */
	public void setDoubleValue(Double value) {
		
		this.doubleValue = value;
	}
	
	/**
	 * Returns the value of this context attribute or <code>null</code>
	 * if the value is not a byte array.
	 * 
	 * @return the value of this context attribute or <code>null</code>
	 *         if the value is not a byte array.
	 * @see #getStringValue()
	 * @see #getIntegerValue()
	 * @see #getDoubleValue()
	 */
	public byte[] getBinaryValue() {
		
		return this.binaryValue;
	}
	
	/**
	 * Sets the value of this context attribute to the specified byte array.
	 * 
	 * @param value
	 *            the byte array value to set
	 * @see #setStringValue(String)
	 * @see #setIntegerValue(Integer)
	 * @see #setDoubleValue(Double)
	 */
	public void setBinaryValue(byte[] value) {
		
		this.binaryValue = value;
	}
	
	/**
	 * Returns the value type of this context attribute
	 * 
	 * @return the value type of this context attribute
	 */
	public CtxAttributeValueType getValueType() {
		
		return this.valueType;
	}
	
	/**
	 * Sets the value type of this context attribute
	 * 
	 * @param valueType 
	 *            the value type to set for this context attribute
	 * @see CtxAttributeValueType
	 */
	public void setValueType(CtxAttributeValueType valueType) {
		
		this.valueType = valueType;
	}
	
	/**
	 * Returns the metric for the context attribute value.
	 * 
	 * @return the metric for the context attribute value.
	 */
	public String getValueMetric() {
		
		return this.valueMetric;
	}
	
	/**
	 * Sets the metric for the context attribute value.
	 * 
	 * @param valueMetric
	 *            the metric for the context attribute value to set
	 */
	public void setValueMetric(String valueMetric) {
		
		this.valueMetric = valueMetric;
	}
	
	/**
	 * Returns the Quality of Context (QoC) information associated to this context
	 * attribute.
	 * 
	 * @return the <code>CommunityCtxQualityDAO</code> associated to this context
	 *         attribute.
	 * @see CommunityCtxQualityDAO
	 */
	public CommunityCtxQualityDAO getQuality() {
		
		return this.quality;
	}
	
	public void setQuality(CommunityCtxQualityDAO quality) {
		
		this.quality = quality;
		
		if (!this.equals(quality.getAttribute()))
			quality.setAttribute(this);
	}
	
	/**
	 * Returns the identifier of the context source for the current attribute value.
	 * 
	 * @return the identifier of the context source for the current attribute value.
	 */
	public String getSourceId() {
		
		return this.sourceId;
	}
	
	/**
	 * Sets the identifier of the context source for the current attribute value.
	 * 
	 * @param sourceId
	 *            the identifier of the context source to set.
	 */
	public void setSourceId(String sourceId) {
		
		this.sourceId = sourceId;
	}
	
	/**
	 * Checks if this context attribute is maintained in the context history
	 * repository
	 * 
	 * @return <code>true</code> if this context attribute is maintained in the
	 * context history repository; <code>false</code> otherwise
	 */
	public boolean isHistoryRecorded() {
		
		return this.historyRecorded;
	}
		
	/**
	 * Controls if this context attribute is to be maintained in the context
	 * history repository
	 * 
	 * @param historyRecorded
	 *            set to <code>true</code> if this context attribute is to be
	 *            maintained in the context history repository;
	 *            <code>false</code> otherwise
	 */
	public void setHistoryRecorded(boolean historyRecorded) {
		
		this.historyRecorded = historyRecorded;
	}
}
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
package org.societies.context.userHistory.impl.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxHistoryAttribute;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4
 */
@Entity
@Table(
		name = "org_societies_context_user_history"
)
public class UserCtxHistoryAttributeDAO implements Serializable {

	private static final long serialVersionUID = 7503947788560927868L;

	@Id
	@GeneratedValue
	@Column(name = "history_record_id")
	private Long historyRecordId;
	
	@Columns(columns = {
			@Column(name = "scope", nullable = false, updatable = false, length = 255),
			@Column(name = "type", nullable = false, updatable = false, length = 63),
			@Column(name = "object_number", nullable = false, updatable = false)
	})
	@Type(type="org.societies.context.userHistory.impl.model.hibernate.CtxAttributeIdentifierCompositeType")
	final private CtxAttributeIdentifier ctxId;
	
	@Column(name = "last_modified", nullable = false, updatable = false)
	final private Date lastModified;
	
	@Column(name = "last_updated", nullable = false, updatable = false)
	final private Date lastUpdated;
	
	@Column(name = "string_value", length = 255, nullable = true, updatable = false)
	final private String stringValue;
	
	@Column(name = "integer_value", nullable = true, updatable = false)
	final private Integer integerValue;
	
	@Column(name = "double_value", nullable = true, updatable = false)
	final private Double doubleValue;
	
	@Column(name = "binary_value", nullable = true, updatable = false)
	@Lob
	final private byte[] binaryValue;
	
	@Column(name = "value_type", nullable = false, updatable = false)
	final private CtxAttributeValueType valueType;
	
	@Column(name = "value_metric", length = 63, nullable = true, updatable = false)
	final private String valueMetric;

	/**
	 * Empty constructor required by Hibernate
	 */
	UserCtxHistoryAttributeDAO() {
		
		this.ctxId = null;
		this.lastModified = null;
		this.lastUpdated = null;
		this.stringValue = null;
		this.integerValue = null;
		this.doubleValue = null;
		this.binaryValue = null;
		this.valueType = null;
		this.valueMetric = null;
	}
	
	UserCtxHistoryAttributeDAO(final CtxAttributeIdentifier id, 
			final Date lastModified, final Date lastUpdated,
			final String stringValue, final Integer integerValue,
			final Double doubleValue, final byte[] binaryValue, 
			final CtxAttributeValueType valueType, final String valueMetric) {
		
		this.ctxId = id;
		this.lastModified = lastModified;
		this.lastUpdated = lastUpdated;
		this.stringValue = stringValue;
		this.integerValue = integerValue;
		this.doubleValue = doubleValue;
		this.binaryValue = binaryValue;
		this.valueType = valueType;
		this.valueMetric = valueMetric;
	}
	
	/**
	 * 
	 * @param attribute
	 */
	UserCtxHistoryAttributeDAO(CtxAttribute attribute) {

		this.ctxId = attribute.getId();
		this.lastModified = attribute.getLastModified();
		this.lastUpdated = attribute.getQuality().getLastUpdated();
		this.stringValue = attribute.getStringValue();
		this.integerValue = attribute.getIntegerValue();
		this.doubleValue = attribute.getDoubleValue();
		this.binaryValue = attribute.getBinaryValue();
		this.valueType = attribute.getValueType();
		this.valueMetric = attribute.getValueMetric();
	}
	
	/**
	 * 
	 * @param attribute
	 */
	UserCtxHistoryAttributeDAO(CtxHistoryAttribute attribute) {

		this.ctxId = attribute.getId();
		this.lastModified = attribute.getLastModified();
		this.lastUpdated = attribute.getLastUpdated();
		this.stringValue = attribute.getStringValue();
		this.integerValue = attribute.getIntegerValue();
		this.doubleValue = attribute.getDoubleValue();
		this.binaryValue = attribute.getBinaryValue();
		this.valueType = attribute.getValueType();
		this.valueMetric = attribute.getValueMetric();
	}
	
	public Long getHistoryRecordId() {
		
		return this.historyRecordId;
	}
	
	public CtxAttributeIdentifier getId() {
		
		return this.ctxId;
	}

	public Date getLastModified() {
		
		return this.lastModified;
	}

	public Date getLastUpdated() {
		
		return this.lastUpdated;
	}
	
	public String getStringValue() {
		
		return this.stringValue;
	}
	
	public Integer getIntegerValue() {
		
		return this.integerValue;
	}

	public Double getDoubleValue() {
		
		return this.doubleValue;
	}
	
	public byte[] getBinaryValue() {
		
		return this.binaryValue;
	}
	
	public CtxAttributeValueType getValueType() {
		
		return this.valueType;
	}
	
	public String getValueMetric() {
		
		return this.valueMetric;
	}
}
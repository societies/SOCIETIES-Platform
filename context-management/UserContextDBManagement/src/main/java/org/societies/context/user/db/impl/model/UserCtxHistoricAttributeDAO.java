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
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.societies.api.context.model.CtxIdentifier;

/**
 * Describe your class here...
 *
 * @author Pavlos Kosmides
 *
 */
@Entity
@Table(name = "history")
public class UserCtxHistoricAttributeDAO implements Serializable {

	private static final long serialVersionUID = 1L;

	private long hocId;
	private CtxIdentifier attributeId;
	private Date timestamp;
	private String valueStr;
	private Integer valueInt;
	private Double valueDbl;
	private byte[] valueBlob;

	/**
	 * @param hocId
	 * @param attributeId
	 * @param timestamp
	 * @param valueStr
	 * @param valueInt
	 * @param valueDbl
	 * @param valueBlob
	 */
	public UserCtxHistoricAttributeDAO(long hocId, CtxIdentifier attributeId, Date timestamp, String valueStr, Integer valueInt, Double valueDbl, byte[] valueBlob) {
		super();

		this.hocId = hocId;
		this.attributeId = attributeId;
		this.timestamp = timestamp;
		this.valueStr = valueStr;
		this.valueInt = valueInt;
		this.valueDbl = valueDbl;
		this.valueBlob = valueBlob;
	}

	/**
	 * 
	 */
	public UserCtxHistoricAttributeDAO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Column(name = "attribute_id")
	@Type(type="org.societies.context.user.db.impl.model.hibernate.CtxAttributeIdentifierType")
	public CtxIdentifier getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(CtxIdentifier attributeId) {
		this.attributeId = attributeId;
	}

	@Column(name = "timestamp")
	@Temporal(value=TemporalType.TIMESTAMP)
	@org.hibernate.annotations.Generated(value=GenerationTime.ALWAYS)
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Column(name = "value_str")
	public String getValueStr() {
		return valueStr;
	}
	public void setValueStr(String valueStr) {
		this.valueStr = valueStr;
	}
	
	@Column(name = "value_int")
	public Integer getValueInt() {
		return valueInt;
	}
	public void setValueInt(Integer valueInt) {
		this.valueInt = valueInt;
	}

	@Column(name = "value_dbl")
	public Double getValueDbl() {
		return valueDbl;
	}
	public void setValueDbl(Double valueDbl) {
		this.valueDbl = valueDbl;
	}
	
	@Column(name = "value_blob")
	public byte[] getValueBlob() {
		return valueBlob;
	}
	public void setValueBlob(byte[] valueBlob) {
		this.valueBlob = valueBlob;
	}
	
	@Id
	@Column(name="hoc_id")
	public long getHocId() {
		return hocId;
	}

	public void setHocId(long hocId) {
		this.hocId = hocId;
	}
	
}
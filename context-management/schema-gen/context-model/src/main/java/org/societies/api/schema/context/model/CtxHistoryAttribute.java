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
package org.societies.api.schema.context.model;

import java.util.Date;

/**
 * This class is used in order to represent context history attributes
 * maintained in the context history database.
 * 
 * @author <a href="mailto:nikosk@cn.ntua.gr">Nikos Kalatzis</a> (ICCS)
 * @since 0.0.1
 */
public class CtxHistoryAttribute extends CtxModelObject {

	private static final long serialVersionUID = -1908778456166623132L;

	
	private Long historyRecordId;
	
	private String stringValue;
	private Integer integerValue;
	private Double doubleValue;
	private byte[] binaryValue;

	private Date lastModified;

	CtxHistoryAttribute() {}
	
	public CtxHistoryAttribute(CtxAttribute ctxAttribute) {
		super(ctxAttribute.getId());
		this.setValues(ctxAttribute);
		this.lastModified = ctxAttribute.getQuality().getLastUpdated();
	}
	
	public CtxHistoryAttribute(CtxAttribute ctxAttribute, Long historyRecordId) {
		this(ctxAttribute);
		this.historyRecordId = historyRecordId;
	}

	/**
	 * Returns the identifier of this historic context attribute.
	 * 
	 * @return the identifier of this historic context attribute.
	 */
	@Override
	public CtxAttributeIdentifier getId() {
		return (CtxAttributeIdentifier) super.getId();
	}
	
	/**
	 * Returns the string value of this historic context attribute.
	 * 
	 * @return string value
	 */
	public String getStringValue() {
		return this.stringValue;
	}
	
	void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	/**
	 * Returns the integer value of this historic context attribute.
	 * 
	 * @return integer value
	 */
	public Integer getIntegerValue() {
		return this.integerValue;
	}
	
	void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	/**
	 * Returns the double value of this historic context attribute.
	 * 
	 * @return double value
	 */
	public Double getDoubleValue() {
		return this.doubleValue;
	}
	
	void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	/**
	 * Returns the blob value of this historic context attribute.
	 * 
	 * @return blob value
	 */
	public byte[] getBinaryValue() {
		return this.binaryValue;
	}
	
	void setBinaryValue(byte[] binaryValue) {
		this.binaryValue = binaryValue;
	}
	
	/**
	 * TODO
	 * Returns a String representation of this historic context attribute.
	 * 
	 * @return a String representation of this historic context attribute.
	 */
	@Override
	public String toString() {
		return getId().toString(); 
	}


	private void setValues(CtxAttribute ctxAttribute) {
		this.stringValue = ctxAttribute.getStringValue();
		this.integerValue = ctxAttribute.getIntegerValue();
		this.doubleValue = ctxAttribute.getDoubleValue();
		this.binaryValue = ctxAttribute.getBinaryValue();
	}

	public Date getLastModified(){
		return this.lastModified;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 * @since 0.0.2
	 */
	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = super.hashCode();
		
		result = prime * result
				+ ((this.historyRecordId == null) ? 0 : this.historyRecordId.hashCode());
		
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
		if (!super.equals(that))
			return false;
		if (this.getClass() != that.getClass())
			return false;
		
		CtxHistoryAttribute other = (CtxHistoryAttribute) that;
		if (this.historyRecordId == null) {
			if (other.historyRecordId != null)
				return false;
		} else if (!this.historyRecordId.equals(other.historyRecordId))
			return false;
		return true;
	}
}
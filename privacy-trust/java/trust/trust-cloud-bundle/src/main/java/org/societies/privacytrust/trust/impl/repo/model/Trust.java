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
package org.societies.privacytrust.trust.impl.repo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;
import org.societies.privacytrust.trust.api.model.ITrust;

/**
 * Implementation of the {@link ITrust} interface.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
@MappedSuperclass
public abstract class Trust implements ITrust {

	private static final long serialVersionUID = 3965922195661451444L;
	
	@Column(name = "value")
	protected Double value;
	
	@Column(name = "last_modified")
	@Type(type = "org.societies.privacytrust.trust.impl.common.hibernate.DateTimeUserType")
	protected Date lastModified;
	
	@Column(name = "last_updated")
	@Type(type = "org.societies.privacytrust.trust.impl.common.hibernate.DateTimeUserType")
	protected Date lastUpdated;

	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrust#getValue()
	 */
	@Override
	public Double getValue() {
		
		return (this.value != null) ? new Double(this.value) : null;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrust#setValue(java.lang.Double)
	 */
	@Override
	public void setValue(Double newValue) {
		
		this.lastUpdated = new Date();
		if ((this.value == null && newValue != null) ||	(!this.value.equals(newValue)))
			this.lastModified = this.lastUpdated;
		this.value = newValue;
	}

	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrust#getLastModified()
	 */
	@Override
	public Date getLastModified() {
		
		return this.lastModified;
	}

	/* (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.model.ITrust#getLastUpdated()
	 */
	@Override
	public Date getLastUpdated() {
		
		return this.lastUpdated;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.lastModified == null) ? 0 : this.lastModified.hashCode());
		result = prime * result
				+ ((this.lastUpdated == null) ? 0 : this.lastUpdated.hashCode());
		result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
		
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
		
		Trust other = (Trust) that;
		if (this.lastModified == null) {
			if (other.lastModified != null)
				return false;
		} else if (!this.lastModified.equals(other.lastModified))
			return false;
		if (this.lastUpdated == null) {
			if (other.lastUpdated != null)
				return false;
		} else if (!this.lastUpdated.equals(other.lastUpdated))
			return false;
		if (this.value == null) {
			if (other.value != null)
				return false;
		} else if (!this.value.equals(other.value))
			return false;
		
		return true;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append("value=" + this.value);
		sb.append(",");
		sb.append("lastModified=" + this.lastModified);
		sb.append(",");
		sb.append("lastUpdated=" + this.lastUpdated);
		sb.append(">");
		
		return sb.toString();
	}
}
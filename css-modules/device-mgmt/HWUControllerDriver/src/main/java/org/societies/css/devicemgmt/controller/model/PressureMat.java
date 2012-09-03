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
package org.societies.css.devicemgmt.controller.model;

import java.io.Serializable;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class PressureMat implements IPluggableResource, Serializable{



	private boolean enabled = false;
	private final String pressureMatId;
	private final String pressureMatLocation;
	private final CtxAttributeIdentifier ctxId;
	private final CtxAttributeValueType valueType = CtxAttributeValueType.INTEGER;
	
	public PressureMat(String id, String location, CtxAttributeIdentifier ctxId){
		this.pressureMatId = id;
		this.pressureMatLocation = location;
		this.ctxId = ctxId;
	}

	/**
	 * @return the pressureMatId
	 */
	public String getPressureMatId() {
		return pressureMatId;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the pressureMatLocation
	 */
	public String getPressureMatLocation() {
		return pressureMatLocation;
	}


	/**
	 * @return the ctxId
	 */
	public CtxAttributeIdentifier getCtxId() {
		return ctxId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ctxId == null) ? 0 : ctxId.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result
				+ ((pressureMatId == null) ? 0 : pressureMatId.hashCode());
		result = prime
				* result
				+ ((pressureMatLocation == null) ? 0 : pressureMatLocation
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PressureMat other = (PressureMat) obj;
		if (ctxId == null) {
			if (other.ctxId != null)
				return false;
		} else if (!ctxId.equals(other.ctxId))
			return false;
		if (enabled != other.enabled)
			return false;
		if (pressureMatId == null) {
			if (other.pressureMatId != null)
				return false;
		} else if (!pressureMatId.equals(other.pressureMatId))
			return false;
		if (pressureMatLocation == null) {
			if (other.pressureMatLocation != null)
				return false;
		} else if (!pressureMatLocation.equals(other.pressureMatLocation))
			return false;
		return true;
	}

	/**
	 * @return the valueType
	 */
	public CtxAttributeValueType getValueType() {
		return valueType;
	}

	@Override
	public String getPortId() {
		return this.pressureMatId;
	}
	
	
}

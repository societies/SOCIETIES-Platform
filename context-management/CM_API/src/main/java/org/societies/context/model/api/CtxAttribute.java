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
package org.societies.context.model.api;

import java.io.Serializable;

/**
 * This class is used to represent context attributes.
 * 
 * @author nikosk
 * @version 0.0.1
 */
public class CtxAttribute extends CtxModelObject {

	private static final long serialVersionUID = 2885099443175534995L;
	
	private Serializable value;
	private CtxAttributeValueType valueType;
	private String valueMetric;
	private CtxQuality quality;
	private String sourceId;

	private CtxAttribute() {}

	/**
	 * 
	 */
	@Override
	public CtxAttributeIdentifier getId() {
		return (CtxAttributeIdentifier) super.getId();
	}

	/**
	 * Returns the value of the CtxAttribute
	 * @return Serializable
	 */
	public Serializable getValue(){
		return this.value;
	}

	/**
	 * Sets the value of the CtxAttribute
	 * @param value
	 */
	public void setValue(Serializable value){
		this.value = value;
	}
	
	/**
	 * Returns the value type of the CtxAttribute
	 * 
	 * @return CtxAttributeValueType
	 */
	public CtxAttributeValueType getValueType(){
		return this.valueType;
	}
	
	/**
	 * Returns the metric for the context attribute value.
	 * 
	 * @return String
	 */
	public String getValueMetric() {
		return this.valueMetric;
	}
	
	/**
	 * Sets tha metric for the context attribute value.
	 * 
	 * @param valueMetric
	 */
	public void setValueMetric(String valueMetric) {
		this.valueMetric = valueMetric;
	}
	
	/**
	 * Returns the Quality of Context (QoC) information associated to this context
	 * attribute.
	 * 
	 * @return the <code>CtxQuality</code> associated to this context
	 *         attribute.
	 * @see CtxQuality
	 */
	public CtxQuality getQuality() {
		return this.quality;
	}
	
	/**
	 * Returns the identifier of the context source.
	 * 
	 * @return the identifier of the context source.
	 */
	public String getSourceId() {
		return this.sourceId;
	}
	
	/**
	 * Sets the identifier of the context source.
	 * 
	 * @param sourceId
	 *            the identifier of the context source to set.
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
}
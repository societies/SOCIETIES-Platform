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
package org.societies.privacytrust.privacyprotection.api.model.privacypreference;

import java.io.Serializable;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyConditionConstants;


/**
 * This class represents a context condition. It is a name-value pair in which the name represents a type in 
 * the personal data of the user such as location or activity and the value represents one of the values that 
 * this type can take (e.g. name = location, value = home). 
 * @author Elizabeth
 *
 */
public class ContextPreferenceCondition implements IPrivacyPreferenceCondition, Serializable{

	private CtxAttributeIdentifier ctxID;
	private String value;
	private PrivacyConditionConstants myConditionType;

	private OperatorConstants operator;
	public ContextPreferenceCondition(CtxAttributeIdentifier ctxIdentifier, OperatorConstants op, String val){
		this.ctxID = ctxIdentifier;
		this.operator = op;
		this.value = val;
		this.myConditionType = PrivacyConditionConstants.CONTEXT;
	}
	
	public CtxAttributeIdentifier getCtxIdentifier(){
		return this.ctxID;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the operator
	 */
	public OperatorConstants getOperator() {
		return operator;
	}
	
	public String toString(){
		return this.ctxID.getType()+" "+this.operator+" "+this.value;
	}
	
	public boolean equals(IPrivacyPreferenceCondition ippc){
		if (!(ippc instanceof ContextPreferenceCondition)){
			return false;
		}
		ContextPreferenceCondition cpc = (ContextPreferenceCondition) ippc;
		if (!(this.ctxID.equals(cpc.getCtxIdentifier()))){
			return false;
		}
		if (!(this.operator.equals(cpc.getOperator()))){
			return false;
		}
		if (!(this.value.toLowerCase().equals(cpc.getValue().toLowerCase()))){
			return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyPreferenceCondition#getType()
	 */
	@Override
	public PrivacyConditionConstants getType() {
		return PrivacyConditionConstants.CONTEXT;
	}
	
}

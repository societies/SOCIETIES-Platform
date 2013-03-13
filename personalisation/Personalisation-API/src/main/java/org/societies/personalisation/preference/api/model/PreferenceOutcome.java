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
package org.societies.personalisation.preference.api.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.personalisation.model.Action;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;



/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:57
 */
public class PreferenceOutcome extends Action implements IPreferenceOutcome, Serializable {

	private int confidenceLevel;
	private ArrayList<String> parameterNames;
	private IQualityofPreference qop;

	public PreferenceOutcome(){
		super();
	}

	public PreferenceOutcome(ServiceResourceIdentifier serviceID, String serviceType, String parameterName, String value){
		super(serviceID, serviceType, parameterName, value);
		this.confidenceLevel = 51;
	}

	public PreferenceOutcome(ServiceResourceIdentifier serviceID, String serviceType, String parameterName, String value, boolean implementable, boolean contextDependent, boolean proactive){
		super(serviceID, serviceType, parameterName, value, implementable, contextDependent, proactive);
		this.confidenceLevel = 51;
	}

	/**
	 * Method to set the confidence level
	 * @param confidenceLevel
	 */
	public void setConfidenceLevel(int confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}

	/**
	 * @see IOutcome#getConfidenceLevel()
	 */
	public int getConfidenceLevel() {
		return confidenceLevel;
	}


	/* (non-Javadoc)
	 * @see org.personalsmartspace.pm.prefmodel.api.platform.IOutcome#getQualityofPreference()
	 */
	@Override
	public IQualityofPreference getQualityofPreference() {
		return this.qop;
	}
	
	public void setQualityofPreference(IQualityofPreference qop){
		this.qop = qop;
	}
	


}
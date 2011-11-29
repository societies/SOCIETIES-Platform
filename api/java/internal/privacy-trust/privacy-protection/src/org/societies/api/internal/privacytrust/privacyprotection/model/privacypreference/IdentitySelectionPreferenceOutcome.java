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
package org.societies.api.internal.privacytrust.privacyprotection.model.privacypreference;


import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypreference.constants.PrivacyPreferenceTypeConstants;



/**
 * This class is used to define that a CSS identity should be used in a specific transaction if the preceding IPrivacyPreferenceConditions are true. 
 * The format of the identity will be defined by the Identity Management component
 * @author Elizabeth
 *
 */
public class IdentitySelectionPreferenceOutcome implements IPrivacyOutcome{

	private int confidenceLevel;
	private EntityIdentifier dpi;
	private ServiceResourceIdentifier serviceID;
	private Subject requestor;
	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyOutcome#getOutcomeType()
	 */
	@Override
	public PrivacyPreferenceTypeConstants getOutcomeType() {
		return PrivacyPreferenceTypeConstants.IDS;
	}
	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyOutcome#getConfidenceLevel()
	 */
	@Override
	public int getConfidenceLevel() {
		return this.confidenceLevel;
	}
	
	public void setConfidenceLevel(int c){
		this.confidenceLevel = c;
	}
	
	public void setIdentity(EntityIdentifier dpi){
		this.dpi = dpi;
	}
	
	public EntityIdentifier getIdentity(){
		return this.dpi;
	}
	public void setServiceID(ServiceResourceIdentifier serviceID) {
		this.serviceID = serviceID;
	}
	public ServiceResourceIdentifier getServiceID() {
		return serviceID;
	}
	public void setRequestor(Subject requestor) {
		this.requestor = requestor;
	}
	public Subject getRequestor() {
		return requestor;
	}
	
	public String toString(){
		return "Select: "+this.dpi.toString();
	}
	
}


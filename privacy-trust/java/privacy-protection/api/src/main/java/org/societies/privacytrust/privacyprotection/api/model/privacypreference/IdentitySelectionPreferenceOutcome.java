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


import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;



/**
 * This class is used to define that a CSS IIdentity should be used in a specific transaction if the preceding IPrivacyPreferenceConditions are true. 
 * The format of the IIdentity will be defined by the IIdentity Management component
 * @author Elizabeth
 *
 */
public class IdentitySelectionPreferenceOutcome extends IPrivacyOutcome{

	private IIdentity userIdentity;
	private Requestor requestor;

	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome#getOutcomeType()
	 */
	@Override
	public PrivacyPreferenceTypeConstants getOutcomeType() {
		return PrivacyPreferenceTypeConstants.IDS;
	}

	
	public void setIdentity(IIdentity userId){
		this.userIdentity = userId;
	}
	
	public IIdentity getIdentity(){
		return this.userIdentity;
	}
	
	public void setRequestor(Requestor requestor) {
		this.requestor = requestor;
	}
	public Requestor getRequestor() {
		return requestor;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((requestor == null) ? 0 : requestor.hashCode());
		result = prime * result
				+ ((userIdentity == null) ? 0 : userIdentity.hashCode());
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
		IdentitySelectionPreferenceOutcome other = (IdentitySelectionPreferenceOutcome) obj;
		if (requestor == null) {
			if (other.requestor != null)
				return false;
		} else if (!requestor.equals(other.requestor))
			return false;
		if (userIdentity == null) {
			if (other.userIdentity != null)
				return false;
		} else if (!userIdentity.equals(other.userIdentity))
			return false;
		return true;
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return "Select: "+this.userIdentity.toString();
	}
	
}


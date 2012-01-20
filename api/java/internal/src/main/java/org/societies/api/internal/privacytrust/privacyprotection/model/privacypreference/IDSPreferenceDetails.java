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

import java.io.Serializable;

import org.societies.api.mock.EntityIdentifier;
import org.societies.api.mock.ServiceResourceIdentifier;

public class IDSPreferenceDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EntityIdentifier affectedDPI;
	private EntityIdentifier providerDPI;
	private ServiceResourceIdentifier serviceID;
	
	public IDSPreferenceDetails(EntityIdentifier affectedDPI){
		this.affectedDPI = affectedDPI;
	}
	
	public void setAffectedDPI(EntityIdentifier affectedDPI) {
		this.affectedDPI = affectedDPI;
	}

	public EntityIdentifier getAffectedDPI() {
		return affectedDPI;
	}

	public void setServiceID(ServiceResourceIdentifier serviceID) {
		this.serviceID = serviceID;
	}

	public ServiceResourceIdentifier getServiceID() {
		return serviceID;
	}

	public void setProviderDPI(EntityIdentifier providerDPI) {
		this.providerDPI = providerDPI;
	}

	public EntityIdentifier getProviderDPI() {
		return providerDPI;
	}
	
	private boolean compareProviderDPI(EntityIdentifier requestorDPI){
		if (requestorDPI==null){
			if (this.providerDPI==null){
				return true;
			}else{
				return false;
			}
		}else{
			if (requestorDPI==null){
				return false;
			}else{
				if (requestorDPI.toUriString().equalsIgnoreCase(this.providerDPI.toUriString())){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	
	private boolean compareServiceID(ServiceResourceIdentifier serviceID2){
		if (serviceID2==null){
			if (this.serviceID == null){
				return true;
			}else{
				return false;
			}
		}else{
			if (serviceID2==null){
				return false;
			}else{
				if (serviceID2.toUriString().equalsIgnoreCase(this.serviceID.toUriString())){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	@Override
	public boolean equals(Object obj){
		if (obj instanceof IDSPreferenceDetails){
			IDSPreferenceDetails details = (IDSPreferenceDetails) obj;
			if (getAffectedDPI().toUriString().equalsIgnoreCase(details.getAffectedDPI().toUriString())){
				if (this.compareProviderDPI(details.getProviderDPI())){
					return this.compareServiceID(details.getServiceID());
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	@Override
	public String toString(){
		String str = "\n";
		str = str.concat("AffectedDPI: "+this.getAffectedDPI().toUriString());
		
		if (this.providerDPI!=null){
			str = str.concat("\nProvider DPI: "+this.providerDPI.toUriString());
		}
		if (this.serviceID!=null){
			str = str.concat("\nServiceID :"+this.serviceID.toUriString());
		}
		str = str.concat("\n");
		return str;
	}


	
}

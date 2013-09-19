/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
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
package org.societies.api.internal.personalisation.model;

import java.io.Serializable;

import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;



//import org.societies.api.internal.servicelifecycle.serviceRegistry.model.ServiceResourceIdentifier;

/**
 * @author Eliza
 * @version 1.0
 * @created 08-Nov-2011 14:02:57
 */
public class PreferenceDetails implements Serializable {

	private String preferenceName;
	private static final long serialVersionUID = 1L;
	private ServiceResourceIdentifier serviceID = null;
	private String serviceType;

	public PreferenceDetails(){

	}

	public void finalize() throws Throwable {

	}

	/**
	 * 
	 * @param serviceType
	 * @param serviceID
	 * @param preferenceName
	 */
	public PreferenceDetails(String serviceType, ServiceResourceIdentifier serviceID, String preferenceName){
		this.serviceID = serviceID;
		this.serviceType = serviceType;
		this.preferenceName = preferenceName;
	}

	/**
	 * 
	 * @param serviceType
	 * @param preferenceName
	 */
	public PreferenceDetails(String serviceType, String preferenceName){
		this.serviceType = serviceType;
		this.preferenceName = preferenceName;
	}

	/**
	 * 
	 * @param preferenceName
	 */
	public PreferenceDetails(String preferenceName){
		this.preferenceName = preferenceName;
	}

/*	*//**
	 * 
	 * @param details
	 *//*
	@Override
	public boolean equals(Object details){
		if (details instanceof PreferenceDetails){
			//does not compare serviceTypes anymore
			if (compareServiceID(((PreferenceDetails) details).getServiceID())){
				if (comparePreferenceName(((PreferenceDetails) details).getPreferenceName())){
					return true;
				}
			}

		}
		return false;
	}*/

/*	*//**
	 * 
	 * @param details
	 *//*
	public boolean equalsServiceOnlyDetails(PreferenceDetails details){

		if (compareServiceType(((PreferenceDetails) details).getServiceType())){
			if (compareServiceID(((PreferenceDetails) details).getServiceID())){

				return true;

			}
		}

		return false;
	}*/

/*	*//**
	 * 
	 * @param serviceType
	 * @param serviceID
	 *//*
	public boolean equalsServiceOnlyDetails(String serviceType, ServiceResourceIdentifier serviceID){
		if (compareServiceType(serviceType)){
			if (compareServiceID(serviceID)){

				return true;

			}
		}

		return false;
	}*/

	public String getPreferenceName(){
		return this.preferenceName;
	}

	public ServiceResourceIdentifier getServiceID(){
		return this.serviceID;
	}

	public String getServiceType(){
		return this.serviceType;
	}

	/**
	 * 
	 * @param preferenceName
	 */
	public void setPreferenceName(String preferenceName){
		this.preferenceName = preferenceName;
	}

	/**
	 * 
	 * @param serviceID
	 */
	public void setServiceID(ServiceResourceIdentifier serviceID){
		this.serviceID = serviceID;
	}

	/**
	 * 
	 * @param serviceType
	 */
	public void setServiceType(String serviceType){
		this.serviceType = serviceType;
	}

	public String toString(){
		String st;
		if (this.serviceType==null){
			st = "GenericType";
		}else{
			st = this.serviceType;
		}

		String sID;
		if (this.serviceID==null){
			sID="GenericID";
		}else{
			sID = ServiceModelUtils.serviceResourceIdentifierToString(serviceID);
			
		}


		return "ServiceType: "+st+"\n"
		+ "ServiceID: "+sID+"\n"
		+ "PreferenceName: "+this.preferenceName+"\n";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((preferenceName == null) ? 0 : preferenceName.hashCode());
		result = prime * result
				+ ((serviceID == null) ? 0 : serviceID.hashCode());
		result = prime * result
				+ ((serviceType == null) ? 0 : serviceType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PreferenceDetails)) {
			return false;
		}
		PreferenceDetails other = (PreferenceDetails) obj;
		if (preferenceName == null) {
			if (other.preferenceName != null) {
				return false;
			}
		} else if (!preferenceName.equals(other.preferenceName)) {
			return false;
		}
		if (serviceID == null) {
			if (other.serviceID != null) {
				return false;
			}
		} else if (!ServiceModelUtils.compare(serviceID, other.serviceID)) {
			return false;
		}
		if (serviceType == null) {
			if (other.serviceType != null) {
				return false;
			}
		} else if (!serviceType.equals(other.serviceType)) {
			return false;
		}
		return true;
	}



}
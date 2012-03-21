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

	}

	/**
	 * 
	 * @param serviceType
	 * @param preferenceName
	 */
	public PreferenceDetails(String serviceType, String preferenceName){

	}

	/**
	 * 
	 * @param preferenceName
	 */
	public PreferenceDetails(String preferenceName){

	}

	/**
	 * 
	 * @param prefName
	 */
	private boolean comparePreferenceName(String prefName){
		return false;
	}

	/**
	 * 
	 * @param sID
	 */
	private boolean compareServiceID(ServiceResourceIdentifier sID){
		return false;
	}

	/**
	 * 
	 * @param sType
	 */
	private boolean compareServiceType(String sType){
		return false;
	}

	/**
	 * 
	 * @param details
	 */
	@Override
	public boolean equals(Object details){
		return false;
	}

	/**
	 * 
	 * @param details
	 */
	public boolean equalsServiceOnlyDetails(PreferenceDetails details){
		return false;
	}

	/**
	 * 
	 * @param serviceType
	 * @param serviceID
	 */
	public boolean equalsServiceOnlyDetails(String serviceType, ServiceResourceIdentifier serviceID){
		return false;
	}

	public String getPreferenceName(){
		return "";
	}

	public ServiceResourceIdentifier getServiceID(){
		return null;
	}

	public String getServiceType(){
		return "";
	}

	/**
	 * 
	 * @param preferenceName
	 */
	public void setPreferenceName(String preferenceName){

	}

	/**
	 * 
	 * @param serviceID
	 */
	public void setServiceID(ServiceResourceIdentifier serviceID){

	}

	/**
	 * 
	 * @param serviceType
	 */
	public void setServiceType(String serviceType){

	}

	public String toString(){
		return "";
	}

}
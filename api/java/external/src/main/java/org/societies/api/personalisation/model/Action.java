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
package org.societies.api.personalisation.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;




/**
 * 
 * This class must be used to create action objects in order to send user actions
 * to the SOCIETIES platform using the IUserActionMonitor interface. 
 *
 * @author Eliza
 *
 */
public class Action implements IAction, Serializable{

	private String value;
	private String parameterName;
	private ArrayList<String> parameterNames;
	private ServiceResourceIdentifier serviceID;
	private String serviceType;
	private ArrayList<String> types;
	

	public Action(){
		this.serviceID = null;
		this.serviceType = "not_initialised";
		this.parameterName = "not_initialised";
		this.value = "not_initialised";
	}

	public Action(ServiceResourceIdentifier serviceID, String serviceType, String parameterName, String value){
		this.serviceID = serviceID;
		this.serviceType = serviceType;
		this.parameterName = parameterName;
		this.value = value;
	}
	
	public String getvalue(){
		return value;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setvalue(String newVal){
		value = newVal;
	}

	public String getparameterName(){
		return parameterName;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setparameterName(String newVal){
		parameterName = newVal;
	}

	/**
	 * 
	 * @param parameter
	 */
	public void addParameter(String parameter){
		this.parameterNames.add(parameter);

	}



	public ArrayList<String> getparameterNames(){
		return parameterNames;
	}



	public String toString(){
		return "ServiceID: "+this.serviceID.getIdentifier()+
				"\n"+"ServiceType: "+this.serviceType+
				"\n"+this.parameterName+" = "+this.value;
	}
	


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((parameterName == null) ? 0 : parameterName.hashCode());
		result = prime * result
				+ ((serviceID == null) ? 0 : serviceID.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Action)) {
			return false;
		}
		Action other = (Action) obj;
		if (parameterName == null) {
			if (other.parameterName != null) {
				return false;
			}
		} else if (!parameterName.equals(other.parameterName)) {
			return false;
		}
		if (serviceID == null) {
			if (other.serviceID != null) {
				return false;
			}
		} else if (!serviceID.equals(other.serviceID)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public ServiceResourceIdentifier getServiceID() {
		return this.serviceID;
	}

	@Override
	public String getServiceType() {
		return this.serviceType;
	}

	@Override
	public List<String> getServiceTypes() {
		return this.types;
	}


	@Override
	public void setServiceID(ServiceResourceIdentifier id) {
		this.serviceID = id;
		
	}

	@Override
	public void setServiceType(String type) {
		this.serviceType = type;
		
	}

	@Override
	public void setServiceTypes(List<String> sTypes) {
		this.types = (ArrayList<String>) sTypes;
		
	}



	
}


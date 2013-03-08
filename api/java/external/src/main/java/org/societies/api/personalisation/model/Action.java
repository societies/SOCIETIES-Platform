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
import org.societies.api.services.IServices;
import org.societies.api.services.ServiceUtils;




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
	private final boolean implementable;
	private final boolean contextDependent;
	private final boolean proactive;

	/**
	 * Not to be used by 3p services
	 */
	public Action(){
		this.serviceID = null;
		this.serviceType = "not_initialised";
		this.parameterName = "not_initialised";
		this.value = "not_initialised";
		this.implementable = false;
		this.contextDependent = false;
		this.proactive = false;
	}

	/**
	 * 
	 * By default, implementable, contextDependent and Proactive are set to true
	 * @param serviceID		the id of the 3p service
	 * @param serviceType	the type of service
	 * @param parameterName	the name of the parameter
	 * @param value			the value of the parameter
	 * 
	 */
	public Action(ServiceResourceIdentifier serviceID, String serviceType, String parameterName, String value){
		this.serviceID = serviceID;
		this.serviceType = serviceType;
		this.parameterName = parameterName;
		this.value = value;
		this.implementable = true;
		this.contextDependent = true;
		this.proactive = true;
	}
	
	public Action(ServiceResourceIdentifier serviceID, String serviceType, String parameterName, String value, boolean implementable, boolean contextDependent, boolean proactive){
		this.serviceID = serviceID;
		this.serviceType = serviceType;
		this.parameterName = parameterName;
		this.value = value;
		this.implementable = implementable;
		this.contextDependent = contextDependent;
		this.proactive = proactive;
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
		String sID;
		
		if (this.serviceID==null){
			sID = "Generic";
		}else{
			sID = this.serviceID.getServiceInstanceIdentifier();
		}
		
		String st;
		if (this.serviceType==null){
			st = "Generic";
		}else{
			st = this.serviceType;
		}
		return "ServiceID: "+sID+
				"\n "+"ServiceType: "+st+
				"\n "+this.parameterName+" = "+this.value;
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
		} else if (!ServiceUtils.compare(serviceID,other.serviceID)) {
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

	/**
	 * Indicates if this action can be implemented or is only used as a conditional action for 
	 * triggering User Intent sequences. These types of actions are created by the UAM and represent 
	 * actions such as joined/left CIS, started/stopped service etc. Other such functions of the 
	 * platform may be added in the future
	 * @return	true if the action can be implemented, false if not. 
	 */
	public boolean isImplementable() {
		return implementable;
	}

	/**
	 * Indicates whether this action should be implemented proactively (by the DecisionMaker) or not. 
	 * The user will have the ability to make this change manually using the preference GUI (T6.5 webapp Profile Settings).
	 * 3p services can also indicate if this action should be implemented when they create actions and send them to 
	 * the UAM component.  
	 * @return	true if the action should be proactively implemented, false if not. 
	 */
	public boolean isContextDependent() {
		return contextDependent;
	}
	
	/**
	 * Indicates whether this action should be learnt or stored as a static preference. 3p services can create 
	 * static preferences (such as configuration settings) that do not depend on changing context. The UAM makes sure not to 
	 * store this action in the context history and avoid learning on this.  
	 * @return	true if the action should be context dependent, false if it shouldn't. Note that this will not return false 
	 * if the action does not currently have context conditions attached but was created as contextDependent 
	 */
	public boolean isProactive() {
		return proactive;
	}



	
}


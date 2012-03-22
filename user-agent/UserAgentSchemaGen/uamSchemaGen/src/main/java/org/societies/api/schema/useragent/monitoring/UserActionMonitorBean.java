/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.api.schema.useragent.monitoring;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

public class UserActionMonitorBean {

	public enum methodType {monitor, registerForActionUpdates};
	private methodType method;
	private String owner; //IIdentity
	private ServiceResourceIdentifier serviceId;
	private String serviceType;
	private String parameterName; //IAction.parameterName
	private String value;  //IAction.value
	//private IUserActionListener listener;

	public methodType getMethod(){
		return method;
	}

	public void setMethod(methodType method){
		this.method = method;
	}
	
	public String getIdentity(){
		return this.owner;
	}
	
	public void setIdentity(String owner){
		this.owner = owner;
	}
	
	public ServiceResourceIdentifier getServiceResourceIdentifier(){
		return this.serviceId;
	}
	
	public void setServiceResourceIdentifier(ServiceResourceIdentifier serviceId){
		this.serviceId = serviceId;
	}
	
	public String getServiceType(){
		return this.serviceType;
	}
	
	public void setServiceType(String serviceType){
		this.serviceType = serviceType;
	}
	
	public String getParameterName(){
		return this.parameterName;
	}
	
	public void setParameterName(String parameterName){
		this.parameterName = parameterName;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public void setValue(String value){
		this.value = value;
	}
}

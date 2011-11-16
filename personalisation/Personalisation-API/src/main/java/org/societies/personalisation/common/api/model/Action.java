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
package org.societies.personalisation.common.api.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;


public class Action implements IAction, Serializable{

	private String value;
	private String parameterName;
	private ArrayList<String> parameterNames;
	private ServiceResourceIdentifier serviceID;
	private String serviceType;
	private ArrayList<String> types;
	
	private int confidenceLevel;

	public Action(){
		this.parameterName = "not_initialised";
		this.value = "not_initialised";
	}
	public Action(String par, String val){
		this.parameterName = par;
		this.value = val;
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
		return this.parameterName+"="+this.value;
	}
	
	public boolean equals(IAction po){
		
		String par = po.getparameterName();
		String val = po.getvalue();
		
		if (this.parameterName.equalsIgnoreCase(par) && this.value.equalsIgnoreCase(val)){
			return true;
		}
		return false;
		
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


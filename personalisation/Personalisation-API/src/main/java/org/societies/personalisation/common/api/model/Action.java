
package org.societies.personalisation.common.api;

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


package org.societies.personalisation.common.api.model;

import java.io.Serializable;

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
package org.societies.personalisation.common.api.model;

/**
 * Class that links a context condition that affects a specific PreferenceName
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:56
 */
public class IPreferenceConditionIOutcomeName {

	private ICtxAttributeIdentifier ctxIdentifier;
	private String prefName;

	public IPreferenceConditionIOutcomeName(){

	}

	public void finalize() throws Throwable {

	}

	/**
	 * 
	 * @param id
	 * @param preferenceName
	 */
	public IPreferenceConditionIOutcomeName(ICtxAttributeIdentifier id, String preferenceName){

	}

	/**
	 * Method to get the context identifier of the condition that affects this
	 * preference
	 * @return 	the context identifier
	 */
	public ICtxAttributeIdentifier getICtxIdentifier(){
		return null;
	}

	/**
	 * Method to get the name of the outcome affected
	 * @return	the name of the preference
	 */
	public String getPreferenceName(){
		return "";
	}

}
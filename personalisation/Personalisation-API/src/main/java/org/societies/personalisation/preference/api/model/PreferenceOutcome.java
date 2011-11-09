package org.societies.personalisation.preference.api.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.societies.personalisation.common.api.model.Action;
import org.societies.personalisation.common.api.model.IOutcome;



/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:57
 */
public class PreferenceOutcome extends Action implements IPreferenceOutcome, Serializable {

	private int confidenceLevel;
	private ArrayList<String> parameterNames;
	private IQualityofPreference qop;

	public PreferenceOutcome(){
		super();
	}

	public PreferenceOutcome(String par, String val){
		super(par,val);
		this.confidenceLevel = 51;
	}


	/**
	 * Method to set the confidence level
	 * @param confidenceLevel
	 */
	public void setConfidenceLevel(int confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}

	/**
	 * @see IOutcome#getConfidenceLevel()
	 */
	public int getConfidenceLevel() {
		return confidenceLevel;
	}


	/* (non-Javadoc)
	 * @see org.personalsmartspace.pm.prefmodel.api.platform.IOutcome#getQualityofPreference()
	 */
	@Override
	public IQualityofPreference getQualityofPreference() {
		return this.qop;
	}
	
	public void setQualityofPreference(IQualityofPreference qop){
		this.qop = qop;
	}
	


}
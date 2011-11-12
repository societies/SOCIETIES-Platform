
package org.societies.privacytrust.privacyprotection.api.model.preference;
import java.io.Serializable;

import org.societies.privacytrust.privacyprotection.api.model.preference.constants.PrivacyConditionConstants;

/**
 * @author Elizabeth
 * @version 1.0
 * @updated 14-Mar-2009 21:06:49
 */
public class TrustPreferenceCondition implements IPrivacyPreferenceCondition, Serializable {

	/*
	private ITrustValue trustValue;
	private PrivacyConditionConstants myType;
	
	public TrustPreferenceCondition(ITrustValue trustVal){
		this.trustValue = trustVal;
		this.myType = PrivacyConditionConstants.TRUST;
	}

	public ITrustValue getTrustValue(){
		return this.trustValue;
	}
	

	public boolean equals(IPrivacyPreferenceCondition pc){

		if (!(pc instanceof TrustPreferenceCondition)){
			return false;
		}
		TrustPreferenceCondition tpc = (TrustPreferenceCondition) pc;
		if (tpc.getTrustValue().compareTo(this.getTrustValue())!=0){
			return false;
		}

		
		return true;
	}

*/
	@Override
	public PrivacyConditionConstants getType() {
		return PrivacyConditionConstants.TRUST; 
	}


}
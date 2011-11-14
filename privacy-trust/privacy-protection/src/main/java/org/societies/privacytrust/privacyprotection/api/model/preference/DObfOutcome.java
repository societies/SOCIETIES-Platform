package org.societies.privacytrust.privacyprotection.api.model.preference;

import org.societies.privacytrust.privacyprotection.api.model.preference.constants.PrivacyPreferenceTypeConstants;



/**
 * This class is used to define the level of obfuscation that has to be applied to
 * a context attribute before being disclosed to an external entity.
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 17:06:54
 */
public class DObfOutcome implements IPrivacyOutcome, IDObfAction {

	public DObfOutcome(){

	}

	public void finalize() throws Throwable {

	}

	public int getConfidenceLevel(){
		return 0;
	}

	public PrivacyPreferenceTypeConstants getOutcomeType(){
		return PrivacyPreferenceTypeConstants.DOBF;
	}

	public int getObfuscationLevel(){
		return 0;
	}

}
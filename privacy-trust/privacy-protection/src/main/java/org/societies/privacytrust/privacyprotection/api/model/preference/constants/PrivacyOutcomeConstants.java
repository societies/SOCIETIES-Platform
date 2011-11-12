package org.societies.privacytrust.privacyprotection.api.model.preference.constants;

/**
 * @author Elizabeth
 *
 */
public enum PrivacyOutcomeConstants {
	
	ALLOW(1), BLOCK(0);
	
	private int decision;
	PrivacyOutcomeConstants(int i){
		this.decision = i;
	}


	public int getInt(){
		return this.decision;
	}
}

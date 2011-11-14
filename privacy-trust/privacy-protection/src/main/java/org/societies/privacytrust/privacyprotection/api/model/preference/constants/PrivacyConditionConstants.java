package org.societies.privacytrust.privacyprotection.api.model.preference.constants;

/**
 * @author Elizabeth
 *
 */
public enum PrivacyConditionConstants {
	
	CONTEXT ("context"), TRUST("trust");

	private String type;
	PrivacyConditionConstants(String type){
		this.type = type;
	}
}

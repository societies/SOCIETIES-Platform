package org.societies.privacytrust.privacyprotection.api.model.preference.constants;

/**
 * @author Elizabeth
 *
 */
public enum PrivacyPreferenceTypeConstants {
	
	IDS("ids"), PPNP("ppnp"), DOBF("dobf");
	
	private String type;
	
	PrivacyPreferenceTypeConstants(String str){
		this.type = str;
	}

}

package org.societies.privacytrust.privacyprotection.api.model.preference.constants;

/**
 * @author Elizabeth
 *
 */
public enum TargetMatchConstants {

	SUBJECT(0),RESOURCE(1),ACTION(2);
	
	int i = -1;
	TargetMatchConstants(int i){
		this.i=i;
	}
	
	public int getInt(){
		return this.i;
	}
}

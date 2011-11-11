package org.societies.privacytrust.privacyprotection.api.model.privacyPolicy;

/**
 * @author Elizabeth
 *
 */
public enum NegotiationStatus {

	SUCCESSFUL("successful"), FAILED("failed"), ONGOING("ongoing");
	
	private String status;
	NegotiationStatus(String status){
		this.status = status;
	}
}

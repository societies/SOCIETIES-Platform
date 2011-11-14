package org.societies.privacytrust.privacyprotection.api.model.preference;


import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.preference.constants.PrivacyPreferenceTypeConstants;



/**
 * This class is used to define that a CSS identity should be used in a specific transaction if the preceding IPrivacyPreferenceConditions are true. 
 * The format of the identity will be defined by the Identity Management component
 * @author Elizabeth
 *
 */
public class IdentitySelectionPreferenceOutcome implements IPrivacyOutcome{

	private int confidenceLevel;
	private EntityIdentifier dpi;
	private ServiceResourceIdentifier serviceID;
	private Subject requestor;
	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyOutcome#getOutcomeType()
	 */
	@Override
	public PrivacyPreferenceTypeConstants getOutcomeType() {
		return PrivacyPreferenceTypeConstants.IDS;
	}
	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyOutcome#getConfidenceLevel()
	 */
	@Override
	public int getConfidenceLevel() {
		return this.confidenceLevel;
	}
	
	public void setConfidenceLevel(int c){
		this.confidenceLevel = c;
	}
	
	public void setIdentity(EntityIdentifier dpi){
		this.dpi = dpi;
	}
	
	public EntityIdentifier getIdentity(){
		return this.dpi;
	}
	public void setServiceID(ServiceResourceIdentifier serviceID) {
		this.serviceID = serviceID;
	}
	public ServiceResourceIdentifier getServiceID() {
		return serviceID;
	}
	public void setRequestor(Subject requestor) {
		this.requestor = requestor;
	}
	public Subject getRequestor() {
		return requestor;
	}
	
	public String toString(){
		return "Select: "+this.dpi.toString();
	}
	
}


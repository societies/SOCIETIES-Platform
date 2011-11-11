package org.societies.privacytrust.privacyprotection.api.model.privacyPolicy;


import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;

public interface INegotiationAgent {

	//public IAgreement startNegotiation(String negAgentURI, String serviceID, String serviceType);
	
	/**
	 * this method is called by the client and informs the provider that it wants to initiate a negotiation process for the specified
	 * serviceID and provides its policy which is a response to the provider's advertised privacy policy. this method can be called a number of times
	 * until the ResponsePolicy.getStatus method returns SUCCESSFUL or FAILED status.
	 * @param serviceID		the service identifier for which the negotiation is going to be performed
	 * @param policy		the ResponsePolicy to the provider's privacy policy
	 * @return 				the ResponsePolicy to the client's privacy policy.
	 */
	public ResponsePolicy negotiate(ServiceResourceIdentifier serviceID, ResponsePolicy policy);
	
	/**
	 * this method is called by any PSS that wants to read the service's provider RequestPolicy for a specific service it provides
	 * 
	 * @param serviceID 	the service identifier of the service for which the negotiation will be performed
	 * @return				the policy of the service provider in the format of RequestPolicy
	 * 
	 */
	public RequestPolicy getPolicy(ServiceResourceIdentifier serviceID);
	
	/**
	 * This method is called by any PSS to get the Identity of the service provider. This is needed to do trust evaluation and 
	 * evaluation of privacy preferences where applicable
	 * 
	 * @return				the identity of the service provider
	 */
	public EntityIdentifier getProviderDPI();
	
	/**
	 * this method is called by the client and asks the provider to acknowledge the agreement document.
	 * 
	 * @param contract		the agreement to acknowledge
	 * @return				true if acknowledged, false if not acknowledged.
	 */
	public boolean acknowledgeAgreement(IAgreementEnvelope contract);
	
	
}


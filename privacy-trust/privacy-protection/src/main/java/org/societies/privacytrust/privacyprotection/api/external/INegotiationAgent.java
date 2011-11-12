package org.societies.privacytrust.privacyprotection.api.external;


import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.IAgreementEnvelope;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.RequestPolicy;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.ResponsePolicy;
/**
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 18:55:01
 */
public interface INegotiationAgent {

	/**
	 * this method is called by the client and asks the provider to acknowledge the
	 * agreement document.
	 * @return				true if acknowledged, false if not acknowledged.
	 * 
	 * @param contract    the agreement to acknowledge
	 */
	public boolean acknowledgeAgreement(IAgreementEnvelope contract);

	/**
	 * this method is called by any PSS that wants to read the service's provider
	 * RequestPolicy for a specific service it provides
	 * @return				the policy of the service provider in the format of RequestPolicy
	 * 
	 * @param serviceID    the service identifier of the service for which the
	 * negotiation will be performed
	 */
	public RequestPolicy getPolicy(ServiceResourceIdentifier serviceID);

	/**
	 * This method is called by any PSS to get the Identity of the service provider.
	 * This is needed to do trust evaluation and evaluation of privacy preferences
	 * where applicable
	 * @return				the identity of the service provider
	 */
	public EntityIdentifier getProviderIdentity();

	/**
	 * this method is called by the client and informs the provider that it wants to
	 * initiate a negotiation process for the specified serviceID and provides its
	 * policy which is a response to the provider's advertised privacy policy. this
	 * method can be called a number of times until the ResponsePolicy.getStatus
	 * method returns SUCCESSFUL or FAILED status.
	 * @return 				the ResponsePolicy to the client's privacy policy.
	 * 
	 * @param serviceID    the service identifier for which the negotiation is going
	 * to be performed
	 * @param policy    the ResponsePolicy to the provider's privacy policy
	 */
	public ResponsePolicy negotiate(ServiceResourceIdentifier serviceID, ResponsePolicy policy);

}
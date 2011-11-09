package org.societies.privacytrust.privacyprotection.api.external;

/**
 * @author olivierm
 * @version 1.0
 * @created 08-nov.-2011 17:13:16
 */
public interface INegotiationAgent {

	/**
	 * this method is called by the client and asks the provider to acknowledge the
	 * agreement document.
	 * @return				true if acknowledged, false if not acknowledged.
	 * 
	 * @param contract    the agreement to acknowledge
	 */
	public boolean acknowledgeAgreement(Object contract);

	/**
	 * this method is called by any PSS that wants to read the service's provider
	 * RequestPolicy for a specific service it provides
	 * @return				the policy of the service provider in the format of RequestPolicy
	 * 
	 * @param serviceID    the service identifier of the service for which the
	 * negotiation will be performed
	 */
	public Object getPolicy(Object serviceID);

	/**
	 * This method is called by any PSS to get the Identity of the service provider.
	 * This is needed to do trust evaluation and evaluation of privacy preferences
	 * where applicable
	 * @return				the identity of the service provider
	 */
	public Object getProviderIdentity();

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
	public Object negotiate(Object serviceID, Object policy);

}
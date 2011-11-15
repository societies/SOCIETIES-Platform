package org.societies.security.policynegotiator.api;

/**
 * Interface for invoking the requester.
 * To be used by other components on same node.
 * 
 * @author Mitja Vardjan
 *
 */
public interface INegotiationRequester {

	/**
	 * Get all available options for the policy.
	 * 
	 * @param callback The callback to be invoked to return the result.
	 * 
	 * @return All available options embedded in a single XML document.
	 */
	public void getPolicyOptions(INegotiationRequesterCallback callback);

	/**
	 * Accept given policy option unchanged, as provided by the provider side.
	 * Alternatively, {@link negotiatePolicy(ResponsePolicy)} can be used
	 * to try to negotiate a different policy if none of the options are
	 * acceptable.
	 * 
	 * @param signedPolicyOption The selected policy alternative, accepted and
	 * signed by the requester side. Includes requester identity and signature.
	 */
	public void acceptPolicy(String signedPolicyOption,
			INegotiationRequesterCallback callback);
	
	/**
	 * Further negotiate given policy option. If any of the policy options
	 * given by the provider suits the requester, then {@link acceptPolicy(String)}
	 * should be used instead in order to save bandwidth and increase chances
	 * of successful negotiation.
	 * 
	 * @param policyOptionId ID of the option the requester side chose as a
	 * basis for further negotiation.
	 * 
	 * @param modifiedPolicy Policy modified by requester side. The policy is
	 * to be offered to the provider. It does not include the requester
	 * identity nor signature (TBC). 
	 */
	public void negotiatePolicy(int policyOptionId, ResponsePolicy modifiedPolicy,
			INegotiationRequesterCallback callback);
	
	/**
	 * Reject all options and terminate negotiation.
	 */
	public void reject();

}

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
	 * Get all available options for SLA.
	 * 
	 * @param callback The callback to be invoked to return the result.
	 * 
	 * @return All available options embedded in a single XML document.
	 */
	public void getPolicyOptions(INegotiationRequesterInternal callback);

	/**
	 * Accept given policy option unchanged, as provided by the provider side.
	 * Alternatively, {@link negotiatePolicy(int, ResponsePolicy)} can be used
	 * to try to negotiate a different policy if none of the options are
	 * acceptable.
	 * 
	 * @param policyOptionId ID of the option accepted by the requestor side.
	 */
	public void acceptPolicy(int policyOptionId,
			INegotiationRequesterInternal callback);
	
	/**
	 * Further negotiate given policy option. If any of the policy options
	 * given by the provider suits the requestor, then {@link acceptPolicy(int)}
	 * should be used instead in order to save bandwidth and increase chances
	 * of successful negotiation.
	 * 
	 * @param policyOptionId ID of the option the requestor side chose as a
	 * basis for further negotiation.
	 * 
	 * @param modifiedPolicy Policy modified by requester side. The policy is
	 * to be offered to the provider.
	 */
	public void negotiatePolicy(int policyOptionId, ResponsePolicy modifiedPolicy,
			INegotiationRequesterInternal callback);
	
	/**
	 * Reject all options and terminate negotiation.
	 */
	public void reject();

}

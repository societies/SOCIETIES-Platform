package org.societies.security.policynegotiator.api;

/**
 * Interface for invoking the requester side (either the policy negotiator or
 * some other component).
 * To be used by policy negotiator from the provider side (from some other node).
 * 
 * @author Mitja Vardjan
 *
 */
public interface INegotiationRequesterCallback {

	/**
	 * Async return for
	 * {@link INegotiationRequester#getPolicyOptions(INegotiationRequesterCallback)}.
	 * 
	 * @param sops All available options for policy, embedded in a single XML document.
	 */
	public void onSignedPolicyOptions(String sops);
	
	/**
	 * Async return for
	 * {@link INegotiationRequester#negotiatePolicy(int, ResponsePolicy, INegotiationRequesterCallback)}.
	 * 
	 * @param modifiedPolicy Policy possibly modified by provider side.
	 * Based on the policy sent before by the requester side.
	 */
	public void onNegotiationResponse(ResponsePolicy modifiedPolicy);
	
	/**
	 * Async return for
	 * {@link INegotiationRequester#acceptPolicy(int, ResponsePolicy, INegotiationRequesterCallback)}.
	 * 
	 * @param policy XML-based final policy signed by both parties.
	 */
	public void onFinalPolicy(String policy);
}

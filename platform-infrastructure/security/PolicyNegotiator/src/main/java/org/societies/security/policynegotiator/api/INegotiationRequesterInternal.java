package org.societies.security.policynegotiator.api;

/**
 * Interface for invoking the requester side (either the policy negotiator or
 * some other component).
 * To be used by policy negotiator from the provider side (from some other node).
 * 
 * @author Mitja Vardjan
 *
 */
public interface INegotiationRequesterInternal {

	/**
	 * Async return for
	 * {@link INegotiationRequester#getPolicyOptions(INegotiationRequesterInternal)}.
	 * 
	 * @param sops All available options for SLA, embedded in a single XML document.
	 */
	public void returnSignedPolicyOptions(String sops);
	
	/**
	 * Async return for
	 * {@link INegotiationRequester#negotiatePolicy(int, ResponsePolicy, INegotiationRequesterInternal)}.
	 * 
	 * @param modifiedPolicy Policy possibly modified by provider side.
	 * Based on the policy sent before by the requester side.
	 */
	public void returnNegotiationResponse(ResponsePolicy modifiedPolicy);
	
	/**
	 * Async return for
	 * {@link INegotiationRequester#acceptPolicy(int, ResponsePolicy, INegotiationRequesterInternal)}.
	 * 
	 * @param sla XML-based final SLA signed by both parties.
	 */
	public void returnSla(String sla);
}

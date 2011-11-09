package org.societies.security.policynegotiator.api;

/**
 * Interface for invoking the provider.
 * To be used by generic secure policy negotiator on the requester side.
 * 
 * @author Mitja Vardjan
 *
 */
public interface INegotiationProvider extends INegotiationRequester {
	
	/**
	 * Get final policy.
	 * 
	 * @param policySignedByRequester The option accepted by the requestor side.
	 * Includes requester identity and signature.
	 * 
	 * @return Final policy, signed by both parties.
	 */
	public String getFinalPolicy(String policySignedByRequester);
}

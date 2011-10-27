package org.societies.security.policynegotiator.api;

/**
 * Interface for invoking the requester.
 * To be used by same component from the provider side (from some other node).
 * 
 * @author Mitja Vardjan
 *
 */
public interface INegotiationRequesterInternal {

	/**
	 * Async return for {@link INegotiationRequester#getPolicyOptions(Object)}.
	 * 
	 * @param sops All available options for SLA.
	 */
	public void returnSignedPolicyOptions(String sops);
	
}

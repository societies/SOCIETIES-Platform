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
	 * Accept given policy option.
	 * 
	 * @param policyOptionId ID of the option accepted by the requestor side.
	 */
	public void acceptPolicy(int policyOptionId);
	
	/**
	 * Get all available options for SLA.
	 * 
	 * @param callback The callback to be invoked to return the result.
	 * 
	 * @return All available options embedded in a single XML document.
	 */
	public void getPolicyOptions(Object callback);
	
	/**
	 * Reject all options and terminate negotiation.
	 */
	public void rejectPolicy();

}

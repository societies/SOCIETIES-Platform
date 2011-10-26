package org.societies.security.policynegotiator.api;

public interface INegotiationProvider {

	/**
	 * Get final SLA.
	 * 
	 * @param policySignedByRequestor The option accepted by the requestor side.
	 * Includes requestor identity and signature.
	 * 
	 * @return Final SLA, signed by both parties.
	 */
	public String getFinalPolicy(String policySignedByRequestor);
	
	/**
	 * Get all available options for SLA.
	 * 
	 * @return All available options embedded in a single XML document.
	 */
	public String getPolicyOptions();
	
	/**
	 * Reject all options and terminate negotiation.
	 */
	public void reject();

}

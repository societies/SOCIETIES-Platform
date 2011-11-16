package org.societies.personalisation.CRIST.api.CRISTCommunityIntentDiscovery;

/**
 * 
 * @author Zhu WANG
 * @version 1.0
 *
 */
public interface ICRISTCommunityIntentDiscovery {

	/**
	 * This method will enable/disable the CRIST Community Intent Model Discovery function which is based on the
	 * historical action records and situations.
	 * 
	 *  @param bool		- true to enable and false to disable
	 */
	public void enableCRISTCIDiscovery(boolean bool);
	
	/**
	 * This method will generate a new CRIST Community Intent Model based on the current available
	 * information of all the community members
	 */
	public void generateNewCRISTCIModel();
}

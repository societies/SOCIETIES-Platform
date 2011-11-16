package org.societies.personalisation.CRIST.api.CRISTUserIntentDiscovery;

/**
 * 
 * @author Zhu WANG
 * @version 1.0
 * @created 14-Nov-2011 16:38:20
 *
 */
public interface ICRISTUserIntentDiscovery {

	/**
	 * This method will enable/disable the CRIST User Intent Model Discovery function which is based on the
	 * historical action records and situations.
	 * 
	 *  @param bool		- true to enable and false to disable
	 */
	public void enableCRISTUIDiscovery(boolean bool);
	
	/**
	 * This method will generate a new CRIST User Intent Model based on the current available
	 * information, i.e., action records, situations records and context records
	 */
	public void generateNewCRISTUIModel();
}

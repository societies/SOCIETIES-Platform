package org.societies.personalisation.CRIST.api.CRISTCommunityIntentDiscovery;

import org.societies.personalisation.CRIST.api.model.ICRISTCommunityAction;
/**
 * 
 * @author Zhu WANG
 * @version 1.0
 *
 */
public interface ICRISTCommunityIntentDetection {

	/**
	 * This method will enable/disable the CRIST Community Intent Detection function
	 * 
	 *  @param bool		- true to enable and false to disable
	 */
	public void enableCRISTCIDetection(boolean bool);
	
	/**
	 * This method will retrieve the current community intent
	 */
	public ICRISTCommunityAction detectCRISTCommunityIntent();
}

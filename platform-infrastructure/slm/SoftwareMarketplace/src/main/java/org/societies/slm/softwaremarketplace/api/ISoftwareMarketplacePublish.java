/**
 * This MarketPlace API provides the functionalities of publication of software
 * that can be installed on a CSS node to access 3rd party services.
 * This API is used by developers or 3rd party service providers.
 * 
 * @author Jacqueline.Floch@sintef.no
 * @version 0.0.1
 * 
 * 
 */

package org.societies.slm.softwaremarketplace.api;

public interface ISoftwareMarketplacePublish {
	
	String publishSoftware (byte[] installableService, String serviceProperties [], String serviceURL); /* returns a unique ID */
	boolean unpublishSoftware (String serviceID);

	/** postponed later prototype
	 * 	String changePublishedService (String serviceID, byte[] installableService, String properties [], String serviceURL);
	 */	
}

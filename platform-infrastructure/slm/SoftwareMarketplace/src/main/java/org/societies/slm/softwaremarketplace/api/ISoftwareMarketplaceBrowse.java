/**
 * This MarketPlace API supports the functionalities of search, select, and download
 * software allowing to access 3rd party services.
 * 
 * @author Jacqueline.Floch@sintef.no
 * @version 0.0.1
 * 
 * 
 */

package org.societies.slm.softwaremarketplace.api;

public interface ISoftwareMarketplaceBrowse {
	
	void searchSoftware (String[] serviceProperties, String queryResult);
	byte[] selectAndInstallSoftware (String softwareId);
	

	/** postponed later prototype
	 *  void payForService (String service Manifest) -  */	

}
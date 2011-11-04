/**
 * This MarketPlace API supports the functionalities of search, select, and download.
 * 
 * @author Jacqueline.Floch@sintef.no
 * @version 0.0.1
 * 
 * 
 */

package org.societies.slm.softwaremarketplace.api;

public interface ISoftwareMarketplaceBrowse {
	
	void searchServices (String serviceProperties [], String queryResult);
	byte[] selectAndInstallService (String serviceId);

	/** postponed later prototype
	 *  void payForService (String service Manifest) -  */
	

}
/**
 * This MarketPlace API supports the functionalities of update for installed SW.
 * 
 * @author Jacqueline.Floch@sintef.no
 * @version 0.0.1
 * 
 * 
 */

package org.societies.slm.softwaremarketplace.api;

public interface ISoftwareMarketplaceUpdate {
	
	void checkServiceUpdate (String serviceID [], String queryResult);
	byte [] selectAndInstallServiceUdapte (String serviceId);
	byte [] installAllServiceUdapte (String serviceId []);
	
	void registerServiceUpdate (String ListenerID, String serviceID [], ISofttwareMarketplaceUpdateListener iUpdateListener);
	void unregisterServiceUpdate (String ListenerID);

}

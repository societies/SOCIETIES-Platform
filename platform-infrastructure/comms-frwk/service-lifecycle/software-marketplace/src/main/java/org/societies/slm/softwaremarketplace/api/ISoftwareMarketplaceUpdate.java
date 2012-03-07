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
	
	void checkSoftwareUpdate (String[] serviceID, String queryResult);
	byte [] selectAndInstallSofwtareUdapte (String serviceId);
	byte [] installAllSoftwareeUdapte (String[] serviceId);
	
	void registerSoftwareUpdate (String ListenerID, String[] serviceID, ISoftwareMarketplaceUpdateListener iUpdateListener);
	void unregisterSoftwareUpdate (String ListenerID);

}

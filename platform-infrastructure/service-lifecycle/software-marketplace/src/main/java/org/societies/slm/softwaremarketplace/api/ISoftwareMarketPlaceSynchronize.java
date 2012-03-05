/**
 * This MarketPlace API supports the synchronization of contents between two marketplaces,
 * either client-cloud or instances in the cloud.
 * 
 * @author Jacqueline.Floch@sintef.no
 * @version 0.0.1
 * 
 * 
 */

package org.societies.slm.softwaremarketplace.api;

public interface ISoftwareMarketPlaceSynchronize {
		
	void registerSoftwareChange (String ListenerID, String[] serviceProperties, ISoftwareMarketPlaceSynchronizeListener iSynchronizeListener);
	void unregisterSoftwareChange (String ListenerID);

}

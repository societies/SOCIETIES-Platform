/**
 * This MarketPlace API supports the synchronization of contents between two marketplaces.
 * The interface is realized by the listener to changes. 
 * 
 * @author Jacqueline.Floch@sintef.no
 * @version 0.0.1
 * 
 * 
 */

package org.societies.slm.softwaremarketplace.api;

public interface ISoftwareMarketPlaceSynchronizeListener {

	boolean NotifySoftwareChange (String[] serviceId);
}

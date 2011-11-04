/**
 * This MarketPlace API supports the functionalities of update for installed SW.
 * The interface is realized by the listener to updates. 
 * 
 * @author Jacqueline.Floch@sintef.no
 * @version 0.0.1
 * 
 * 
 */

package org.societies.slm.softwaremarketplace.api;

public interface ISofttwareMarketplaceUpdateListener {
	
	boolean NotifyServiceUdapte (String serviceId []);

}

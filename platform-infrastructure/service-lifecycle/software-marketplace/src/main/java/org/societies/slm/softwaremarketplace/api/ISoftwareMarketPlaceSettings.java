/**
 * This MarketPlace API supports the configuration of a marketplace.
 * It is provided to the owner of a MarkeptPlace (e.g. CSS user for the MarketPlace client
 * or administrator of a MarketPlace in the Cloud).
 * 
 * @author Jacqueline.Floch@sintef.no
 * @version 0.0.1
 * 
 * 
 */

package org.societies.slm.softwaremarketplace.api;

public interface ISoftwareMarketPlaceSettings {

	void setSoftwareUpdatesettings (String[] MarketPlaceID);
	void setSoftwareUpdateMode (boolean automaticUpdateOnOff);

	void setSoftwareSynchronizeSettings (String[] MarketPlaceID);
	void setSoftwareSynchronizeFrequency (Integer time);

}

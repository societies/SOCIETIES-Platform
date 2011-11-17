package org.societies.device.api;


/**
 * 
 * Interface for a GPS OS device API. The methods represents the basic functionalities any GPS device offers.  
 * The implementor will use the OS device driver to produce the results. 
 *
 */
public interface GpsDeviceAPI {
	
	GpsLocation getLastKnowLocation();

}

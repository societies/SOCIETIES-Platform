package org.societies.device.impl;

import org.societies.device.api.GpsLocation;
import org.societies.device.api.GpsDeviceAPI;

public class AndriodGpsDeviceAdapter extends AndriodOSDeviceAPI implements GpsDeviceAPI{

	@Override
	public GpsLocation getLastKnowLocation() {
		
		/*
		 * The method will do something like this: 
		 * 
		 * 		LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		 *		String locationProvider = LocationManager.GPS_PROVIDER;
		 * 		Location lastKnownLocation = mlocManager.getLastKnownLocation(locationProvider);
		 * 		//use the location object to initialize the "GpsLocation" object
		 */
		
		return null;
	}

	
}

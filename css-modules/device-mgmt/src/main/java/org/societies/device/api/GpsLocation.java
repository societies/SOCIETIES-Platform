package org.societies.device.api;

/**
 * 
 * Interface for a GPS device.
 *
 */
public interface GpsLocation {
	
	/*
	 * 
	 * @Return
	 */
	double getLatitude();
	
	/*
	 * 
	 * @Return
	 */
	double getLongtitude();
	
	/*
	 * 
	 * @Return
	 */
	float getAccuracy();
	
	/*
	 * 
	 * @return
	 */
	double getBearing();
	
	/*
	 * 
	 * @return
	 */
	String getProvider();
	
	/*
	 * 
	 * @return
	 */
	float getSpeed();
}

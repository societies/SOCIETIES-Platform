package org.societies.api.internal.css.management;

import java.util.List;

public interface ICssNodeClient {

	/**
         *@return boolean true if CSS successfully logged in
	 *
	 */
	boolean loginCSS(String username, String password, String deviceIdentifier, String cssIdentifier);	

	/**
         *@return boolean true if CSS successfully logged out
	 *
	 */
	boolean logoutCSS(String username, String password, String deviceIdentifier, String cssIdentifier);	
	
	/**
	 * 
	 * @return List of current device configuration
	 * Information returned could be installed and/or missing 
	 * Societies components
	 */
	List checkDeviceConfiguration();
	/**
	 * 
	 * @return List of potential devices not currently 
	 * CSS nodes
	 */
	List cssNodeDiscovery();
	/**
	 * 
	 * @return List of CSS information to 
	 * query from another user's device currently 
	 * unregistered in the CSS. 
	 * TODO How is an unregistered device validated as being
	 * a user's device and not another random device ?
	 * 
	 */
	List respondToCSSNodeDiscovery();
	/**
	 * 
	 * @return List of CSS information having registered the current
	 * device as a CSS node.
	 */
	List registerCSSNode();
	/**
	 * 
	 * @return boolean
	 * Unregister the device 
	 */
	boolean unregisterCSSNode();
	/**
	 * @return boolean
	 * Change a CSS node device status 
	 * TODO Do these status codes exist and/or are they required ?
	 * Are the only two status codes registered and unregistered ? 
	 * Hibernation due to battery levels but still availabe for emergencies
	 * may be another status.
	 */
	boolean changeCSSNodeStatus();
}

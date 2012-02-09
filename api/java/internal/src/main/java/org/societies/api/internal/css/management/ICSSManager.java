package org.societies.api.internal.css.management;


/**
 * 
 * This interface forms the basis of the CSSManager, a component that will exist on all nodes. Its main tasks are:
 * 
 * 1. Allow a user to register or unregister a CSS
 * 2. Allow a user to login into a CSS
 * 3. Allow a user to logout out of a CSS
 * 3. Add and remove nodes that make up a CSS
 * 4. Get the current CSS profile
 * 5. Allow the status of a device to be changed
 * 6. Synchronise profile data
 * 7. Modify the CSS profile 
 * 
 * This interface will be implemented for rich and cloud nodes. It is assumed that methods
 * that modify the CSS profile will actually be implemented on the cloud node. As a result, 
 * there will be two implementations of this interface.
 *
 */
public interface ICSSManager {

	/**
	 * Login a user to a CSS. The registration of devices included in the profile
	 * is implied.
	 * 
	 * @param profile
	 * @param callback
	 * @return boolean operation successful
	 */
	boolean loginCSS(ICSSProfile profile, ICSSManagerCallback callback);	

	/**
	 * Logout the user from a CSS
	 * 
	 * @param profile
	 * @param callback
	 * @return boolean operation successful
	 */
	boolean logoutCSS(ICSSProfile profile, ICSSManagerCallback callback);	
	
	/**
	 * Register a CSS
	 * 
	 * @param profile
	 * @param callback
	 * @return boolean operation successful
	 */
	boolean registerCSS(ICSSProfile profile, ICSSManagerCallback callback);

	/**
	 * Unregister the CSS
	 * TODO Is a CSS deleted or made inactive
	 * 
	 * @param profile
	 * @param callback
	 * @return boolean operation successful
	 */
	boolean unregisterCSS(ICSSProfile profile, ICSSManagerCallback callback);

	/**
	 * Register a device(s) with a CSS
	 * 
	 * @param profile containing device(s) to register with CSS
	 * @param callback
	 * @return boolean operation successful
	 */
	boolean registerCSSDevice(ICSSProfile profile, ICSSManagerCallback callback);
	/**
	 * Unregister a device(s) from a CSS
	 * 
	 * @param profile containing device(s) to unregister with CSS
	 * @param callback
	 * @return boolean operation successful
	 */
	boolean unregisterCSSDevice(ICSSProfile profile, ICSSManagerCallback callback);
	
	/**
	 * Get the CSS Profile. This operation will retrieve the local CSS Profile. 
	 * If none exists or the local cache is deemed to have expired this will 
	 * result in a synchronisation with the cloud node. 
	 * 
	 * @param callback
	 * @return ICSSProfile current CSS profile
	 */
	ICSSProfile getCSSProfile(ICSSManagerCallback callback);

	/**
	 * Modify the CSS Profile
	 * 
	 * @param profile
	 * @return boolean operation successful
	 */
	boolean modifyCSSProfile(ICSSProfile profile);
	/**
	 * Change the status a CSS device
	 * 
	 * @param profile
	 * @param callback
	 * @return boolean operation successful
	 */
	boolean changeCSSNodeStatus(ICSSProfile profile, ICSSManagerCallback callback);
	
	/**
	 * Synchronise the CSS profile. The CSS cloud node's current profile is synchronised
	 * with the local device's cached version
	 * 
	 * @param profile
	 * @param callback
	 * @return boolean operation successful
	 */
	boolean synchProfile(ICSSProfile profile, ICSSManagerCallback callback);
	
	/**
	 * Set the presence status of the user
	 * 
	 * @param profile
	 * @param callback
	 * @return boolean operation successful
	 */
	boolean setPresenceStatus(ICSSProfile profile, ICSSManagerCallback callback);
}

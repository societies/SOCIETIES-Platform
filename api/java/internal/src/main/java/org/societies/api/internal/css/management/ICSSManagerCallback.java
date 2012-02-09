package org.societies.api.internal.css.management;

/**
 * ICSSManager callback class as used in {@link ICSSManager}
 * 
 *
 */
public interface ICSSManagerCallback {
	
	/**
	 * Defines what happens when a method is called 
	 * @param message
	 * @param profile
	 */
	public void onMethodInvocation(String message, ICSSProfile profile);

}

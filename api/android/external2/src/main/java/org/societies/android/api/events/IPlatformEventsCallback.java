package org.societies.android.api.events;

/**
 * Simple callback interface to allow the Societies Client platform events service 
 * to return asynchronous method invocation results
 *
 */
public interface IPlatformEventsCallback {

	/**
	 * A generic method to receive the callback action
	 * @param resultFlag
	 */
	void returnAction(boolean resultFlag);
	/**
	 * A generic method to receive the callback action
	 * @param result
	 */
	void returnAction(int result);
	/**
	 * A generic method to receive the callback exception
	 * @param result
	 */
	void returnException(int exception);
}

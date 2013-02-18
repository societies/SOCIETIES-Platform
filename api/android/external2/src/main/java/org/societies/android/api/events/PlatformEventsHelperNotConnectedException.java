package org.societies.android.api.events;

/**
 * Exception thrown by Platform Events helper class to signify that the class is not connected
 * to Platform Events service. This exception should always be expected as Android can terminate
 * a service at any time.
 *
 */
public class PlatformEventsHelperNotConnectedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PlatformEventsHelperNotConnectedException() {
		super();
	}
}

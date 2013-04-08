package org.societies.android.platform.comms.state;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Exception thrown by {@link XMPPConnectionManager} in the event that valid {@link XMPPConnection} is requested
 * and it is not available due to connectivity problems.
 *
 */
public class NoXMPPConnectionAvailableException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoXMPPConnectionAvailableException() {
		super();
	}
	public NoXMPPConnectionAvailableException(String message) {
		super(message);
	}
}

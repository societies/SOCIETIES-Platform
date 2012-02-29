package org.societies.api.internal.servicelifecycle;

public final class ServiceDiscoveryException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	/**
     * @param message
     *	describes the issue that caused this exception
     */
    public ServiceDiscoveryException(String message) {
        super(message);
    }

    /**
     * @param message
     *            describes the issue that caused this exception
     * @param cause
     *            another exception (wrapped) that actually caused this
     *            exception
     */
    public ServiceDiscoveryException(String message, Throwable cause) {
        super(message, cause);
    }

}

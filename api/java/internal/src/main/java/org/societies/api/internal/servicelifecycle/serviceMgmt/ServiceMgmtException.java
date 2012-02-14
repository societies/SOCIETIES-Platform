package org.societies.api.internal.servicelifecycle.serviceMgmt;

public final class ServiceMgmtException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	/**
     * @param message
     *            describes the issue that caused this exception
     */
    public ServiceMgmtException(String message) {
        super(message);
    }

    /**
     * @param message
     *            describes the issue that caused this exception
     * @param cause
     *            another exception (wrapped) that actually caused this
     *            exception
     */
    public ServiceMgmtException(String message, Throwable cause) {
        super(message, cause);
    }

}
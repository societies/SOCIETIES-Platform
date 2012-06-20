package org.societies.api.internal.servicelifecycle;

public final class ServiceDiscoveryException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

    /**
     * Constructs a <code>ServiceDiscoveryException</code> with no detail message.
     */
	public ServiceDiscoveryException() {
		super();
	}
	
    /**
     * Constructs a <code>ServiceDiscoveryException</code> with the specified detailed
     * message.
     * 
     * @param message
     *            the detailed message.
     */
    public ServiceDiscoveryException(String message) {
        super(message);
    }

    /**
     * Creates a <code>ServiceDiscoveryException</code> with the specified cause and a
     * detailed message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
	public ServiceDiscoveryException(Throwable cause) {
		super(cause);
	}

    /**
     * Creates a <code>ServiceDiscoveryException</code> with the specified detailed message
     * and the throwable cause.
     * 
     * @param message
     *            the detail message (which is saved for later retrieval by the
     *            {@link #getMessage()} method).
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
	public ServiceDiscoveryException(String message, Throwable cause) {
		super(message, cause);
	}
}

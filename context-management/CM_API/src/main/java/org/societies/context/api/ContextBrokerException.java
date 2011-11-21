package org.societies.context.api;

/**
 * General context broker exception.
 * 
 * @author <a href="mailto:phdn@users.sourceforge.net">phdn</a>
 */
public class ContextBrokerException extends ContextException {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -9027082171081698935L;

    /**
     * Constructs a <code>ContextBrokerException</code> with no detail message.
     */
    public ContextBrokerException() {

        super("General context broker exception");
    }

    /**
     * Constructs a <code>ContextBrokerException</code> with the specified detail
     * message.
     * 
     * @param s
     *            the detail message.
     */
    public ContextBrokerException(String s) {

        super(s);
    }

    /**
     * Creates a <code>ContextBrokerException</code> with the specified detail
     * message and cause.
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
    public ContextBrokerException(String message, Throwable cause) {

        super(message, cause);
    }

    /**
     * Creates a <code>ContextBrokerException</code> with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public ContextBrokerException(Throwable cause) {

        super(cause);
    }
}

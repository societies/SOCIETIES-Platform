package org.societies.context.api;

/**
 * Thrown to indicate problems in the context database
 * 
 * @author <a href="mailto:nliampotis@users.sourceforge.net">Nicolas Liampotis</a>
 *         (ICCS)
 * @since 0.0.2
 */
public class ContextDBException extends ContextException {

    private static final long serialVersionUID = -6725672676514742451L;

    /**
     * Constructs a <code>ContextDBException</code> with no detail message.
     */
    public ContextDBException() {

        super();
    }

    /**
     * Constructs a <code>ContextDBException</code> with the specified detail
     * message.
     * 
     * @param s
     *            the detail message.
     */
    public ContextDBException(String s) {

        super(s);
    }

    /**
     * Creates a <code>ContextDBException</code> with the specified detail
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
    public ContextDBException(String message, Throwable cause) {

        super(message, cause);
    }

    /**
     * Creates a <code>ContextDBException</code> with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public ContextDBException(Throwable cause) {

        super(cause);
    }
}

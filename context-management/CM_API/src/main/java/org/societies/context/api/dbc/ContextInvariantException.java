package org.societies.context.api.dbc;

import org.societites.context.api.ContextException;

/**
 * Context Invariant Exception.
 * 
 * Thrown if an invariant is not satisfied.
 * 
 * @author <a href="mailto:phdn@users.sourceforge.net">phdn</a>
 * 
 * @since 0.5.1
 *
 */
public class ContextInvariantException extends ContextException {


    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -8764967379191578839L;

    /**
     * Constructs a <code>ContextInvariantException</code> with no detail message.
     */
    public ContextInvariantException() {

        super();
    }

    /**
     * Constructs a <code>ContextInvariantException</code> with the specified detail
     * message.
     * 
     * @param s
     *            the detail message.
     */
    public ContextInvariantException(String s) {

        super(s);
    }

    /**
     * Creates a <code>ContextInvariantException</code> with the specified detail
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
    public ContextInvariantException(String message, Throwable cause) {

        super(message, cause);
    }

    /**
     * Creates a <code>ContextInvariantException</code> with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public ContextInvariantException(Throwable cause) {

        super(cause);
    }
}

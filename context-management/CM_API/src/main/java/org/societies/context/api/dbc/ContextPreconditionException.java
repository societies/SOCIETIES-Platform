package org.societies.context.api.dbc;

import org.societites.context.api.ContextException;

/**
 * Context Precondition Exception.
 * 
 * Thrown if a precondition is not satisfied.
 * 
 * @author <a href="mailto:phdn@users.sourceforge.net">phdn</a>
 * 
 * @since 0.5.1
 *
 */
public class ContextPreconditionException extends ContextException {


    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -9122918750129208931L;

    /**
     * Constructs a <code>ContextPreconditionException</code> with no detail message.
     */
    public ContextPreconditionException() {

        super();
    }

    /**
     * Constructs a <code>ContextPreconditionException</code> with the specified detail
     * message.
     * 
     * @param s
     *            the detail message.
     */
    public ContextPreconditionException(String s) {

        super(s);
    }

    /**
     * Creates a <code>ContextPreconditionException</code> with the specified detail
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
    public ContextPreconditionException(String message, Throwable cause) {

        super(message, cause);
    }

    /**
     * Creates a <code>ContextPreconditionException</code> with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public ContextPreconditionException(Throwable cause) {

        super(cause);
    }
}

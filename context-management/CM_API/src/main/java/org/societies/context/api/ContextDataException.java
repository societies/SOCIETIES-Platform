package org.societies.context.api;

/**
 * Thrown to indicate problems in the evaluation of a valid SQL statement
 * against the given context data resulting in some illegal operation or
 * mismatched SQL types.
 * 
 * @author <a href="mailto:nliampotis@users.sourceforge.net">Nicolas
 *         Liampotis</a> (ICCS)
 * @since 0.4.0
 */
public class ContextDataException extends ContextDBException {

    private static final long serialVersionUID = 2171706737170783108L;

    /**
     * Constructs a <code>ContextDBException</code> with no detail message.
     */
    public ContextDataException() {
        super();
    }

    /**
     * Constructs a <code>ContextDBException</code> with the specified detail
     * message.
     * 
     * @param s
     *            the detail message.
     */
    public ContextDataException(String s) {
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
    public ContextDataException(String message, Throwable cause) {
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
    public ContextDataException(Throwable cause) {
        super(cause);
    }
}

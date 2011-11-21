package org.societies.context.api;

/**
 * Thrown to indicate problems related to context sources.
 * 
 * @author <a href="mailto:nliampotis@users.sourceforge.net">Nicolas Liampotis</a>
 *         (ICCS)
 * @since 0.5.3
 */
public class ContextSourceException extends ContextException {

    private static final long serialVersionUID = 3316014839508561587L;

    /**
     * Constructs a <code>ContextSourceException</code> with no detail message.
     */
    public ContextSourceException() {
        super();
    }

    /**
     * Constructs a <code>ContextSourceException</code> with the specified detail
     * message.
     * 
     * @param s
     *            the detail message.
     */
    public ContextSourceException(String s) {
        super(s);
    }

    /**
     * Creates a <code>ContextSourceException</code> with the specified detail
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
    public ContextSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a <code>ContextSourceException</code> with the specified cause and
     * a detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of <tt>cause</tt>
     * ).
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public ContextSourceException(Throwable cause) {
        super(cause);
    }
}
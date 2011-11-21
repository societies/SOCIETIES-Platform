//package org.personalsmartspace.cm.api.pss3p;
package org.societies.context.api;

/**
 * Thrown to indicate a malformed context identifier.
 * 
 * @see org.personalsmartspace.cm.model.api.pss3p.ICtxIdentifier
 * @author <a href="mailto:nliampotis@users.sourceforge.net">Nicolas Liampotis</a>
 *         (ICCS)
 * @since 0.0.2
 */
public class MalformedCtxIdentifierException extends ContextException {

    private static final long serialVersionUID = 3524505145520084823L;

    /**
     * Constructs a <code>ContextException</code> with no detail message.
     */
    public MalformedCtxIdentifierException() {

        super();
    }

    /**
     * Constructs a <code>MalformedCtxIdentifierException</code> with the
     * specified detail message.
     * 
     * @param s
     *            the detail message.
     */
    public MalformedCtxIdentifierException(String s) {

        super(s);
    }

    /**
     * Creates a <code>MalformedCtxIdentifierException</code> with the specified
     * detail message and cause.
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
    public MalformedCtxIdentifierException(String message, Throwable cause) {

        super(message, cause);
    }

    /**
     * Creates a <code>MalformedCtxIdentifierException</code> with the specified
     * cause and a detail message of
     * <tt>(cause==null ? null : cause.toString())</tt> (which typically
     * contains the class and detail message of <tt>cause</tt>).
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public MalformedCtxIdentifierException(Throwable cause) {

        super(cause);
    }

}

//package org.personalsmartspace.cm.api.pss3p;
package org.societies.context.api;

/**
 * Subclasses of <code>ContextException</code> are thrown by the Context Mgmt
 * block components to indicate conditions that a context consumer might want to
 * catch.
 * 
 * TODO extend the PSSFrameworkException when available
 * 
 * @author <a href="mailto:nliampotis@users.sourceforge.net">Nicolas Liampotis</a>
 *         (ICCS)
 * @since 0.0.2
 */
public abstract class ContextException extends Exception {

    private static final long serialVersionUID = 3769214814858706875L;

    /**
     * Constructs a <code>ContextException</code> with no detail message.
     */
    public ContextException() {

        super();
    }

    /**
     * Constructs a <code>ContextException</code> with the specified detail
     * message.
     * 
     * @param s
     *            the detail message.
     */
    public ContextException(String s) {

        super(s);
    }

    /**
     * Creates a <code>ContextException</code> with the specified detail message
     * and cause.
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
    public ContextException(String message, Throwable cause) {

        super(message, cause);
    }

    /**
     * Creates a <code>ContextException</code> with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public ContextException(Throwable cause) {

        super(cause);
    }
}

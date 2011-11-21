//package org.personalsmartspace.cm.api.pss3p.dbc;
package org.societies.context.api.dbc;

//import org.personalsmartspace.cm.api.pss3p.ContextException;
import org.societites.context.api.ContextException;

/**
 * Context Assertion Exception.
 * 
 * Thrown if an assertion is not satisfied.
 * 
 * @author <a href="mailto:phdn@users.sourceforge.net">phdn</a>
 * 
 * @since 0.5.1
 *
 */
public class ContextAssertionException extends ContextException {


    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -5613027997570832919L;

    /**
     * Constructs a <code>ContextAssertionException</code> with no detail message.
     */
    public ContextAssertionException() {

        super();
    }

    /**
     * Constructs a <code>ContextAssertionException</code> with the specified detail
     * message.
     * 
     * @param s
     *            the detail message.
     */
    public ContextAssertionException(String s) {

        super(s);
    }

    /**
     * Creates a <code>ContextAssertionException</code> with the specified detail
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
    public ContextAssertionException(String message, Throwable cause) {

        super(message, cause);
    }

    /**
     * Creates a <code>ContextAssertionException</code> with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public ContextAssertionException(Throwable cause) {

        super(cause);
    }
}

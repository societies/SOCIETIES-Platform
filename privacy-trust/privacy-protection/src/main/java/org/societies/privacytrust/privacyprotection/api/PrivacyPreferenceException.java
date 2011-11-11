package org.societies.privacytrust.privacyprotection.api;

/**
 * @author Elizabeth
 *
 */
public class PrivacyPreferenceException extends Exception{
    /**
     * Constructs a <code>PrivacyPreferenceException</code> with no detail
     * message.
     */
    public PrivacyPreferenceException() {
        super();
    }

    /**
     * Constructs a <code>PrivacyPreferenceException</code> with the specified
     * detail message.
     * 
     * @param s
     *            the detail message.
     */
    public PrivacyPreferenceException(String s) {
        super(s);
    }

    /**
     * Creates a <code>PrivacyPreferenceException</code> with the specified detail
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
    public PrivacyPreferenceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a <code>PrivacyPreferenceException</code> with the specified cause
     * and a detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of <tt>cause</tt>
     * ).
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the
     *            {@link #getCause()} method). (A <tt>null</tt> value is
     *            permitted, and indicates that the cause is nonexistent or
     *            unknown.)
     */
    public PrivacyPreferenceException(Throwable cause) {
        super(cause);
    }
}

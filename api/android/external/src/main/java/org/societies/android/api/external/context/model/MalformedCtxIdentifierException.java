package org.societies.android.api.external.context.model;

import org.societies.android.api.external.context.CtxException;

public class MalformedCtxIdentifierException extends CtxException {

	private static final long serialVersionUID = -1989914038541623047L;

	/**
     * Constructs a <code>MalformedCtxIdentifierException</code> with no detail message.
     */
    public MalformedCtxIdentifierException() {
    	
        super();
    }

    /**
     * Constructs a <code>MalformedCtxIdentifierException</code> with the specified detail
     * message.
     * 
     * @param message
     *            the detail message.
     */
    public MalformedCtxIdentifierException(String message) {

        super(message);
    }

    /**
     * Creates a <code>MalformedCtxIdentifierException</code> with the specified detail
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
    public MalformedCtxIdentifierException(String message, Throwable cause) {

        super(message, cause);
    }

    /**
     * Creates a <code>MalformedCtxIdentifierException</code> with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
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
//package org.personalsmartspace.cm.api.pss3p;
package org.societies.context.api;

/**
 * Context broker remote exception.
 * 
 * Thrown when a exception occurs while servicing a request in a remote
 * context broker.
 * 
 * @author <a href="mailto:phdn@users.sourceforge.net">phdn</a>
 *
 * @since 0.5.3
 */
public class BrokerRemoteException extends ContextException {

    /**
     * 
     */
    private static final long serialVersionUID = -7850309162651063303L;

    /**
     * Empty constructor: required by XStream for de-serialisation
     */
    public BrokerRemoteException() {
    }

    /**
     * Constructor: stack trace is required for serialisation.
     * 
     * @param message The error message.
     * @param stackTrace The exception stack trace.
     */
    public BrokerRemoteException(String message, StackTraceElement[] stackTrace) {
        super(message);
        this.setStackTrace(stackTrace);
    }
}

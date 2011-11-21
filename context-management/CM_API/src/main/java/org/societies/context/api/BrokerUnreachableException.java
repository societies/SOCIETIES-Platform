package org.societies.context.api;

/**
 * Context broker un-reachable exception.
 * 
 * @author <a href="mailto:phdn@users.sourceforge.net">phdn</a>
 *
 * @since 0.5.2
 */
public class BrokerUnreachableException extends ContextBrokerException {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 1801287956044223067L;

    public BrokerUnreachableException() {
        super("Target context broker is un-reachable");
    }

    public BrokerUnreachableException(String s) {
        super(s);
    }

    public BrokerUnreachableException(String message, Throwable cause) {
        super(message, cause);
    }

    public BrokerUnreachableException(Throwable cause) {
        super(cause);
    }

}

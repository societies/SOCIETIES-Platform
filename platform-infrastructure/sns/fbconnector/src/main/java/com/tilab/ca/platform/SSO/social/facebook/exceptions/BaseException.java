/**
 * 
 */
package com.tilab.ca.platform.SSO.social.facebook.exceptions;




/**
 * Superclasse di tutte le eccezioni di CA Platform
 * @author ue014084
 */
public abstract class BaseException extends Exception {
    
    private int code = StatusCode.SC_500_INTERNAL_SERVER_ERROR;
    private String status = StatusCode.STATUS_ERROR;
    
    /**
     * 
     */
    private static final String MSG = "Generic exception";
    
    /**
     * 
     */
    public BaseException() {
	super(MSG);
    }
    
    /**
     * @param message
     */
    public BaseException(String message) {
	super(message);
    }
    
    /**
     * @param message
     * @param cause
     */
    public BaseException(String message, Throwable cause) {
	super(message, cause);
    }
    
    /**
     * @param cause
     */
    public BaseException(Throwable cause) {
	super(MSG, cause);
    }
    
    /**
     * @return the code
     */
    public int getCode() {
	return code;
    }
    
    /**
     * @return the status
     */
    public String getStatus() {
	return status;
    }
    
}

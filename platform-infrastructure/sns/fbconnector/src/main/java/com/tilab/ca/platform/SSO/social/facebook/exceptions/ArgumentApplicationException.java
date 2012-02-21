package com.tilab.ca.platform.SSO.social.facebook.exceptions;


public class ArgumentApplicationException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2450139334386396763L;
	
	private int code = StatusCode.SC_400_BAD_REQUEST;
	private String status = StatusCode.STATUS_ERROR;
	
	/**
	 * 
	 */
	private static final String MSG = "Missing or not valid parameter(s)";

	/**
	 * 
	 */
	public ArgumentApplicationException() {
		super(MSG);
	}

	/**
	 * @param message
	 */
	public ArgumentApplicationException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ArgumentApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public ArgumentApplicationException(Throwable cause) {
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
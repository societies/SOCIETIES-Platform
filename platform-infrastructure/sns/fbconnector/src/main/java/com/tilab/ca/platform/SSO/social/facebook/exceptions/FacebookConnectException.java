/*********************************************
  Context Awareness Platform
 ********************************************
  Copyright (c) 2006-2010 Telecom Italia S.p.A
 ******************************************** 
  $Id: UserAlreadyExistsException.java 6992 2010-02-24 18:39:40Z papurello $
  $HeadURL: http://163.162.93.163:90/svn/ca/platform/commons-ca/branches/rel-1_0-ev/src/com/tilab/ca/platform/commons/exception/UserAlreadyExistsException.java $
 *********************************************
 */
package com.tilab.ca.platform.SSO.social.facebook.exceptions;




public class FacebookConnectException extends BaseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9075111849522061223L;
	/**
	 * 
	 */
	private static final String MSG = "Connectivity Error";
	
	/**
	 * 
	 */
	public FacebookConnectException() {
		super(MSG);
	}
	
	/**
	 * @param message
	 */
	public FacebookConnectException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public FacebookConnectException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @param cause
	 */
	public FacebookConnectException(Throwable cause) {
		super(MSG, cause);
	}
	
}

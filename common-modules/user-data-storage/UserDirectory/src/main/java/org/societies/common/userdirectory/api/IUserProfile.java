/**
 * This is an initial interface for persisting user information 
 */
package org.societies.common.userdirectory.api;
/**
 * 
 * 
 */

public interface IUserProfile {		
	/**
	 * 
	 * @param jid unique XMPP communication id of an user
	 */	
	public void setJabberID(Object jid);	
	/**
	 * @param cssid
	 */
	public void addCSSID(Object cssid);	
	/**User
	 * @param memberCIS
	 */
	public void addCISMembershipGroup(Object memberCIS);
	/**
	 * @param fullname
	 */
	public void setName(Object fullname);
	/**
	 * @param note
	 */
	public void setContent(Object note);
	/**
	 * @param email
	 */
	public void addEmailAddress(Object email);
	/**
	 * @param phone
	 */
	public void addPhoneNumber(Object phone);
	/**
	 * @param address
	 */
	public void addStructuredPostalAddress(Object address);	
	
}

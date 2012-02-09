package org.societies.api.internal.css.management;

import java.sql.Date;
import java.util.List;

/**
 * 
 * Contains information that details a CSS (user) profile
 *
 */
public interface ICSSProfile {
	/**
	 * Enum for CSS status
	 */
	enum cssStatus {Active, Inactive};
	/**
	 * Enum for entity types
	 */
	enum entityType {Person, Organisation};
	/**
	 * Enum for gender types
	 */
	enum genderType {Male, Female, Unspecified};
	/**
	 * Enum for presence types
	 */
	enum presenceType {Available, DoNotDisturb, Offline, Away, ExtendedAway };
	
	
	/**
	 * is the CSS a person or organisation ?
	 */
	String entityType = null;
	/**
	 * used for personal CSS
	 */
	String foreName = null;
	/**
	 * used for personal surname or organisation's name
	 */
	String name = null;
	/**
	 * CSS name
	 * Will be required to be unique
	 */
	String identityName = null;
	/**
	 * password 
	 * Will be required to one way encrypted e.g. SHA-1
	 */
	String password = null;
	/**
	 * e-mail account
	 */
	String emailID = null;
	/**
	 * Instant messaging ID
	 */
	String imID = null;
	/**
	 * Social Network URI
	 */
	String socialURI = null;
	/**
	 * Gender of person
	 */
	String sex = null;
	/**
	 * Home or default location
	 */
	String homeLocation = null;
	
	/**
	 * CSS UID 
	 */
	String cssIdentity = null;
	/**
	 * Current list of device IDs that constitute a CSS
	 */
	List <ICSSDevice> cssDevices = null;
	/**
	 * Status of CSS
	 */
	String cssStatus = null;
	/**
	 * Date of CSS registration
	 */
	Date cssRegistration = null;
	/**
	 * Date of CSS inactivation
	 */
	Date cssInactivation = null;
	/**
	 * Number of minutes that the CSS has been logged in
	 */
	int cssUpTime = 0;
	
	/**
	 * List of CIS that the CSS has participated in
	 * TODO Requires CIS generic data type
	 */
	List encounteredCIS = null;
	/**
	 * List of devices that have participated in the CSS
	 */
	List <ICSSDevice> archiveCSSDevices = null;
	/**
	 * Presence status user
	 */
	String presenceStatus = null;
}
	
	

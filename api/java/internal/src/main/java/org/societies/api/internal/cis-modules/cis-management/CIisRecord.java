/**
 * Stores meta data relevant for a CIS.
 * 
 * @author Babak Farshchian
 * @version 0
 */
package org.societies.cis.management.api;

public class CISRecord {
	public CISActivityFeed feed;
	public String ownerCss;
	public String membershipCriteria;
	/**
	 * permaLink is a permanent URL to this CIS. A type of CIS homepage.
	 */
	public String permaLink;
	public String[] membersCss;
	private String password = "none";
	public ServiceSharingRecord[] sharedServices;
	

}

/**
 * Stores meta data relevant for a CIS.
 * 
 * @author Babak Farshchian
 * @version 0
 */
package org.societies.cis.management.api;

import java.util.List;

public class CISRecord {
	
	public CISRecord(CISActivityFeed feed, String ownerCss,
			String membershipCriteria, String permaLink, String[] membersCss,
			String password, List<ServiceSharingRecord> sharedServices) {
		
		this.feed = feed;
		this.ownerCss = ownerCss;
		this.membershipCriteria = membershipCriteria;
		this.permaLink = permaLink;
		this.membersCss = membersCss;
		this.password = password;
		this.sharedServices = sharedServices;
	}
	
	
	public CISActivityFeed feed;
	public String ownerCss;
	public String membershipCriteria;
	/**
	 * permaLink is a permanent URL to this CIS. A type of CIS homepage.
	 */
	public String permaLink;
	public String[] membersCss;
	private String password = "none";
	public List<ServiceSharingRecord> sharedServices;
	

}

package org.societies.api.internal.css.management;

/**
 * Defines a CSS profile
 * 
 * In order to allow for Android compatibility, the natural Enum types 
 * are int types. 
 */
public class CSSRecord {
	
	/**
	 * User's chosen domain server
	 */
	String domainServer = null;
	
	/**
	 * User's CSS hosting location
	 */
	String cssHostingLocation = null;
	/**
	 * is the CSS a person or organisation ?
	 */
	int entity = 0;
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
	int sex = 0;
	/**
	 * Home or default location
	 */
	String homeLocation = null;
	
	/**
	 * CSS UID 
	 */
	String cssIdentity = null;
	/**
	 * Current list of node IDs that constitute a CSS
	 */
	CSSNode cssNodes[] = null;
	/**
	 * Node Types
	 */
	int nodeType = 0;
		/**
	 * Status of CSS
	 */
	int status = 0;
	/**
	 * Date of CSS registration
	 */
	String cssRegistration = null;
	/**
	 * Date of CSS inactivation
	 */
	String cssInactivation = null;
	/**
	 * Number of minutes that the CSS has been logged in
	 */
	int cssUpTime = 0;
	
//	/**
//	 * List of CIS that the CSS has participated in
//	 * TODO Requires CIS generic data type
//	 */
//	List encounteredCIS = null;
	/**
	 * Array of nodes that have participated in the CSS
	 */
	CSSNode archiveCSSNodes[] = null;
	/**
	 * Presence status user
	 */
	int presence = 0;
	
	/**
	 * Default Contructor
	 */
	public CSSRecord() {
		
	}

	public String getDomainServer() {
		return domainServer;
	}

	public void setDomainServer(String domainServer) {
		this.domainServer = domainServer;
	}

	public String getCssHostingLocation() {
		return cssHostingLocation;
	}

	public void setCssHostingLocation(String cssHostingLocation) {
		this.cssHostingLocation = cssHostingLocation;
	}

	public int getEntity() {
		return entity;
	}

	public void setEntity(int entity) {
		this.entity = entity;
	}

	public String getForeName() {
		return foreName;
	}

	public void setForeName(String foreName) {
		this.foreName = foreName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentityName() {
		return identityName;
	}

	public void setIdentityName(String identityName) {
		this.identityName = identityName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}

	public String getImID() {
		return imID;
	}

	public void setImID(String imID) {
		this.imID = imID;
	}

	public String getSocialURI() {
		return socialURI;
	}

	public void setSocialURI(String socialURI) {
		this.socialURI = socialURI;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getHomeLocation() {
		return homeLocation;
	}

	public void setHomeLocation(String homeLocation) {
		this.homeLocation = homeLocation;
	}

	public String getCssIdentity() {
		return cssIdentity;
	}

	public void setCssIdentity(String cssIdentity) {
		this.cssIdentity = cssIdentity;
	}

	public CSSNode[] getCssNodes() {
		return cssNodes;
	}

	public void setCssNodes(CSSNode[] cssNodes) {
		this.cssNodes = cssNodes;
	}
	
	public int getnodeType() {
		return nodeType;
	}
	
	public void setnodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCssRegistration() {
		return cssRegistration;
	}

	public void setCssRegistration(String cssRegistration) {
		this.cssRegistration = cssRegistration;
	}

	public String getCssInactivation() {
		return cssInactivation;
	}

	public void setCssInactivation(String cssInactivation) {
		this.cssInactivation = cssInactivation;
	}

	public int getCssUpTime() {
		return cssUpTime;
	}

	public void setCssUpTime(int cssUpTime) {
		this.cssUpTime = cssUpTime;
	}

	public CSSNode[] getArchiveCSSNodes() {
		return archiveCSSNodes;
	}

	public void setArchiveCSSNodes(CSSNode[] archiveCSSNodes) {
		this.archiveCSSNodes = archiveCSSNodes;
	}

	public int getPresence() {
		return presence;
	}

	public void setPresence(int presence) {
		this.presence = presence;
	}


}

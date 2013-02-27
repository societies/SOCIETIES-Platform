package org.societies.api.internal.css;


/**
 * Defines a CSS profile
 * 
 * In order to allow for Android compatibility, the natural Enum types 
 * are int types. 
 */
public class CSSRecord {
	
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
	 * e-mail account
	 */
	String emailID = null;
	
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
	 * Current workplace
	 */

	String workplace = null; 

	/**
	 * Current role within the workplace
	 */

	String position = null;
	
	/**
	 * Default Contructor
	 */
	public CSSRecord() {
		
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

	public String getEmailID() {
		return emailID;
	}

	public void setEmailID(String emailID) {
		this.emailID = emailID;
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

	public CSSNode[] getArchiveCSSNodes() {
		return archiveCSSNodes;
	}

	public void setArchiveCSSNodes(CSSNode[] archiveCSSNodes) {
		this.archiveCSSNodes = archiveCSSNodes;
	}
	
	public String getWorkplace() {
		return workplace;
	}

	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}



}

package org.societies.css.cssRegistry.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * This is the Class accepted by the CssRegistry when a css wants to register.
 * This Object contains attributes used to retrieve services shared from/to a
 * CSS/CIS and also information to retrieve organization that has developed the
 * service.
 * 
 * @author apanazzolo
 * @version 1.0
 * @created 06-dic-2011 12.12.57
 */

@Entity
@Table(name = "CssRegistryEntry")
public class CssRegistryEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String cssHostingLocation;
	private String cssIdentity;
	private String cssInactivation;
	private String cssRegistration;
	private int cssUpTime;
	private String domainServer;
	private String emailID;
	private int entity;
	private String foreName;
	private String homeLocation;
	private String identityName;
	private String imID;
	private String name;
	private String password;
	private int presence;
	private int sex;
	private String socialURI;
	private int status;

	/**
	 * @return the cssHostingLocation
	 */
	@Column(name = "CssHostingLocation")
	public String getCssHostingLocation() {
		return cssHostingLocation;
	}
	/**
	 * @param cssHostingLocation the cssHostingLocation to set
	 */
	public void setCssHostingLocation(String cssHostingLocation) {
		this.cssHostingLocation = cssHostingLocation;
	}
	/**
	 * @return the cssIdentity
	 */
	@Column(name = "CssIdentity")
	public String getCssIdentity() {
		return cssIdentity;
	}
	/**
	 * @param cssIdentity the cssIdentity to set
	 */
	public void setCssIdentity(String cssIdentity) {
		this.cssIdentity = cssIdentity;
	}
	/**
	 * @return the cssInactivation
	 */
	@Column(name = "CssInactivation")
	public String getCssInactivation() {
		return cssInactivation;
	}
	/**
	 * @param cssInactivation the cssInactivation to set
	 */
	public void setCssInactivation(String cssInactivation) {
		this.cssInactivation = cssInactivation;
	}

	/**
	 * @return the cssRegistration
	 */
	@Column(name = "CssRegistration")
	public String getCssRegistration() {
		return cssRegistration;
	}
	/**
	 * @param cssRegistration the cssRegistration to set
	 */
	public void setCssRegistration(String cssRegistration) {
		this.cssRegistration = cssRegistration;
	}
	/**
	 * @return the cssUpTime
	 */
	//TODO : We are not going to save this. Figure out what to do here!
	public int getCssUpTime() {
		return cssUpTime;
	}
	/**
	 * @param cssUpTime the cssUpTime to set
	 */
	public void setCssUpTime(int cssUpTime) {
		this.cssUpTime = cssUpTime;
	}
	/**
	 * @return the domainServer
	 */
	@Column(name = "DomainServer")
	public String getDomainServer() {
		return domainServer;
	}
	/**
	 * @param domainServer the domainServer to set
	 */
	public void setDomainServer(String domainServer) {
		this.domainServer = domainServer;
	}
	/**
	 * @return the emailID
	 */
	@Column(name = "EmailID")
	public String getEmailID() {
		return emailID;
	}
	/**
	 * @param emailID the emailID to set
	 */
	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}
	/**
	 * @return the entity
	 */
	@Column(name = "Entity")
	public int getEntity() {
		return entity;
	}
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(int entity) {
		this.entity = entity;
	}
	/**
	 * @return the foreName
	 */
	@Column(name = "ForeName")
	public String getForeName() {
		return foreName;
	}
	/**
	 * @param foreName the foreName to set
	 */
	public void setForeName(String foreName) {
		this.foreName = foreName;
	}
	/**
	 * @return the homeLocation
	 */
	@Column(name = "HomeLocation")
	public String getHomeLocation() {
		return homeLocation;
	}
	/**
	 * @param homeLocation the homeLocation to set
	 */
	public void setHomeLocation(String homeLocation) {
		this.homeLocation = homeLocation;
	}
	/**
	 * @return the identityName
	 */
	@Column(name = "IdentityName")
	public String getIdentityName() {
		return identityName;
	}
	/**
	 * @param identityName the identityName to set
	 */
	public void setIdentityName(String identityName) {
		this.identityName = identityName;
	}
	/**
	 * @return the imID
	 */
	@Column(name = "ImID")
	public String getImID() {
		return imID;
	}
	/**
	 * @param imID the imID to set
	 */
	public void setImID(String imID) {
		this.imID = imID;
	}
	/**
	 * @return the name
	 */
	@Column(name = "Name")
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the password
	 */
	@Column(name = "Password")
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the presence
	 */
	@Column(name = "Presence")
	public int getPresence() {
		return presence;
	}
	/**
	 * @param presence the presence to set
	 */
	public void setPresence(int presence) {
		this.presence = presence;
	}
	/**
	 * @return the sex
	 */
	@Column(name = "Sex")
	public int getSex() {
		return sex;
	}
	/**
	 * @param sex the sex to set
	 */
	public void setSex(int sex) {
		this.sex = sex;
	}
	/**
	 * @return the socialURI
	 */
	@Column(name = "SocialURI")
	public String getSocialURI() {
		return socialURI;
	}
	/**
	 * @param socialURI the socialURI to set
	 */
	public void setSocialURI(String socialURI) {
		this.socialURI = socialURI;
	}
	/**
	 * @return the status
	 */
	@Column(name = "Status")
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @param cssHostingLocation
	 * @param cssIdentity
	 * @param cssInactivation
	 * @param cssRegistration
	 * @param cssUpTime
	 * @param domainServer
	 * @param emailID
	 * @param entity
	 * @param foreName
	 * @param homeLocation
	 * @param identityName
	 * @param imID
	 * @param name
	 * @param password
	 * @param presence
	 * @param sex
	 * @param socialURI
	 * @param status
	 */
	public CssRegistryEntry(String cssHostingLocation, String cssIdentity,
			String cssInactivation, String cssRegistration, int cssUpTime, String domainServer,
			String emailID, int entity, String foreName, String homeLocation,
			String identityName, String imID, String name, String password,
			int presence, int sex, String socialURI, int status) {

		this.cssHostingLocation = cssHostingLocation;
		this.cssIdentity = cssIdentity;
		this.cssInactivation = cssInactivation;
		this.cssRegistration = cssRegistration;
		this.cssUpTime = cssUpTime;
		this.domainServer = domainServer;
		this.emailID = emailID;
		this.entity = entity;
		this.foreName = foreName;
		this.homeLocation = homeLocation;
		this.identityName = identityName;
		this.imID = imID;
		this.name = name;
		this.password = password;
		this.presence = presence;
		this.sex = sex;
		this.socialURI = socialURI;
		this.status = status;
	}

	
}
/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 package org.societies.css.cssRegistry.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;


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
	
	private String cssIdentity;
	private String emailID;
	private int entity;
	private String foreName;
	private String homeLocation;
	private String name;
	private int sex;
	private String position;
	private String workplace;
	private String domainServer;
	private String password;

	/**
	 * @return the cssIdentity
	 */
	@Id
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
	 * @return the workplace
	 */
	@Column(name = "Workplace")
	public String getWorkplace() {
		return workplace;
	}
	/**
	 * @param workplace the workplace to set
	 */
	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}
	
	/**
	 * @return the position
	 */
	@Column(name = "Position")
	public String getPosition() {
		return position;
	}
	/**
	 * @param Position the Position to set
	 */
	public void setPosition(String position) {
		this.position = position;
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
	 * @param cssIdentity
	 * @param emailID
	 * @param entity
	 * @param foreName
	 * @param homeLocation
	 * @param name
	 * @param sex
	 * @param workplace
	 * @param position
	 */
	public CssRegistryEntry(String cssIdentity,
			String emailID, int entity, String foreName, String homeLocation,
			String name, int sex, String workplace, String position) {

		super();
		this.cssIdentity = cssIdentity;
		this.emailID = emailID;
		this.entity = entity;
		this.foreName = foreName;
		this.homeLocation = homeLocation;
		this.name = name;
		this.sex = sex;
		this.workplace = workplace;
		this.position = position;
		
		
	}

	public CssRegistryEntry() {
		super();
	}
	
	
}

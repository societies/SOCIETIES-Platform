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
 package org.societies.cis.directory.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Id;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * This is the Class accepted by the CisDirectory when a cis wants to register
 * an advertisement record. This Object contains attributes used to retrieve
 * services shared from/to a CSS/CIS and also information to retrieve
 * organization that has developed the service.
 *
 * @author mmannion
 * @version 1.0
 */

@Entity
@Table(name = "cis_directory_advertisements")
public class CisAdvertisementRecordEntry implements Serializable {

	private static final long serialVersionUID = 7819484667842436359L;

	private String id;
	private String name;
	private String cssownerid;
	private String password;
	private String type;
	private Set<CriteriaRecordEntry> criteriaRecords;

	/**@return the id*/
	@Id
	@Column(name = "cis_id")
	public String getId() {
		return id;
	}

	/**@param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**@return the name */
	@Column(name = "cis_name")
	public String getName() {
		return name;
	}

	/**@param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the cssownerid
	 */
	@Column(name = "cssownerid")
	public String getCssOwnerId() {
		return cssownerid;
	}

	/**
	 * @param cssownwerid
	 *            the css ownwer id to set
	 */
	public void setCssOwnerId(String cssownerid) {
		this.cssownerid = cssownerid;
	}
	
	
	/**
	 * @return the password
	 */
	@Column(name = "password")
	public String getpassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * @return the Type
	 */
	@Column(name = "type")
	public String gettype() {
		return type;
	}

	/**@param Type
	 *            the Type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/** @return the criteriaRecords */
	@OneToMany(fetch=FetchType.LAZY, mappedBy="cisAdvertRecord") //criteria_id
	@Cascade(CascadeType.DELETE)
	public Set<CriteriaRecordEntry> getCriteriaRecords() {
		return criteriaRecords;
	}

	/** @param criteriaRecords the criteriaRecords to set */
	public void setCriteriaRecords(Set<CriteriaRecordEntry> criteriaRecords) {
		this.criteriaRecords = criteriaRecords;
	}
	
	/**
	 * @param name
	 * @param id
	 * @param cssownwerid
	 * @param password
	 * @param type
	 * @param criteriaRecords
	 */
	public CisAdvertisementRecordEntry(String name, String id, String cssownerid, String password, String type) {
		this();
		this.name = name;
		this.id = id;
		this.cssownerid = cssownerid;
		this.password = password;
		this.type = type;
	}

	public CisAdvertisementRecordEntry(){
		super();
		criteriaRecords = new HashSet<CriteriaRecordEntry>();
	}
}
/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.security.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
@Entity
@Table(name = "org_societies_security_service")
public class Service {
	
	private static Logger LOG = LoggerFactory.getLogger(Service.class);
	protected static final String delimiter = ":";
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private int id;
	
	@Column(name="serviceId", length = 1024)
	private String serviceId;

	@Column(name="slaXmlOptions", length = 1024*1024)
	private String slaXmlOptions;

	@Lob
	@Column(name="fileServerHost")
	private URI fileServerHost;

//	@ElementCollection
//	private List<String> files;
	
//	@CollectionOfElements(targetElement = java.lang.String.class)
//	@OneToMany(fetch=FetchType.EAGER)
//	private List<String> files;

//	@Transient
//	private List<String> files;

	// I love Hibernate
	@Column(name="filesConcatenated", length = 100*1024)
	private String filesConcatenated;
	
	/**
	 * Constructor for Hibernate only.
	 */
	public Service() {
	}
	
	public Service(String serviceId, String slaXmlOptions, URI fileServerHost, List<String> files) {
		
		this.serviceId = serviceId;
		this.slaXmlOptions = slaXmlOptions;
		this.fileServerHost = fileServerHost;
//		this.files = files;

		this.filesConcatenated = cat(files);
		
		LOG.debug("Service(" + id + ", ..., " + fileServerHost + ", " + files + ")");
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the serviceId
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	/**
	 * @return the slaXmlOptions
	 */
	public String getSlaXmlOptions() {
		return slaXmlOptions;
	}

	/**
	 * @param slaXmlOptions the slaXmlOptions to set
	 */
	public void setSlaXmlOptions(String slaXmlOptions) {
		this.slaXmlOptions = slaXmlOptions;
	}

	/**
	 * @return the fileServerHost
	 */
	public URI getFileServerHost() {
		return fileServerHost;
	}

	/**
	 * @param fileServerHost the fileServerHost to set
	 */
	public void setFileServerHost(URI fileServerHost) {
		this.fileServerHost = fileServerHost;
	}

//	/**
//	 * @return relative paths to files
//	 */
//	public List<String> getFiles() {
//		return files;
//	}

	/**
	 * @return the filesConcatenated
	 */
	public String getFilesConcatenated() {
		return filesConcatenated;
	}

	/**
	 * @param filesConcatenated the filesConcatenated to set
	 */
	public void setFilesConcatenated(String filesConcatenated) {
		
		this.filesConcatenated = filesConcatenated;
//		this.files = decat(filesConcatenated);
	}
	
	public List<String> getFiles() {
		return decat(filesConcatenated);
	}
	
	// I love Hibernate
	protected static List<String> decat(String str) {
		
		List<String> list = new ArrayList<String>();
		
		if (str == null || str.isEmpty()) {
			return list;
		}
		
		String[] strArray = str.split(delimiter);
		
		if (strArray != null) {
			for (String s : strArray) {
				list.add(s);
			}
		}
		return list;
	}
	
	// I love Hibernate
	protected static String cat(List<String> strings) {
		String str = "";
		if (strings == null) {
			return str;
		}
		for (String s : strings) {
			if (!str.isEmpty()) {
				// TODO: use stringbuilder or something
				str += delimiter;
			}
			str += s;
		}
		return str;
	}
}

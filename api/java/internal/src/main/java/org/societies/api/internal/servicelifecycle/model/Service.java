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
package org.societies.api.internal.servicelifecycle.model;

/**
 * The Service class represents a generic Service with appropriate attributes
 * 
 * @author mmazzariol
 * @version 1.0
 * @created 21-dic-2011 17.18.32
 */
@XmlRootElement(
        name="ServiceMetadata",
        namespace="http://societies.org/service/model")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class Service {

	/**
	 * Unique identifier for a single instance of a service.
	 */
	@XmlElement(namespace="http://societies.org/service/model")
	private ServiceResourceIdentifier serviceIdentifier;

	/**
	 * It represents the CSS Id where the service is physically installed.
	 */
	@XmlElement(namespace="http://societies.org/service/model")
	private String CSSIDInstalled;
	/**
	 * The version of the service, it must be updated by developer
	 */
	@XmlElement(namespace="http://societies.org/service/model")
	private String version;

	/**
	 * An alias name for the service
	 */
	@XmlElement(namespace="http://societies.org/service/model")
	private String serviceName;
	/**
	 * A "long" description of the service
	 */
	@XmlElement(namespace="http://societies.org/service/model")
	private String serviceDescription;
	/**
	 * The signature of the author
	 */
	@XmlElement(namespace="http://societies.org/service/model")
	private String authorSignature;

	@XmlElement(namespace="http://societies.org/service/model")
	private ServiceType serviceType;
	
	@XmlElement(namespace="http://societies.org/service/model")
	private ServiceLocation serviceLocation;
	
	/**
	 * @param serviceIdentifier
	 * @param cSSIDInstalled
	 * @param version
	 * @param serviceName
	 * @param serviceDescription
	 * @param authorSignature
	 */	
	public Service(ServiceResourceIdentifier serviceIdentifier,
			String cSSIDInstalled, String version, String serviceName,
			String serviceDescription, String authorSignature, ServiceType type, ServiceLocation location) {

		super();
		this.serviceIdentifier = serviceIdentifier;
		this.CSSIDInstalled = cSSIDInstalled;
		this.version = version;
		this.serviceName = serviceName;
		this.serviceDescription = serviceDescription;
		this.authorSignature = authorSignature;
		
		this.serviceType = type;
		this.serviceLocation = location;
	}
	
	public Service() {
		
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public String getAuthorSignature() {
		return authorSignature;
	}

	public void setAuthorSignature(String authorSignature) {
		this.authorSignature = authorSignature;
	}

	public String getCSSIDInstalled() {
		return CSSIDInstalled;
	}

	public void setCSSIDInstalled(String CSSIDInstalled) {
		this.CSSIDInstalled = CSSIDInstalled;
	}

	public ServiceResourceIdentifier getServiceIdentifier() {
		return serviceIdentifier;
	}

	public void setServiceIdentifier(ServiceResourceIdentifier serviceIdentifier) {
		this.serviceIdentifier = serviceIdentifier;
	}
	
	public void setServiceType (ServiceType serviceType) {
		this.serviceType = serviceType;
	}
	
	public ServiceType getServiceType () {
		return this.serviceType;
	}
	
	public void setServiceLocation (ServiceLocation serviceLocation) {
		this.serviceLocation = serviceLocation;
	}
	
	public ServiceLocation getServiceLocation () {
		return this.serviceLocation;
	}
}
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
package org.societies.platform.servicelifecycle.serviceRegistry.model;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceLocation;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.model.ServiceType;


/**
 * This is the Class accepted by the ServiceRegistry when a service wants to
 * register. This Object contains attributes used to retrieve services shared
 * from/to a CSS/CIS and also information to retrieve organization that has
 * developed the service.
 * 
 * @author apanazzolo
 * @version 1.0
 * @created 06-dic-2011 12.12.57
 */

@Entity
@Table(name = "RegistryEntry")
public class RegistryEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9064750069927104572L;

	// private String serviceIdentifier;
	// private ServiceResourceIdentifier serviceIdentifier;

	private ServiceResourceIdentiferDAO serviceIdentifier;

	/**
	 * the service endPoint.
	 */
	private String serviceEndPoint;

	/**
	 * An alias name for the service
	 */
	private String serviceName;
	/**
	 * A "long" description of the service
	 */
	private String serviceDescription;
	/**
	 * The signature of the author
	 */
	private String authorSignature;

	private String serviceType;

	private String serviceLocation;

	private ServiceInstanceDAO serviceInstance;

	private String serviceStatus;

	

	/**
	 * @param serviceEndpointURI
	 * @param cSSIDInstalled
	 * @param hash
	 * @param lifetime
	 * @param serviceName
	 * @param serviceDescription
	 * @param authorSignature
	 * @param serviceInstance
	 */
	public RegistryEntry(ServiceResourceIdentifier serviceIdentifier,
			String serviceEndPoint, String serviceName,
			String serviceDescription, String authorSignature,
			ServiceType type, ServiceLocation location,
			ServiceInstance serviceInstance, ServiceStatus serviceStatus) {

		super();

		this.serviceIdentifier = new ServiceResourceIdentiferDAO(
				serviceIdentifier.getIdentifier().toString(),
				serviceIdentifier.getServiceInstanceIdentifier());

		this.serviceName = serviceName;
		this.serviceDescription = serviceDescription;
		this.authorSignature = authorSignature;
		this.serviceEndPoint = serviceEndPoint;

		this.serviceType = type.toString();
		this.serviceLocation = location.toString();
		this.serviceStatus = serviceStatus.toString();
		this.serviceInstance = new ServiceInstanceDAO(
				serviceInstance.getFullJid(), serviceInstance.getXMPPNode(),
				new ServiceImplementationDAO(serviceInstance.getServiceImpl()
						.getServiceNameSpace(), serviceInstance
						.getServiceImpl().getServiceProvider(), serviceInstance
						.getServiceImpl().getServiceVersion()));
	}
	
	public RegistryEntry(){
		super();
	}

	@Column(name = "ServiceName")
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Column(name = "ServiceDescription")
	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	@Column(name = "AuthorSignature")
	public String getAuthorSignature() {
		return authorSignature;
	}

	public void setAuthorSignature(String authorSignature) {
		this.authorSignature = authorSignature;
	}

	// @Column(name = "ServiceIdentifier")
	// @Id
	// @Target(value = ServiceResourceIdentiferDAO.class)
	@EmbeddedId
	public ServiceResourceIdentiferDAO getServiceIdentifier() {
		return this.serviceIdentifier;
	}

	public void setServiceIdentifier(
			ServiceResourceIdentiferDAO serviceIdentifier) {
		this.serviceIdentifier = serviceIdentifier;
	}

	public Service createServiceFromRegistryEntry() {
		Service returnedService = null;
		try {
			ServiceType tmpServiceType = null;
			ServiceLocation tmpServiceLocation = null;
			ServiceStatus tmpServiceStatus=null;
			
			/* Retrieve the service type from the service and
			 * create the appropriate enumeration type
			 */
			if (serviceType.equals(ServiceType.THIRD_PARTY_SERVICE.toString())) {
				tmpServiceType = ServiceType.THIRD_PARTY_SERVICE;
			} else {
				if (serviceType.equals( ServiceType.CORE_SERVICE.toString())) {
					tmpServiceType = ServiceType.CORE_SERVICE;
				}
			}
			
			/* Same as before but for service location */
			if (serviceLocation.equals(ServiceLocation.LOCAL.toString())) {
				tmpServiceLocation = ServiceLocation.LOCAL;
			} else {
				if (serviceLocation.equals(ServiceLocation.REMOTE.toString()) ) {
					tmpServiceLocation = ServiceLocation.REMOTE;
				}
			}
			
			/*Same but for the serviceStatus*/
			if (serviceStatus.equals(ServiceStatus.STARTED.toString())){
				tmpServiceStatus=ServiceStatus.STARTED;
			}else{
				if (serviceStatus.equals(ServiceStatus.STOPPED.toString())){
					tmpServiceStatus=ServiceStatus.STOPPED;
				}else{tmpServiceStatus=ServiceStatus.UNAVAILABLE;}
			}

			returnedService = new Service();
			returnedService.setAuthorSignature(this.authorSignature);
			returnedService.setServiceDescription(this.serviceDescription);
			returnedService.setServiceEndpoint(serviceEndPoint);
			ServiceInstance si = new ServiceInstance();
			si.setFullJid(this.serviceInstance.getFullJid());
			ServiceImplementation servImpl = new ServiceImplementation();
			servImpl.setServiceNameSpace(this.serviceInstance.getServiceImpl().getServiceNameSpace());
			servImpl.setServiceProvider(this.serviceInstance.getServiceImpl().getServiceProvider());
			servImpl.setServiceVersion(this.serviceInstance.getServiceImpl().getServiceVersion());
			si.setServiceImpl(servImpl);
			returnedService.setServiceInstance(si);
			returnedService.setServiceLocation(tmpServiceLocation);
			returnedService.setServiceName(serviceName);
			returnedService.setServiceStatus(tmpServiceStatus);
			returnedService.setServiceType(tmpServiceType);
			ServiceResourceIdentifier serviceResourceIdentifier=new ServiceResourceIdentifier();
			serviceResourceIdentifier.setIdentifier(new URI(this.getServiceIdentifier().getIdentifier()));
			serviceResourceIdentifier.setServiceInstanceIdentifier(this.getServiceIdentifier().getInstanceId());
			returnedService.setServiceIdentifier(serviceResourceIdentifier);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnedService;
	}

	@Column(name = "ServiceType")
	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	@Column(name = "ServiceLocation")
	public String getServiceLocation() {
		return serviceLocation;
	}

	public void setServiceLocation(String serviceLocation) {
		this.serviceLocation = serviceLocation;
	}

	@Column(name = "ServiceEndPoint")
	public String getServiceEndPoint() {
		return serviceEndPoint;
	}

	public void setServiceEndPoint(String serviceEndPoint) {
		this.serviceEndPoint = serviceEndPoint;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public ServiceInstanceDAO getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstanceDAO serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	@Column(name = "ServiceStatus")
	public String getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

}
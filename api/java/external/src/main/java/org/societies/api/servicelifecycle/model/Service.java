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
package org.societies.api.servicelifecycle.model;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * The Service class represents a generic Service with appropriate attributes
 * 
 * @author mmazzariol
 * @version 1.0
 * @created 21-dic-2011 17.18.32
 */
public class Service implements IService{

	/**
	 * Unique identifier for a single instance of a service.
	 */
	private ServiceResourceIdentifier serviceIdentifier;

	/**
	 * It represents the service endpoint for its invocation.
	 */
	private String serviceEndpoint;

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

	private ServiceType serviceType;
	
	private ServiceLocation serviceLocation;
	
	/**
	 * The class that contains information about the service instance
	 */
	private ServiceInstance serviceInstance;
	
	private ServiceStatus serviceStatus;
	
	/**
	 * @param serviceIdentifier
	 * @param serviceEndpoint
	 * @param serviceName
	 * @param serviceDescription
	 * @param authorSignature
	 * @param serviceType
	 * @param serviceLocation
	 * @param serviceInstance
	 */
	public Service(
			String serviceEndpoint, String serviceName,
			String serviceDescription, String authorSignature,
			ServiceType serviceType, ServiceLocation serviceLocation,
			ServiceInstance serviceInstance, ServiceStatus serviceStatus) {
		super();
		
		this.serviceEndpoint = serviceEndpoint;
		this.serviceName = serviceName;
		this.serviceDescription = serviceDescription;
		this.authorSignature = authorSignature;
		this.serviceType = serviceType;
		this.serviceLocation = serviceLocation;
		this.serviceInstance = serviceInstance;
		try {
			this.serviceIdentifier = new ServiceResourceIdentifier(new URI(serviceInstance.getServiceImpl().getServiceImplementationId()), serviceInstance.getServiceInstanceId());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.serviceStatus=serviceStatus;
	}



	public Service() {
		
	}

	

	public String getVersion() {
		return serviceInstance.getServiceImpl().getServiceVersion();
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

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}
	
	public IServiceResourceIdentifier createServiceResourceIdentifier(){
		ServiceResourceIdentifier returnedServiceResourceIdentifier=null;
		try {
			returnedServiceResourceIdentifier= new ServiceResourceIdentifier(new URI(serviceInstance.getServiceImpl().getServiceImplementationId()),serviceInstance.getServiceInstanceId());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}return returnedServiceResourceIdentifier;
	}



	public String getServiceEndpoint() {
		return serviceEndpoint;
	}



	public void setServiceEndpoint(String serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
	}



	public ServiceStatus getServiceStatus() {
		return serviceStatus;
	}



	public void setServiceStatus(ServiceStatus serviceStatus) {
		this.serviceStatus = serviceStatus;
	}
}
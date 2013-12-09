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
package org.societies.api.services;

import java.io.Serializable;

import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceType;

/**
 * The generic payload class for Service Management Events
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class ServiceMgmtEvent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ServiceResourceIdentifier serviceId;
	private long bundleId;
	private String bundleSymbolName;
	private ServiceType serviceType;
	private IIdentity sharedNode;
	private ServiceMgmtEventType eventType;

	private String serviceName;

	private String problem;

	private String interfaceName;
	
	
	/**
	 * @return the eventType
	 */
	public ServiceMgmtEventType getEventType() {
		return eventType;
	}
	/**
	 * @return the sharedNode
	 */
	public IIdentity getSharedNode() {
		return sharedNode;
	}
	/**
	 * @param sharedNode the sharedNode to set
	 */
	public void setSharedNode(IIdentity sharedNode) {
		this.sharedNode = sharedNode;
	}
	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(ServiceMgmtEventType eventType) {
		this.eventType = eventType;
	}
	/**
	 * @return the serviceId
	 */
	public ServiceResourceIdentifier getServiceId() {
		return serviceId;
	}
	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(ServiceResourceIdentifier serviceId) {
		this.serviceId = serviceId;
	}
	/**
	 * @return the bundleId
	 */
	public long getBundleId() {
		return bundleId;
	}
	/**
	 * @param bundleId the bundleId to set
	 */
	public void setBundleId(long bundleId) {
		this.bundleId = bundleId;
	}
	/**
	 * @return the bundleSymbolName
	 */
	public String getBundleSymbolName() {
		return bundleSymbolName;
	}
	/**
	 * @param bundleSymbolName the bundleSymbolName to set
	 */
	public void setBundleSymbolName(String bundleSymbolName) {
		this.bundleSymbolName = bundleSymbolName;
	}
	
	/**
	 * @return the bundleSymbolName
	 */
	public String getInterfaceName() {
		return interfaceName;
	}
	/**
	 * @param bundleSymbolName the bundleSymbolName to set
	 */
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}


	public void setServiceType(ServiceType serviceType){
		this.serviceType = serviceType;
	}
	
	public ServiceType getServiceType(){
		return this.serviceType;
	}
	
	public void setServiceName(String serviceName){
		this.serviceName = serviceName;
	}
	
	public String getServiceName(){
		return serviceName;
	}
	
	public String getProblem(){
		return problem;
	}
	
	public void setProblem(String problem){
		this.problem = problem;
	}
	
}

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
package org.societies.webapp.controller.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
/**
 * Describe your class here...
 *
 * @author 10036469
 *
 */
public class ServiceWrapper {

	private Service service;
	private ServicesController controller;
	private static final Logger log = LoggerFactory.getLogger(ServiceWrapper.class);
	
	public ServiceWrapper(Service service, ServicesController controller) {
		if(log.isDebugEnabled())
			log.debug("ServiceWrapper created for Service: " + service.getServiceName());
	}
	
	public Service getService(){
		return service;
	}
	
	public void setService(Service service){
		this.service = service;
		if(log.isDebugEnabled())
			log.debug("ServiceWrapper changed for for Service: " + service.getServiceName());
	}
	public String getServiceName(){
		return service.getServiceName();
	}
	
	public String getServiceDescription(){
		return service.getServiceDescription();
	}
	
	public String getServiceType(){
		return service.getServiceCategory();
	}
	
	public ServiceStatus getServiceStatus(){
		return service.getServiceStatus();
	}
	
	public boolean isStarted(){
		return service.getServiceStatus().equals(ServiceStatus.STARTED);
	}
	
	public boolean isStopped(){
		return service.getServiceStatus().equals(ServiceStatus.STOPPED);
	}
	
	public String getCreator(){
		return service.getAuthorSignature();
	}
	
	public boolean isInstalled(){
		if(isMine())
			return true;
		else{
			return false;
		}
	}
	
	public boolean isCanShare(){
		if(service.getServiceType().equals(ServiceType.THIRD_PARTY_CLIENT) || !isMine())
			return false;
		else
			return true;

	}
	
	public List<ICis> getSharedCis(){
		try {
			return controller.getServiceControl().getCisServiceIsSharedWith(service.getServiceIdentifier()).get();
		} catch (InterruptedException e) {
			log.error("Exception!");
			e.printStackTrace();
		} catch (ExecutionException e) {
			log.error("Exception!");
			e.printStackTrace();
		}
		
		return new ArrayList<ICis>();
	}
	
	
	public boolean isMine(){
		try {
			return ServiceModelUtils.isServiceOurs(service, controller.getCommManager());
		} catch (Exception e) {
			log.error("Exception accessing method on ServiceModelUtils: {}", e.getMessage());
			e.printStackTrace();
			return false;
		} 
	}
	
	@Override
	public boolean equals(Object obj){
	
		if(obj.getClass().equals(ServiceWrapper.class)){
			//Now we compare the SRI
			ServiceResourceIdentifier objSri = ((ServiceWrapper) obj).getService().getServiceIdentifier();
			return ServiceModelUtils.compare(service.getServiceIdentifier(), objSri);
		} else
			return false;
		
	}
	
	@Override
	public int hashCode(){
		return ServiceModelUtils.serviceResourceIdentifierToString(service.getServiceIdentifier()).hashCode();
	}
	
	public String getId(){
		return ServiceModelUtils.serviceResourceIdentifierToString(service.getServiceIdentifier());
	}
	

}

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
package org.societies.platform.servicelifecycle.servicecontrol;

import java.util.concurrent.Future;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.api.services.IServices;

/**
 * Implementation bean, this will help third-party services to get access to their data
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class ServiceMgmt implements IServices {

	static final Logger logger = LoggerFactory.getLogger(ServiceMgmt.class);
	
	private ICommManager commMngr;
	private IServiceDiscovery serviceDiscovery;
	private IServiceControl serviceControl;

	
	public IServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
	}

	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}

	public void setCommMngr(ICommManager commMngr) {
		this.commMngr = commMngr;
	}
	
	public ICommManager getCommMngr() {
		return commMngr;
	}
	
	public IServiceControl getServiceControl(){
		return serviceControl;
	}
	
	public void setServiceControl(IServiceControl serviceControl){
		this.serviceControl = serviceControl;
	}
	
	@Override
	public ServiceResourceIdentifier getMyServiceId(Class<?> callingClass) {
		
		try{
			
			logger.debug("Trying to get ServiceResourceIdentifier for class: {}", callingClass);
			
			// First we get the calling Bundle
			Bundle serviceBundle = FrameworkUtil.getBundle(callingClass);
		
			logger.debug("Bundle of service is: {}",serviceBundle.getSymbolicName());
		
			logger.debug("Bundle location is: {}",serviceBundle.getLocation());
			
			Service myService = ServiceModelUtils.getServiceFromBundle(serviceBundle, getServiceDiscovery());
		
			logger.debug("Service is {}", myService.getServiceName());
			
			if(myService != null){
				return myService.getServiceIdentifier();
			}
			else{
				return null;
			}
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception occured: " + ex.getMessage());
			return null;
		}
	}



	@Override
	public IIdentity getServer(ServiceResourceIdentifier serviceId) {
		try{
			
			// First we get the calling Bundle
			Future<Service> serviceAsync = getServiceDiscovery().getService(serviceId);
			Service myService = serviceAsync.get();
			
			if(logger.isDebugEnabled())
				logger.debug("myService is" + myService.getServiceName());
		
			String parentJid = myService.getServiceInstance().getParentJid();
			
			if(parentJid == null){
				if(logger.isDebugEnabled())
					logger.debug("no Parent Jid!");
				return null;
				
			}
			
			return getCommMngr().getIdManager().fromFullJid(parentJid); 
		
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exceptioon occured: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public String getMyName(ServiceResourceIdentifier serviceId) {
		try{
			
			// First we get the calling Bundle
			Future<Service> serviceAsync = getServiceDiscovery().getService(serviceId);
			Service myService = serviceAsync.get();
			
			if(logger.isDebugEnabled())
				logger.debug("myService is" + myService.getServiceName());
		
			return myService.getServiceName();
		
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception occured: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public String getMyVersion(ServiceResourceIdentifier serviceId) {
		
		try{
			
			// First we get the calling Bundle
			Future<Service> serviceAsync = getServiceDiscovery().getService(serviceId);
			Service myService = serviceAsync.get();
			
			if(logger.isDebugEnabled())
				logger.debug("myService Version is" + myService.getServiceInstance().getServiceImpl().getServiceVersion());
		
			return myService.getServiceInstance().getServiceImpl().getServiceVersion();
		
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception occured: " + ex.getMessage());
			return null;
		}
		
	}

	@Override
	public String getMyCategory(ServiceResourceIdentifier serviceId) {
		
		try{
			
			// First we get the calling Bundle
			Future<Service> serviceAsync = getServiceDiscovery().getService(serviceId);
			Service myService = serviceAsync.get();
			
			if(logger.isDebugEnabled())
				logger.debug("myService category is" + myService.getServiceCategory());
		
			return myService.getServiceCategory();
		
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception occured: " + ex.getMessage());
			return null;
		}
		
	}
	
	@Override
	public ServiceResourceIdentifier getServerServiceIdentifier(
			ServiceResourceIdentifier serviceId) {

		try{
			
			// First we get the calling Bundle
			Future<Service> serviceAsync = getServiceDiscovery().getService(serviceId);
			Service myService = serviceAsync.get();
			
			if(logger.isDebugEnabled())
				logger.debug("myService Parent Id is" + myService.getServiceInstance().getParentIdentifier());
		
			return myService.getServiceInstance().getParentIdentifier();
		
		} catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception occured: " + ex.getMessage());
			return null;
		}

	}

	@Override
	public boolean compare(ServiceResourceIdentifier serviceId,
			ServiceResourceIdentifier otherServiceId) {
		
		if(serviceId == null || otherServiceId == null)
			return false;
		
		if(serviceId.getIdentifier().equals(otherServiceId.getIdentifier()) && serviceId.getServiceInstanceIdentifier().equals(otherServiceId.getServiceInstanceIdentifier()))
			return true;
		else
			return false;
	}
	
	@Override
	public boolean shareService(ServiceResourceIdentifier serviceId, IIdentity node){
		
		logger.info("ServiceManagement: sharing a service with a CIS");
		
		boolean result = false;
		
		try{
			// First we get the calling Bundle
			Future<Service> serviceAsync = getServiceDiscovery().getService(serviceId);
			Service myService = serviceAsync.get();
			
			logger.debug("Found service: {}", myService.getServiceName());
			
			Future<ServiceControlResult> shareAsync = getServiceControl().shareService(myService, node);
			ServiceControlResult shareResult = shareAsync.get();
			
			if(shareResult.getMessage().equals(ResultMessage.SUCCESS)){
				logger.debug("Sharing with {} was successful.", node.getJid());
				result = true;
			}
		} catch(Exception ex){
			logger.error("Exception occured: " + ex);
			ex.printStackTrace();
			result=false;
		} 

		return result;
	}
	
	
	@Override
	public boolean unshareService(ServiceResourceIdentifier serviceId, IIdentity node){
		
		logger.info("ServiceManagement: usharing a service with a CIS");
		
		boolean result = false;
		
		try{
			// First we get the calling Bundle
			Future<Service> serviceAsync = getServiceDiscovery().getService(serviceId);
			Service myService = serviceAsync.get();
			
			logger.debug("Found service: {}", myService.getServiceName());
			
			Future<ServiceControlResult> shareAsync = getServiceControl().unshareService(myService, node);
			ServiceControlResult shareResult = shareAsync.get();
			
			if(shareResult.getMessage().equals(ResultMessage.SUCCESS)){
				logger.debug("Sharing with {} was successful.", node.getJid());
				result = true;
			}
		} catch(Exception ex){
			logger.error("Exception occured: " + ex);
			ex.printStackTrace();
			result=false;
		} 

		return result;
	}
	
	

}

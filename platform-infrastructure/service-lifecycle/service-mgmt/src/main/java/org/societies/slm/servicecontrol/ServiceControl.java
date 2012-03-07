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
package org.societies.slm.servicecontrol;

import java.net.URL;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.servicelifecycle.IServiceControl;
import org.societies.api.servicelifecycle.ServiceControlException;
import org.springframework.osgi.context.BundleContextAware;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class ServiceControl implements IServiceControl, BundleContextAware {

	private BundleContext bundleContext;
	
	static final Logger logger = LoggerFactory.getLogger(ServiceControl.class);
			
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		
		this.bundleContext = bundleContext;

		if(logger.isDebugEnabled()) logger.debug("BundleContextSet");
	}

	/* (non-Javadoc)
	 * @see org.societies.api.servicelifecycle.IServiceControl#startService(org.societies.api.servicelifecycle.model.IServiceResourceIdentifier)
	 */
	@Override
	public void startService(ServiceResourceIdentifier serviceId)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: startService method");

		try{
			// Get the service from the repository
			if(logger.isDebugEnabled()) logger.debug("Attempting to get service from registry");
			Service serviceToStart = null; //getServiceReg().retrieveService(serviceId);
			
			// Does it exist?
			if(serviceToStart == null){
				logger.info("Service " + serviceId.getIdentifier() + " not found!");
				return;
			}
			
			// It exists, so we do whatever we need to do to start the service.
			
			
			// After it starts, we set the status to started
			//serviceToStart.setServiceStatus(ServiceStatus.STARTED);
			if(logger.isInfoEnabled()) logger.info("Service " + serviceToStart.getServiceName() + " has been started.");
			
			// And update it in the repository
			if(logger.isDebugEnabled()) logger.debug("Telling repository to update!");
			
		} catch(Exception ex){
			logger.error("Exception occured while starting Service: " + ex.getMessage());
			throw new ServiceControlException("Exception occured while starting Service.", ex);
		}

	}

	/* (non-Javadoc)
	 * @see org.societies.api.servicelifecycle.IServiceControl#stopService(org.societies.api.servicelifecycle.model.IServiceResourceIdentifier)
	 */
	@Override
	public void stopService(ServiceResourceIdentifier serviceId)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: stopService method");

		try{
			// Get the service from the repository
			if(logger.isDebugEnabled()) logger.debug("Attempting to get service from registry");
			Service serviceToStop = null; //getServiceReg().retrieveService(serviceId);
			
			// Does it exist?
			if(serviceToStop == null){
				logger.info("Service " + serviceId.getIdentifier() + " not found!");
				return;
			}
		
			// It exists, so we do whatever we need to do to stop the service.

			// After it starts, we set the status to started
			//serviceToStop.setServiceStatus(ServiceStatus.STARTED);
			if(logger.isInfoEnabled()) logger.info("Service " + serviceToStop.getServiceName() + " has been stopped.");
			
			// And update it in the repository
			if(logger.isDebugEnabled()) logger.debug("Telling repository to update!");
			
			
		} catch(Exception ex){
			logger.error("Exception occured while stopping Service: " + ex.getMessage());
			throw new ServiceControlException("Exception occured while stopping Service.", ex);
		}

	}

	/* (non-Javadoc)
	 * @see org.societies.api.servicelifecycle.IServiceControl#installService(java.net.URL)
	 */
	@Override
	public void installService(URL bundleLocation)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: installService method");

		
		String serviceBundlelocation = null;
		try {
			bundleContext.installBundle(serviceBundlelocation);
		} catch (Exception ex) {
			logger.error("Exception while attempting to install a bundle: " + ex.getMessage());
			throw new ServiceControlException("Exception while attempting to install a bundle.", ex);
		}

	}

	/* (non-Javadoc)
	 * @see org.societies.api.servicelifecycle.IServiceControl#uninstallService(org.societies.api.servicelifecycle.model.IServiceResourceIdentifier)
	 */
	@Override
	public void uninstallService(ServiceResourceIdentifier serviceId)
			throws ServiceControlException {
		
		if(logger.isDebugEnabled()) logger.debug("Service Management: uninstallService method");

	}

}

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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.servicelifecycle.model.Service;

/**
 * Event Listener for SOCIETIES events
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class SocietiesEventListener extends EventListener {

	
	static final Logger logger = LoggerFactory.getLogger(SocietiesEventListener.class);
	private ServiceControl parent;
	private String[] eventTypes;
	private String eventFilter;
			
	public SocietiesEventListener(ServiceControl parent) {
		logger.debug("ServiceManagement: SOCIETIES Event Listener");
		this.parent = parent;
		this.eventFilter = null;
		
		eventTypes = new String[] {EventTypes.CIS_DELETION, EventTypes.CIS_UNSUBS};
		parent.getEventMgr().subscribeInternalEvent(this, eventTypes, eventFilter);
	}
	
	public void unregister(){
		
		if(logger.isDebugEnabled())
			logger.debug("Unregistering from CIS Events...");
		
		parent.getEventMgr().unSubscribeInternalEvent(this, eventTypes, eventFilter);
		
	}

	public void handleInternalEvent(InternalEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("Received an InternalEvent:" + event.geteventType());
		
		try{
			Community eventInfo = (Community) event.geteventInfo();
			List<Service> servicesSharedWithCis = parent.getServiceReg().retrieveServicesSharedByCIS(eventInfo.getCommunityJid());

			if(event.geteventType().equals(EventTypes.CIS_DELETION)){
				if(logger.isDebugEnabled())
					logger.debug("CIS deleted: " + eventInfo.getCommunityName());
				
				// We remove all services that are shared with this CIS
				parent.getServiceReg().clearServiceSharedCIS(eventInfo.getCommunityJid());
				
				List<String> sharedCis;
				List<Service> servicesList = new ArrayList<Service>();
				for(Service sharedService: servicesSharedWithCis){
					
					sharedCis = parent.getServiceReg().retrieveCISSharedService(sharedService.getServiceIdentifier());
					if(!ServiceModelUtils.isServiceOurs(sharedService, parent.getCommMngr()) && sharedCis.isEmpty()){
						if(logger.isDebugEnabled())
							logger.debug("Service "+ sharedService.getServiceName() +" isn't ours and is not shared by any other CIS, so we delete it!");
						servicesList.add(sharedService);
					} else{
						if(logger.isDebugEnabled())
							logger.debug("Service "+ sharedService.getServiceName() +" is still shared by other CIS, so we need to keep it in the repository.");
					}
				}
				
				parent.getServiceReg().unregisterServiceList(servicesList);
				

			} else{
				if(event.geteventType().equals(EventTypes.CIS_UNSUBS)){
					if(logger.isDebugEnabled())
						logger.debug("We have unsubscribed from a CIS: " + eventInfo.getCommunityJid());
					
					for(Service sharedService: servicesSharedWithCis){
						if(ServiceModelUtils.isServiceOurs(sharedService, parent.getCommMngr())){
							parent.unshareService(sharedService,eventInfo.getCommunityJid());
						}
					}
					
				}
			}
			
		} catch(Exception ex){
			logger.error("Exception while processing event!");
			ex.printStackTrace();
		}
		
	}


	public void handleExternalEvent(CSSEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("External Event received, do nothing!" +event.geteventName());
		
	}
}

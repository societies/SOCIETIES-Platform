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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
import org.societies.api.services.ServiceMgmtEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
@Service
@ManagedBean
@ApplicationScoped
public class ServiceEventListener extends EventListener implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(ServiceEventListener.class);
	private Set<ServicesController> controllers;
	
	@ManagedProperty(value= "#{eventManager}")
    private IEventMgr eventManager; 
    
    
    public IEventMgr getEventManager() {
		return eventManager;
	}

	public void setEventManager(IEventMgr eventManager) {
		this.eventManager = eventManager;
	}
	
	public ServiceEventListener() {
		if(log.isDebugEnabled())
			log.debug("Service Management Listener for Webapp created!");
		controllers = new HashSet<ServicesController>();
	}

	@PostConstruct
	public void init(){
		log.debug("Service Management Listener for Webapp: init!");
		getEventManager().subscribeInternalEvent(this, new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT}, null);
	}
	
	@PreDestroy
	public void unsubscribe(){
		this.getEventManager().unSubscribeInternalEvent(this, new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT}, null);
		if(log.isDebugEnabled())
			log.debug("Service Management Listener for Webapp: unregistered!");
	}
	
	public void registerController(ServicesController controller){
		controllers.add(controller);
	}
	
	public void unregisterController(ServicesController controller){
		controllers.remove(controller);
	}
	
	@Override
	public void handleInternalEvent(InternalEvent event) {
		log.debug("InternalEvent arrived: {}",event.geteventName());

		
		if(event.geteventType().equals(EventTypes.SERVICE_LIFECYCLE_EVENT)){
			ServiceMgmtEvent ourEvent = (ServiceMgmtEvent) event.geteventInfo();
			log.debug("Received event is of type: {} for serviceId {}",ourEvent.getEventType(),ourEvent.getServiceId());
				
			if(ourEvent.getServiceId() == null){
				log.warn("Service Id of received event was null! This should not happen, aborting!");
				return;
			}
				
			log.debug("We have {} ServicesControllers registered, we must iterate!",controllers.size());
				
			Iterator<ServicesController> controllerIterator = controllers.iterator();
			ServicesController controller;
				
			while(controllerIterator.hasNext()){
				
				controller = controllerIterator.next();
				try{
					
					log.debug("Iterating for service controller: {}",controller);
					
					ServiceWrapper service = controller.getService(ourEvent.getServiceId());
					
					switch(ourEvent.getEventType()){
						case NEW_SERVICE:
							if(ourEvent.getServiceType().equals(ServiceType.THIRD_PARTY_CLIENT) || ourEvent.getServiceType().equals(ServiceType.DEVICE))
								controller.serviceInstalled(ourEvent.getServiceId());
							break;
						case SERVICE_REMOVED: 
							if(controller.getServiceId()==null){
								controller.serviceRemoved(ourEvent.getServiceId(), ourEvent.getServiceName(),ourEvent.getServiceType());
							} else{
								log.debug("Event is being dealt with by the controller, so no need to be processed!");
							}
							break;
						case SERVICE_SHARED: 
							if(controller.getServiceId()==null){
								if(service != null && service.isSharedWithCis(ourEvent.getSharedNode().getJid())){
									log.debug("Service {} is already shared with this CIS, no need to process event.", ourEvent.getServiceName() );
									break;
								}
								controller.serviceShared(ourEvent.getServiceId(), ourEvent.getServiceName(), ourEvent.getSharedNode());
							}  else{
								log.debug("Event is being dealt with by the controller, so no need to be processed!");
							}
							break;
							
						case SERVICE_UNSHARED:
							if(controller.getServiceId()==null){
								if(service != null && !service.isSharedWithCis(ourEvent.getSharedNode().getJid())){
									log.debug("Service {} is already NOT shared with this CIS, no need to process event.", ourEvent.getServiceName() );
									break;
								}
								controller.serviceUnshared(ourEvent.getServiceId(), ourEvent.getServiceName(), ourEvent.getSharedNode());

							} else{
								log.debug("Event is being dealt with by the controller, so no need to be processed!");
							}
							break;
						case SERVICE_STARTED: 
							if(controller.getServiceId()==null){
								if(service != null && service.isStarted()){
									log.debug("Service {} is already started, no need to process event.", ourEvent.getServiceName() );
									break;
								}
								controller.serviceStarted(ourEvent.getServiceId(), ourEvent.getServiceName());
							} else{
								log.debug("Event is being dealt with by the controller, so no need to be processed!");
							}
							break;
						case SERVICE_STOPPED:
							if(controller.getServiceId()==null){
								if(service != null && service.isStopped()){
									log.debug("Service {} is already stopped, no need to process event.", ourEvent.getServiceName() );
									break;
								}
								controller.serviceStopped(ourEvent.getServiceId(), ourEvent.getServiceName());
							} else{
								log.debug("Event is being dealt with by the controller, so no need to be processed!");
							}
							break;
						case PROBLEM_OCURRED:
							controller.installFailed(ourEvent.getServiceId(), ourEvent.getServiceName());
							break;
					default: log.debug("Unknown event! {}",ourEvent.getEventType());
					}
					
				} catch(Exception ex){
					log.error("Error in this listener, so we unregister!");
					unregisterController(controller);
					ex.printStackTrace();
				}
			}
		}
		
	}


	@Override
	public void handleExternalEvent(CSSEvent event) {
		if(log.isDebugEnabled())
			log.debug("Nothing to do with a CSSEvent!");
		
	}
	


}

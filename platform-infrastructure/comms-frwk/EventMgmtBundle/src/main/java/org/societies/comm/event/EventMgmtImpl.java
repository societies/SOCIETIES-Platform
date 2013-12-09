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
package org.societies.comm.event;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.springframework.osgi.context.BundleContextAware;
/**
 * 
 * @author pkuppuud
 *
 */
public class EventMgmtImpl implements IEventMgr, BundleContextAware {

	private BundleContext bc;
	private EventAdmin eventAdmin;	
	private Map<EventListener, ServiceRegistration> serRegMap = new HashMap<EventListener, ServiceRegistration>();

	static final Logger logger = LoggerFactory.getLogger(EventMgmtImpl.class);

	public EventAdmin getEventAdmin() {
		return eventAdmin;
	}

	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	/**
	 * @see IEventMgr
	 * @param listener
	 * @param eventType
	 * @param filterOption
	 */
	public void subscribeInternalEvent(EventListener listener, String[] eventTypes,
			String filterOption) {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, eventTypes);
		if (filterOption != null) {
			properties.put(EventConstants.EVENT_FILTER, filterOption);
		}
		serRegMap.put(listener, bc.registerService(
				EventHandler.class.getName(), listener, properties));
		logger.debug("{} Registered for event: {}",listener,eventTypes);
		logger.debug("We now have {} registered listeners.",serRegMap.size());
	}

	public void unSubscribeInternalEvent(EventListener listener, String[] eventTypes,
			String filterOption) {

		if (serRegMap.containsKey(listener)) {
			(serRegMap.get(listener)).unregister();
			serRegMap.remove(listener);
			logger.debug("{} Unregistered for event: {}",listener,eventTypes);
			logger.debug("We now have {} registered listeners.",serRegMap.size());
		} else {
			logger.debug("Listener object {} does not exists to unregister: {}",listener,eventTypes);
		}
	}

	public void publishInternalEvent(final InternalEvent event) throws EMSException {
		if (getEventAdmin() != null) {
			final Dictionary<String, Object> properties = new Hashtable<String, Object>();
			properties.put(CSSEventConstants.EVENT_TARGET,CSSEventConstants.INTERNAL_EVENT);
			properties.put(CSSEventConstants.EVENT_NAME, event.geteventName());
			properties.put(CSSEventConstants.EVENT_SOURCE,event.geteventSource());
			properties.put(CSSEventConstants.EVENT_INFO, event.geteventInfo());
			if (this.eventAdmin!=null){
				new Thread() {

					@Override
					public void run() {

						getEventAdmin().postEvent(
								new Event(event.geteventType(), properties));
						logger.debug("Posted event: {} with name {}",event.geteventType(),event.geteventName());

					}
				}.start();
			}else{
				this.logger.error("EventAdmin service is null");
			}

		} else {
			throw new EMSException(
					"Could not get OSG Event Admin Service, therefore event was not posted");
		}
	}

	@Override
	public void setBundleContext(BundleContext bc) {
		logger.debug("Bundle context is set for event mgmt system");
		this.bc = bc;
	}
}

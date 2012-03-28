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
package org.societies.css.devicemgmt.devicemanagerconsumer;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;


/**
 * Consumer of DeviceDriverSimulator3
 * @author Olivier Maridat (Trialog)
 *
 */
public class DeviceManagerConsumer extends EventListener  {
	private static Logger LOG = LoggerFactory.getLogger(DeviceManagerConsumer.class.getSimpleName());

	private IDevice deviceService;
	private IEventMgr eventManager;

	public DeviceManagerConsumer() {
	}

	public void initConsumer()
	{
		// -- Track LightSensor (to know what to subscribe)
		IDriverService driverService = deviceService.getService("lightSensorService");
		if (null != driverService) {
			IAction getEventNames = driverService.getAction("getEventNames");
			if (null != getEventNames) {
				Dictionary dic = getEventNames.invokeAction(null);
				List<String> eventNames = (List<String>) dic.get("outputEventNames");
				if (null != eventNames) {
					LOG.info("Event names: ");
					for(String eventName : eventNames) {
						LOG.info(eventName+", ");
					}
				}
				else {
					LOG.info("No event names: ");
				}
			}
			else {
				LOG.info("Get event names action not available");
			}
		}
		else {
			LOG.info("IDeviceService lightSensorService is not available");
		}

		// -- Subscribe to LightSensorEvent
		// Set filter
		String eventFilter = "(&" + 
				"(" + CSSEventConstants.EVENT_NAME + "=Sensor/LightSensorEvent)" + 
				//"(" + CSSEventConstants.EVENT_SOURCE + "=test_event_source)" + 
				")";
		// Subscribe
		eventManager.subscribeInternalEvent(this, new String[] {"org/societies/css/device"}, eventFilter);
		LOG.info("Subscribe to internal event: org/societies/css/device -> Sensor/LightSensorEvent");
	}

	@Override
	public void handleInternalEvent(InternalEvent event) {
		LOG.info("*** Internal event received *****");    
		LOG.info("** event name : "+ event.geteventName());
		LOG.info("** event source : "+ event.geteventSource());
		LOG.info("** event type : "+event.geteventType());
		if (event.geteventName().equals("Sensor/LightSensorEvent")) {
			HashMap<String, Object> payload = (HashMap<String, Object>)event.geteventInfo();
			LOG.info("** lightLevel : "+ payload.get("lightLevel"));
		}
	}

	@Override
	public void handleExternalEvent(CSSEvent event) {
		LOG.info("*** External event received *****");    
	}


	/* --- Injections --- */
	public IEventMgr getEventManager() { return eventManager; }
	public void setEventManager(IEventMgr eventManager) { 
		if (null == eventManager) {
			LOG.error("[COMM02] EventManager not available");
		}
		this.eventManager = eventManager;
	}

	public IDevice getDeviceService() {	return deviceService; }
	public void setDeviceService(IDevice deviceService)
	{
		if (null == deviceService) {
			LOG.error("[DEVI02] DeviceService not available");
		}
		this.deviceService = deviceService;
	}
}
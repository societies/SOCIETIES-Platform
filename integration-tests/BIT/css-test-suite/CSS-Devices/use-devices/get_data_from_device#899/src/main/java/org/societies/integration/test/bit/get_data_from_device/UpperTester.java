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
package org.societies.integration.test.bit.get_data_from_device;


import java.util.HashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.model.DeviceMgmtEventConstants;
import org.societies.api.css.devicemgmt.model.DeviceTypeConstants;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.springframework.osgi.context.BundleContextAware;

/**
 *
 * @author Rafik
 *
 */
public class UpperTester extends EventListener implements ServiceTrackerCustomizer, BundleContextAware{
	
	
	private BundleContext bundleContext;
	
	private static Logger LOG = LoggerFactory.getLogger(UpperTester.class);
	
	public static IDevice ls1 = null;
	public static IDevice ls2 = null;
	public static IDevice ls3 = null;
	public static IDevice screen = null;
	
	private ServiceTracker serviceTracker;
	
	private IEventMgr eventManager;
	
	public UpperTester (){
		
	}
	

	public IEventMgr getEventManager() 
	{
		return eventManager;
	}
	
	public void setEventManager(IEventMgr eventManager) 
	{ 
		if (null == eventManager) 
		{
			LOG.error("[COMM02] EventManager not available");
		}
		this.eventManager = eventManager;
	}
	
	
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}
	
	/**
	 * @return the bundleContext
	 */
	public BundleContext getBundleContext() {
		return bundleContext;
	}
	
	public void initUpperTester()
	{
		
		this.serviceTracker = new ServiceTracker(bundleContext, IDevice.class.getName(), this);
		this.serviceTracker.open();

		// Set filter
		String lightEventFilter = 	"(|"+
										"(" + CSSEventConstants.EVENT_NAME + "=" + DeviceMgmtEventConstants.LIGHT_SENSOR_EVENT + ")"+
										"(" + CSSEventConstants.EVENT_NAME + "="+DeviceMgmtEventConstants.SCREEN_EVENT+")"+
									")";
		
		// Subscribe
		eventManager.subscribeInternalEvent(this, new String[] {EventTypes.DEVICE_MANAGEMENT_EVENT}, lightEventFilter);
		//LOG.info("Subscribe to internal event: org/societies/css/device -> sensor/lightSensorEvent");
	}


	@Override
	public Object addingService(ServiceReference reference) {
		Object obj = bundleContext.getService(reference);

		IDevice iDevice = (IDevice)obj;

		if (iDevice != null) 
		{ 		
			if (iDevice.getDeviceType() == DeviceTypeConstants.LIGHT_SENSOR) 
			{
				if (iDevice.getDeviceLocation().equalsIgnoreCase("Room1")) 
				{
					LOG.info("UpperTester > addingService =============== " + iDevice.getDeviceId());
					ls1 = iDevice;
					
				}
				else if (iDevice.getDeviceLocation().equalsIgnoreCase("Room2")) 
				{
					LOG.info("UpperTester > addingService =============== " + iDevice.getDeviceId());
					ls2 = iDevice;
				}
				else if (iDevice.getDeviceLocation().equalsIgnoreCase("Room3")) 
				{
					LOG.info("UpperTester > addingService =============== " + iDevice.getDeviceId());
					ls3 = iDevice;
				}
			}
			else if (iDevice.getDeviceType() == DeviceTypeConstants.SCREEN)
			{
				if (iDevice.getDeviceLocation().equalsIgnoreCase("Corridor1")) 
				{
					LOG.info("UpperTester > addingService =============== " + iDevice.getDeviceId());
					screen = iDevice;
				}
			}
		}
		return null;
	}


	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		LOG.info("UpperTester: ############################### modifiedService");
	}


	@Override
	public void removedService(ServiceReference reference, Object service) {
		
		LOG.info("UpperTester: ############################### removedService");
		
	}



	@Override
	public void handleInternalEvent(InternalEvent event) {
		
		LOG.info("*** Internal event received *****");    
		LOG.info("** event name : "+ event.geteventName());
		LOG.info("** event source : "+ event.geteventSource());
		LOG.info("** event type : "+event.geteventType());
		if (event.geteventName().equals(DeviceMgmtEventConstants.LIGHT_SENSOR_EVENT)) {
			HashMap<String, Object> payload = (HashMap<String, Object>)event.geteventInfo();
			LOG.info("DeviceMgmtConsumer %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% handleEvent lightLevel : "+ payload.get("lightLevel"));
		}
		else if (event.geteventName().equals(DeviceMgmtEventConstants.SCREEN_EVENT))
		{
			HashMap<String, Object> payload = (HashMap<String, Object>)event.geteventInfo();
			LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% handleEvent screenMessage: " + payload.get("screenEvent"));
		}
		
	}



	@Override
	public void handleExternalEvent(CSSEvent event) {
		
	}

}

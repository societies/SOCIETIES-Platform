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
package org.societies.context.source.impl;

import java.util.HashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.context.source.ICtxSourceMgr;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.internal.css.devicemgmt.IDeviceManager;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;

@Service
public class NewDeviceListener extends EventListener implements ServiceTrackerCustomizer, BundleContextAware {
	
	private static Logger LOG = LoggerFactory.getLogger(NewDeviceListener.class);
	
	private BundleContext bundleContext;
	
	private ServiceTracker serviceTracker;

	private IDeviceManager deviceManager;
	
	private IEventMgr eventManager;
	
	private ICtxSourceMgr csm;
	
	@Autowired(required=true)
	public NewDeviceListener(IDeviceManager deviceManager, 
			IEventMgr eventManager, ICtxSourceMgr contextSourceManagement) {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		this.deviceManager = deviceManager;
		this.eventManager = eventManager;
		this.csm = contextSourceManagement;
	}
	
	/*
	 * @see org.springframework.osgi.context.BundleContextAware#setBundleContext(org.osgi.framework.BundleContext)
	 */
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Setting bundle context " + bundleContext);
		this.bundleContext = bundleContext;
		this.registerDevicesAndUpdates();
	}	

	public void registerDevicesAndUpdates() {

		if (LOG.isDebugEnabled())
			LOG.debug("Subscribing ServiceTracker for " + IDevice.class.getName() + " services");
		if (bundleContext!=null) {
			// subscribe for all new devices
			this.serviceTracker = new ServiceTracker(bundleContext, IDevice.class.getName(), this);
			this.serviceTracker.open();
		} else {
			LOG.error("Could not subscribe ServiceTracker for " + IDevice.class.getName() + " services");
		}
		
		// Subscribe to all device management events
		// empty filter represented by "null"
		if (LOG.isDebugEnabled())
			LOG.debug("Subscribing for internal events of type " + EventTypes.DEVICE_MANAGEMENT_EVENT);
		if (this.eventManager != null)
			this.eventManager.subscribeInternalEvent(this, new String[] { EventTypes.DEVICE_MANAGEMENT_EVENT }, null);
		else 
			LOG.error("Could not subscribe for internal events of type " + EventTypes.DEVICE_MANAGEMENT_EVENT
					+ "Event Mgr service is not available");
	}

	/**
	 * Called by OSGI via ServiceTracker for all registered services. the only registered Service is IDevice, 
	 * 
	 * @see #registerDevicesAndUpdates(org.osgi.framework.ServiceReference)
	 */
	@Override
	public Object addingService(ServiceReference reference) 
	{
		Object obj = bundleContext.getService(reference);

		IDevice iDevice = (IDevice)obj;
		if (!iDevice.isContextSource())
			return null;
		
		String sourceName;
		for (String eventName : iDevice.getEventNameList()){
			sourceName = iDevice.getDeviceName() + "#" + eventName;
			// !! DANGEROUS CODE !!
			((ContextSourceManagement) csm).registerFull(null, sourceName, eventName, iDevice.getDeviceId());
		}

		return iDevice;
	}

	/*
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("NewDeviceListener has received modifiedService event: " + reference.getBundle());
	}

	/*
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	@Override
	public void removedService(ServiceReference reference, Object service) {
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("NewDeviceListener has received removedService event: "+reference.getBundle());
		
		IDevice toRemove = (IDevice)bundleContext.getService(reference);
		String sourceName;
		for (String eventName : toRemove.getEventNameList()){
			sourceName = toRemove.getDeviceName() + "#" + eventName;
			csm.unregister(sourceName);
		}
	}

	/**
	 * Is called when devices send updates. 
	 */
	@Override
	public void handleInternalEvent(InternalEvent event) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received internal event: name=" + event.geteventName()
					+ ", source="+ event.geteventSource() + ", type=" + event.geteventType()
					+ ", payload=" + event.geteventInfo());
		
		if (!(event.geteventInfo() instanceof HashMap<?, ?>)) {
			LOG.error("Could not handle '" + event.geteventType() + "' event: "
					+ "Unexpected event info: " + event.geteventInfo());
			return;
		}
		@SuppressWarnings("unchecked")
		HashMap<String, Object> payload = (HashMap<String, Object>) event.geteventInfo();
		
		final String sourceName = event.geteventSource() + "#" + event.geteventName();
		this.csm.sendUpdate(sourceName, payload);
	}

	/**
	 * should not be called - no registration for external events is necessary.
	 */
	@Override
	public void handleExternalEvent(CSSEvent event) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received external event: name=" + event.geteventName()
					+ ", source="+ event.geteventSource() + ", type=" + event.geteventType());
	}

	/**
	 * 
	 */
	public void stop() {
		
		//unregister all events
		//empty filter means null
		eventManager.unSubscribeInternalEvent(this, new String[]{EventTypes.DEVICE_MANAGEMENT_EVENT}, null);
		serviceTracker.close();
	}
	
	/* --- Injections --- */
	/**
	 * @return the deviceManager
	 */
	public IDeviceManager getDeviceManager() {
		return deviceManager;
	}

	/**
	 * @param deviceManager the deviceManager to set
	 */
	public void setDeviceManager(IDeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}
}
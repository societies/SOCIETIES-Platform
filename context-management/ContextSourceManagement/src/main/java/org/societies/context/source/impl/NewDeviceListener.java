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
public class NewDeviceListener extends EventListener implements ServiceTrackerCustomizer, BundleContextAware{
	private static Logger LOG = LoggerFactory.getLogger(ContextSourceManagement.class);
	private BundleContext bundleContext;
	private ServiceTracker serviceTracker;

	private IDeviceManager deviceManager;
	private IEventMgr eventManager;
	
	private ICtxSourceMgr csm;
	private boolean RUNNING_MODE = true;

	private String filterOption = "(&" + 
				//"(" + CSSEventConstants.EVENT_NAME + "="+DeviceMgmtEventConstants.LIGHT_SENSOR_EVENT+")" + //example 
				// "(" + CSSEventConstants.EVENT_NAME + "="+DeviceMgmtEventConstants.SCREEN_EVENT+")" +  //example
				//"(" + CSSEventConstants.EVENT_SOURCE + "=test_event_source)" +  //example
				")";
	
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
	
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Setting bundle context " + bundleContext);
		this.bundleContext = bundleContext;
		this.registerDevicesAndUpdates();
	}	
	
	@Autowired(required=true)
	public NewDeviceListener(IDeviceManager deviceManager, 
			IEventMgr eventManager, ICtxSourceMgr contextSourceManagement) {
		this.deviceManager = deviceManager;
		this.eventManager = eventManager;
		this.csm = contextSourceManagement;

		LOG.info(this.getClass() + " instantiated");
		//registerDevicesAndUpdates();
	}

	

	public void registerDevicesAndUpdates() {

		if (bundleContext!=null){
			// subscribe for all new devices
			this.serviceTracker = new ServiceTracker(bundleContext, IDevice.class.getName(), this);
			this.serviceTracker.open();
		}
		else
			LOG.error("BundleContext="+bundleContext+"\n\tNo device services will be found!");
		
//		while(RUNNING_MODE){
//			try {
//				Thread.sleep(10000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			if (LOG.isDebugEnabled())
//				LOG.debug("NewDeviceListener: keeps looking");
//		}

		// Subscribe to all device management events
		// empty filter represented by "null"
		LOG.info("eventManager="+eventManager);
		eventManager.subscribeInternalEvent(this, new String[] {EventTypes.DEVICE_MANAGEMENT_EVENT}, null);
		LOG.debug("Subscribe to all internal events of device management: org/societies/css/device");
		
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

	
	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		if (LOG.isDebugEnabled()) LOG.debug("NewDeviceListener has received modifiedService event: "+reference.getBundle());
	}

	
	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	@Override
	public void removedService(ServiceReference reference, Object service) {
		if (LOG.isDebugEnabled()) LOG.debug("NewDeviceListener has received removedService event: "+reference.getBundle());
		
		IDevice toRemove = (IDevice)bundleContext.getService(reference);
		String sourceName;
		for (String eventName : toRemove.getEventNameList()){
			sourceName = toRemove.getDeviceName() + "#" + eventName;
			csm.unregister(sourceName);
		}

	}
	

	
	
//	public void handleEvent(Event event) {
//		
//		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% handleEvent ");
//		if (event.getProperty("event_name").equals(DeviceMgmtEventConstants.LIGHT_SENSOR_EVENT)) 
//		{
//			ll = (Double)event.getProperty("lightLevel");
//			LOG.info("DeviceMgmtConsumer: ***********%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% handleEvent lightLevel: " + ll);
//		}
//		else if (event.getProperty("event_name").equals(DeviceMgmtEventConstants.SCREEN_EVENT))
//		{
//			screenMessage = (String)event.getProperty("screenEvent");
//			LOG.info("DeviceMgmtConsumer: ***********%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% handleEvent screenMessage: " + screenMessage);
//		}
//	}


	/**
	 * Is called when devices send updates.
	 * 
	 */
	@Override
	public void handleInternalEvent(InternalEvent event) {
		LOG.debug("*** Internal event received *****");    
		LOG.debug("** event name : "+ event.geteventName());
		LOG.debug("** event source : "+ event.geteventSource());
		LOG.debug("** event type : "+event.geteventType());
		

		@SuppressWarnings("unchecked")
		HashMap<String, Object> payload = (HashMap<String, Object>)event.geteventInfo();
		
		//		sourceName = iDevice.getDeviceName() + "#" + eventName;
		String sourceName = event.geteventSource() + "#" + event.geteventName();
		
		csm.sendUpdate(sourceName, payload);
		LOG.debug("CSM-DM-Integration: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% handleEvent "+event.geteventName() +" from " +event.geteventSource() + ":\t" + payload.get(event.geteventName()));
		LOG.debug("CSM-DM-Integration: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Source "+sourceName+ " has sent update "+payload);
		
		/*
		if (event.geteventName().equals(DeviceMgmtEventConstants.LIGHT_SENSOR_EVENT)) {
			HashMap<String, Object> payload = (HashMap<String, Object>)event.geteventInfo();
			LOG.info("DeviceMgmtConsumer %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% handleEvent lightLevel : "+ payload.get("lightLevel"));
		}
		else if (event.geteventName().equals(DeviceMgmtEventConstants.SCREEN_EVENT))
		{
			HashMap<String, Object> payload = (HashMap<String, Object>)event.geteventInfo();
			LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% handleEvent screenMessage: " + payload.get("screenEvent"));
		}
		*/
		
	}

	/**
	 * should not be called - no registration for external events is necessary.
	 */
	@Override
	public void handleExternalEvent(CSSEvent event) {
		if (LOG.isDebugEnabled()) LOG.debug("NewDeviceListener has received external Event: "+event.geteventType());
	}

	/**
	 * 
	 */
	public void stop() {
		RUNNING_MODE = false;
		
		//unregister all events
		//empty filter means null
		eventManager.unSubscribeInternalEvent(this, new String[]{EventTypes.DEVICE_MANAGEMENT_EVENT}, null);
		serviceTracker.close();
	}

}

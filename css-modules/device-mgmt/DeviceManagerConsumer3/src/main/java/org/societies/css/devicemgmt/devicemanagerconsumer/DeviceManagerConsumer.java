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
				"(" + CSSEventConstants.EVENT_NAME + "=LightSensorEvent)" + 
				//"(" + CSSEventConstants.EVENT_SOURCE + "=test_event_source)" + 
				")";
		// Subscribe
		eventManager.subscribeInternalEvent(this, new String[] {"org/societies/css/device"}, eventFilter);
		LOG.info("Subscribe to internal event: org/societies/css/device -> LightSensorEvent");
	}

	@Override
	public void handleInternalEvent(InternalEvent event) {
		LOG.info("*** Internal event received *****");    
		LOG.info("** event name : "+ event.geteventName());
		LOG.info("** event source : "+ event.geteventSource());
		LOG.info("** event type : "+event.geteventType());
		if (event.geteventName().equals("LightSensorEvent")) {
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
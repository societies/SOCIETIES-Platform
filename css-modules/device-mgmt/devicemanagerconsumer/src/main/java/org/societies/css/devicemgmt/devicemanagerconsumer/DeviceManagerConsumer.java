package org.societies.css.devicemgmt.devicemanagerconsumer;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.css.devicemgmt.model.DeviceMgmtDriverServiceNames;
import org.societies.api.css.devicemgmt.model.DeviceMgmtEventConstants;
import org.societies.api.css.devicemgmt.model.DeviceTypeConstants;
import org.springframework.osgi.context.BundleContextAware;



public class DeviceManagerConsumer implements EventHandler, ServiceTrackerCustomizer, BundleContextAware{
	
	//TODO For Test
	private Double ll = new Double(0.0);
	private String screenMessage = "default message";
	
	private static Logger LOG = LoggerFactory.getLogger(DeviceManagerConsumer.class);
	
	private BundleContext bundleContext;
	
	private ServiceTracker serviceTracker;
	
	public DeviceManagerConsumer() 
	{
		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device manager consumer constructor");
	}
	
	
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		
	}
	
	public void initConsumer()
	{	
		this.serviceTracker = new ServiceTracker(bundleContext, IDevice.class.getName(), this);
		this.serviceTracker.open();
		
	}


	public void handleEvent(Event event) {
		
		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% handleEvent ");
		if (event.getProperty("event_name").equals(DeviceMgmtEventConstants.LIGHT_SENSOR_EVENT)) 
		{
			ll = (Double)event.getProperty("lightLevel");
			LOG.info("DeviceMgmtConsumer: ***********%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% handleEvent lightLevel: " + ll);
		}
		else if (event.getProperty("event_name").equals(DeviceMgmtEventConstants.SCREEN_EVENT))
		{
			screenMessage = (String)event.getProperty("screenEvent");
			LOG.info("DeviceMgmtConsumer: ***********%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% handleEvent screenMessage: " + screenMessage);
		}
	}
	
	
	@Override
	public Object addingService(ServiceReference reference) 
	{
		Object obj = bundleContext.getService(reference);

		IDevice iDevice = (IDevice)obj;

		if (iDevice != null) 
		{ 		
			if (iDevice.getDeviceType() == DeviceTypeConstants.LIGHT_SENSOR) 
			{
				if (iDevice.getDeviceLocation().equalsIgnoreCase("Room1")) 
				{
					
					IDevice ls1 = iDevice;
					IDriverService driverService = ls1.getService(DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE);
					
					IAction ia = driverService.getAction("getLightLevel");
					
					Dictionary dic = ia.invokeAction(null);
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Name: "+ ls1.getDeviceName());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Location: "+ ls1.getDeviceType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device ID: "+ ls1.getDeviceId());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Description: "+ ls1.getDeviceDescription());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device provider: "+ ls1.getDeviceProvider());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Connection Type: "+ ls1.getDeviceConnetionType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Location: "+ ls1.getDeviceLocation());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% getLightLevel action Return: "+ dic.get("outputLightLevel"));
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
				}
				else if (iDevice.getDeviceLocation().equalsIgnoreCase("Room2")) 
				{
					
					IDevice ls2 = iDevice;
					IDriverService driverService = ls2.getService(DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE);
					
					IAction ia = driverService.getAction("getLightLevel");

					Dictionary dic = ia.invokeAction(null);
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Name: "+ ls2.getDeviceName());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Location: "+ ls2.getDeviceType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device ID: "+ ls2.getDeviceId());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Description: "+ ls2.getDeviceDescription());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device provider: "+ ls2.getDeviceProvider());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Connection Type: "+ ls2.getDeviceConnetionType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Location: "+ ls2.getDeviceLocation());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% getLightLevel action Return: "+ dic.get("outputLightLevel"));
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
				}
				else if (iDevice.getDeviceLocation().equalsIgnoreCase("Room3")) 
				{
					IDevice ls3 = iDevice;
					IDriverService driverService = ls3.getService(DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE);
					
					IAction ia = driverService.getAction("getLightLevel");

					Dictionary dic = ia.invokeAction(null);
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Name: "+ ls3.getDeviceName());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Location: "+ ls3.getDeviceType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device ID: "+ ls3.getDeviceId());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Description: "+ ls3.getDeviceDescription());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device provider: "+ ls3.getDeviceProvider());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Connection Type: "+ ls3.getDeviceConnetionType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Location: "+ ls3.getDeviceLocation());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% getLightLevel action Return: "+ dic.get("outputLightLevel"));
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
				}
			}
			else if (iDevice.getDeviceType() == DeviceTypeConstants.SCREEN)
			{
				if (iDevice.getDeviceLocation().equalsIgnoreCase("Corridor1")) 
				{
					IDevice screen1 = iDevice;
					IDriverService driverService = screen1.getService(DeviceMgmtDriverServiceNames.SCREEN_DRIVER_SERVICE);
					
					IAction ia = driverService.getAction("displayMessage");

					Dictionary<String, Object> dic = new Hashtable<String, Object>();
					dic.put("message", "Display this message for me please ! ");
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Name: "+ screen1.getDeviceName());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Location: "+ screen1.getDeviceType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device ID: "+ screen1.getDeviceId());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Description: "+ screen1.getDeviceDescription());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device provider: "+ screen1.getDeviceProvider());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Connection Type: "+ screen1.getDeviceConnetionType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Location: "+ screen1.getDeviceLocation());
					ia.invokeAction(dic);
					
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
				}
				
			}
		}
		return null;
	}


	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		
	}


	@Override
	public void removedService(ServiceReference reference, Object service) {

	}
}
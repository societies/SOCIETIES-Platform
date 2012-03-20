package org.societies.css.devicemgmt.DeviceDriverSimulator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IDeviceService;
import org.societies.api.internal.css.devicemgmt.IDeviceManager;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.springframework.osgi.context.BundleContextAware;


public class SampleActivatorDriver implements BundleContextAware{
	/**Bundle Context */
	private BundleContext bc;
	
	private IDeviceManager deviceManager;
	private EventAdmin eventAdmin;
	
	/* sensors */
	private LightSensor ls;
	private LightSensor ls2;
	private LightSensor ls3;
	
	private String deviceServiceId1 = "lightSensor1";
	private String deviceServiceId2 = "lightSensor2";
	private String deviceServiceId3 = "lightSensor3";
	
	private String deviceMacAddress1 = "lightSensorService1";
	private String deviceMacAddress2 = "lightSensorService2";
	private String deviceMacAddress3 = "lightSensorService3";
	
	private DeviceCommonInfo deviceCommonInfo1;
	private DeviceCommonInfo deviceCommonInfo2;
	private DeviceCommonInfo deviceCommonInfo3;
	
	private String [] serviceIds1 = {"lightSensor1"};
	private String [] serviceIds2 = {"lightSensor2"};
	private String [] serviceIds3 = {"lightSensor3"};
	
	private Dictionary<String, String> properties;
	/** Service registration */

    
    private ServiceRegistration lsReg;
    private ServiceRegistration lsReg2;
    private ServiceRegistration lsReg3;
   
    private ServiceRegistration laReg;

    private int lightSensorCount = 1;
   

    private static Logger LOG = LoggerFactory.getLogger(SampleActivatorDriver.class);
  

    
	public void setBundleContext(BundleContext bc) {
		this.bc =  bc;
		
	}
	
	
	public EventAdmin getEventAdmin()
	{
		return this.eventAdmin;
	}
	
	public void setEventAdmin(EventAdmin eventAdmin)
	{
		
		LOG.info("DeviceDriverExample: " + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% setEventAdmin injection");
		this.eventAdmin = eventAdmin;
		
		LOG.info("DeviceDriverExample: " + "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% post setEventAdmin injection" + eventAdmin.toString());
	}
	
	
	
	public void setDeviceManager (IDeviceManager deviceManager)
	{
		this.deviceManager = deviceManager;
	}
	
	public void initSimul()
	{
		startSimul();
	}
	
	
    
	
	/*
	 * starts each actuator/sensor
	 */
	public void startSimul() 
	{
		deviceCommonInfo1 = new DeviceCommonInfo("family1", deviceMacAddress1, "Light Sensor", "LightSensor", "Light Sensor test 1", "Zigbee", "Room1", "Trialog", true);
		deviceCommonInfo2 = new DeviceCommonInfo("family1", deviceMacAddress2, "Light Sensor", "LightSensor", "Light Sensor test 1", "Zigbee", "Room1", "Trialog", true);
		deviceCommonInfo3 = new DeviceCommonInfo("family1", deviceMacAddress3, "Light Sensor", "LightSensor", "Light Sensor test 1", "Zigbee", "Room1", "Trialog", true);
		
		/* creation of sensors */
		
		deviceManager.fireNewDeviceConnected(deviceMacAddress1, deviceCommonInfo1, serviceIds1);
		deviceManager.fireNewDeviceConnected(deviceMacAddress2, deviceCommonInfo2, serviceIds2);
		deviceManager.fireNewDeviceConnected(deviceMacAddress3, deviceCommonInfo3, serviceIds3);
		
		ls = new LightSensor(this, deviceServiceId1, deviceMacAddress1, lightSensorCount);
		lightSensorCount++;
		ls2 = new LightSensor(this, deviceServiceId2, deviceMacAddress2, lightSensorCount);
		lightSensorCount++;
		ls3 = new LightSensor(this, deviceServiceId3, deviceMacAddress3, lightSensorCount);
		lightSensorCount++;

		Object lock = new Object();			

		synchronized(lock) 
		{
			properties = new Hashtable<String, String>();
			properties.put("serviceId", deviceServiceId1);
			properties.put("deviceMacAddress", deviceMacAddress1);
			lsReg = bc.registerService(IDeviceService.class.getName(), ls, properties);
			
			properties = new Hashtable<String, String>();
			properties.put("serviceId", deviceServiceId2);
			properties.put("deviceMacAddress", deviceMacAddress2);
			lsReg2 = bc.registerService(IDeviceService.class.getName(), ls2, properties);
			
			properties = new Hashtable<String, String>();
			properties.put("serviceId", deviceServiceId3);
			properties.put("deviceMacAddress", deviceMacAddress3);
			lsReg3 = bc.registerService(IDeviceService.class.getName(), ls3, properties);
		}
	}
	
	
	public void stop(BundleContext context) throws Exception {
		
		stopappli();
		
		if(lsReg != null){
			lsReg.unregister();
		}	
		if(lsReg2 != null){
			lsReg2.unregister();
		}
		if(lsReg3 != null){
			lsReg3.unregister();
		}
		
		if(laReg != null){
			laReg.unregister();
		}
	}
	
	public void stopappli(){

		/* Light sensors */
		if (ls!=null){
			deviceManager.fireDeviceDisconnected("family1", deviceMacAddress1);
			ls = null;
		}
		else {
			System.out.println("no ls");
		}
		if (ls2!=null){
			deviceManager.fireDeviceDisconnected("family1", deviceMacAddress2);
			ls2 = null;
		}
		else {
			System.out.println("no ls2");
		}
		if (ls3!=null){
			deviceManager.fireDeviceDisconnected("family1", deviceMacAddress3);
			ls3 = null;
		}
		else {
			System.out.println("no ls3");
		}
	}
}

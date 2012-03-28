package org.societies.css.devicemgmt.DeviceDriverSimulator;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IDriverService;
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
	private Screen screen;
	
	private String driverServiceId1 = "lightSensorService";
	private String driverServiceId2 = "lightSensorService";
	private String driverServiceId3 = "lightSensorService";
	
	private String screenDriverServiceId = "screenService";
	
	private String physicalDeviceId1 = "3b:6d:01";
	private String physicalDeviceId2 = "3b:6d:02";
	private String physicalDeviceId3 = "3b:6d:03";
	
	private String screenPhysicalDeviceId = "23:75:01";
	
	private DeviceCommonInfo deviceCommonInfo1;
	private DeviceCommonInfo deviceCommonInfo2;
	private DeviceCommonInfo deviceCommonInfo3;
	
	private DeviceCommonInfo screenCommonInfo;
	
	private String [] serviceIds1 = {driverServiceId1};
	private String [] serviceIds2 = {driverServiceId2};
	private String [] serviceIds3 = {driverServiceId3};
	
	private String [] screenServiceIds = {screenDriverServiceId};
	
	private Dictionary<String, String> properties;
	/** Service registration */

    
    private ServiceRegistration lsReg;
    private ServiceRegistration lsReg2;
    private ServiceRegistration lsReg3;
    
    private ServiceRegistration screenReg;


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
		deviceCommonInfo1 = new DeviceCommonInfo("family1", "Light Sensor", "LightSensor", "Light Sensor 1 test", "Zigbee", "Room1", "Trialog", null, true);
		deviceCommonInfo2 = new DeviceCommonInfo("family1", "Light Sensor", "LightSensor", "Light Sensor 2 test", "Zigbee", "Room2", "Trialog", null, true);
		deviceCommonInfo3 = new DeviceCommonInfo("family1", "Light Sensor", "LightSensor", "Light Sensor 3 test", "Zigbee", "Room3", "Trialog", null, true);

		screenCommonInfo = new DeviceCommonInfo("family2", "Sony Screen", "Screen", "Screen display test", "HDMI", "Corridor1", "Trialog", null, false);
		/* creation of sensors */
		
		ls = new LightSensor(this, driverServiceId1, physicalDeviceId1, lightSensorCount);
		lightSensorCount++;
		ls2 = new LightSensor(this, driverServiceId2, physicalDeviceId2, lightSensorCount);
		lightSensorCount++;
		ls3 = new LightSensor(this, driverServiceId3, physicalDeviceId3, lightSensorCount);
		lightSensorCount++;

		screen = new Screen(this, screenDriverServiceId, screenPhysicalDeviceId);
			
		Object lock = new Object();			

		synchronized(lock) 
		{
			LOG.info("DeviceDriverExample: " + "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii registration process");
			
			properties = new Hashtable<String, String>();
			properties.put("driverServiceId", driverServiceId1);
			properties.put("physicalDeviceId", physicalDeviceId1);
			//properties.put("deviceId", lsDeviceId1);
			lsReg = bc.registerService(IDriverService.class.getName(), ls, properties);
			
			properties = new Hashtable<String, String>();
			properties.put("driverServiceId", driverServiceId2);
			properties.put("physicalDeviceId", physicalDeviceId2);
			//properties.put("deviceId", lsDeviceId2);
			lsReg2 = bc.registerService(IDriverService.class.getName(), ls2, properties);
			
			properties = new Hashtable<String, String>();
			properties.put("driverServiceId", driverServiceId3);
			properties.put("physicalDeviceId", physicalDeviceId3);
			//properties.put("deviceId", lsDeviceId3);
			lsReg3 = bc.registerService(IDriverService.class.getName(), ls3, properties);
			
			properties = new Hashtable<String, String>();
			properties.put("driverServiceId", screenDriverServiceId);
			properties.put("physicalDeviceId", screenPhysicalDeviceId);
			//properties.put("deviceId", screenDeviceId);
			screenReg = bc.registerService(IDriverService.class.getName(), screen, properties);
		}
		
		
		String lsDeviceId1 = deviceManager.fireNewDeviceConnected(physicalDeviceId1, deviceCommonInfo1, serviceIds1);
		String lsDeviceId2 = deviceManager.fireNewDeviceConnected(physicalDeviceId2, deviceCommonInfo2, serviceIds2);
		String lsDeviceId3 = deviceManager.fireNewDeviceConnected(physicalDeviceId3, deviceCommonInfo3, serviceIds3);	
		String screenDeviceId = deviceManager.fireNewDeviceConnected(screenPhysicalDeviceId, screenCommonInfo, screenServiceIds);
		
		
		LOG.info("DeviceDriverExample: " + "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii " + lsDeviceId1);
		
		ls.setDeviceId(lsDeviceId1);
		ls2.setDeviceId(lsDeviceId2);
		ls3.setDeviceId(lsDeviceId3);
		screen.setDeviceId(screenDeviceId);
		
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
		if(screenReg != null){
			screenReg.unregister();
		}
		
	}
	
	public void stopappli(){

		/* Light sensors */
		if (ls!=null){
			deviceManager.fireDeviceDisconnected("family1", physicalDeviceId1);
			ls = null;
		}
		else {
			System.out.println("no ls");
		}
		if (ls2!=null){
			deviceManager.fireDeviceDisconnected("family1", physicalDeviceId2);
			ls2 = null;
		}
		else {
			System.out.println("no ls2");
		}
		if (ls3!=null){
			deviceManager.fireDeviceDisconnected("family1", physicalDeviceId3);
			ls3 = null;
		}
		else {
			System.out.println("no ls3");
		}
		
		/* Screen */
		if (screen!=null){
			deviceManager.fireDeviceDisconnected("family2", screenPhysicalDeviceId);
			screen = null;
		}
		else {
			System.out.println("no screen");
		}
	}
}

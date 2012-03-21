package org.societies.css.devicemgmt.devicemanagerconsumer;

import java.util.Dictionary;
import java.util.HashMap;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.IDeviceService;
//import org.societies.comm.xmpp.event.EventFactory;
//import org.societies.comm.xmpp.event.EventStream;
//import org.societies.comm.xmpp.event.InternalEvent;
import org.springframework.context.ApplicationListener;





public class DeviceManagerConsumer implements EventHandler{

	private IDevice deviceService;
	
	//private EventStream myStream;
	
	private Double lightLevel = new Double(0.0); 
	
	//TODO For Test
	private Double ll = new Double(0.0);
	
	private HashMap<String, Long> eventResult;
	
	private static Logger LOG = LoggerFactory.getLogger(DeviceManagerConsumer.class);
	
	public DeviceManagerConsumer() {
		
		eventResult = new HashMap<String, Long>();

		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device manager consumer constructor");
		
		//myStream = EventFactory.getStream("lightLevel");
		
		//myStream.addApplicationListener(this);
		
		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% post addApplicationListener ");
	}
	
	public IDevice getDeviceService()
	{
		return deviceService;
	}
	
	public void setDeviceService(IDevice deviceService)
	{
		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% deviceService pre injection ");
		
		this.deviceService=deviceService;
		
		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% deviceService post injection "+ deviceService.getDeviceId());
	}
	
	public void initConsumer()
	{
		IDeviceService ds = getDeviceService().getService("lightSensor1");
				
		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% pre ds.getAction ");
				
		IAction ia = ds.getAction("getLightLevel");
		
		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% post ds.getAction ");
				
		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% post ia.invokeAction ");
		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Action Name is: "+ ia.getName());
				
		int a = 1, b=2;
		while (a<2) 
		{
			try 
			{
				Dictionary dic = ia.invokeAction(null);
				LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Action Return is: "+ dic.get("outputLightLevel")); 
				Thread.sleep(4000);
			} 
			catch (InterruptedException e) 
			{
						
				e.printStackTrace();
			}
			a = 3;	
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
//	public void onApplicationEvent(InternalEvent event) {
//		
//		 LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% onApplicationEvent ");
//		
//		if (event.getEventNode().equals("lightLevel"))
//		{
//				eventResult = (HashMap<String, Long>)event.getEventInfo();
//			     
//				lightLevel = eventResult.get("lightLevel");
//			     LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% onApplicationEvent: "+ lightLevel);
//		}	
//	}

	/* (non-Javadoc)
	 * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
	 */
	public void handleEvent(Event event) {
		
		LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% handleEvent ");
		
		if (event.getTopic().equals("LightSensorEvent"))
		{
			ll = (Double)event.getProperty("lightLevel");
		}
		LOG.info("DeviceMgmtConsumer: ***********%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% handleEvent " + ll);
	}

}
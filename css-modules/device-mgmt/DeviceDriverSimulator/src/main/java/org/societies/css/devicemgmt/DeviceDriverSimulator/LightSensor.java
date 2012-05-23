package org.societies.css.devicemgmt.DeviceDriverSimulator;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.css.devicemgmt.IDeviceStateVariable;
import org.societies.api.css.devicemgmt.model.DeviceMgmtEventConstants;
import org.societies.api.osgi.event.EventTypes;
import org.societies.css.devicemgmt.DeviceDriverSimulator.actions.GetLightLevelAction;
import org.societies.css.devicemgmt.DeviceDriverSimulator.statevariables.LightLevelStateVariable;

public class LightSensor implements IDriverService{

	private Map<String, IDeviceStateVariable> stateVariables = new HashMap<String, IDeviceStateVariable>();
	private Map<String, IAction> actions = new HashMap<String, IAction>(); 
	
	private Double maxValue;
	private Double minValue;
	private Double lightValue;
	private boolean dec ;
	private LightSimulator lightSimulator;
	private String deviceMacAddress;
	
	private static Logger LOG = LoggerFactory.getLogger(LightSensor.class);
	
	private String serviceId;
	
	private SampleActivatorDriver activatorDriver;
	
	private String deviceId;
	
	private int deviceCount;
	
	private Double lightLevel = new Double (0.0);
	
	private boolean state = false;
	//private EventStream myStream;
	
	private EventAdmin eventAdmin;
	
	
	public LightSensor(SampleActivatorDriver activatorDriver, String serviceId, String deviceMacAddress, int deviceCount) {
		
		this.activatorDriver = activatorDriver;
		this.deviceMacAddress = deviceMacAddress;
		this.serviceId = serviceId;
		this.deviceCount = deviceCount;
		
		this.state = false;
		
		eventAdmin = activatorDriver.getEventAdmin();
		
		IDeviceStateVariable stateVariable;
		IAction action;
		
		stateVariable = new LightLevelStateVariable();
		stateVariables.put(stateVariable.getName(), stateVariable);
		
		
		lightValue= 100.0;
		maxValue= 150.0 ;
		minValue= 50.0;
		dec=true;
		
		action = new GetLightLevelAction(this, (LightLevelStateVariable) stateVariable);
		actions.put(action.getName(), action);
	}
	
	public LightSensor(SampleActivatorDriver activatorDriver, String serviceId, String deviceMacAddress, int deviceCount, String deviceId) {
		
		this.activatorDriver = activatorDriver;
		this.deviceMacAddress = deviceMacAddress;
		this.serviceId = serviceId;
		this.deviceCount = deviceCount;
		this.deviceId = deviceId;
		
		this.state = true;
		
		eventAdmin = activatorDriver.getEventAdmin();
		
		IDeviceStateVariable stateVariable;
		IAction action;
		
		stateVariable = new LightLevelStateVariable();
		stateVariables.put(stateVariable.getName(), stateVariable);
		
		
		lightValue= 100.0;
		maxValue= 150.0 ;
		minValue= 50.0;
		dec=true;
		
		action = new GetLightLevelAction(this, (LightLevelStateVariable) stateVariable);
		actions.put(action.getName(), action);
		
		lightSimulator = new LightSimulator(this, deviceCount);
	}
	
	public void setDeviceId (String deviceId)
	{
		this.deviceId = deviceId;
		this.state = true;
		
		if (null == lightSimulator) 
		{
			lightSimulator = new LightSimulator(this, deviceCount);
		}		
	}


	public IAction getAction(String actionName) {
		
		return (IAction)actions.get(actionName);
	}

	
	public IAction[] getActions() {
		
		return (IAction[])(actions.values()).toArray(new IAction[]{});
	}

	
	public IDeviceStateVariable getStateVariable(String stateVariableName) {
		
		return (IDeviceStateVariable)stateVariables.get(stateVariableName);
	}

	
	public IDeviceStateVariable[] getStateVariables() {
		
		return (IDeviceStateVariable[])(stateVariables.values()).toArray(new IDeviceStateVariable[]{}); 
	}

	
	public String getId() {
		
		return serviceId;
	}
	
	
	
	public double getLightValue() {
		 
		 return lightValue;
	 }
	
	
	public void setLightLevel(){
			
		if (lightValue > maxValue ){
			dec =true;
		}
		else if (lightValue < minValue ) {
			dec = false;
		}
		if (dec) {
			lightValue--;
		}
		else {
			lightValue++;
		}
		
		
		LOG.info("DeviceDriverExample info: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% sending event by eventAdmin");
		Dictionary<String, Object> eventAdminDic = new Hashtable<String, Object>();
		
		eventAdminDic.put("lightLevel", getLightValue());
		eventAdminDic.put("event_name", DeviceMgmtEventConstants.LIGHT_SENSOR_EVENT);
		
		eventAdmin.sendEvent(new Event(EventTypes.DEVICE_MANAGEMENT_EVENT, eventAdminDic));
		LOG.info("DeviceDriverExample info: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% event sent by eventAdmin");
	}
}

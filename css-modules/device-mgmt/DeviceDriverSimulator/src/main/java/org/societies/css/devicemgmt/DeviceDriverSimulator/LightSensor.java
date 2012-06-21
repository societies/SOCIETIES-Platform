package org.societies.css.devicemgmt.DeviceDriverSimulator;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.css.devicemgmt.IDeviceStateVariable;
import org.societies.api.css.devicemgmt.model.DeviceMgmtEventConstants;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
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
	
	private IEventMgr eventManager;
	
	
	public LightSensor(SampleActivatorDriver activatorDriver, String serviceId, String deviceMacAddress, int deviceCount) {
		
		this.activatorDriver = activatorDriver;
		this.deviceMacAddress = deviceMacAddress;
		this.serviceId = serviceId;
		this.deviceCount = deviceCount;
		
		this.state = false;
		
		eventManager = activatorDriver.getEventManager();
		
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
		
		eventManager = activatorDriver.getEventManager();
		
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

	@Override
	public IAction getAction(String actionName) {
		
		return (IAction)actions.get(actionName);
	}

	@Override
	public IAction[] getActions() {
		
		return (IAction[])(actions.values()).toArray(new IAction[]{});
	}

	@Override
	public IDeviceStateVariable getStateVariable(String stateVariableName) {
		
		return (IDeviceStateVariable)stateVariables.get(stateVariableName);
	}

	@Override
	public IDeviceStateVariable[] getStateVariables() {
		
		return (IDeviceStateVariable[])(stateVariables.values()).toArray(new IDeviceStateVariable[]{}); 
	}
	
	
	@Override
	public String getDriverServiceName() {
		
		return serviceId;
	}

	
	@Override
	public String getServiceDescription() {
		
		return "This service is used to make some control on the Light Sensor, for instance getting the last value of the light level";
	}

	
	@Override
	public String getName() {
		
		return "Light Sensor Control Service";
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
		
		
		LOG.info("Publish internal event: org/societies/css/device -> Sensor/LightSensorEvent");
		HashMap<String, Object> payload = new HashMap<String, Object>();
		payload.put("lightLevel", getLightValue());
		InternalEvent event = new InternalEvent(EventTypes.DEVICE_MANAGEMENT_EVENT, DeviceMgmtEventConstants.LIGHT_SENSOR_EVENT, deviceId, payload);
		try {
			eventManager.publishInternalEvent(event);
		} catch (EMSException e) {
			LOG.error("Error when publishing new light level", e);
		}
	}
}

package org.societies.css.devicemgmt.DeviceDriverExample.impl;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDeviceService;
import org.societies.api.css.devicemgmt.IDeviceStateVariable;
import org.societies.comm.xmpp.event.EventStream;
import org.societies.css.devicemgmt.DeviceDriverExample.actions.GetLightLevelAction;
import org.societies.css.devicemgmt.DeviceDriverExample.statevariables.LightLevelStateVariable;

public class LightSensor implements IDeviceService{

	private Map<String, IDeviceStateVariable> stateVariables = new HashMap<String, IDeviceStateVariable>();
	private Map<String, IAction> actions = new HashMap<String, IAction>(); 
	
	private BundleContext bundleContext;
	private ServiceRegistration registration;
	
	private Dictionary<String, String> properties;
	
	private DeviceDriverExample deviceDriverExample;
	
	private String deviceMacAddress;
	
	private static Logger LOG = LoggerFactory.getLogger(LightSensor.class);
	
	private String serviceId;
	
	
	
	public LightSensor(BundleContext bc, DeviceDriverExample deviceDriverExample, String serviceId, String deviceMacAddress) {
		
		this.bundleContext = bc;
		this.deviceDriverExample = deviceDriverExample;
		this.deviceMacAddress = deviceMacAddress;
		this.serviceId = serviceId;
		
		IDeviceStateVariable stateVariable;
		IAction action;
		
		stateVariable = new LightLevelStateVariable();
		stateVariables.put(stateVariable.getName(), stateVariable);
		
		action = new GetLightLevelAction(deviceDriverExample, (LightLevelStateVariable) stateVariable, deviceMacAddress);
		actions.put(action.getName(), action);
		
		
		properties = new Hashtable<String, String>();
		properties.put("serviceId", serviceId);
		properties.put("deviceMacAddress", deviceMacAddress);
		
		Object lock = new Object();

		synchronized(lock)
		{
			registration = bundleContext.registerService(IDeviceService.class.getName(), this, properties);
			
			LOG.info(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% LightSensor info: lightSensor service registred ");
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
	public String getId() {
		
		return serviceId;
	}

}

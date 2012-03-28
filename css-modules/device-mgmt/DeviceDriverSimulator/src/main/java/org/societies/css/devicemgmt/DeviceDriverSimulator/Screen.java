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
import org.societies.css.devicemgmt.DeviceDriverSimulator.actions.DisplayMessageAction;
import org.societies.css.devicemgmt.DeviceDriverSimulator.statevariables.MessageStateVariable;

/**
 * Describe your class here...
 *
 * @author rafik
 *
 */
public class Screen implements IDriverService{


	private Map<String, IDeviceStateVariable> stateVariables = new HashMap<String, IDeviceStateVariable>();
	private Map<String, IAction> actions = new HashMap<String, IAction>(); 
	
	private static Logger LOG = LoggerFactory.getLogger(Screen.class);
	
	private SampleActivatorDriver sampleActivatorDriver;
	private String serviceId;
	private String  physicalDeviceId;
	private String deviceId;
	
	private EventAdmin eventAdmin;
	
	public Screen(SampleActivatorDriver sampleActivatorDriver, String serviceId, String physicalDeviceId, String deviceId) {
		
		this.sampleActivatorDriver = sampleActivatorDriver;
		this.serviceId = serviceId;
		this.physicalDeviceId = physicalDeviceId;
		this.deviceId = deviceId;
		
		eventAdmin = sampleActivatorDriver.getEventAdmin();
		
		IDeviceStateVariable stateVariable;
		IAction action;
		
		stateVariable = new MessageStateVariable();
		stateVariables.put(stateVariable.getName(), stateVariable);
		
		action = new DisplayMessageAction(this, (MessageStateVariable) stateVariable);
		actions.put(action.getName(), action);
	}
	
	public Screen(SampleActivatorDriver sampleActivatorDriver, String serviceId, String physicalDeviceId) {
		
		this.sampleActivatorDriver = sampleActivatorDriver;
		this.serviceId = serviceId;
		this.physicalDeviceId = physicalDeviceId;
		
		
		eventAdmin = sampleActivatorDriver.getEventAdmin();
		
		IDeviceStateVariable stateVariable;
		IAction action;
		
		stateVariable = new MessageStateVariable();
		stateVariables.put(stateVariable.getName(), stateVariable);
		
		action = new DisplayMessageAction(this, (MessageStateVariable) stateVariable);
		actions.put(action.getName(), action);
	}
	
	public void setDeviceId (String deviceId)
	{
		this.deviceId = deviceId;
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
	
	
	public  void sendMessageToScreen (String message)
	{
		LOG.info("DeviceDriverSimulator Screen Displays: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% " + message);
		
		LOG.info("DeviceDriverExample info: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% sending event by eventAdmin");
		Dictionary<String, Object> eventAdminDic = new Hashtable<String, Object>();
		
		eventAdminDic.put("screenEvent", message);
		eventAdminDic.put("event_name", "Actuator/ScreenActuatorEvent");
		
		eventAdmin.sendEvent(new Event("org/societies/css/device", eventAdminDic));
		LOG.info("DeviceDriverExample info: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% event sent by eventAdmin");
	}

}

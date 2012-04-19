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
package org.societies.api.css.devicemgmt;

import java.util.HashMap;
import java.util.Map;

import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.css.devicemgmt.IDeviceStateVariable;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

/**
 * Abstract class to create more easily driver services
 * @author Olivier Maridat (Trialog)
 */
public abstract class DriverService implements IDriverService {
	/**
	 * ID of the service
	 */
	protected String serviceId;
	
	/**
	 * ID of the device
	 * Mandatory to be in ready state
	 */
	protected String deviceId;
	
	/**
	 * Physical device ID
	 * e.g. the MAC adress
	 */
	protected String physicalDeviceId;
	
	/**
	 * List of actions available in this service
	 */
	protected Map<String, IAction> actions;
	
	/**
	 * List of state variables provided in this service
	 */
	protected Map<String, IDeviceStateVariable> stateVariables;
	
	/**
	 * State of the DriverService
	 * A driver service is ready when its device ID is filled
	 */
	protected boolean ready;
	
	/**
	 * Event Manager to publish/subscribe/be notified of events
	 */
	protected IEventMgr eventManager;

	
	/* --- Constructors --- */

	public DriverService(String serviceId, String physicalDeviceId) {
		this.physicalDeviceId = physicalDeviceId;
		this.serviceId = serviceId;
		// Init variables
		actions = new HashMap<String, IAction>();
		stateVariables = new HashMap<String, IDeviceStateVariable>();
		ready = false;
	}
	public DriverService(String serviceId, String physicalDeviceId, IEventMgr eventManager) {
		this(serviceId, physicalDeviceId);
		this.eventManager = eventManager;
	}
	public DriverService(String serviceId, String physicalDeviceId, String deviceId) {
		this(serviceId, physicalDeviceId);
		setDeviceId(deviceId);
	}
	public DriverService(String serviceId, String physicalDeviceId, String deviceId, IEventMgr eventManager) {
		this(serviceId, physicalDeviceId, eventManager);
		setDeviceId(deviceId);
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

	
	/* --- Getters / Setters --- */
	
	/**
	 * Getter Service ID
	 * @return The service ID
	 */
	public String getId() {
		return serviceId;
	}
	/**
	 * Setter Service ID
	 * @param serviceID The service ID
	 */
	public void setId(String serviceId) {
		this.serviceId =  serviceId;
	}

	public String getPhysicalDeviceId() {
		return physicalDeviceId;
	}
	public void setPhysicalDeviceId(String physicalDeviceId) {
		this.physicalDeviceId =  physicalDeviceId;
	}

	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId =  deviceId;
		ready = (null != deviceId && !deviceId.equals(""));
	}
}

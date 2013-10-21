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
package org.societies.css.devicemgmt.rfiddriver.readers;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDeviceStateVariable;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.css.devicemgmt.model.DeviceMgmtDriverServiceNames;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.css.devicemgmt.rfiddriver.actions.ConnectAction;
import org.societies.css.devicemgmt.rfiddriver.impl.SocketClient;
import org.societies.css.devicemgmt.rfiddriver.statesvariables.IpAddressStatesVariable;

/**
 *
 * @author Rafik
 *
 */
public class RfidSystem implements IDriverService{
	
	private IEventMgr eventMgr;
	
	Hashtable<String, SocketClient> sockets;
	
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	
	private Map<String, IDeviceStateVariable> stateVariables = new HashMap<String, IDeviceStateVariable>();
	private Map<String, IAction> actions = new HashMap<String, IAction>();
	
	private String deviceId;


	public RfidSystem (IEventMgr eventMgr) {
		
		this.eventMgr = eventMgr;
		
		sockets = new Hashtable<String, SocketClient>();
		
		IDeviceStateVariable stateVariable;
		IAction action;
		
		stateVariable = new IpAddressStatesVariable();
		stateVariables.put(stateVariable.getName(), stateVariable);
		
		action = new ConnectAction(this, (IpAddressStatesVariable) stateVariable);
		actions.put(action.getName(), action);
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

		return DeviceMgmtDriverServiceNames.RFID_READER_DRIVER_SERVICE;
	}


	@Override
	public String getServiceDescription() {

		return "This service is used to control the RFiD System";
	}


	@Override
	public String getName() {

		return "RFID System Control";
	}

	/**
	 * @param ipAddress
	 */
	public void connect(String ipAddress) {
		
		if (sockets.containsKey(ipAddress)){
			if(logging.isDebugEnabled()) logging.debug("Already connected to: "+ipAddress);
			return;
		}

		SocketClient socketClient = new SocketClient(ipAddress);
		if (socketClient.checkIp(ipAddress)){
			socketClient.setEventMgr(eventMgr);
			socketClient.setDeviceId(deviceId);
			socketClient.start();
			this.sockets.put(ipAddress, socketClient);
		}else{
			if(logging.isDebugEnabled()) logging.error(ipAddress+" not valid. ignoring request");
		}	
	}
	
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
}

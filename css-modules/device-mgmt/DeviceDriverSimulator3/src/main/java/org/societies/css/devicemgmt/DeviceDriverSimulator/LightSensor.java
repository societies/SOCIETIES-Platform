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

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.DriverService;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDeviceStateVariable;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.css.devicemgmt.DeviceDriverSimulator.actions.GetLightLevelAction;
import org.societies.css.devicemgmt.DeviceDriverSimulator.statevariables.LightLevelStateVariable;

/**
 * LightSensor that publish Sensor/LightSensorEvent events
 * @author Olivier Maridat (Trialog)
 *
 */
public class LightSensor extends DriverService {
	protected static Logger LOG = LoggerFactory.getLogger(LightSensor.class.getSimpleName());

	private LightSimulator lightSimulator;
	private Double lightValue;
	private Double maxValue;
	private Double minValue;
	private boolean dec ;


	/* --- Constructors --- */
	
	public LightSensor(String serviceId, String physicalDeviceId, IEventMgr eventManager) {
		super(serviceId, physicalDeviceId, eventManager);

		IDeviceStateVariable stateVariable = new LightLevelStateVariable();
		stateVariables.put(stateVariable.getName(), stateVariable);

		IAction action = new GetLightLevelAction(this, (LightLevelStateVariable) stateVariable);
		actions.put(action.getName(), action);

		lightValue= 100.0;
		maxValue= 150.0 ;
		minValue= 50.0;
		dec=true;
	}
	public LightSensor(String serviceId, String physicalDeviceId, String deviceId, IEventMgr eventManager) {
		this(serviceId, physicalDeviceId, eventManager);
		setDeviceId(deviceId);
	}

	
	/* --- Getters / Setters --- */
	
	@Override
	public void setDeviceId(String deviceId) {
		super.setDeviceId(deviceId);
		if (ready) {
			lightSimulator = new LightSimulator(this, deviceId);
		}
	}
	
	public double getLightValue() {
		return lightValue;
	}
	public void setLightLevel() {
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
		InternalEvent event = new InternalEvent("org/societies/css/device", "Sensor/LightSensorEvent", deviceId, payload);
		try {
			eventManager.publishInternalEvent(event);
		} catch (EMSException e) {
			LOG.error("Error when publishing new light level", e);
		}
	}
}

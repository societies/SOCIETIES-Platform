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

package org.societies.css.devicemgmt.DeviceDriverSimulator.actions;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDeviceStateVariable;
import org.societies.api.css.devicemgmt.model.DeviceActionsConstants;
import org.societies.css.devicemgmt.DeviceDriverSimulator.LightSensor;
import org.societies.css.devicemgmt.DeviceDriverSimulator.statevariables.LightLevelStateVariable;

public class GetLightLevelAction implements IAction {

	final private String NAME = DeviceActionsConstants.LIGHT_SENSOR_GET_LIGHT_LEVEL_ACTION;
	final private String OUTPUT_LIGHT_LEVEL = "outputLightLevel";
	final private String INPUT_LIGHT_LEVEL = null;

	private static Logger LOG = LoggerFactory.getLogger(GetLightLevelAction.class);
	
	private List<String> outputArguments;
	
	private LightLevelStateVariable lightLevelStateVariable;
	
	
	private LightSensor lightSensor;
	
	public GetLightLevelAction(LightSensor lightSensor, LightLevelStateVariable lightLevelStateVariable) {

		this.lightSensor = lightSensor;
		this.lightLevelStateVariable =lightLevelStateVariable;
		
		outputArguments = new ArrayList<String>();
		this.outputArguments.add(OUTPUT_LIGHT_LEVEL);
	}
	
	

	public List<String> getInputArgumentNames() {
		
		return null;
	}


	public String getName() {
		return NAME;
	}


	public List<String> getOutputArgumentNames() {
		return outputArguments;
	}


	public IDeviceStateVariable getStateVariable(String argumentName) {
		return lightLevelStateVariable;
	}


	public Dictionary<String, Object> invokeAction(Dictionary<String, Object> arguments) {
	
		Double lightLevel = lightSensor.getLightValue();
		
		Hashtable result = new Hashtable();
		result.put(OUTPUT_LIGHT_LEVEL, lightLevel);
		return result;
	}

	@Override
	public String getActionDescription() {
		
		return "Used to get the last value of light level";
	}

	@Override
	public String getActionName() {
		
		return "Light Level Value";
	}

}

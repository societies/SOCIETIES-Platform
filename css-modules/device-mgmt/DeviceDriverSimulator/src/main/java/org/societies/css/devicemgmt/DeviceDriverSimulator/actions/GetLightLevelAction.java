package org.societies.css.devicemgmt.DeviceDriverSimulator.actions;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDeviceStateVariable;
import org.societies.css.devicemgmt.DeviceDriverSimulator.LightSensor;
import org.societies.css.devicemgmt.DeviceDriverSimulator.statevariables.LightLevelStateVariable;

public class GetLightLevelAction implements IAction {

	final private String NAME = "getLightLevel";
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

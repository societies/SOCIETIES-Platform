package org.societies.css.devicemgmt.DeviceDriverExample.actions;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDeviceStateVariable;
import org.societies.css.devicemgmt.DeviceDriverExample.impl.DeviceDriverExample;
import org.societies.css.devicemgmt.DeviceDriverExample.statevariables.LightLevelStateVariable;

public class GetLightLevelAction implements IAction {

	final private String NAME = "getLightLevel";
	final private String OUTPUT_LIGHT_LEVEL = "outputLightLevel";
	final private String INPUT_LIGHT_LEVEL = null;

	private static Logger LOG = LoggerFactory.getLogger(GetLightLevelAction.class);
	
	private List<String> outputArguments;
	
	private LightLevelStateVariable lightLevelStateVariable;
	
	private String macAddress;
	
	private DeviceDriverExample deviceDriverExample;
	
	public GetLightLevelAction(DeviceDriverExample deviceDriverExample, LightLevelStateVariable lightLevelStateVariable, String macAddress) {

		
		this.deviceDriverExample = deviceDriverExample;
		this.lightLevelStateVariable =lightLevelStateVariable;
		
		outputArguments = new ArrayList<String>();
		this.outputArguments.add(OUTPUT_LIGHT_LEVEL);
		
		this.macAddress = macAddress;

	}
	
	
	@Override
	public List<String> getInputArgumentNames() {
		
		return null;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public List<String> getOutputArgumentNames() {
		return outputArguments;
	}

	@Override
	public IDeviceStateVariable getStateVariable(String argumentName) {
		return lightLevelStateVariable;
	}

	@Override
	public Dictionary<String, String> invokeAction(Dictionary<String, String> arguments) {
	
		Long lightLevel = deviceDriverExample.getLightLevel(macAddress);
		
		Hashtable result = new Hashtable();
		result.put(OUTPUT_LIGHT_LEVEL, lightLevel);
		return result;
	}

}

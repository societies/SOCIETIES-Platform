package org.societies.css.devicemgmt.DeviceDriverSimulator.statevariables;

import org.societies.api.css.devicemgmt.IDeviceStateVariable;

public class LightLevelStateVariable implements IDeviceStateVariable{

	final private String NAME = "lightLevel";
	
	@Override
	public String[] getAllowedValues() {

		return null;
	}

	@Override
	public Object getDefaultValue() {
		
		return new Long (100);
	}

	@Override
	public Class<?> getDataJavaType() {
		
		return Long.class;
	}

	@Override
	public Number getMaximumValue() {

		return 3000.0;
	}

	@Override
	public Number getMinimumValue() {
		
		return 10.0;
	}

	@Override
	public String getName() {
		
		return NAME;
	}

	@Override
	public Number getStep() {
		
		return null;
	}

	@Override
	public String getDeviceMgmtDataType() {
		
		return null;
	}

	@Override
	public boolean isEnventable() {

		return true;
	}


	@Override
	public String getDescription() {
		return "Represent a light level variable";
	}

}

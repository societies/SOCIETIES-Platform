package org.societies.css.devicemgmt.DeviceDriverSimulator.statevariables;

import org.societies.api.css.devicemgmt.IDeviceStateVariable;

public class LightLevelStateVariable implements IDeviceStateVariable{

	final private String NAME = "lightLevel";
	

	public String[] getAllowedValues() {

		return null;
	}


	public Object getDefaultValue() {
		
		return new Long (100);
	}


	public Class<?> getDataJavaType() {
		
		return Long.class;
	}


	public Number getMaximumValue() {

		return 3000.0;
	}


	public Number getMinimumValue() {
		
		return 10.0;
	}


	public String getName() {
		
		return NAME;
	}


	public Number getStep() {
		
		return null;
	}


	public String getDeviceMgmtDataType() {
		
		return null;
	}


	public boolean isEnventable() {

		return true;
	}

}

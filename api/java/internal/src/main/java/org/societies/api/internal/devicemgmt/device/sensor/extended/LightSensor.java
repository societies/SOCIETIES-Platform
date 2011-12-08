package org.societies.api.internal.devicemgmt.device.sensor.extended;

import org.societies.api.internal.devicemgmt.device.sensor.Sensor;


/**
 * <font color="#3f5fbf">Interface <b>proposal</b> for a ligth sensor.</font>
 * @author rafik
 * @version 1.0
 * @created 06-déc.-2011 15:18:17
 */
public interface LightSensor extends Sensor {

	/**
	 * <font color="#3f5fbf">This method redefines the getValue method in Sensor,
	 * providing a typed data representation for light level</font>
	 * <font color="#7f9fbf"><b>@return</b></font><font color="#3f5fbf"> light level
	 * measured in percentage (double)</font>
	 */
	public double getLightLevel();

}
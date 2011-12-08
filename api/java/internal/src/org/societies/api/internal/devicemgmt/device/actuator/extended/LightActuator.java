package org.societies.api.internal.devicemgmt.device.actuator.extended;

import org.societies.api.internal.devicemgmt.device.actuator.Actuator;


/**
 * <font color="#3f5fbf">Interface <b>proposal</b> for a ligth actuator.</font>
 * @author rafik
 * @version 1.0
 * @created 06-déc.-2011 15:18:15
 */
public interface LightActuator extends Actuator {

	/**
	 * <font color="#3f5fbf">Request actuator to toggle its state.</font>
	 * <font color="#7f9fbf"><b>@return</b></font><font color="#3f5fbf"> Returns 0 if
	 * sucess, other value if error.</font>
	 */
	public int toggle();

	/**
	 * <font color="#3f5fbf">Request actuator to switch into OFF state.</font>
	 * <font color="#7f9fbf"><b>@return</b></font><font color="#3f5fbf"> Returns 0 if
	 * sucess, other value if error.</font>
	 */
	public int turnOFF();

	/**
	 * <font color="#3f5fbf">Request actuator to switch into ON state.</font>
	 * <font color="#7f9fbf"><b>@return</b></font><font color="#3f5fbf"> Returns 0 if
	 * sucess, other value if error.</font>
	 */
	public int turnON();

}
package org.societies.api.internal.devicemgmt.device.actuator;

import java.util.Map;

import org.societies.api.internal.devicemgmt.device.Device;


/**
 * <font color="#3f5fbf">Defines base actuator from which all actuators derive
 * from.</font>
 * @author rafik
 * @version 1.0
 * @created 06-déc.-2011 15:18:11
 */
public interface Actuator extends Device {

	public static final int EVENT_STATUS_CHANGED = 1;
	public static final int EVENT_STATUS_UPDATED = 0;

	/**
	 * 
	 * @param listener
	 */
	public int registerActuatorListener(ActuatorListener  listener);

	/**
	 * 
	 * @param status
	 */
	public int setActuatorStatus(Map status);

	/**
	 * 
	 * @param listener
	 */
	public int unregisterActuatorListener(ActuatorListener listener);

}
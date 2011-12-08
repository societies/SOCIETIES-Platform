package org.societies.api.internal.devicemgmt.device.sensor.extended;

import org.societies.api.internal.devicemgmt.device.sensor.Sensor;


/**
 * <font color="#3f5fbf">Interface <b>proposal</b> for a RFID sensor.</font>
 * @author rafik
 * @version 1.0
 * @created 06-déc.-2011 15:18:21
 */
public interface RFiDSensor extends Sensor {

	public String getTagId();

}
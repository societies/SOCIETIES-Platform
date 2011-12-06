package org.societies.api.internal.devicemgmt.device.sensor.extended;

import org.societies.api.internal.devicemgmt.device.sensor.Sensor;


/**
 * <font color="#3f5fbf">Interface <b>proposal</b> for a GPS sensor.</font>
 * @author rafik
 * @version 1.0
 * @created 06-déc.-2011 15:18:19
 */
public interface GpsSensor extends Sensor {

	public float getAccuracy();

	public double getAltitude();

	public float getBearing();

	public double getLatitude();

	public double getLongitude();

	public String getProvider();

	public float getSpeed();

}
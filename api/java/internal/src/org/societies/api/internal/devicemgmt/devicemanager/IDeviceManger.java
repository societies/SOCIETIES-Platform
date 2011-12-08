package org.societies.api.internal.devicemgmt.devicemanager;

import org.societies.api.internal.devicemgmt.device.Device;

public interface IDeviceManger {
	
	/**
	 * 
	 * @param device
	 */
	public void addDevice(Device device);
	
	
	/**
	 * 
	 * @param deviceId
	 */
	public void deleteDevice(int deviceId);
	
	/**
	 * 
	 * @param deviceId
	 * @param status
	 */
	public void deviceStatusChanged(int deviceId, int status);
	
	/**
	 * 
	 * @param deviceId
	 * @param parameter
	 */
	public void configureDevice(int deviceId, Object parameter);
	
	/**
	 * 
	 * @param deviceId
	 * @param data
	 * 
	 * 
	 */
	public void newDataReceived(int deviceId, Object data);
	
}

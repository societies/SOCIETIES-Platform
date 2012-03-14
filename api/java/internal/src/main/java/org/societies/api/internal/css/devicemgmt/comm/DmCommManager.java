package org.societies.api.internal.css.devicemgmt.comm;

import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;

public interface DmCommManager {
	public void fireNewDeviceConnected(String deviceID, DeviceCommonInfo deviceCommonInfo);
	public void fireDeviceDisconnected(String deviceID, DeviceCommonInfo deviceCommonInfo);
	public void fireDeviceDataChanged(String deviceId,DeviceCommonInfo deviceCommonInfo, String key,String value);
}

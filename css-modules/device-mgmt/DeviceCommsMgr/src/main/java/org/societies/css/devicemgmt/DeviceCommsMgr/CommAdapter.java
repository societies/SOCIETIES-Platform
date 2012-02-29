package org.societies.css.devicemgmt.DeviceCommsMgr;

import java.util.Map;

import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;

public interface CommAdapter {
	public void fireNewDeviceConnected(String deviceID, DeviceCommonInfo deviceCommonInfo);
	public void fireDeviceDisconnected(String deviceID, DeviceCommonInfo deviceCommonInfo);
	public void fireDeviceDataChanged(String deviceID,Map<String,String> values);
}

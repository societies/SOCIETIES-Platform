package org.societies.devicemgmt.deviceregistry.api;

import java.util.Collection;
import java.util.List;

import org.societies.devicemgmt.deviceregistry.impl.CSSDevice;
import org.societies.devicemgmt.deviceregistry.impl.IDeviceIdentifier;

public interface IDeviceRegistry {
	
	/*
	 * Description:		Add new device to the device registry
	 * 				
	 * @return 			IDeviceidentifier
	 */
	public IDeviceIdentifier addDevice (CSSDevice device, Object CSSID);

	/* 
	 * Description: 	Remove device from the device registry
	 * 				
	 * @return 			boolean
	 */

	public boolean deleteDevice (CSSDevice device, Object CSSID);
	
	/* 
	 * Description:		Search the device registry for a particular device given the deviceID 
	 * @return 			CSSDevice
	 */

	public CSSDevice findDevice(IDeviceIdentifier deviceID);
	
	/* 
	 * Description: 	Given a particular deviceID get all the services associated with that device 
	 * @return 			collection
	 */
	public Collection<Object> getDeviceServiceList (Object deviceID);
	
	/* 
	 * Description: 	Search the device registry for all devices registered 
	 * @return 			collection
	 */
	
	public Collection<CSSDevice> findAllDevices();
	
	
	/* 
	 * Description:		Search the device registry for a particular device type 
	 * @return 			collection of device types
	 */
	public Collection<CSSDevice> findByDeviceType(String deviceType);
	
	
	/* 
	 * Description:		Clear the device registry of all entries
	 * @return 			
	 */
    public void clearRegistry();

    /* 
	 * Description:		Determine the number of devices registered in the device registry
	 * @return 			int
	 */
    public int registrySize();


}

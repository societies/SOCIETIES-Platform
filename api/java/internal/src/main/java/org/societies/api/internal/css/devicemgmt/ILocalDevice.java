package org.societies.api.internal.css.devicemgmt;

import java.util.Collection;
//import org.societies.css.devicemgmt.deviceregistry.CSSDevice;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;

public interface ILocalDevice {
	/**
     * A local device has been added
     * @param <CSSDevice>
     * 
     * @param device
     */
    public boolean addDevice(DeviceCommonInfo device, String CSSID)
            throws Exception;

    /**
     * A convenience method to handle collections of new devices
     * @param <CSSDevice>
     * 
     * @param deviceCollection
     */
    public boolean addDevices(Collection<DeviceCommonInfo> deviceCollection, String CSSID)
            throws Exception;

    /**
     * A local device has been removed
     * 
     * @param device
     */
    public boolean removeDevice(DeviceCommonInfo device, String CSSID)
            throws Exception;

    /**
     * A convenience method to handle collections of removed devices
     * 
     * @param serviceCollection
     */
    public boolean removeDevices(
            Collection<DeviceCommonInfo> deviceCollection, String CSSID)
            throws Exception;

    /**
     * Allow the registry to be cleared
     * 
     * @return boolean
     * @throws Exception
     */
    public boolean clearRegistry() throws Exception;
}
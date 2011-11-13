package org.societies.devicemgmt.deviceregistry.impl;

public class RegistryUtility {
	
	/**
     * 
     */
    private RegistryUtility() {
    }


    /**
     * Create a key string for storing a device
     *
     * @param key
     * @param value
     * @return
     */
    public static String createKeyString(IDeviceIdentifier deviceId) {

        return deviceId.toString();
    }

}

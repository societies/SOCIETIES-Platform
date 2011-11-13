package org.societies.devicemgmt.deviceregistry.impl;

import java.io.Serializable;
import java.util.Properties;


public class CSSDevice implements Serializable{
	
	private static final long serialVersionUID = 1L;

    /**
     * A human-readable name of the device
     */
    private String deviceName;

    /**
     * A human-readable description of this device
     */
    private String deviceDescription;
    
    /**
     * The id of this device
     */
    private IDeviceIdentifier deviceId;
    
       
    /**
     * The type of this device
     */
    private String deviceType;
    
    
    /**
     * Default constructor
     * 
     * @param deviceId
     * @param deviceType
     * 
     */
    public CSSDevice(IDeviceIdentifier deviceId, String deviceType) {
       
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }

    public String getdeviceName() {
        return deviceName;
    }

    public void setdeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getdeviceDescription() {
        return deviceDescription;
    }

    public void setdeviceDescription(String deviceDescription) {
        
        this.deviceDescription = deviceDescription;
    }

    public IDeviceIdentifier getdeviceId() {
        return deviceId;
    }

    public void setdeviceId(IDeviceIdentifier deviceId) {
        
        this.deviceId = deviceId;
    }

    public String getdeviceType() {
        return deviceType;
    }

    public void setdeviceType(String deviceType) {
        

        this.deviceType = deviceType;
    }

    
    
    /**
     * Override inherited equals method
     * 
     * @return boolean
     */
    public boolean equals(Object object) {
        boolean returnValue = false;

        if (null != object && object instanceof CSSDevice) {
        	CSSDevice otherdevice = (CSSDevice) object;
            if (this == object) {
                returnValue = true;
            } else if (this.deviceId.equals(otherdevice.getdeviceId())) {
                returnValue = true;
            }
        }
        return returnValue;
    }

    /**
     * Override Object hashcode method
     */
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * Override Object hashcode method
     */
    public String toString() {
        
        return this.deviceId.toString();
    }

	public IDeviceIdentifier getDeviceId() {
		// TODO Auto-generated method stub
		return this.deviceId;
	}

}


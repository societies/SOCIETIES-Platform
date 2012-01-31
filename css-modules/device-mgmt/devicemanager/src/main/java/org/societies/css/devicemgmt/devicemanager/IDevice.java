package org.societies.css.devicemgmt.devicemanager;

public interface IDevice {

	/**
	 * 
	 * @return
	 */
    public String getDeviceName();
   
    /**
     * 
     * @return
     */
    public String getDeviceId();
    
    /**
     * 
     * @return
     */
    public String getDeviceType();
    
    /**
     * 
     * @return
     */
    public String getDeviceDescription();
    
    /**
     * 
     * @return
     */
    public String getDeviceConnetionType();
    
    /**
     * 
     */
    public void enable();
    
    /**
     * 
     */
    public void disable();
    
    /**
     * 
     * @return
     */
    public boolean isEnable();
    
    /**
     * 
     * @return
     */
    public String getDeviceLocation();
    
    /**
     * 
     * @return
     */
    public String getDeviceProvider();
    
    /**
     * 
     * @return
     */
    public boolean isContextCompliant();
    
    
    
    
    
    
    

}
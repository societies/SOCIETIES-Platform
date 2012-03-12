package org.societies.css.devicemgmt.deviceregistry;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCSSDevice {
	
	private String deviceName_1 = "Device1";
    private String deviceDescription = "this is a good device";
    private String deviceId = "liam.societies.org/first/service";
    private String deviceType = "lightSensor";
    private String deviceName_2 = "Device2";
    private String deviceDescription2 = "this is a fair device";
    private String deviceId2 = "liam.societies.org/second/service";
    private String deviceType2 = "TempSensor";
    private String deviceName_3 = "Device3";
    private String deviceDescription3 = "this is a bad device";
    private String deviceId3 = "liam.societies.org/third/service";
    private String deviceType3 = "GPSSensor";
    
    private CSSDevice device_1;
	private CSSDevice device_2;
	private CSSDevice device_3;

	@Before
	public void setUp() throws Exception {
		
		device_1 = new CSSDevice(deviceName_1, deviceDescription, deviceId, deviceType);
        assertTrue(null != device_1);
        
        device_2 = new CSSDevice(deviceName_2, deviceDescription2, deviceId2, deviceType2);
        assertTrue(null != device_2);
        
        device_3 = new CSSDevice(deviceName_1, deviceDescription, deviceId, deviceType);
        assertTrue(null != device_3);
	}

	@After
	public void tearDown() throws Exception {
		
		device_1 = null;
        device_2 = null;
        device_3 = null;
	}

	@Test
	public void setname() throws Exception {
		CSSDevice device = new CSSDevice(deviceName_1, deviceDescription, deviceId, deviceType);
		assertNotNull(device);
		assertEquals(deviceName_1, device.getdeviceName());
		assertEquals(deviceDescription, device.getdeviceDescription());
		assertEquals(deviceId, device.getdeviceId());
		assertEquals(deviceType, device.getdeviceType());
		
		device.setdeviceName(deviceName_2);
        assertEquals(deviceName_2, device.getdeviceName());
        
        device.setdeviceDescription(deviceDescription2);
        assertEquals(deviceDescription2, device.getdeviceDescription());
        
        device.setdeviceId(deviceId2);
        assertEquals(deviceId2, device.getdeviceId());
        
        device.setdeviceType(deviceType2);
        assertEquals(deviceType2, device.getdeviceType());
        
        assertTrue(device_1.equals(device_3));
        assertFalse(device_1.equals(device_2));
          
	}

}

package org.societies.css.devicemgmt.RegSynchroniser.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.css.devicemgmt.deviceregistry.CSSDevice;
import org.societies.css.devicemgmt.deviceregistry.DeviceRegistry;

public class TestLocalDevices {
	
	private String deviceFamilyIdentity1 = "Sensors"; 
	private String deviceMacAddress1 = "aa:bb:cc";
	private String deviceName_1 = "Device1";
	private String deviceType = "lightSensor";
    private String deviceDescription = "this is a good device";
    private String deviceConnectionType1 = "wifi";
    private String deviceLocation1 = "Room1";
    private String deviceProvider1 = "INTEL";
    private String deviceId = "liam.societies.org/first/service";
    private boolean contextSource1 = true;
    
    
    private String deviceFamilyIdentity2 = "Actuators"; 
    private String deviceMacAddress2 = "dd:ee:ff";
    private String deviceName_2 = "Device2";
    private String deviceType2 = "TempSensor";
    private String deviceDescription2 = "this is a fair device";
    private String deviceConnectionType2 = "Zigbee";
    private String deviceLocation2 = "Room2";
    private String deviceProvider2 = "IBM";
    private String deviceId2 = "liam.societies.org/second/service";
    private boolean contextSource2 = false;
    
    
    private String deviceFamilyIdentity3 = "GPS"; 
    private String deviceMacAddress3 = "aa:aa:aa";
    private String deviceName_3 = "Device3";
    private String deviceType3 = "GPSSensor";
    private String deviceDescription3 = "this is a bad device";
    private String deviceConnectionType3 = "Bluetooth";
    private String deviceLocation3 = "Room3";
    private String deviceProvider3 = "MICROSOFT";
    private String deviceId3 = "liam.societies.org/third/service";
    private boolean contextSource3 = true;
    
    //private BundleContext context;
    private DeviceRegistry registry;
    private DeviceCommonInfo device_1;
	private DeviceCommonInfo device_2;
	private DeviceCommonInfo device_3;
	private BundleContext context;
	private String CSSNodeID = "liam@societies.org";

	@Before
	public void setUp() throws Exception {
		context = mock(BundleContext.class);
		
		device_1 = new DeviceCommonInfo(deviceFamilyIdentity1, deviceMacAddress1, deviceName_1, deviceType, deviceDescription, deviceConnectionType1, deviceLocation1, deviceProvider1, contextSource1);
        assertTrue(null != device_1);
        device_1.setDeviceID(deviceId);
        
        device_2 = new DeviceCommonInfo(deviceFamilyIdentity2, deviceMacAddress2, deviceName_2, deviceType2, deviceDescription2, deviceConnectionType2, deviceLocation2, deviceProvider2, contextSource2);
        assertTrue(null != device_2);
        device_2.setDeviceID(deviceId2);
        
        device_3 = new DeviceCommonInfo(deviceFamilyIdentity3, deviceMacAddress3, deviceName_3, deviceType3, deviceDescription3, deviceConnectionType3, deviceLocation3, deviceProvider3, contextSource3);
        assertTrue(null != device_3);
        device_3.setDeviceID(deviceId3);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLocalAddDevice() throws Exception {

        boolean retValue = false;
        
       RegManager regmanager = new RegManager(
                this.context);

        DeviceRegistry registry = DeviceRegistry.getInstance();
        assertTrue(null != registry);
        registry.clearRegistry();
        assertEquals(0, registry.registrySize());

        try {
            retValue = LocalDevices.addDevice(device_1, CSSNodeID);
            assertTrue(retValue);
            retValue = LocalDevices.addDevice(device_2, CSSNodeID);
            assertTrue(retValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        assertEquals(2, registry.registrySize());

        assertEquals(2, registry.findAllDevices().size());
        
        assertEquals(2, registry.registrySize());

        assertEquals(2, registry.findAllDevices().size());

        assertEquals(device_1, registry.findDevice(device_1.getDeviceID()));
        assertEquals(device_2, registry.findDevice(device_2.getDeviceID()));

	}
	
	@Test
	public void testLocalremoveDevice() throws Exception {

        boolean retValue = false;
        
       // RegManager regmanager = new RegManager(
         //       this.context);

        DeviceRegistry registry = DeviceRegistry.getInstance();
        assertTrue(null != registry);
        registry.clearRegistry();
        assertEquals(0, registry.registrySize());

        try {
            retValue = LocalDevices.addDevice(device_1, CSSNodeID);
            assertTrue(retValue);
            retValue = LocalDevices.addDevice(device_2, CSSNodeID);
            assertTrue(retValue);
            retValue = LocalDevices.addDevice(device_3, CSSNodeID);
            assertTrue(retValue);
            retValue = LocalDevices.removeDevice(device_2, CSSNodeID);
            assertTrue(retValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        assertEquals(2, registry.registrySize());

        assertEquals(2, registry.findAllDevices().size());
        
        assertEquals(2, registry.registrySize());

        assertEquals(2, registry.findAllDevices().size());

        assertEquals(device_1, registry.findDevice(device_1.getDeviceID()));
        assertEquals(device_3, registry.findDevice(device_3.getDeviceID()));

	}

}

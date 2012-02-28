package org.societies.css.devicemgmt.RegSynchroniser.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import static org.mockito.Mockito.mock;
import org.societies.css.devicemgmt.deviceregistry.CSSDevice;
import org.societies.css.devicemgmt.deviceregistry.DeviceRegistry;

public class TestRegManager {
	
    private static String deviceName_1 = "Device1";
    private static String deviceDescription = "this is a good device";
    private static String deviceId = "liam.societies.org/first/service";
    private static String deviceType = "lightSensor";
    private static String deviceName_2 = "Device2";
    private static String deviceDescription2 = "this is a fair device";
    private static String deviceId2 = "liam.societies.org/second/service";
    private static String deviceType2 = "TempSensor";
    private static String deviceName_3 = "Device3";
    private static String deviceDescription3 = "this is a bad device";
    private static String deviceId3 = "liam.societies.org/third/service";
    private static String deviceType3 = "GPSSensor";
    
    private BundleContext context;
    private DeviceRegistry registry;
    private CSSDevice device_1;
	private CSSDevice device_2;
	private CSSDevice device_3;
	private String CSSID = "liam@societies.org";


	@Before
	public void setUp() throws Exception {
		context = mock(BundleContext.class);
		device_1 = new CSSDevice(deviceName_1, deviceDescription, deviceId, deviceType);
        assertTrue(null != device_1);
        
        device_2 = new CSSDevice(deviceName_2, deviceDescription2, deviceId2, deviceType2);
        assertTrue(null != device_2);
        
        device_3 = new CSSDevice(deviceName_3, deviceDescription3, deviceId3, deviceType3);
        assertTrue(null != device_3);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddDevice() throws Exception {

        boolean retValue = false;
        
        RegManager regmanager = new RegManager(
                this.context);

        DeviceRegistry registry = DeviceRegistry.getInstance();
        assertTrue(null != registry);
        registry.clearRegistry();
        assertEquals(0, registry.registrySize());

        try {
            retValue = regmanager.addDevice(device_1, CSSID);
            assertTrue(retValue);
            retValue = regmanager.addDevice(device_2, CSSID);
            assertTrue(retValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        assertEquals(2, registry.registrySize());

        assertEquals(2, registry.findAllDevices().size());
        
        assertEquals(2, registry.registrySize());

        assertEquals(2, registry.findAllDevices().size());

        assertEquals(device_1, registry.findDevice(device_1.getdeviceId()));
        assertEquals(device_2, registry.findDevice(device_2.getdeviceId()));

	}
	
	@Test
	public void removeDevice() throws Exception {

        boolean retValue = false;
        
        RegManager regmanager = new RegManager(
                this.context);

        DeviceRegistry registry = DeviceRegistry.getInstance();
        assertTrue(null != registry);
        registry.clearRegistry();
        assertEquals(0, registry.registrySize());

        try {
            retValue = regmanager.addDevice(device_1, CSSID);
            assertTrue(retValue);
            retValue = regmanager.addDevice(device_2, CSSID);
            assertTrue(retValue);
            retValue = regmanager.addDevice(device_3, CSSID);
            assertTrue(retValue);
            retValue = regmanager.removeDevice(device_2, CSSID);
            assertTrue(retValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        assertEquals(2, registry.registrySize());

        assertEquals(2, registry.findAllDevices().size());
        
        assertEquals(2, registry.registrySize());

        assertEquals(2, registry.findAllDevices().size());

        assertEquals(device_1, registry.findDevice(device_1.getdeviceId()));
        assertEquals(device_3, registry.findDevice(device_3.getdeviceId()));

	}
	
	@Test
	public void addmultipleDevices() throws Exception{
		//fail("Tests Not yet implemented just putting in place holder");
		boolean retValue = false;
		//Collection<CSSDevice> devices = new Collection<CSSDevice>();
        RegManager regmanager = new RegManager(
                this.context);

        DeviceRegistry registry = DeviceRegistry.getInstance();
        assertTrue(null != registry);
        registry.clearRegistry();
        
        
        Collection<CSSDevice> Devices = new ArrayList<CSSDevice>();
       
        Devices.add(device_1);
        Devices.add(device_2);
        Devices.add(device_3);
        
		retValue =  regmanager.addDevices(Devices, CSSID);
		assertTrue(retValue);
		//retValue =  regmanager.addDevice(device_2, CSSID);
		//assertTrue(retValue);
		//retValue =  regmanager.addDevice(device_3, CSSID);
		//assertTrue(retValue);
		//assertEquals(3, registry.registrySize());
		//registry.clearRegistry();
        //assertEquals(0, registry.registrySize());
		
        Devices =  registry.findAllDevices();
        assertTrue(null != Devices);
        assertEquals(3, Devices.size());
            
	}
	
	@Test
	public void removemultipleDevices() throws Exception{
		//fail("Tests Not yet implemented just putting in place holder");
		boolean retValue = false;
		//Collection<CSSDevice> devices = new Collection<CSSDevice>();
        RegManager regmanager = new RegManager(
                this.context);

        DeviceRegistry registry = DeviceRegistry.getInstance();
        assertTrue(null != registry);
        registry.clearRegistry();
        
        
        Collection<CSSDevice> Devices = new ArrayList<CSSDevice>();
       
        Devices.add(device_1);
        Devices.add(device_2);
        //Devices.add(device_3);
        
		retValue =  regmanager.addDevice(device_1, CSSID);
		assertTrue(retValue);
		retValue =  regmanager.addDevice(device_2, CSSID);
		assertTrue(retValue);
		retValue =  regmanager.addDevice(device_3, CSSID);
		assertTrue(retValue);
		assertEquals(3, registry.registrySize());
		
		retValue = regmanager.removeDevices(Devices, CSSID);
		assertEquals(1, registry.registrySize());
		
		registry.clearRegistry();
        assertEquals(0, registry.registrySize());
		
            
	}
	
	@Test
	public void clearReg() throws Exception {

        boolean retValue = false;
        
        RegManager regmanager = new RegManager(
                this.context);

        DeviceRegistry registry = DeviceRegistry.getInstance();
        assertTrue(null != registry);
        registry.clearRegistry();
        assertEquals(0, registry.registrySize());
        
        retValue =  regmanager.addDevice(device_1, CSSID);
		assertTrue(retValue);
		retValue =  regmanager.addDevice(device_2, CSSID);
		assertTrue(retValue);
		retValue =  regmanager.addDevice(device_3, CSSID);
		assertTrue(retValue);
		assertEquals(3, registry.registrySize());

        try {
            retValue = regmanager.clearRegistry();
            assertTrue(retValue);

        } catch (Exception e) {
            e.printStackTrace();
        }

	}
}

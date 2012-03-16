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
import org.societies.api.internal.css.devicemgmt.ILocalDevice;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.api.schema.css.devicemanagment.DmEvent;
import org.societies.comm.xmpp.event.InternalEvent;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.comm.xmpp.event.PubsubEventFactory;
import org.societies.comm.xmpp.event.PubsubEventStream;
import org.societies.comm.xmpp.event.EventFactory;
import org.societies.comm.xmpp.event.EventStream;

public class TestRegManager {
/*	
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
*/
	
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
    private BundleContext context;
    private DeviceRegistry registry;
    private DeviceCommonInfo device_1;
	private DeviceCommonInfo device_2;
	private DeviceCommonInfo device_3;
	private String CSSNodeID = "liam@societies.org";
	
	private String node = "DEVICE_REGISTERED";
	
	
	private EventStream myStream;


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
	public void testAddDevice() throws Exception {

        boolean retValue = false;
        
        RegManager regmanager = new RegManager(
                this.context);

        DeviceRegistry registry = DeviceRegistry.getInstance();
        assertTrue(null != registry);
        registry.clearRegistry();
        assertEquals(0, registry.registrySize());

        try {
            retValue = regmanager.addDevice(device_1, CSSNodeID);
            assertTrue(retValue);
            retValue = regmanager.addDevice(device_2, CSSNodeID);
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
	public void removeDevice() throws Exception {

        boolean retValue = false;
        
        RegManager regmanager = new RegManager(
                this.context);

        DeviceRegistry registry = DeviceRegistry.getInstance();
        assertTrue(null != registry);
        registry.clearRegistry();
        assertEquals(0, registry.registrySize());

        try {
            retValue = regmanager.addDevice(device_1, CSSNodeID);
            assertTrue(retValue);
            retValue = regmanager.addDevice(device_2, CSSNodeID);
            assertTrue(retValue);
            retValue = regmanager.addDevice(device_3, CSSNodeID);
            assertTrue(retValue);
            retValue = regmanager.removeDevice(device_2, CSSNodeID);
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
        
        
        Collection<DeviceCommonInfo> Devices = new ArrayList<DeviceCommonInfo>();
       
        Devices.add(device_1);
        Devices.add(device_2);
        Devices.add(device_3);
        
		retValue =  regmanager.addDevices(Devices, CSSNodeID);
		assertTrue(retValue);
		//retValue =  regmanager.addDevice(device_2, CSSNodeID);
		//assertTrue(retValue);
		//retValue =  regmanager.addDevice(device_3, CSSNodeID);
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
        
        
        Collection<DeviceCommonInfo> Devices = new ArrayList<DeviceCommonInfo>();
       
        Devices.add(device_1);
        Devices.add(device_2);
        //Devices.add(device_3);
        
		retValue =  regmanager.addDevice(device_1, CSSNodeID);
		assertTrue(retValue);
		retValue =  regmanager.addDevice(device_2, CSSNodeID);
		assertTrue(retValue);
		retValue =  regmanager.addDevice(device_3, CSSNodeID);
		assertTrue(retValue);
		assertEquals(3, registry.registrySize());
		
		retValue = regmanager.removeDevices(Devices, CSSNodeID);
		assertEquals(1, registry.registrySize());
		
		registry.clearRegistry();
        assertEquals(0, registry.registrySize());
		
            
	}
	
	@Test
	public void receiveEvent() throws Exception{
		
		boolean retValue = false;
        RegManager regmanager = new RegManager(
                this.context);

        DeviceRegistry registry = DeviceRegistry.getInstance();
        assertTrue(null != registry);
        registry.clearRegistry();
        
        myStream = EventFactory.getStream("DEVICE_REGISTERED");
        assertTrue(null != myStream);
        
        System.out.println("Testing --------: testEvent ");
        try {
            retValue = regmanager.addDevice(device_1, CSSNodeID);
            assertTrue(retValue);
            retValue = regmanager.addDevice(device_2, CSSNodeID);
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
		
		InternalEvent event1 = new InternalEvent(node, device_3);
		//event1.setEventNode(node);
		
		System.out.println("Event Node is  = " + event1.getEventNode());
		System.out.println("Event Info is  = " + event1.getEventInfo());
		
		
		System.out.println("CSSID is  = " + CSSNodeID);
		
		System.out.println("Created new Event");
		myStream.multicastEvent(event1); 
		System.out.println("Just returning from sending event");
		assertEquals(2, registry.registrySize());
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
        
        retValue =  regmanager.addDevice(device_1, CSSNodeID);
		assertTrue(retValue);
		retValue =  regmanager.addDevice(device_2, CSSNodeID);
		assertTrue(retValue);
		retValue =  regmanager.addDevice(device_3, CSSNodeID);
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

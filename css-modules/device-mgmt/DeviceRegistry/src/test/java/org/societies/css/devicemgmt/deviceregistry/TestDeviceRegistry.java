/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.css.devicemgmt.deviceregistry;

import static org.junit.Assert.*;

import java.util.Collection;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
//import org.societies.css.devicemgmt.deviceregistry.CSSDevice;
import org.osgi.framework.BundleContext;
//import org.societies.api.comm.xmpp.pubsub.PubsubClient;
//import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.internal.css.devicemgmt.comm.DmCommManager;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;

public class TestDeviceRegistry {
	
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
	//private DmCommManager dmCommManager;
	private DmCommManager dmCommManagerMock; 
	
	
	private String CSSNodeID = "liam@societies.org";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
        
        registry = DeviceRegistry.getInstance();
        assertTrue(null != registry);
        registry.clearRegistry();
        //dmCommManager = DmCommManager.class.newInstance();
        //dmCommManager = DmCommManager.class.newInstance();

        //Create mocks
        context = mock(BundleContext.class);
        dmCommManagerMock = mock(DmCommManager.class);
        
        /*	
    	private String deviceFamilyIdentity; 
    	String deviceMacAddress
    	private String deviceName;
    	private String deviceType;
    	private String deviceDescription;
    	private String deviceConnectionType; 
    	private String deviceLocation;
    	private String deviceProvider;
    	private String deviceID;
    	private boolean contextSource;
    	*/
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
		
		registry.clearRegistry();
        assertEquals(0, registry.registrySize());
        registry = null;
        device_1 = null;
        device_2 = null;
        device_3 = null;
	}

	@Test
	public void addaDevice() throws Exception{
		//fail("Tests Not yet implemented just putting in place holder");
		registry.setCommManager(dmCommManagerMock);
		String result =  registry.addDevice(device_1, CSSNodeID);
		assertTrue(null != result);
		assertEquals(1, registry.registrySize());
		registry.clearRegistry();
        assertEquals(0, registry.registrySize());
	}
	
	@Test
	public void addmoreDevices() throws Exception{
		//fail("Tests Not yet implemented just putting in place holder");
		
		registry.setCommManager(dmCommManagerMock);
		String result1 =  registry.addDevice(device_1, CSSNodeID);
		assertTrue(null != result1);
		String result2 =  registry.addDevice(device_2, CSSNodeID);
		assertTrue(null != result2);
		String result3 =  registry.addDevice(device_3, CSSNodeID);
		assertTrue(null != result3);
		assertEquals(3, registry.registrySize());
		//registry.clearRegistry();
        //assertEquals(0, registry.registrySize());
		System.out.println("Device ID is  = " + device_1.getDeviceID());
		System.out.println("Device Name is  = " + device_1.getDeviceName());
		System.out.println("Device Type is  = " + device_1.getDeviceType());
		System.out.println("Device Description is  = " + device_1.getDeviceDescription());
		System.out.println("CSSNodeID is  = " + CSSNodeID);
        Collection<DeviceCommonInfo> alldevices =  registry.findAllDevices();
        assertTrue(null != alldevices);
        assertEquals(3, alldevices.size());
            
	}
	
	@Test
	public void allDevices() throws Exception{
		
		registry.setCommManager(dmCommManagerMock);
		String result1 =  registry.addDevice(device_1, CSSNodeID);
		assertTrue(null != result1);
		String result2 =  registry.addDevice(device_2, CSSNodeID);
		assertTrue(null != result2);
		String result3 =  registry.addDevice(device_3, CSSNodeID);
		assertTrue(null != result3);
		assertEquals(3, registry.registrySize());
		
        Collection<DeviceCommonInfo> alldevices =  registry.findAllDevices();
        assertTrue(null != alldevices);
        assertEquals(3, alldevices.size());
            
	}
	
	
	@Test
	public void removeDevice() throws Exception{
		registry.setCommManager(dmCommManagerMock);
		String result1 =  registry.addDevice(device_1, CSSNodeID);
		assertTrue(null != result1);
		String result2 =  registry.addDevice(device_2, CSSNodeID);
		assertTrue(null != result2);
		String result3 =  registry.addDevice(device_3, CSSNodeID);
		assertTrue(null != result3);
		assertEquals(3, registry.registrySize());
		
		assertTrue(registry.deleteDevice(device_1, CSSNodeID));
		assertTrue(registry.unregisterDevice(deviceId2));
		
        Collection<DeviceCommonInfo> alldevices =  registry.findAllDevices();
        assertTrue(null != alldevices);
        assertEquals(1, alldevices.size());
            
	}
	
	@Test
	public void findDevice() throws Exception{
		registry.setCommManager(dmCommManagerMock);
		String result1 =  registry.addDevice(device_1, CSSNodeID);
		assertTrue(null != result1);
		String result2 =  registry.addDevice(device_2, CSSNodeID);
		assertTrue(null != result2);
		String result3 =  registry.addDevice(device_3, CSSNodeID);
		assertTrue(null != result3);
		assertEquals(3, registry.registrySize());

		DeviceCommonInfo retrievedevice = registry.findDevice(deviceId2);
        
        System.out.println("retrievedevice ID is  = " + retrievedevice.getDeviceID());
		System.out.println("retrievedevice Name is  = " + retrievedevice.getDeviceName());
		System.out.println("retrievedevice Type is  = " + retrievedevice.getDeviceType());
		System.out.println("retrievedevice Description is  = " + retrievedevice.getDeviceDescription());

        assertTrue(null != retrievedevice);
        assertTrue(retrievedevice instanceof DeviceCommonInfo);
            
	}
	
	@Test
	public void findDeviceType() throws Exception{
		Collection<DeviceCommonInfo> Result = null;
		
		registry.setCommManager(dmCommManagerMock);
		String result1 =  registry.addDevice(device_1, CSSNodeID);
		assertTrue(null != result1);
		String result2 =  registry.addDevice(device_2, CSSNodeID);
		assertTrue(null != result2);
		String result3 =  registry.addDevice(device_3, CSSNodeID);
		assertTrue(null != result3);
		assertEquals(3, registry.registrySize());
		System.out.println("reg size = " + registry.registrySize());
		
		Result = registry.findByDeviceType(device_1.getDeviceType());
        assertEquals(1, Result.size());
		
		
        Result = registry.findByDeviceType(device_2.getDeviceType());
        System.out.println("retrieved devicetype = " + device_2.getDeviceType());
        assertEquals(1, Result.size());
       
      
        Result = registry.findByDeviceType(device_3.getDeviceType());
        assertEquals(1, Result.size());
        

            
	}

}

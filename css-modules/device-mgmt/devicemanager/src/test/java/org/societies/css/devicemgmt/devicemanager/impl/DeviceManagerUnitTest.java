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
package org.societies.css.devicemgmt.devicemanager.impl;

import static org.junit.Assert.*;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.societies.api.comm.xmpp.interfaces.ICommManager;

import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.css.devicemgmt.comm.DmCommManager;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;

/**
 * Describe your class here...
 *
 * @author rafik
 *
 */
public class DeviceManagerUnitTest {

	private DeviceManager deviceManager;
	private DmCommManager deviceCommManagerMock;
	private BundleContext bundleContextMock;
	private ICommManager commManagerMock;
	private IIdentityManager identityManagerMock;
	private INetworkNode iNetworkNodeMock;

	private String [] serviceIds = {"service1"};

	private Dictionary<String, String> properties;

	private ServiceRegistration sr;

	private String deviceId;
	private String physicalDeviceId;
	private DeviceCommonInfo deviceCommonInfo;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// create the necessary mocks
		bundleContextMock = mock(BundleContext.class);
		commManagerMock = mock(ICommManager.class);
		identityManagerMock = mock(IIdentityManager.class);
		iNetworkNodeMock = mock(INetworkNode.class);
		
		deviceCommManagerMock = mock(DmCommManager.class);

		deviceManager = new DeviceManager();

		//Simulate a BundleContext injection
		deviceManager.setBundleContext(bundleContextMock);
		
		//simulate a DmCommManager injection
		deviceManager.setDeviceCommManager(deviceCommManagerMock);

		//Create a stub to simulate getIdManager method call by returning identityManagerMock
		when(commManagerMock.getIdManager()).thenReturn(identityManagerMock);

		//Create a stub to simulate getThisNetworkNode method call by returning iNetworkNodeMock
		when(identityManagerMock.getThisNetworkNode()).thenReturn(iNetworkNodeMock);

		//Create a stub to simulate getting CSSNodeId
		when(iNetworkNodeMock.getJid()).thenReturn("node1");

		//Simulate a Communication manager injection
		deviceManager.setCommManager(commManagerMock);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		deviceManager = null;
	}


	@Test
	public void testFireNewDeviceConnected() {
		// this test case tests the fireNewDeviceConnected method


		//Simulate a new deviceCommonInfo received by the device manager by creating a new instance of DeviceCommonInfo class
		deviceCommonInfo = new DeviceCommonInfo("family1", "Light Sensor", "LightSensor", "just for test", "zigbee", "room1","trialog", null, true);
		physicalDeviceId = "33:40:F5";
		//Generate a deviceId from the physicalDeviceId and deviceCommonInfo information
		deviceId = iNetworkNodeMock.getJid() + "/" + deviceCommonInfo.getDeviceFamilyIdentity()+ "/" + deviceCommonInfo.getDeviceType() + "/" + physicalDeviceId;
		//Verify if the the deviceId generated equals to the deviceId returned by the fireNewDeviceConnected method.
		assertEquals( deviceId , deviceManager.fireNewDeviceConnected(physicalDeviceId, deviceCommonInfo, serviceIds));
		//Verify if the the fireNewDeviceConnected method returns null if it is called with the same deviceFamily and the deviceId
		assertEquals(null, deviceManager.fireNewDeviceConnected(physicalDeviceId, deviceCommonInfo, serviceIds));

		//Changing physicalDeviceId only
		physicalDeviceId = "2012";
		//Generating a new deviceId.
		deviceId = iNetworkNodeMock.getJid() + "/" + deviceCommonInfo.getDeviceFamilyIdentity()+ "/" + deviceCommonInfo.getDeviceType() + "/" + physicalDeviceId;
		//Verify if the the deviceId generated equals to the deviceId returned by the fireNewDeviceConnected method when we change the deviceId and no change for deviceFamily.
		assertEquals( deviceId , deviceManager.fireNewDeviceConnected(physicalDeviceId, deviceCommonInfo, serviceIds));
		//Verify if the the fireNewDeviceConnected method returns null if it is called with the same deviceFamily and the deviceId
		assertEquals(null, deviceManager.fireNewDeviceConnected(physicalDeviceId, deviceCommonInfo, serviceIds));

		//Changing the deviceFamily
		deviceCommonInfo.setDeviceFamilyIdentity("family2");
		//Create the physicalDeviceId
		physicalDeviceId = "2011";
		//Generating a new deviceId.
		deviceId = iNetworkNodeMock.getJid() + "/" + deviceCommonInfo.getDeviceFamilyIdentity()+ "/" + deviceCommonInfo.getDeviceType() + "/" + physicalDeviceId;
		//Verify if the the deviceId generated equals to the deviceId returned by the fireNewDeviceConnected method when we change the deviceId and the deviceFamily.
		assertEquals( deviceId , deviceManager.fireNewDeviceConnected(physicalDeviceId, deviceCommonInfo, serviceIds));
		//Verify if the the fireNewDeviceConnected method returns null if it is called with the same deviceFamily and the deviceId
		assertEquals(null, deviceManager.fireNewDeviceConnected(physicalDeviceId, deviceCommonInfo, serviceIds));
	}

	@Test
	public void testFireNewDeviceDisconnected() {
		// this test case tests the fireNewDeviceDisconnected method

		//Simulate a new deviceCommonInfo received by the device manager by creating a new instance of DeviceCommonInfo class
		deviceCommonInfo = new DeviceCommonInfo("family1", "Light Sensor", "LightSensor", "just for test", "zigbee", "room1","trialog", null, true);
		physicalDeviceId = "33:40:F5";
		//Verify that the method fireDeviceDisconnected returns null when we never connect device before
		assertEquals( null , deviceManager.fireDeviceDisconnected(deviceCommonInfo.getDeviceFamilyIdentity(), physicalDeviceId));

		//Simulate notifying DeviceManager about new device connected
		deviceManager.fireNewDeviceConnected(physicalDeviceId, deviceCommonInfo, serviceIds);

		//Verify that the method fireDeviceDisconnected returns the same physicalDeviceId that it receives
		assertEquals( physicalDeviceId , deviceManager.fireDeviceDisconnected(deviceCommonInfo.getDeviceFamilyIdentity(), physicalDeviceId));

		//Verify that the method fireDeviceDisconnected returns null when we pass to it a physicalDeviceId of a device that is never been connected before
		assertEquals( null , deviceManager.fireDeviceDisconnected(deviceCommonInfo.getDeviceFamilyIdentity(), "any"));


	}

}

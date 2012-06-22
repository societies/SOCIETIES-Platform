package org.societies.integration.test.bit.get_data_from_device;

import static org.junit.Assert.*;

import java.util.Dictionary;
import java.util.Hashtable;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.css.devicemgmt.model.DeviceMgmtDriverServiceNames;
import org.societies.api.css.devicemgmt.model.DeviceTypeConstants;


/**
 * @author Rafik
 *
 */
public class NominalTestCase {

	private static Logger LOG = LoggerFactory.getLogger(NominalTestCase.class);
	
	private IDriverService lsDriverService1 = null;
	public IDriverService lsDriverService2  = null;
	public IDriverService lsDriverService3  = null;
	public IDriverService screenDriverService  = null;
	
	public IAction lsIaction1 = null;
	public IAction lsIaction2 = null;
	public IAction lsIaction3 = null;
	public IAction screenIaction = null;
	
	private Dictionary<String, Object> lsInvokeActionResult1 = null;
	private Dictionary<String, Object> lsInvokeActionResult2 = null;
	private Dictionary<String, Object> lsInvokeActionResult3 = null;
	private Dictionary<String, Object> screenInvokeActionResult = null;

	public NominalTestCase() {
	}

	@Before
	public void setUp() {
		LOG.info("###899... setUp");

		
		try {
			Thread.sleep(5*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void body1() {
		LOG.info("###899... body1");

		
		
		assertNotNull("IDevice LightSensor1 is null, not tracked yet", UpperTester.ls1);
		
		String deviceId =  TestCase899.node.getJid() + "/org.societies.DeviceDriverSimulator/" + DeviceTypeConstants.LIGHT_SENSOR + "/3b:6d:01";
		assertEquals("The lisgt sensor's device name is not correct", "Light Sensor", UpperTester.ls1.getDeviceName());
		assertEquals("The lisgt sensor's device type is not correct", DeviceTypeConstants.LIGHT_SENSOR, UpperTester.ls1.getDeviceType());
		assertEquals("The lisgt sensor's device description is not correct", deviceId, UpperTester.ls1.getDeviceId());
		assertEquals("The lisgt sensor's device description is not correct", "Light Sensor 1 test", UpperTester.ls1.getDeviceDescription());
		assertEquals("The lisgt sensor's device connection type is not correct", "Zigbee", UpperTester.ls1.getDeviceConnectionType());
		assertEquals("The lisgt sensor's device location is not correct", "Room1", UpperTester.ls1.getDeviceLocation());
		assertEquals("The lisgt sensor's device location is not correct", "Trialog", UpperTester.ls1.getDeviceProvider());
		
		
		
		lsDriverService1 = UpperTester.ls1.getService(DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE);
		assertNotNull("IDriverService of the Light Sensor 1 is null", lsDriverService1);
		
		lsIaction1 = lsDriverService1.getAction("getLightLevel");
		assertNotNull("IAction of the Light Sensor 1 is null", lsIaction1);

		lsInvokeActionResult1 = lsIaction1.invokeAction(null);
		assertNotNull("getLightLevel action returns null", lsInvokeActionResult1);

	
		assertNotNull("outputLightLevel is null", lsInvokeActionResult1.get("outputLightLevel"));
		
		//TODO Adding test for eventing

	}
	
	@Test
	public void body2() {
		LOG.info("###899... body2");

		assertNotNull("IDevice LightSensor2 is null, not tracked yet", UpperTester.ls2);
		
		String deviceId =  TestCase899.node.getJid() + "/org.societies.DeviceDriverSimulator/" + DeviceTypeConstants.LIGHT_SENSOR + "/3b:6d:02";
		assertEquals("The lisgt sensor's device name is not correct", "Light Sensor", UpperTester.ls2.getDeviceName());
		assertEquals("The lisgt sensor's device type is not correct", DeviceTypeConstants.LIGHT_SENSOR, UpperTester.ls2.getDeviceType());
		assertEquals("The lisgt sensor's device description is not correct", deviceId, UpperTester.ls2.getDeviceId());
		assertEquals("The lisgt sensor's device description is not correct", "Light Sensor 2 test", UpperTester.ls2.getDeviceDescription());
		assertEquals("The lisgt sensor's device connection type is not correct", "Zigbee", UpperTester.ls2.getDeviceConnectionType());
		assertEquals("The lisgt sensor's device location is not correct", "Room2", UpperTester.ls2.getDeviceLocation());
		assertEquals("The lisgt sensor's device location is not correct", "Trialog", UpperTester.ls2.getDeviceProvider());
		
		
		
		lsDriverService2 = UpperTester.ls2.getService(DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE);
		assertNotNull("IDriverService of the Light Sensor 2 is null", lsDriverService2);
		
		lsIaction2 = lsDriverService2.getAction("getLightLevel");
		assertNotNull("IAction of the Light Sensor 2 is null", lsIaction2);

		lsInvokeActionResult2 = lsIaction2.invokeAction(null);
		assertNotNull("getLightLevel action returns null", lsInvokeActionResult2);

	
		assertNotNull("outputLightLevel is null", lsInvokeActionResult2.get("outputLightLevel"));
		
		//TODO Adding test for eventing

	}
	
	@Test
	public void body3() {
		LOG.info("###899... body3");

		assertNotNull("IDevice LightSensor3 is null, not tracked yet", UpperTester.ls3);
		
		String deviceId =  TestCase899.node.getJid() + "/org.societies.DeviceDriverSimulator/" + DeviceTypeConstants.LIGHT_SENSOR + "/3b:6d:03";
		assertEquals("The lisgt sensor's device name is not correct", "Light Sensor", UpperTester.ls3.getDeviceName());
		assertEquals("The lisgt sensor's device type is not correct", DeviceTypeConstants.LIGHT_SENSOR, UpperTester.ls3.getDeviceType());
		assertEquals("The lisgt sensor's device description is not correct", deviceId, UpperTester.ls3.getDeviceId());
		assertEquals("The lisgt sensor's device description is not correct", "Light Sensor 3 test", UpperTester.ls3.getDeviceDescription());
		assertEquals("The lisgt sensor's device connection type is not correct", "Zigbee", UpperTester.ls3.getDeviceConnectionType());
		assertEquals("The lisgt sensor's device location is not correct", "Room3", UpperTester.ls3.getDeviceLocation());
		assertEquals("The lisgt sensor's device location is not correct", "Trialog", UpperTester.ls3.getDeviceProvider());
		
		
		
		lsDriverService3 = UpperTester.ls3.getService(DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE);
		assertNotNull("IDriverService of the Light Sensor 3 is null", lsDriverService3);
		
		lsIaction3 = lsDriverService3.getAction("getLightLevel");
		assertNotNull("IAction of the Light Sensor 3 is null", lsIaction3);

		lsInvokeActionResult3 = lsIaction3.invokeAction(null);
		assertNotNull("getLightLevel action returns null", lsInvokeActionResult3);

		assertNotNull("outputLightLevel is null", lsInvokeActionResult3.get("outputLightLevel"));
		
		//TODO Adding test for eventing
		
	}

	@Test
	public void body4() {
		LOG.info("###899... body4");
		
		
		assertNotNull("IDevice Screen is null, not tracked yet", UpperTester.screen);
		
		String deviceId =  TestCase899.node.getJid() + "/org.societies.DeviceDriverSimulator/" + DeviceTypeConstants.SCREEN + "/23:75:01";
		assertEquals("The screen's device name is not correct", "Sony Screen", UpperTester.screen.getDeviceName());
		assertEquals("The screen's device type is not correct", DeviceTypeConstants.SCREEN, UpperTester.screen.getDeviceType());
		assertEquals("The screen's device description is not correct", deviceId, UpperTester.screen.getDeviceId());
		assertEquals("The screen's device description is not correct", "Screen display test", UpperTester.screen.getDeviceDescription());
		assertEquals("The screen's device connection type is not correct", "HDMI", UpperTester.screen.getDeviceConnectionType());
		assertEquals("The screen's device location is not correct", "Corridor1", UpperTester.screen.getDeviceLocation());
		assertEquals("The screen's device location is not correct", "Trialog", UpperTester.screen.getDeviceProvider());
		
		
		
		screenDriverService = UpperTester.screen.getService(DeviceMgmtDriverServiceNames.SCREEN_DRIVER_SERVICE);
		assertNotNull("IDriverService of the Screen is null", screenDriverService);
		
		screenIaction = screenDriverService.getAction("displayMessage");
		assertNotNull("IAction of the Screen is null", screenIaction);

		Dictionary<String, Object> dic = new Hashtable<String, Object>();
		dic.put("message", "Display this message for me please ! ");
		screenInvokeActionResult = screenIaction.invokeAction(dic);
		assertNull("displayMessage action returns a result", screenInvokeActionResult);

		//TODO Adding test for eventing
	}

	@After
	public void tearDown() {
		LOG.info("###899... tearDown");
	}

}
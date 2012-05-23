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
package org.societies.integration.test.bit.get_data_from_device;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.css.devicemgmt.model.DeviceMgmtDriverServiceNames;
import org.societies.api.css.devicemgmt.model.DeviceTypeConstants;
import org.springframework.osgi.context.BundleContextAware;

/**
 *
 * @author Rafik
 *
 */
public class UpperTester implements ServiceTrackerCustomizer, BundleContextAware{
	
	
	private BundleContext bundleContext;
	
	private static Logger LOG = LoggerFactory.getLogger(UpperTester.class);
	
	public UpperTester (){
		
	}
	
	
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}
	
	/**
	 * @return the bundleContext
	 */
	public BundleContext getBundleContext() {
		return bundleContext;
	}


	@Override
	public Object addingService(ServiceReference reference) {
		Object obj = bundleContext.getService(reference);

		IDevice iDevice = (IDevice)obj;

		if (iDevice != null) 
		{ 		
			if (iDevice.getDeviceType() == DeviceTypeConstants.LIGHT_SENSOR) 
			{
				if (iDevice.getDeviceLocation().equalsIgnoreCase("Room1")) 
				{
					
					IDevice ls1 = iDevice;
					IDriverService driverService = ls1.getService(DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE);
					
					IAction ia = driverService.getAction("getLightLevel");
					
					Dictionary dic = ia.invokeAction(null);
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Name: "+ ls1.getDeviceName());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Type: "+ ls1.getDeviceType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device ID: "+ ls1.getDeviceId());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Description: "+ ls1.getDeviceDescription());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device provider: "+ ls1.getDeviceProvider());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Connection Type: "+ ls1.getDeviceConnetionType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Location: "+ ls1.getDeviceLocation());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% getLightLevel action Return: "+ dic.get("outputLightLevel"));
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
				}
				else if (iDevice.getDeviceLocation().equalsIgnoreCase("Room2")) 
				{
					
					IDevice ls2 = iDevice;
					IDriverService driverService = ls2.getService(DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE);
					
					IAction ia = driverService.getAction("getLightLevel");

					Dictionary dic = ia.invokeAction(null);
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Name: "+ ls2.getDeviceName());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Type: "+ ls2.getDeviceType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device ID: "+ ls2.getDeviceId());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Description: "+ ls2.getDeviceDescription());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device provider: "+ ls2.getDeviceProvider());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Connection Type: "+ ls2.getDeviceConnetionType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Location: "+ ls2.getDeviceLocation());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% getLightLevel action Return: "+ dic.get("outputLightLevel"));
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
				}
				else if (iDevice.getDeviceLocation().equalsIgnoreCase("Room3")) 
				{
					IDevice ls3 = iDevice;
					IDriverService driverService = ls3.getService(DeviceMgmtDriverServiceNames.LIGHT_SENSOR_DRIVER_SERVICE);
					
					IAction ia = driverService.getAction("getLightLevel");

					Dictionary dic = ia.invokeAction(null);
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Name: "+ ls3.getDeviceName());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Type: "+ ls3.getDeviceType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device ID: "+ ls3.getDeviceId());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Description: "+ ls3.getDeviceDescription());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device provider: "+ ls3.getDeviceProvider());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Connection Type: "+ ls3.getDeviceConnetionType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Location: "+ ls3.getDeviceLocation());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% getLightLevel action Return: "+ dic.get("outputLightLevel"));
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
				}
			}
			else if (iDevice.getDeviceType() == DeviceTypeConstants.SCREEN)
			{
				if (iDevice.getDeviceLocation().equalsIgnoreCase("Corridor1")) 
				{
					IDevice screen1 = iDevice;
					IDriverService driverService = screen1.getService(DeviceMgmtDriverServiceNames.SCREEN_DRIVER_SERVICE);
					
					IAction ia = driverService.getAction("displayMessage");

					Dictionary<String, Object> dic = new Hashtable<String, Object>();
					dic.put("message", "Display this message for me please ! ");
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Name: "+ screen1.getDeviceName());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Location: "+ screen1.getDeviceType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device ID: "+ screen1.getDeviceId());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Description: "+ screen1.getDeviceDescription());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device provider: "+ screen1.getDeviceProvider());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Connection Type: "+ screen1.getDeviceConnetionType());
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Device Location: "+ screen1.getDeviceLocation());
					ia.invokeAction(dic);
					
					LOG.info("DeviceMgmtConsumer: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
				}
				
			}
		}
		return null;
	}


	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		
	}


	@Override
	public void removedService(ServiceReference reference, Object service) {
		
	}

}

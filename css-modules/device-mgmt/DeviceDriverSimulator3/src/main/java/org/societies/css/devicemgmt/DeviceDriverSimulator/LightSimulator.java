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
package org.societies.css.devicemgmt.DeviceDriverSimulator;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  This thread is created to simulate the change of temperature.
 * we decide to inform the first listener
 * @author Olivier Maridat (Trialog)
 */
public class LightSimulator extends Thread {
	private static Logger LOG = LoggerFactory.getLogger(LightSimulator.class.getSimpleName());

	public boolean run = true;
	private LightSensor lightSensor ;
	private long delay;
	private String lsId;

	public LightSimulator (Object d, String deviceId)
	{
		LOG.info("Start Light Simulation");
		lightSensor = (LightSensor) d;
		lsId=deviceId;
		this.start();	
	}
	public synchronized void stopSimul ()
	{
		LOG.info("Stop Light Simulation");
		run = false;
	}

	public void run ()
	{   
		while (run == true) {				
			// Fisrt delay to start the simulators at different time 
			try {
				Random randomGenerator = new Random();
				Thread.sleep((randomGenerator.nextInt(10)+1)*1000);
			}
			catch(Exception e){
				LOG.error("Thread error:", e);
			}
			lightSensor.setLightLevel();
			LOG.info("Someone has modified the light level of sensor " + lsId+" to: "+Double.toString(lightSensor.getLightValue()));

			try {
				Thread.sleep(10000);
			}
			catch(Exception e){
				LOG.error("Thread error:", e);
			}
		}
	}
	public void startSimul() {
		LOG.info("Start Simulation");
	}
}

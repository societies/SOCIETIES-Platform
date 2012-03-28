package org.societies.css.devicemgmt.DeviceDriverSimulator;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  This thread is created to simulate the change of temperature.
 * we decide to inform the first listener
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
				Thread.sleep((10*randomGenerator.nextInt()+1)*1000);
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

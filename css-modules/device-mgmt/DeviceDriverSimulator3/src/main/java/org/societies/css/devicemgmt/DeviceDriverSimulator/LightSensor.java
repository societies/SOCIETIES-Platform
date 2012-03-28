package org.societies.css.devicemgmt.DeviceDriverSimulator;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.DriverService;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDeviceStateVariable;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.css.devicemgmt.DeviceDriverSimulator.actions.GetLightLevelAction;
import org.societies.css.devicemgmt.DeviceDriverSimulator.statevariables.LightLevelStateVariable;

public class LightSensor extends DriverService {
	protected static Logger LOG = LoggerFactory.getLogger(LightSensor.class.getSimpleName());

	private LightSimulator lightSimulator;
	private Double lightValue;
	private Double maxValue;
	private Double minValue;
	private boolean dec ;


	/* --- Constructors --- */
	
	public LightSensor(String serviceId, String physicalDeviceId, IEventMgr eventManager) {
		super(serviceId, physicalDeviceId, eventManager);

		IDeviceStateVariable stateVariable = new LightLevelStateVariable();
		stateVariables.put(stateVariable.getName(), stateVariable);

		IAction action = new GetLightLevelAction(this, (LightLevelStateVariable) stateVariable);
		actions.put(action.getName(), action);

		lightValue= 100.0;
		maxValue= 150.0 ;
		minValue= 50.0;
		dec=true;
	}
	public LightSensor(String serviceId, String physicalDeviceId, String deviceId, IEventMgr eventManager) {
		this(serviceId, physicalDeviceId, eventManager);
		setDeviceId(deviceId);
	}

	
	/* --- Getters / Setters --- */
	
	@Override
	public void setDeviceId(String deviceId) {
		super.setDeviceId(deviceId);
		if (ready) {
			lightSimulator = new LightSimulator(this, deviceId);
		}
	}
	
	public double getLightValue() {
		return lightValue;
	}
	public void setLightLevel() {
		if (lightValue > maxValue ){
			dec =true;
		}
		else if (lightValue < minValue ) {
			dec = false;
		}
		if (dec) {
			lightValue--;
		}
		else {
			lightValue++;
		}

		LOG.info("Publish internal event: org/societies/css/device -> LightSensorEvent");
		HashMap<String, Object> payload = new HashMap<String, Object>();
		payload.put("lightLevel", getLightValue());
		InternalEvent event = new InternalEvent("org/societies/css/device", "LightSensorEvent", deviceId, payload);
		try {
			eventManager.publishInternalEvent(event);
		} catch (EMSException e) {
			LOG.error("Error when publishing new light level", e);
		}
	}
}

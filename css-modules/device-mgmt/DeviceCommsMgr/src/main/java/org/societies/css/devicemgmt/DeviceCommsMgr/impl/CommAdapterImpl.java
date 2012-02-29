package org.societies.css.devicemgmt.DeviceCommsMgr.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.api.schema.css.devicemanagment.DmEvent;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.comm.xmpp.event.PubsubEventFactory;
import org.societies.comm.xmpp.event.PubsubEventStream;
import org.societies.comm.xmpp.interfaces.IdentityManager;
import org.societies.css.devicemgmt.DeviceCommsMgr.CommAdapter;

public class CommAdapterImpl implements CommAdapter{

	public static final String DEVICE_CONNECTED = "DEVICE_REGISTERED";
	public static final String DEVICE_DISCONNECTED = "DEVICE_DISCONNECTED";
	public static final String DATA_CAHNGED_PREFIX_EVENT = "DEVICE_DATA_CAHNGED_PREFIX";
	public static final String JID = "XCManager.societies.local";
	public static final String SCHEMA = "org.societies.api.schema.css.devicemanagement.xsd";
	
	@Override
	public void fireNewDeviceConnected(String deviceId,DeviceCommonInfo deviceCommonInfo) {
		DmEvent dmEvent = generateEvent(deviceId, deviceCommonInfo);
		sendEvent(DEVICE_CONNECTED, dmEvent);
	}

	@Override
	public void fireDeviceDisconnected(String deviceId,	DeviceCommonInfo deviceCommonInfo) {
		DmEvent dmEvent = generateEvent(deviceId, deviceCommonInfo);
		sendEvent(DEVICE_DISCONNECTED, dmEvent);
	}

	@Override
	public void fireDeviceDataChanged(String deviceId,DeviceCommonInfo deviceCommonInfo, String key,String value) {
		DmEvent dmEvent = generateEvent(deviceId, deviceCommonInfo);
		dmEvent.setKey(key);
		dmEvent.setValue(value);
		String eventNodeId = DATA_CAHNGED_PREFIX_EVENT+deviceId;
		sendEvent(eventNodeId, dmEvent);
	}
	
	private DmEvent generateEvent(String deviceId, DeviceCommonInfo deviceCommonInfo){
		DmEvent dmEvent = new DmEvent();
		dmEvent.setDeviceId(deviceId);
		dmEvent.setDescription(deviceCommonInfo.getDeviceDescription());
		dmEvent.setType(deviceCommonInfo.getDeviceType());
		return dmEvent;
	}
	
	private void sendEvent(String type, DmEvent dmEvent){
		IdentityManager idManager = new IdentityManager();
		Identity pubsubID = idManager.fromJid(JID);
		PubsubEventFactory eventFactory = PubsubEventFactory.getInstance(pubsubID);
		PubsubEventStream eventStream=null;
		
		//ADD LIST OF PACKAGES TO SUPPORT SCHEMA OBJECTS
		List<String> packageList = new ArrayList<String>();
		packageList.add(SCHEMA);
		
		try {
			eventStream.addJaxbPackages(packageList);
		} catch (JAXBException e1) {
			//ERROR RESOLVING PACKAGE NAMES - CHECK PATH IS CORRECT
			e1.printStackTrace();
		}
		
		try{
			eventStream = eventFactory.getStream(pubsubID, type);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		//GENERATE EVENT
		PubsubEvent event = new PubsubEvent(this, dmEvent);
		eventStream.multicastEvent(event);
	}
	
}


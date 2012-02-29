package org.societies.css.devicemgmt.DeviceCommsMgr.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.api.schema.devicemanagement.DmEvent;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.comm.xmpp.event.PubsubEventFactory;
import org.societies.comm.xmpp.event.PubsubEventStream;
import org.societies.comm.xmpp.interfaces.IdentityManager;
import org.societies.css.devicemgmt.DeviceCommsMgr.CommAdapter;

public class CommAdapterImpl implements CommAdapter{

	public static final String DEVICE_CONNECTED = "DEVICE_REGISTERED";
	public static final String DEVICE_DISCONNECTED = "DEVICE_DISCONNECTED";
	public static final String DEVICE_DATA_CAHNGED_PREFIX = "DEVICE_DATA_CAHNGED_PREFIX";
	public static final String JID = "XCManager.societies.local";
	public static final String SCHEMA = "org.societies.api.schema.css.devicemanagement.xsd";
	
	@Override
	public void fireNewDeviceConnected(String deviceID,DeviceCommonInfo deviceCommonInfo) {
		DmEvent dmEvent = new DmEvent();
		dmEvent.setDeviceId(deviceID);
		dmEvent.setDescription("");
		
		sendEvent(DEVICE_CONNECTED, dmEvent);
	}

	@Override
	public void fireDeviceDisconnected(String deviceID,	DeviceCommonInfo deviceCommonInfo) {
		DmEvent dmEvent = new DmEvent();
		dmEvent.setDeviceId(deviceID);
		dmEvent.setDescription("");
		sendEvent(DEVICE_DISCONNECTED, dmEvent);
	}

	@Override
	public void fireDeviceDataChanged(String deviceID,	Map<String, String> values) {
		DmEvent dmEvent = new DmEvent();
		dmEvent.setDeviceId(deviceID);
		dmEvent.setDescription("");
		String eventNodeId = DEVICE_DATA_CAHNGED_PREFIX+deviceID;
		sendEvent(eventNodeId, dmEvent);
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


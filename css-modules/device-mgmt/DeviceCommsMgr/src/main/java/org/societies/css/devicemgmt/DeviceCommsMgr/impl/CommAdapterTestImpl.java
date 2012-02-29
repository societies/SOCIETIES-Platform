package org.societies.css.devicemgmt.DeviceCommsMgr.impl;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.internal.css.devicemgmt.model.DeviceCommonInfo;
import org.societies.comm.xmpp.event.PubsubEvent;
import org.societies.comm.xmpp.event.PubsubEventFactory;
import org.societies.comm.xmpp.event.PubsubEventStream;
import org.societies.comm.xmpp.interfaces.IdentityManager;
import org.societies.css.devicemgmt.DeviceCommsMgr.CommAdapter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class CommAdapterTestImpl implements CommAdapter,ApplicationListener<ApplicationEvent>{
	
	private String EVENTING_NODE_NAME = "GUY";
	
	int delay = 1000*20;   // delay for 5 sec.
	int period = 1000*20;  // repeat every sec.
	Timer timer = new Timer();
	
	PubsubClient pubsubClient;
	
	public CommAdapterTestImpl(){
		timer.scheduleAtFixedRate(new TimerTask() {
		        public void run() {
		        	sendEvent();
		        }
		    }, delay, period);
	}
		
	
	public void fireNewDeviceConnected(String deviceID, DeviceCommonInfo deviceCommonInfo){
		
		
	}
	
	public void fireDeviceDisconnected(String deviceID, DeviceCommonInfo deviceCommonInfo){	
		
	}

	
	public void fireDeviceDataChanged(String deviceID,Map<String,String> values){
		
	}

	private void sendEvent(){
		IdentityManager idManager = new IdentityManager();
		Identity pubsubID = idManager.fromJid("XCManager.societies.local");
		PubsubEventFactory eventFactory = PubsubEventFactory.getInstance(pubsubID);
		PubsubEventStream eventStream=null;
		try{
			eventStream = eventFactory.getStream(pubsubID, EVENTING_NODE_NAME);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		 
		Document doc; Element entry = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			entry = doc.getDocumentElement();
			
			Node wisdom = doc.createElement("wisdom"); 
			wisdom.setNodeValue(Long.valueOf(System.currentTimeMillis()).toString());
			
			entry.appendChild(wisdom);
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		eventStream.addApplicationListener(this);

			//GENERATE EVENT
		PubsubEvent event = new PubsubEvent(this, entry);
		eventStream.multicastEvent(event);
	}
	
	

	@Override
	public void onApplicationEvent(ApplicationEvent arg0) {
		System.out.println(arg0.getTimestamp());
		System.out.println(arg0.getSource());
	}
	
	public static void main(String[] agrgs){
		CommAdapterTestImpl commAdapterImpl = new CommAdapterTestImpl();
		commAdapterImpl.sendEvent();
	}


	public PubsubClient getPubsubClient() {
		return pubsubClient;
	}


	public void setPubsubClient(PubsubClient pubsubClient) {
		this.pubsubClient = pubsubClient;
	}
	
	
}

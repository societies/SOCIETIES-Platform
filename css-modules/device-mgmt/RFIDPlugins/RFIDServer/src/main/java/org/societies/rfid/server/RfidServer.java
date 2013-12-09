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

package org.societies.rfid.server;

import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Timer;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.devicemgmt.IAction;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.css.devicemgmt.IDriverService;
import org.societies.api.css.devicemgmt.model.DeviceActionsConstants;
import org.societies.api.css.devicemgmt.model.DeviceMgmtConstants;
import org.societies.api.css.devicemgmt.model.DeviceMgmtDriverServiceNames;
import org.societies.api.css.devicemgmt.model.DeviceMgmtEventConstants;
import org.societies.api.css.devicemgmt.model.DeviceStateVariableConstants;
import org.societies.api.css.devicemgmt.model.DeviceTypeConstants;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.rfid.client.api.remote.IRfidClient;
import org.societies.rfid.server.api.IRfidServer;
import org.springframework.osgi.context.BundleContextAware;


public class RfidServer extends EventListener implements IRfidServer, ServiceTrackerCustomizer, BundleContextAware {


	private Logger logging = LoggerFactory.getLogger(this.getClass());
	//private ServiceResourceIdentifier myServiceId;
	//private List<String> myServiceTypes = new ArrayList<String>();

	private Hashtable<String, String> tagToPasswordTable;

	private Hashtable<String, String> tagtoIdentityTable;
	
	private Hashtable<String, String> tagtoSymlocTable; //TAG TO SYMLOC

	private Hashtable<String, String> wUnitToSymlocTable;


	private IEventMgr eventMgr;

	//private Hashtable<String, String> dpiToServiceID;
	Hashtable<String, RFIDUpdateTimerTask> tagToTimerTable = new Hashtable<String, RFIDUpdateTimerTask>();

	private IRfidClient rfidClientRemote;
	
	private BundleContext bundleContext;

	private ServiceTracker serviceTracker;

	private IDevice iDevice;

	private ContextRetriever ctxRetriever;

	private ICommManager commManager;
	private IIdentityManager idManager;

	private IIdentity serverIdentity;
	private ICtxBroker ctxBroker;
	private RfidWebAppEventListener webAppEventListener;

	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	/**
	 * @return the eventMgr
	 */
	public IEventMgr getEventMgr() {
		return eventMgr;
	}

	/**
	 * @param eventMgr the eventMgr to set
	 */
	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}



	public void initialiseRFIDServer(){

		String stringFilter = "(&("+ Constants.OBJECTCLASS +"="+ IDevice.class.getName()+")("+DeviceMgmtConstants.DEVICE_TYPE+"="+DeviceTypeConstants.RFID_READER+"))";

		Filter filter = null;
		try {
			filter = bundleContext.createFilter(stringFilter);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

		this.serviceTracker = new ServiceTracker(bundleContext, filter, this);
		this.serviceTracker.open();



		this.tagtoIdentityTable = new Hashtable<String, String>();
		this.tagToPasswordTable = new Hashtable<String, String>();
		this.tagtoSymlocTable = new Hashtable<String, String>();
		//this.dpiToServiceID = new Hashtable<String, String>();
		RFIDConfig rfidConfig = new RFIDConfig();
		this.wUnitToSymlocTable = rfidConfig.getUnitToSymloc();
		if (this.wUnitToSymlocTable==null){
			this.wUnitToSymlocTable = new Hashtable<String, String>();

		}
		this.registerRFIDReaders();

		this.ctxRetriever = new ContextRetriever(getCtxBroker(), this.serverIdentity);
		this.tagtoIdentityTable = ctxRetriever.getTagToIdentity();
		this.tagToPasswordTable = ctxRetriever.getTagToPassword();
		this.tagtoSymlocTable = ctxRetriever.getTagToSymloc();
		logging.debug("in initialisation - starting eventlistener");
		this.webAppEventListener = new RfidWebAppEventListener(this);
	}


	private void registerRFIDReaders(){
		String[] options = new String[]{"0.localhost","1.University addresses"};
		//String str = (String) JOptionPane.showInputDialog(null, "Select Configuration", "Configuration", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		String str = options[1];
		if (str.equalsIgnoreCase(options[0])){

			if (null != iDevice) {

				this.registerForRFIDEvents(iDevice.getDeviceId());

				IDriverService iDriverService = iDevice.getService(DeviceMgmtDriverServiceNames.RFID_READER_DRIVER_SERVICE);
				IAction iAction = iDriverService.getAction(DeviceActionsConstants.RFID_CONNECT_ACTION);

				Dictionary<String, Object> dic = new Hashtable<String, Object>();

				dic.put(DeviceStateVariableConstants.IP_ADDRESS_STATE_VARIABLE, "127.0.0.1");
				iAction.invokeAction(dic);
			}


			//this.rfidDriver.connect("127.0.0.1");
		}else{

			if (null != iDevice) 
			{
				this.registerForRFIDEvents(iDevice.getDeviceId());

				IDriverService iDriverService = iDevice.getService(DeviceMgmtDriverServiceNames.RFID_READER_DRIVER_SERVICE);
				IAction iAction = iDriverService.getAction(DeviceActionsConstants.RFID_CONNECT_ACTION);

				Dictionary<String, Object> dic = new Hashtable<String, Object>();

				//dic.put(DeviceStateVariableConstants.IP_ADDRESS_STATE_VARIABLE, "137.195.27.199");
				//iAction.invokeAction(dic);

				dic = new Hashtable<String, Object>();
				dic.put(DeviceStateVariableConstants.IP_ADDRESS_STATE_VARIABLE, "137.195.27.198");
				iAction.invokeAction(dic);
				
				dic = new Hashtable<String, Object>();
				dic.put(DeviceStateVariableConstants.IP_ADDRESS_STATE_VARIABLE, "137.195.27.197");
				iAction.invokeAction(dic);
				
				
			}

			//this.rfidDriver.connect("137.195.27.197");
			//this.rfidDriver.connect("137.195.27.198");
		}

	}
	public RfidServer(){
		logging.debug("started!");
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	private void registerForRFIDEvents(String deviceId){
		String eventFilter = "(&" + 
				"(" + CSSEventConstants.EVENT_NAME + "=" + DeviceMgmtEventConstants.RFID_READER_EVENT + ")" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=" + deviceId + ")" +
				")";
		if(logging.isDebugEnabled()) logging.debug("Registering for RFIDEvent: "+eventFilter);

		this.eventMgr.subscribeInternalEvent(this, new String[]{EventTypes.RFID_UPDATE_EVENT}, eventFilter);


	}


	/*
	 * This method is called by the RFIDUpdateTimerTask every minute to 
	 * send the symbolic locations to the appropriate user
	 */
	@Override
	public void sendRemoteUpdate(String tagNumber, String symLoc){
		if (this.tagtoIdentityTable.containsKey(tagNumber)){
			String jid = this.tagtoIdentityTable.get(tagNumber);
			updateTagToSymloc(symLoc, tagNumber);
			//String clientServiceID = this.dpiToServiceID.get(dpi);
			//this.sendUpdateMessage(dpi, clientServiceID, rfidTagNumber, symLoc);
			this.rfidClientRemote.sendUpdate(jid, symLoc, tagNumber);
		}

	}
	
	public void updateTagToSymloc(String symloc, String tag) {
		this.tagtoSymlocTable.remove(tag);//remove last entry if exists
		this.tagtoSymlocTable.put(tag,symloc);
		this.ctxRetriever.setTagToSymloc(tagtoSymlocTable);
		this.ctxRetriever.updateContext();
		logging.debug("Tag to Symloc Updated!");
	}



	/*
	 * Method called when an RFID_UPDATE_EVENT is received
	 */
	public void sendUpdate(String wUnit, String rfidTagNumber) {

		if (this.wUnitToSymlocTable.containsKey(wUnit)){
			if(logging.isDebugEnabled()) logging.debug("wUnit: "+wUnit+" matches symloc: "+wUnitToSymlocTable.get(wUnit));
			if (this.tagToTimerTable.containsKey(rfidTagNumber)){

				this.tagToTimerTable.get(rfidTagNumber).setSymLoc(this.wUnitToSymlocTable.get(wUnit));
				if(logging.isDebugEnabled()) logging.debug("setting symloc: "+this.wUnitToSymlocTable.get(wUnit)+" to: "+rfidTagNumber);
			}else{
				if (this.tagtoIdentityTable.containsKey(rfidTagNumber)){
					if(logging.isDebugEnabled()) logging.debug("tag "+rfidTagNumber+" registered to identity "+tagtoIdentityTable.get(rfidTagNumber));
					RFIDUpdateTimerTask task = new RFIDUpdateTimerTask(this, rfidTagNumber, this.wUnitToSymlocTable.get(wUnit), this.tagtoIdentityTable.get(rfidTagNumber));
					this.tagToTimerTable.put(rfidTagNumber, task);
					Timer timer = new Timer();
					timer.schedule(task, new Date(), 5000);
					if(logging.isDebugEnabled()) logging.debug("Created timer");
				}
			}

		}else{
			if(logging.isDebugEnabled()) logging.debug("wUnit :"+wUnit+" doesn't match any symLoc");
		}



	}

	@Override
	public void registerRFIDTag(String tagNumber, String dpiAsString, String serviceID, String password) {
		logging.debug("Received request to register RFID tag: "+tagNumber+" from identity: "+dpiAsString+" and serviceID: "+serviceID+" and password: "+password);
		if (this.tagToPasswordTable.containsKey(tagNumber)){
			String myPass = this.tagToPasswordTable.get(tagNumber);
			logging.debug("Tag exists");
			if (myPass.equalsIgnoreCase(password)){

				this.removeOldRegistration(dpiAsString);

				this.tagtoIdentityTable.put(tagNumber, dpiAsString);
				this.ctxRetriever.setTagToIdentity(tagtoIdentityTable);
				this.ctxRetriever.setTagToPassword(tagToPasswordTable);
				this.ctxRetriever.setTagToSymloc(tagtoSymlocTable);//MAYBE DONT NEED?!
				this.ctxRetriever.updateContext();
				this.rfidClientRemote.acknowledgeRegistration(dpiAsString, 0);
				logging.debug("Registration successfull. Sent Acknowledgement 0");

			}else{
				this.rfidClientRemote.acknowledgeRegistration(dpiAsString, 1);
				logging.debug("Registration unsuccessfull. Sent Ack 1");
			}
		}else{

			this.rfidClientRemote.acknowledgeRegistration(dpiAsString, 2);
			logging.debug("Registration unsuccessfull. Sent Ack 2");
		}



	}

	@Override
	public void unregisterRFIDTag(String tagNumber, String dpiAsString, String serviceID, String password) {
		logging.debug("Received request to unregister RFID tag: "+tagNumber+" from identity: "+dpiAsString+" and serviceID: "+serviceID+" and password: "+password);
		this.tagtoIdentityTable.remove(tagNumber);
		this.tagToPasswordTable.remove(tagNumber);
		this.tagToPasswordTable.put(tagNumber, getPassword());
		this.ctxRetriever.setTagToIdentity(tagtoIdentityTable);
		this.ctxRetriever.setTagToPassword(tagToPasswordTable);
		this.ctxRetriever.updateContext();
		//TODO ACK's
		this.rfidClientRemote.acknowledgeRegistration(dpiAsString, 3);
		logging.debug("UnRegistration successfull. Sent Acknowledgement 3");


	}



	public void removeOldRegistration( String dpiAsString){
		if (this.tagtoIdentityTable.contains(dpiAsString)){

			Enumeration<String> tags = this.tagtoIdentityTable.keys();

			while (tags.hasMoreElements()){
				String tag = tags.nextElement();
				String dpi = this.tagtoIdentityTable.get(tag);
				if (dpi.equalsIgnoreCase(dpiAsString)){
					this.tagtoIdentityTable.remove(tag);
					/*if (this.dpiToServiceID.containsKey(dpiAsString)){
						this.dpiToServiceID.remove(dpiAsString);
					}*/
					return;
				}
			}
		}
	}
	

	@Override
	public void deleteTag(String tag) {
		this.tagtoIdentityTable.remove(tag);
		this.tagToPasswordTable.remove(tag);
		this.ctxRetriever.setTagToIdentity(tagtoIdentityTable);
		this.ctxRetriever.setTagToPassword(tagToPasswordTable);
		this.ctxRetriever.updateContext();
		if(logging.isDebugEnabled()) logging.debug("deleted rfidTag: "+tag);
		this.printInformation();
	}

	//CHECKS IF TAG IS REGISTERED TO A USER, THEN REQUESTS TO UNREGISTER. OR DELETES TAG IF NO REG
	public void requestDeleteTag(String tag){
		String id = tagtoIdentityTable.get(tag);
		if(id!=null)
		{
			this.rfidClientRemote.deleteTag(id, tag);
			//this.rfidClientRemote.acknowledgeRegistration(identity, rStatus);
			//TAG HAS BEEN REGISTERED WITH ID
		}
		else
		{
			deleteTag(tag);
		}


	}

	private void printInformation(){
		Enumeration<String> keys = this.tagToPasswordTable.keys();
		int i=1;
		while(keys.hasMoreElements()){
			String nextElement = keys.nextElement();
			if (this.tagtoIdentityTable.containsKey(nextElement)){
				if(logging.isDebugEnabled()) logging.debug("Rfid record "+i+") rfidTag: "+nextElement+", password: "+tagToPasswordTable.get(nextElement)+", identity: "+this.tagtoIdentityTable.get(nextElement));
			}else{
				if(logging.isDebugEnabled()) logging.debug("Rfid record "+i+") rfidTag: "+nextElement+", password: "+tagToPasswordTable.get(nextElement));
			}
			i++;
		}
	}
	public String getPassword() {
		int n = 4;
		char[] pw = new char[n];
		int c  = 'A';
		int  r1 = 0;
		for (int i=0; i < n; i++)
		{
			r1 = (int)(Math.random() * 3);
			switch(r1) {
			case 0: c = '0' +  (int)(Math.random() * 10); break;
			case 1: c = 'a' +  (int)(Math.random() * 26); break;
			case 2: c = 'A' +  (int)(Math.random() * 26); break;
			}
			pw[i] = (char)c;
		}
		return new String(pw);
	}

	public void addTag(String tagNumber, String password){
		this.tagToPasswordTable.put(tagNumber, password);
		this.tagtoIdentityTable.remove(tagNumber);
		this.ctxRetriever.setTagToIdentity(tagtoIdentityTable);
		this.ctxRetriever.setTagToPassword(tagToPasswordTable);
		this.ctxRetriever.updateContext();
		this.printInformation();
	}




	/**
	 * @return the rfidClient
	 */
	public IRfidClient getRfidClientRemote() {
		return rfidClientRemote;
	}



	/**
	 * @param rfidClientRemote the rfidClient to set
	 */
	public void setRfidClientRemote(IRfidClient rfidClientRemote) {
		this.rfidClientRemote = rfidClientRemote;
	}

	//	/**
	//	 * @return the rfidDriver
	//	 */
	//	public IRfidDriver getRfidDriver() {
	//		return rfidDriver;
	//	}
	//
	//	/**
	//	 * @param rfidDriver the rfidDriver to set
	//	 */
	//	public void setRfidDriver(IRfidDriver rfidDriver) {
	//		this.rfidDriver = rfidDriver;
	//	}

	@Override
	public void handleExternalEvent(CSSEvent arg0) {

	}

	@Override
	public void handleInternalEvent(InternalEvent event) {
		//		if (event.geteventInfo() instanceof RFIDUpdateEvent){
		//			RFIDUpdateEvent rfidEvent = (RFIDUpdateEvent) event.geteventInfo();
		//			this.sendUpdate(rfidEvent.getWakeupUnit(), rfidEvent.getRFIDTagNumber());
		//			if(logging.isDebugEnabled()) logging.debug("Received RFIDUpdateEvent: "+rfidEvent.getWakeupUnit()+" - "+rfidEvent.getRFIDTagNumber() );
		//		}


		HashMap<String, String> payload = (HashMap<String, String>)event.geteventInfo();
		if(logging.isDebugEnabled()) logging.debug("Received RFIDUpdateEvent: "+payload.get("wakeupUnit")+" - "+ payload.get("tagNumber"));
		this.sendUpdate(payload.get("wakeupUnit"), payload.get("tagNumber"));
		if(logging.isDebugEnabled()) logging.debug("Sent rfid update WU:"+payload.get("wakeupUnit")+" tag: "+payload.get("tagNumber"));


	}





	@Override
	public Object addingService(ServiceReference reference) {

		iDevice = (IDevice) bundleContext.getService(reference);


		return iDevice;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service) {


	}

	@Override
	public void removedService(ServiceReference reference, Object service) {

	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		idManager = commManager.getIdManager();
		serverIdentity = idManager.getThisNetworkNode();
	}

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
}
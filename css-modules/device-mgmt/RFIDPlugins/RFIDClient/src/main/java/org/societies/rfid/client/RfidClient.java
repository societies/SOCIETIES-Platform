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
package org.societies.rfid.client;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.source.CtxSourceNames;
import org.societies.api.context.source.ICtxSourceMgr;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.rfid.client.api.IRfidClient;
import org.societies.rfid.server.api.remote.IRfidServer;





/**
 * @author  Eliza Papadopoulou
 * @created December, 2010
 */

public class RfidClient extends EventListener implements IRfidClient {

	private static final String RFID_EVENT_TYPE = "org/societies/rfid";
	private static final String RFID_REGISTRATION_ERROR = "RFID_REGISTRATION_ERROR";
	private static final String RFID_INFO = "RFID_INFO";
	private final static String CTX_SOURCE_ID = "CTX_SOURCE_ID";
	private final static String RFID_TAG = "RFID_TAG";
	private final static String RFID_PASSWORD = "RFID_PASSWORD";
	private final static String RFID_SERVER = "RFID_SERVER";
	private final static String RFID_REGISTERED = "RFID_REGISTERED";
	private final static String RFID_UNREGISTERED = "RFID_UNREGISTERED";
	private final static String RFID_LAST_LOCATION = "RFID_LAST_LOCATION";


	private ICommManager commManager;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private ICtxSourceMgr ctxSourceMgr;
	private IIdentityManager idm;
	private IIdentity userIdentity;
	private IUserFeedback userFeedback;

	private ClientGUIFrame clientGUI;

	private IRfidServer rfidServerRemote;
	private ICtxBroker ctxBroker;
	private IEventMgr evMgr;
	private Hashtable<String, CtxAttribute> information = new Hashtable<String, CtxAttribute>();
	private String lastKnownSymLoc;
	private CtxEntity ctxEntity;
	private String myCtxSourceId;



	public void initialiseRFIDClient() {
		if(logging.isDebugEnabled()) logging.debug("Init RFID CLIENT");
		this.registerForRfidWebEvents();
		if(logging.isDebugEnabled()) logging.debug("REGISTERED FOR WEB EVENTS");
		this.registerWithContextSourceManager();
		if(logging.isDebugEnabled()) logging.debug("REGISTERED WITH CONTEXT SOURCE MANAGER");
		try {
			if(logging.isDebugEnabled()) logging.debug("CHECKING FOR INFO IN DB");
			//first try to see if there is information in the DB.
			List<CtxIdentifier> entities = this.ctxBroker.lookup(userIdentity, CtxModelType.ENTITY, RFID_INFO).get();

			boolean haveAllInfo = true;
			if (entities.size()>0){
				String rfidServer = "";
				String rfidTag = "";
				String password = "";
				//	boolean rfidRegistered = false;

				CtxIdentifier entityId = entities.get(0);
				CtxEntity entity = (CtxEntity) this.ctxBroker.retrieve(entityId).get();


				Set<CtxAttribute> passwords = entity.getAttributes(RFID_PASSWORD);

				Iterator<CtxAttribute> iterator = passwords.iterator();

				if (iterator.hasNext()){
					CtxAttribute attribute = iterator.next();
					password = attribute.getStringValue();
					this.information.put(RFID_PASSWORD, attribute);

				}else{
					haveAllInfo = false;
				}

				Set<CtxAttribute> rfidServers = entity.getAttributes(RFID_SERVER);

				iterator = rfidServers.iterator();

				if (iterator.hasNext()){
					CtxAttribute attribute = iterator.next();
					this.information.put(RFID_SERVER, attribute);
					rfidServer = attribute.getStringValue();
				}else{
					haveAllInfo=  false;
				}
				Set<CtxAttribute> rfidTags = entity.getAttributes(RFID_TAG);

				iterator = rfidTags.iterator();

				if (iterator.hasNext()){
					CtxAttribute attribute = iterator.next();
					this.information.put(RFID_TAG, attribute);
					rfidTag = attribute.getStringValue();
				}else{
					haveAllInfo = false;
				}

				Set<CtxAttribute> rfidRegistration = entity.getAttributes(RFID_REGISTERED);

				iterator = rfidRegistration.iterator();

				if (iterator.hasNext()){
					CtxAttribute attribute = iterator.next();
					this.information.put(RFID_REGISTERED, attribute);
					//	rfidRegistered = attribute.getStringValue().equalsIgnoreCase("true");
				}else{
					haveAllInfo = false;
				}

				//	this.updateContext(RFID_REGISTERED, "false");
				//	this.updateContext(RFID_REGISTRATION_ERROR, "");
				//	this.logging.debug("set "+RFID_REGISTERED+" to false");

				//TODO: IF i HAVE ALL THE INFO, REGISTER WITH RFID SERVER

				if (haveAllInfo){
					this.rfidServerRemote.registerRFIDTag(rfidServer,rfidTag, userIdentity.getBareJid(), null, password);
					if(logging.isDebugEnabled()) logging.debug("sent registerRFIDTag message");
				}			

			}

		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

	private void registerForRfidWebEvents(){
		if(logging.isDebugEnabled()) logging.debug("REGISTERED FOR WEB EVENTS");
		this.getEvMgr().subscribeInternalEvent(this, new String[]{RFID_EVENT_TYPE}, null);
	}

	//NOTIFIES USER THAT THEY ARE UNREGISTERD WHEN DELETING REGISTERED TAG ON SERVER
	public void notifyUser(String tag)
	{
		userFeedback.showNotification("You have been unregistered from the RFID tag: " + tag);
	}


	private void registerWithContextSourceManager(){
		try {

			myCtxSourceId = this.ctxSourceMgr.register(CtxSourceNames.RFID, CtxAttributeTypes.LOCATION_SYMBOLIC).get();

			if (this.ctxEntity==null){
				List<CtxIdentifier> list = this.ctxBroker.lookup(userIdentity, CtxModelType.ENTITY, RFID_INFO).get();
				if (list.size()==0){
					ctxEntity = this.ctxBroker.createEntity(RFID_INFO).get();
				}
				else{
					ctxEntity = (CtxEntity) this.ctxBroker.retrieve(list.get(0)).get();
				}

			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	//DELETES (UNREGISTERS USER) FROM DB - WHEN TAG IS DELETED ON SERVER
	@Override
	public void deleteContext(String serverJid, String tag)
	{
		try{
			List<CtxIdentifier> list = this.ctxBroker.lookup(userIdentity, CtxModelType.ENTITY, RFID_INFO).get();
			this.ctxBroker.remove(list.get(0)).get();
			this.ctxEntity = this.ctxBroker.createEntity(RFID_INFO).get();
			if(serverJid!=null)
			{
				this.rfidServerRemote.ackDeleteTag(serverJid, tag);
			}
			this.information.clear();//CLEAR INFO
		}catch(Exception e) {}

	}

	//DELETES (UNREGISTERS USER) FROM DB - WHEN USER UNREGISTERS
	public void deleteContext()
	{
		try{
			List<CtxIdentifier> list = this.ctxBroker.lookup(userIdentity, CtxModelType.ENTITY, RFID_INFO).get();
			this.ctxBroker.remove(list.get(0)).get();
			this.ctxEntity = this.ctxBroker.createEntity(RFID_INFO).get();
		}catch(Exception e) {}
		this.information.clear();//CLEAR INFO


	}


	private void updateContext(String type, String value){

		try {
		//	if (this.ctxEntity==null){

			List<CtxIdentifier> list = this.ctxBroker.lookup(userIdentity, CtxModelType.ENTITY, RFID_INFO).get();
			//	List<CtxIdentifier> list = this.ctxBroker.lookup(CtxModelType.ENTITY, RFID_INFO).get();
				if (list.size()==0){
					this.ctxEntity = this.ctxBroker.createEntity(RFID_INFO).get();
				}else{
					this.ctxEntity = (CtxEntity) this.ctxBroker.retrieve(list.get(0)).get();
				}

		//	}

			if (this.ctxEntity!=null){
				Set<CtxAttribute> attributes = this.ctxEntity.getAttributes(type);
				Iterator<CtxAttribute> iterator = attributes.iterator();
				
				if (iterator.hasNext()){
					CtxAttribute attribute = iterator.next();
					attribute.setStringValue(value);
					attribute = (CtxAttribute) this.ctxBroker.update(attribute).get();
					this.information.put(type, attribute);
				}else{
					CtxAttribute ctxAttribute = this.ctxBroker.createAttribute(ctxEntity.getId(), type).get();
					ctxAttribute.setStringValue(value);
					ctxAttribute = (CtxAttribute) this.ctxBroker.update(ctxAttribute).get();
					this.information.put(type, ctxAttribute);
				}
			}else{
				if(logging.isDebugEnabled()) logging.error("Entity: "+RFID_INFO+" could not be retrieved/created");
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void sendUpdate(String symLoc, String tagNumber) {


		if (this.myCtxSourceId==null){
			this.registerWithContextSourceManager();
		}
		if (this.myCtxSourceId==null){
			this.updateContext(RFID_REGISTRATION_ERROR, "RFID location received from RFID server but an error occured while registering as a context source with Context Source Manager");
			if(logging.isDebugEnabled()) logging.debug("RFID_REGISTRATION_ERROR: Context Source Manager error ");
			return ;
		}
		try {
			this.ctxSourceMgr.sendUpdate(myCtxSourceId, symLoc, null, false, 1.0, 1d/5).get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.updateContext(RFID_LAST_LOCATION, symLoc);
		this.lastKnownSymLoc = symLoc;

	}





	@Override
	public void handleInternalEvent(InternalEvent event) {
		if(logging.isDebugEnabled()) logging.debug("Received event - type: "+event.geteventType()+" event source "+event.geteventSource()+" event name: "+event.geteventName());
		if (event.geteventType().equals(RFID_EVENT_TYPE) && (event.geteventName().equalsIgnoreCase("registerRequest"))){
			if(logging.isDebugEnabled()) logging.debug("GOT A REGISTER REQUEST!");
			Hashtable<String, String> hash = (Hashtable<String, String>) event.geteventInfo();
			if (hash!=null){
				//IF REGISTERING, REMOVE ANY CONTEXT WHICH EXISTS?
				deleteContext();
				//	String action = hash.get("action");
				String rfidTag = hash.get("rfidTag");
				this.updateContext(RFID_TAG, rfidTag);
				if(logging.isDebugEnabled()) logging.debug("Stored RFID_TAG");
				String password = hash.get("password");
				this.updateContext(RFID_PASSWORD, password);
				if(logging.isDebugEnabled()) logging.debug("Stored RFID_PASSWORD");
				String serverJid = hash.get("serverJid");
				this.updateContext(RFID_SERVER, serverJid);
				if(logging.isDebugEnabled()) logging.debug("Stored RFID_SERVER");
				this.rfidServerRemote.registerRFIDTag(serverJid, rfidTag, this.userIdentity.getJid(), "", password);
				if(logging.isDebugEnabled()) logging.debug("Requested RFID tag registration");

			}
		}
		else if (event.geteventType().equals(RFID_EVENT_TYPE) && (event.geteventName().equalsIgnoreCase("unregisterRequest"))){
			Hashtable<String, String> hash = (Hashtable<String, String>) event.geteventInfo();
			deleteContext();
			if (hash!=null){
				String action = hash.get("action");
				String rfidTag = hash.get("rfidTag");
				//this.updateContext(RFID_TAG, null);
				if(logging.isDebugEnabled()) logging.debug("Removed RFID_TAG");
				String password = hash.get("password");
				//	this.updateContext(RFID_PASSWORD, null);
				if(logging.isDebugEnabled()) logging.debug("Removed RFID_PASSWORD");
				String serverJid = hash.get("serverJid");
				//	this.updateContext(RFID_SERVER, null);
				if(logging.isDebugEnabled()) logging.debug("Removed RFID_SERVER");
				this.rfidServerRemote.unregisterRFIDTag(serverJid, rfidTag, this.userIdentity.getJid(), "", password);
				if(logging.isDebugEnabled()) logging.debug("Requested RFID tag unregistration");

				//this.rfidServerRemote.unregisterRFIDTag(serverJid, rfidTag, this.userIdentity.getJid(), "", password);

			}
		}
	}

	@Override
	public void handleExternalEvent(CSSEvent event) {
		// TODO Auto-generated method stub

	}


	@Override
	public void acknowledgeRegistration(Integer rStatus) {
		String rfidtag;
		switch (rStatus){
		case 0 : 
			this.updateContext(RFID_REGISTERED, "true");

			rfidtag = this.information.get(RFID_TAG).getStringValue();
			if(logging.isDebugEnabled()) logging.debug("Successfully registered tag: "+rfidtag);

			break;
		case 1 :
			this.updateContext(RFID_REGISTERED, "false");
			this.updateContext(RFID_REGISTRATION_ERROR, "The password for registering your RFID tag number was incorrect. Please enter your password again.");
			if(logging.isDebugEnabled()) logging.debug("RFID_REGISTRATION_ERROR: Incorrect password");

			break;
		case 2 :
			this.updateContext(RFID_REGISTERED, "false");
			this.updateContext(RFID_REGISTRATION_ERROR, "The RFID tag number was not recognised. Please enter a valid RFID tag number. ");
			if(logging.isDebugEnabled()) logging.debug("RFID_REGISTRATION_ERROR: Unrecognised rfid tag number");

			break;
		case 3 :
			//	this.updateContext(RFID_REGISTERED, "false");
			rfidtag = this.information.get(RFID_TAG).getStringValue();
			if(logging.isDebugEnabled()) logging.debug("Successfully unregistered tag: "+rfidtag);

			break;
		default: 
			this.updateContext(RFID_REGISTERED, "false");
			this.updateContext(RFID_REGISTRATION_ERROR, "An unknown error occured");
			if(logging.isDebugEnabled()) logging.debug("RFID_REGISTRATION_ERROR: Unknown error");
			break;
		}

		publishEvent();


	}


	private void publishEvent(){
		InternalEvent event = new InternalEvent(RFID_EVENT_TYPE, "registrationResult", this.getClass().getName(), information);
		try {
			this.evMgr.publishInternalEvent(event);
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the commManager
	 */
	public ICommManager getCommManager() {
		return commManager;
	}

	/**
	 * @param commManager the commManager to set
	 */
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		this.idm = this.commManager.getIdManager();
		this.userIdentity = this.idm.getThisNetworkNode();
	}

	/**
	 * @return the ctxSourceMgr
	 */
	public ICtxSourceMgr getCtxSourceMgr() {
		return ctxSourceMgr;
	}

	/**
	 * @param ctxSourceMgr the ctxSourceMgr to set
	 */
	public void setCtxSourceMgr(ICtxSourceMgr ctxSourceMgr) {
		this.ctxSourceMgr = ctxSourceMgr;
	}

	/**
	 * @return the rfidServer
	 */
	public IRfidServer getRfidServerRemote() {
		return rfidServerRemote;
	}

	/**
	 * @param rfidServer the rfidServer to set
	 */
	public void setRfidServerRemote(IRfidServer rfidServer) {
		this.rfidServerRemote = rfidServer;
	}

	/**
	 * @return the userFeedback
	 */
	public IUserFeedback getUserFeedback() {
		return userFeedback;
	}

	/**
	 * @param userFeedback the userFeedback to set
	 */
	public void setUserFeedback(IUserFeedback userFeedback) {
		this.userFeedback = userFeedback;
	}

	/**
	 * @return the ctxBroker
	 */
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	/**
	 * @param ctxBroker the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}


	public IEventMgr getEvMgr() {
		return evMgr;
	}




	public void setEvMgr(IEventMgr evMgr) {
		this.evMgr = evMgr;
	}

} 




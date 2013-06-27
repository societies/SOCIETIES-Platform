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
import org.societies.api.osgi.event.CSSEvent;
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

	private static final String RFID_REGISTRATION_ERROR = "RFID_REGISTRATION_ERROR";
	private static final String RFID_INFO = "RFID_INFO";
	private final static String CTX_SOURCE_ID = "CTX_SOURCE_ID";
	private final static String RFID_TAG = "RFID_TAG";
	private final static String RFID_PASSWORD = "RFID_PASSWORD";
	private final static String RFID_SERVER = "RFID_SERVER";
	private final static String RFID_REGISTERED = "RFID_REGISTERED";
	private final static String RFID_LAST_LOCATION = "RFID_LAST_LOCATION";
	
	
	private ICommManager commManager;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private ICtxSourceMgr ctxSourceMgr;
	private IIdentityManager idm;
	private IIdentity userIdentity;

	private ClientGUIFrame clientGUI;

	private IRfidServer rfidServerRemote;
	private ICtxBroker ctxBroker;
	private IEventMgr evMgr;
	private Hashtable<String, CtxAttribute> information = new Hashtable<String, CtxAttribute>();
	private String lastKnownSymLoc;
	private CtxEntity ctxEntity;
	private String myCtxSourceId;



	public void initialiseRFIDClient() {
		this.registerForRfidWebEvents();
		this.registerWithContextSourceManager();
		try {
            //first try to see if there is information in the DB.
			List<CtxIdentifier> entities = this.ctxBroker.lookup(CtxModelType.ENTITY, RFID_INFO).get();

            boolean haveAllInfo = true;
			if (entities.size()>0){
                String rfidServer = "";
                String rfidTag = "";
                String password = "";

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

				
				//TODO: IF i HAVE ALL THE INFO, REGISTER WITH RFID SERVER

                if (haveAllInfo){

                    this.rfidServerRemote.registerRFIDTag(rfidServer,rfidTag, userIdentity.getBareJid(), null, password);
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
		this.getEvMgr().subscribeInternalEvent(this, new String[]{"ac/hw/rfid"}, null);
	}



	private void registerWithContextSourceManager(){
		try {

			myCtxSourceId = this.ctxSourceMgr.register(CtxSourceNames.RFID, CtxAttributeTypes.LOCATION_SYMBOLIC).get();

			if (this.ctxEntity==null){
				List<CtxIdentifier> list = this.ctxBroker.lookup(CtxModelType.ENTITY, RFID_INFO).get();
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

	private void updateContext(String type, String value){
		try {
			if (this.ctxEntity==null){

				List<CtxIdentifier> list = this.ctxBroker.lookup(CtxModelType.ENTITY, RFID_INFO).get();
				if (list.size()==0){
					this.ctxEntity = this.ctxBroker.createEntity(RFID_INFO).get();
				}else{
					this.ctxEntity = (CtxEntity) this.ctxBroker.retrieve(list.get(0)).get();
				}

			}

			if (this.ctxEntity!=null){
				Set<CtxAttribute> attributes = this.ctxEntity.getAttributes(type);

				Iterator<CtxAttribute> iterator = attributes.iterator();

				if (iterator.hasNext()){
					CtxAttribute attribute = iterator.next();
					attribute.setStringValue(value);
					this.ctxBroker.update(attribute).get();
				}else{
					CtxAttribute ctxAttribute = this.ctxBroker.createAttribute(ctxEntity.getId(), type).get();
					ctxAttribute.setStringValue(value);
					this.ctxBroker.update(ctxAttribute).get();
				}
			}else{
				this.logging.error("Entity: "+RFID_INFO+" could not be retrieved/created");
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
			this.logging.debug("RFID_REGISTRATION_ERROR: Context Source Manager error ");
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






	public void handleInternalEvent(InternalEvent event) {
		this.logging.debug("Received event - type: "+event.geteventType()+" event source "+event.geteventSource()+" event name: "+event.geteventName());
		if (event.geteventType().equals("ac/hw/rfid")){
			Hashtable<String, String> hash = (Hashtable<String, String>) event.geteventInfo();
			if (hash!=null){
				String action = hash.get("action");
				String rfidTag = hash.get("rfidTag");
				this.updateContext(RFID_TAG, rfidTag);
				this.logging.debug("Stored RFID_TAG");
				String password = hash.get("password");
				this.updateContext(RFID_PASSWORD, password);
				this.logging.debug("Stored RFID_PASSWORD");
				String serverJid = hash.get("serverJid");
				this.updateContext(RFID_SERVER, serverJid);
				this.logging.debug("Stored RFID_SERVER");
				this.rfidServerRemote.registerRFIDTag(serverJid, rfidTag, this.userIdentity.getJid(), "", password);
				this.logging.debug("Requested RFID tag registration");

			}
		}
	}

	@Override
	public void handleExternalEvent(CSSEvent event) {
		// TODO Auto-generated method stub

	}


	@Override
	public void acknowledgeRegistration(Integer rStatus) {
		switch (rStatus){
		case 0 : 
			this.updateContext(RFID_REGISTERED, "true");
			
			String rfidtag = this.information.get(RFID_TAG).getStringValue();
			this.logging.debug("Successfully registered tag: "+rfidtag);
			break;
		case 1 :
			this.updateContext(RFID_REGISTRATION_ERROR, "The password for registering your RFID tag number was incorrect. Please enter your password again.");
			this.logging.debug("RFID_REGISTRATION_ERROR: Incorrect password");
			break;
		case 2 :
			this.updateContext(RFID_REGISTRATION_ERROR, "The RFID tag number was not recognised. Please enter a valid RFID tag number. ");
			this.logging.debug("RFID_REGISTRATION_ERROR: Unrecognised rfid tag number");
			break;
		default: this.updateContext(RFID_REGISTRATION_ERROR, "An unknown error occured");
		this.logging.debug("RFID_REGISTRATION_ERROR: Unknown error");
		break;

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


	/*
	private void registerForSLMEvents() {
		String eventFilter = "(&" + 
				"(" + CSSEventConstants.EVENT_NAME + "="+ServiceMgmtEventType.NEW_SERVICE+")" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/servicelifecycle)" +
				")";
		this.getEvMgr().subscribeInternalEvent(this, new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT}, eventFilter);
		this.logging.debug("Subscribed to "+EventTypes.SERVICE_LIFECYCLE_EVENT+" events");

	}


	private void unRegisterFromSLMEvents()
	{
		String eventFilter = "(&" + 
				"(" + CSSEventConstants.EVENT_NAME + "="+ServiceMgmtEventType.NEW_SERVICE+")" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/servicelifecycle)" +
				")";

		this.evMgr.unSubscribeInternalEvent(this, new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT}, eventFilter);
		//this.evMgr.subscribeInternalEvent(this, new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT}, eventFilter);
		this.logging.debug("Unsubscribed from "+EventTypes.SERVICE_LIFECYCLE_EVENT+" events");
	} */

	/*	@Override
	public void handleInternalEvent(InternalEvent event) {
		if (event.geteventType().equals(ServiceMgmtEventType.SERVICE_STARTED)){
		ServiceMgmtEvent slmEvent = (ServiceMgmtEvent) event.geteventInfo();

		if (slmEvent.getBundleSymbolName().equalsIgnoreCase("ac.hw.rfid.RFIDClientApp")){
			this.logging.debug("Received SLM event for my bundle");

			if (slmEvent.getEventType().equals(ServiceMgmtEventType.NEW_SERVICE)){
				ServiceResourceIdentifier myClientServiceID = slmEvent.getServiceId();
				this.serverIdentity = this.services.getServer(myClientServiceID);
				this.logging.debug("Retrieved my server's identity: "+this.serverIdentity.getJid());
				//this.requestServerIdentityFromUser();
				//ServiceResourceIdentifier serviceId = this.portalServerRemote.getServerServiceId(serverIdentity);

				//UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());

				ServiceResourceIdentifier serviceId = this.getServices().getServerServiceIdentifier(myClientServiceID);
				this.logging.debug("Retrieved my server's serviceID: "+serviceId.getIdentifier());
				this.requestor = new RequestorService(serverIdentity, serviceId);

				boolean registered = this.register();
				if (registered){
					UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
					clientGUI = new ClientGUIFrame(this.rfidServerRemote, this.getCtxBroker(), this.userIdentity, this.serverIdentity, serviceId);
					this.logging.debug("Started client");
				}else{
					this.logging.debug("unable to register as a context source with the ICtxSourceMgr");
				}

				this.unRegisterFromSLMEvents();

			}
		}else{
			this.logging.debug("Received SLM event but it wasn't related to my bundle");
		}

	}


		private void updateContextDirectly(String value){

		try {
			Future<IndividualCtxEntity> retrieveIndividualEntity = this.ctxBroker.retrieveIndividualEntity(userIdentity);
			CtxEntity person = retrieveIndividualEntity.get();
			if (person!=null){
				Future<List<CtxIdentifier>> lookup = this.ctxBroker.lookup(person.getId(), CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC);
				List<CtxIdentifier> attributes = lookup.get();

				if (attributes.size()==0){
					this.logging.debug("no symbolic location attributes found");
				}else{
					for (int i=0; i<attributes.size(); i++){
						CtxAttribute attr = this.ctxBroker.updateAttribute((CtxAttributeIdentifier) attributes.get(i), value).get();
						this.logging.debug("Updating attribute: "+attr.getId().toUriString()+" with value: "+attr.getStringValue());
					}
				}
			}else{
				this.logging.debug("Entity Person is null");
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
		} catch (Exception e){
			e.printStackTrace();
			this.logging.error(e.getMessage());
		}
	}


		@Override
	public void acknowledgeRegistration(Integer rStatus) {

		while (this.clientGUI==null){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.clientGUI.acknowledgeRegistration(rStatus);
		this.logging.debug("Received acknowledgement for registration");

	}
	 *
	 */

} 




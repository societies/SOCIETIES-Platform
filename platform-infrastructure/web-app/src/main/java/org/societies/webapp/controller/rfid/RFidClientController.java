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

package org.societies.webapp.controller.rfid;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.primefaces.context.RequestContext;
import org.primefaces.util.Constants;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.service.UserService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.swing.JOptionPane;

@ManagedBean(name = "rfidClientController")
@ViewScoped
public class RFidClientController extends BasePageController{


	private static final String RFID_INFO = "RFID_INFO";	
	private final static String CTX_SOURCE_ID = "CTX_SOURCE_ID";
	private final static String RFID_TAG = "RFID_TAG";
	private final static String RFID_PASSWORD = "RFID_PASSWORD";
	private final static String RFID_SERVER = "RFID_SERVER";
	private final static String RFID_REGISTERED = "RFID_REGISTERED";
	private final static String RFID_LAST_LOCATION = "RFID_LAST_LOCATION";
	private static final String RFID_REGISTRATION_ERROR = "RFID_REGISTRATION_ERROR";
	private static final String RFID_EVENT_TYPE = "org/societies/rfid";


	@ManagedProperty(value = "#{userService}")
	private UserService userService; // NB: MUST include public getter/setter

	@ManagedProperty(value= "#{eventManager}")
	private IEventMgr eventManager; 

	@ManagedProperty(value="#{internalCtxBroker}")
	private ICtxBroker ctxBroker;

	@ManagedProperty(value="#{commMngrRef}")
	private ICommManager commManager;
	private IIdentity registeredId;
	private String lastRecordedLocation = "";
	private String myRfidTag;
	private IIdentity serverJid;
	private boolean registered = false;
	private String mypasswd;
	private IIdentityManager idManager;
	private RfidEventListener rfidEventListener;
	private String regError = "";
	
	private boolean isDone = false;

	public RFidClientController() {
	}

	@PreDestroy
	public void destroyEventListener(){
		if (this.rfidEventListener!=null){

			this.rfidEventListener.unsubscribe();

		}
	}
	
	

	@PostConstruct
	public void initController(){
		rfidEventListener = new RfidEventListener(this, eventManager);
		this.retrieveRfidInfo();
	}

	public void retrieveRfidInfo(){

		if(log.isDebugEnabled()) log.debug("Retrieving RFID information from context");
		try {
			List<CtxIdentifier> list = this.ctxBroker.lookup(CtxModelType.ENTITY, RFID_INFO).get();
			log.debug("SIZE OF LIST - " + list.size());
			if (list.size()>0){
				CtxEntity ctxEntity = (CtxEntity) this.ctxBroker.retrieve(list.get(0)).get();

				Set<CtxAttribute> attributes = ctxEntity.getAttributes(RFID_TAG);
				Iterator<CtxAttribute> iterator = attributes.iterator();
				if (iterator.hasNext()){
					CtxAttribute attribute = iterator.next();
					this.myRfidTag = attribute.getStringValue();
				}

				attributes = ctxEntity.getAttributes(RFID_SERVER);
				iterator = attributes.iterator();
				if (iterator.hasNext()){
					CtxAttribute attribute = iterator.next();
					this.serverJid = this.idManager.fromJid(attribute.getStringValue());
				}


				attributes = ctxEntity.getAttributes(RFID_LAST_LOCATION);
				iterator = attributes.iterator();
				if (iterator.hasNext()){
					CtxAttribute attribute = iterator.next();
					this.lastRecordedLocation = attribute.getStringValue();
				}

				attributes  = ctxEntity.getAttributes(RFID_PASSWORD);
				iterator = attributes.iterator();
				if (iterator.hasNext()){
					CtxAttribute attribute = iterator.next();
					this.mypasswd = attribute.getStringValue();
				}

				attributes  = ctxEntity.getAttributes(RFID_REGISTRATION_ERROR);
				iterator = attributes.iterator();
				if (iterator.hasNext()){
					CtxAttribute attribute = iterator.next();
					this.regError = attribute.getStringValue();
				}
				
				//DO THIS LAST
				attributes = ctxEntity.getAttributes(RFID_REGISTERED);
				iterator = attributes.iterator();
				if (iterator.hasNext()){
					CtxAttribute attribute = iterator.next();
					this.registered = attribute.getStringValue().trim().equalsIgnoreCase("true");
					if(log.isDebugEnabled()) log.debug("Retrieved "+RFID_REGISTERED+" attribute, value: "+attribute.getStringValue()+" this.registered: "+this.registered);
					//IF WEBAPP IS WAITING FOR A RESPONSE, SEND IT
					setIsDone(true);
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
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	
	
	public void register()  {
		if(log.isDebugEnabled()) log.debug("Register button clicked");

		if(this.myRfidTag!=null && !this.mypasswd.isEmpty() && !this.serverJid.getJid().isEmpty())
		{
			Hashtable<String, String> hash = new Hashtable<String, String>();

			hash.put("action", "register");
			hash.put("rfidTag", this.myRfidTag);
			hash.put("password", this.mypasswd);
			hash.put("serverJid", this.serverJid.getJid());


			InternalEvent event = new InternalEvent("org/societies/rfid", "registerRequest", this.getClass().getName(), hash);
			try {
				this.eventManager.publishInternalEvent(event);
				if(log.isDebugEnabled()) log.debug("Published registration event");
				//setRegisterStatus(true);

			} catch (EMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int count = 0;
			while(!this.isDone && count<10)
			{
				try {
					log.debug("Thread is sleeping");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				count++;
				//DO NOTHING...WAIT MAX 10 SECONDS
			}
			
			//return to false
			setIsDone(false);
			
			//If error exists, show it
			if(!this.regError.isEmpty())
			{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Error", this.regError));  
			}
			//Reset error to empty
			this.regError="";
			
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Error", "Please enter a value for all fields!"));
		}

	}

	//SYNC METHODS TO ALERT WEBAPP TO REFRESH
		public synchronized void setIsDone(boolean isDone){
		   this.isDone = isDone;
		}


	
	public void unregister(){
		//this.registerStatus = false;
		setRegistered(false);
		//setRegisterStatus(false);
		if(log.isDebugEnabled()) log.debug("Unregister button clicked");

		Hashtable<String, String> hash = new Hashtable<String, String>();

		hash.put("action", "unregister");
		hash.put("rfidTag", this.myRfidTag);
		hash.put("password", this.mypasswd);
		hash.put("serverJid", this.serverJid.getJid());
	
		InternalEvent event = new InternalEvent("org/societies/rfid", "unregisterRequest", this.getClass().getName(), hash);
		try {
			this.eventManager.publishInternalEvent(event);
			if(log.isDebugEnabled()) log.debug("Published unregistration event");
		//	setRegisterStatus(false);

		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//setRegistered(false);

	}


	

	
	public boolean isRegistered() {
		return registered;
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public String getMypasswd() {
		if (mypasswd==null){
			return "";
		}
		return mypasswd;
	}

	public void setMypasswd(String mypasswd) {
		this.mypasswd = mypasswd;
	}

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		idManager = commManager.getIdManager();
		this.commManager = commManager;
		this.registeredId = idManager.getThisNetworkNode();
	}

	public IEventMgr getEventManager() {
		return eventManager;
	}

	public void setEventManager(IEventMgr eventManager) {
		this.eventManager = eventManager;
	}

/*	public boolean isRegisterStatus() {
		return registerStatus;
	}

	public void setRegisterStatus(boolean registerStatus) {
		this.registerStatus = registerStatus;
	}*/
	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public IIdentity getRegisteredId() {
		return registeredId;
	}

	public void setRegisteredId(IIdentity registeredId) {
		this.registeredId = registeredId;
	}

	public String getLastRecordedLocation() {
		if (lastRecordedLocation == null){
			return "unknown";
		}
		return lastRecordedLocation;
	}

	public void setLastRecordedLocation(String lastRecordedLocation) {
		this.lastRecordedLocation = lastRecordedLocation;
	}

	public String getMyRfidTag() {
		if (myRfidTag==null){
			return "";
		}
		return myRfidTag;
	}

	public void setMyRfidTag(String myRfidTag) {
		this.myRfidTag = myRfidTag;
	}

	public String getServerJid() {
		if (serverJid==null){
			return "";
		}
		return serverJid.getJid();
	}

	public void setServerJid(IIdentity serverJid) {
		this.serverJid = serverJid;
	}

	public void setServerJid(String serverJid) {
		try {
			this.serverJid = this.commManager.getIdManager().fromJid(serverJid);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getRegError() {
		return regError;
	}

	public void setRegError(String regError) {
		this.regError = regError;
	}

}

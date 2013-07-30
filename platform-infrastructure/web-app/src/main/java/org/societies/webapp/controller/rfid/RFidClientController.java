package org.societies.webapp.controller.rfid;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.primefaces.context.RequestContext;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
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
	private boolean registerStatus = false;
	private RfidEventListener rfidEventListener;
	private String regError = "";
	
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
		
		this.log.debug("Retrieving RFID information from context");
		try {
			List<CtxIdentifier> list = this.ctxBroker.lookup(CtxModelType.ENTITY, RFID_INFO).get();
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
				
				attributes = ctxEntity.getAttributes(RFID_REGISTERED);
				iterator = attributes.iterator();
				if (iterator.hasNext()){
					CtxAttribute attribute = iterator.next();
					this.registered = attribute.getStringValue().trim().equalsIgnoreCase("true");
					this.log.debug("Retrieved "+RFID_REGISTERED+" attribute, value: "+attribute.getStringValue()+" this.registered: "+this.registered);
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

	
	public void register(){
		this.registerStatus = true;
		this.log.debug("Register button clicked");
		
		Hashtable<String, String> hash = new Hashtable<String, String>();
		
		hash.put("action", "register");
		hash.put("rfidTag", this.myRfidTag);
		hash.put("password", this.mypasswd);
		hash.put("serverJid", this.serverJid.getJid());
		
		
		InternalEvent event = new InternalEvent("org/societies/rfid", "registerRequest", this.getClass().getName(), hash);
		try {
			this.eventManager.publishInternalEvent(event);
			this.log.debug("Published registration event");
			
			
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	public boolean isRegisterStatus() {
		return registerStatus;
	}

	public void setRegisterStatus(boolean registerStatus) {
		this.registerStatus = registerStatus;
	}
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

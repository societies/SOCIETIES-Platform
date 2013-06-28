package org.societies.webapp.controller.rfid;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.service.UserService;

@ManagedBean(name = "rfidServerController")
@ViewScoped
public class RFidServerController extends BasePageController {

	
    @ManagedProperty(value = "#{userService}")
    private UserService userService; // NB: MUST include public getter/setter
    
    @ManagedProperty(value= "#{eventManager}")
    private IEventMgr eventManager; 
    
    @ManagedProperty(value="#{internalCtxBroker}")
    private ICtxBroker ctxBroker;
    
    @ManagedProperty(value="#{commMngrRef}")
    private ICommManager commManager;
    
    private List<RfidBean> rfidBeans;
    private RfidBean selectedRfidBean;
    private RfidBean addRfidBean;

	private IIdentityManager idManager;

    public RFidServerController() {
        // controller constructor - called every time this page is requested!
    	this.setRfidBeans(new ArrayList<RfidBean>());
    	RfidBean example = new RfidBean();
    	example.setPassword("password");
    	example.setRfidTag("rfidNumber");
    	example.setSymLoc("symLoc");
    	example.setUserIdentity("UserId");
    	example.setWakeupUnit("WakeupUnit");
    	getRfidBeans().add(example);
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

	

	public void addTag(){
		
		this.log.debug("Add button clicked");
		
		this.rfidBeans.add(addRfidBean);
		Hashtable<String, String> hash = new Hashtable<String, String>();
		
/*		hash.put("action", "register");
		hash.put("rfidTag", this.myRfidTag);
		hash.put("password", this.mypasswd);
		hash.put("serverJid", this.serverJid.getJid());*/
		if (this.addRfidBean==null){
			this.log.debug("addRfid is null");
		}
		hash.put("tag", this.addRfidBean.getRfidTag());
		if (!(this.addRfidBean.getPassword()==null || this.addRfidBean.getPassword()=="")){
			hash.put("password", this.addRfidBean.getPassword());
		}
		
		InternalEvent event = new InternalEvent("org/societies/rfid/server", "addNewTag", this.getClass().getName(), hash);
		try {
			this.eventManager.publishInternalEvent(event);
			this.log.debug("Published add new tag event");
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void deleteTag(){
		this.log.debug("Delete record");
        Hashtable<String, String> hash = new Hashtable<String, String>();


        hash.put("tag", this.selectedRfidBean.getRfidTag());
        InternalEvent event = new InternalEvent("org/societies/rfid/server", "deleteTag", this.getClass().getName(), hash);
        try {
            this.eventManager.publishInternalEvent(event);
            this.log.debug("Published deletion event");
        } catch (EMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	

	public IEventMgr getEventManager() {
		return eventManager;
	}

	public void setEventManager(IEventMgr eventManager) {
		this.eventManager = eventManager;
	}
	
	

	@PostConstruct
	public void retrieveRfidInfo(){
		
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
	}

	public RfidBean getSelectedRfidBean() {
		this.log.debug("Get selectedRfidBean");
		return selectedRfidBean;
		
	}

	public void setSelectedRfidBean(RfidBean selectedRfidBean) {
		this.selectedRfidBean = selectedRfidBean;
		this.log.debug("Set selectedRfidBean");
	}

	public List<RfidBean> getRfidBeans() {
		return rfidBeans;
	}

	public void setRfidBeans(List<RfidBean> rfidBeans) {
		this.rfidBeans = rfidBeans;
	}

	public RfidBean getAddRfidBean() {
		this.log.debug("Get addRfidBean");
		return addRfidBean;
	}

	public void setAddRfidBean(RfidBean addRfidBean) {
		this.addRfidBean = addRfidBean;
		this.log.debug("Set addRfidBean");
		
	}
}

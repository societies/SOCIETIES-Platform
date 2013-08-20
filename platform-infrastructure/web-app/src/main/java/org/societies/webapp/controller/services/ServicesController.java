package org.societies.webapp.controller.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import org.primefaces.event.FileUploadEvent;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceControlException;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.controller.rfid.RfidEventListener;
import org.societies.webapp.service.UserService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "servicesController")
@SessionScoped
public class ServicesController extends BasePageController {

    @ManagedProperty(value= "#{eventManager}")
    private IEventMgr eventManager; 
    
    
    public IEventMgr getEventManager() {
		return eventManager;
	}

	public void setEventManager(IEventMgr eventManager) {
		this.eventManager = eventManager;
	}


	@ManagedProperty(value = "#{userService}")
    private UserService userService; // NB: MUST include public getter/setter
    
    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    @ManagedProperty(value = "#{CisManagementBundle}")
	private ICisManager cisManager;
    
    public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}


	@ManagedProperty(value = "#{serviceControl}")
    private IServiceControl serviceControl; // NB: MUST include public getter/setter
    
    public IServiceControl getServiceControl() {
        return serviceControl;
    }

    public void setServiceControl(IServiceControl serviceControl) {
        this.serviceControl = serviceControl;
    }
    
 
    @ManagedProperty(value = "#{serviceDiscovery}")
    private IServiceDiscovery serviceDiscovery; // NB: MUST include public getter/setter
    
    public IServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }

    public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }
    
	@ManagedProperty(value="#{commMngrRef}")
	private ICommManager commManager;
	
    
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}


	// PROPERTIES that the Webpage will access
	private IIdentity selectedNode;
	
	public void setSelectedNode(String selectedJid){
		
		try{
			if("mynode".equals(selectedNode)){
				selectedNode = getUserService().getIdentity();
				setHeader("Apps for " + getUserService().getUsername());
			}
			else{
				selectedNode = getCommManager().getIdManager().fromJid(selectedJid);
				setHeader("Apps for " +getCisManager().getCis(selectedJid).getName());
			}
		} catch(Exception ex){
			log.error("Exception converting node!");
			ex.printStackTrace();
			addGlobalMessage("Ooops!","Something went wrong, please retry!",FacesMessage.SEVERITY_ERROR);
		}
		
		
	}
	
	public List<ICis> getNodeList(){
		return getCisManager().getCisList();
	}
	
	public String getSelectedNode(){
		if(selectedNode.equals(getUserService().getIdentity()))
			return "mypersonalnode";
		else
			return selectedNode.getJid();
	}
    
    private Service selectedService;
    private HashMap<String,ServiceWrapper> currentServices;
    
    public Collection<ServiceWrapper> getCurrentServices(){
    	
    	return currentServices.values();
    	
    }
    
    
	public String getServiceId(){
		if(selectedService != null)
			return ServiceModelUtils.serviceResourceIdentifierToString(selectedService.getServiceIdentifier());
		else
			return "none";
	}
	
	public void setServiceId(String serviceId){
		selectedService = currentServices.get(serviceId).getService();
	}
    
    private String header;
    
    public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
	

	private ServiceMgmtListener serviceEventListener;

    public ServicesController() {
        if(log.isDebugEnabled())
        	log.debug("ServiceController created!");
    }

    @PreDestroy
    public void destroyEventListener(){
    	if (this.serviceEventListener!=null){
    		
    		this.serviceEventListener.unsubscribe();
    		
    	}
    }

    @PostConstruct
    public void initController(){
    	serviceEventListener = new ServiceMgmtListener(this, eventManager);
    	setSelectedNode(getUserService().getIdentity().getJid());
    	selectNode();
    }
    
    public void handleFileUpload(FileUploadEvent event) {  
        if(log.isDebugEnabled())
        	log.debug("Upload file was successful: " + event.getFile().getFileName());
        
        try {
			addGlobalMessage("Installing Service...","SOCIETIES is trying to install your service. You will be notified when the process is complete.",FacesMessage.SEVERITY_INFO);
        	// We don't actually wait for OSGI to install the file, as this would clog up the webapp.
        	getServiceControl().installService(event.getFile().getInputstream(), event.getFile().getFileName());

		} catch (Exception e) {
			log.error("Exception occured: {}", e.getMessage());
			e.printStackTrace();
			addGlobalMessage("Problem Installing Service!","SOCIETIES encountered a problem while install your service!",FacesMessage.SEVERITY_ERROR);

		}

    }  

    public void uninstallService(){
    	if(log.isDebugEnabled())
    		log.debug("We are trying to uninstall a service: {}",selectedService.getServiceName());
        
        try {
        	// We don't actually wait for OSGI to install the file, as this would clog up the webapp.
			addGlobalMessage("Uninstalling Service...","SOCIETIES is uninstalling " + selectedService.getServiceName() +". You will be notified when the process is complete.",FacesMessage.SEVERITY_INFO);
			getServiceControl().uninstallService(selectedService.getServiceIdentifier());
			
        } catch (ServiceControlException e) {
			log.error("Exception occured: {}", e.getMessage());
			e.printStackTrace();
	
        }//	addGlobalMessage("Problem Removing Service!","SOCIETIES encountered a problem while trying to remove " + selectedService.getServiceName(),FacesMessage.SEVERITY_ERROR);
    }
    
    public void startService(){
    	if(log.isDebugEnabled())
    		log.debug("Starting Service: {} ", selectedService.getServiceName());

    	try {
			addGlobalMessage("Service Started!","The service " + selectedService.getServiceName() + " was started!",FacesMessage.SEVERITY_INFO);
			Future<ServiceControlResult> resultAync = getServiceControl().startService(selectedService.getServiceIdentifier());
		} catch (ServiceControlException e) {
			log.error("Exception while attempting to start service: {}",e.getMessage());
			e.printStackTrace();
			addGlobalMessage("Problem starting Service!","There was a problem starting your service!",FacesMessage.SEVERITY_ERROR);
		}
    }
    
    public String viewServiceDetails(){
    	if(log.isDebugEnabled())
    		log.debug("View service details!");
    	return null;
    }
    
    public void stopService(){
    	if(log.isDebugEnabled())
    		log.debug("Stopping service: {}", selectedService.getServiceName());
    	
    }
    
    public void installService(){
    	if(log.isDebugEnabled())
    		log.debug("Installing service: {}", selectedService.getServiceName());
    	
    }
    
    public void removeService(){
    	if(log.isDebugEnabled())
    		log.debug("Remove service: {}",selectedService.getServiceName());
    }
    
    public void selectNode(){
    	if(log.isDebugEnabled())
    		log.debug("Selecting one node to view the services:" + selectedNode.getIdentifier());
    	
    	
    	List<Service> serviceList;
    	try{
    	 Future<List<Service>> serviceListAsync = getServiceDiscovery().getServices(selectedNode);
    	 serviceList = serviceListAsync.get();
    	} catch(Exception ex){
    		log.error("Exception occurred while retrieving services:" + ex.getMessage());
    		ex.printStackTrace();
    		serviceList = new ArrayList<Service>();
    	}
    	
    	if(!serviceList.isEmpty())
    		currentServices = new HashMap<String,ServiceWrapper>(serviceList.size()*2);
    	else
    		currentServices = new HashMap<String,ServiceWrapper>();
    	
    	for(Service service : serviceList){
    		currentServices.put(ServiceModelUtils.serviceResourceIdentifierToString(service.getServiceIdentifier()), new ServiceWrapper(service,this));
    	}
    	
    }


}

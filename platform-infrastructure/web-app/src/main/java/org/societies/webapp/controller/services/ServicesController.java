package org.societies.webapp.controller.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceControlException;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.model.ServiceType;
import org.societies.api.schema.servicelifecycle.servicecontrol.ResultMessage;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.service.UserService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@ManagedBean(name = "servicesController")
@SessionScoped
public class ServicesController extends BasePageController {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    @ManagedProperty(value= "#{cssManager}")
    private ICSSInternalManager cssManager; 
    
    
    public ICSSInternalManager getCssManager() {
		return cssManager;
	}

	public void setCssManager(ICSSInternalManager cssManager) {
		this.cssManager = cssManager;
	}
	
	@ManagedProperty(value = "#{userService}")
    private UserService userService; // NB: MUST include public getter/setter
    
    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
	@ManagedProperty(value = "#{serviceEventListener}")
	private ServiceEventListener serviceEventListener;
	
	
	public ServiceEventListener getServiceEventListener() {
		return serviceEventListener;
	}

	public void setServiceEventListener(ServiceEventListener serviceEventListener) {
		this.serviceEventListener = serviceEventListener;
	}
	
    @ManagedProperty(value = "#{cisManager}")
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
			if("mynode".equals(selectedJid)){
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
		try{
			return getCisManager().getCisList();
			
		} catch(Exception ex){
			log.error("Error!");
			ex.printStackTrace();
			return new ArrayList<ICis>();
		}
	}
	
	public String getSelectedNode(){
		if(selectedNode.equals(getUserService().getIdentity()))
			return "mynode";
		else
			return selectedNode.getJid();
	}
    
    private Service selectedService;
    
    public ServiceWrapper getSelectedService(){
    	if(selectedService == null)
    		return null;
    	
    	ServiceWrapper serviceWrap = currentServices.get(ServiceModelUtils.serviceResourceIdentifierToString(selectedService.getServiceIdentifier()));
    	if(serviceWrap == null)
    		serviceWrap = new ServiceWrapper(selectedService, this);
    	return serviceWrap;
    }
    
    
    private HashMap<String,ServiceWrapper> currentServices;
    
    public ServiceWrapper[] getCurrentServices(){
    	
    	processMessageCache();
    	return currentServices.values().toArray(new ServiceWrapper[currentServices.size()]);
    	
    }
    
    private HashMap<String,String> thirdClients;
    
    public HashMap<String,String> getThirdClients(){
    	
    	return thirdClients;
    	
    }
    
     
	public synchronized String getServiceId(){
		if(selectedService != null)
			return ServiceModelUtils.serviceResourceIdentifierToString(selectedService.getServiceIdentifier());
		else
			return null;
	}
	
	public synchronized void setServiceId(String serviceId){
		if(log.isDebugEnabled())
			log.debug("serviceId: " + serviceId);
		if(serviceId == null){
			selectedService = null;
			return;
		}
		selectedService = currentServices.get(serviceId).getService();
	}
    
	private String visibleServices;
	
    public String getVisibleServices() {
		return visibleServices;
	}

	public void setVisibleServices(String visibleServices) {
		this.visibleServices = visibleServices;
	}
	
    public void onTabChange(TabChangeEvent event) {
        String activeIndex = ((AccordionPanel) event.getComponent()).getActiveIndex();
        setVisibleServices(activeIndex);
    }
	
    private String header;
    
    public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
	
	private String searchBy;
	public String getSearchBy() {
		return searchBy;
	}

	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}

	private String searchOption;
	
	public String getSearchOption() {
		return searchOption;
	}

	public void setSearchOption(String searchOption) {
		this.searchOption = searchOption;
	}

	private List<String> searchOptions;
	
	public List<String> getSearchOptions() {
		return searchOptions;
	}

	private ConcurrentLinkedQueue<QueuedMessage> messageQueue;

	private boolean didSearch;
	public boolean isDidSearch() {
		return didSearch;
	}

	public void setDidSearch(boolean didSearch) {
		this.didSearch = didSearch;
	}

	private final static String SERVICES_GROWL = "servicesGrowl";
	
    public ServicesController() {
        if(log.isDebugEnabled())
        	log.debug("ServiceController created!");
       
    }

    @PreDestroy
    public void destroyEventListener(){
    	log.debug("destroyEventListener");
    	if (this.serviceEventListener!=null){
    		
    		this.serviceEventListener.unregisterController(this);
    		
    	}
    	
    }

    @PostConstruct
    public void initController(){
    	log.debug("PostConstruct:initController");
    	this.log.info("#CODE2#: Initialising Apps controller");
    	this.thirdClients = new HashMap<String,String>();
    	this.messageQueue = new ConcurrentLinkedQueue<QueuedMessage>();
    	setSelectedNode("mynode");
    	selectNode();
    	searchOptions = new ArrayList<String>();
    	searchOptions.add("Name");
    	searchOptions.add("Description");
    	searchOptions.add("Category");
    	searchOptions.add("Creator");
    	setVisibleServices("");
    	this.serviceEventListener.registerController(this);
    	
    }
        
    public void handleFileUpload(FileUploadEvent event) {  
        if(log.isDebugEnabled())
        	log.debug("Upload file was successful: " + event.getFile().getFileName());
        
        try {
        	//sendMessage("Installing App...","SOCIETIES is trying to install your app. You will be notified when the process is complete.",FacesMessage.SEVERITY_INFO);
        	// We don't actually wait for OSGI to install the file, as this would clog up the webapp.
        	ServiceControlResult result = getServiceControl().installService(event.getFile().getInputstream(), event.getFile().getFileName()).get(60, TimeUnit.SECONDS);
			if(result.getMessage().equals(ResultMessage.SUCCESS))
				serviceInstalled(result.getServiceId());
			else{
				sendMessage("Problem Installing App!","SOCIETIES encountered a problem while install your app: " +result.getMessage(),FacesMessage.SEVERITY_ERROR);
			}
		} catch (Exception e) {
			log.error("Exception occured: {}", e.getMessage());
			e.printStackTrace();
			sendMessage("Problem Installing App!","SOCIETIES encountered a problem while install your app!",FacesMessage.SEVERITY_ERROR);

		} finally{
			setServiceId(null);
		}

    }  
    
    public void startService(){
    	log.debug("Staring service: {}", selectedService.getServiceName());
    	//sendMessage("Starting App...","The app '" +selectedService.getServiceName() +"' is starting up!",FacesMessage.SEVERITY_INFO);
    	
    	try {

			ServiceControlResult result = getServiceControl().startService(selectedService.getServiceIdentifier()).get(10, TimeUnit.SECONDS);
			if(result.getMessage().equals(ResultMessage.SUCCESS))
				serviceStarted(selectedService.getServiceIdentifier(),selectedService.getServiceName());
			else{
				sendMessage("Problem starting App","The App couldn't start because: " + result.getMessage() ,FacesMessage.SEVERITY_ERROR);
			}
			
		} catch (Exception e) {
			log.error("There was an exception trying to start the service: ", e.getMessage());
			e.printStackTrace();
			sendMessage("Problem starting App","A problem occured while trying to start the app " + selectedService.getServiceName(),FacesMessage.SEVERITY_ERROR);
		} finally{
			setServiceId(null);
		}
    }
    
    
    public void stopService(){
    	log.debug("Stopping service: {}", selectedService.getServiceName());
    	//sendMessage("App Stopping...","The app '" +selectedService.getServiceName() +"' is being stopped.",FacesMessage.SEVERITY_INFO);
    	
    	try {
    		ServiceControlResult result = getServiceControl().stopService(selectedService.getServiceIdentifier()).get(10, TimeUnit.SECONDS);
			if(result.getMessage().equals(ResultMessage.SUCCESS))
				serviceStopped(selectedService.getServiceIdentifier(),selectedService.getServiceName());
			else{
				sendMessage("Problem stopping App","The App couldn't stop because: " + result.getMessage() ,FacesMessage.SEVERITY_ERROR);
			}
		} catch (Exception e) {
			log.error("There was an exception trying to stop the service: ", e.getMessage());
			e.printStackTrace();
			sendMessage("Problem stopping App","A problem occured while trying to stop the app " + selectedService.getServiceName(),FacesMessage.SEVERITY_ERROR);
		} finally{
			setServiceId(null);
		}

    }
    
    public void installService(){
    	log.debug("Installing service: {}", selectedService.getServiceName());
    	sendMessage("Installing Shared App...","Please look at your notifications to review privacy rules so the process can continue!",FacesMessage.SEVERITY_INFO);
    	
    	try {
			getServiceControl().installService(selectedService);
		} catch (ServiceControlException e) {
			log.error("There was an exception trying to install the service: ", e.getMessage());
			e.printStackTrace();
			sendMessage("Problem stopping App","A problem occured while trying to install the app " + selectedService.getServiceName(),FacesMessage.SEVERITY_ERROR);
		}
    }
    
    public void removeService(){
    	log.debug("Removing service: {}", selectedService.getServiceName());
    	//sendMessage("Removing App...","The app '" +selectedService.getServiceName() +"' is being removed.",FacesMessage.SEVERITY_INFO);
    	
    	try {
    		ServiceControlResult result = getServiceControl().uninstallService(selectedService.getServiceIdentifier()).get(10, TimeUnit.SECONDS);
			if(result.getMessage().equals(ResultMessage.SUCCESS))
				serviceRemoved(selectedService.getServiceIdentifier(),selectedService.getServiceName(), selectedService.getServiceType());
			else{
				sendMessage("Problem removing App","The App couldn't be removed because " + result.getMessage() ,FacesMessage.SEVERITY_ERROR);
			}
		} catch (Exception e) {
			log.error("There was an exception trying to remove the service: ", e.getMessage());
			e.printStackTrace();
			sendMessage("Problem removing App","A problem occured while trying to remove the app " + selectedService.getServiceName(),FacesMessage.SEVERITY_ERROR);
		} finally{
			setServiceId(null);
		}
    }
    
    public void shareService(String serviceId, String node){
    	setServiceId(serviceId);
    	log.debug("Sharing: {} from {}", selectedService.getServiceName(), node);
    	    	
    	try {
    		setServiceId(serviceId);
    		IIdentity nodeId = getCommManager().getIdManager().fromJid(node);
    		ServiceControlResult result = getServiceControl().shareService(selectedService, nodeId).get(10, TimeUnit.SECONDS);
			if(result.getMessage().equals(ResultMessage.SUCCESS))
				serviceShared(selectedService.getServiceIdentifier(),selectedService.getServiceName(), nodeId);
			else{
				sendMessage("Problem sharing App","The App couldn't be added because " + result.getMessage() ,FacesMessage.SEVERITY_ERROR);
			}
		} catch (Exception e) {
			log.error("There was an exception trying to share the service: ", e.getMessage());
			e.printStackTrace();
			sendMessage("Problem Sharing App!","A problem occured while trying to share the app " + selectedService.getServiceName(),FacesMessage.SEVERITY_ERROR);
		} finally{
			setServiceId(null);
		}
    }
    
    public void unshareService(String serviceId, String node){
    	setServiceId(serviceId);
    	log.debug("Removing unsharing: {} from {}", selectedService.getServiceName(), node);

    	try {
    		setServiceId(serviceId);
    		IIdentity nodeId = getCommManager().getIdManager().fromJid(node);
    		ServiceControlResult result = getServiceControl().unshareService(selectedService, nodeId).get(10, TimeUnit.SECONDS);
			if(result.getMessage().equals(ResultMessage.SUCCESS))
				serviceUnshared(selectedService.getServiceIdentifier(),selectedService.getServiceName(), nodeId);
			else{
				sendMessage("Problem removing App from Community","The App couldn't be unshared because " + result.getMessage() ,FacesMessage.SEVERITY_ERROR);
			}
		} catch (Exception e) {
			log.error("There was an exception trying to unshare the service: ", e.getMessage());
			e.printStackTrace();
			sendMessage("Problem removing App from Community","A problem occured while trying to remove the app " + selectedService.getServiceName(),FacesMessage.SEVERITY_ERROR);
		} finally{
			setServiceId(null);
		}
    }
    
    public void unshareService(){
    	log.debug("Removing sharing: {} from {}", selectedService.getServiceName(), getSelectedNode());
    	/*ICis myCis = getCisManager().getCis(getSelectedNode());
    	StringBuilder message = new StringBuilder();
    	message.append("The app '").append(selectedService.getServiceName()).append("' was removed from Community '").append(myCis.getName());
    	sendMessage("Removing App from Community...",message.toString(),FacesMessage.SEVERITY_INFO);*/
    	
    	try {
    		ServiceControlResult result = getServiceControl().unshareService(selectedService, selectedNode).get(10, TimeUnit.SECONDS);
			if(result.getMessage().equals(ResultMessage.SUCCESS))
				serviceUnshared(selectedService.getServiceIdentifier(),selectedService.getServiceName(), selectedNode);
			else{
				sendMessage("Problem removing App from Community","The App couldn't be unshared because " + result.getMessage() ,FacesMessage.SEVERITY_ERROR);
			}
		} catch (Exception e) {
			log.error("There was an exception trying to unshare the service: ", e.getMessage());
			e.printStackTrace();
			sendMessage("Problem removing App from Community","A problem occured while trying to remove the app " + selectedService.getServiceName(),FacesMessage.SEVERITY_ERROR);
		} finally{
			setServiceId(null);
		}
    }
    

    public void selectNode(){
    	if(log.isDebugEnabled())
    		log.debug("Selecting one node to view the services:" + selectedNode.getIdentifier());
    	
    	setDidSearch(false);
    	
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
    		ServiceWrapper servWrapped = new ServiceWrapper(service,this);
    		currentServices.put(servWrapped.getId(), servWrapped);
    		if(service.getServiceType().equals(ServiceType.THIRD_PARTY_CLIENT)){
    			String parentId = ServiceModelUtils.serviceResourceIdentifierToString(service.getServiceInstance().getParentIdentifier());
    			thirdClients.put(servWrapped.getId(),parentId);
    		}
    		
    	}
    	
    	
    }
    
    public boolean isMyNode(){
    	return "mynode".equals(getSelectedNode());
    }

    public void launchService(){
    	log.debug("Launching 3P Service...");
    	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
    	StringBuilder urlBuilder = new StringBuilder();
    	urlBuilder.append("http://").append(context.getRequestServerName()).append(':').append(context.getRequestServerPort()).append(selectedService.getServiceEndpoint());
    	try{
			sendMessage("Launching App...","A problem occured while trying to launch app!",FacesMessage.SEVERITY_INFO);
    		log.debug("Trying to launch service at URL: {}",urlBuilder.toString());
    		context.redirect(urlBuilder.toString());
    	} catch(Exception ex){
    		log.error("");
    		ex.printStackTrace();
			sendMessage("Problem Redirecting to App","A problem occured while trying to launch app!",FacesMessage.SEVERITY_ERROR);

    	}
    }
    public void searchService(){
    	log.debug("Searching for services, the option is {} and the key is {}",getSearchOption(),getSearchBy());
    	
    	try {
    		
    		if(searchBy == null || searchBy.isEmpty()){
    			log.debug("Nothing to search for...");
    			sendMessage("No Search Done!","No search terms introduced...",FacesMessage.SEVERITY_INFO);
    			return;
    		}
    		
    		// NOW WE PREPARE THE FILTER!
    		Service filter = ServiceModelUtils.generateEmptyFilter();
    		
    		if("Name".equals(searchOption)){
    			log.debug("Searching by Name... preparing filter!");
    			filter.setServiceName(searchBy);
    		} else{
    			if("Description".equals(searchOption)){
        			log.debug("Searching by Description... preparing filter!");
        			filter.setServiceDescription(searchBy);
    			} else{
    	   			if("Creator".equals(searchOption)){
            			log.debug("Searching by Author... preparing filter!");
            			filter.setAuthorSignature(searchBy);
        			} else{
        	   			if("Category".equals(searchOption)){
                			log.debug("Searching by Author... preparing filter!");
                			filter.setServiceCategory(searchBy);
            			} else{
            				log.debug("Unrecognized search option: {}",searchOption);
            				sendMessage("No search option!","There was a problem searching!",FacesMessage.SEVERITY_WARN);
            				return;
            			}
        			}
    			}
    		}
    		
    		List<Service> result = getServiceDiscovery().searchServicesAll(filter).get(30, TimeUnit.SECONDS);
			if(result.isEmpty())
				sendMessage("No App Found...","Didn't find any apps that matched the search query!",FacesMessage.SEVERITY_INFO);
			else{
				setHeader("Search result for Apps with "+ searchOption + " that matches " + searchBy);
				currentServices = new HashMap<String, ServiceWrapper>(result.size()*2);
		    	for(Service service : result){
		    		ServiceWrapper servWrapped = new ServiceWrapper(service,this);
		    		currentServices.put(servWrapped.getId(), servWrapped);		    		
		    	}
		    	
		    	setDidSearch(true);
			}
		} catch (Exception e) {
			log.error("There was an exception trying to search for services: ", e.getMessage());
			e.printStackTrace();
			sendMessage("Problem Searching App","A problem occured while trying to search!",FacesMessage.SEVERITY_ERROR);
		} 
 
    }

    protected void serviceStarted(ServiceResourceIdentifier serviceId, String serviceName){
    	log.debug("Service was started: {}", serviceName);
        
    	ServiceWrapper service = currentServices.get(ServiceModelUtils.serviceResourceIdentifierToString(serviceId));
    	if(service != null){
    		log.debug("Started service exists in currentServices, so we need to start it");
    		service.getService().setServiceStatus(ServiceStatus.STARTED);
    	} else{
    		log.debug("Started service was not being shown, so nothing needs to be done!");
    	}
    	
    	sendMessage("App Started", "App '"+serviceName+"' has started running.", FacesMessage.SEVERITY_INFO);
    }
    
    protected void serviceStopped(ServiceResourceIdentifier serviceId, String serviceName){
    	log.debug("Service was stopped: {}", serviceName);
        
    	ServiceWrapper service = currentServices.get(ServiceModelUtils.serviceResourceIdentifierToString(serviceId));
    	if(service != null){
    		log.debug("Stopped service exists in currentServices, so we need to stop it");
    		service.getService().setServiceStatus(ServiceStatus.STOPPED);
    	} else{
    		log.debug("Stopped service was not being shown, so nothing needs to be done!");
    	}
    	
    	sendMessage("App Stopped", "App '"+serviceName+"' has stopped running.", FacesMessage.SEVERITY_INFO);
    		
    }
    
    protected void serviceInstalled(ServiceResourceIdentifier serviceId){
    	log.debug("Service was installed: {}", ServiceModelUtils.serviceResourceIdentifierToString(serviceId));
        
    	//First step, we get the service
    	try{
    		
    		Service newService = getServiceDiscovery().getService(serviceId).get();
    		
    		if(newService == null){
    			log.debug("Service {} was not found!",newService.getServiceName());
    			sendMessage("Error Installing New App", "App '"+newService.getServiceName()+"' can't be found?!", FacesMessage.SEVERITY_WARN);
    			return;
    		} else{
    			if(isMyNode()){
    				log.debug("Service {} is being put into the list", newService.getServiceName());
    				ServiceWrapper newServiceWrapper = new ServiceWrapper(newService,this);
    				currentServices.put(newServiceWrapper.getId(), newServiceWrapper);
    			}
    			
        		if(newService.getServiceType().equals(ServiceType.THIRD_PARTY_CLIENT)){
        			String parentId = ServiceModelUtils.serviceResourceIdentifierToString(newService.getServiceInstance().getParentIdentifier());
        			thirdClients.put(ServiceModelUtils.serviceResourceIdentifierToString(serviceId),parentId);
        		}
        		
    			sendMessage("New App Installed", "App '"+newService.getServiceName()+"' was installed!", FacesMessage.SEVERITY_INFO);

    		}
    			
    	} catch(Exception ex){
    		log.error("Exception ocurred while processing event: {}", ex.getMessage());
    		ex.printStackTrace();
    	}

    }
    
    protected void installFailed(ServiceResourceIdentifier serviceId,String serviceName){
    	log.debug("Installing the service {} failed!",serviceName);
    	sendMessage("Install App Failed!","SOCIETIES didn't succeed in installing the app" + serviceName,FacesMessage.SEVERITY_WARN);
    }
    
    protected ServiceWrapper getService(ServiceResourceIdentifier serviceId){
		return currentServices.get(ServiceModelUtils.serviceResourceIdentifierToString(serviceId));
    }
    
    protected void serviceRemoved(ServiceResourceIdentifier serviceId, String serviceName, ServiceType serviceType){
    	
    	log.debug("Service was removed: {}", serviceName);
    
    	ServiceWrapper deletedService = currentServices.get(ServiceModelUtils.serviceResourceIdentifierToString(serviceId));
    	if(deletedService != null){
    		log.debug("Deleted service exists in currentServices, so we need to remove it");
    		currentServices.remove(deletedService.getId());	
    	} else{
    		log.debug("Deleted service was not being shown, so nothing needs to be done!");
    	}
    	
  		if(serviceType.equals(ServiceType.THIRD_PARTY_CLIENT)){
  			this.thirdClients.remove(ServiceModelUtils.serviceResourceIdentifierToString(serviceId));
		}
    	
    	sendMessage("App Removed", "App '"+serviceName+"' was removed from the platform.", FacesMessage.SEVERITY_INFO);
    	  	
    }
    
    protected void serviceShared(ServiceResourceIdentifier serviceId, String serviceName, IIdentity sharedNode){
    	log.debug("Service {} was shared with {}",serviceName,sharedNode.getJid());
    	
    	if(sharedNode.equals(selectedNode)){
    		log.debug("We are viewing this node, so we need to add this service to the node");
    		selectNode();
    	} else {
    		log.debug("We are not viewing the node, so no need to add the service...");
    		ServiceWrapper myService = currentServices.get(ServiceModelUtils.serviceResourceIdentifierToString(serviceId));
    		if(myService != null)
    			myService.setSharedCisId(null);
    	}
    	
    	StringBuilder message = new StringBuilder();
    	message.append("App '").append(serviceName).append("' is now shared with Community: '").append(getCisManager().getCis(sharedNode.getJid()).getName()).append("'");
    	sendMessage("App Shared", message.toString(), FacesMessage.SEVERITY_INFO);
    	
    }
    
    protected void serviceUnshared(ServiceResourceIdentifier serviceId, String serviceName, IIdentity sharedNode){
    	log.debug("Service {} was unshared with {}",serviceName,sharedNode.getJid());
    	
    	if(sharedNode.equals(selectedNode)){
    		log.debug("We are viewing this node, so we need to remove this service from the node");
    		selectNode();
    	}else {
    		log.debug("We are not viewing the node, so no need to remove the service...");
    		ServiceWrapper myService = currentServices.get(ServiceModelUtils.serviceResourceIdentifierToString(serviceId));
    		if(myService != null)
    			myService.setSharedCisId(null);
    	}
    	
    	StringBuilder message = new StringBuilder();
    	message.append("App '").append(serviceName).append("' is no longer shared with Community: '").append(getCisManager().getCis(sharedNode.getJid()).getName()).append("'");
    	sendMessage("App Sharing Stopped", message.toString(), FacesMessage.SEVERITY_INFO);
    	
    }
    
    private void sendMessage(String title, String detail, Severity severity){    	
    	try{
    		if(FacesContext.getCurrentInstance() != null){
    	    	log.debug("Appending message {} : {} ", title, detail);
    			this.addFacesMessage(SERVICES_GROWL, title, detail, severity);
    		}
    		else{
    			cacheMessage(title,detail,severity);
    		}
    			
    			
    	} catch(Exception ex){
    		log.error("Exception occured while trying to send Faces Message! : {}",ex.getMessage());
    		ex.printStackTrace();
    	}
    }

    private void processMessageCache(){
    	QueuedMessage message = messageQueue.poll();
    	while(message != null){
    		addFacesMessage(SERVICES_GROWL,message.getTitle(),message.getDetail(),message.getSeverity());
    		message = messageQueue.poll();
    	}
    }
    
	private void cacheMessage(String title, String detail, Severity severity) {
		log.debug("Caching message: {} : {}",title,detail);
		messageQueue.add(new QueuedMessage(title,detail,severity));
	}
	
	private class QueuedMessage {
		private String title;
		private String detail;
		private Severity severity;
		
		public QueuedMessage(String title, String detail, Severity severity){
			this.title = title;
			this.detail = detail;
			this.severity = severity;
		}
		
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

		public Severity getSeverity() {
			return severity;
		}

		public void setSeverity(Severity severity) {
			this.severity = severity;
		}		

	}
}

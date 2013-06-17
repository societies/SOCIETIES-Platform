package org.societies.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.personalisation.CAUI.api.CAUIDiscovery.ICAUIDiscovery;
import org.societies.personalisation.CACI.api.CACIDiscovery.ICACIDiscovery;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.webapp.models.CAUIAction;
import org.societies.webapp.service.UserService;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "cauiController")
@RequestScoped
public class CAUIController extends BasePageController {


	private static Logger LOG = LoggerFactory.getLogger(CAUIController.class);


	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{userService}")
	private UserService userService; 
	private String stringProperty;
	private boolean boolProperty;

	private List<CAUIAction> userActionsList = new ArrayList<CAUIAction>();

	public CAUIController() {

		LOG.info(this.getClass().getName() + " instantiated");
	}

	@PostConstruct
	public void init() {

		LOG.info(this.getClass().getName() + " initialising");

		//userActionsList = this.getCAUIActiveModel();
		
		HashMap<String,Double> target1 = new HashMap<String,Double>();
		target1.put("targetAct1", 0.8);
		target1.put("targetAct2", 0.2);
		CAUIAction action1 = new CAUIAction("action1",target1);

		HashMap<String,Double> target2 = new HashMap<String,Double>();
		target2.put("targetAct2", 1.0);
		CAUIAction action2 = new CAUIAction("action2",target2);

		System.out.println("action records 1 ");
		this.userActionsList.add(action1);
		this.userActionsList.add(action2);
		System.out.println("action records 2 aaaa "+this.userActionsList);
		
	}

	@ManagedProperty(value = "#{cauiTaskManager}")
	private ICAUITaskManager cauiTaskManager;

	@ManagedProperty(value = "#{cauiDiscovery}")
	private ICAUIDiscovery cauiDiscovery;

	@ManagedProperty(value = "#{caciDiscovery}")
	private ICACIDiscovery caciDiscovery;

	@ManagedProperty(value = "#{internalCtxBroker}")
	private ICtxBroker internalCtxBroker;

	private ICommManager commMngrRef;



	public List<CAUIAction> getUserActionsList(){

		return this.userActionsList;
	}



	public ICAUITaskManager getCauiTaskManager() {

		LOG.info("get task manager "+cauiTaskManager);
		return this.cauiTaskManager;
	}


	public void setCauiTaskManager(ICAUITaskManager cauiTaskManager) {

		if (cauiTaskManager == null)
			log.info("taskManager() = null");
		else
			LOG.info("set taskManager manager " +cauiTaskManager.toString());
		this.cauiTaskManager = cauiTaskManager;
	}

	public ICAUIDiscovery getCauiDiscovery() {

		LOG.info("get cauiDiscovery manager "+cauiDiscovery);    
		return cauiDiscovery;
	}

	public ICACIDiscovery getCaciDiscovery() {

		LOG.info("get cauiDiscovery manager "+cauiDiscovery);    
		return caciDiscovery;
	}

	public ICtxBroker getInternalCtxBroker() {
		LOG.info("get internalCtxBroker manager " +internalCtxBroker);
		return internalCtxBroker;
	}

	public void setInternalCtxBroker(ICtxBroker internalCtxBroker) {

		if (internalCtxBroker == null)
			log.info("setInternalCtxBroker() = null");
		else
			log.info("setInternalCtxBroker() = " + internalCtxBroker.toString());

		this.internalCtxBroker = internalCtxBroker;
	}


	public void setCauiDiscovery(ICAUIDiscovery cauiDiscovery) {
		LOG.info("set cauiDiscovery manager " +cauiDiscovery);
		this.cauiDiscovery = cauiDiscovery;
	}

	public void setCaciDiscovery(ICACIDiscovery caciDiscovery) {
		LOG.info("set caciDiscovery manager " +caciDiscovery);
		this.caciDiscovery = caciDiscovery;
	}

	public ICommManager getCommMngrRef() {
		return this.commMngrRef;
	}


	public void setCommMngrRef(ICommManager commMngrRef) {
		this.commMngrRef = commMngrRef;
	}



	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}



	public String getStringProperty() {
		return stringProperty;
	}

	public void setStringProperty(String stringProperty) {
		this.stringProperty = stringProperty;
	}

	public boolean isBoolProperty() {
		return boolProperty;
	}

	public void setBoolProperty(boolean boolProperty) {
		this.boolProperty = boolProperty;
	}

	public void showMeAMessage() {
		addGlobalMessage("THIS IS CAUI MESSAGE", "Now here's your message!", FacesMessage.SEVERITY_INFO);
	}



	public List<CAUIAction> getCAUIActiveModel(){

		List<CAUIAction> result = new ArrayList<CAUIAction>();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> model = new 	HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>>();
		if(this.cauiTaskManager.getCAUIActiveModel() != null){
			model = this.cauiTaskManager.getCAUIActiveModel();	

			for(IUserIntentAction sourceAct : model.keySet()){

				String sourcActionString = sourceAct.toString();

				HashMap<String, Double> targetMap = new HashMap<String, Double>();
				HashMap<IUserIntentAction, Double> targetMapOriginal = model.get(sourceAct);

				for(IUserIntentAction targetActOrig : targetMapOriginal.keySet()){
					targetMap.put(targetActOrig.toString(), targetMapOriginal.get(targetActOrig));
				}

				CAUIAction cauiAct = new CAUIAction(sourcActionString, targetMap);
				result.add(cauiAct);
			}		
		}

		return result;		 
	}


	public void learnUserModel(){
		int i = 0;
		addGlobalMessage("learn User Model", "for id "+i, FacesMessage.SEVERITY_INFO);
		LOG.info("service ref for cauiDisc "+ this.cauiDiscovery);
		LOG.info("service ref for broker  "+ this.internalCtxBroker);
		this.cauiDiscovery.generateNewUserModel();
		LOG.info("discovery started..." );

	}

	
	public void refreshUserModel(){
		List<CAUIAction> ls = getCAUIActiveModel();
		
		LOG.info("refreshUserModel: "+ ls);
		
		addGlobalMessage("MODEL REFRESHED", "Now here's your message!", FacesMessage.SEVERITY_INFO);
	}

	public void learnCommunityModel(){
		int i = 0;
		addGlobalMessage("learn community Model", "for id "+i, FacesMessage.SEVERITY_INFO);
		this.caciDiscovery.generateNewCommunityModel();
		LOG.info("discovery started..." );
	}

	private IIdentity getOwnerId(){

		IIdentity cssOwnerId = null;
		try {
			final INetworkNode cssNodeId = this.commMngrRef.getIdManager().getThisNetworkNode();

			final String cssOwnerStr = cssNodeId.getBareJid();
			cssOwnerId = this.commMngrRef.getIdManager().fromJid(cssOwnerStr);
			LOG.info("*** css identity = " + cssOwnerId);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cssOwnerId;
	}
}
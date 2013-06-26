package org.societies.webapp.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.personalisation.CAUI.api.CAUIDiscovery.ICAUIDiscovery;
import org.societies.personalisation.CACI.api.CACIDiscovery.ICACIDiscovery;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.webapp.models.CAUIAction;
import org.societies.webapp.models.CAUIActionLog;
import org.societies.webapp.service.UserService;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.context.model.CtxAttributeTypes;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "cauiController")
@ViewScoped
public class CAUIController extends BasePageController {

	private static Logger LOG = LoggerFactory.getLogger(CAUIController.class);

	private static final long serialVersionUID = 1L;

	private String stringProperty;
	private boolean boolProperty;

	private boolean enableUserPrediction = true;
	static int i = 0;

	private List<CAUIAction> userActionsList = new ArrayList<CAUIAction>();
	private List<CAUIAction> communityActionsList = new ArrayList<CAUIAction>();
	private List<CAUIActionLog> predictionLogList = new ArrayList<CAUIActionLog>();


	public CAUIController() {

		LOG.info(this.getClass().getName() + "constructor instantiated");
	}


	@PostConstruct
	public void init() {

		LOG.info(this.getClass().getName() + " initialising");

		this.userActionsList = this.getActiveModel(CtxAttributeTypes.CAUI_MODEL);
		this.communityActionsList = this.getActiveModel(CtxAttributeTypes.CACI_MODEL);
		this.predictionLogList = this.getPredictionLog();

		LOG.info("isUserPredictionEnabled ::"+this.cauiPrediction.isUserPredictionEnabled());
		this.enableUserPrediction = this.cauiPrediction.isUserPredictionEnabled();

		LOG.info("action records ::"+this.userActionsList);
		LOG.info("community records ::"+this.communityActionsList);
		LOG.info("predictionLogList ::"+this.predictionLogList);

		//learnUserModel();
		//learnUserModel();
	}


	@ManagedProperty(value = "#{userService}")
	private UserService userService; 

	@ManagedProperty(value = "#{internalCtxBroker}")
	private ICtxBroker internalCtxBroker;

	@ManagedProperty(value = "#{cauiPrediction}")
	private ICAUIPrediction cauiPrediction;

	private ICommManager commMngrRef;


	public void setEnableUserPrediction(boolean bool){
		this.enableUserPrediction = bool;
		LOG.info("setEnableUserPrediction  : "+ bool);
	}

	public Boolean isEnableUserPrediction(){
		LOG.info("getEnableUserPrediction  : "+ this.enableUserPrediction);
		return this.enableUserPrediction ;
	}


	public List<CAUIAction> getUserActionsList(){

		return this.userActionsList;
	}

	public List<CAUIAction> getCommunityActionsList(){

		return this.communityActionsList;
	}

	public List<CAUIActionLog> getPredictionLogList(){

		return this.predictionLogList;
	}

	public List<CAUIAction> getActiveModel(String type){

		List<CAUIAction> result = new ArrayList<CAUIAction>();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> model = new 	HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>>();

		if (type.equals(CtxAttributeTypes.CAUI_MODEL)){
			model = this.cauiPrediction.getCAUIActiveModel();
			if(!model.isEmpty()){
				LOG.info("getCAUIActiveModel  : "+ model);
				result = convertModel(model);
				return result;
			}
		} else if(type.equals(CtxAttributeTypes.CACI_MODEL) ){
			model = this.cauiPrediction.getCAUIActiveModel();
			if(!model.isEmpty()){				
				LOG.info("getCACIActiveModel  : "+ model);
				result = convertModel(model);
				return result;
			}
		} 
		return result;
	}


	public List<CAUIActionLog>  getPredictionLog(){

		List<CAUIActionLog> result = new ArrayList<CAUIActionLog>(); 
		List<Entry<String, String>> tempResultList = this.cauiPrediction.getPredictionPairLog();

		if( !tempResultList.isEmpty()){
			for(Entry<String,String> entry : tempResultList){
				String performed = entry.getKey();
				String predicted = entry.getValue();
				CAUIActionLog cauiLog = new CAUIActionLog(performed,predicted);
				LOG.info("getPredictionLog  cauiLog : "+ cauiLog);
				result.add(cauiLog);
			}
		}
		LOG.info("getPredictionLog result : "+ result);
		return result;
	}


	private List<CAUIAction> convertModel(HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> originalModel ){

		List<CAUIAction> result = new ArrayList<CAUIAction>();

		if(!originalModel.isEmpty()){
			for(IUserIntentAction sourceAct : originalModel.keySet()){

				//String sourcActionString = sourceAct.toString();
				
				String sourcActionString = sourceAct.getparameterName() +"="+ sourceAct.getvalue();

				HashMap<String, Double> targetMap = new HashMap<String, Double>();
				HashMap<IUserIntentAction, Double> targetMapOriginal = originalModel.get(sourceAct);

				for(IUserIntentAction targetActOrig : targetMapOriginal.keySet()){
					String trimedTargetStr = targetActOrig.getparameterName()+"="+targetActOrig.getvalue();
					Double transProb = targetMapOriginal.get(targetActOrig);
					DecimalFormat df = new DecimalFormat("#.##");
					Double transProbTrimmed = Double.parseDouble(df.format(transProb));
					targetMap.put(trimedTargetStr, transProbTrimmed);

				}
				CAUIAction cauiAct = new CAUIAction(sourcActionString, targetMap);
				LOG.info("convertModel  : "+ cauiAct);
				result.add(cauiAct);
			}		
		}
		return result;
	}


	public void learnUserModel(){

		System.out.println("learnUserModel");

		IIdentity cssID = null ; 
		//if( getOwnerId() != null) {	cssID = getOwnerId();		}
		LOG.info("service ref for cauiDisc "+ this.cauiPrediction);
		LOG.info("service ref for broker  "+ this.internalCtxBroker);
		this.cauiPrediction.generateNewUserModel();
		LOG.info("discovery started..." );
		addGlobalMessage("MODEL LEARNING TRIGGERED for cssID", "click on refresh model button", FacesMessage.SEVERITY_INFO);
	}


	public void refreshUserModel(){
		addGlobalMessage("MODEL REFRESHED", "Retrieving model from DB", FacesMessage.SEVERITY_INFO);
	}


	public void learnCommunityModel(){

		addGlobalMessage("learn community Model", "for id "+i, FacesMessage.SEVERITY_INFO);
		IIdentity cisId = null;

		this.cauiPrediction.generateNewCommunityModel(cisId);
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



	public ICommManager getCommMngrRef() {
		return this.commMngrRef;
	}


	public void setCommMngrRef(ICommManager commMngrRef) {
		this.commMngrRef = commMngrRef;
	}


	public ICAUIPrediction getCauiPrediction() {
		LOG.info("get getCauiPrediction "+this.cauiPrediction);
		return this.cauiPrediction;
	}

	public void setCauiPrediction(ICAUIPrediction cauiPrediction) {
		this.cauiPrediction = cauiPrediction;
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

}
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
	private ICommManager commMngrRef;

	private String stringProperty;
	private boolean boolProperty;

	private boolean enableUserPrediction = true;
	private boolean enableCommunityPrediction = true;

	static int i = 0;
	

	private List<CAUIAction> userActionsList = new ArrayList<CAUIAction>();
	private List<CAUIAction> communityActionsList = new ArrayList<CAUIAction>();
	private List<CAUIActionLog> predictionLogList = new ArrayList<CAUIActionLog>();
	private String cisId ;
	//private List<String> cisIdList ;
	private String[] cisIdList ;
	


	public String[] getCisIdList() {
		return cisIdList;
	}


	public void setCisIdList(String[] cisIdList) {
		this.cisIdList = cisIdList;
	}


	public CAUIController() {
		
		//cisIdList = new ArrayList<String>();
		cisIdList = new String[] {"name1", "name2", "name2"};
		LOG.info(this.getClass().getName() + "constructor instantiated");
	}


	@PostConstruct
	public void init() {

		LOG.info(this.getClass().getName() + " initialising");

		this.userActionsList = this.getCAUIActiveModel();
		this.communityActionsList = this.getCACIActiveModel();
		this.predictionLogList = this.getPredictionLog();

		//LOG.info("isUserPredictionEnabled ::"+this.cauiPrediction.isUserPredictionEnabled());
		//this.enableUserPrediction = this.cauiPrediction.isUserPredictionEnabled();

		//LOG.info("isCommunityPredictionEnabled ::"+this.cauiPrediction.isCommunityPredictionEnabled());
		//this.enableCommunityPrediction = this.cauiPrediction.isCommunityPredictionEnabled();

		LOG.info("action records ::"+this.userActionsList);
		LOG.info("community records ::"+this.communityActionsList);
		LOG.info("predictionLogList ::"+this.predictionLogList);
		LOG.info("cisId ::"+this.cisId);
		
		
		///cisIdList.add("aaaaa");
		//cisIdList.add("bbbbb");
		//cisIdList.add("ccccc");
		LOG.info("cisIdList ::"+this.cisIdList.length);
	}


	@ManagedProperty(value = "#{userService}")
	private UserService userService; 

	@ManagedProperty(value = "#{internalCtxBroker}")
	private ICtxBroker internalCtxBroker;

	@ManagedProperty(value = "#{cauiPrediction}")
	private ICAUIPrediction cauiPrediction;

	
	
	public String getCisId() {
		return cisId;
	}

	public void setCisId(String cisId) {
		this.cisId = cisId;
	}

	/*
	public List<String> getCisIdList() {
		return cisIdList;
	}


	public void setCisIdList(List<String> cisIdList) {
		this.cisIdList = cisIdList;
	}
	*/
	
	

	public void setEnableUserPrediction(boolean bool){

		this.enableUserPrediction = bool;
		LOG.info("setEnableUserPrediction  : "+ bool);
	}

	public boolean getEnableUserPrediction(){
		LOG.info("getEnableUserPrediction  : "+ this.enableUserPrediction);
		return this.enableUserPrediction ;
	}


	public void updateComUsrPred(){

		LOG.info("updateComUsrPred  usr : "+ this.enableUserPrediction);
		LOG.info("updateComUsrPred  comm : "+ this.enableCommunityPrediction);

		this.cauiPrediction.enableCommPrediction(this.enableCommunityPrediction);
		this.cauiPrediction.enableUserPrediction(this.enableUserPrediction);

		//this.enableUserPrediction = this.cauiPrediction.isUserPredictionEnabled();
		//this.enableCommunityPrediction = this.cauiPrediction.isCommunityPredictionEnabled();
	}



	public void setEnableCommunityPrediction(boolean bool){
		this.enableCommunityPrediction = bool;
		LOG.info("setEnableCommunityPrediction  : "+ bool);
	}

	public boolean getEnableCommunityPrediction(){


		LOG.info("getEnableCommunityPrediction  : "+ this.enableCommunityPrediction);

		return this.enableCommunityPrediction ;
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

	
	
	
	public List<CAUIAction> getCAUIActiveModel(){

		List<CAUIAction> result = new ArrayList<CAUIAction>();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> model = new 	HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>>();

		
			model = this.cauiPrediction.getCAUIActiveModel();
			LOG.info("getCAUIActiveModel  : "+ model);
			if(!model.isEmpty()){
				result = convertModel(model);
			
			}
			return result;
	}
			
	public List<CAUIAction> getCACIActiveModel(){

		List<CAUIAction> result = new ArrayList<CAUIAction>();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> model = new 	HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>>();

		
			model = this.cauiPrediction.getCACIActiveModel();
			LOG.info("getCAUIActiveModel  : "+ model);
			if(!model.isEmpty()){
				result = convertModel(model);
			
			}
			return result;
	}
			
			
	/*
	public List<CAUIAction> getActiveModel(String type){

		List<CAUIAction> result = new ArrayList<CAUIAction>();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> model = new 	HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>>();

		if (type.equals(CtxAttributeTypes.CAUI_MODEL)){
			model = this.cauiPrediction.getCAUIActiveModel();
			LOG.info("getCAUIActiveModel  : "+ model);
			if(!model.isEmpty()){

				result = convertModel(model);
				return result;
			}
		} else if(type.equals(CtxAttributeTypes.CACI_MODEL) ){
			model = this.cauiPrediction.getCACIActiveModel();
			LOG.info("getCACIActiveModel  : "+ model);
			if(!model.isEmpty()){				

				result = convertModel(model);
				return result;
			}
		} 
		return result;
	}
	*/
	

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
				LOG.info("convertModel 0 sourcActionString : "+ sourcActionString);

				HashMap<String, Double> targetMap = new HashMap<String, Double>();
				if( originalModel.get(sourceAct) != null) {

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
		}
		return result;
	}


	public void learnUserModel(){

		System.out.println("learnUserModel");

		//IIdentity cssID = getOwnerId(); 
		//if( getOwnerId() != null) {	cssID = getOwnerId();		}
		LOG.info("service ref for cauiDisc "+ this.cauiPrediction);
		LOG.info("service ref for broker  "+ this.internalCtxBroker);
		this.cauiPrediction.generateNewUserModel();
		LOG.info("discovery started..." );
		addGlobalMessage("MODEL LEARNING TRIGGERED for user", "click on refresh model button", FacesMessage.SEVERITY_INFO);
	}


	public void refreshUserModels(){

		this.userActionsList = this.getCAUIActiveModel();
	//	this.communityActionsList = this.getCACIActiveModel();
		

		addGlobalMessage("Prediction model and log tables REFRESHED", "Retrieving model from DB", FacesMessage.SEVERITY_INFO);

	}

	public void refreshCommunityModels(){

		LOG.debug("this.cauiPrediction.retrieveCACIModel(null)");
		this.userActionsList = this.getCACIActiveModel();
		//this.cauiPrediction.retrieveCACIModel(null);
		addGlobalMessage("Retriece community model from remote cis", "Retrieving model from DB", FacesMessage.SEVERITY_INFO);

	}

	public void refreshPredictionLog(){

		LOG.debug("this.cauiPrediction.refreshPredictionLoc");

		
		this.predictionLogList = this.getPredictionLog();
		addGlobalMessage("Refreshing prediction log", "xxx", FacesMessage.SEVERITY_INFO);

	}
	
	public void learnCommunityModel(){

		addGlobalMessage("learn community Model", "for id "+i, FacesMessage.SEVERITY_INFO);
		IIdentity cisId = null;
		LOG.debug("discovery started..." );
		this.cauiPrediction.generateNewCommunityModel(cisId);

	}

	public List<String> retrieveCISids (){
		List<String> results = new ArrayList<String>();  
	
		//for (int i = 0; i < 10; i++) {  
			results.add("aaaaa");
			results.add("bbbbb");
			results.add("ccccc");
			LOG.debug("result : "+results);
		//}		
		return  results;
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
		LOG.info("get getCommMngrRef "+this.commMngrRef);
		return this.commMngrRef;
	}


	public void setCommMngrRef(ICommManager commMngrRef) {
		LOG.info("set getCommMngrRef "+this.commMngrRef);
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
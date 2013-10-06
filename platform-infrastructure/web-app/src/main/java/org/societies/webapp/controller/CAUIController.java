package org.societies.webapp.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.webapp.models.CAUIAction;
import org.societies.webapp.models.CAUIActionLog;
import org.societies.webapp.service.UserService;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.util.SerialisationHelper;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;


@ManagedBean(name = "cauiController")
@ViewScoped
public class CAUIController extends BasePageController {

	private static Logger LOG = LoggerFactory.getLogger(CAUIController.class);

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{userService}")
	private UserService userService; 

	@ManagedProperty(value = "#{internalCtxBroker}")
	private ICtxBroker internalCtxBroker;

	@ManagedProperty(value = "#{cauiPrediction}")
	private ICAUIPrediction cauiPrediction;
	
	@ManagedProperty(value = "#{commMngrRef}")
	private ICommManager commMngrRef;


	private String stringProperty;
	private boolean boolProperty;

	private boolean enableUserPrediction = true;
	private boolean enableCommunityPrediction = true;

	//	static int i = 0;

	private List<CAUIAction> userActionsList = new ArrayList<CAUIAction>();
	private List<CAUIAction> communityActionsList = new ArrayList<CAUIAction>();
	private List<CAUIActionLog> predictionLogList = new ArrayList<CAUIActionLog>();
	private String cisId ;

	private String[] cisIdList ;

	public CAUIController() {

		cisIdList = new String[] {};
		LOG.debug(this.getClass().getName() + "constructor instantiated");
	}


	@PostConstruct
	public void init() {

		LOG.debug(this.getClass().getName() + " initialising");

		this.userActionsList = this.getCAUIActiveModel();
		this.communityActionsList = this.getLocalCACIActiveModel();
		this.predictionLogList = this.getPredictionLog();

		//LOG.debug("action records ::"+this.userActionsList);
		//LOG.debug("community records ::"+this.communityActionsList);
		//LOG.debug("predictionLogList ::"+this.predictionLogList);
		//LOG.debug("cisId ::"+this.cisId);
		//LOG.debug("cisIdList ::"+this.cisIdList.length);
	}



	public String[] getCisIdList() {
		return cisIdList;
	}

	public String[] retrieveCisIdList(){

		LOG.debug("retrieveCisIdList :: fetching cis ids ");
		//cisIdList = new String[] {"name1", "name2", "name2", "aaa","bbb"};
		List<CtxEntityIdentifier> entidList = this.cauiPrediction.retrieveMyCIS();

		LOG.debug("retrieveCisIdList :: entidList "+entidList);

		List<String> entidListString = new ArrayList<String>();

		if(!entidList.isEmpty()){

			for(CtxEntityIdentifier entID : entidList){
				String identityString = entID.getOwnerId();

				entidListString.add(identityString);
				LOG.debug("retrieveCisIdList ::entID.toString()  "+entID.toString());
			}			
			this.cisIdList = entidListString.toArray(new String[entidListString.size()]);
		}

		LOG.debug("retrieveCisIdList ::cisIdList "+cisIdList);	
		return cisIdList;
	}




	public void setCisIdList(String[] cisIdList) {
		this.cisIdList = cisIdList;
	}





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
		LOG.debug("setEnableUserPrediction  : "+ bool);
	}

	public boolean getEnableUserPrediction(){
		LOG.debug("getEnableUserPrediction  : "+ this.enableUserPrediction);
		return this.enableUserPrediction ;
	}


	public void updateComUsrPred(){

		LOG.debug("updateComUsrPred  usr : "+ this.enableUserPrediction);
		LOG.debug("updateComUsrPred  comm : "+ this.enableCommunityPrediction);

		this.cauiPrediction.enableCommPrediction(this.enableCommunityPrediction);
		this.cauiPrediction.enableUserPrediction(this.enableUserPrediction);

		//this.enableUserPrediction = this.cauiPrediction.isUserPredictionEnabled();
		//this.enableCommunityPrediction = this.cauiPrediction.isCommunityPredictionEnabled();
	}



	public void setEnableCommunityPrediction(boolean bool){
		this.enableCommunityPrediction = bool;
		LOG.debug("setEnableCommunityPrediction  : "+ bool);
	}

	public boolean getEnableCommunityPrediction(){


		LOG.debug("getEnableCommunityPrediction  : "+ this.enableCommunityPrediction);

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
		LOG.debug("getCAUIActiveModel  : "+ model);
		if(!model.isEmpty()){
			result = convertModel(model);

		}
		return result;
	}

	public List<CAUIAction> getLocalCACIActiveModel(){

		List<CAUIAction> result = new ArrayList<CAUIAction>();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> model = new 	HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>>();


		model = this.cauiPrediction.getCACIActiveModel();
	
		LOG.debug("getCACIActiveModel  : "+ model);
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
				LOG.debug("getPredictionLog  cauiLog : "+ cauiLog);
				result.add(cauiLog);
			}
		}
		LOG.debug("getPredictionLog result : "+ result);
		return result;
	}


	private List<CAUIAction> convertModel(HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> originalModel ){

		List<CAUIAction> result = new ArrayList<CAUIAction>();

		if(!originalModel.isEmpty()){
			for(IUserIntentAction sourceAct : originalModel.keySet()){

				//String sourcActionString = sourceAct.toString();

				String sourcActionString = sourceAct.getparameterName() +"="+ sourceAct.getvalue();
				LOG.debug("convertModel 0 sourcActionString : "+ sourcActionString);

				HashMap<String, Double> targetMap = new HashMap<String, Double>();
				if( originalModel.get(sourceAct) != null) {

					HashMap<IUserIntentAction, Double> targetMapOriginal = originalModel.get(sourceAct);

					for(IUserIntentAction targetActOrig : targetMapOriginal.keySet()){
						String trimedTargetStr = targetActOrig.getparameterName()+"="+targetActOrig.getvalue();
						Double transProb = targetMapOriginal.get(targetActOrig);
						
						DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
						symbols.setDecimalSeparator('.');
												
						DecimalFormat df = new DecimalFormat("#.##",symbols );
						Double transProbTrimmed = Double.parseDouble(df.format(transProb));
						targetMap.put(trimedTargetStr, transProbTrimmed);

					}
					CAUIAction cauiAct = new CAUIAction(sourcActionString, targetMap);
					LOG.debug("convertModel  : "+ cauiAct);
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
		LOG.debug("service ref for cauiDisc "+ this.cauiPrediction);
		LOG.debug("service ref for broker  "+ this.internalCtxBroker);
		this.cauiPrediction.generateNewUserModel();
		LOG.debug("discovery started..." );
		addGlobalMessage("MODEL LEARNING TRIGGERED for user", "click on refresh model button", FacesMessage.SEVERITY_INFO);
	}


	public void refreshUserModels(){

		this.userActionsList = this.getCAUIActiveModel();
		LOG.debug("this.userActionsList :"+this.userActionsList);

		addGlobalMessage("Refreshing user model", "Retrieving model from DB", FacesMessage.SEVERITY_INFO);

	}

	public void refreshCommunityModels(){

		//this.communityActionsList = this.getLocalCACIActiveModel();
		LOG.debug("refreshCommunityModels  1");
		IIdentity identityCisId  = getIIdentity(this.cisId);
		LOG.debug("refreshCommunityModels  2 selected id: "+identityCisId);
		
		CtxAttribute caciAttr = this.cauiPrediction.retrieveCACIModel(identityCisId);
		UserIntentModelData newCACIModelData = null;
		
		LOG.debug("refreshCommunityModels  3 caciAttr id: "+caciAttr.getId());
	
		if(caciAttr.getBinaryValue() != null){
			
			try {
				newCACIModelData = (UserIntentModelData) SerialisationHelper.deserialise(caciAttr.getBinaryValue(), this.getClass().getClassLoader());
				
				if(!newCACIModelData.getActionModel().isEmpty()){
					this.communityActionsList = convertModel(newCACIModelData.getActionModel());
					LOG.debug("refreshCommunityModels  4 newCACIModelData: "+newCACIModelData.getActionModel());
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		LOG.debug("this.communityActionsList :"+this.communityActionsList);
		addGlobalMessage("Refreshing community model", "Retrieving model from DB", FacesMessage.SEVERITY_INFO);
	}

	public void refreshPredictionLog(){

		LOG.debug("this.cauiPrediction.refreshPredictionLoc");


		this.predictionLogList = this.getPredictionLog();
		addGlobalMessage("Refreshing prediction log", "xxx", FacesMessage.SEVERITY_INFO);

	}

	public void learnCommunityModel(){

		LOG.debug("discovery started..." );
		//addGlobalMessage("learn community Model", "for id ", FacesMessage.SEVERITY_INFO);
		LOG.debug("discovery started...learnCommunityModel before:: "+this.cisId );
		
		//FacesContext fc = FacesContext.getCurrentInstance();
		//LOG.debug("discovery started...learnCommunityModel fc:: "+fc );
		//Map<String,String> params = fc.getExternalContext().getRequestParameterMap();
		//LOG.debug("discovery started...learnCommunityModel params:: "+params );
		//String action = params.get("action");
			
		IIdentity identityCisId  = getIIdentity(this.cisId);
		this.cauiPrediction.generateNewCommunityModel(identityCisId);
	}

	
	private IIdentity getOwnerId(){

		IIdentity cssOwnerId = null;
		try {
			final INetworkNode cssNodeId = this.commMngrRef.getIdManager().getThisNetworkNode();

			final String cssOwnerStr = cssNodeId.getBareJid();
			cssOwnerId = this.commMngrRef.getIdManager().fromJid(cssOwnerStr);
			LOG.debug("*** css identity = " + cssOwnerId);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cssOwnerId;
	}


	private IIdentity getIIdentity(String cisIDString){

		IIdentity result = null;
		try {
			LOG.debug("*** getIIdentity this.commMngrRef = " + this.commMngrRef);
			LOG.debug("*** getIIdentity cisIDString = " + cisIDString);
			result = this.commMngrRef.getIdManager().fromJid(cisIDString);
			LOG.debug("***getIIdentity converted identity = " + result);
		} catch (InvalidFormatException e) {
			
			e.printStackTrace();
		}
		return result;
	}
	
	

	public ICtxBroker getInternalCtxBroker() {
		LOG.debug("get internalCtxBroker manager " +internalCtxBroker);
		return internalCtxBroker;
	}

	public void setInternalCtxBroker(ICtxBroker internalCtxBroker) {

		if (internalCtxBroker == null)
			log.debug("setInternalCtxBroker() = null");
		else
			log.debug("setInternalCtxBroker() = " + internalCtxBroker.toString());

		this.internalCtxBroker = internalCtxBroker;
	}



	public ICommManager getCommMngrRef() {
		LOG.debug("get getCommMngrRef "+this.commMngrRef);
		return this.commMngrRef;
	}


	public void setCommMngrRef(ICommManager commMngrRef) {
		LOG.debug("set getCommMngrRef "+this.commMngrRef);
		this.commMngrRef = commMngrRef;
	}


	public ICAUIPrediction getCauiPrediction() {
		LOG.debug("get getCauiPrediction "+this.cauiPrediction);
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
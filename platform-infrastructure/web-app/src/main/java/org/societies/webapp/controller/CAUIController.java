package org.societies.webapp.controller;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
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
		//if (LOG.isDebugEnabled())LOG.debug(this.getClass().getName() + "constructor instantiated");
	}


	@PostConstruct
	public void init() {

		//LOG.debug(this.getClass().getName() + " initialising");

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

		if (LOG.isDebugEnabled())LOG.debug("retrieveCisIdList :: fetching cis ids ");
		//cisIdList = new String[] {"name1", "name2", "name2", "aaa","bbb"};
		List<String> entidListOwn = retrieveOwningCIS();
		List<String> entidListMember = retrieveMemberCIS();
		entidListMember.removeAll(entidListOwn);
		
		if (LOG.isDebugEnabled())LOG.debug("retrieveCisIdList :: entidListOwn "+entidListOwn);
		if (LOG.isDebugEnabled())LOG.debug("retrieveCisIdList :: entidList "+entidListMember);

		List<String> entidListString = new ArrayList<String>();

		if(!entidListOwn.isEmpty()){
			entidListString.add("--CIS I own");
			for(String cisID : entidListOwn){
				//String identityString = entID.getOwnerId();
				entidListString.add(cisID);

			}			

			if (LOG.isDebugEnabled())LOG.debug("retrieveCisIdList ::entidListString  "+entidListString);
		}


		if(!entidListMember.isEmpty()){
			entidListString.add("--CIS I am member");
			for(String cisID : entidListMember){
				//String identityString = entID.getOwnerId();
				entidListString.add(cisID);
			}	
		}

		this.cisIdList = entidListString.toArray(new String[entidListString.size()]);

		if (LOG.isDebugEnabled())LOG.debug("retrieveCisIdList ::cisIdList "+cisIdList);	
		return cisIdList;
	}



	public List<String> retrieveMemberCIS(){

		List<String> cisIDList = new ArrayList<String>();

		List<CtxIdentifier> listMemberOf = new ArrayList<CtxIdentifier>();
		IIdentity cssOwnerId =  getOwnerId();
		try {
			listMemberOf = this.internalCtxBroker.lookup(cssOwnerId, CtxModelType.ASSOCIATION, CtxAssociationTypes.IS_MEMBER_OF).get();
			if(!listMemberOf.isEmpty() ){
				CtxAssociation assoc = (CtxAssociation) this.internalCtxBroker.retrieve(listMemberOf.get(0)).get();
				// a set with css entity id
				Set<CtxEntityIdentifier> entIDSet = assoc.getChildEntities();

				for(CtxEntityIdentifier entId : entIDSet){
					IIdentity cisId = this.commMngrRef.getIdManager().fromJid(entId.getOwnerId());

					//CtxEntityIdentifier commId = this.internalCtxBroker.retrieveCommunityEntityId(cisId).get();
					cisIDList.add(cisId.getBareJid());
				}
			}
		} catch (Exception e) {
			LOG.error("Unable to retrieve CISids that css belongs to " +e.getLocalizedMessage());
		} 

		return cisIDList;
	}



	public List<String> retrieveOwningCIS(){

		List<String> cisIDList = new ArrayList<String>();

		List<CtxIdentifier> listAdminOf = new ArrayList<CtxIdentifier>();
		IIdentity cssOwnerId =  getOwnerId();

		try {
			//listISMemberOf = this.ctxBroker.lookup(this.cssOwnerId, CtxModelType.ASSOCIATION, CtxAssociationTypes.IS_MEMBER_OF).get();
			listAdminOf = this.internalCtxBroker.lookup(cssOwnerId, CtxModelType.ASSOCIATION, CtxAssociationTypes.IS_ADMIN_OF).get();
			if (LOG.isDebugEnabled())LOG.debug("........assoc is admin of ...." +listAdminOf);

			if(!listAdminOf.isEmpty() ){
				CtxAssociation assoc = (CtxAssociation) this.internalCtxBroker.retrieve(listAdminOf.get(0)).get();
				Set<CtxEntityIdentifier> entIDSet = assoc.getChildEntities();

				for(CtxEntityIdentifier entId : entIDSet){
					IIdentity cisId = this.commMngrRef.getIdManager().fromJid(entId.getOwnerId());

					//	CtxEntityIdentifier commId = this.internalCtxBroker.retrieveCommunityEntityId(cisId).get();
					cisIDList.add(cisId.getBareJid());
				}

			}
			if (LOG.isDebugEnabled())LOG.debug("is admin of cis ids : "+cisIDList );
		} catch (Exception e) {
			LOG.error("Unable to retrieve CISids that css belongs to " +e.getLocalizedMessage());
		} 

		return cisIDList;
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
		if (LOG.isDebugEnabled())LOG.debug("setEnableUserPrediction  : "+ bool);
	}

	public boolean getEnableUserPrediction(){
		if (LOG.isDebugEnabled())LOG.debug("getEnableUserPrediction  : "+ this.enableUserPrediction);
		return this.enableUserPrediction ;
	}


	public void updateComUsrPred(){

		if (LOG.isDebugEnabled())LOG.debug("updateComUsrPred  usr : "+ this.enableUserPrediction);
		if (LOG.isDebugEnabled())LOG.debug("updateComUsrPred  comm : "+ this.enableCommunityPrediction);

		this.cauiPrediction.enableCommPrediction(this.enableCommunityPrediction);
		this.cauiPrediction.enableUserPrediction(this.enableUserPrediction);

		//this.enableUserPrediction = this.cauiPrediction.isUserPredictionEnabled();
		//this.enableCommunityPrediction = this.cauiPrediction.isCommunityPredictionEnabled();
	}



	public void setEnableCommunityPrediction(boolean bool){
		this.enableCommunityPrediction = bool;
		if (LOG.isDebugEnabled())LOG.debug("setEnableCommunityPrediction  : "+ bool);
	}

	public boolean getEnableCommunityPrediction(){

		if (LOG.isDebugEnabled())LOG.debug("getEnableCommunityPrediction  : "+ this.enableCommunityPrediction);
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
		if (LOG.isDebugEnabled())LOG.debug("getCAUIActiveModel  : "+ model);
		if(!model.isEmpty()){
			result = convertModel(model);

		}
		return result;
	}

	public List<CAUIAction> getLocalCACIActiveModel(){

		List<CAUIAction> result = new ArrayList<CAUIAction>();
		HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> model = new 	HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>>();


		model = this.cauiPrediction.getCACIActiveModel();

		if (LOG.isDebugEnabled())LOG.debug("getCACIActiveModel  : "+ model);
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
				if (LOG.isDebugEnabled())LOG.debug("getPredictionLog  cauiLog : "+ cauiLog);
				result.add(cauiLog);
			}
		}
		if (LOG.isDebugEnabled())LOG.debug("getPredictionLog result : "+ result);
		return result;
	}


	private List<CAUIAction> convertModel(HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> originalModel ){

		List<CAUIAction> result = new ArrayList<CAUIAction>();

		if(!originalModel.isEmpty()){
			for(IUserIntentAction sourceAct : originalModel.keySet()){

				//String sourcActionString = sourceAct.toString();

				String sourcActionString = sourceAct.getparameterName() +"="+ sourceAct.getvalue();
				if (LOG.isDebugEnabled())LOG.debug("convertModel 0 sourcActionString : "+ sourcActionString);
				Map<String,Serializable> contextAction = new HashMap<String,Serializable>();
				if(sourceAct.getActionContext()!= null){
					contextAction = sourceAct.getActionContext();
				}

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
					CAUIAction cauiAct = new CAUIAction(sourcActionString, targetMap, contextAction);
					if (LOG.isDebugEnabled())LOG.debug("convertModel  : "+ cauiAct);
					result.add(cauiAct);
				}
			}		
		}
		return result;
	}


	public void learnUserModel(){

		//IIdentity cssID = getOwnerId(); 
		//if( getOwnerId() != null) {	cssID = getOwnerId();		}
		if (LOG.isDebugEnabled())LOG.debug("service ref for cauiDisc "+ this.cauiPrediction);
		if (LOG.isDebugEnabled())LOG.debug("service ref for broker  "+ this.internalCtxBroker);
		this.cauiPrediction.generateNewUserModel();
		if (LOG.isDebugEnabled())LOG.debug("discovery started..." );
		addGlobalMessage("MODEL LEARNING TRIGGERED for user", "click on refresh model button", FacesMessage.SEVERITY_INFO);
	}


	public void refreshUserModels(){

		this.userActionsList = this.getCAUIActiveModel();
		if (LOG.isDebugEnabled())LOG.debug("this.userActionsList :"+this.userActionsList);

		addGlobalMessage("Refreshing user model", "Retrieving model from DB", FacesMessage.SEVERITY_INFO);

	}

	public void refreshCommunityModels(){

		//this.communityActionsList = this.getLocalCACIActiveModel();
		if (LOG.isDebugEnabled())LOG.debug("refreshCommunityModels  1");
		
		
		if(this.cisId.equalsIgnoreCase("--CIS I own") || this.cisId.equalsIgnoreCase("--CIS I own") ){
			addGlobalMessage("Refreshing Community Intent Model", "Select a valid CIS id", FacesMessage.SEVERITY_INFO);
			return;
		}
		
		
		
		IIdentity identityCisId  = getIIdentity(this.cisId);
		if (LOG.isDebugEnabled())LOG.debug("refreshCommunityModels  2 selected id: "+identityCisId);

		CtxAttribute caciAttr = this.cauiPrediction.retrieveCACIModel(identityCisId);
		UserIntentModelData newCACIModelData = null;

		if (LOG.isDebugEnabled())LOG.debug("refreshCommunityModels  3 caciAttr id: "+caciAttr.getId());

		if(caciAttr.getBinaryValue() != null){

			try {
				newCACIModelData = (UserIntentModelData) SerialisationHelper.deserialise(caciAttr.getBinaryValue(), this.getClass().getClassLoader());

				if(!newCACIModelData.getActionModel().isEmpty()){
					this.communityActionsList = convertModel(newCACIModelData.getActionModel());
					if (LOG.isDebugEnabled())LOG.debug("refreshCommunityModels  4 newCACIModelData: "+newCACIModelData.getActionModel());
				}

			} catch (Exception e) {
				LOG.error("Exception while refreshing CACI models " +e.getLocalizedMessage());
				e.printStackTrace();
			} 
		}

		if (LOG.isDebugEnabled())LOG.debug("this.communityActionsList :"+this.communityActionsList);
		addGlobalMessage("Refreshing community model", "Retrieving model from DB", FacesMessage.SEVERITY_INFO);
	}

	public void refreshPredictionLog(){

		if (LOG.isDebugEnabled())LOG.debug("this.cauiPrediction.refreshPredictionLoc");
		this.predictionLogList = this.getPredictionLog();
		addGlobalMessage("Refreshing prediction log", "list of last performed and predicted actions", FacesMessage.SEVERITY_INFO);
	}

	public void learnCommunityModel(){

		if(this.cisId.equalsIgnoreCase("--CIS I own") || this.cisId.equalsIgnoreCase("--CIS I own") ){
			addGlobalMessage("Learning new Community Intent Model", "Select a valid CIS id", FacesMessage.SEVERITY_INFO);
			return;
		}
		if (LOG.isDebugEnabled())LOG.debug("discovery started...learnCommunityModel before:: "+this.cisId );
		IIdentity identityCisId  = getIIdentity(this.cisId);
		this.cauiPrediction.generateNewCommunityModel(identityCisId);
	}


	private IIdentity getOwnerId(){

		IIdentity cssOwnerId = null;
		try {
			final INetworkNode cssNodeId = this.commMngrRef.getIdManager().getThisNetworkNode();
			final String cssOwnerStr = cssNodeId.getBareJid();
			cssOwnerId = this.commMngrRef.getIdManager().fromJid(cssOwnerStr);
			if (LOG.isDebugEnabled())LOG.debug("*** css identity = " + cssOwnerId);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cssOwnerId;
	}


	private IIdentity getIIdentity(String cisIDString){

		IIdentity result = null;
		try {
			if (LOG.isDebugEnabled())LOG.debug("*** getIIdentity this.commMngrRef = " + this.commMngrRef);
			if (LOG.isDebugEnabled())LOG.debug("*** getIIdentity cisIDString = " + cisIDString);
			result = this.commMngrRef.getIdManager().fromJid(cisIDString);
			if (LOG.isDebugEnabled())LOG.debug("***getIIdentity converted identity = " + result);
		} catch (InvalidFormatException e) {

			e.printStackTrace();
		}
		return result;
	}



	public ICtxBroker getInternalCtxBroker() {
		//LOG.debug("get internalCtxBroker manager " +internalCtxBroker);
		return internalCtxBroker;
	}

	public void setInternalCtxBroker(ICtxBroker internalCtxBroker) {

		this.internalCtxBroker = internalCtxBroker;
	}



	public ICommManager getCommMngrRef() {
		//LOG.debug("get getCommMngrRef "+this.commMngrRef);
		return this.commMngrRef;
	}


	public void setCommMngrRef(ICommManager commMngrRef) {
		//LOG.debug("set getCommMngrRef "+this.commMngrRef);
		this.commMngrRef = commMngrRef;
	}


	public ICAUIPrediction getCauiPrediction() {
		//LOG.debug("get getCauiPrediction "+this.cauiPrediction);
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
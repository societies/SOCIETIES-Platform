package org.societies.webapp.controller;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.personalisation.model.IAction;
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
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
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

	private final static String newline = "\n";
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
		
		//remove this from constructor
		this.predictionLogList = this.getHistoryLog();

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
		if (LOG.isDebugEnabled())LOG.debug("retrieveCisIdList :: entidListParticipate "+entidListMember);

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
		String cisName = "";
		String cisID = "";
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

	
					
					/*
					List<CtxIdentifier> cisNameList = this.internalCtxBroker.lookup(entId,CtxModelType.ATTRIBUTE,CtxAttributeTypes.NAME).get();
					if(!cisNameList.isEmpty()){
						CtxAttribute cisNameAttr = (CtxAttribute) this.internalCtxBroker.retrieve(cisNameList.get(0)).get();
						cisName = cisNameAttr.getStringValue();
					}
					//	CtxEntityIdentifier commId = this.internalCtxBroker.retrieveCommunityEntityId(cisId).get();
					
					cisID = cisName+"#"+cisId.getBareJid();
					*/
					cisIDList.add(cisId.getBareJid());
				

					
					
					//cisIDList.add(cisId.getBareJid());
				}
			}
		} catch (Exception e) {
			LOG.error("Unable to retrieve CISids that css belongs to " +e.getLocalizedMessage());
		} 
		if (LOG.isDebugEnabled())LOG.debug("retrieveMemberCIS ::  "+cisIDList);
		
		return cisIDList;
	}



	public List<String> retrieveOwningCIS(){

		List<String> cisIDList = new ArrayList<String>();
		String cisName = "";
		String cisID = "";
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
					/*
					List<CtxIdentifier> cisNameList = this.internalCtxBroker.lookup(entId,CtxModelType.ATTRIBUTE,CtxAttributeTypes.NAME).get();
					if(!cisNameList.isEmpty()){
						CtxAttribute cisNameAttr = (CtxAttribute) this.internalCtxBroker.retrieve(cisNameList.get(0)).get();
						cisName = cisNameAttr.getStringValue();
					}
					//	CtxEntityIdentifier commId = this.internalCtxBroker.retrieveCommunityEntityId(cisId).get();
					
					cisID = cisName+"#"+cisId.getBareJid();
					*/
					cisIDList.add(cisId.getBareJid());
				}

			}
			if (LOG.isDebugEnabled())LOG.debug("is admin of cis ids : "+cisIDList );
		} catch (Exception e) {
			LOG.error("Unable to retrieve CISids that css belongs to " +e.getLocalizedMessage());
		} 

		return cisIDList;
	}




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
		addGlobalMessage("Options Submitted", "", FacesMessage.SEVERITY_INFO);
		
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

	/*
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
	 */
	
	
	public List<CAUIActionLog>  getHistoryLog(){

		List<CAUIActionLog> result = new ArrayList<CAUIActionLog>(); 
	//	List<Entry<String, String>> tempResultList = this.cauiPrediction.getPredictionPairLog();
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> results = retrieveHistoryTupleData();
		result = convertHocTuplesToCauiActLog(results);

		if (LOG.isDebugEnabled())LOG.debug("getPredictionLog result : "+ result);
		return result;
	}

	
	
	public Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> retrieveHistoryTupleData(){

		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> results = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
		try {
			results = this.internalCtxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, listOfEscortingAttributeIds, null, null).get();
			//System.out.println(" retrieveHistoryTupleData: " +results);

		}catch (Exception e) {
			LOG.error("Exception thrown while retrieving history of performed user actions "+e.getLocalizedMessage());
			e.printStackTrace();
		}
		return results;
	}

	public List<CAUIActionLog> convertHocTuplesToCauiActLog(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData){

		//String data =""; 
		List<CAUIActionLog> cauiActLog =new ArrayList<CAUIActionLog>();
	
		//int i = 0;
		for(CtxHistoryAttribute ctxHocAttr :mapHocData.keySet()){

			try {
				Date time = ctxHocAttr.getLastUpdated();
				IAction action = (IAction)SerialisationHelper.deserialise(ctxHocAttr.getBinaryValue(), this.getClass().getClassLoader());
				List<CtxHistoryAttribute> escortingAttrList = mapHocData.get(ctxHocAttr);

				CtxHistoryAttribute attr1 = escortingAttrList.get(0);
				CtxHistoryAttribute attr3 = escortingAttrList.get(2);
				CtxHistoryAttribute attr4 = escortingAttrList.get(3);

				//String actionParValLog = action.getparameterName()+"="+ action.getvalue();
				String actionParValLog = action.getparameterName() +"="+ action.getvalue()+"/"+action.getServiceID().getServiceInstanceIdentifier();
				
				HashMap<String,Serializable> hocMap = new HashMap<String,Serializable>();
						
				hocMap.put(attr1.getType(), getValueFromAttr(attr1));
				hocMap.put(attr3.getType(), getValueFromAttr(attr3));
				hocMap.put(attr4.getType(), getValueFromAttr(attr4));
										
				CAUIActionLog actLog = new CAUIActionLog(actionParValLog, "n/a", hocMap, time.toString() );
				
				cauiActLog.add(actLog);
			} catch (Exception e) {
				LOG.error("Exception while trying to conver history attributes to readable string "+e.getLocalizedMessage());
				e.printStackTrace();
			}	
		}
		
		return cauiActLog;
	}

	private String getValueFromAttr(CtxHistoryAttribute attr){

		String result = "";
		
		if(attr.getValueType().equals(CtxAttributeValueType.STRING) && attr.getStringValue()!= null){
			result = attr.getStringValue();
			
			return result;
		} else if (attr.getValueType().equals(CtxAttributeValueType.INTEGER) && attr.getIntegerValue()!= null ){
			Integer intResult = attr.getIntegerValue();
			result = String.valueOf(intResult);
		
			return result;
		}	

		return result;

	}

	private List<CAUIAction> convertModel(HashMap<IUserIntentAction, HashMap<IUserIntentAction, Double>> originalModel ){

		List<CAUIAction> result = new ArrayList<CAUIAction>();

		if(!originalModel.isEmpty()){
			for(IUserIntentAction sourceAct : originalModel.keySet()){

				//String sourcActionString = sourceAct.toString();

				String sourcActionString = sourceAct.getparameterName() +"="+ sourceAct.getvalue()+"/"+sourceAct.getServiceID().getServiceInstanceIdentifier();
				if (LOG.isDebugEnabled())LOG.debug("convertModel 0 sourcActionString : "+ sourcActionString);
				Map<String,Serializable> contextAction = new HashMap<String,Serializable>();
				if(sourceAct.getActionContext()!= null){
					contextAction = sourceAct.getActionContext();
					if (LOG.isDebugEnabled())LOG.debug("convertModel 1 contextActionMap : "+ contextAction);
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

		this.cauiPrediction.generateNewUserModel();
		if (LOG.isDebugEnabled())LOG.debug("discovery started..." );
		addGlobalMessage("USER INTENT MODEL LEARNING TRIGGERED", "click on refresh model button", FacesMessage.SEVERITY_INFO);
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

		//int i = this.cisId.indexOf("#");
		//IIdentity identityCisId  = getIIdentity(this.cisId.substring(i+1));
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
		addGlobalMessage("Retrieving community model", "from context DB", FacesMessage.SEVERITY_INFO);
	}


	public void learnCommunityModel(){

		if(this.cisId.equalsIgnoreCase("--CIS I own") || this.cisId.equalsIgnoreCase("--CIS I own") ){
			addGlobalMessage("Learning new Community Intent Model", "Select a valid CIS id", FacesMessage.SEVERITY_INFO);
			return;
		}
		List<String> ownedCIS = retrieveOwningCIS();

		if(!ownedCIS.contains(this.cisId)){
			addGlobalMessage("Learning new Community Intent Model", "You are not the administrator of this community ", FacesMessage.SEVERITY_INFO);
			return;
		}	

		if (LOG.isDebugEnabled())LOG.debug("discovery started...learnCommunityModel before:: "+this.cisId );

		if(ownedCIS.contains(this.cisId)){
			int i = this.cisId.indexOf("#");
			IIdentity identityCisId  = getIIdentity(this.cisId.substring(i+1));
			this.cauiPrediction.generateNewCommunityModel(identityCisId);
		}
	
		addGlobalMessage("Learning of a new community model started", "Press refresh button afer a few seconds", FacesMessage.SEVERITY_INFO);
	}

	
	
	
	
	public void refreshPredictionLog(){

		if (LOG.isDebugEnabled())LOG.debug("this.cauiPrediction.refreshPredictionLog");
		this.predictionLogList = this.getHistoryLog();
		addGlobalMessage("Retrieving history of performed actions", "last actions", FacesMessage.SEVERITY_INFO);
	}

	
	public void refreshContextAction(){
		
		this.userActionsList = this.getCAUIActiveModel();
		addGlobalMessage("Refreshing user actions and context table", "", FacesMessage.SEVERITY_INFO);
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


	public void setCisIdList(String[] cisIdList) {
		this.cisIdList = cisIdList;
	}

	public String getCisId() {
		return cisId;
	}

	public void setCisId(String cisId) {
		this.cisId = cisId;
	}
	
	public void showMeAMessage() {
		addGlobalMessage("THIS IS CAUI MESSAGE", "Now here's your message!", FacesMessage.SEVERITY_INFO);
	}

	
	
	
	
	
/*
	public void removeHistoryLog(){

		if (LOG.isDebugEnabled())LOG.debug("removeHistoryLog");
		try {
		
			List<CtxIdentifier> hocTuplesIdList = this.internalCtxBroker.lookup(getOwnerId(), CtxModelType.ATTRIBUTE, CtxAttributeTypes.LAST_ACTION).get();
			if(!hocTuplesIdList.isEmpty()){
				
				for (CtxIdentifier id : hocTuplesIdList ){
					List<CtxAttributeIdentifier> escortingList = new ArrayList<CtxAttributeIdentifier>();
					CtxAttributeIdentifier primaryAttrID = (CtxAttributeIdentifier) id ;
					
					if (LOG.isDebugEnabled())LOG.debug("removeHistoryLog for "+primaryAttrID );
					
					this.internalCtxBroker.removeHistoryTuples(primaryAttrID, escortingList).get();	
				}
			}
	
		} catch (Exception e) {
			LOG.error("Exception while removing history records " +e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		addGlobalMessage("Remove history of performed actions", "records removed", FacesMessage.SEVERITY_INFO);
	}
	*/
	
	
}
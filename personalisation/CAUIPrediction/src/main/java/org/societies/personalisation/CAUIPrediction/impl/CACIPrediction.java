/**
 * Copyright (c) 2011, SOCIETIES Consortium
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.personalisation.CAUIPrediction.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.personalisation.model.IAction;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;

/**
 * CAUIPrediction
 * 
 * @author nikosk
 * @created 12-May-2013 7:15:15 PM
 */
public class CACIPrediction {

	private static final Logger LOG = LoggerFactory.getLogger(CACIPrediction.class);

	private ICtxBroker ctxBroker;
	private ICAUITaskManager caciTaskManager;
	private ICommManager commsMgr ;

	static boolean caciPredictionEnabled = true;
	static boolean caciFreshness = false;
	static boolean cacimodelExist = false;
	
	public UserIntentModelData currentCACIModelData;
	
	public CACIPrediction(ICtxBroker ctxBroker, ICommManager commsMgr){

		this.ctxBroker = ctxBroker;
		this.commsMgr = commsMgr;
	}

	public CACIPrediction(ICtxBroker ctxBroker, ICAUITaskManager caciTaskManager, ICommManager commsMgr){

		this.ctxBroker = ctxBroker;
		this.caciTaskManager = caciTaskManager;
		this.commsMgr = commsMgr;
		LOG.debug("inside CACIPrediction ");

		//when css joins a new cis, will automatically register for caci model events.
		try {
			LOG.debug("register for cis join and new community model creation");
			new CommunityJoinMonitor(this.ctxBroker ,this.commsMgr);

		} catch (Exception e) {
			LOG.error("Exception while trying to register for new community join events " +e.getLocalizedMessage());
		}
	}

	/*
	private void setupLocalCaciAttr(){

		CtxAttribute caciModelAttrLocal = null;
		List<CtxIdentifier> caciModelAttrLocalList;
		try {
			// TODO update this with proper lookup method lookup(entID, CtxModelType.ATTRIBUTE, CtxAttributeTypes.CACI_MODEL)
			caciModelAttrLocalList = ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.CACI_MODEL).get();

			if( !caciModelAttrLocalList.isEmpty() ){
				// get locally stored model
				CtxAttribute caciModelAttr = this.ctxBroker.retrieveAttribute((CtxAttributeIdentifier) caciModelAttrLocalList.get(0), false).get();
				if(caciModelAttr.getBinaryValue() != null){
					UserIntentModelData newCACIModelData = (UserIntentModelData) SerialisationHelper.deserialise(caciModelAttr.getBinaryValue(), this.getClass().getClassLoader());
					setCACIActiveModel(newCACIModelData);	
				}
			} else {
				LOG.debug("caci model doesn't exist -- creating empty local Caci Attribute");
				IIdentity localcssID = getOperatorID();
				CtxEntityIdentifier entityID = ctxBroker.retrieveIndividualEntityId(null, localcssID).get();
				caciModelAttrLocal = ctxBroker.createAttribute(entityID, CtxAttributeTypes.CACI_MODEL).get();
				LOG.debug("***CACI constructor ctxBroker.createAttribute= " + caciModelAttrLocal.getId());
			}

		} catch (Exception e) {

			LOG.error("Exception while trying to create CACI CtxAttribute upon component start up" +e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	 */




	public List<IUserIntentAction> getPrediction(IIdentity requestor,
			IAction action){

		//List<IUserIntentAction> predictedActionsList = new ArrayList<IUserIntentAction>();
		// identify performed action in model
		List<IUserIntentAction> results = new ArrayList<IUserIntentAction>();

		String par = action.getparameterName();
		String val = action.getvalue();

		if(currentCACIModelData != null ) 	{
			
			LOG.debug("set latest CACI model " + currentCACIModelData);
			this.caciTaskManager.updateModel(currentCACIModelData);
		}
		
		
		
		LOG.debug("cacimodel to be used for prediction: "+ this.caciTaskManager.getCAUIActiveModel() );
		
		List<IUserIntentAction> actionsList = this.caciTaskManager.retrieveActionsByTypeValue(par, val);
		LOG.debug("1. CACIMODEL TaskManager.retrieveActionsByTypeValue(par, val) " +actionsList);

		if(actionsList.size()>0){

			// improve this to also use context for action identification
			//IUserIntentAction currentAction = actionsList.get(0);

			IUserIntentAction currentAction = findBestMatchingAction(actionsList);

			LOG.debug("2. CACIMODEL currentAction " +currentAction);
			Map<IUserIntentAction,Double> nextActionsMap = this.caciTaskManager.retrieveNextActions(currentAction);	
			//LOG.info("5. nextActionsMap " +nextActionsMap);

			// no context
			if(nextActionsMap.size()>0){
				for(IUserIntentAction nextAction : nextActionsMap.keySet()){
					Double doubleConf = nextActionsMap.get(nextAction);
					doubleConf = doubleConf*100;
					//doubleConf = 70.0;
					nextAction.setConfidenceLevel(doubleConf.intValue());
					//LOG.info("6. nextActionsMap " +nextAction);
					results.add(nextAction);

					LOG.debug(" ****** caci prediction map created "+ results);
				}
			}			
		}

		return results;
	}


	private IUserIntentAction findBestMatchingAction(List<IUserIntentAction> actionList){
		IUserIntentAction bestAction = null;

		HashMap<IUserIntentAction, Integer> actionsScoreMap = new HashMap<IUserIntentAction, Integer>();

		CtxAttribute currentLocation = retrieveOperatorsCtx(CtxAttributeTypes.LOCATION_SYMBOLIC);
		CtxAttribute currentStatus = retrieveOperatorsCtx(CtxAttributeTypes.STATUS);
		//CtxAttribute currentTemp = retrieveOperatorsCtx(CtxAttributeTypes.TEMPERATURE);

		for(IUserIntentAction action : actionList ){

			HashMap<String,Serializable> actionCtx = action.getActionContext();
			int actionMatchScore = 0;

			if( actionCtx != null ){

				actionMatchScore = 0;			

				for(String ctxType : actionCtx.keySet()){
					Serializable ctxValue = actionCtx.get(ctxType);
					if( ctxValue != null){
						if(ctxType.equals(CtxAttributeTypes.LOCATION_SYMBOLIC)&& ctxValue instanceof String){
							String actionLocation = (String) ctxValue;
							//	LOG.info("String context location value :"+ actionLocation);
							if(currentLocation != null){
								if(currentLocation.getStringValue() != null){

									if(currentLocation.getStringValue().equals(actionLocation)) actionMatchScore = actionMatchScore +1;	
								}
							}					

						}
						/*else if(ctxType.equals(CtxAttributeTypes.TEMPERATURE) && ctxValue instanceof Integer ){
					Integer actionTemperature= (Integer) ctxValue;
					LOG.info("Integer context temperature value :"+ actionTemperature);
					if(currentTemp.getIntegerValue().equals(actionTemperature)) actionMatchScore = actionMatchScore +1;
					}*/
						else if(ctxType.equals(CtxAttributeTypes.STATUS) && ctxValue instanceof String ){
							String actionStatus = (String) ctxValue;
							//LOG.info("String context status value :"+ actionStatus);
							if(currentStatus != null ){
								if(currentStatus.getStringValue() != null){
									if( currentStatus.getStringValue().equals(actionStatus)) actionMatchScore = actionMatchScore +1;	
								}
							}
						} else {
							LOG.debug("findBestMatchingAction: context type:"+ctxType +" does not match");
						}
					} 
				}

				actionsScoreMap.put(action, actionMatchScore);
				//System.out.println("actionsScoreMap  " +actionsScoreMap);
			}
		}

		int maxValueInMap=(Collections.max(actionsScoreMap.values()));  // This will return max value in the Hashmap
		for(IUserIntentAction action  : actionsScoreMap.keySet()){

			if(actionsScoreMap.get(action).equals(maxValueInMap)) bestAction = action;
		}

		//LOG.info("best action "+bestAction);

		return bestAction;
	}












	/*
	 * retreive caci model code from CIS context db 
	

	public void retrieveCACIModel(){

		List<CtxEntityIdentifier> commEntIDList = retrieveBelongingCIS();

		LOG.debug("retrieveCACIModel commEntIDList  1 "+commEntIDList);
		//TODO keep local caci model
		//TODO remove remote com to community

		if(!commEntIDList.isEmpty()){

			for(CtxEntityIdentifier cisEntID : commEntIDList){

				LOG.debug("retrieveCACIModel commEntIDList  2 "+commEntIDList);
				try {
					CommunityCtxEntity commEntity = (CommunityCtxEntity) this.ctxBroker.retrieve(cisEntID).get();
					Set<CtxAttribute> caciSet = commEntity.getAttributes(CtxAttributeTypes.CACI_MODEL);
					LOG.debug("retrieveCACIModel commEntIDList  3  commEntity "+commEntity.getId());

					for(CtxAttribute caciAttrCommunity: caciSet){
						LOG.debug("retrieveCACIModel commEntIDList  3  caciAttr "+caciAttrCommunity.getId());
						if(caciAttrCommunity.getBinaryValue() != null){
							//currentCaciModelAttr = caciAttr;

							UserIntentModelData newCACIModelData = (UserIntentModelData) SerialisationHelper.deserialise(caciAttrCommunity.getBinaryValue(), this.getClass().getClassLoader());
							LOG.debug("retrieveCACIModel commEntIDList  4  caciAttr "+newCACIModelData );
							storeCaciModelBD(newCACIModelData);

							setCACIActiveModel(newCACIModelData);	
						}
					}
				} catch (Exception e) {
					LOG.error("Exception while trying to retrieve a CACI model from an existing CIS  " +e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		}
	} 
 */


	/*
	private Boolean getCaciFreshness(CtxAttribute currentCaciModelAttr){

		Boolean isFresh = false;
		if(currentCaciModelAttr.getQuality().getFreshness() < 10000 ){
			isFresh = true;
		}

		return isFresh;
	}
	 */

	public void storeCaciModelDB(UserIntentModelData modelData){

		final INetworkNode cssNodeId = this.commsMgr.getIdManager().getThisNetworkNode();
		final String cssOwnerStr = cssNodeId.getBareJid();
		CtxAttribute attr = null;

		try {
			IIdentity cssOwnerId = this.commsMgr.getIdManager().fromJid(cssOwnerStr);
			CtxEntityIdentifier indiEntId = this.ctxBroker.retrieveIndividualEntityId(null, cssOwnerId).get();

			List<CtxIdentifier> lsCaci = this.ctxBroker.lookup(indiEntId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.CACI_MODEL).get();
			CtxAttributeIdentifier caciModelAttributeId = null;

			if (lsCaci.size()>0) {
				caciModelAttributeId = (CtxAttributeIdentifier) lsCaci.get(0);
				attr = (CtxAttribute) this.ctxBroker.retrieve(caciModelAttributeId).get();
				byte[] binaryModel = SerialisationHelper.serialise(modelData);
				LOG.debug("updating caci model  " +attr.getId() +" model: "+modelData );

				this.ctxBroker.updateAttribute(attr.getId(), binaryModel).get();
			}


		} catch (Exception e) {
			LOG.error("CtxAttribute of type "+CtxAttributeTypes.CACI_MODEL+ "was not updated with caci model "+e.getLocalizedMessage());
			e.printStackTrace();
		} 
	}


	public List<CtxEntityIdentifier> retrieveBelongingCIS(){

		List<CtxEntityIdentifier> commEntIDList = new ArrayList<CtxEntityIdentifier>();

		List<CtxIdentifier> listISMemberOf = new ArrayList<CtxIdentifier>();
		try {
			listISMemberOf = this.ctxBroker.lookup(CtxModelType.ASSOCIATION, CtxAssociationTypes.IS_MEMBER_OF).get();
			LOG.debug(".............listISMemberOf................." +listISMemberOf);

			if(!listISMemberOf.isEmpty() ){
				CtxAssociation assoc = (CtxAssociation) this.ctxBroker.retrieve(listISMemberOf.get(0)).get();
				Set<CtxEntityIdentifier> entIDSet = assoc.getChildEntities();

				for(CtxEntityIdentifier entId : entIDSet){
					IIdentity cisId = this.commsMgr.getIdManager().fromJid(entId.getOwnerId());
					LOG.debug("cis id : "+cisId );
					CtxEntityIdentifier commId = this.ctxBroker.retrieveCommunityEntityId(cisId).get();
					commEntIDList.add(commId);
				}
			}

		} catch (Exception e) {
			LOG.error("Unable to retrieve CISids that css belongs to " +e.getLocalizedMessage());
		} 

		return commEntIDList;
	}

	private CtxAttribute retrieveOperatorsCtx(String type){
		CtxAttribute ctxAttr = null;
		try {

			IndividualCtxEntity operator = this.ctxBroker.retrieveIndividualEntity(getOperatorID()).get();

			Set<CtxAttribute> ctxAttrSet = operator.getAttributes(type);
			if(ctxAttrSet.size()>0){
				List<CtxAttribute>  ctxAttrList = new ArrayList<CtxAttribute> (ctxAttrSet);
				ctxAttr = ctxAttrList.get(0);
			}

		} catch (InterruptedException e) {
			// 
			e.printStackTrace();
		} catch (ExecutionException e) {
			// 
			e.printStackTrace();
		} catch (CtxException e) {
			// 
			e.printStackTrace();
		}

		return ctxAttr;
	}

	public void setCACIActiveModel (UserIntentModelData newCACIModelData){

		if (newCACIModelData != null){
			this.caciTaskManager.updateModel(null);
			this.caciTaskManager.updateModel(newCACIModelData);
			currentCACIModelData = newCACIModelData;
			cacimodelExist = true;		 
			LOG.info("caci model set - actions map: "+newCACIModelData.getActionModel());
		}
	}

	protected IIdentity getOperatorID (){

		final INetworkNode cssNodeId = this.commsMgr.getIdManager().getThisNetworkNode();
		final String cssOwnerStr = cssNodeId.getBareJid();
		IIdentity cssOwnerId = null;

		try {
			cssOwnerId = this.commsMgr.getIdManager().fromJid(cssOwnerStr);
		} catch (InvalidFormatException e) {
			LOG.error("Exception while trying to retrieve operator IIdentity "+ e.getLocalizedMessage());
			e.printStackTrace();
		}		
		return cssOwnerId;
	}
}
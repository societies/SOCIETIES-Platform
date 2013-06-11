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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
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
	private ICAUITaskManager cauiTaskManager;
	private ICommManager commsMgr ;

	static boolean caciPredictionEnabled = true;
	static boolean caciFreshness = false;
	static boolean cacimodelExist = false;

	protected CtxAttribute currentCaciModelAttr;


	public CACIPrediction(ICtxBroker ctxBroker, ICAUITaskManager cauiTaskManager,ICommManager commsMgr){

		this.ctxBroker = ctxBroker;
		this.cauiTaskManager = cauiTaskManager;
		this.commsMgr = commsMgr;
	}

	public CACIPrediction(ICtxBroker ctxBroker, ICommManager commsMgr){

		this.ctxBroker = ctxBroker;
		this.commsMgr = commsMgr;
	}

	public List<IUserIntentAction> getPrediction(IIdentity requestor,
			IAction action){

		List<IUserIntentAction> predictedActionsList = new ArrayList<IUserIntentAction>();

		if(cacimodelExist == true && caciPredictionEnabled == true){

			/*
			if( !getCaciFreshness(currentCaciModelAttr ) ){
				retrieveCACIModel();	
			}else {
				LOG.debug("caci model is fresh and enabled");
			}
			 */

			String par = action.getparameterName();
			String val = action.getvalue();



		} 

		return predictedActionsList;
	}


	/*
	 * retreive caci model code from CIS context db 
	 */

	public void retrieveCACIModel(){

		List<CtxEntityIdentifier> commEntIDList = retrieveBelongingCIS();

		for(CtxEntityIdentifier cisEntID : commEntIDList){
			try {
				CommunityCtxEntity commEntity = (CommunityCtxEntity) this.ctxBroker.retrieve(cisEntID).get();
				Set<CtxAttribute> caciSet = commEntity.getAttributes(CtxAttributeTypes.CACI_MODEL);
				for(CtxAttribute caciAttr: caciSet){

					if(caciAttr.getBinaryValue() != null){
						currentCaciModelAttr = caciAttr;
						UserIntentModelData newCACIModelData = (UserIntentModelData) SerialisationHelper.deserialise(caciAttr.getBinaryValue(), this.getClass().getClassLoader());
						setCACIActiveModel(newCACIModelData);	
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}


	private Boolean getCaciFreshness(CtxAttribute currentCaciModelAttr){

		Boolean isFresh = false;
		if(currentCaciModelAttr.getQuality().getFreshness() < 10000 ){
			isFresh = true;
		}

		return isFresh;
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

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return commEntIDList;
	}


	//************ caci model code



	public void setCACIActiveModel (UserIntentModelData newCACIModelData){

		if (newCACIModelData != null){
			// get a new instance of cauiTaskManager
			//cauiTaskManager.updateModel(newUIModelData);
			cacimodelExist = true;		 
			LOG.info("caci model set - actions map: "+newCACIModelData.getActionModel());
		}
	}


















}
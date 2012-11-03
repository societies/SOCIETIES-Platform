/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
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
package org.societies.context.evaluation.broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides examples for using the internal Context Broker in OSGi. 
 */
@Service
public class CtxBrokerEvaluation {

	
//	private static final String XLS_INPUT_FILE = "realityMining.xls";

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxBrokerEvaluation.class);

	/** The Internal Context Broker service reference. */
	private ICtxBroker internalCtxBroker;
	private ICommManager commMgrService;

	private XlsReader xlsReader;

	HashMap<Integer, List<Integer>> friendsData = new HashMap<Integer, List<Integer>>();
	//HashMap<Integer, List<String>> proximityData = new HashMap<Integer, List<String>>();

	CtxBrokerEvaluation(){

	}

	@Autowired(required=true)
	public CtxBrokerEvaluation(ICtxBroker internalCtxBroker, ICommManager commMgr) throws Exception {

		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");

		this.internalCtxBroker = internalCtxBroker;
		LOG.info("*** CtxBroker service "+this.internalCtxBroker);

		this.commMgrService = commMgr;
		LOG.info("*** commMgrService instantiated "+this.commMgrService);

		this.xlsReader = new XlsReader();
		loadCtxData();
		}

	
	
	private void loadCtxData(){
		
		LOG.info("*** loading friends");
		loadIndiEntitiesFriend();
		
		
		LOG.info("*** loading Lab Proximity");
		loadLabProximity();	
		
		LOG.info("*** loading Outlab Proximity");
		loadOutLabProximity();
		
	}
	
	private void loadLabProximity(){
		
	}
	
	
	private void loadOutLabProximity(){
		
	}
	
	
	
	private void loadIndiEntitiesFriend() {

		if (LOG.isInfoEnabled())
			LOG.info("CtxBrokerEvaluation data "+friendsData);	

		//1. create entities, add friends associations
		friendsData = this.xlsReader.xlsFriendsReader();
		
		try {
		
		// create individual entities
		for( Integer i : friendsData.keySet()){

			if (this.getIndiEntity(i) == null) {
				String identityString = "identity_"+i+"@societies.local";

				IIdentity cssIDx;
				
					cssIDx = this.commMgrService.getIdManager().fromJid(identityString);
			
				IndividualCtxEntity indiEnt = (IndividualCtxEntity) this.internalCtxBroker.createIndividualEntity(cssIDx, CtxEntityTypes.PERSON).get();
				CtxAttribute attributeEvalID = this.internalCtxBroker.createAttribute(indiEnt.getId(), "evaluationID").get();
				attributeEvalID.setIntegerValue(i);
				this.internalCtxBroker.update(attributeEvalID);
				//System.out.println(indiEnt.getId());
			}
		}

		List<CtxIdentifier> allIndiEntityIDList = this.internalCtxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PERSON).get();
		System.out.println("idList: "+allIndiEntityIDList);
		System.out.println("idList size : "+allIndiEntityIDList.size());

		//individual entities created
		LOG.info("individual entities created ");


		// add friend associations
		IndividualCtxEntity indiEntity = null;

		LOG.info("create associations ");
		for(CtxIdentifier indiEntIdentifier : allIndiEntityIDList){

			indiEntity = (IndividualCtxEntity) this.internalCtxBroker.retrieve(indiEntIdentifier).get();

			CtxAttribute attrID = getEvaluationID(indiEntity);

			if(attrID != null){

				List<Integer> idFriendsList = friendsData.get(attrID.getIntegerValue());

				LOG.info("friends for indiEntity "+indiEntity.getId() +" are :"+idFriendsList);

				Set<CtxAssociationIdentifier> friendsAssocIdSet = indiEntity.getAssociations(CtxAssociationTypes.IS_FRIENDS_WITH);
				List<CtxAssociationIdentifier> friendsAssocIdList = new ArrayList<CtxAssociationIdentifier>(friendsAssocIdSet);

				CtxAssociation friendsAssoc = null;
				if(friendsAssocIdSet.size() == 0  ){

					friendsAssoc = this.internalCtxBroker.createAssociation(CtxAssociationTypes.IS_FRIENDS_WITH).get();
					LOG.info("friendsAssoc "+friendsAssoc.getId() +" created ");
				} else {
					friendsAssoc =   (CtxAssociation) this.internalCtxBroker.retrieve(friendsAssocIdList.get(0)).get();
				}

				if(idFriendsList.size() > 0 ){
					friendsAssoc.setParentEntity((CtxEntityIdentifier) indiEntIdentifier);
					for(Integer i : idFriendsList ){
						if(this.getIndiEntity(i) != null){
							IndividualCtxEntity friendIndiEntity = this.getIndiEntity(i);
							friendsAssoc.addChildEntity(friendIndiEntity.getId());
						}
					}
					this.internalCtxBroker.update(friendsAssoc);	
				}
				// end if assoc
			}
		}
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	// attribute type "evaluationID" has value "i"
	// for this i retrieve the respective individual entity
	private IndividualCtxEntity getIndiEntity(Integer i){

		IndividualCtxEntity indiEntityResult = null;

		//HashMap<Integer, List<Integer>> data
		try {
			//List<CtxIdentifier> attrEvalIDs = this.internalCtxBroker.lookup(CtxModelType.ATTRIBUTE,"evaluationID").get();
			List<CtxIdentifier> listIds = this.internalCtxBroker.lookup(CtxModelType.ENTITY,CtxEntityTypes.PERSON).get();

			List<CtxEntityIdentifier> entityCtxId = new ArrayList<CtxEntityIdentifier>();

			for(CtxIdentifier ctxId : listIds){
				entityCtxId.add((CtxEntityIdentifier) ctxId);
			}

			List<CtxEntityIdentifier> entityIdList = internalCtxBroker.lookupEntities(entityCtxId, "evaluationID", i).get();
			if( entityIdList.size() == 1 ) {
				CtxEntityIdentifier indiEntityID = entityIdList.get(0);
				indiEntityResult = (IndividualCtxEntity) this.internalCtxBroker.retrieve(indiEntityID).get();
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
		}

		return indiEntityResult;
	}


	private CtxAttribute getEvaluationID(IndividualCtxEntity indiEntity){

		CtxAttribute attrID = null;

		Set<CtxAttribute> attrsSet = indiEntity.getAttributes("evaluationID");
		List<CtxAttribute> attrListID = new ArrayList<CtxAttribute>(attrsSet);
		if(attrListID.size() > 0  ) attrID = attrListID.get(0);

		return attrID;
	}


	/*
	 * dailyWorkProximity1
	 * dailyWorkProximity2
	
	public HashMap<Integer, List<String>> xlsProximityReader(Workbook w)  {

		// user '1' has proximity with {'2#5,'3#1','4#2'} userId#ProximityLevel
		final HashMap<Integer, List<String>> mapOfProximityData = new HashMap<Integer, List<String>>();

		// 1 is outlab
		Sheet sheet = this.w.getSheet(1);
		Integer key = 0;
		for (int j = 0; j < sheet.getColumns(); j++) {

			List<String> data = new ArrayList<String>();
			key=j;
			String proximityValue;
			for (int i = 0; i < sheet.getRows(); i++) { 
				Cell cell = sheet.getCell(j, i);
				proximityValue = i+"#"+cell.getContents();
				data.add(proximityValue);
			}
		}



		return mapOfProximityData;
	}
 */
}
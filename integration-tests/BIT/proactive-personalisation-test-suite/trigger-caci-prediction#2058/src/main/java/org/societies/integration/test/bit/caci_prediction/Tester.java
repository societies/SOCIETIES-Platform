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
package org.societies.integration.test.bit.caci_prediction;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;

import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUIDiscovery.ICAUIDiscovery;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.CACI.api.CACIDiscovery.ICACIDiscovery;

public class Tester {

	private static Logger LOG = LoggerFactory.getLogger(TestCase2058.class);

	// run test in university's container
	//private IIdentity uniIdentity;
	//private IIdentity emmaIdentity;


	private String uniStringID = "university.ict-societies.eu";
	//emma.ict-societies.eu
	private String emmaStringID= "emma.ict-societies.eu";

	private IndividualCtxEntity emmaEntity;
	private IndividualCtxEntity universityEntity;

	public ICtxBroker ctxBroker;
	public ICommManager commManager;
	public ICisManager cisManager;
	public ICACIDiscovery caciDiscovery;
	public org.societies.api.internal.context.broker.ICtxBroker internalCtxBroker;


	boolean modelExist = false;

	private RequestorService requestorService = null;
	private IIdentity serviceIdentity = null;

	//public IIdentity cisID = null;

	@Before
	public void setUp(){

		this.ctxBroker = TestCase2058.getCtxBroker();
		this.commManager = TestCase2058.getCommMgr();
		this.cisManager = TestCase2058.getCisManager();
		this.caciDiscovery = TestCase2058.getCaciDiscovery();
		this.internalCtxBroker = TestCase2058.getInternalCtxBroker();

		LOG.info("setUp: this.ctxBroker " +this.ctxBroker );
		LOG.info("setUp: this.commManager " +this.commManager );
	}


	/*
	 * scenario test
	 * 1) createCAUIModels
	 * 2) createCIS()
	 * 3) createCACIModel
	 * 4) retrieveCACIModels from members
	 */

	
	@Test	
	public void createCAUIModels(){

		CtxAttribute uniCauiModelAttr = null;
		CtxAttribute emmaCauiModelAttr =  null;

		IIdentity localid = getOwnerId();

		LOG.info("Start testing ...........createCAUIModels " + localid+" "+localid.getJid());
		if(localid.getJid().equals(emmaStringID)) createCAUIEmma();
		if(localid.getJid().equals(uniStringID)) createCAUIUni();
		//retrieveCAUIModels();
	}

	// runs only on uni node
	// create cis and adds emma as member 

	@Ignore
	@Test
	public void createCIS(){

		LOG.info("Start testing ...........createCIS ");

		IIdentity localid = getOwnerId();
		if(localid.getJid().equals(uniStringID)){
			IIdentity cisid = TestCreateCIS();
			LOG.info("CIS created.... ");
			addMember(cisid, getCSSId(emmaStringID) );
			LOG.info("member added.... ");
			//createCACIModel(cisid);
		}
	}

	@Ignore
	@Test
	public void createCACIModel(){

		IIdentity cisId = null;
		List<CtxIdentifier> listISMemberOf;
		LOG.info(".............createCACIModel................." );
		
		try {
			Thread.sleep(5000);
			listISMemberOf = this.internalCtxBroker.lookup(CtxModelType.ASSOCIATION, CtxAssociationTypes.IS_MEMBER_OF).get();
			LOG.info(".............listISMemberOf................." +listISMemberOf);
			if(!listISMemberOf.isEmpty() ){
				CtxAssociation assoc = (CtxAssociation) this.internalCtxBroker.retrieve(listISMemberOf.get(0)).get();
				Set<CtxEntityIdentifier> entIDSet = assoc.getChildEntities();

				for(CtxEntityIdentifier entId : entIDSet){
					cisId = commManager.getIdManager().fromJid(entId.getOwnerId());
					System.out.println("cis id : "+cisId );
				}
				if( cisId!= null){
					LOG.info(".............generateNewCommunityModel.................");
					this.caciDiscovery.generateNewCommunityModel(cisId);
					LOG.info(".............generateNewCommunityModel.................finished");
				}

				Thread.sleep(5000);
				LOG.info("retrieving caci attr ");
				CtxAttribute caciAttr = retrieveCACIAttribute(cisId);
				LOG.info("retrieving caci attr "+caciAttr.getId());

				//LOG.info("retrieving caci attr 2 : " +caciAttr);
				UserIntentModelData caciModel = null;

				if(caciAttr.getBinaryValue() != null) {
					LOG.info(caciAttr.getBinaryValue() + " " + this.getClass().getClassLoader());
					LOG.info("retrieve Model 5 cauiAtt.getBinaryValue() != null .. retrieve Model 5 " );
					caciModel = (UserIntentModelData) SerialisationHelper.deserialise(caciAttr.getBinaryValue(), this.getClass().getClassLoader());
					LOG.info("retrieve Model 7 ... "+ caciModel.getActionModel() );
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
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public void retrieveCACIModels(){

		LOG.info(" retrieveCACIModels .... " );

		UserIntentModelData modelCACIUni = retrieveCAUIAttribute(uniStringID, CtxAttributeTypes.CACI_MODEL);
		LOG.info(" modelCACIUni .... "+modelCACIUni.getActionModel());
		//	UserIntentModelData modelCACIEmma = retrieveCAUIAttribute(emmaStringID, CtxAttributeTypes.CACI_MODEL);
		//	LOG.info(" modelCACIEmma .... "+modelCACIEmma.getActionModel());
		//	Assert.assertEquals(modelCACIUni, modelCACIEmma);
	}



	public void retrieveEmmaCAUIModels(){
		LOG.info(" retrieving emma model " +emmaStringID);
		UserIntentModelData modelEmma = retrieveCAUIAttribute(emmaStringID,CtxAttributeTypes.CAUI_MODEL);
		LOG.info(" model EMMA retrieved : " +modelEmma.getActionModel() );
	}

	public void retrieveCAUIModels(){

		//LOG.info("Start testing ...........retrieveCAUIModels ");
		IIdentity localid = getOwnerId();
		if(localid.getJid().equals(uniStringID)) {

			LOG.info(" retrieving uni model 1 " );
			boolean modelCreated = false;
			//retrieve uni mode
			int i = 0;

			while(!modelCreated){

				try {	
					LOG.info(" retrieving uni model 2 " );
					UserIntentModelData modelUni = retrieveCAUIAttribute(uniStringID, CtxAttributeTypes.CAUI_MODEL);
					Thread.sleep(5000);
					if (modelUni != null){
						LOG.info(" Model CAUI UNI retrieved "+ modelUni.getActionModel() );
						modelCreated = true;
						Assert.assertNotNull(modelUni);

					} else LOG.info(" Model not retrieved/created yet " + i);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			LOG.info(" retrieving emma model " +emmaStringID);
			UserIntentModelData modelEmma = retrieveCAUIAttribute(emmaStringID,CtxAttributeTypes.CAUI_MODEL);
			LOG.info(" model EMMA retrieved : " +modelEmma.getActionModel() );
			Assert.assertNotNull(modelEmma);
		}
	}


	public IIdentity TestCreateCIS(){

		LOG.info(" createCIS : " );
		IIdentity cisID = null;

		try {
			Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria> ();
			LOG.info("*** trying to create cis:");
			ICisOwned cisOwned = this.cisManager.createCis("testCIS13", "cisType", cisCriteria, "nice CIS").get();
			LOG.info("*** cis created: "+cisOwned.getCisId());

			LOG.info("*** cisOwned " +cisOwned);
			LOG.info("*** cisOwned.getCisId() " +cisOwned.getCisId());
			String cisIDString = cisOwned.getCisId();

			cisID = this.commManager.getIdManager().fromJid(cisIDString);

			LOG.info("*** cisID " +cisID);

			LOG.info("*** waiting *****" );
			Thread.sleep(10000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return cisID;
	}	


	public CtxAttribute retrieveCACIAttribute(IIdentity cisID){

		LOG.info("retrieving caci model 1");
		CtxAttribute caciAtt = null;

		try {
			ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
			serviceId1.setIdentifier(new URI("css://nikosk@societies.org/radioService"));
			serviceId1.setServiceInstanceIdentifier("css://nikosk@societies.org/radioService");
			this.requestorService = new RequestorService(serviceIdentity, serviceId1);
			IIdentity localid = getOwnerId();		

			this.requestorService = new RequestorService(localid, serviceId1);

			LOG.info("retrieving caci model 2 " +this.requestorService);

			List<CtxIdentifier> ls = this.ctxBroker.lookup(this.requestorService, cisID, CtxModelType.ATTRIBUTE, CtxAttributeTypes.CACI_MODEL).get();

			LOG.info("retrieving caci model 3 " +ls);

			if (ls.size() > 0) {

				CtxAttributeIdentifier uiModelAttributeId = (CtxAttributeIdentifier) ls.get(0);
				LOG.info("retrieving caci model 4 ");
				caciAtt = (CtxAttribute) this.ctxBroker.retrieve(this.requestorService, uiModelAttributeId).get();
				LOG.info("retrieving caci model 5 " +caciAtt);
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
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return caciAtt;
	}

	/*
	 * model type will be either caui or caci 
	 */

	public UserIntentModelData retrieveCAUIAttribute(String targetID, String modelType){

		LOG.info("retrieveCAUIAttribute" );

		LOG.info("Services: this.ctxBroker " +this.ctxBroker );
		LOG.info("Services: this.commManager " +this.commManager );


		try {
			ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
			serviceId1.setIdentifier(new URI("css://nikosk@societies.org/radioService"));
			serviceId1.setServiceInstanceIdentifier("css://nikosk@societies.org/radioService");
			this.requestorService = new RequestorService(serviceIdentity, serviceId1);
			IIdentity localid = getOwnerId();		

			this.requestorService = new RequestorService(localid, serviceId1);

			LOG.info("retrieveModels ... "+ this.requestorService );

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		UserIntentModelData cauiModel = null;
		LOG.info("retrieve Model 1" );
		List<CtxIdentifier> ls = null;

		try {

			if(targetID.equals(uniStringID)) {
				//	LOG.info("retrieveModels ... "+ this.requestorService );
				//	LOG.info("retrieveModels ... identity "+ getCCSId(targetID) );
				ls = this.ctxBroker.lookup(this.requestorService, getCSSId(targetID), CtxModelType.ATTRIBUTE, modelType.toLowerCase()).get();
				LOG.info("retrieve Model UNI 2" );

			} else if (targetID.equals(emmaStringID)) {
				ls = this.ctxBroker.lookup(this.requestorService, getCSSId(targetID), CtxModelType.ATTRIBUTE, modelType.toLowerCase()).get();
				LOG.info("retrieve Model EMMA 2" );

			} else LOG.info(" ERROR no model for "+targetID  );

			if (ls.size() > 0) {
				CtxAttributeIdentifier uiModelAttributeId = (CtxAttributeIdentifier) ls.get(0);
				CtxAttribute cauiAtt = (CtxAttribute) this.ctxBroker.retrieve(this.requestorService, uiModelAttributeId).get();
				LOG.info("retrieve Model 3 " +cauiAtt.getId() );

				if(cauiAtt != null) {
					LOG.info("cauiAtt != null :: retrieve Model 4 " );
					if(cauiAtt.getBinaryValue() != null) {
						LOG.info(cauiAtt.getBinaryValue() + " " + this.getClass().getClassLoader());

						LOG.info("retrieve Model 5 cauiAtt.getBinaryValue() != null .. retrieve Model 5 " );
						cauiModel = (UserIntentModelData) SerialisationHelper.deserialise(cauiAtt.getBinaryValue(), this.getClass().getClassLoader());
						LOG.info("retrieve Model 6 cauiModel: "+cauiModel );
						LOG.info("retrieve Model 7 ... "+ cauiModel.getActionModel() );
					} else if (cauiAtt.getBinaryValue() == null ) LOG.info("caui attr binary = null");
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

		return cauiModel;
	}


	void createCAUIEmma(){
		LOG.info("Start testing ........... EMMA" );

		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		try {

			serviceId1.setIdentifier(new URI("css://nikosk@societies.org/radioService"));
			serviceId1.setServiceInstanceIdentifier("css://nikosk@societies.org/radioService");

			IAction action1 = new Action(serviceId1, "serviceType1", "setRadio", "on");
			IAction action2 = new Action(serviceId1, "serviceType1", "setVolume", "medium");
			IAction action3 = new Action(serviceId1, "serviceType1", "setTuner", "favoriteChannel1");

			IAction actionRandom1 = new Action(serviceId1, "serviceIdRandom", "random", "xxx");
			IAction actionRandom2 = new Action(serviceId1, "serviceIdRandom", "random", "yyy");
			IAction actionRandom3 = new Action(serviceId1, "serviceIdRandom", "random", "zzz");

			actionsTask1(action1,action2,action3);
			randomAction(actionRandom1);
			actionsTask1(action1,action2,action3);
			randomAction(actionRandom2);
			actionsTask1(action1,action2,action3);
			randomAction(actionRandom3);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} 

	}


	void createCAUIUni(){
		LOG.info("Start testing ........... UNI" );

		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		try {
			serviceId1.setIdentifier(new URI("css://nikosk@societies.org/radioService"));
			serviceId1.setServiceInstanceIdentifier("css://nikosk@societies.org/radioService");

			IAction action1 = new Action(serviceId1, "serviceType1", "setRadio", "on");
			IAction action2 = new Action(serviceId1, "serviceType1", "setVolume", "medium");
			IAction action3 = new Action(serviceId1, "serviceType1", "setTuner", "favoriteChannel1");

			IAction action4 = new Action(serviceId1, "serviceType2", "setDestination", "gasStation");
			IAction action5 = new Action(serviceId1, "serviceType2", "setDestination", "office");
			IAction action6 = new Action(serviceId1, "serviceType2", "getInfo", "traffic");

			IAction actionRandom1 = new Action(serviceId1, "serviceIdRandom", "random", "xxx");
			IAction actionRandom2 = new Action(serviceId1, "serviceIdRandom", "random", "yyy");
			IAction actionRandom3 = new Action(serviceId1, "serviceIdRandom", "random", "zzz");

			actionsTask1(action1,action2,action3);
			randomAction(actionRandom1);
			actionsTask2(action4,action5,action6);
			randomAction(actionRandom2);
			actionsTask1(action1,action2,action3);
			randomAction(actionRandom3);
			actionsTask2(action4,action5,action6);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} 

	}


	public void addMember(IIdentity cisID, 	IIdentity cssMemberID){

		try{
			CtxEntityIdentifier memberEntityID = this.ctxBroker.retrieveIndividualEntityId(this.requestorService, cssMemberID).get();

			CtxEntityIdentifier ctxCommunityEntityIdentifier = this.ctxBroker.retrieveCommunityEntityId(this.requestorService, cisID).get();
			LOG.info("ctxCommunityEntity id : " + ctxCommunityEntityIdentifier.toString());

			CommunityCtxEntity communityEntity = (CommunityCtxEntity) this.ctxBroker.retrieve(this.requestorService,ctxCommunityEntityIdentifier).get();
			LOG.info("ctxCommunityEntity : " + communityEntity);


			LOG.info("ctxCommunityEntity members : " + communityEntity.getMembers());
			LOG.info("ctxCommunityEntity members size : " + communityEntity.getMembers().size());


			// the following lines will be removed with code adding a css member to the cis
			// adding emma to community
			Set<CtxAssociationIdentifier> comAssocIdSet = communityEntity.getAssociations(CtxAssociationTypes.HAS_MEMBERS);
			LOG.info("ctxCommunityEntity members comAssocIdSet : " + comAssocIdSet);
			LOG.info("ctxCommunityEntity members comAssocIdSet size : " + comAssocIdSet.size());

			CtxAssociation hasMembersAssoc = null;	

			if(comAssocIdSet != null ){
				for(CtxAssociationIdentifier assocID : comAssocIdSet){
					hasMembersAssoc = (CtxAssociation) this.ctxBroker.retrieve(this.requestorService,assocID).get();	
					LOG.info("hasMembersAssoc getChildEntities: " + hasMembersAssoc.getChildEntities());
					LOG.info("hasMembersAssoc size: " + hasMembersAssoc.getChildEntities().size());
					LOG.info("hasMembersAssoc getParentEntity: " + hasMembersAssoc.getParentEntity());

					//CtxEntityIdentifier emmaEntityID = this.ctxBroker.retrieveIndividualEntityId(null,this.cssIDEmma).get();
					hasMembersAssoc.addChildEntity(memberEntityID);

					//TODO is this necessary?
					//CtxEntityIdentifier uniEntityID = this.ctxBroker.retrieveIndividualEntityId(null, this.cssIDUniversity).get();
					//hasMembersAssoc.addChildEntity(uniEntityID);
					hasMembersAssoc = (CtxAssociation) this.ctxBroker.update(this.requestorService, hasMembersAssoc).get();
				}
			}
			CommunityCtxEntity communityEntityUpdated = (CommunityCtxEntity) this.ctxBroker.retrieve(this.requestorService, ctxCommunityEntityIdentifier).get();
			LOG.info("Updated ctxCommunityEntity : " + communityEntityUpdated.getMembers());
			LOG.info("Updated ctxCommunityEntity members size : " + communityEntityUpdated.getMembers().size());

			//Assert.assertEquals(2,communityEntityUpdated.getMembers().size());
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
	}
	/*

	@Test
	public void TestPerformOnDemandPrediction() {

		try {	
			LOG.info("TestPerformOnDemandPrediction : waiting 9000 for model creation ");
			Thread.sleep(9000);

			IIdentity cssOwnerId = getOwnerId();

			ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
			serviceId.setIdentifier(new URI("css://nikosk@societies.org/radioService"));
			serviceId.setServiceInstanceIdentifier("css://nikosk@societies.org/radioService");

			// this action simulates an action performed by the user 
			IAction actionRadio1 = new Action(serviceId, "serviceType1", "setRadio", "on");
			printOperatorAttr();
			LOG.info("A action performed :  "+ actionRadio1 );

			List<IUserIntentAction> actionList = TestCase2058.cauiPrediction.getPrediction(cssOwnerId, actionRadio1).get();
			LOG.info("B List of predicted actions :  "+  actionList );

			if(actionList.size()>0){
				IUserIntentAction predictedAction = actionList.get(0);
				String parName = predictedAction.getparameterName();
				String value = predictedAction.getvalue();

				LOG.info("C CAUI PREDICTION perform prediction :"+ predictedAction);
				Assert.assertEquals("setVolume", parName);
				Assert.assertEquals("medium", value);

				HashMap<String, Serializable> context = predictedAction.getActionContext();

				if(context != null){
					LOG.info("predicted action cotnext :"+ context);	
					//LOG.info("predicted action cotnext size :"+ context.size());
				} else {
					LOG.info("predicted action cotnext is null");
				}
				//TODO fix broker set type method

				if(context.get(CtxAttributeTypes.LOCATION_SYMBOLIC)!= null){
					String location = (String) context.get(CtxAttributeTypes.LOCATION_SYMBOLIC);
					LOG.info("String context location value :"+ location);
					Assert.assertEquals("Home-Parking", location);
				}

				if(context.get(CtxAttributeTypes.STATUS)!= null){
					String status = (String) context.get(CtxAttributeTypes.STATUS);
					LOG.info("String context status value :"+ status);
					//Assert.assertEquals("driving", status);
				}

			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	 */

	//********************************************
	//           helper classes 
	//******************************************** 
	private void randomAction (IAction action){

		IIdentity cssOwnerId = getOwnerId();

		//setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "randomLocation");
		//setContext(CtxAttributeTypes.TEMPERATURE, new Integer(300));
		//setContext(CtxAttributeTypes.STATUS, "randomStatus");

		TestCase2058.uam.monitor(cssOwnerId, action);
		try {
			Thread.sleep(12000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}


	private void actionsTask1 (IAction action1, IAction action2, IAction action3){

		try {
			IIdentity cssOwnerId = getOwnerId();
			//setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "Home-Parking");
			//setContext(CtxAttributeTypes.TEMPERATURE, new Integer(30));
			//setContext(CtxAttributeTypes.STATUS, "driving");

			Date date= new Date();
			LOG.info("monitor action1 "+action1 + " time "+date.getTime());

			TestCase2058.uam.monitor(cssOwnerId, action1);
			Thread.sleep(5000);

			//setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "Home-Parking");
			//setContext(CtxAttributeTypes.TEMPERATURE, new Integer(30));
			//setContext(CtxAttributeTypes.STATUS, "driving");

			date= new Date();
			LOG.info("monitor action2 "+action2 + " time "+date.getTime());
			TestCase2058.uam.monitor(cssOwnerId, action2);
			Thread.sleep(5000);

			//setContext(CtxAttributeTypes.LOCATION_SYMBOLIC,"Home-Parking");
			//setContext(CtxAttributeTypes.TEMPERATURE,  new Integer(30));
			//setContext(CtxAttributeTypes.STATUS, "driving");

			date= new Date();
			LOG.info("monitor action3 "+action3 + " time "+date.getTime());
			TestCase2058.uam.monitor(cssOwnerId, action3);
			Thread.sleep(5000);

		} catch  (InterruptedException e1) {
			e1.printStackTrace();
		}
	}


	private void actionsTask2 (IAction action4, IAction action5, IAction action6){

		IIdentity cssOwnerId = getOwnerId();
		//IAction action4 = new Action(serviceId2, "serviceType2", "setDestination", "gasStation");
		//IAction action5 = new Action(serviceId2, "serviceType2", "setDestination", "office");
		//IAction action6 = new Action(serviceId2, "serviceType2", "getInfo", "traffic");
		try {
			//setContext(CtxAttributeTypes.LOCATION_SYMBOLIC,"High_way");
			//setContext(CtxAttributeTypes.TEMPERATURE,  new Integer(22));
			//setContext(CtxAttributeTypes.STATUS, "driving");

			Date date= new Date();
			LOG.info("monitor action4 "+action4 + " time "+date.getTime());
			TestCase2058.uam.monitor(cssOwnerId, action4);
			Thread.sleep(5000);

			//setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "Gas_station");
			//setContext(CtxAttributeTypes.TEMPERATURE,  new Integer(28));
			//setContext(CtxAttributeTypes.STATUS, "stopped");

			date= new Date();
			LOG.info("monitor action5 "+action5 + " time "+date.getTime());
			TestCase2058.uam.monitor(cssOwnerId, action5);
			Thread.sleep(5000);

			//setContext(CtxAttributeTypes.LOCATION_SYMBOLIC,"high_way_junction");
			//setContext(CtxAttributeTypes.TEMPERATURE,  new Integer(30));
			//setContext(CtxAttributeTypes.STATUS, "driving");

			date= new Date();
			LOG.info("monitor action6 "+action6 + " time "+date.getTime());
			TestCase2058.uam.monitor(cssOwnerId, action6);
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}


	private void actionsTask3 (IAction action7, IAction action8){

		try {
			IIdentity cssOwnerId = getOwnerId();

			//setContext(CtxAttributeTypes.LOCATION_SYMBOLIC,"office_parking");
			//setContext(CtxAttributeTypes.TEMPERATURE,  new Integer(22));
			//setContext(CtxAttributeTypes.STATUS, "stopped");

			Date date= new Date();
			LOG.info("monitor action7 "+action7 + " time "+date.getTime());
			TestCase2058.uam.monitor(cssOwnerId, action7);

			date= new Date();
			Thread.sleep(5000);
			LOG.info("monitor action8 "+action8 + " time "+date.getTime());
			TestCase2058.uam.monitor(cssOwnerId, action8);

			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}

	/*

	private CtxAttribute setContext(String type, Serializable value){

		IIdentity cssOwnerId = getOwnerId();
		CtxAttribute attr = null; 
		try {
			IndividualCtxEntity operator = TestCase2058.ctxBroker.retrieveIndividualEntity(cssOwnerId).get();
			Set<CtxAttribute> ctxAttrSet = operator.getAttributes(type);
			if(ctxAttrSet.size()>0 ){
				ArrayList<CtxAttribute> ctxAttrList = new ArrayList<CtxAttribute>(ctxAttrSet);	
				attr = ctxAttrList.get(0);
				attr = TestCase2058.ctxBroker.updateAttribute(attr.getId(), value).get();
			} else {
				attr = TestCase2058.ctxBroker.createAttribute(operator.getId(), type).get();
				attr = TestCase2058.ctxBroker.updateAttribute(attr.getId(),value).get();
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
		LOG.info("ctxAttr of type: "+attr.getType()+" set to value: "+attr.getStringValue());

		return attr;
	}


	 */

	/*
	private void printOperatorAttr() {


		IIdentity cssOwnerId = getOwnerId();
		try {
			final INetworkNode cssNodeId = TestCase2058.commMgr.getIdManager().getThisNetworkNode();
			//LOG.info("*** cssNodeId = " + cssNodeId);
			final String cssOwnerStr = cssNodeId.getBareJid();

			cssOwnerId = TestCase2058.commMgr.getIdManager().fromJid(cssOwnerStr);
			IndividualCtxEntity operator = TestCase2058.ctxBroker.retrieveIndividualEntity(cssOwnerId).get();

			System.out.println("operator: "+operator);
			Set<CtxAttribute> attrSet = operator.getAttributes();
			//System.out.println("operator attrs : "+attrSet);
			for(CtxAttribute attrs: attrSet){
				System.out.println("attr type: "+attrs.getType());
				if(attrs.getStringValue() != null) System.out.println(" value "+attrs.getStringValue());
			}
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	}
	 */

	protected void printHocTuplesDB(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults){

		LOG.info("printing Tuples");
		int i = 0;
		for (CtxHistoryAttribute primary : tupleResults.keySet()){
			try {
				IAction action = (IAction)SerialisationHelper.deserialise(primary.getBinaryValue(),this.getClass().getClassLoader());
				LOG.info(i+ " action name: "+action.getparameterName()+" action value: "+action.getvalue()+ " action service "+action.getServiceID().getIdentifier());
				for(CtxHistoryAttribute escortingAttr: tupleResults.get(primary)){
					String result = getValue(escortingAttr);
					LOG.info("escording attribute type: "+escortingAttr.getType()+" value:"+result);
				}
				i++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected String getValue(CtxHistoryAttribute attribute){

		String result = "";
		if (attribute.getStringValue()!=null) {
			result = attribute.getStringValue();
			return result;             			
		}
		else if(attribute.getIntegerValue()!=null) {
			Integer valueInt = attribute.getIntegerValue();
			result = valueInt.toString();
			return result; 
		} else if (attribute.getDoubleValue()!=null) {
			Double valueDouble = attribute.getDoubleValue();
			result = valueDouble.toString();  			
			return result; 
		} 
		return result; 
	}


	private IIdentity getOwnerId(){

		IIdentity cssOwnerId = null;
		try {
			final INetworkNode cssNodeId = TestCase2058.commMgr.getIdManager().getThisNetworkNode();
			//LOG.info("*** cssNodeId = " + cssNodeId);
			final String cssOwnerStr = cssNodeId.getBareJid();
			cssOwnerId = TestCase2058.commMgr.getIdManager().fromJid(cssOwnerStr);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cssOwnerId;
	}

	private IIdentity getCSSId(String targetStringID ){

		IIdentity cssOwnerId = null;
		try {
			//final INetworkNode cssNodeId = TestCase2058.commMgr.getIdManager().getThisNetworkNode();
			LOG.info("*** targetStringID = " + targetStringID);
			//final String cssOwnerStr = cssNodeId.getBareJid();
			cssOwnerId = TestCase2058.commMgr.getIdManager().fromJid(targetStringID);

			LOG.info("*** IIdentity = " + cssOwnerId);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cssOwnerId;
	}



	private CtxAttribute updateCAUIAttr(IIdentity identity, UserIntentModelData value){

		CtxAttribute attributeUpdated = null;
		List<CtxIdentifier> attributeList;
		//List<CtxIdentifier> emmaInterestList = this.ctxBroker.lookup(null, this.cssIDEmma,CtxModelType.ATTRIBUTE,CtxAttributeTypes.INTERESTS).get();
		try {
			attributeList = this.ctxBroker.lookup(this.requestorService, identity, CtxModelType.ATTRIBUTE, CtxAttributeTypes.CAUI_MODEL).get();
			CtxAttribute attribute = null;
			LOG.info("the attributeList size is aaaaa:"+attributeList.size()); 
			if( attributeList.size() == 0){
				LOG.info("CAUI attribute doesn't exist... creating for :" +identity.getJid() );

				CtxEntityIdentifier entityID = this.ctxBroker.retrieveIndividualEntityId(this.requestorService,identity).get();
				attribute = this.ctxBroker.createAttribute(this.requestorService, entityID, CtxAttributeTypes.CAUI_MODEL).get();

			} else {
				LOG.info("CAUI attribute exist  for :" +identity.getJid() );

				attribute = (CtxAttribute) this.ctxBroker.retrieve(this.requestorService, attributeList.get(0)).get();
				//attribute = (CtxAttribute) this.ctxBroker.retrieveAttribute((CtxAttributeIdentifier)attributeList.get(0), false).get();
				LOG.info("CAUI attribute retrieved 1:" +attribute.getId() );
			}
			LOG.info("CAUI attribute retrieved 2:" +identity.getJid());

			byte [] cauiValue = SerialisationHelper.serialise(value);
			attribute.setBinaryValue(cauiValue);
			attributeUpdated = (CtxAttribute) this.ctxBroker.update(this.requestorService, attribute).get();


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
		}

		return attributeUpdated;
	}








	/*

	private CtxAttribute updateIntegerAttr(IIdentity identity, String attributeType, Binary value){

		CtxAttribute attributeUpdated = null;
		List<CtxIdentifier> attributeList;
		//List<CtxIdentifier> emmaInterestList = this.ctxBroker.lookup(null, this.cssIDEmma,CtxModelType.ATTRIBUTE,CtxAttributeTypes.INTERESTS).get();
		try {
			attributeList = this.ctxBroker.lookup(this.requestorService,this.cssIDEmma, CtxModelType.ATTRIBUTE,attributeType).get();
			CtxAttribute attribute = null;
			LOG.info("the attributeList size is aaaaa:"+attributeList.size());
			if( attributeList.size() == 0){

				CtxEntityIdentifier entityID = this.ctxBroker.retrieveIndividualEntityId(identity).get();
				attribute = this.ctxBroker.createAttribute(entityID, attributeType).get();

			} else {
				LOG.info("The attribute is :" + attributeList.get(0));
				attribute = (CtxAttribute) this.ctxBroker.retrieve(attributeList.get(0)).get();
				//attribute = (CtxAttribute) this.ctxBroker.retrieveAttribute((CtxAttributeIdentifier)attributeList.get(0), false).get();
				LOG.info("The attribute 2 is :" + attribute);
			}
			attribute.setIntegerValue(value);
			attributeUpdated = (CtxAttribute) this.ctxBroker.update(attribute).get();

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

		return attributeUpdated;
	}
	 */

}
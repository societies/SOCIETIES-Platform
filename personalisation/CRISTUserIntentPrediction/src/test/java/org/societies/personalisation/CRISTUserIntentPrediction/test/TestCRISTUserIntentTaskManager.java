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
package org.societies.personalisation.CRISTUserIntentPrediction.test;


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Test;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.personalisation.model.Action;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CRISTUserIntentDiscovery.impl.CRISTUserIntentDiscovery;
import org.societies.personalisation.CRISTUserIntentPrediction.impl.CRISTUserIntentPrediction;
import org.societies.personalisation.CRISTUserIntentTaskManager.impl.CRISTUserIntentTaskManager;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.model.PersonalisationTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;


/**
 * This is a JUnit 4 test for CRISTUserIntentTastManager's Community related methods:
 * public CtxAttributeIdentifier createCommunityCRISTModel(IIdentity cisId)
 * public CtxAttributeIdentifier updateCommunityCRISTModel(IIdentity cisId)
 * public void updateIndividualCRISTModelWithCommunity(IIdentity cisId)
 * 
 * @author Zhiyong YU
 * @created 3-May-2012 7:15:15 PM
 */


public class TestCRISTUserIntentTaskManager {

	private static ICtxBroker ctxBroker;
	private static IIdentity userId;
	
	@Autowired
	private ICisManager cisManager;

	
	public TestCRISTUserIntentTaskManager() {

	}


	@Test
	public void test() {
		try {
			String stringId = "zhiyong@societies.org";
			CtxEntityIdentifier entityId = new CtxEntityIdentifier(stringId, "testEntity", new Long(123456));
			CtxAttributeIdentifier statusId = new CtxAttributeIdentifier(entityId, CtxAttributeTypes.STATUS, new Long(123456));
			CtxAttributeIdentifier locationId = new CtxAttributeIdentifier(entityId, CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(123456));
			CtxAttributeIdentifier gpsId = new CtxAttributeIdentifier(entityId, CtxAttributeTypes.LOCATION_COORDINATES, new Long(123456));
			CtxAttribute status = new CtxAttribute(statusId);
			CtxAttribute location = new CtxAttribute(locationId);
			CtxAttribute gps = new CtxAttribute(gpsId);
			
			userId = new MockIdentity(IdentityType.CSS, "zhiyong", "societies.org");
			IndividualCtxEntity personEntity = new IndividualCtxEntity(entityId);	
			Future<IndividualCtxEntity> personEntityFuture = new AsyncResult<IndividualCtxEntity>(personEntity);
			personEntity.addAttribute(status);
			personEntity.addAttribute(location);
			personEntity.addAttribute(gps);

			CRISTUserIntentPrediction cristPredictor = new CRISTUserIntentPrediction();
			CRISTUserIntentTaskManager cristMgr = new CRISTUserIntentTaskManager();
			CRISTUserIntentDiscovery cristDisc = new CRISTUserIntentDiscovery();
			cristPredictor.setCristTaskManager(cristMgr);
			ctxBroker = mock(ICtxBroker.class);
			cristMgr.setCtxBroker(ctxBroker);
			cristMgr.setCristDiscovery(cristDisc);
			
			when(ctxBroker.retrieveIndividualEntity(userId)).thenReturn(personEntityFuture);
			when(ctxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION,
					new ArrayList<CtxAttributeIdentifier>(), null, null)).thenReturn(null);		
			when(ctxBroker.retrieve(statusId)).thenReturn(new AsyncResult<CtxModelObject>(status));
			when(ctxBroker.retrieve(locationId)).thenReturn(new AsyncResult<CtxModelObject>(location));
			when(ctxBroker.retrieve(gpsId)).thenReturn(new AsyncResult<CtxModelObject>(gps));
			
			String cisString = "testCommunity";
			IIdentity cisId = new MockIdentity(IdentityType.CIS, cisString, "societies.org");
			CtxEntityIdentifier communityCtxEntityId = new CtxEntityIdentifier(cisString, CtxEntityTypes.COMMUNITY, new Long(666666));
			CommunityCtxEntity communityCtxEntity = new CommunityCtxEntity(communityCtxEntityId);
			when(ctxBroker.retrieveCommunityEntityId(cisId)).thenReturn(new AsyncResult<CtxEntityIdentifier>(communityCtxEntityId));
			when(ctxBroker.createEntity(cisId, CtxEntityTypes.COMMUNITY)).thenReturn(new AsyncResult<CtxEntity>(communityCtxEntity));
			CtxAttributeIdentifier communityCristModelCtxAttrId = new CtxAttributeIdentifier(communityCtxEntityId, CtxAttributeTypes.CRIST_MODEL, new Long(555555));
			CtxAttribute communityCristModelCtxAttr = new CtxAttribute(communityCristModelCtxAttrId);
			when(ctxBroker.createAttribute(communityCtxEntityId, CtxAttributeTypes.CRIST_MODEL)).thenReturn(new AsyncResult<CtxAttribute>(communityCristModelCtxAttr));
			when(ctxBroker.retrieve(communityCristModelCtxAttrId)).thenReturn(new AsyncResult<CtxModelObject>(communityCristModelCtxAttr));
			//ctxBroker.lookup
			List<CtxIdentifier> attrIds = new ArrayList<CtxIdentifier>();
			attrIds.add(communityCristModelCtxAttrId);
			when(ctxBroker.lookup(cisId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.CRIST_MODEL)).thenReturn(new AsyncResult<List<CtxIdentifier>>(attrIds));
			
			//ctxBroker.update --- not able to mock
			when(ctxBroker.update(communityCristModelCtxAttr)).thenReturn(new AsyncResult<CtxModelObject>(communityCristModelCtxAttr));
			
			IInternalPersonalisationManager mockPersoMgr = mock(IInternalPersonalisationManager.class);
			cristMgr.setPersoMgr(mockPersoMgr);
			doNothing().when(mockPersoMgr).registerForContextUpdate(userId, PersonalisationTypes.CRISTIntent, statusId);
			doNothing().when(mockPersoMgr).registerForContextUpdate(userId, PersonalisationTypes.CRISTIntent, locationId);
			doNothing().when(mockPersoMgr).registerForContextUpdate(userId, PersonalisationTypes.CRISTIntent, gpsId);

			
			
			
			
			ServiceResourceIdentifier serviceId_MyTV = new ServiceResourceIdentifier();
			serviceId_MyTV.setIdentifier(new URI("http://testService_MyTV"));
			ServiceResourceIdentifier serviceId_checkin = new ServiceResourceIdentifier();
			serviceId_checkin.setIdentifier(new URI("http://testService_checkin"));


			
			System.out.println("Start to input mock history: ");
			
			location.setStringValue("RoomA"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "1"));
			Thread.sleep(100);//0
			
			location.setStringValue("RoomB"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "2"));
			Thread.sleep(100);//1
			
			location.setStringValue("RoomA"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "1"));
			Thread.sleep(100);//2
			
			location.setStringValue("RoomB"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "2"));
			Thread.sleep(100);//3
			
			location.setStringValue("RoomA"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "1"));
			Thread.sleep(100);//4
			
			location.setStringValue("RoomB"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "2"));
			Thread.sleep(100);//5		
			
			location.setStringValue("RoomA"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "1"));
			Thread.sleep(100);//6
			
			location.setStringValue("RoomB"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "2"));
			Thread.sleep(100);//7
			
			location.setStringValue("RoomA"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "1"));
			Thread.sleep(100);//8
			
			location.setStringValue("RoomB"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "2"));
			Thread.sleep(100);//9
			
			location.setStringValue("RoomA"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "2"));
			Thread.sleep(100);//0
			
			location.setStringValue("RoomB"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "1"));
			Thread.sleep(100);//1
			
			location.setStringValue("RoomA"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "2"));
			Thread.sleep(100);//2
			
			location.setStringValue("RoomB"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "1"));
			Thread.sleep(100);//3
			
			location.setStringValue("RoomA"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "2"));
			Thread.sleep(100);//4
			
			location.setStringValue("RoomB"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "1"));
			Thread.sleep(100);//5		
			
			location.setStringValue("RoomA"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "2"));
			Thread.sleep(100);//6
			
			location.setStringValue("RoomB"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "1"));
			Thread.sleep(100);//7
			
			location.setStringValue("RoomA"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "2"));
			Thread.sleep(100);//8
			
			location.setStringValue("RoomB"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "1"));
			Thread.sleep(100);//9
			
			
			//testing
			location.setStringValue("RoomA"); cristPredictor.getCRISTPrediction(userId, location);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "2"));
			Thread.sleep(100);//	
			
			
			cristMgr.displayHistoryList();
			cristMgr.displayIntentModel();
			
			
			
			
			//------------------------------------
			// create a cis
			//...
//			Future<ICisOwned> testCISFuture = cisManager.createCis("TestCIS_Name", "TestCIS_Type", null, "This is a test CIS.");//set null <= Hashtable<String, MembershipCriteria> cisCriteria
// 			testCISFuture.get().getCisId()			
			
			
			CtxAttributeIdentifier ctxAttributeId = cristMgr.createCommunityCRISTModel(cisId);
			// check the result
			LinkedHashMap<String, Integer> communityCRISTModel = cristMgr.retrieveCommunityCRISTModel(ctxAttributeId);
			System.out.println("testing createCommunityCRISTModel ...");
			System.out.println(communityCRISTModel.toString());
			Assert.assertTrue(communityCRISTModel != null);
			

			 

			 
			
			// join a cis OR be added by the owner of a cis
			//...
			
			ctxAttributeId = cristMgr.updateCommunityCRISTModel(cisId);
			// check the result
			communityCRISTModel = cristMgr.retrieveCommunityCRISTModel(ctxAttributeId);
			System.out.println("testing updateCommunityCRISTModel ...");
			System.out.println(communityCRISTModel.toString());
			Assert.assertTrue(communityCRISTModel != null);
			
			
			cristMgr.updateIndividualCRISTModelWithCommunity(cisId);
			// check the result
			System.out.println("testing updateIndividualCRISTModelWithCommunity ...");
			cristMgr.displayIntentModel();

			


			
			

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	
/*	//for Autowired
	public ICisManager getCisManager() {
		return cisManager;
	}
	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}*/


}
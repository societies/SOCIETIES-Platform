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

package org.societies.personalisation.CRISTUserIntentPrediction.test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Test;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.Action;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.CRISTUserIntentDiscovery.impl.CRISTUserIntentDiscovery;
import org.societies.personalisation.CRISTUserIntentPrediction.impl.CRISTUserIntentPrediction;
import org.societies.personalisation.CRISTUserIntentTaskManager.impl.CRISTUserIntentTaskManager;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.model.PersonalisationTypes;
import org.springframework.scheduling.annotation.AsyncResult;


/**
 * This is a JUnit 4 test for CRISTUserIntentPrediction's methods:
 * public Future<List<CRISTUserAction>> getCRISTPrediction(IIdentity entityID,
 *			CtxAttribute ctxAttribute)
 * public Future<List<CRISTUserAction>> getCRISTPrediction(IIdentity entityID,
 *			IAction action)
 * public Future<CRISTUserAction> getCurrentUserIntentAction(
 *			IIdentity ownerID, ServiceResourceIdentifier serviceID, String parameterName)
 * @author Zhiyong YU
 * @created 3-May-2012 7:15:15 PM
 */


public class TestCRISTUserIntentPrediction {

	private static ICtxBroker ctxBroker;
	private static IIdentity userId;


	
	public TestCRISTUserIntentPrediction() {

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
			
			
			
			

/*			
			//status.setStringValue("busy"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("home"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "volume", "mute"));
			Thread.sleep(100);//0
			
			//status.setStringValue("idle"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("home"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "volume", "unmute"));
			Thread.sleep(100);//1
			
	
			status.setStringValue("busy"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("home"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "volume", "mute"));
			Thread.sleep(100);//2
			
			//status.setStringValue("busy"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("home"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "3"));
			Thread.sleep(100);//3
			
			status.setStringValue("idle"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("outdoor"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue("48.9, 2.33"); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_checkin, "checkinService", "switch", "on"));
			Thread.sleep(100);//4
			
			status.setStringValue("idle"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("outdoor"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue("48.9, 2.33"); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_checkin, "checkinService", "checkin", "current"));
			Thread.sleep(100);//5
			
			status.setStringValue("busy"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("outdoor"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_checkin, "checkinService", "switch", "off"));
			Thread.sleep(100);//6
			
			status.setStringValue("idle"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("office"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue("48.9, 2.33"); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "1"));
			Thread.sleep(100);//7
			
			status.setStringValue("idle"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("office"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue("48.9, 2.33"); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "volume", "mute"));
			Thread.sleep(100);//8
			
			status.setStringValue("idle"); cristPredictor.getCRISTPrediction(userId, status);
			// location.setStringValue("office"); cristPredictor.getCRISTPrediction(userId, location);
			// gps.setStringValue("48.9, 2.33"); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "volume", "mute"));
			Thread.sleep(100);//9
			
			status.setStringValue("busy"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("home"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "3"));
			Thread.sleep(100);//10
			
			status.setStringValue("idle"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("office"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue("48.9, 2.33"); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_checkin, "checkinService", "switch", "on"));
			Thread.sleep(100);//11
			
			status.setStringValue("idle"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("office"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue("48.9, 2.33"); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_checkin, "checkinService", "checkin", "current"));
			Thread.sleep(100);//12
			
			status.setStringValue("busy"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("outdoor"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_checkin, "checkinService", "switch", "off"));
			Thread.sleep(100);//13
			
			status.setStringValue("busy"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("home"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "volume", "mute"));
			Thread.sleep(100);//14
			
			status.setStringValue("busy"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("home"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "volume", "unmute"));
			Thread.sleep(100);//15

			status.setStringValue("busy"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("home"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "channel", "3"));
			Thread.sleep(100);//16
			
			status.setStringValue("busy"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("home"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "volume", "mute"));
			Thread.sleep(100);//17
			
			status.setStringValue("busy"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("home"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "volume", "unmute"));
			Thread.sleep(100);//18
			
			status.setStringValue("busy"); cristPredictor.getCRISTPrediction(userId, status);
			location.setStringValue("home"); cristPredictor.getCRISTPrediction(userId, location);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_MyTV, "TVService", "volume", "mute"));
			Thread.sleep(100);//19
*/			
			cristMgr.displayHistoryList();
			cristMgr.displayIntentModel();
			

			//check the result
			location.setStringValue("RoomB");
			Future<List<CRISTUserAction>> results = cristPredictor.getCRISTPrediction(userId, location);
			for (int i = 0; i < results.get().size(); i++)	{
				System.out.println("Infer intent by context: (Last Action is channel 2, current location is RoomB)");
				System.out.println("results.get().get(i).getActionID(): " + results.get().get(i).getActionID());
				Assert.assertTrue(results.get().get(i).getActionID() != null);
			}
/*
			
			
			//check the result
			status.setStringValue("idle");
			Future<List<CRISTUserAction>> results = cristPredictor.getCRISTPrediction(userId, status);
			for (int i = 0; i < results.get().size(); i++)	{
				System.out.println("Infer intent by context: ");
				System.out.println("results.get().get(i).getActionID(): " + results.get().get(i).getActionID());
				Assert.assertTrue(results.get().get(i).getActionID() != null);
			}
			
			IAction myAction = new Action(serviceId_MyTV, "TVService", "channel", "3");
			results = cristPredictor.getCRISTPrediction(userId, myAction);
			for (int i = 0; i < results.get().size(); i++)	{
				System.out.println("Infer intent by action: ");
				System.out.println("results.get().get(i).getActionID(): " + results.get().get(i).getActionID());
				Assert.assertTrue(results.get().get(i).getActionID() != null);
			}
			
			Future<CRISTUserAction> result = cristPredictor.getCurrentUserIntentAction(userId, serviceId_checkin, "switch");
			System.out.println("result: " + result);
			System.out.println("result.get(): " + result.get());
			Assert.assertTrue(result != null); //.getActionID()result.get() != null
*/
/*			
			verify(ctxBroker).retrieveIndividualEntity(userId);
			verify(ctxBroker).retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION,
					new ArrayList<CtxAttributeIdentifier>(), null, null);
			verify(ctxBroker).retrieve(statusId);
			verify(ctxBroker).retrieve(locationId);
			verify(ctxBroker).retrieve(tempId);
			verify(ctxBroker).retrieve(gpsId);
*/
			
			

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
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



}

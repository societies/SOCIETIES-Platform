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

import static org.mockito.Mockito.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.IPersonalisationManager;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.CRISTUserIntentDiscovery.impl.CRISTUserIntentDiscovery;
import org.societies.personalisation.CRISTUserIntentPrediction.impl.CRISTUserIntentPrediction;
import org.societies.personalisation.CRISTUserIntentTaskManager.impl.CRISTUserIntentTaskManager;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
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
			CtxAttributeIdentifier lightId = new CtxAttributeIdentifier(entityId, "LIGHT", new Long(123456));
			CtxAttributeIdentifier soundId = new CtxAttributeIdentifier(entityId, "SOUND", new Long(123456));
			CtxAttributeIdentifier tempId = new CtxAttributeIdentifier(entityId, CtxAttributeTypes.TEMPERATURE, new Long(123456));
			CtxAttributeIdentifier gpsId = new CtxAttributeIdentifier(entityId, CtxAttributeTypes.LOCATION_COORDINATES, new Long(123456));
			CtxAttribute light = new CtxAttribute(lightId);
			CtxAttribute sound = new CtxAttribute(soundId);
			CtxAttribute temp = new CtxAttribute(tempId);
			CtxAttribute gps = new CtxAttribute(gpsId);
			
			userId = new MockIdentity(IdentityType.CSS, "zhiyong", "societies.org");
			IndividualCtxEntity personEntity = new IndividualCtxEntity(entityId);

			CRISTUserIntentPrediction cristPredictor = new CRISTUserIntentPrediction();
			CRISTUserIntentTaskManager cristMgr = new CRISTUserIntentTaskManager();
			CRISTUserIntentDiscovery cristDisc = new CRISTUserIntentDiscovery();
			cristPredictor.setCristTaskManager(cristMgr);
			ctxBroker = mock(ICtxBroker.class);
			cristMgr.setCtxBroker(ctxBroker);
			cristMgr.setCristDiscovery(cristDisc);
			
			when(ctxBroker.retrieveIndividualEntity(userId)).thenReturn(new AsyncResult<IndividualCtxEntity>(personEntity));
			when(ctxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION,
					new ArrayList<CtxAttributeIdentifier>(), null, null)).thenReturn(null);		
			when(ctxBroker.retrieve(lightId)).thenReturn(new AsyncResult<CtxModelObject>(light));
			when(ctxBroker.retrieve(soundId)).thenReturn(new AsyncResult<CtxModelObject>(sound));
			when(ctxBroker.retrieve(tempId)).thenReturn(new AsyncResult<CtxModelObject>(temp));
			when(ctxBroker.retrieve(gpsId)).thenReturn(new AsyncResult<CtxModelObject>(gps));

			
			ServiceResourceIdentifier serviceId_music = new ServiceResourceIdentifier();
			serviceId_music.setIdentifier(new URI("http://testService_music"));
			ServiceResourceIdentifier serviceId_checkin = new ServiceResourceIdentifier();
			serviceId_checkin.setIdentifier(new URI("http://testService_checkin"));


			
			System.out.println("Start to input mock history: ");
			light.setStringValue("100"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("30"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("22"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_music, "musicService", "switch", "on"));
			Thread.sleep(100);//0
			
			light.setStringValue("100"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("30"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("22"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_music, "musicService", "volume", "down"));
			Thread.sleep(100);//1
			
			light.setStringValue("100"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("30"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("22"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_music, "musicService", "switch", "next"));
			Thread.sleep(100);//2
			
			light.setStringValue("100"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("30"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("22"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_music, "musicService", "switch", "off"));
			Thread.sleep(100);//3
			
			light.setStringValue("120"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("60"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("15"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue("48.9, 2.33"); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_checkin, "checkinService", "switch", "on"));
			Thread.sleep(100);//4
			
			light.setStringValue("120"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("60"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("15"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue("48.9, 2.33"); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_checkin, "checkinService", "checkin", "current"));
			Thread.sleep(100);//5
			
			light.setStringValue("100"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("80"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("22"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_checkin, "checkinService", "switch", "off"));
			Thread.sleep(100);//6
			
			light.setStringValue("120"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("60"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("15"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue("48.9, 2.33"); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_music, "musicService", "switch", "on"));
			Thread.sleep(100);//7
			
			light.setStringValue("120"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("60"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("15"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue("48.9, 2.33"); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_music, "musicService", "volume", "up"));
			Thread.sleep(100);//8
			
			light.setStringValue("120"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("60"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("15"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue("48.9, 2.33"); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_music, "musicService", "switch", "next"));
			Thread.sleep(100);//9
			
			light.setStringValue("100"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("30"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("26"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_music, "musicService", "switch", "off"));
			Thread.sleep(100);//10
			
			light.setStringValue("120"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("60"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("15"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue("48.9, 2.33"); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_checkin, "checkinService", "switch", "on"));
			Thread.sleep(100);//11
			
			light.setStringValue("120"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("60"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("15"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue("48.9, 2.33"); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_checkin, "checkinService", "checkin", "current"));
			Thread.sleep(100);//12
			
			light.setStringValue("100"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("80"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("22"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_checkin, "checkinService", "switch", "off"));
			Thread.sleep(100);//13
			
			light.setStringValue("100"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("30"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("22"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_music, "musicService", "switch", "on"));
			Thread.sleep(100);//14
			
			light.setStringValue("100"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("30"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("22"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_music, "musicService", "volume", "down"));
			Thread.sleep(100);//15

			light.setStringValue("100"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("30"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("22"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_music, "musicService", "switch", "next"));
			Thread.sleep(100);//16
			
			light.setStringValue("100"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("30"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("22"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_music, "musicService", "switch", "off"));
			Thread.sleep(100);//17
			
			light.setStringValue("100"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("30"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("22"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_music, "musicService", "switch", "on"));
			Thread.sleep(100);//18
			
			light.setStringValue("100"); cristPredictor.getCRISTPrediction(userId, light);
			sound.setStringValue("30"); cristPredictor.getCRISTPrediction(userId, sound);
			temp.setStringValue("22"); cristPredictor.getCRISTPrediction(userId, temp);
			gps.setStringValue(""); cristPredictor.getCRISTPrediction(userId, gps);
			cristPredictor.getCRISTPrediction(userId, new Action(serviceId_music, "musicService", "volume", "down"));
			Thread.sleep(100);//19
			
			cristMgr.displayHistoryList();
			cristMgr.displayIntentModel();
			
			
			
			//check the result
			light.setStringValue("120");
			Future<List<CRISTUserAction>> results = cristPredictor.getCRISTPrediction(userId, light);
			for (int i = 0; i < results.get().size(); i++)	{
				System.out.println("Infer intent by context: ");
				System.out.println("results.get().get(i).getActionID(): " + results.get().get(i).getActionID());
				Assert.assertTrue(results.get().get(i).getActionID() != null);
			}
			
			IAction myAction = new Action(serviceId_music, "musicService", "switch", "on");
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

/*			
			verify(ctxBroker).retrieveIndividualEntity(userId);
			verify(ctxBroker).retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION,
					new ArrayList<CtxAttributeIdentifier>(), null, null);
			verify(ctxBroker).retrieve(lightId);
			verify(ctxBroker).retrieve(soundId);
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

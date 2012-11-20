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
package org.societies.personalisation.DIANNE.test;

import static org.mockito.Mockito.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.DIANNE.api.model.IDIANNEOutcome;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.model.PersonalisationTypes;
import org.societies.personalisation.dianne.DIANNE;
import org.springframework.scheduling.annotation.AsyncResult;

public class DIANNETest extends TestCase{

	ICtxBroker mockCtxBroker;
	IInternalPersonalisationManager mockPersoMgr;
	ICommManager mockCommsMgr;
	IIdentityManager mockIdManager;
	DIANNE dianne;

	//variables
	String stringId;
	IIdentity mockIdentity;

	//context
	CtxEntityIdentifier mockPersonId;
	IndividualCtxEntity mockPersonEntity;
	CtxEntityIdentifier mockEntityId;
	CtxAttributeIdentifier mockSymLocId;
	CtxAttributeIdentifier mockStatusId;
	CtxAttributeIdentifier mockTemperatureId;
	CtxAttribute mockSymLoc;
	CtxAttribute mockStatus;
	CtxAttribute mockTemperature;

	//arraylists
	List<CtxIdentifier> mockSymLocIds;
	List<CtxIdentifier> mockStatusIds;
	List<CtxIdentifier> mockTemperatureIds;

	//futures
	Future<List<CtxIdentifier>> mockSymLocIdFuture;
	Future<List<CtxIdentifier>> mockStatusIdFuture;
	Future<List<CtxIdentifier>> mockTemperatureIdFuture;

	public void setUp() throws Exception{
		mockCtxBroker = mock(ICtxBroker.class);
		mockPersoMgr = mock(IInternalPersonalisationManager.class);
		mockCommsMgr = mock(ICommManager.class);
		mockIdManager = mock(IIdentityManager.class);
		dianne = new DIANNE();
		dianne.setCtxBroker(mockCtxBroker);
		dianne.setPersoMgr(mockPersoMgr);
		dianne.setCommsMgr(mockCommsMgr);

		/*
		 * Define mock variable
		 */
		stringId = "sarah@societies.org";
		mockIdentity = new MockIdentity(IdentityType.CSS, "sarah", "societies.org");

		/*
		 * Define mock context
		 */
		mockPersonId = new CtxEntityIdentifier(stringId, "PERSON", new Long(12345));
		mockPersonEntity = new IndividualCtxEntity(mockPersonId);
		mockEntityId = new CtxEntityIdentifier(stringId, "testEntity", new Long(12345));
		mockSymLocId = new CtxAttributeIdentifier(mockEntityId, CtxAttributeTypes.LOCATION_SYMBOLIC, new Long(12345));
		mockStatusId = new CtxAttributeIdentifier(mockEntityId, CtxAttributeTypes.STATUS, new Long(12345));
		mockTemperatureId = new CtxAttributeIdentifier(mockEntityId, CtxAttributeTypes.TEMPERATURE, new Long(12345));
		mockSymLoc = new CtxAttribute(mockSymLocId);
		mockStatus = new CtxAttribute(mockStatusId);
		mockTemperature = new CtxAttribute(mockTemperatureId);

		/*
		 * Define arraylists
		 */
		mockSymLocIds = new ArrayList<CtxIdentifier>();
		mockSymLocIds.add(mockSymLocId);
		mockStatusIds = new ArrayList<CtxIdentifier>();
		mockStatusIds.add(mockStatusId);
		mockTemperatureIds = new ArrayList<CtxIdentifier>();
		mockTemperatureIds.add(mockTemperatureId);

		/*
		 * Define futures
		 */
		//mock symLoc
		mockSymLocIdFuture = new AsyncResult<List<CtxIdentifier>>(mockSymLocIds);
		mockStatusIdFuture = new AsyncResult<List<CtxIdentifier>>(mockStatusIds);
		mockTemperatureIdFuture = new AsyncResult<List<CtxIdentifier>>(mockTemperatureIds);

	}

	public void tearDown() throws Exception{
		mockCtxBroker = null;
		mockPersoMgr = null;
		mockCommsMgr = null;
		dianne = null;
	}
	
	@Test
	public void testTemp(){
		Assert.assertTrue(true);
	}
	
	/*@Ignore
	@Test
	public void testDIANNE(){
		// Test initialise
		// No stored DIANNE networks should be returned here
		try {
			when(mockCommsMgr.getIdManager()).thenReturn(mockIdManager);
			when(mockIdManager.getThisNetworkNode()).thenReturn((INetworkNode)mockIdentity);
			when(mockCtxBroker.retrieveIndividualEntity(mockIdentity)).thenReturn(new AsyncResult<IndividualCtxEntity>(mockPersonEntity));
		} catch (CtxException e) {
			e.printStackTrace();
		}
		dianne.initialiseDIANNELearning();
		try {
			verify(mockCommsMgr).getIdManager();
			verify(mockIdManager).getThisNetworkNode();
			verify(mockCtxBroker).retrieveIndividualEntity(mockIdentity);
		} catch (CtxException e) {
			e.printStackTrace();
		}

		//test register for context
		try {
			when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC)).thenReturn(mockSymLocIdFuture);
			when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.STATUS)).thenReturn(mockStatusIdFuture);
			when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.TEMPERATURE)).thenReturn(mockTemperatureIdFuture);
			doNothing().when(mockPersoMgr).registerForContextUpdate(mockIdentity, PersonalisationTypes.DIANNE, mockSymLocId);
			doNothing().when(mockPersoMgr).registerForContextUpdate(mockIdentity, PersonalisationTypes.DIANNE, mockStatusId);
			doNothing().when(mockPersoMgr).registerForContextUpdate(mockIdentity, PersonalisationTypes.DIANNE, mockTemperatureId);
		} catch (CtxException e) {
			e.printStackTrace();
		}	
		dianne.registerContext();
		try {
			verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC);
			verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.STATUS);
			verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.TEMPERATURE);
			verify(mockPersoMgr, times(1)).registerForContextUpdate(mockIdentity, PersonalisationTypes.DIANNE, mockSymLocId);
			verify(mockPersoMgr, times(1)).registerForContextUpdate(mockIdentity, PersonalisationTypes.DIANNE, mockStatusId);
			verify(mockPersoMgr, times(1)).registerForContextUpdate(mockIdentity, PersonalisationTypes.DIANNE, mockTemperatureId);
		} catch (CtxException e) {
			e.printStackTrace();
		}

		//TEST DIANNE

		//set service type
		String serviceType = "testService";

		//set service IDs
		String serviceId1_string = "http://societies.org/testServiceOne";
		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		try {
			serviceId1.setIdentifier(new URI(serviceId1_string));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		serviceId1.setServiceInstanceIdentifier(serviceId1_string);

		String serviceId2_string = "http://societies.org/testServiceTwo";
		ServiceResourceIdentifier serviceId2 = new ServiceResourceIdentifier();
		try {
			serviceId2.setIdentifier(new URI(serviceId2_string));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		serviceId2.setServiceInstanceIdentifier(serviceId2_string);

		//set actions
		IAction service1_volume_high = new Action(serviceId1, serviceType, "volume", "high");
		IAction service1_volume_low = new Action(serviceId1, serviceType, "volume", "low");
		IAction service1_genre_pop = new Action(serviceId1, serviceType, "genre", "pop");
		IAction service1_genre_country = new Action(serviceId1, serviceType, "genre", "country");
		IAction service2_volume_high = new Action(serviceId2, serviceType, "volume", "high");
		IAction service2_volume_low = new Action(serviceId2, serviceType, "volume", "low");
		IAction service2_genre_pop = new Action(serviceId2, serviceType, "genre", "pop");
		IAction service2_genre_country = new Action(serviceId2, serviceType, "genre", "country");

		try {
			*//**
			 * Context - LOCATION=HOME, STATUS=FREE
			 * Outcomes - service1(VOLUME=HIGH), service1(GENRE=POP), service2(VOLUME=LOW), service2(GENRE=COUNTRY)
			 *//*
			//getOutcome(location = home) -> should return empty
			System.out.println("Setting: LOCATION=HOME");
			mockSymLoc.setStringValue("home");
			List<IDIANNEOutcome> results1 = dianne.getOutcome(mockIdentity, mockSymLoc).get();
			Assert.assertTrue(results1.size() == 0);
			Thread.sleep(1000);

			//getOutcome(status = free) -> should return empty
			System.out.println("Setting: STATUS=FREE");
			mockStatus.setStringValue("free");
			List<IDIANNEOutcome> results2 = dianne.getOutcome(mockIdentity, mockStatus).get();
			Assert.assertTrue(results2.size() == 0);
			Thread.sleep(1000);

			//SERVICE 1 - getOutcome(volume = high) -> should return empty
			System.out.println("Setting: service1(VOLUME=HIGH)");
			List<IDIANNEOutcome> results3 = dianne.getOutcome(mockIdentity, service1_volume_high).get();
			Assert.assertTrue(results3.size() == 0);
			Thread.sleep(1000);

			//SERVICE 2 - getOutcome(volume = low) -> should return empty
			System.out.println("Setting: service2(VOLUME=LOW)");
			List<IDIANNEOutcome> results4 = dianne.getOutcome(mockIdentity, service2_volume_low).get();
			Assert.assertTrue(results4.size() == 0);

			//SERVICE1 - getOutcome(genre = pop) -> should return empty
			System.out.println("Setting: service1(GENRE=POP)");
			List<IDIANNEOutcome> results5 = dianne.getOutcome(mockIdentity, service1_genre_pop).get();
			Assert.assertTrue(results5.size() == 0);
			Thread.sleep(1000);

			//SERVICE2 - getOutcome(genre = country) -> should return empty
			System.out.println("Setting: service2(GENRE=COUNTRY)");
			List<IDIANNEOutcome> results6 = dianne.getOutcome(mockIdentity, service2_genre_country).get();
			Assert.assertTrue(results6.size() == 0);

			//wait 5 secs
			System.out.println("Waiting for 5 seconds...");
			Thread.sleep(5000);
			System.out.println("...resuming");

			*//**
			 * Context - LOCATION=WORK, STATUS=BUSY
			 * Outcomes - service1(VOLUME=LOW), service1(GENRE=COUNTRY), service2(VOLUME=HIGH), service2(GENRE=POP)
			 *//*
			//getOutcome(location = work) -> should return nothing - no new outcomes 
			System.out.println("Setting: LOCATION=WORK");
			mockSymLoc.setStringValue("work");
			List<IDIANNEOutcome> results7 = dianne.getOutcome(mockIdentity, mockSymLoc).get();
			Assert.assertTrue(results7.size() == 0);
			Thread.sleep(1000);

			//getOutcome(status = busy) -> should return nothing - no new outcomes
			System.out.println("Setting: STATUS=BUSY");
			mockStatus.setStringValue("busy");
			List<IDIANNEOutcome> results8 = dianne.getOutcome(mockIdentity, mockStatus).get();
			Assert.assertTrue(results8.size() == 0);
			Thread.sleep(1000);

			//SERVICE1 - getOutcome(volume = low) -> should return empty
			System.out.println("Setting: service1(VOLUME=LOW)");
			List<IDIANNEOutcome> results9 = dianne.getOutcome(mockIdentity, service1_volume_low).get();
			Assert.assertTrue(results9.size() == 0);
			Thread.sleep(1000);

			//SERVICE2 - getOutcome(volume = high) -> should return empty
			System.out.println("Setting: service2(VOLUME=HIGH)");
			List<IDIANNEOutcome> results10 = dianne.getOutcome(mockIdentity, service2_volume_high).get();
			Assert.assertTrue(results10.size() == 0);
			Thread.sleep(1000);

			//SERVICE1 - getOutcome(genre = country) -> should return empty
			System.out.println("Setting: service1(GENRE=COUNTRY)");
			List<IDIANNEOutcome> results11 = dianne.getOutcome(mockIdentity, service1_genre_country).get();
			Assert.assertTrue(results11.size() == 0);
			Thread.sleep(1000);

			//SERVICE2 - getOutcome(genre = pop) -> should return empty
			System.out.println("Setting: service2(GENRE=POP)");
			List<IDIANNEOutcome> results12 = dianne.getOutcome(mockIdentity, service2_genre_pop).get();
			Assert.assertTrue(results12.size() == 0);

			//wait 5 secs
			System.out.println("Waiting for 5 seconds...");
			Thread.sleep(5000);
			System.out.println("...resuming");

			*//**
			 * Change context and check output
			 * LOCATION = HOME, STATUS=FREE
			 *//*
			//getOutcome(location = home) -> should return service1(VOLUME=LOW, GENRE=COUNTRY), service2(VOLUME=HIGH, GENRE=POP)
			System.out.println("Setting: LOCATION=HOME");
			mockSymLoc.setStringValue("home");
			List<IDIANNEOutcome> results13 = dianne.getOutcome(mockIdentity, mockSymLoc).get();
			System.out.println("results13.size = "+results13.size());
			for(IDIANNEOutcome nextOutcome : results13){
				System.out.println(nextOutcome.getServiceID().getServiceInstanceIdentifier()
						+": "+nextOutcome.getparameterName()+"="+nextOutcome.getvalue());
			}
			Assert.assertTrue(results13.size() == 4);
			boolean got1 = false, got2 = false, got3 = false, got4 =false;
			for(IDIANNEOutcome nextOutcome: results13){
				if(nextOutcome.getServiceID().getServiceInstanceIdentifier().equals(serviceId1.getServiceInstanceIdentifier())){
					if(nextOutcome.getparameterName().equals("volume")){
						Assert.assertEquals(serviceType, nextOutcome.getServiceType());
						Assert.assertEquals("low", nextOutcome.getvalue());
						got1 = true;
					}else if(nextOutcome.getparameterName().equals("genre")){
						Assert.assertEquals(serviceType, nextOutcome.getServiceType());
						Assert.assertEquals("country", nextOutcome.getvalue());
						got2 = true;
					}
				}else if(nextOutcome.getServiceID().getServiceInstanceIdentifier().equals(serviceId2.getServiceInstanceIdentifier())){
					if(nextOutcome.getparameterName().equals("volume")){
						Assert.assertEquals(serviceType, nextOutcome.getServiceType());
						Assert.assertEquals("high", nextOutcome.getvalue());
						got3 = true;
					}else if(nextOutcome.getparameterName().equals("genre")){
						Assert.assertEquals(serviceType, nextOutcome.getServiceType());
						Assert.assertEquals("pop", nextOutcome.getvalue());
						got4 = true;
					}
				}
			}
			Assert.assertTrue(got1);
			Assert.assertTrue(got2);
			Assert.assertTrue(got3);
			Assert.assertTrue(got4);

			//getOutcome(status = free) -> should return service1(VOLUME=HIGH, GENRE=POP), service2(VOLUME=LOW, GENRE=COUNTRY)
			System.out.println("Setting: STATUS=FREE");
			mockStatus.setStringValue("free");
			List<IDIANNEOutcome> results14 = dianne.getOutcome(mockIdentity, mockStatus).get();
			System.out.println("results14.size = "+results14.size());
			for(IDIANNEOutcome nextOutcome: results14){
				System.out.println(nextOutcome.getServiceID().getServiceInstanceIdentifier()
						+": "+nextOutcome.getparameterName()+"="+nextOutcome.getvalue());
			}
			Assert.assertTrue(results14.size() == 4);
			got1 = false; got2 = false; got3 = false; got4 =false;
			for(IDIANNEOutcome nextOutcome: results14){
				if(nextOutcome.getServiceID().getServiceInstanceIdentifier().equals(serviceId1.getServiceInstanceIdentifier())){
					if(nextOutcome.getparameterName().equals("volume")){
						Assert.assertEquals(serviceType, nextOutcome.getServiceType());
						Assert.assertEquals("high", nextOutcome.getvalue());
						got1 = true;
					}else if(nextOutcome.getparameterName().equals("genre")){
						Assert.assertEquals(serviceType, nextOutcome.getServiceType());
						Assert.assertEquals("pop", nextOutcome.getvalue());
						got2 = true;
					}
				}else if(nextOutcome.getServiceID().getServiceInstanceIdentifier().equals(serviceId2.getServiceInstanceIdentifier())){
					if(nextOutcome.getparameterName().equals("volume")){
						Assert.assertEquals(serviceType, nextOutcome.getServiceType());
						Assert.assertEquals("low", nextOutcome.getvalue());
						got3 = true;
					}else if(nextOutcome.getparameterName().equals("genre")){
						Assert.assertEquals(serviceType, nextOutcome.getServiceType());
						Assert.assertEquals("country", nextOutcome.getvalue());
						got4 = true;
					}
				}
			}
			Assert.assertTrue(got1);
			Assert.assertTrue(got2);
			Assert.assertTrue(got3);
			Assert.assertTrue(got4);


			*//**
			 * Request outcomes
			 * service1(VOLUME->high, GENRE->pop), service2(VOLUME->low, GENRE->country)
			 *//*
			//getOutcome(serviceId1, volume) -> should return VOLUME=HIGH
			System.out.println("Requesting: service1(VOLUME)...");
			List<IDIANNEOutcome> results15 = dianne.getOutcome(mockIdentity, serviceId1, "volume").get();
			Assert.assertTrue(results15.size() == 1);
			for(IDIANNEOutcome nextOutcome: results15){
				Assert.assertEquals(serviceId1.getServiceInstanceIdentifier(), nextOutcome.getServiceID().getServiceInstanceIdentifier());
				Assert.assertEquals(serviceType, nextOutcome.getServiceType());
				Assert.assertEquals("volume", nextOutcome.getparameterName());
				Assert.assertEquals("high", nextOutcome.getvalue());
			}
			Thread.sleep(1000);

			//getOutcome(serviceId1, genre) -> should return GENRE=POP
			System.out.println("Requesting: service1(GENRE)...");
			List<IDIANNEOutcome> results16 = dianne.getOutcome(mockIdentity, serviceId1, "genre").get();
			Assert.assertTrue(results16.size() == 1);
			for(IDIANNEOutcome nextOutcome: results16){
				Assert.assertEquals(serviceId1.getServiceInstanceIdentifier(), nextOutcome.getServiceID().getServiceInstanceIdentifier());
				Assert.assertEquals(serviceType, nextOutcome.getServiceType());
				Assert.assertEquals("genre", nextOutcome.getparameterName());
				Assert.assertEquals("pop", nextOutcome.getvalue());
			}
			Thread.sleep(1000);

			//getOutcome(serviceId2, volume) -> should return VOLUME=LOW
			System.out.println("Requesting: service2(VOLUME)...");
			List<IDIANNEOutcome> results17 = dianne.getOutcome(mockIdentity, serviceId2, "volume").get();
			Assert.assertTrue(results17.size() == 1);
			for(IDIANNEOutcome nextOutcome: results17){
				Assert.assertEquals(serviceId2.getServiceInstanceIdentifier(), nextOutcome.getServiceID().getServiceInstanceIdentifier());
				Assert.assertEquals(serviceType, nextOutcome.getServiceType());
				Assert.assertEquals("volume", nextOutcome.getparameterName());
				Assert.assertEquals("low", nextOutcome.getvalue());
			}
			Thread.sleep(1000);

			//getOutcome(serviceId2, genre) -> should return GENRE=COUNTRY
			System.out.println("Requesting: service2(GENRE)...");
			List<IDIANNEOutcome> results18 = dianne.getOutcome(mockIdentity, serviceId2, "genre").get();
			Assert.assertTrue(results18.size() == 1);
			for(IDIANNEOutcome nextOutcome: results18){
				Assert.assertEquals(serviceId2.getServiceInstanceIdentifier(), nextOutcome.getServiceID().getServiceInstanceIdentifier());
				Assert.assertEquals(serviceType, nextOutcome.getServiceType());
				Assert.assertEquals("genre", nextOutcome.getparameterName());
				Assert.assertEquals("country", nextOutcome.getvalue());
			}
			Thread.sleep(1000);


			*//**
			 * Change context and check output
			 * LOCATION=WORK, STATUS=BUSY
			 *//*

			//getOutcome(location = work) -> should return nothing
			System.out.println("Setting: LOCATION=WORK");
			mockSymLoc.setStringValue("work");
			List<IDIANNEOutcome> results19 = dianne.getOutcome(mockIdentity, mockSymLoc).get();
			System.out.println("results19.size = "+results19.size());
			for(IDIANNEOutcome nextOutcome : results19){
				System.out.println(nextOutcome.getServiceID().getServiceInstanceIdentifier()
						+": "+nextOutcome.getparameterName()+"="+nextOutcome.getvalue());
			}
			Assert.assertTrue(results19.size() == 0);

			//getOutcome(status = busy) -> should return service1(VOLUME=LOW, GENRE=COUNTRY), service2(VOLUME=HIGH, GENRE=POP)
			System.out.println("Setting: STATUS=BUSY");
			mockStatus.setStringValue("busy");
			List<IDIANNEOutcome> results20 = dianne.getOutcome(mockIdentity, mockStatus).get();
			System.out.println("results20.size = "+results20.size());
			for(IDIANNEOutcome nextOutcome : results20){
				System.out.println(nextOutcome.getServiceID().getServiceInstanceIdentifier()
						+": "+nextOutcome.getparameterName()+"="+nextOutcome.getvalue());
			}
			Assert.assertTrue(results20.size() == 4);
			got1 = false; got2 = false; got3 = false; got4 =false;
			for(IDIANNEOutcome nextOutcome: results20){
				if(nextOutcome.getServiceID().getServiceInstanceIdentifier().equals(serviceId1.getServiceInstanceIdentifier())){
					if(nextOutcome.getparameterName().equals("volume")){
						Assert.assertEquals(serviceType, nextOutcome.getServiceType());
						Assert.assertEquals("low", nextOutcome.getvalue());
						got1 = true;
					}else if(nextOutcome.getparameterName().equals("genre")){
						Assert.assertEquals(serviceType, nextOutcome.getServiceType());
						Assert.assertEquals("country", nextOutcome.getvalue());
						got2 = true;
					}
				}else if(nextOutcome.getServiceID().getServiceInstanceIdentifier().equals(serviceId2.getServiceInstanceIdentifier())){
					if(nextOutcome.getparameterName().equals("volume")){
						Assert.assertEquals(serviceType, nextOutcome.getServiceType());
						Assert.assertEquals("high", nextOutcome.getvalue());
						got3 = true;
					}else if(nextOutcome.getparameterName().equals("genre")){
						Assert.assertEquals(serviceType, nextOutcome.getServiceType());
						Assert.assertEquals("pop", nextOutcome.getvalue());
						got4 = true;
					}
				}
			}
			Assert.assertTrue(got1);
			Assert.assertTrue(got2);
			Assert.assertTrue(got3);
			Assert.assertTrue(got4);

			*//**
			 * Request outcomes
			 * service1(VOLUME->low, GENRE->country), service2(VOLUME->high, GENRE->pop)
			 *//*
			//getOutcome(serviceId1, volume) -> should return VOLUME=LOW
			System.out.println("Requesting: service1(VOLUME)...");
			List<IDIANNEOutcome> results21 = dianne.getOutcome(mockIdentity, serviceId1, "volume").get();
			Assert.assertTrue(results21.size() == 1);
			for(IDIANNEOutcome nextOutcome: results21){
				Assert.assertEquals(serviceId1.getServiceInstanceIdentifier(), nextOutcome.getServiceID().getServiceInstanceIdentifier());
				Assert.assertEquals(serviceType, nextOutcome.getServiceType());
				Assert.assertEquals("volume", nextOutcome.getparameterName());
				Assert.assertEquals("low", nextOutcome.getvalue());
			}
			Thread.sleep(1000);

			//getOutcome(service1, genre) -> should return GENRE=COUNTRY
			System.out.println("Requesting: service1(GENRE)...");
			List<IDIANNEOutcome> results22 = dianne.getOutcome(mockIdentity, serviceId1, "genre").get();
			Assert.assertTrue(results22.size() == 1);
			for(IDIANNEOutcome nextOutcome: results22){
				Assert.assertEquals(serviceId1.getServiceInstanceIdentifier(), nextOutcome.getServiceID().getServiceInstanceIdentifier());
				Assert.assertEquals(serviceType, nextOutcome.getServiceType());
				Assert.assertEquals("genre", nextOutcome.getparameterName());
				Assert.assertEquals("country", nextOutcome.getvalue());
			}
			Thread.sleep(1000);

			//getOutcome(serviceId2, volume) -> should return VOLUME=HIGH
			System.out.println("Requesting: service2(VOLUME)...");
			List<IDIANNEOutcome> results23 = dianne.getOutcome(mockIdentity, serviceId2, "volume").get();
			Assert.assertTrue(results23.size() == 1);
			for(IDIANNEOutcome nextOutcome: results23){
				Assert.assertEquals(serviceId2.getServiceInstanceIdentifier(), nextOutcome.getServiceID().getServiceInstanceIdentifier());
				Assert.assertEquals(serviceType, nextOutcome.getServiceType());
				Assert.assertEquals("volume", nextOutcome.getparameterName());
				Assert.assertEquals("high", nextOutcome.getvalue());
			}
			Thread.sleep(1000);

			//getOutcome(service2, genre) -> should return GENRE=POP
			System.out.println("Requesting: service2(GENRE)...");
			List<IDIANNEOutcome> results24 = dianne.getOutcome(mockIdentity, serviceId2, "genre").get();
			Assert.assertTrue(results24.size() == 1);
			for(IDIANNEOutcome nextOutcome: results24){
				Assert.assertEquals(serviceId2.getServiceInstanceIdentifier(), nextOutcome.getServiceID().getServiceInstanceIdentifier());
				Assert.assertEquals(serviceType, nextOutcome.getServiceType());
				Assert.assertEquals("genre", nextOutcome.getparameterName());
				Assert.assertEquals("pop", nextOutcome.getvalue());
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}*/

}

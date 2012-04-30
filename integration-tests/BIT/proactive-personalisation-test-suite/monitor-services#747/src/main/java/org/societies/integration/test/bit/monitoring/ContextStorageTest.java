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

package org.societies.integration.test.bit.monitoring;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Test;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.useragent.monitoring.IUserActionMonitor;

public class ContextStorageTest {

	private static IUserActionMonitor uam;
	private static ICtxBroker ctxBroker;

	public void setUp(){
		uam = TestCase747.getUAM();
		ctxBroker = TestCase747.getCtxBroker();
	}

	@Test
	public void test() {
		//create actions
		IIdentity identity = new MockIdentity(IdentityType.CSS, "sarah", "societies.org");
		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		ServiceResourceIdentifier serviceId2 = new ServiceResourceIdentifier();
		try {
			serviceId1.setIdentifier(new URI("testService1"));
			serviceId2.setIdentifier(new URI("testService2"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		//create actions
		IAction action1 = new Action(serviceId1, "testService", "volume", "high");
		IAction action2 = new Action(serviceId2, "testService", "volume", "high");
		IAction action3 = new Action(serviceId1, "testService", "volume", "low");
		IAction action4 = new Action(serviceId2, "testService", "colour", "blue");
		IAction action5 = new Action(serviceId2, "testService", "colour", "green");

		//send actions
		uam.monitor(identity, action1);
		uam.monitor(identity, action2);
		uam.monitor(identity, action3);
		uam.monitor(identity, action4);
		uam.monitor(identity, action5);

		//check context storage structure
		try {
			
			//Check service 1
			
			Future<List<CtxEntityIdentifier>> futureServiceEntityIDs = ctxBroker.lookupEntities(CtxEntityTypes.SERVICE, CtxAttributeTypes.ID, serviceId1, serviceId1);
			List<CtxEntityIdentifier> serviceEntityIDs = futureServiceEntityIDs.get();

			//Check number of service entities returned
			Assert.assertNotNull(serviceEntityIDs);
			Assert.assertTrue(serviceEntityIDs.size() == 1);	

			//check attributes for service 1
			Future<CtxModelObject> futureServiceEntity = ctxBroker.retrieve(serviceEntityIDs.get(0));
			CtxEntity serviceEntity = (CtxEntity)futureServiceEntity.get();
			Set<CtxAttribute> attributes = serviceEntity.getAttributes();

			Assert.assertNotNull(attributes);
			Assert.assertTrue(attributes.size() == 2); //ID, volume

			for(CtxAttribute attribute: attributes){
				if(attribute.getType().equals(CtxAttributeTypes.ID)){  //check service ID
					ServiceResourceIdentifier actualServiceId = 
							(ServiceResourceIdentifier)SerialisationHelper.deserialise(attribute.getBinaryValue(), this.getClass().getClassLoader());

					Assert.assertEquals(serviceId1, actualServiceId);

				}else if(attribute.getType().equals("volume")){  //check volume parameter
					IAction actualAction = (IAction)SerialisationHelper.deserialise(attribute.getBinaryValue(), this.getClass().getClassLoader());

					Assert.assertEquals(action3, actualAction);

				}else{ //unknown parameter!

					Assert.fail("Unknown parameter type for serviceId1");

				}
			}
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		
		//Check service 2
		
		try {
			Future<List<CtxEntityIdentifier>> futureServiceEntityIDs = ctxBroker.lookupEntities(CtxEntityTypes.SERVICE, CtxAttributeTypes.ID, serviceId2, serviceId2);
			List<CtxEntityIdentifier> serviceEntityIDs = futureServiceEntityIDs.get();
		
			//Check number of service entities returned
			Assert.assertNotNull(serviceEntityIDs);
			Assert.assertTrue(serviceEntityIDs.size() == 1);
		
			//check attributes for service 2
			Future<CtxModelObject> futureServiceEntity = ctxBroker.retrieve(serviceEntityIDs.get(0));
			CtxEntity serviceEntity = (CtxEntity)futureServiceEntity.get();
			Set<CtxAttribute> attributes = serviceEntity.getAttributes();
			
			Assert.assertNotNull(attributes);
			Assert.assertTrue(attributes.size() == 3); //ID, volume, colour

			for(CtxAttribute attribute: attributes){
				if(attribute.getType().equals(CtxAttributeTypes.ID)){  //check service ID
					ServiceResourceIdentifier actualServiceId = 
							(ServiceResourceIdentifier)SerialisationHelper.deserialise(attribute.getBinaryValue(), this.getClass().getClassLoader());

					Assert.assertEquals(serviceId2, actualServiceId);

				}else if(attribute.getType().equals("volume")){  //check volume parameter
					IAction actualAction = (IAction)SerialisationHelper.deserialise(attribute.getBinaryValue(), this.getClass().getClassLoader());

					Assert.assertEquals(action2, actualAction);

				}else if(attribute.getType().equals("colour")){
					IAction actualAction = (IAction)SerialisationHelper.deserialise(attribute.getBinaryValue(), this.getClass().getClassLoader());
					
					Assert.assertEquals(action5, actualAction);
					
				}else{ //unknown parameter!

					Assert.fail("Unknown parameter type for serviceId1");

				}
			}
		
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}	
}

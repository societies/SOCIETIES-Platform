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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.useragent.monitoring.IUserActionMonitor;

public class ContextStorageTest {

	private static Logger LOG = LoggerFactory.getLogger(ContextStorageTest.class);
	//private static IUserActionMonitor uam;
	//private static ICtxBroker ctxBroker;

	public void setUp(){
		//uam = TestCase747.getUam();
		//ctxBroker = TestCase747.getCtxBroker();
	}

	@Test
	public void test() {
		LOG.info("Monitor services #747 - Running ContextStorageTest....");
		//create actions
		IIdentity identity = new MockIdentity(IdentityType.CSS, "sarah", "societies.org");
		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		ServiceResourceIdentifier serviceId2 = new ServiceResourceIdentifier();
		try {
			serviceId1.setIdentifier(new URI("http://testService1"));
			serviceId2.setIdentifier(new URI("http://testService2"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		//create actions
		IAction action1 = new Action(serviceId1, "testService", "volume", "high");
		IAction action2 = new Action(serviceId2, "testService", "volume", "high");
		IAction action3 = new Action(serviceId1, "testService", "volume", "low");
		IAction action4 = new Action(serviceId2, "testService", "colour", "blue");
		IAction action5 = new Action(serviceId2, "testService", "colour", "green");

		//send actions - 1 second apart
		LOG.info("Monitor services #747 - sending mock actions for storage");
		TestCase747.uam.monitor(identity, action1);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		TestCase747.uam.monitor(identity, action2);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		TestCase747.uam.monitor(identity, action3);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		TestCase747.uam.monitor(identity, action4);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		TestCase747.uam.monitor(identity, action5);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		//check context storage structure

		Set<CtxEntityIdentifier> serviceEntityIDs = null;

		IndividualCtxEntity person;
		try {
			person = TestCase747.ctxBroker.retrieveCssOperator().get();

			if(person == null){
				LOG.error("Person entity is NULL");
			}
			Assert.assertNotNull(person);

			Set<CtxAssociationIdentifier> usesServiceAssocIDs = person.getAssociations(CtxAssociationTypes.USES_SERVICE);
			if(usesServiceAssocIDs == null){
				LOG.error("USES_SERVICE association IDs in null");
			}
			if(usesServiceAssocIDs.size() != 1){
				LOG.error("Incorrect number of USES_SERVICE association IDs :"+usesServiceAssocIDs.size());
			}
			Assert.assertNotNull(usesServiceAssocIDs);
			Assert.assertTrue(usesServiceAssocIDs.size() == 1);

			CtxAssociation usesServiceAssoc = (CtxAssociation)TestCase747.ctxBroker.retrieve(usesServiceAssocIDs.iterator().next()).get();
			if(usesServiceAssoc == null){
				LOG.error("USES_SERVICE association is NULL");
			}
			Assert.assertNotNull(usesServiceAssoc);

			serviceEntityIDs = usesServiceAssoc.getChildEntities();
			if(serviceEntityIDs.size() != 2){
				LOG.error("Wrong number of SERVICE entity IDs for USES_SERVICE association: "+serviceEntityIDs.size());
			}
			Assert.assertTrue(serviceEntityIDs.size() == 2);  //2 service entities
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		} catch (CtxException e1) {
			e1.printStackTrace();
		}

		try{
			/*
			 * CHECK SERVICE 1
			 */
			ArrayList<CtxEntityIdentifier> serviceEntityIDsList = new ArrayList<CtxEntityIdentifier>(serviceEntityIDs);

			List<CtxEntityIdentifier> check1ServiceIDs = 
					TestCase747.ctxBroker.lookupEntities(serviceEntityIDsList, CtxAttributeTypes.ID, serviceId1).get();

			//Check number of service entities returned
			if(check1ServiceIDs == null){
				LOG.error("1) NULL - no SERVICE entity exists with ID: "+serviceId1);
			}
			if(check1ServiceIDs.size() <= 0){
				LOG.error("1) EMPTY - no SERVICE entity exists with ID: "+serviceId1);
			}
			Assert.assertNotNull(serviceEntityIDs);
			Assert.assertTrue(serviceEntityIDs.size() == 1);	

			//check service ID attribute
			CtxEntity serviceEntity = (CtxEntity)TestCase747.ctxBroker.retrieve(check1ServiceIDs.get(0)).get();

			Set<CtxAttribute> attributes = serviceEntity.getAttributes();
			if(attributes == null){
				LOG.error("1) NULL - no attributes under SERVICE entity");
			}
			if(attributes.size() != 1){
				LOG.error("1) Wrong number of attributes under SERVICE entity: "+attributes.size());
			}
			Assert.assertNotNull(attributes);
			Assert.assertTrue(attributes.size() == 1);

			CtxAttribute idAttr = attributes.iterator().next();
			ServiceResourceIdentifier actualServiceId = (ServiceResourceIdentifier)SerialisationHelper.deserialise(idAttr.getBinaryValue(), this.getClass().getClassLoader());
			if(!serviceId1.equals(actualServiceId)){
				LOG.error("1) wrong serviceID stored with SERVICE entity.  Expected: "+serviceId1+", Actual: "+actualServiceId);
			}
			Assert.assertEquals(serviceId1, actualServiceId);

			//check service HAS_PARAMETER associations
			Set<CtxAssociationIdentifier> hasParamIDs = serviceEntity.getAssociations(CtxAssociationTypes.HAS_PARAMETER);
			if(hasParamIDs == null){
				LOG.error("1) NULL - no HAS_PARAMETER associations for SERVICE entity");
			}
			if(hasParamIDs.size() != 1){
				LOG.error("1) Wrong numbder of HAS_PARAMETER associations under SERVICE entity: "+hasParamIDs.size());
			}
			Assert.assertNotNull(hasParamIDs);
			Assert.assertTrue(hasParamIDs.size() == 1);

			CtxAssociation hasParamAssoc = (CtxAssociation)TestCase747.ctxBroker.retrieve(hasParamIDs.iterator().next()).get();
			if(hasParamAssoc == null){
				LOG.error("1) HAS_PARAMETER association is NULL");
			}
			Assert.assertNotNull(hasParamAssoc);

			Set<CtxEntityIdentifier> serviceParamIDs = hasParamAssoc.getChildEntities();
			if(serviceParamIDs.size() != 1){
				LOG.error("1) Wrong number of SERVICE_PARAMETER entity IDs for HAS_PARAMETERS association "+serviceParamIDs.size());
			}
			Assert.assertTrue(serviceParamIDs.size() == 1);

			CtxEntity serviceParam = (CtxEntity)TestCase747.ctxBroker.retrieve(serviceParamIDs.iterator().next());

			Set<CtxAttribute> serviceParamAttrs = serviceParam.getAttributes(); 

			for(CtxAttribute nextAttr: serviceParamAttrs){
				if(nextAttr.getType().equals(CtxAttributeTypes.PARAMETER_NAME)){  //check name
					String actualParameterName = nextAttr.getStringValue();

					LOG.info("1) volume should be the same as "+actualParameterName);
					Assert.assertEquals("volume", actualParameterName);

				}else if(nextAttr.getType().equals(CtxAttributeTypes.LAST_ACTION)){  //check last action
					IAction actualAction = (IAction)SerialisationHelper.deserialise(nextAttr.getBinaryValue(), this.getClass().getClassLoader());

					LOG.info("1) "+action3+" should be the same as "+actualAction);
					Assert.assertEquals(action3, actualAction);
				}else if(nextAttr.getType().equals("tuple_volume")){
					//ignore
				}else if(nextAttr.getType().equals("tupleIds_volume")){
					//ignore
				}else{ //unknown parameter!

					LOG.error("1) received unknown attribute type: "+nextAttr.getType());
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


		/*
		 * CHECK SERVICE 2
		 */
		try{
			ArrayList<CtxEntityIdentifier> serviceEntityIDsList = new ArrayList<CtxEntityIdentifier>(serviceEntityIDs);

			List<CtxEntityIdentifier> check2ServiceIDs = 
					TestCase747.ctxBroker.lookupEntities(serviceEntityIDsList, CtxAttributeTypes.ID, serviceId2).get();

			//Check number of service entities returned
			if(check2ServiceIDs == null){
				LOG.error("2) NULL - no SERVICE entity exists with ID: "+serviceId2);
			}
			if(check2ServiceIDs.size() <= 0){
				LOG.error("2) EMPTY - no SERVICE entity exists with ID: "+serviceId2);
			}
			Assert.assertNotNull(serviceEntityIDs);
			Assert.assertTrue(serviceEntityIDs.size() == 1);	

			//check service ID attribute
			CtxEntity serviceEntity = (CtxEntity)TestCase747.ctxBroker.retrieve(check2ServiceIDs.get(0)).get();

			Set<CtxAttribute> attributes = serviceEntity.getAttributes();
			if(attributes == null){
				LOG.error("2) NULL - no attributes under SERVICE entity");
			}
			if(attributes.size() != 1){
				LOG.error("2) Wrong number of attributes under SERVICE entity: "+attributes.size());
			}
			Assert.assertNotNull(attributes);
			Assert.assertTrue(attributes.size() == 1);

			CtxAttribute idAttr = attributes.iterator().next();
			ServiceResourceIdentifier actualServiceId = (ServiceResourceIdentifier)SerialisationHelper.deserialise(idAttr.getBinaryValue(), this.getClass().getClassLoader());
			if(!serviceId2.equals(actualServiceId)){
				LOG.error("2) wrong serviceID stored with SERVICE entity.  Expected: "+serviceId2+", Actual: "+actualServiceId);
			}
			Assert.assertEquals(serviceId2, actualServiceId);

			//check service HAS_PARAMETER associations
			Set<CtxAssociationIdentifier> hasParamIDs = serviceEntity.getAssociations(CtxAssociationTypes.HAS_PARAMETER);
			if(hasParamIDs == null){
				LOG.error("2) NULL - no HAS_PARAMETER associations for SERVICE entity");
			}
			if(hasParamIDs.size() != 1){
				LOG.error("2) Wrong numbder of HAS_PARAMETER associations under SERVICE entity: "+hasParamIDs.size());
			}
			Assert.assertNotNull(hasParamIDs);
			Assert.assertTrue(hasParamIDs.size() == 1);

			CtxAssociation hasParamAssoc = (CtxAssociation)TestCase747.ctxBroker.retrieve(hasParamIDs.iterator().next()).get();
			if(hasParamAssoc == null){
				LOG.error("2) HAS_PARAMETER association is NULL");
			}
			Assert.assertNotNull(hasParamAssoc);

			Set<CtxEntityIdentifier> serviceParamIDs = hasParamAssoc.getChildEntities();
			if(serviceParamIDs.size() != 2){
				LOG.error("2) Wrong number of SERVICE_PARAMETER entity IDs for HAS_PARAMETERS association "+serviceParamIDs.size());
			}
			Assert.assertTrue(serviceParamIDs.size() == 2);

			CtxEntity serviceParam = (CtxEntity)TestCase747.ctxBroker.retrieve(serviceParamIDs.iterator().next());

			Set<CtxAttribute> serviceParamAttrs = serviceParam.getAttributes(); 

			for(CtxAttribute nextAttr: serviceParamAttrs){
				if(nextAttr.getType().equals(CtxAttributeTypes.PARAMETER_NAME)){  //check name
					String actualParameterName = nextAttr.getStringValue();

					LOG.info("2) actual parameter name is: "+actualParameterName);
					//Assert.assertEquals("volume", actualParameterName);

				}else if(nextAttr.getType().equals(CtxAttributeTypes.LAST_ACTION)){  //check last action
					IAction actualAction = (IAction)SerialisationHelper.deserialise(nextAttr.getBinaryValue(), this.getClass().getClassLoader());

					LOG.info("1) "+action3+" should be the same as "+actualAction);
					Assert.assertEquals(action3, actualAction);
				}else if(nextAttr.getType().equals("tuple_volume")){
					//ignore
				}else if(nextAttr.getType().equals("tupleIds_volume")){
					//ignore
				}else{ //unknown parameter!

					LOG.error("1) received unknown attribute type: "+nextAttr.getType());
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

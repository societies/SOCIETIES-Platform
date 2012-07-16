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

package org.societies.integration.test.bit.caui_prediction;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;


public class ContextStorageTest {

	private static Logger LOG = LoggerFactory.getLogger(TestCase1109.class);
	
	//IIdentity identity = new MockIdentity(IdentityType.CSS, "user", "societies.org");

	private IIdentity cssOwnerId;
	
	
	public void setUp(){
		System.out.println("Test 1109 started : ContextStorageTest");
	}

	@Test
	public void TestMonitorActionsContext() {
		System.out.println("Test 1109 started : ContextStorageTest");
		//create actions
		
		
		
		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		ServiceResourceIdentifier serviceId2 = new ServiceResourceIdentifier();
		ServiceResourceIdentifier serviceIdRandom = new ServiceResourceIdentifier();
		try {
		
			final INetworkNode cssNodeId = TestCase1109.commMgr.getIdManager().getThisNetworkNode();
			LOG.info("*** cssNodeId = " + cssNodeId);
			final String cssOwnerStr = cssNodeId.getBareJid();
			this.cssOwnerId = TestCase1109.commMgr.getIdManager().fromJid(cssOwnerStr);
			
			
			
			
			
			serviceId1.setIdentifier(new URI("css://nikosk@societies.org/radioService"));
			serviceId1.setServiceInstanceIdentifier("css://nikosk@societies.org/radioService");

			serviceId2.setIdentifier(new URI("css://nikosk@societies.org/navigatorService"));
			serviceId2.setServiceInstanceIdentifier("css://nikosk@societies.org/navigatorService");


			serviceIdRandom.setIdentifier(new URI("css://nikosk@societies.org/randomService"));
			serviceIdRandom.setServiceInstanceIdentifier("css://nikosk@societies.org/randomService");

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//create actions
		//task1
		IAction action1 = new Action(serviceId1, "serviceType1", "setRadio", "on");
		IAction action2 = new Action(serviceId1, "serviceType1", "setVolume", "medium");
		IAction action3 = new Action(serviceId1, "serviceType1", "setTuner", "favoriteChannel1");
		//task2
		IAction action4 = new Action(serviceId2, "serviceType2", "setDestination", "gasStation");
		IAction action5 = new Action(serviceId2, "serviceType2", "setDestination", "office");
		IAction action6 = new Action(serviceId2, "serviceType2", "getInfo", "traffic");
		//task3
		IAction action7 = new Action(serviceId1, "serviceType1", "setRadio", "off");
		IAction action8 = new Action(serviceId2, "serviceType2", "setDestinator", "off");

		// random action 1
		IAction actionRandom1 = new Action(serviceIdRandom, "serviceIdRandom", "random", "xxx");
		IAction actionRandom2 = new Action(serviceIdRandom, "serviceIdRandom", "random", "yyy");
		IAction actionRandom3 = new Action(serviceIdRandom, "serviceIdRandom", "random", "zzz");
		IAction actionRandom4 = new Action(serviceIdRandom, "serviceIdRandom", "random", "ooo");

		//set context data
		setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "home");
		setContext(CtxAttributeTypes.TEMPERATURE, 25);
		setContext(CtxAttributeTypes.STATUS, "free");

		//send actions - 2 second delay
		LOG.info("Monitor services #1109 - sending mock actions for storage");

		actionsTask1(action1,action2,action3);
		randomAction(actionRandom1);
		actionsTask2(action4,action5,action6);
		randomAction(actionRandom2);
		actionsTask3(action7,action8);

		actionsTask1(action1,action2,action3);
		randomAction(actionRandom2);
		randomAction(actionRandom1);
		actionsTask2(action4,action5,action6);
		randomAction(actionRandom2);
		randomAction(actionRandom1);
		actionsTask3(action7,action8);

		actionsTask1(action1,action2,action3);
		randomAction(actionRandom3);
		randomAction(actionRandom1);
		actionsTask2(action4,action5,action6);
		randomAction(actionRandom3);
		randomAction(actionRandom2);
		randomAction(actionRandom2);
		actionsTask3(action7,action8);

		actionsTask1(action1,action2,action3);
		randomAction(actionRandom2);

		actionsTask2(action4,action5,action6);
		randomAction(actionRandom1);
		randomAction(actionRandom3);
		actionsTask3(action7,action8);

		randomAction(actionRandom4);
		randomAction(actionRandom2);
		randomAction(actionRandom4);
		/*
		 * CHECK HISTORY DATA
		 */
		LOG.info("*********** CHECK HISTORY DATA ************");
		//Assert.assertTrue(tupleResults.size() == 5);
	}

	@Test
	public void TestHistoryDataRetrieval() {

		List<CtxAttributeIdentifier> ls = new ArrayList<CtxAttributeIdentifier>();
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults;
		try {
			tupleResults = TestCase1109.ctxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, ls, null, null).get();
			Assert.assertEquals(49,tupleResults.size());
			printHocTuplesDB(tupleResults);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
	
	//********************************************
    //           helper classes 
	//******************************************** 
	private void randomAction (IAction action){
		TestCase1109.uam.monitor(this.cssOwnerId, action);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "randomLocation");
		setContext(CtxAttributeTypes.TEMPERATURE, 300);
		setContext(CtxAttributeTypes.STATUS, "randomStatus");
	}


	private void actionsTask1 (IAction action1, IAction action2, IAction action3){

		setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "Home-Parking");
		setContext(CtxAttributeTypes.TEMPERATURE, 30);
		setContext(CtxAttributeTypes.STATUS, "driving");

		TestCase1109.uam.monitor(this.cssOwnerId, action1);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "Home-Parking");
		setContext(CtxAttributeTypes.TEMPERATURE, 30);
		setContext(CtxAttributeTypes.STATUS, "driving");

		TestCase1109.uam.monitor(this.cssOwnerId, action2);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		setContext(CtxAttributeTypes.LOCATION_SYMBOLIC,"Home-Parking");
		setContext(CtxAttributeTypes.TEMPERATURE, 30);
		setContext(CtxAttributeTypes.STATUS, "driving");

		TestCase1109.uam.monitor(this.cssOwnerId, action3);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}


	private void actionsTask2 (IAction action4, IAction action5, IAction action6){

		//IAction action4 = new Action(serviceId2, "serviceType2", "setDestination", "gasStation");
		//IAction action5 = new Action(serviceId2, "serviceType2", "setDestination", "office");
		//IAction action6 = new Action(serviceId2, "serviceType2", "getInfo", "traffic");

		setContext(CtxAttributeTypes.LOCATION_SYMBOLIC,"High_way");
		setContext(CtxAttributeTypes.TEMPERATURE, 22);
		setContext(CtxAttributeTypes.STATUS, "driving");

		TestCase1109.uam.monitor(this.cssOwnerId, action4);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "Gas_station");
		setContext(CtxAttributeTypes.TEMPERATURE, 28);
		setContext(CtxAttributeTypes.STATUS, "stopped");

		TestCase1109.uam.monitor(this.cssOwnerId, action5);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		setContext(CtxAttributeTypes.LOCATION_SYMBOLIC,"high_way_junction");
		setContext(CtxAttributeTypes.TEMPERATURE, 30);
		setContext(CtxAttributeTypes.STATUS, "driving");

		TestCase1109.uam.monitor(this.cssOwnerId, action6);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}


	private void actionsTask3 (IAction action7, IAction action8){

		setContext(CtxAttributeTypes.LOCATION_SYMBOLIC,"office_parking");
		setContext(CtxAttributeTypes.TEMPERATURE, 22);
		setContext(CtxAttributeTypes.STATUS, "stopped");
		TestCase1109.uam.monitor(this.cssOwnerId, action7);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		TestCase1109.uam.monitor(this.cssOwnerId, action8);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}

	private CtxAttribute setContext(String type, Serializable value){

		CtxAttribute attr = null; 
		try {
			IndividualCtxEntity operator = TestCase1109.ctxBroker.retrieveIndividualEntity(this.cssOwnerId).get();
			Set<CtxAttribute> ctxAttrSet = operator.getAttributes(type);
			if(ctxAttrSet.size()>0 ){
				ArrayList<CtxAttribute> ctxAttrList = new ArrayList<CtxAttribute>(ctxAttrSet);	
				attr = ctxAttrList.get(0);
				attr = TestCase1109.ctxBroker.updateAttribute(attr.getId(), value).get();
			} else {
				attr = TestCase1109.ctxBroker.createAttribute(operator.getId(), type).get();
				attr = TestCase1109.ctxBroker.updateAttribute(attr.getId(),value).get();
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
		return attr;
	}

	private void printAttr(CtxEntity entity) throws InterruptedException, ExecutionException, CtxException{
		System.out.println("operator: "+entity);
		Set<CtxAttribute> attrSet = entity.getAttributes();
		System.out.println("operator attrs : "+attrSet);
		for(CtxAttribute attrs: attrSet){
			System.out.println("attr type: "+attrs.getType());
			if(attrs.getStringValue() != null) System.out.println(" value "+attrs.getStringValue());
		}
	}

	protected CtxAttribute lookupRetrieveAttrHelp(String type){
		CtxAttribute ctxAttr = null;
		try {
			List<CtxIdentifier> tupleAttrList = TestCase1109.ctxBroker.lookup(CtxModelType.ATTRIBUTE,type).get();
			if(tupleAttrList.size() >0 ){
				CtxIdentifier ctxId = tupleAttrList.get(0);
				ctxAttr =  (CtxAttribute) TestCase1109.ctxBroker.retrieve(ctxId).get();	
				System.out.println("lookupRetrieveAttrHelp "+ ctxAttr);
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
		return ctxAttr;
	}


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

}
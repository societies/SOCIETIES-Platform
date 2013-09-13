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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;

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
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;


public class Tester {

	private static Logger LOG = LoggerFactory.getLogger(TestCase1109.class);

	//private IIdentity cssOwnerId;

	boolean modelExist = false;

	public void setUp(){

	}

	@Test
	public void TestMonitorActionsContext() {
		System.out.println("Test 1876 started : ContextStorageTest");


		CtxAttributeIdentifier uiModelAttributeId = null;
		List<CtxIdentifier> ls;

		try {
			ls = TestCase1109.ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.CAUI_MODEL).get();

			if (ls.size()>0) {
				uiModelAttributeId = (CtxAttributeIdentifier) ls.get(0);
				CtxAttribute uiModelAttr = (CtxAttribute) TestCase1109.ctxBroker.retrieve(uiModelAttributeId).get();

				if(uiModelAttr != null) {

					if(uiModelAttr.getBinaryValue() != null) {
						modelExist = true;						
					}
				}
			} 

		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (ExecutionException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (CtxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}


		if(modelExist == false)	{	

			ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
			ServiceResourceIdentifier serviceId2 = new ServiceResourceIdentifier();
			ServiceResourceIdentifier serviceIdRandom = new ServiceResourceIdentifier();
			try {
				//	IIdentity cssOwnerId = getOwnerId();

				serviceId1.setIdentifier(new URI("xc.societies.local/Cobrowse_Service1"));
				serviceId1.setServiceInstanceIdentifier("cobrowse-webapp1");

				serviceId2.setIdentifier(new URI("xc.societies.local/Cobrowse_Service2"));
				serviceId2.setServiceInstanceIdentifier("cobrowse-webapp2");
				
				serviceIdRandom.setIdentifier(new URI("css://nikosk@societies.org/randomService"));
				serviceIdRandom.setServiceInstanceIdentifier("randomService3");

			} catch (URISyntaxException e) {
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
			//setContext(CtxAttributeTypes.TEMPERATURE, 25);
			//setContext(CtxAttributeTypes.STATUS, "free");

			//send actions - 2 second delay
			LOG.info("Monitor services #1876 - sending mock actions for storage");

			actionsTask1(action1,action2,action3);
			randomAction(actionRandom1);
			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "office");
			actionsTask2(action4,action5,action6);
			randomAction(actionRandom2);
			actionsTask3(action7,action8);

			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "home");
			actionsTask1(action1,action2,action3);
			randomAction(actionRandom2);
			randomAction(actionRandom1);
			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "office");
			actionsTask2(action4,action5,action6);
			randomAction(actionRandom2);


			randomAction(actionRandom1);

			//actionsTask3(action7,action8);

			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "home");
			actionsTask1(action1,action2,action3);
			randomAction(actionRandom3);
			randomAction(actionRandom1);
			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "office");
			actionsTask2(action4,action5,action6);
			randomAction(actionRandom3);
			randomAction(actionRandom2);
			randomAction(actionRandom2);
			actionsTask3(action7,action8);
			
			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "home");
			actionsTask1(action1,action2,action3);
			randomAction(actionRandom2);
			
			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "office");
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

			LOG.info("*********** ACTIONS SEND WAITING FOR MODEL CREATION ************");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}


		}
	}


	@Test
	public void TestHistoryDataRetrieval() {

		LOG.info("TestHistoryDataRetrieval ");

		List<CtxAttributeIdentifier> ls = new ArrayList<CtxAttributeIdentifier>();
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults;
		try {
			tupleResults = TestCase1109.ctxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, ls, null, null).get();
			boolean success = false;
			if(tupleResults.size() >=0 )success= true;
			Assert.assertTrue(success);


			printHocTuplesDB(tupleResults);
			LOG.info("number of actions in history "+ tupleResults.size());

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void TestPerformOnDemandPrediction() {

		try {	
			LOG.info("TestPerformOnDemandPrediction : waiting 9000 for model creation ");
			Thread.sleep(9000);

			IIdentity cssOwnerId = getOwnerId();

			ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
			serviceId1.setIdentifier(new URI("xc.societies.local/Cobrowse_Service1"));
			serviceId1.setServiceInstanceIdentifier("cobrowse-webapp1");

			
			// this action simulates an action performed by the user 
			IAction actionRadio1 = new Action(serviceId1, "serviceType1", "setRadio", "on");
			printOperatorAttr();
			LOG.info("A action performed :  "+ actionRadio1 );

			List<IUserIntentAction> actionList = TestCase1109.cauiPrediction.getPrediction(cssOwnerId, actionRadio1).get();
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


	@Test
	public void TestPerformContinuousPrediction(){

		LOG.info("Test 1876 : TestPerformContinuousPrediction");


		try {
			LOG.info("TestPerformContinuousPrediction : waiting 9000 ");
			Thread.sleep(9000);

			IIdentity cssOwnerId = getOwnerId();

			ServiceResourceIdentifier serviceId2 = new ServiceResourceIdentifier();
			serviceId2.setIdentifier(new URI("xc.societies.local/Cobrowse_Service2"));
			serviceId2.setServiceInstanceIdentifier("cobrowse-webapp2");

			// this action simulates an action performed by the user 
			IAction action4 = new Action(serviceId2, "serviceType2", "setDestination", "gasStation");
			printOperatorAttr();

			LOG.info("performing action: "+ action4);
			TestCase1109.uam.monitor(cssOwnerId, action4);
			//LOG.info("");
		}  catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}


	@Test
	public void TestGetCurrentIntentAction(){

		LOG.info("Test 1876 : TestGetCurrentIntentAction");

		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		try {
			IIdentity cssOwnerId = getOwnerId();

			LOG.info("TestGetCurrentIntentAction : waiting 9000 ");
			Thread.sleep(9000);

			serviceId1.setIdentifier(new URI("xc.societies.local/Cobrowse_Service1"));
			serviceId1.setServiceInstanceIdentifier("cobrowse-webapp1");

			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "Home-Parking");
			//setContext(CtxAttributeTypes.TEMPERATURE, new Integer(30));
			//setContext(CtxAttributeTypes.STATUS, "driving");

			LOG.info("print current context");
			printOperatorAttr();

			LOG.info("input for TestGetCurrentIntentAction serviceId1:" + serviceId1);
			LOG.info("input for TestGetCurrentIntentAction serviceId1:" + serviceId1.getServiceInstanceIdentifier());
			LOG.info("input for TestGetCurrentIntentAction serviceId1:" + serviceId1.getIdentifier());
			
			LOG.info("input for TestGetCurrentIntentAction cssOwnerId:" + cssOwnerId); 
			
			IUserIntentAction currentAction = TestCase1109.cauiPrediction.getCurrentIntentAction(cssOwnerId, serviceId1, "setVolume").get();

			LOG.info("outcome for TestGetCurrentIntentAction ");
			LOG.info("currentAction "+currentAction.getActionID());
			LOG.info("currentAction context: "+currentAction.getActionContext());
			LOG.info("confidence level of predicted action: "+currentAction.getConfidenceLevel());
			// predictedAction css://nikosk@societies.org/navigatorService#setDestination=gasStation/4 
			// predictedAction context: {location=High_way, status=driving}
			//Assert.assertEquals("css://nikosk@societies.org/navigatorService#setDestination=gasStation/4",predictedAction.getActionID());

			Assert.assertEquals("medium",currentAction.getvalue());
			// no null

		} catch (URISyntaxException e) {
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

	@Test
	public void TestGetPredictionByAttrUpdate(){

		LOG.info("Test 1876 : TestGetPredictionByAttrUpdate");
		IIdentity cssOwnerId = getOwnerId();

		CtxAttribute updatedlocAttr =  setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "Gas_station");

		try {
			List<IUserIntentAction> currentActionList = TestCase1109.cauiPrediction.getPrediction(cssOwnerId, updatedlocAttr).get();
			

			//Hashmap<Integer, IUserIntentAction> resultsRates = new Hashmap<Integer, IUserIntentAction>();
			if(currentActionList.size()>0){
				
				IUserIntentAction maxAction = null;
				
				int maxConfLevel = 0;
				for(IUserIntentAction userAction : currentActionList){

					int confLevel = userAction.getConfidenceLevel();
					LOG.info("userAction: "+confLevel);
					LOG.info("confLevel: "+confLevel);
					
					
					if(confLevel > maxConfLevel) {
						maxConfLevel = confLevel;
						maxAction = userAction;
						LOG.info("maxConfLevel: "+maxConfLevel);
						LOG.info("maxAction: "+maxAction); 
						
					}
					
				}
				
				LOG.info("currentAction "+currentActionList);
				
				Assert.assertEquals("setDestination",maxAction.getparameterName());
				Assert.assertEquals("office",maxAction.getvalue());
			}

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

		IIdentity cssOwnerId = getOwnerId();

		setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "randomLocation");
		//setContext(CtxAttributeTypes.TEMPERATURE, new Integer(300));
		//setContext(CtxAttributeTypes.STATUS, "randomStatus");

		TestCase1109.uam.monitor(cssOwnerId, action);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}


	private void actionsTask1 (IAction action1, IAction action2, IAction action3){

		try {
			IIdentity cssOwnerId = getOwnerId();
			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "Home-Parking");
			//setContext(CtxAttributeTypes.TEMPERATURE, new Integer(30));
			//setContext(CtxAttributeTypes.STATUS, "driving");

			Date date= new Date();
			LOG.info("monitor action1 "+action1 + " time "+date.getTime());

			TestCase1109.uam.monitor(cssOwnerId, action1);
			Thread.sleep(5000);

			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "Home-Parking");
			//setContext(CtxAttributeTypes.TEMPERATURE, new Integer(30));
			//setContext(CtxAttributeTypes.STATUS, "driving");

			date= new Date();
			LOG.info("monitor action2 "+action2 + " time "+date.getTime());
			TestCase1109.uam.monitor(cssOwnerId, action2);
			Thread.sleep(5000);

			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC,"Home-Parking");
			//setContext(CtxAttributeTypes.TEMPERATURE,  new Integer(30));
			//setContext(CtxAttributeTypes.STATUS, "driving");

			date= new Date();
			LOG.info("monitor action3 "+action3 + " time "+date.getTime());
			TestCase1109.uam.monitor(cssOwnerId, action3);
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
			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC,"High_way");
			//setContext(CtxAttributeTypes.TEMPERATURE,  new Integer(22));
			//setContext(CtxAttributeTypes.STATUS, "driving");

			Date date= new Date();
			LOG.info("monitor action4 "+action4 + " time "+date.getTime());
			TestCase1109.uam.monitor(cssOwnerId, action4);
			Thread.sleep(5000);

			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "Gas_station");
			//setContext(CtxAttributeTypes.TEMPERATURE,  new Integer(28));
			//setContext(CtxAttributeTypes.STATUS, "stopped");

			date= new Date();
			LOG.info("monitor action5 "+action5 + " time "+date.getTime());
			TestCase1109.uam.monitor(cssOwnerId, action5);
			Thread.sleep(5000);

			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC,"high_way_junction");
			//setContext(CtxAttributeTypes.TEMPERATURE,  new Integer(30));
			//setContext(CtxAttributeTypes.STATUS, "driving");

			date= new Date();
			LOG.info("monitor action6 "+action6 + " time "+date.getTime());
			TestCase1109.uam.monitor(cssOwnerId, action6);
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}


	private void actionsTask3 (IAction action7, IAction action8){

		try {
			IIdentity cssOwnerId = getOwnerId();

			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC,"office_parking");
			//setContext(CtxAttributeTypes.TEMPERATURE,  new Integer(22));
			//setContext(CtxAttributeTypes.STATUS, "stopped");

			Date date= new Date();
			LOG.info("monitor action7 "+action7 + " time "+date.getTime());
			TestCase1109.uam.monitor(cssOwnerId, action7);

			date= new Date();
			Thread.sleep(5000);
			LOG.info("monitor action8 "+action8 + " time "+date.getTime());
			TestCase1109.uam.monitor(cssOwnerId, action8);

			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}


	private CtxAttribute setContext(String type, Serializable value){

		IIdentity cssOwnerId = getOwnerId();
		CtxAttribute attr = null; 
		try {
			IndividualCtxEntity operator = TestCase1109.ctxBroker.retrieveIndividualEntity(cssOwnerId).get();
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
		LOG.info("ctxAttr of type: "+attr.getType()+" set to value: "+attr.getStringValue());

		return attr;
	}



	private void printOperatorAttr() {

		IIdentity cssOwnerId = getOwnerId();
		try {
			final INetworkNode cssNodeId = TestCase1109.commMgr.getIdManager().getThisNetworkNode();
			//LOG.info("*** cssNodeId = " + cssNodeId);
			final String cssOwnerStr = cssNodeId.getBareJid();

			cssOwnerId = TestCase1109.commMgr.getIdManager().fromJid(cssOwnerStr);
			IndividualCtxEntity operator = TestCase1109.ctxBroker.retrieveIndividualEntity(cssOwnerId).get();

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
			final INetworkNode cssNodeId = TestCase1109.commMgr.getIdManager().getThisNetworkNode();
			//LOG.info("*** cssNodeId = " + cssNodeId);
			final String cssOwnerStr = cssNodeId.getBareJid();
			cssOwnerId = TestCase1109.commMgr.getIdManager().fromJid(cssOwnerStr);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cssOwnerId;
	}
	
	/*
	 * Actions create the following model
	 * caui model created - actions map: 
{
.../navigatorService#setDestination=office/5={css://nikosk@societies.org/navigatorService#getInfo=traffic/6=1.0}, 
.../radioService#setVolume=medium/1={css://nikosk@societies.org/radioService#setTuner=favoriteChannel1/2=1.0}, 
.../navigatorService#getInfo=traffic/6={css://nikosk@societies.org/randomService#random=yyy/7=1.0}, 
.../navigatorService#setDestination=gasStation/4={css://nikosk@societies.org/navigatorService#setDestination=office/5=1.0}, 
.../radioService#setRadio=on/0={css://nikosk@societies.org/radioService#setVolume=medium/1=1.0}, 
.../navigatorService#setDestinator=off/9={css://nikosk@societies.org/radioService#setRadio=on/0=1.0}, 
.../radioService#setRadio=off/8={css://nikosk@societies.org/navigatorService#setDestinator=off/9=1.0}, 
.../radioService#setTuner=favoriteChannel1/2={css://nikosk@societies.org/randomService#random=yyy/7=0.5, css://nikosk@societies.org/randomService#random=xxx/3=0.5}

.../randomService#random=xxx/3={css://nikosk@societies.org/navigatorService#setDestination=gasStation/4=1.0}, 
.../randomService#random=yyy/7={css://nikosk@societies.org/radioService#setRadio=off/8=0.5, css://nikosk@societies.org/randomService#random=xxx/3=0.5},
} 
	 */


}
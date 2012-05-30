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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.CRISTUserIntentPrediction.impl.CRISTUserIntentPrediction;
import org.societies.personalisation.CRISTUserIntentTaskManager.impl.CRISTUserIntentTaskManager;


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

	
	private static Logger LOG = LoggerFactory.getLogger(TestCRISTUserIntentPrediction.class);
	public static ICtxBroker ctxBroker;
	public static IUserActionMonitor uam;
	
	private CRISTUserIntentPrediction cristPredictor;
	private IIdentity identity;


	

	public void setCtxBroker(ICtxBroker ctxBroker){
		TestCRISTUserIntentPrediction.ctxBroker = ctxBroker;
	}

	protected static ICtxBroker getCtxBroker(){
		return TestCRISTUserIntentPrediction.ctxBroker;
	}
	
	public void setUam(IUserActionMonitor uam){
		TestCRISTUserIntentPrediction.uam = uam;
	}
	
	protected static IUserActionMonitor getUam(){
		return TestCRISTUserIntentPrediction.uam;
	}
	
	
	@Ignore
	@Test
	public void monitorActionsContext()
	{
		
		identity = new MockIdentity(IdentityType.CSS, "user", "societies.org");
		
		ServiceResourceIdentifier serviceId_music = new ServiceResourceIdentifier();
		ServiceResourceIdentifier serviceId_checkin = new ServiceResourceIdentifier();
		try {
			serviceId_music.setIdentifier(new URI("http://testService_music"));
			serviceId_checkin.setIdentifier(new URI("http://testService_checkin"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		LOG.info("#TestCRIST - sending mock actions for storage");

		//set context data
		//setContext(CtxAttributeTypes.SITUATION, "Study Hall");
		//setContext(CtxAttributeTypes.LIGHT, 100);
	setContext("SOUND", 30);
		setContext(CtxAttributeTypes.TEMPERATURE, 22);
		//setContext(CtxAttributeTypes.GPS, "N/A");

		//send actions - 1 second apart
		IAction action1 = new Action(serviceId_music, "musicService", "switch", "on");
		TestCRISTUserIntentPrediction.uam.monitor(identity, action1);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		
		//setContext(CtxAttributeTypes.SITUATION, "Study Hall");
		//setContext(CtxAttributeTypes.LIGHT, 100);
		//setContext(CtxAttributeTypes.SOUND, 30);
		setContext(CtxAttributeTypes.TEMPERATURE, 22);
		//setContext(CtxAttributeTypes.GPS, "N/A");
		
		IAction action2 = new Action(serviceId_music, "musicService", "volume", "low");
		TestCRISTUserIntentPrediction.uam.monitor(identity, action2);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//...
		
		/*
		 * CHECK HISTORY DATA
		 */
		LOG.info("*********** CHECK HISTORY DATA ************");
		List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults = new HashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		try {
			tupleResults  = TestCRISTUserIntentPrediction.ctxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, listOfEscortingAttributeIds, null, null).get();
			Assert.assertTrue(tupleResults.size() == 2);
			printHocTuplesDB(tupleResults);
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
	}
	
	private CtxAttribute setContext(String type, Serializable value){

		CtxAttribute attr = null; 
		try {
			IndividualCtxEntity operator = TestCRISTUserIntentPrediction.ctxBroker.retrieveCssOperator().get();

			Set<CtxAttribute> ctxAttrSet = operator.getAttributes(type);
			if(ctxAttrSet.size()>0 ){
				ArrayList<CtxAttribute> ctxAttrList = new ArrayList<CtxAttribute>(ctxAttrSet);	
				attr = ctxAttrList.get(0);
				attr = TestCRISTUserIntentPrediction.ctxBroker.updateAttribute(attr.getId(), value).get();
			} else {
				attr = TestCRISTUserIntentPrediction.ctxBroker.createAttribute(operator.getId(), type).get();
				attr = TestCRISTUserIntentPrediction.ctxBroker.updateAttribute(attr.getId(),value).get();
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
	
	
	
	
	/*
	@Before
	public void setUp() throws Exception {
		
		cristPredictor = new CRISTUserIntentPrediction();
		CRISTUserIntentTaskManager cristTaskManager = (CRISTUserIntentTaskManager) cristPredictor.getCristTaskManager();
		
		//mock data
		cristTaskManager.initialiseCRISTUserIntentManager();
		//myID = ;//How to construct?
		//serviceID = 
		//CtxEntityIdentifier ctxEntityIdentifier = new CtxEntityIdentifier("UserSurrounding"); 
		//myCtx = new CtxAttribute(new CtxAttributeIdentifier(ctxEntityIdentifier, "Light", 123456789888L));
		//myCtx.setStringValue("30");//dark
		//cristPredictor.enableCRISTPrediction(true);

	}

	
	@Ignore
	@Test
	public void testGetCRISTPredictionIIdentityCtxAttribute() {
		Future<List<CRISTUserAction>> results = cristPredictor
				.getCRISTPrediction(myID, myCtx);
		
		//fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testGetCRISTPredictionIIdentityIAction() {
		Future<List<CRISTUserAction>> results = cristPredictor
				.getCRISTPrediction(myID, myAction);
		
	}

	@Ignore("Not yet implemented")
	@Test
	public void testGetCurrentUserIntentAction() {
		Future<CRISTUserAction> result = cristPredictor
				.getCurrentUserIntentAction(myID, 
						serviceID, "volume");
		fail("Not yet implemented");
	}
*/
}

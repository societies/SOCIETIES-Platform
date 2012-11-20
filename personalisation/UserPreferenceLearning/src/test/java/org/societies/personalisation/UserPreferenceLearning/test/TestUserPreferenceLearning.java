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

package org.societies.personalisation.UserPreferenceLearning.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.Action;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceLearning.impl.UserPreferenceLearning;
import org.societies.personalisation.preference.api.model.IC45Consumer;
import org.societies.personalisation.preference.api.model.IC45Output;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.springframework.scheduling.annotation.AsyncResult;

import junit.framework.Assert;
import junit.framework.TestCase;

import static org.mockito.Mockito.*;

public class TestUserPreferenceLearning extends TestCase implements IC45Consumer{

	NumberGenerator ng;
	ICtxBroker mockCtxBroker;
	UserPreferenceLearning prefLearning;
	IIdentity mockID;
	String mockCssOperator;
	ServiceResourceIdentifier mockServiceID_A;
	ServiceResourceIdentifier mockServiceID_B;
	//Date startDate;
	List<CtxAttributeIdentifier> emptyList;
	Future<Map<CtxHistoryAttribute, List<CtxHistoryAttribute>>> history;
	Future<Map<CtxHistoryAttribute, List<CtxHistoryAttribute>>> sparseHistory;
	List<IC45Output> results;

	public void setUp() throws Exception {
		mockCtxBroker = mock(ICtxBroker.class);
		prefLearning = new UserPreferenceLearning();
		prefLearning.setCtxBroker(mockCtxBroker);
		mockID = new MockIdentity(IdentityType.CSS, "sarah", "societies.org");
		mockCssOperator = "mockFooIdentity";
		mockServiceID_A = new ServiceResourceIdentifier();
		mockServiceID_A.setIdentifier(new URI("http://testServiceA"));
		mockServiceID_A.setServiceInstanceIdentifier("testServiceA");
		mockServiceID_B = new ServiceResourceIdentifier();
		mockServiceID_B.setIdentifier(new URI("http://testServiceB"));
		mockServiceID_B.setServiceInstanceIdentifier("testServiceB");
		//startDate = new Date();
		emptyList = new ArrayList<CtxAttributeIdentifier>();
		ng = new NumberGenerator();
		history = new AsyncResult<Map<CtxHistoryAttribute, List<CtxHistoryAttribute>>>(this.getDataset());
		sparseHistory = new AsyncResult<Map<CtxHistoryAttribute, List<CtxHistoryAttribute>>>(this.getSparseDataset());
		results = null;
	}

	public void tearDown() throws Exception {
		mockCtxBroker = null;
		prefLearning = null;
		mockID = null;
		mockCssOperator = null;
		mockServiceID_A = null;
		mockServiceID_B = null;
		//startDate = null;
		history = null;
		ng = null;
		results = null;
	}

	//test condition situation for all actions and all identities
	@Test
	public void testNoCondition(){
		System.out.println("Running test 1...");
		try {
			printDataset(sparseHistory.get());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
		Date startDate = new Date();
		when(mockCtxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, new ArrayList<CtxAttributeIdentifier>(), startDate, null))
		.thenReturn(sparseHistory);
		
		prefLearning.runC45Learning(this, startDate);
		
		//check return
		
		int counter = 10;
		while (results == null && counter > 0){
			try{
				Thread.sleep(1000);
				counter--;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		verify(mockCtxBroker).retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, new ArrayList<CtxAttributeIdentifier>(), startDate, null);
		
		Assert.assertNotNull(results);
		printResults();
	}

	//test all actions and all identities
	@Test
	public void testAA_AI(){
		System.out.println("Running test 2...");
		Date startDate = new Date();
		when(mockCtxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, new ArrayList<CtxAttributeIdentifier>(), startDate, null))
		.thenReturn(history);

		prefLearning.runC45Learning(this, startDate);

		//check return
		int counter = 10;
		while(results == null && counter > 0){
			try {
				Thread.sleep(1000);
				counter --;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		verify(mockCtxBroker).retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, new ArrayList<CtxAttributeIdentifier>(), startDate, null);

		Assert.assertNotNull(results);
		printResults();
	}

	//test all actions and specific identity
	@Test
	public void testAA_SI(){
		System.out.println("Running test 3...");
		Date startDate = new Date();
		when(mockCtxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, new ArrayList<CtxAttributeIdentifier>(), startDate, null))
		.thenReturn(history);

		prefLearning.runC45Learning(this, startDate, mockID);		

		//check return
		int counter = 10;
		while(results == null && counter > 0){
			try {
				Thread.sleep(1000);
				counter --;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		verify(mockCtxBroker).retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, new ArrayList<CtxAttributeIdentifier>(), startDate, null);

		Assert.assertNotNull(results);
		printResults();
	}

	//test specific action and all identities
	@Test
	public void testSA_AI(){
		System.out.println("Running test 4...");
		Date startDate = new Date();
		when(mockCtxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, new ArrayList<CtxAttributeIdentifier>(), startDate, null))
		.thenReturn(history);

		prefLearning.runC45Learning(this, startDate, mockServiceID_A, "tennis1");	

		//check return
		int counter = 10;
		while(results == null && counter > 0){
			try {
				Thread.sleep(1000);
				counter --;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		verify(mockCtxBroker).retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, new ArrayList<CtxAttributeIdentifier>(), startDate, null);

		Assert.assertNotNull(results);
		printResults();
	}

	//test specific action and specific identity
	@Test
	public void testSA_SI(){
		System.out.println("Running test 5...");
		Date startDate = new Date();
		when(mockCtxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, new ArrayList<CtxAttributeIdentifier>(), startDate, null))
		.thenReturn(history);

		prefLearning.runC45Learning(this, startDate, mockID, mockServiceID_A, "tennis1");

		//check return
		int counter = 10;
		while(results == null && counter > 0){
			try {
				Thread.sleep(1000);
				counter --;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		verify(mockCtxBroker).retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, new ArrayList<CtxAttributeIdentifier>(), startDate, null);

		Assert.assertNotNull(results);
		printResults();
	}

	private void printResults(){
		for(IC45Output nextOutput: results){
			System.out.println("**********************RESULTS**************************************");
			System.out.println("Data Owner: "+nextOutput.getOwner());
			System.out.println("Service ID: "+nextOutput.getServiceId());
			System.out.println("Service Type: "+nextOutput.getServiceType());
			List<IPreferenceTreeModel> trees = nextOutput.getTreeList();
			for(IPreferenceTreeModel nextTree: trees){
				System.out.println("-----------------------------------------------------");
				IPreference preference = nextTree.getRootPreference();
				System.out.println(preference.toTreeString());
			}
			System.out.println("-----------------------------------------------------");
		}
		System.out.println("**************************END**********************************");
	}


	/*
	 * PreProcessor tests

	 *//**
	 * This test should extract a list of actions for a particular serviceId from the dataset
	 *//*
	public void testServiceActionExtraction(){
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> dataset = this.getFullDataset();
		System.out.println("*************************************************");
		System.out.println("TEST - Service Action Extraction");
		System.out.println("*************************************************");
		ServiceSubset results = pre.extractServiceActions(dataset, serviceId1, "tennis1");
		Assert.assertNotNull(results);

		ServiceResourceIdentifier serviceId = results.getServiceId();
		System.out.println("serviceId = "+serviceId.getIdentifier());
		Assert.assertNotNull(serviceId);
		Assert.assertEquals(serviceId1.getIdentifier(), serviceId.getIdentifier());

		List<ActionSubset> actionList = results.getActionSubsets();
		Assert.assertNotNull(actionList);
		System.out.println("action list size = "+actionList.size());
		Assert.assertTrue(actionList.size() == 1);

		for(ActionSubset nextSubset: actionList){
			String actionType = nextSubset.getParameterName();
			Assert.assertNotNull(actionType);
			System.out.println("action parameter name = "+actionType);
			Assert.assertEquals("tennis1", actionType);
			Assert.assertTrue(nextSubset.size() == 7);
			System.out.println("action list size = "+nextSubset.size());

			//Print action subsets
			for(Iterator<CtxHistoryAttribute> nextSubset_it = nextSubset.keySet().iterator(); nextSubset_it.hasNext();){
				CtxHistoryAttribute nextAttr = nextSubset_it.next();
				List<CtxHistoryAttribute> context = nextSubset.get(nextAttr);
				try {
					Action action = (Action) SerialisationHelper.deserialise(nextAttr.getBinaryValue(), this.getClass().getClassLoader());
					System.out.println(action.toString());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				for(CtxHistoryAttribute nextContext: context){
					System.out.println(nextContext.getType()+" = "+nextContext.getStringValue());
				}
			}
			System.out.println("DONE PRINTING");
		}

		System.out.println();
		System.out.println("-----------------------------------------------------------");
		System.out.println();
	}

	  *//**
	  * This test should return a list of ServiceSubsets - one for each serviceId in the dataset
	  *//*
	public void testSplitHistory(){
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> dataset = this.getFullDataset();
		System.out.println("*************************************************");
		System.out.println("TEST - Split History");
		System.out.println("*************************************************");
		List<ServiceSubset> results = pre.splitHistory(dataset);
		Assert.assertNotNull(results);
		Assert.assertTrue(results.size() == 2);
		System.out.println("service list size = "+results.size());

		for(ServiceSubset nextService: results){
			ServiceResourceIdentifier serviceId = nextService.getServiceId();
			Assert.assertNotNull(serviceId);
			System.out.println("serviceId = "+serviceId.getIdentifier());

			List<ActionSubset> actionList = nextService.getActionSubsets();
			Assert.assertNotNull(actionList);
			System.out.println("action list size = "+actionList.size());
			Assert.assertTrue(actionList.size() == 2);

			for(ActionSubset nextSubset: actionList){
				String actionType = nextSubset.getParameterName();
				Assert.assertNotNull(actionType);
				System.out.println("action parameter name = "+actionType);
				Assert.assertTrue(nextSubset.size() > 0);
				System.out.println("action subset size = "+nextSubset.size());

				//Print action subsets
				for(Iterator<CtxHistoryAttribute> nextSubset_it = nextSubset.keySet().iterator(); nextSubset_it.hasNext();){
					CtxHistoryAttribute nextAttr = nextSubset_it.next();
					List<CtxHistoryAttribute> context = nextSubset.get(nextAttr);
					try {
						Action action = (Action) SerialisationHelper.deserialise(nextAttr.getBinaryValue(), this.getClass().getClassLoader());
						System.out.println(action.toString());
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					for(CtxHistoryAttribute nextContext: context){
						System.out.println(nextContext.getType()+" = "+nextContext.getStringValue());
					}
				}
			}
		}

		System.out.println();
		System.out.println("-----------------------------------------------------------");
		System.out.println();
	}

	   *//**
	   * This test should return a trimmed ServiceSubset where consistent context attributes have been removed from ActionSubsets
	   *//*
	public void testTrimServiceSubset(){
		ServiceSubset dataset = this.getServiceSubset();
		System.out.println("*************************************************");
		System.out.println("TEST - Trim ServiceSubset");
		System.out.println("*************************************************");
		ServiceSubset results = pre.trimServiceSubset(dataset);
		Assert.assertNotNull(results);
		ServiceResourceIdentifier serviceId = results.getServiceId();
		Assert.assertNotNull(serviceId);
		System.out.println("serviceId = "+serviceId.getIdentifier());

		List<ActionSubset> actionList = results.getActionSubsets();
		Assert.assertNotNull(actionList);
		System.out.println("action list size = "+actionList.size());
		Assert.assertTrue(actionList.size() == 2);

		for(ActionSubset nextSubset: actionList){
			String actionType = nextSubset.getParameterName();
			Assert.assertNotNull(actionType);
			System.out.println("action parameter name = "+actionType);
			Assert.assertTrue(nextSubset.size() > 0);
			System.out.println("action subset size = "+nextSubset.size());

			//Print action subsets
			for(Iterator<CtxHistoryAttribute> nextSubset_it = nextSubset.keySet().iterator(); nextSubset_it.hasNext();){
				CtxHistoryAttribute nextAttr = nextSubset_it.next();
				List<CtxHistoryAttribute> context = nextSubset.get(nextAttr);
				try {
					Action action = (Action) SerialisationHelper.deserialise(nextAttr.getBinaryValue(), this.getClass().getClassLoader());
					System.out.println(action.toString());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				for(CtxHistoryAttribute nextContext: context){
					System.out.println(nextContext.getType()+" = "+nextContext.getStringValue());
				}
			}
		}

		System.out.println();
		System.out.println("-----------------------------------------------------------");
		System.out.println();
	}

	    *//**
	    * This test should translate the ActionSubset into Weka instances
	    *//*
	public void testToInstances(){
		ActionSubset dataset = this.getActionSubset();
		System.out.println("*************************************************");
		System.out.println("TEST - to Instances");
		System.out.println("*************************************************");
		Instances results = pre.toInstances(dataset);
		Assert.assertNotNull(results);
		results.toString();

		System.out.println();
		System.out.println("-----------------------------------------------------------");
		System.out.println();
	}
	     */
	/*
	 * PostProcessor tests
	 */
	/*	public void testPostProcessor(){
		//String dataset = this.getTreeString();
		//EntityIdentifier ownerId = new EntityIdentifier();
		//IPreferenceTreeModel preference = post.process(ownerId, "tennis", dataset, cache, serviceId1, "testService"); 
	}*/





	/*
	 * Dataset methods
	 */
	private Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> getDataset(){
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> fulldataset = 
				new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();

		//Create EntityIdentifier
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(mockCssOperator, "testEntity", new Long(12345));

		//extract tennis dataset
		//ServiceId = tennisPlanner
		String[] tennisActionTypes = {"tennis1", "tennis2"};
		int toggle = 1;
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tennis_data =
				new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		URL url = getClass().getResource("/tennis.txt");
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				String[] instance = strLine.split(",");

				//Create CtxHistoryAttribute
				CtxAttributeIdentifier action_attrId = new CtxAttributeIdentifier(entityId, "action", new Long(12345));
				CtxAttribute action_attribute = new CtxAttribute(action_attrId);
				if(toggle == 1) toggle = 0; else toggle = 1;
				Action value = new Action(mockServiceID_A, "testService", tennisActionTypes[toggle], instance[0]);
				byte[] blobValue = SerialisationHelper.serialise(value);
				action_attribute.setBinaryValue(blobValue);
				CtxHistoryAttribute action = new CtxHistoryAttribute(action_attribute, ng.getNextValue());

				//add context CtxHistoryAttribute attributes
				ArrayList<CtxHistoryAttribute> context = new ArrayList<CtxHistoryAttribute>();
				for(int i=1; i<instance.length; i++){
					CtxAttributeIdentifier context_attrId = new CtxAttributeIdentifier(entityId, "context"+i, new Long(12345));
					CtxAttribute context_attribute = new CtxAttribute(context_attrId);
					context_attribute.setStringValue(instance[i]);
					CtxHistoryAttribute nextContext = new CtxHistoryAttribute(context_attribute, ng.getNextValue());
					context.add(nextContext);
				}
				tennis_data.put(action, context);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//printDataset(tennis_data);

		//extract lymphography dataset
		//ServiceId = lymphChecker;
		String[] lymphActionTypes = {"lymph1", "lymph2"};
		toggle = 1;
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> lymph_data =
				new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		url = getClass().getResource("/lymphography.txt");
		try {
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				String[] instance = strLine.split(",");

				//Create CtxHistoryAttribute
				CtxAttributeIdentifier action_attrId = new CtxAttributeIdentifier(entityId, "action", new Long(12345));
				CtxAttribute action_attribute = new CtxAttribute(action_attrId);
				if(toggle == 1) toggle = 0; else toggle = 1;
				Action value = new Action(mockServiceID_B, "testService", lymphActionTypes[toggle], instance[0]);
				byte[] blobValue = SerialisationHelper.serialise(value);
				action_attribute.setBinaryValue(blobValue);
				CtxHistoryAttribute action = new CtxHistoryAttribute(action_attribute, ng.getNextValue());

				//add context CtxHistoryAttribute attributes
				ArrayList<CtxHistoryAttribute> context = new ArrayList<CtxHistoryAttribute>();
				for(int i=1; i<instance.length; i++){
					CtxAttributeIdentifier context_attrId = new CtxAttributeIdentifier(entityId, "context"+i, new Long(12345));
					CtxAttribute context_attribute = new CtxAttribute(context_attrId);
					context_attribute.setStringValue(instance[i]);
					CtxHistoryAttribute nextContext = new CtxHistoryAttribute(context_attribute, ng.getNextValue());
					context.add(nextContext);
				}
				lymph_data.put(action, context);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//printDataset(lymph_data);

		//merge datasets
		boolean readTennis = true;
		boolean readLymph = true;
		Iterator<CtxHistoryAttribute> tennis_it = tennis_data.keySet().iterator();
		Iterator<CtxHistoryAttribute> lymph_it = lymph_data.keySet().iterator();
		while(readTennis || readLymph){
			if(readTennis){
				if(tennis_it.hasNext()){
					CtxHistoryAttribute nextTennis = tennis_it.next();
					List<CtxHistoryAttribute> nextTennisCtx = tennis_data.get(nextTennis);
					fulldataset.put(nextTennis, nextTennisCtx);
				}else readTennis = false;
			}
			if(readLymph){
				if(lymph_it.hasNext()){
					CtxHistoryAttribute nextLymph = lymph_it.next();
					List<CtxHistoryAttribute> nextLymphCtx = lymph_data.get(nextLymph);
					fulldataset.put(nextLymph, nextLymphCtx);
				}else readLymph = false;
			}
		}

		//printDataset(fulldataset);
		return fulldataset;
	}

	public Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> getSparseDataset(){
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> sparseDataset = 
				new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();

		try{
			//Create EntityIdentifier
			CtxEntityIdentifier entityId = new CtxEntityIdentifier(mockCssOperator, "testEntity", new Long(12345));

			//create context CtxHistoryAttribute attribute
			ArrayList<CtxHistoryAttribute> context = new ArrayList<CtxHistoryAttribute>();
			CtxAttributeIdentifier context_attrId = new CtxAttributeIdentifier(entityId, "locationSymbolic", new Long(12345));
			CtxAttribute context_attribute = new CtxAttribute(context_attrId);
			context_attribute.setStringValue("screen1");
			CtxHistoryAttribute nextContext = new CtxHistoryAttribute(context_attribute, ng.getNextValue());
			context.add(nextContext);
			
			for(int i=0; i<3; i++){
				CtxAttributeIdentifier action_attrId = new CtxAttributeIdentifier(entityId, "action", new Long(i));
				CtxAttribute action_attribute = new CtxAttribute(action_attrId);
				Action value = new Action(mockServiceID_A, "testService", "channel", "1");
				byte[] blobValue = SerialisationHelper.serialise(value);
				action_attribute.setBinaryValue(blobValue);
				CtxHistoryAttribute action = new CtxHistoryAttribute(action_attribute, ng.getNextValue());
				sparseDataset.put(action, context);
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return sparseDataset;
	}

	@Override
	public void handleC45Output(List<IC45Output> results) {
		this.results = results;
	}


	/*	private ServiceSubset getServiceSubset(){
		//Create EntityIdentifier
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(identity, "testEntity", new Long(12345));
		List<ActionSubset> actionSubsets = new ArrayList<ActionSubset>();

		URL url = getClass().getResource("/tennis_singletons.txt");
		ActionSubset tennisActionSubset = new ActionSubset("tennis");
		String[] tennisActionTypes = {"tennis1", "tennis2"};
		int toggle = 1;
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				String[] instance = strLine.split(",");

				CtxAttributeIdentifier action_attrId = new CtxAttributeIdentifier(entityId, "action", new Long(12345));
				CtxAttribute action_attribute = new CtxAttribute(action_attrId);
				if(toggle == 1) toggle = 0; else toggle = 1;
				Action value = new Action(serviceId1, "testService", tennisActionTypes[toggle], instance[0]);
				byte[] blobValue = SerialisationHelper.serialise(value);
				action_attribute.setBinaryValue(blobValue);
				CtxHistoryAttribute action = new CtxHistoryAttribute(action_attribute, ng.getNextValue());

				//add context CtxHistoryAttribute attributes
				ArrayList<CtxHistoryAttribute> context = new ArrayList<CtxHistoryAttribute>();
				for(int i=1; i<instance.length; i++){
					CtxAttributeIdentifier context_attrId = new CtxAttributeIdentifier(entityId, "context"+i, new Long(12345));
					CtxAttribute context_attribute = new CtxAttribute(context_attrId);
					context_attribute.setStringValue(instance[i]);
					CtxHistoryAttribute nextContext = new CtxHistoryAttribute(context_attribute, ng.getNextValue());
					context.add(nextContext);
				}
				tennisActionSubset.put(action, context);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		url = getClass().getResource("/lymphography.txt");
		ActionSubset lymphActionSubset = new ActionSubset("lymph");
		String[] lymphActionTypes = {"lymph1", "lymph2"};
		toggle = 1;
		try {
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				String[] instance = strLine.split(",");

				CtxAttributeIdentifier action_attrId = new CtxAttributeIdentifier(entityId, "action", new Long(12345));
				CtxAttribute action_attribute = new CtxAttribute(action_attrId);
				if(toggle == 1) toggle = 0; else toggle = 1;
				Action value = new Action(serviceId1, "testService", lymphActionTypes[toggle], instance[0]);
				byte[] blobValue = SerialisationHelper.serialise(value);
				action_attribute.setBinaryValue(blobValue);
				CtxHistoryAttribute action = new CtxHistoryAttribute(action_attribute, ng.getNextValue());

				//add context CtxHistoryAttribute attributes
				ArrayList<CtxHistoryAttribute> context = new ArrayList<CtxHistoryAttribute>();
				for(int i=1; i<instance.length; i++){
					CtxAttributeIdentifier context_attrId = new CtxAttributeIdentifier(entityId, "context"+i, new Long(12345));
					CtxAttribute context_attribute = new CtxAttribute(context_attrId);
					context_attribute.setStringValue(instance[i]);
					CtxHistoryAttribute nextContext = new CtxHistoryAttribute(context_attribute, ng.getNextValue());
					context.add(nextContext);
				}
				lymphActionSubset.put(action, context);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		actionSubsets.add(tennisActionSubset);
		actionSubsets.add(lymphActionSubset);
		ServiceSubset serviceSubset = new ServiceSubset(serviceId1, "testService" ,actionSubsets);

		return serviceSubset;
	}


	private ActionSubset getActionSubset(){
		//Create EntityIdentifier
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(identity, "testEntity", new Long(12345));
		ActionSubset actionSubset = new ActionSubset("tennis");

		URL url = getClass().getResource("/tennis.txt");
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				String[] instance = strLine.split(",");

				CtxAttributeIdentifier action_attrId = new CtxAttributeIdentifier(entityId, "action", new Long(12345));
				CtxAttribute action_attribute = new CtxAttribute(action_attrId);
				Action value = new Action(serviceId1, "testService", "tennis", instance[0]);
				byte[] blobValue = SerialisationHelper.serialise(value);
				action_attribute.setBinaryValue(blobValue);
				CtxHistoryAttribute action = new CtxHistoryAttribute(action_attribute, ng.getNextValue());

				//add context CtxHistoryAttribute attributes
				ArrayList<CtxHistoryAttribute> context = new ArrayList<CtxHistoryAttribute>();
				for(int i=1; i<instance.length; i++){
					CtxAttributeIdentifier context_attrId = new CtxAttributeIdentifier(entityId, "context"+i, new Long(12345));
					CtxAttribute context_attribute = new CtxAttribute(context_attrId);
					context_attribute.setStringValue(instance[i]);
					CtxHistoryAttribute nextContext = new CtxHistoryAttribute(context_attribute, ng.getNextValue());
					context.add(nextContext);
				}
				actionSubset.put(action, context);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return actionSubset;
	}
	 */	
	/*

	private String getTreeString(){
		return null;
	}*/

	private void printDataset(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> dataset){
		//printout full dataset test
		for(Iterator<CtxHistoryAttribute> dataset_it = dataset.keySet().iterator(); dataset_it.hasNext();){
			CtxHistoryAttribute printAction = dataset_it.next();
			try {
				Action next_action = (Action) SerialisationHelper.deserialise(printAction.getBinaryValue(), this.getClass().getClassLoader());
				System.out.println(next_action.getServiceID().getIdentifier()+": "+next_action.toString());
				List<CtxHistoryAttribute> printContext = dataset.get(printAction);
				for(CtxHistoryAttribute nextParam : printContext){
					System.out.println(nextParam.getType()+" = "+nextParam.getStringValue());
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}

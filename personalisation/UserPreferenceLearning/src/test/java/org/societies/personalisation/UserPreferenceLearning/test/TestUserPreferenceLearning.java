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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.personalisation.model.Action;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
//import org.societies.personalisation.UserPreferenceLearning.impl.CtxIdentifierCache;
import org.societies.personalisation.UserPreferenceLearning.impl.PostProcessor;
import org.societies.personalisation.UserPreferenceLearning.impl.PreProcessor;
import org.societies.personalisation.preference.api.model.ActionSubset;
import org.societies.personalisation.preference.api.model.ServiceSubset;

import weka.core.Instances;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestUserPreferenceLearning extends TestCase{

	PreProcessor pre;
	PostProcessor post;
	ServiceResourceIdentifier serviceId1;
	ServiceResourceIdentifier serviceId2;
	IIdentity ownerId;
	NumberGenerator ng;

	public void setUp() throws Exception {
		pre = new PreProcessor();
		post = new PostProcessor();
		serviceId1 = new ServiceResourceIdentifier();
		serviceId1.setIdentifier(new URI("tennisPlanner"));
		serviceId2 = new ServiceResourceIdentifier();
		serviceId2.setIdentifier(new URI("lymphChecker"));
		ownerId = new MockIdentity(IdentityType.CSS, "test", "domain");
		ng = new NumberGenerator();
	}

	public void tearDown() throws Exception {
		//null
	}

	/*
	 * PreProcessor tests
	 */
	/**
	 * This test should extract a list of actions for a particular serviceId from the dataset
	 */
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
			/*for(Iterator<CtxHistoryAttribute> nextSubset_it = nextSubset.keySet().iterator(); nextSubset_it.hasNext();){
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
			System.out.println("DONE PRINTING");*/
		}

		System.out.println();
		System.out.println("-----------------------------------------------------------");
		System.out.println();
	}

	/**
	 * This test should return a list of ServiceSubsets - one for each serviceId in the dataset
	 */
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
				/*for(Iterator<CtxHistoryAttribute> nextSubset_it = nextSubset.keySet().iterator(); nextSubset_it.hasNext();){
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
				}*/
			}
		}

		System.out.println();
		System.out.println("-----------------------------------------------------------");
		System.out.println();
	}

	/**
	 * This test should return a trimmed ServiceSubset where consistent context attributes have been removed from ActionSubsets
	 */
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
			/*for(Iterator<CtxHistoryAttribute> nextSubset_it = nextSubset.keySet().iterator(); nextSubset_it.hasNext();){
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
			}*/
		}

		System.out.println();
		System.out.println("-----------------------------------------------------------");
		System.out.println();
	}

	/**
	 * This test should translate the ActionSubset into Weka instances
	 */
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

	/*
	 * PostProcessor tests
	 */
	public void testPostProcessor(){
		//String dataset = this.getTreeString();
		//EntityIdentifier ownerId = new EntityIdentifier();
		//IPreferenceTreeModel preference = post.process(ownerId, "tennis", dataset, cache, serviceId1, "testService"); 
	}





	/*
	 * Dataset methods
	 */
	private Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> getFullDataset(){
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> fulldataset = 
				new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();

		//Create EntityIdentifier
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId, "testEntity", new Long(12345));

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
				Action value = new Action(tennisActionTypes[toggle], instance[0]);
				value.setServiceID(serviceId1);
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
				Action value = new Action(lymphActionTypes[toggle], instance[0]);
				value.setServiceID(serviceId2);
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


	private ServiceSubset getServiceSubset(){
		//Create EntityIdentifier
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId, "testEntity", new Long(12345));
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
				Action value = new Action(tennisActionTypes[toggle], instance[0]);
				value.setServiceID(serviceId1);
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
				Action value = new Action(lymphActionTypes[toggle], instance[0]);
				value.setServiceID(serviceId1);
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
		CtxEntityIdentifier entityId = new CtxEntityIdentifier(ownerId, "testEntity", new Long(12345));
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
				Action value = new Action("tennis", instance[0]);
				value.setServiceID(serviceId1);
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


	private String getTreeString(){
		return null;
	}

	/*private void printDataset(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> dataset){
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
}

/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druΕΎbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAΓ‡ΓƒO, SA (PTIN), IBM Corp., 
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
package org.societies.context.community.estimation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.context.broker.impl.CtxBroker;
import org.societies.context.broker.impl.InternalCtxBroker;
import org.societies.context.community.estimation.impl.CommunityContextEstimation;
import org.societies.context.user.db.impl.UserCtxDBMgr;
import org.societies.context.userHistory.impl.UserContextHistoryManagement;

/**
 * Describe your class here...
 *
 * @author 
 *
 */
public class TE_ST {
	
	/******************************************************************************************
		This component is responsible for collecting the User Context data of the Community members, couple these and infer the respective Context
	information of the entire community. It should support both on-demand, as well as continuous Community Context Estimation.
	It will require the usage of the Context Similarity Evaluation component and of the WP4 eventing facilities, so that it automatically identifies when
	the Community Context values need to be updated due to changes to the respective User Context values of the specific community members.
	Various models can be used to represent and estimate the Community Context values, such as: mere aggregation of the values of the User Context
	data of the Community members, stochastic representation of the above (mainly for the discrete or enumerated context value formats), average
	values (for the discrete or continuous context value formats), median values (for the discrete or enumerated context value formats), most probable value, etc.
 
	 ******************************************************************************************/

	private InternalCtxBroker internalCtxBroker;
	CtxEntityIdentifier ctxEntityIdentifier = null;
	CtxIdentifier ctxAttributeStringIdentifier = null;
	CtxIdentifier ctxAttributeBinaryIdentifier = null;
	Logger log;
	
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		internalCtxBroker = new InternalCtxBroker();
		internalCtxBroker.setUserCtxDBMgr(new UserCtxDBMgr());
		internalCtxBroker.setUserCtxHistoryMgr(new UserContextHistoryManagement());
		new CommunityContextEstimation();

		
		Long objectNumber = null;
		String type = null;
	
		
		new CommunityCtxEntity(ctxEntityId);
		
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		internalCtxBroker = null;
	}
	
	@Test
	public void testComCtxEst() throws CtxException, InterruptedException, ExecutionException{
	
		//CommunityContextEstimation cce = new CommunityContextEstimation();
			
		final CtxAttribute ctxAttribute;
		final CtxEntity ctxEntity;
		
		final Future<CtxEntity> futureCtxEntity = internalCtxBroker.createEntity("entType");
		ctxEntity = futureCtxEntity.get();
		
		Future<CtxAttribute> futureCtxAttribute = internalCtxBroker.createAttribute(ctxEntity.getId(), "attrType");
		ctxAttribute = futureCtxAttribute.get();
		
		assertNotNull(ctxAttribute.getId());
		assertEquals(ctxAttribute.getId().getScope(), ctxEntity.getId());
		assertTrue(ctxAttribute.getType().equalsIgnoreCase("attrType"));
		
	}
//	@Test
	//public List<CtxAttribute> getListOfCtxAttributes(List<CommunityCtxEntity> lst, CtxEntity ctxId) {
//		public void getListOfCtxAttributes() {
//		/*
//		 * This method will return a list of attributes. As input it will have to have the attributes of a given (common CIS)
//		 * (we will have to use communityCtxEntity)
//		 */
//		
//		/*Step0: use the getMembers() of the communityContextEntity class to retrieve communityCtxEntitys
//		 * 
//		 */
//		
	//		/*
//		 * Step1: Use CtxBroker to access the Attributes
//		 */
//		System.out.println("Hello");
//		System.out.println(communityContextEntity.getMembers());
//		//		
//		/*
//		 *Step2: Populate the list
//		 * 
//		 */
//		
//		/*
//		 * Return the list
//		 */
//		//return null;
		
//	}
	
	@Test
	public void TestCalculation() throws CtxException, Exception, Exception{
		
		CtxBroker brok = Mockito.mock(CtxBroker.class);

		//Create a futureEnt

		Future<CtxEntity> futureEnt;
		
		try {
			futureEnt = this.internalCtxBroker.createEntity("Device");
			CtxEntity ctxEntity = (CtxEntity) futureEnt.get();
			System.out.println("1. CtxEntity (futureEnt.get()="+ctxEntity); //= org.societies.api.context.model.CtxEntity@8e08ed4f
						
			//get the context identifier of the created entity (to be used at the next step)
			
			this.ctxEntityIdentifier = ctxEntity.getId();
			System.out.println("2. ctxEntity.getId()="+ctxEntity.getId()); //=null/ENTITY/Device/2
			
			System.out.println("3. get the contextEntity ID:"+ctxEntity.getId()); //null/ENTITY/DeviceID/2
			
			//create ctxAttribute with a String value that it is assigned to the previously created ctxEntity
			Future<CtxAttribute> futureCtxAttrString = this.internalCtxBroker.createAttribute(this.ctxEntityIdentifier, "DeviceID"); //null/ENTITY/Device/2
			// get the object of the created CtxAttribute
			CtxAttribute ctxAttributeString = (CtxAttribute) futureCtxAttrString.get();

			// by setting this flag to true the CtxAttribute values will be stored to Context History Database upon update
			ctxAttributeString.setHistoryRecorded(true);

			// set a string value to CtxAttribute
			ctxAttributeString.setStringValue("device1234");

			// with this update the attribute is stored in Context DB
			Future<CtxModelObject> futureAttrUpdated = this.internalCtxBroker.update(ctxAttributeString);
			
			// get the updated CtxAttribute object and identifier (to be used later for retrieval purposes)
			ctxAttributeString = (CtxAttribute) futureAttrUpdated.get();
			this.ctxAttributeStringIdentifier = ctxAttributeString.getId();
			
			//create a ctxAttribute with a Binary value that is assigned to the same CtxEntity
			Future<CtxAttribute> futureCtxAttrBinary = this.internalCtxBroker.createAttribute(ctxEntity.getId(), "CustomData");
			//System.out.println("ctxAttributeStringIdentifier:"+ctxAttributeStringIdentifier);
			//System.out.println("ctxAttributeString ID:"+ ctxAttributeString.getId());
			
		} catch (CtxException e) {
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
	
		
		
		
		
		
		CtxBroker brok = Mockito.mock(CtxBroker.class);
		
		
		
		//System.out.println("brok.retrieveCommunityMembers is: "+brok.retrieveCommunityMembers(null, null));
		
		
//		ExecutorService executor = Executors.newFixedThreadPool(1);
//		executor = Executors.newCachedThreadPool();
//		
//		Future<?> f1 = executor.submit(new CtxEntity(null));
//		
//
//		// reject new tasks, must call in order to exit VM
//		executor.shutdown();
//		
//		Mockito.when(brok.createEntity(null, null)).then(fe);
//		Future<?> fe = E


		//brok.createEntity(requester, "person");
		
		
		//System.out.println("brok.createEntity is: "+brok.createEntity(null,"person"));
		
	
		//CommunityContextEstimation c = Mockito.mock(CommunityContextEstimation.class);

	
	
		//Mockito.when(c.getAllCommunityMembers(null, null)).then(new Future<List<CtxEntityIdentifier>>())
		
		//CommunityContextEstimation cce = new CommunityContextEstimation();
		
		//cce.setB(null);
		// me Mokito tha eixa 
		//cce.retrieveCisMembersWitPredefinedAttr.addresult("objects pou tha epistrefei" (checkare pos orizetai))
		//cce.retrieveMembersAttribute.addresult ....
		
		//ftiaxno tis input parameters pou xreiazontai pio kato...
		
		//cce.estimateContext(null, null);
		//Integer actual = 5;
		
		//Integer expectedResult =5; 
		
		//assertEquals(expectedResult, actual);
		
		//prepei na vgalo to estimation model
	//}
	
	public double calculateMeanOfIntegerValues(){
		/*
		 * Input: An array of values 
		 * Output: the mean of these values
		 * 
		 */
		
		return 0;
		
	}

	
	
	//@Test
	public void createContext(){
		
		Future<CtxEntity> futureEnt;
		
		try {
			futureEnt = this.internalCtxBroker.createEntity("Device");
			CtxEntity ctxEntity = (CtxEntity) futureEnt.get();
			System.out.println("1. CtxEntity (futureEnt.get()="+ctxEntity); //= org.societies.api.context.model.CtxEntity@8e08ed4f
						
			//get the context identifier of the created entity (to be used at the next step)
			
			this.ctxEntityIdentifier = ctxEntity.getId();
			System.out.println("2. ctxEntity.getId()="+ctxEntity.getId()); //=null/ENTITY/Device/2
			
			System.out.println("3. get the contextEntity ID:"+ctxEntity.getId()); //null/ENTITY/DeviceID/2
			
			//create ctxAttribute with a String value that it is assigned to the previously created ctxEntity
			Future<CtxAttribute> futureCtxAttrString = this.internalCtxBroker.createAttribute(this.ctxEntityIdentifier, "DeviceID"); //null/ENTITY/Device/2
			// get the object of the created CtxAttribute
			CtxAttribute ctxAttributeString = (CtxAttribute) futureCtxAttrString.get();

			// by setting this flag to true the CtxAttribute values will be stored to Context History Database upon update
			ctxAttributeString.setHistoryRecorded(true);

			// set a string value to CtxAttribute
			ctxAttributeString.setStringValue("device1234");

			// with this update the attribute is stored in Context DB
			Future<CtxModelObject> futureAttrUpdated = this.internalCtxBroker.update(ctxAttributeString);
			
			// get the updated CtxAttribute object and identifier (to be used later for retrieval purposes)
			ctxAttributeString = (CtxAttribute) futureAttrUpdated.get();
			this.ctxAttributeStringIdentifier = ctxAttributeString.getId();
			
			//create a ctxAttribute with a Binary value that is assigned to the same CtxEntity
			Future<CtxAttribute> futureCtxAttrBinary = this.internalCtxBroker.createAttribute(ctxEntity.getId(), "CustomData");
			//System.out.println("ctxAttributeStringIdentifier:"+ctxAttributeStringIdentifier);
			//System.out.println("ctxAttributeString ID:"+ ctxAttributeString.getId());
			
		} catch (CtxException e) {
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
	public void retrieveContext() {
		System.out.println("0.");
		// if the CtxEntityID or CtxAttributeID is known the retrieval is performed by using the ctxBroker.retrieve(CtxIdentifier) method
		try {
			System.out.println("0.1");
		
		// retrieve ctxEntity
		// This retrieval is performed based on the known CtxEntity identifier
		// Retrieve is also possible to be performed based on the type of the CtxEntity. This will be demonstrated in a later example.
		Future<CtxModelObject> ctxEntityRetrievedFuture = this.internalCtxBroker.retrieve(this.ctxEntityIdentifier);
		System.out.println("0.2:" + ctxEntityRetrievedFuture.get());
		CtxEntity retrievedCtxEntity = (CtxEntity) ctxEntityRetrievedFuture.get();
		System.out.println("0.2:" + retrievedCtxEntity.getId());

		this.log.info("Retrieved ctxEntity id " +retrievedCtxEntity.getId()+ " of type: "+retrievedCtxEntity.getType());
		System.out.println("4. " );
		System.out.println("0.3");

		// retrieve the CtxAttribute contained in the CtxEntity with the string value
		// again the retrieval is based on an known identifier, it is possible to retrieve it based on type.This will be demonstrated in a later example.
		Future<CtxModelObject> ctxAttributeRetrievedStringFuture = this.internalCtxBroker.retrieve(this.ctxAttributeStringIdentifier);
		CtxAttribute retrievedCtxAttribute = (CtxAttribute) ctxAttributeRetrievedStringFuture.get();
		this.log.info("Retrieved ctxAttribute id " +retrievedCtxAttribute.getId()+ " and value: "+retrievedCtxAttribute.getStringValue());

		// retrieve ctxAttribute with the binary value
		Future<CtxModelObject> ctxAttributeRetrievedBinaryFuture = this.internalCtxBroker.retrieve(this.ctxAttributeBinaryIdentifier);
		CtxAttribute ctxAttributeRetrievedBinary = (CtxAttribute) ctxAttributeRetrievedBinaryFuture.get();

		//deserialize object
//		MockBlobClass retrievedBlob = (MockBlobClass) SerialisationHelper.deserialise(ctxAttributeRetrievedBinary.getBinaryValue(), this.getClass().getClassLoader());
//		this.log.info("Retrieved ctxAttribute id " +ctxAttributeRetrievedBinary.getId()+ "and value: "+ retrievedBlob.toString());

		} catch (CtxException e) {
		// TODO Auto-generated catch block
			System.out.println("5.");
		e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("6.");		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (ExecutionException e) {
			System.out.println("7.");
		// TODO Auto-generated catch block
		e.printStackTrace();
		}

		}
}
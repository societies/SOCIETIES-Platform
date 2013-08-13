/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM Corp., 
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
package org.societies.android.platform.context.test;

import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.android.api.context.CtxException;
import org.societies.android.api.context.ICtxClient;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.internal.cssmanager.CSSManagerEnums.entityType;
import org.societies.android.platform.context.container.TestAndroidContextBroker;
import org.societies.android.platform.context.container.TestAndroidContextBroker.TestContextBrokerBinder;
import org.societies.android.platform.context.ContextBrokerBase;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * @author pkosmides
 *
 */
public class TestSocietiesAndroidContext extends ServiceTestCase <TestAndroidContextBroker>{

	private static final String LOG_TAG = TestSocietiesAndroidContext.class.getName();
	private static final String CLIENT_ID = "org.societies.android.platform.context.test";
	private static final int DELAY = 20001;

	private static final String REQUESTOR_ID = "jane.societies.local";
	
	private ICtxClient ctxBrokerService;
	private long testStartTime, testEndTime;
    private boolean testCompleted;
	private CountDownLatch serviceStartedSignal;
	private CountDownLatch testDoneSignal;
	
	private BroadcastReceiver receiver;	
    
//	private ARequestor requestor;
	private RequestorBean requestor;
	private Boolean receivedResult = false;

	private CtxEntityBean entity;
	private CtxAttributeBean attribute, updatedAttribute;
	private CtxAssociationBean association;
	private CtxModelObjectBean retrievedModelObject;
	private CtxEntityIdentifierBean retrievedIndEntityId, retrievedCommunityEntityId;
	
	public TestSocietiesAndroidContext() {
		super(TestAndroidContextBroker.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		this.receiver = this.setupBroadcastReceiver();
		this.serviceStartedSignal = new CountDownLatch(1);
		Intent commsIntent = new Intent(getContext(), TestAndroidContextBroker.class);
        TestContextBrokerBinder binder = (TestContextBrokerBinder) bindService(commsIntent);
        assertNotNull(binder);
        this.ctxBrokerService = (ICtxClient) binder.getService();
        this.ctxBrokerService.startService();
//		Thread.sleep(DELAY);
        assertTrue(this.serviceStartedSignal.await(DELAY, TimeUnit.MILLISECONDS));
	}
	
	protected void tearDown() throws Exception {
		this.unregisterReceiver(this.receiver);
		Thread.sleep(DELAY);
        //ensure that service is shutdown to test if service leakage occurs
        super.shutdownService();
		super.tearDown();
	}

    private void unregisterReceiver(BroadcastReceiver receiver) {
        Log.d(LOG_TAG, "Unregister broadcast receiver");
        getContext().unregisterReceiver(receiver);
    }
    
	private class MainReceiver extends BroadcastReceiver{
//		private final String LOG_TAG = MainReceiver.class.getName();
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "ContextBrokerTest - Received action: " + intent.getAction());
			
			if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {
				final boolean serviceStarted = intent.getBooleanExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false);
				Log.d(LOG_TAG, "Service started: " + serviceStarted);
				assertTrue("Service not started", serviceStarted);
				TestSocietiesAndroidContext.this.serviceStartedSignal.countDown();
				return;
			}
			else if (intent.getAction().equals(ICtxClient.CREATE_ENTITY)) {
				Log.d(LOG_TAG, "Created Context Entity");
				entity = intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
				Log.d(LOG_TAG, "Entity created: " + entity);
				Log.d(LOG_TAG, "entityId: " + entity.getId());
				assertNotNull(entity);
			}
			else if (intent.getAction().equals(ICtxClient.CREATE_ATTRIBUTE)) {
				Log.d(LOG_TAG, "Created Context Attribute");
				attribute = intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
				Log.d(LOG_TAG, "Attribute created: " + attribute + " with binVal: " + attribute.getBinaryValue().toString() + " intVal: " + attribute.getIntegerValue().toString() + " and doubleVal: " + attribute.getDoubleValue().toString());
				assertNotNull(attribute);
			}
			else if (intent.getAction().equals(ICtxClient.CREATE_ASSOCIATION)) {
				Log.d(LOG_TAG, "Created Context Association");
				association = intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
				Log.d(LOG_TAG, "Association created: " + association);
				assertNotNull(association);
			}
			else if (intent.getAction().equals(ICtxClient.RETRIEVE)) {
				Log.d(LOG_TAG, "Retrieved ");
				retrievedModelObject = intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
				Log.d(LOG_TAG, "Retrieved Model Object: " + retrievedModelObject.getId().getString());
				attribute = (CtxAttributeBean) retrievedModelObject;
				Log.d(LOG_TAG, "binaryValue: " + attribute.getBinaryValue() + " intValue: " + attribute.getIntegerValue() + " and doubleValue: " + attribute.getDoubleValue());
				Log.d(LOG_TAG, "Retrieved ModelObject attribute has ownerId: " + attribute.getId().getOwnerId() + ", type: " + attribute.getId().getType() + ", objectNumber: " + attribute.getId().getObjectNumber() + " and modelType: " + attribute.getId().getModelType());
				assertNotNull(retrievedModelObject);
			}
			else if (intent.getAction().equals(ICtxClient.RETRIEVE_INDIVIDUAL_ENTITY_ID)) {
				Log.d(LOG_TAG, "RetrievedIndividualEntityId ");
				retrievedIndEntityId = intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
				Log.d(LOG_TAG, "Retrieved Individual Entity Id: " + retrievedIndEntityId.getString());
				Log.d(LOG_TAG, "EntityId owner is: " + retrievedIndEntityId.getOwnerId() + ", objectNumber: " + retrievedIndEntityId.getObjectNumber() 
						+ ", type: " + retrievedIndEntityId.getType() + " and modelType: " + retrievedIndEntityId.getModelType());
				assertNotNull(retrievedIndEntityId);
			}
			else if (intent.getAction().equals(ICtxClient.RETRIEVE_COMMUNITY_ENTITY_ID)) {
				Log.d(LOG_TAG, "RetrievedCommunityEntityId ");
				retrievedCommunityEntityId = intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
				Log.d(LOG_TAG, "Retrieved Community Entity Id: " + retrievedCommunityEntityId.getString());
				assertNotNull(retrievedCommunityEntityId);
			}
			else if (intent.getAction().equals(ICtxClient.UPDATE)) {
				Log.d(LOG_TAG, "Update Attribute ");
				updatedAttribute = intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY);
				Log.d(LOG_TAG, "Updated Attribute: " + updatedAttribute.getId().getString());
				assertNotNull(updatedAttribute);
			}
			
			TestSocietiesAndroidContext.this.receivedResult = true;
			assertNotNull(intent.getParcelableExtra(ICtxClient.INTENT_RETURN_VALUE_KEY));
			Log.d(LOG_TAG, "OnReceive finished");
			
			//signal that test has completed
/*			TestSocietiesAndroidContext.this.testCompleted = true;
			TestSocietiesAndroidContext.this.testEndTime = System.currentTimeMillis();		
	        Log.d(LOG_TAG, intent.getAction() + " elapse time: " 
	        		+ (TestSocietiesAndroidContext.this.testEndTime - TestSocietiesAndroidContext.this.testStartTime));
*/
			TestSocietiesAndroidContext.this.testEndTime = System.currentTimeMillis();
			Log.d(LOG_TAG, intent.getAction() + " elapse time: " 
					+ (TestSocietiesAndroidContext.this.testEndTime - TestSocietiesAndroidContext.this.testStartTime));
			TestSocietiesAndroidContext.this.testDoneSignal.countDown();
		}

	}
	
	@MediumTest
	public void testCreateEntity() throws URISyntaxException, Exception{
//		this.testCompleted = false;
		this.testDoneSignal = new CountDownLatch(1);
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;

		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(REQUESTOR_ID);
		
		Log.d(LOG_TAG, "Requestor is: " + requestor.getRequestorId());
		Log.d(LOG_TAG, "test createEntity start time: " + this.testStartTime);
		try {
			this.ctxBrokerService.createEntity(CLIENT_ID, requestor, "jane.societies.local", "androidEntity");
			Log.d(LOG_TAG, "entity created: " + entity);
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to create entity: " + e.getLocalizedMessage());
		}

//		Thread.sleep(DELAY);

//		Log.d(LOG_TAG, "Received result");
//        this.unregisterReceiver(receiver);

		//this.testCompleted = true;
        //assertTrue(this.testCompleted);
		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
	}
	
	@MediumTest
	public void testCreateAttribute() throws URISyntaxException, Exception{
//		this.testCompleted = false;
		this.testDoneSignal = new CountDownLatch(1);
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;

		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(this.REQUESTOR_ID);
		
		Log.d(LOG_TAG, "Requestor is: " + requestor.getRequestorId());
		Log.d(LOG_TAG, "test createAttribute start time: " + this.testStartTime);
		try {
			Log.d(LOG_TAG, "attribute Test1");
			this.ctxBrokerService.createEntity(CLIENT_ID, requestor, "jane.societies.local", "androidEntity2");
			Log.d(LOG_TAG, "attribute Test2");

		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to create attribute: " + e.getLocalizedMessage());
		}

		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
		this.testDoneSignal = new CountDownLatch(1);

		try {
			
//			CtxEntityIdentifierBean entityId = (CtxEntityIdentifierBean) entity.getId();
			Log.d(LOG_TAG, "attribute Test3");
			Log.d(LOG_TAG, "entityId used to Create Attribute: " + entity);
			Log.d(LOG_TAG, "entityId.getString: " + entity.getId().getString());
			CtxEntityIdentifierBean entityId = new CtxEntityIdentifierBean();
			entityId.setString(entity.getId().getString());
			this.ctxBrokerService.createAttribute(CLIENT_ID, requestor, entityId, "androidAttribute");
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to create attribute: " + e.getLocalizedMessage());
		}

//		Thread.sleep(DELAY);

//		Log.d(LOG_TAG, "Received result");
//        this.unregisterReceiver(receiver);
		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
	}
	
	@MediumTest
	public void testCreateAssociation() throws URISyntaxException, Exception{
//		this.testCompleted = false;
		this.testDoneSignal = new CountDownLatch(1);
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;

		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(this.REQUESTOR_ID);
		
		Log.d(LOG_TAG, "Requestor is: " + requestor.getRequestorId());
		Log.d(LOG_TAG, "test createAssociation start time: " + this.testStartTime);
		try {
			this.ctxBrokerService.createAssociation(CLIENT_ID, requestor, "jane.societies.local", "associationAndroid");
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to create association: " + e.getLocalizedMessage());
		}

		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
	}
	
	@MediumTest
	public void testRetrieve() throws URISyntaxException, Exception{
//		this.testCompleted = false;
		this.testDoneSignal = new CountDownLatch(1);
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;

		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(this.REQUESTOR_ID);
		
		Log.d(LOG_TAG, "Requestor is: " + requestor.getRequestorId());
		Log.d(LOG_TAG, "test retrieve start time: " + this.testStartTime);
		try {
			this.ctxBrokerService.createEntity(CLIENT_ID, requestor, "jane.societies.local", "androidEntity3");
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to create entity: " + e.getLocalizedMessage());
		}

		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
		this.testDoneSignal = new CountDownLatch(1);

		try {
			
//			CtxEntityIdentifierBean entityId = (CtxEntityIdentifierBean) entity.getId();
			Log.d(LOG_TAG, "entityId used to Create Attribute: " + entity);
			Log.d(LOG_TAG, "entityId.getString: " + entity.getId().getString());
			CtxEntityIdentifierBean entityId = new CtxEntityIdentifierBean();
			entityId.setString(entity.getId().getString());
			this.ctxBrokerService.createAttribute(CLIENT_ID, requestor, entityId, "androidAttribute3");
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to create attribute: " + e.getLocalizedMessage());
		}
		
		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
		this.testDoneSignal = new CountDownLatch(1);
		try {
			Log.d(LOG_TAG, "entity used to retrieve: " + entity);
			Log.d(LOG_TAG, "entity.getString: " + entity.getId().getString());
//			CtxEntityIdentifierBean entityId = new CtxEntityIdentifierBean();
//			entityId.setString(entity.getId().getString());

//			this.ctxBrokerService.retrieve(CLIENT_ID, requestor, entityId);

			CtxAttributeIdentifierBean attrId = new CtxAttributeIdentifierBean();
			attrId.setString(attribute.getId().getString());
			
			this.ctxBrokerService.retrieve(CLIENT_ID, requestor, attrId);
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to retrieve: " + e.getLocalizedMessage());
		}

		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
	}
	
	@MediumTest
	public void testRetrieveIndividualEntityId() throws URISyntaxException, Exception{
//		this.testCompleted = false;
		this.testDoneSignal = new CountDownLatch(1);
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;

		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(this.REQUESTOR_ID);
		
		Log.d(LOG_TAG, "Requestor is: " + requestor.getRequestorId());
		Log.d(LOG_TAG, "test retrieveIndividualEntityId start time: " + this.testStartTime);
		try {
			this.ctxBrokerService.retrieveIndividualEntityId(CLIENT_ID, requestor, REQUESTOR_ID);
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to retrieveIndividualEntityId: " + e.getLocalizedMessage());
		}

		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
	}
	
/*	@MediumTest
	public void testRetrieveCommunityEntityId() throws URISyntaxException, Exception{
		this.testDoneSignal = new CountDownLatch(1);
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;

		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(this.REQUESTOR_ID);
		
		Log.d(LOG_TAG, "Requestor is: " + requestor.getRequestorId());
		Log.d(LOG_TAG, "test retrieveCommunityEntityId start time: " + this.testStartTime);
		try {
			this.ctxBrokerService.retrieveCommunityEntityId(CLIENT_ID, requestor, REQUESTOR_ID);
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to retrieveCommunityEntityId: " + e.getLocalizedMessage());
		}

		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
	}*/
	
	@MediumTest
	public void testUpdateAttributeIntegerValue() throws URISyntaxException, Exception{
//		this.testCompleted = false;
		this.testDoneSignal = new CountDownLatch(1);
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;

		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(this.REQUESTOR_ID);
		
		Log.d(LOG_TAG, "Requestor is: " + requestor.getRequestorId());
		Log.d(LOG_TAG, "test retrieve start time: " + this.testStartTime);
		try {
			this.ctxBrokerService.createEntity(CLIENT_ID, requestor, "jane.societies.local", "androidEntity4");
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to create entity: " + e.getLocalizedMessage());
		}

		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
		
		this.testDoneSignal = new CountDownLatch(1);
		try {
			
			Log.d(LOG_TAG, "entityId used to Create Attribute: " + entity);
			Log.d(LOG_TAG, "entityId.getString: " + entity.getId().getString());
			CtxEntityIdentifierBean entityId = new CtxEntityIdentifierBean();
			entityId.setString(entity.getId().getString());
			this.ctxBrokerService.createAttribute(CLIENT_ID, requestor, entityId, "androidAttribute4");
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to create attribute: " + e.getLocalizedMessage());
		}
		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
		this.testDoneSignal = new CountDownLatch(1);
		try {
			Log.d(LOG_TAG, "attribute used to update: " + attribute);
			Log.d(LOG_TAG, "attribute.getString: " + attribute.getId().getString());
			attribute.setIntegerValue(321);
			this.ctxBrokerService.update(CLIENT_ID, requestor, attribute);
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to update: " + e.getLocalizedMessage());
		}

		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
	}	

	@MediumTest	
	public void testUpdateAttributeDouble() throws URISyntaxException, Exception{
		this.testDoneSignal = new CountDownLatch(1);
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;

		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(this.REQUESTOR_ID);
		
		Log.d(LOG_TAG, "Requestor is: " + requestor.getRequestorId());
		Log.d(LOG_TAG, "test retrieve start time: " + this.testStartTime);
		try {
			this.ctxBrokerService.createEntity(CLIENT_ID, requestor, "jane.societies.local", "androidEntity5");
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to create entity: " + e.getLocalizedMessage());
		}

		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
		
		this.testDoneSignal = new CountDownLatch(1);
		try {
			
			Log.d(LOG_TAG, "entityId used to Create Attribute: " + entity);
			Log.d(LOG_TAG, "entityId.getString: " + entity.getId().getString());
			CtxEntityIdentifierBean entityId = new CtxEntityIdentifierBean();
			entityId.setString(entity.getId().getString());
			this.ctxBrokerService.createAttribute(CLIENT_ID, requestor, entityId, "androidAttribute5");
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to create attribute: " + e.getLocalizedMessage());
		}
		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
		this.testDoneSignal = new CountDownLatch(1);
		try {
			Log.d(LOG_TAG, "attribute used to update: " + attribute);
			Log.d(LOG_TAG, "attribute.getString: " + attribute.getId().getString());
			attribute.setDoubleValue(12.123);
			this.ctxBrokerService.update(CLIENT_ID, requestor, attribute);
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to update: " + e.getLocalizedMessage());
		}

		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
	}

	@MediumTest	
	public void testUpdateAttributeOnlyBinary() throws URISyntaxException, Exception{
		this.testDoneSignal = new CountDownLatch(1);
		this.testStartTime = System.currentTimeMillis();
		this.testEndTime = this.testStartTime;

		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(this.REQUESTOR_ID);
		
		Log.d(LOG_TAG, "Requestor is: " + requestor.getRequestorId());
		Log.d(LOG_TAG, "test retrieve start time: " + this.testStartTime);
		try {
			this.ctxBrokerService.createEntity(CLIENT_ID, requestor, "jane.societies.local", "androidEntity6");
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to create entity: " + e.getLocalizedMessage());
		}

		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
		this.testDoneSignal = new CountDownLatch(1);
		try {
			
			Log.d(LOG_TAG, "entityId used to Create Attribute: " + entity);
			Log.d(LOG_TAG, "entityId.getString: " + entity.getId().getString());
			CtxEntityIdentifierBean entityId = new CtxEntityIdentifierBean();
			entityId.setString(entity.getId().getString());
			this.ctxBrokerService.createAttribute(CLIENT_ID, requestor, entityId, "androidAttribute6");
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to create attribute: " + e.getLocalizedMessage());
		}
		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
		this.testDoneSignal = new CountDownLatch(1);

		try {
			Log.d(LOG_TAG, "attribute used to update: " + attribute);
			Log.d(LOG_TAG, "attribute.getString: " + attribute.getId().getString());
			byte[] b1 = new byte[] {97, 98, 99};
			attribute.setBinaryValue(b1);
			this.ctxBrokerService.update(CLIENT_ID, requestor, attribute);
		} 		catch (Exception e) {
			Log.e(LOG_TAG, "Failed to retrieve: " + e.getLocalizedMessage());
		}

		assertTrue(this.testDoneSignal.await(DELAY, TimeUnit.MILLISECONDS));
	}
	
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
    	BroadcastReceiver receiver = null;
    	Log.d(LOG_TAG, "Set up broadcast receiver");
        
        receiver = new MainReceiver();
        getContext().registerReceiver(receiver, this.createTestIntentFilter());    	
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    
	
    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createTestIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        Log.d(LOG_TAG, "intentFilter.addAction " + IServiceManager.INTENT_SERVICE_STARTED_STATUS);
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
        Log.d(LOG_TAG, "intentFilter.addAction " + ICtxClient.CREATE_ENTITY);
        intentFilter.addAction(ICtxClient.CREATE_ENTITY);
        Log.d(LOG_TAG, "intentFilter.addAction " + ICtxClient.CREATE_ATTRIBUTE);
        intentFilter.addAction(ICtxClient.CREATE_ATTRIBUTE);
        Log.d(LOG_TAG, "intentFilter.addAction " + ICtxClient.CREATE_ASSOCIATION);
        intentFilter.addAction(ICtxClient.CREATE_ASSOCIATION);
        Log.d(LOG_TAG, "intentFilter.addAction " + ICtxClient.RETRIEVE);
        intentFilter.addAction(ICtxClient.RETRIEVE);
        Log.d(LOG_TAG, "intentFilter.addAction " + ICtxClient.RETRIEVE_INDIVIDUAL_ENTITY_ID);
        intentFilter.addAction(ICtxClient.RETRIEVE_INDIVIDUAL_ENTITY_ID);
        Log.d(LOG_TAG, "intentFilter.addAction " + ICtxClient.RETRIEVE_COMMUNITY_ENTITY_ID);
        intentFilter.addAction(ICtxClient.RETRIEVE_COMMUNITY_ENTITY_ID);
        Log.d(LOG_TAG, "intentFilter.addAction " + ICtxClient.UPDATE);
        intentFilter.addAction(ICtxClient.UPDATE);

//        intentFilter.addAction(ICtxClient.INTENT_RETURN_VALUE_KEY);
        Log.d(LOG_TAG, "created test intentFilter " + intentFilter);
        return intentFilter;
    }
}

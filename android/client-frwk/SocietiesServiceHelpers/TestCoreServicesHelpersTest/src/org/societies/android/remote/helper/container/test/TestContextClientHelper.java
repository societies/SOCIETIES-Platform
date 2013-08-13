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
package org.societies.android.remote.helper.container.test;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.societies.android.api.common.ADate;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.context.CtxException;
import org.societies.android.api.context.ICtxClientCallback;
import org.societies.android.remote.helper.ContextClientHelper;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.android.api.context.model.CtxAttributeTypes;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.android.api.context.model.CtxEntityTypes;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.context.model.CtxModelTypeBean;
import org.societies.api.schema.identity.RequestorBean;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * The test suite tests the Societies Client app Context Client service helper class and its interaction with the remote Android Context Client service.
 * 
 * In order to run the tests contained in this class ensure that the following steps are taken:
 * 
 * 1. An Openfire XMPP server must be running
 * 2. A suitable AVD must be running
 * 3. The Android Client app must have already logged in successfully
 *
 * @author <a href="mailto:pkosmidis@cn.ntua.gr">Pavlos Kosmides</a> (ICCS)
 * @since 1.1
 */
public class TestContextClientHelper extends AndroidTestCase {

	private static final String LOG_TAG = TestContextClientHelper.class.getName();
	
	private static final int LATCH_TIME_OUT = 10000;
	
	private static final String REQUESTOR_ID = "jane.societies.local";
	
	private boolean testCompleted;
	private CountDownLatch latch;
	private ContextClientHelper helper;
	private long startTime;

	private CtxEntityBean entityCreated, entityCreated2, entityCreated3;
	private CtxEntityIdentifierBean retrEntId;
	private CtxAttributeBean attributeCreated, attributeCreated2, attributeCreated3, attributeCreated4;
	private CtxIdentifierBean locationIdentifier;
	
	private String cssId = "jane.societies.local";
	
	protected void setUp() throws Exception {
		super.setUp();
		Log.d(LOG_TAG, "setUp");
		this.startTime = System.currentTimeMillis();
	}
	
	protected void tearDown() throws Exception {
		Log.d(LOG_TAG, "tearDown: Test duration: " + (System.currentTimeMillis() - this.startTime));
		super.tearDown();
	}
	
	@MediumTest
	public void testServiceConfiguration() throws Exception {
		this.testCompleted = false;
		this.latch = new CountDownLatch(1);
		
		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
				
			@Override
			public void returnException(String exception) {
			}

			@Override
			public void returnAction(String result) {
				fail();
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				assertTrue(resultFlag);
				helper.tearDownService(new IMethodCallback() {

					@Override
					public void returnException(String exception) {
					}
					
					@Override
					public void returnAction(String result) {
						fail();
					}
					
					@Override
					public void returnAction(boolean resultFlag) {
						assertTrue(resultFlag);
						TestContextClientHelper.this.testCompleted = true;
						latch.countDown();
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testCreateEntity() throws Exception {
		this.testCompleted = false;
		this.latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(REQUESTOR_ID);

		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.createEntity(requestor, "jane.societies.local", "androidEntityHelper", new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {

							fail("createEntity callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {

							fail("createEntity callback onCreatedAttribute: "
									+ attribute);							
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							Log.d(LOG_TAG, "on CreatedEntity, entityId is: "+ entity.getId().toString());
							assertNotNull(entity);
//							assertEquals(REQUESTOR_ID, entity.getId());
							
							helper.tearDownService(new IMethodCallback() {

								@Override
								public void returnAction(boolean resultFlag) {

									assertTrue(resultFlag);
									TestContextClientHelper.this.testCompleted = true;
									latch.countDown();
								}

								@Override
								public void returnAction(String result) {

									fail("tearDownService returned action: " + result);
								}

								@Override
								public void returnException(String exception) {
									
									fail("tearDownService returned exception: " + exception);
								}
								
							});
							
						}

						@Override
						public void onException(CtxException exception) {

							fail("createEntity callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {

							fail("createEntity callback onLookupCallback: "
									+ lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {

							fail("createEntity callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {

							fail("createEntity callback onRetrievedCtx: "
									+ modelObject);							
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {

							fail("createEntity callback onRetrievedEntityId: "
									+ entityId);							
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {

							fail("createEntity callback onUpdateCtx: "
									+ modelObject);							
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testCreateAttribute() throws Exception {
		this.testCompleted = false;
		this.latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(REQUESTOR_ID);

		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.createEntity(requestor, "jane.societies.local", "androidEntityHelper2", new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {

							fail("createAttribute callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {

							fail("createAttribute callback onCreatedAttribute: "
									+ attribute);							
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							Log.d(LOG_TAG, "on CreatedEntity, entityId is: "+ entity.getId().toString());
							assertNotNull(entity);
//							assertEquals(REQUESTOR_ID, entity.getId());
							
							entityCreated = entity;
							latch.countDown();
							helper.tearDownService(new IMethodCallback() {

								@Override
								public void returnAction(boolean resultFlag) {

									assertTrue(resultFlag);
//									TestContextClientHelper.this.testCompleted = true;
									latch.countDown();
								}

								@Override
								public void returnAction(String result) {

									fail("tearDownService returned action: " + result);
								}

								@Override
								public void returnException(String exception) {
									
									fail("tearDownService returned exception: " + exception);
								}
								
							});
							
						}

						@Override
						public void onException(CtxException exception) {

							fail("createAttribute callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {

							fail("createAttribute callback onLookupCallback: "
									+ lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {

							fail("createAttribute callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {

							fail("createAttribute callback onRetrievedCtx: "
									+ modelObject);							
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {

							fail("createAttribute callback onRetrievedEntityId: "
									+ entityId);							
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {

							fail("createAttribute callback onUpdateCtx: "
									+ modelObject);							
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));
		this.latch = new CountDownLatch(1);
		
		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.createAttribute(requestor, (CtxEntityIdentifierBean) entityCreated.getId(), "androidAttributeHelper", new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {

							fail("createAttribute callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {

							Log.d(LOG_TAG, "on CreatedAttribute, attributeId is: " + attribute.getId().toString());
							assertNotNull(attribute);
							
							helper.tearDownService(new IMethodCallback() {

								@Override
								public void returnAction(boolean resultFlag) {
									assertTrue(resultFlag);
//									TestContextClientHelper.this.testCompleted = true;
									latch.countDown();
								}

								@Override
								public void returnAction(String result) {
									fail("tearDownService returned action: " + result);
								}

								@Override
								public void returnException(String exception) {
									fail("tearDownService returned exception: " + exception);
								} 
								
							});
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							fail("createAttribute callback onCreatedEntity: "
									+ entity);														
						}

						@Override
						public void onException(CtxException exception) {

							fail("createAttribute callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {

							fail("createAttribute callback onLookupCallback: "
									+ lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {

							fail("createAttribute callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {

							fail("createAttribute callback onRetrievedCtx: "
									+ modelObject);							
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {

							fail("createAttribute callback onRetrievedEntityId: "
									+ entityId);							
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {

							fail("createAttribute callback onUpdateCtx: "
									+ modelObject);							
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
//		assertTrue(this.testCompleted);
		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));
	}
	
	@MediumTest
	public void testCreateAssociation() throws Exception {
		this.testCompleted = false;
		this.latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(REQUESTOR_ID);

		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.createAssociation(requestor, "jane.societies.local", "associationAndroidHelper", new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							Log.d(LOG_TAG, "on CreatedAssociation, associationId is: " + association.getId().toString());
							assertNotNull(association);
							
							helper.tearDownService(new IMethodCallback(){

								@Override
								public void returnAction(boolean resultFlag) {
									assertTrue(resultFlag);
									latch.countDown();
								}

								@Override
								public void returnAction(String result) {
									fail("tearDownService returned action: " + result);
								}

								@Override
								public void returnException(String exception) {
									fail("tearDownService returned exception: " + exception);
								} 
								
							});
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {
							fail("createAssociation callback onCreatedAttribute: " + attribute);							
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							fail("createAssociation callback onCreatedEntity: " + entity);
						}

						@Override
						public void onException(CtxException exception) {
							fail("createAssociation callback onException: " + exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							fail("createAssociation callback onLookupCallback: " + lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {
							fail("createAssociation callback onRemovedModelObject: " + modelObject);	
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {
							fail("createAssociation callback onRetrievedCtx: " + modelObject);	
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
							fail("createAssociation callback onRetrievedEntityId: " + entityId);
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							fail("createAssociation callback onUpdateCtx: " + modelObject);
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));
	}
	
	@MediumTest
	public void testRetrieveAttribute() throws Exception {
		this.testCompleted = false;
		this.latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(REQUESTOR_ID);

		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.createEntity(requestor, "jane.societies.local", "androidEntityHelper3", new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							fail("retrieveAttribute callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {
							fail("retrieveAttribute callback onCreatedAttribute: "
									+ attribute);							
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							Log.d(LOG_TAG, "on CreatedEntity, entityId is: "+ entity.getId().toString());
							assertNotNull(entity);
//							assertEquals(REQUESTOR_ID, entity.getId());
							
							entityCreated = entity;

							helper.tearDownService(new IMethodCallback() {

								@Override
								public void returnAction(boolean resultFlag) {

									assertTrue(resultFlag);
//									TestContextClientHelper.this.testCompleted = true;
									latch.countDown();
								}

								@Override
								public void returnAction(String result) {

									fail("tearDownService returned action: " + result);
								}

								@Override
								public void returnException(String exception) {
									
									fail("tearDownService returned exception: " + exception);
								}
								
							});
							
						}

						@Override
						public void onException(CtxException exception) {
							fail("retrieveAttribute callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							fail("retrieveAttribute callback onLookupCallback: "
									+ lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRetrievedCtx: "
									+ modelObject);							
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
							fail("retrieveAttribute callback onRetrievedEntityId: "
									+ entityId);							
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onUpdateCtx: "
									+ modelObject);							
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));
		this.latch = new CountDownLatch(1);
		
		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.createAttribute(requestor, (CtxEntityIdentifierBean) entityCreated.getId(), "androidAttributeHelper2", new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							fail("retrieveAttribute callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {

							Log.d(LOG_TAG, "on CreatedAttribute, attributeId is: " + attribute.getId().toString());
							assertNotNull(attribute);
							attributeCreated = attribute; 
							
							helper.tearDownService(new IMethodCallback() {

								@Override
								public void returnAction(boolean resultFlag) {
									assertTrue(resultFlag);
//									TestContextClientHelper.this.testCompleted = true;
									latch.countDown();
								}

								@Override
								public void returnAction(String result) {
									fail("tearDownService returned action: " + result);
								}

								@Override
								public void returnException(String exception) {
									fail("tearDownService returned exception: " + exception);
								} 
								
							});
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							fail("retrieveAttribute callback onCreatedEntity: "
									+ entity);														
						}

						@Override
						public void onException(CtxException exception) {
							fail("retrieveAttribute callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							fail("retrieveAttribute callback onLookupCallback: "
									+ lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRetrievedCtx: "
									+ modelObject);							
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
							fail("retrieveAttribute callback onRetrievedEntityId: "
									+ entityId);							
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onUpdateCtx: "
									+ modelObject);							
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));
		this.latch = new CountDownLatch(1);
		
		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.retrieve(requestor, attributeCreated.getId(), new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							fail("retrieveAttribute callback onCreatedEntity: " + association);	
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {
							fail("retrieveAttribute callback onCreatedAttribute: " + attribute);
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							fail("retrieveAttribute callback onCreatedEntity: " + entity);
						}

						@Override
						public void onException(CtxException exception) {
							fail("retrieveAttribute callback onException: " + exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							fail("retrieveAttribute callback onLookupCallback: " + lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRemovedModelObject: " + modelObject);	
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {
							Log.d(LOG_TAG, "on RetrieveCtx, modelObject is: " + modelObject.getId().toString());
							assertNotNull(modelObject);
							CtxAttributeBean retrAttr = (CtxAttributeBean) modelObject;
							Log.d(LOG_TAG, "on RetrieveCtx, the attribute is: " + retrAttr + " getId: " + retrAttr.getId().getString() + " to String: " + retrAttr.getId().toString());
							
							helper.tearDownService(new IMethodCallback(){

								@Override
								public void returnAction(boolean resultFlag) {
									assertTrue(resultFlag);
									latch.countDown();
								}

								@Override
								public void returnAction(String result) {
									fail("tearDownService returned action: " + result);
								}

								@Override
								public void returnException(String exception) {
									fail("tearDownService returned exception: " + exception);
								} 
								
							});	
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
							fail("retrieveAttribute callback onRetrievedEntityId: " + entityId);
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onUpdateCtx: " + modelObject);
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));
	}
	
	@MediumTest
	public void testLookupByAttributeType() throws Exception {
		this.testCompleted = false;
		this.latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(REQUESTOR_ID);

		//CreateEntity
		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.createEntity(requestor, "jane.societies.local", "androidEntityHelper4", new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							fail("retrieveAttribute callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {
							fail("retrieveAttribute callback onCreatedAttribute: "
									+ attribute);							
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							Log.d(LOG_TAG, "on CreatedEntity, entityId is: "+ entity.getId().toString());
							assertNotNull(entity);
//							assertEquals(REQUESTOR_ID, entity.getId());
							
							entityCreated2 = entity;

							helper.tearDownService(new IMethodCallback() {

								@Override
								public void returnAction(boolean resultFlag) {

									assertTrue(resultFlag);
//									TestContextClientHelper.this.testCompleted = true;
									latch.countDown();
								}

								@Override
								public void returnAction(String result) {

									fail("tearDownService returned action: " + result);
								}

								@Override
								public void returnException(String exception) {
									
									fail("tearDownService returned exception: " + exception);
								}
								
							});
							
						}

						@Override
						public void onException(CtxException exception) {
							fail("retrieveAttribute callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							fail("retrieveAttribute callback onLookupCallback: "
									+ lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRetrievedCtx: "
									+ modelObject);							
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
							fail("retrieveAttribute callback onRetrievedEntityId: "
									+ entityId);							
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onUpdateCtx: "
									+ modelObject);							
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));

		this.latch = new CountDownLatch(1);
		
		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.createAttribute(requestor, (CtxEntityIdentifierBean) entityCreated2.getId(), "androidAttributeHelper3", new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							fail("retrieveAttribute callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {

							Log.d(LOG_TAG, "on CreatedAttribute, attributeId is: " + attribute.getId().toString());
							assertNotNull(attribute);
							attributeCreated2 = attribute; 
							
							helper.tearDownService(new IMethodCallback() {

								@Override
								public void returnAction(boolean resultFlag) {
									assertTrue(resultFlag);
//									TestContextClientHelper.this.testCompleted = true;
									latch.countDown();
								}

								@Override
								public void returnAction(String result) {
									fail("tearDownService returned action: " + result);
								}

								@Override
								public void returnException(String exception) {
									fail("tearDownService returned exception: " + exception);
								} 
								
							});
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							fail("retrieveAttribute callback onCreatedEntity: "
									+ entity);														
						}

						@Override
						public void onException(CtxException exception) {
							fail("retrieveAttribute callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							fail("retrieveAttribute callback onLookupCallback: "
									+ lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRetrievedCtx: "
									+ modelObject);							
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
							fail("retrieveAttribute callback onRetrievedEntityId: "
									+ entityId);							
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onUpdateCtx: "
									+ modelObject);							
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));
		this.latch = new CountDownLatch(1);
		
		//Lookup entities
		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.lookup(requestor, "jane.societies.local", CtxModelTypeBean.ATTRIBUTE, "androidAttributeHelper3", new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							fail("retrieveAttribute callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {
							fail("retrieveAttribute callback onCreatedAttribute: "
									+ attribute);
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							fail("retrieveAttribute callback onCreatedEntity: "
									+ entity);														
						}

						@Override
						public void onException(CtxException exception) {
							fail("retrieveAttribute callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							assertNotNull(lookupList);
							assertFalse(lookupList.isEmpty());
							boolean containsId = false;
							for (final CtxIdentifierBean lookupId : lookupList) {
								if (attributeCreated2.getId().getString().equals(lookupId.getString())) {
									containsId = true;
									Log.d(LOG_TAG, "lookup contains the id and value is: " + containsId);
								}
							}
							assertTrue(containsId);
							
							helper.tearDownService(new IMethodCallback() {

								@Override
								public void returnAction(boolean resultFlag) {
									assertTrue(resultFlag);
//									TestContextClientHelper.this.testCompleted = true;
									latch.countDown();
								}

								@Override
								public void returnAction(String result) {
									fail("tearDownService returned action: " + result);
								}

								@Override
								public void returnException(String exception) {
									fail("tearDownService returned exception: " + exception);
								} 
								
							});
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRetrievedCtx: "
									+ modelObject);							
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
							fail("retrieveAttribute callback onRetrievedEntityId: "
									+ entityId);							
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onUpdateCtx: "
									+ modelObject);							
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));
	}
	
	@MediumTest
	public void testRetrieveLocation() throws Exception {
		this.testCompleted = false;
		this.latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(REQUESTOR_ID);

		//Step 1 to retrieve location 
		//retrieve the corresponding individual context entity
		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.retrieveIndividualEntityId(requestor, cssId, new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							fail("retrieveAttribute callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {
							fail("retrieveAttribute callback onCreatedAttribute: "
									+ attribute);
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							fail("retrieveAttribute callback onCreatedEntity: "
									+ entity);														
						}

						@Override
						public void onException(CtxException exception) {
							fail("retrieveAttribute callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							fail("retrieveAttribute callback onException: "
									+ lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRetrievedCtx: "
									+ modelObject);							
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
			                   Log.d(LOG_TAG, "on RetrieveEntityId, entityId is: " + entityId.toString());
			                    assertNotNull(entityId);
			                    retrEntId = entityId;

			                    helper.tearDownService(new IMethodCallback(){

			                        @Override
			                        public void returnAction(boolean resultFlag) {
			                            assertTrue(resultFlag);
			                            latch.countDown();
			                        }

			                        @Override
			                        public void returnAction(String result) {
			                            fail("tearDownService returned action: " + result);
			                        }

			                        @Override
			                        public void returnException(String exception) {
			                            fail("tearDownService returned exception: " + exception);
			                        } 

			                    });    
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onUpdateCtx: "
									+ modelObject);							
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));
		this.latch = new CountDownLatch(1);
		
		//Step 2 to retrieve location 
		//lookup entities with retrieved entity id from step1
		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.lookup(requestor, retrEntId, CtxModelTypeBean.ATTRIBUTE, CtxAttributeTypes.LOCATION_COORDINATES, new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							fail("retrieveAttribute callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {
							fail("retrieveAttribute callback onCreatedAttribute: "
									+ attribute);
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							fail("retrieveAttribute callback onCreatedEntity: "
									+ entity);														
						}

						@Override
						public void onException(CtxException exception) {
							fail("retrieveAttribute callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							assertNotNull(lookupList);
							assertFalse(lookupList.isEmpty());
//							boolean containsId = false;
//							for (final CtxIdentifierBean lookupId : lookupList) {
//								if (attributeCreated3.getId().getString().equals(lookupId.getString())) {
//									containsId = true;
//									Log.d(LOG_TAG, "lookup contains the id and value is: " + containsId);
//								}
//							}
//							assertTrue(containsId);
							
							locationIdentifier = lookupList.get(0);
							
							helper.tearDownService(new IMethodCallback() {

								@Override
								public void returnAction(boolean resultFlag) {
									assertTrue(resultFlag);
//									TestContextClientHelper.this.testCompleted = true;
									latch.countDown();
								}

								@Override
								public void returnAction(String result) {
									fail("tearDownService returned action: " + result);
								}

								@Override
								public void returnException(String exception) {
									fail("tearDownService returned exception: " + exception);
								} 
								
							});
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRetrievedCtx: "
									+ modelObject);							
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
							fail("retrieveAttribute callback onRetrievedEntityId: "
									+ entityId);							
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onUpdateCtx: "
									+ modelObject);							
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));
		this.latch = new CountDownLatch(1);

		//Step 3 to retrieve location 
		//retrieve the ctxModelObject that includes the desired location useing the ctxIdentifier from step2
		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.retrieve(requestor, locationIdentifier, new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							fail("retrieveAttribute callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {
							fail("retrieveAttribute callback onCreatedAttribute: "
									+ attribute);
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							fail("retrieveAttribute callback onCreatedEntity: "
									+ entity);														
						}

						@Override
						public void onException(CtxException exception) {
							fail("retrieveAttribute callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							fail("retrieveAttribute callback onException: "
									+ lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {
							Log.d(LOG_TAG, "on RetrieveCtx, modelObject is: " + modelObject.toString());
			                assertNotNull(modelObject);
			                CtxAttributeBean retrAttr = (CtxAttributeBean) modelObject;
			                Log.d(LOG_TAG, "location retrieved: " + retrAttr.getStringValue() + " from attrId: " + retrAttr.getId().toString() + " and : " + retrAttr.getSourceId() + " and : " + retrAttr.getId().getString());

			                helper.tearDownService(new IMethodCallback(){

			                        @Override
			                        public void returnAction(boolean resultFlag) {
			                            assertTrue(resultFlag);
			                            latch.countDown();
			                        }

			                        @Override
			                        public void returnAction(String result) {
			                            fail("tearDownService returned action: " + result);
			                        }

			                        @Override
			                        public void returnException(String exception) {
			                            fail("tearDownService returned exception: " + exception);
			                        } 

			                });   
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
							fail("retrieveAttribute callback onRetrievedEntityId: "
									+ entityId);							
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onUpdateCtx: "
									+ modelObject);							
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));
	}
	
	@MediumTest
	public void testUpdateAttribute() throws Exception {
		this.testCompleted = false;
		this.latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(REQUESTOR_ID);

		//CreateEntity
		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.createEntity(requestor, cssId, CtxEntityTypes.DEVICE, new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							fail("retrieveAttribute callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {
							fail("retrieveAttribute callback onCreatedAttribute: "
									+ attribute);							
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							Log.d(LOG_TAG, "on CreatedEntity, entityId is: "+ entity.getId().toString());
							assertNotNull(entity);
//							assertEquals(REQUESTOR_ID, entity.getId());
							
							entityCreated3 = entity;

							helper.tearDownService(new IMethodCallback() {

								@Override
								public void returnAction(boolean resultFlag) {

									assertTrue(resultFlag);
//									TestContextClientHelper.this.testCompleted = true;
									latch.countDown();
								}

								@Override
								public void returnAction(String result) {

									fail("tearDownService returned action: " + result);
								}

								@Override
								public void returnException(String exception) {
									
									fail("tearDownService returned exception: " + exception);
								}
								
							});
							
						}

						@Override
						public void onException(CtxException exception) {
							fail("retrieveAttribute callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							fail("retrieveAttribute callback onLookupCallback: "
									+ lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRetrievedCtx: "
									+ modelObject);							
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
							fail("retrieveAttribute callback onRetrievedEntityId: "
									+ entityId);							
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onUpdateCtx: "
									+ modelObject);							
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));

		this.latch = new CountDownLatch(1);
		
		//Create Attribute
		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					helper.createAttribute(requestor, (CtxEntityIdentifierBean) entityCreated3.getId(), CtxAttributeTypes.LOCATION_COORDINATES, new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							fail("retrieveAttribute callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {

							Log.d(LOG_TAG, "on CreatedAttribute, attributeId is: " + attribute.getId().toString());
							assertNotNull(attribute);
							attributeCreated3 = attribute; 
							
							helper.tearDownService(new IMethodCallback() {

								@Override
								public void returnAction(boolean resultFlag) {
									assertTrue(resultFlag);
//									TestContextClientHelper.this.testCompleted = true;
									latch.countDown();
								}

								@Override
								public void returnAction(String result) {
									fail("tearDownService returned action: " + result);
								}

								@Override
								public void returnException(String exception) {
									fail("tearDownService returned exception: " + exception);
								} 
								
							});
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							fail("retrieveAttribute callback onCreatedEntity: "
									+ entity);														
						}

						@Override
						public void onException(CtxException exception) {
							fail("retrieveAttribute callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							fail("retrieveAttribute callback onLookupCallback: "
									+ lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRetrievedCtx: "
									+ modelObject);							
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
							fail("retrieveAttribute callback onRetrievedEntityId: "
									+ entityId);							
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onUpdateCtx: "
									+ modelObject);							
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));
		this.latch = new CountDownLatch(1);
		
		//Update Attribute with Location Coordinates
		this.helper = new ContextClientHelper(getContext());
		helper.setUpService(new IMethodCallback() {
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
			 */
			@Override
			public void returnException(String exception) {
				
				fail("setUpService returned exception: " + exception);
			}

			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
			 */
			@Override
			public void returnAction(String result) {
				
				fail("setUpService returned action: " + result);
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				try {
					attributeCreated3.setStringValue("23.00.41245");
					helper.update(requestor, attributeCreated3, new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {
							fail("retrieveAttribute callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {
							fail("retrieveAttribute callback onCreatedAttribute: " + attribute);
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
							fail("retrieveAttribute callback onCreatedEntity: "
									+ entity);														
						}

						@Override
						public void onException(CtxException exception) {
							fail("retrieveAttribute callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {
							fail("retrieveAttribute callback onLookupCallback: "
									+ lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {
							fail("retrieveAttribute callback onRetrievedCtx: "
									+ modelObject);							
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {
							fail("retrieveAttribute callback onRetrievedEntityId: "
									+ entityId);							
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {
							Log.d(LOG_TAG, "on UpdateCtx, modelObject is: " + modelObject.toString());
							assertNotNull(modelObject);
							attributeCreated4 = (CtxAttributeBean) modelObject; 
							
							helper.tearDownService(new IMethodCallback() {

								@Override
								public void returnAction(boolean resultFlag) {
									assertTrue(resultFlag);
//									TestContextClientHelper.this.testCompleted = true;
									latch.countDown();
								}

								@Override
								public void returnAction(String result) {
									fail("tearDownService returned action: " + result);
								}

								@Override
								public void returnException(String exception) {
									fail("tearDownService returned exception: " + exception);
								} 
								
							});	
						}
					});
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		assertTrue(this.latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS));
	}
}

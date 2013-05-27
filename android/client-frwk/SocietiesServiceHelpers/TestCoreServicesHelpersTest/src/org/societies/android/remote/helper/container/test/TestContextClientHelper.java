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
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
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
	private long startTime;
	
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
		final CountDownLatch latch = new CountDownLatch(1);
		
		final ContextClientHelper helper = new ContextClientHelper(getContext());
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
		final CountDownLatch latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(REQUESTOR_ID);

		final ContextClientHelper helper = new ContextClientHelper(getContext());
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
//				helper.addDirectTrustEvidence(requestor, subjectId, objectId, 
//						TrustEvidenceTypeBean.RATED, new ADate(new Date()),
//						new Double(TEST_TRUST_RATING), new ITrustClientCallback() {
				try {
					helper.createEntity(requestor, "jane.societies.local", "androidEntityHelper", new ICtxClientCallback() {

						@Override
						public CtxException getException() {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public void onCreatedAssociation(CtxAssociationBean association) {

							fail("onCreatedEntity callback onCreatedAssociation: "
									+ association);
						}

						@Override
						public void onCreatedAttribute(CtxAttributeBean attribute) {

							fail("onCreatedEntity callback onCreatedAttribute: "
									+ attribute);							
						}

						@Override
						public void onCreatedEntity(CtxEntityBean entity) {
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

							fail("onCreatedEntity callback onException: "
									+ exception);
						}

						@Override
						public void onLookupCallback(List<CtxIdentifierBean> lookupList) {

							fail("onCreatedEntity callback onLookupCallback: "
									+ lookupList);
						}

						@Override
						public void onRemovedModelObject(CtxModelObjectBean modelObject) {

							fail("onCreatedEntity callback onRemovedModelObject: "
									+ modelObject);							
						}

						@Override
						public void onRetrieveCtx(CtxModelObjectBean modelObject) {

							fail("onCreatedEntity callback onRetrievedCtx: "
									+ modelObject);							
						}

						@Override
						public void onRetrievedEntityId(CtxEntityIdentifierBean entityId) {

							fail("onCreatedEntity callback onRetrievedEntityId: "
									+ entityId);							
						}

						@Override
						public void onUpdateCtx(CtxModelObjectBean modelObject) {

							fail("onCreatedEntity callback onUpdateCtx: "
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
}

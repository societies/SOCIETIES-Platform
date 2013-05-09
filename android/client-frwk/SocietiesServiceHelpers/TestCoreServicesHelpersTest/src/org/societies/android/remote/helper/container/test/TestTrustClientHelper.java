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
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.societies.android.api.common.ADate;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.privacytrust.trust.ITrustClientCallback;
import org.societies.android.api.privacytrust.trust.TrustException;
import org.societies.android.remote.helper.TrustClientHelper;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;
import org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * This test suite tests the Societies Client app Trust Client service helper
 * class and its interaction with the (remote) Android Trust Client service.
 * <p>
 * In order to run the tests contained in this class ensure that the following
 * steps are taken:
 * <ol>
 * <li> An Openfire XMPP server must be running</li>
 * <li> A suitable AVD must be running</li>
 * <li> The Android Client app must have already logged in successfully</li>
 * </ol>
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.1
 */
public class TestTrustClientHelper extends AndroidTestCase {
	
	private static final String TAG = TestTrustClientHelper.class.getName();
	
	// TEST VALUES
	private static final String TEST_TRUSTOR_ID = "university.ict-societies.eu"; // MUST MATCH THE CLOUD NODE!
	private static final String TEST_TRUSTEE_ID = "bob.societies.local"; // ANY STRING WILL DO 
	private static final String TEST_TRUSTEE_ID2 = "arthur.societies.local"; // ANY STRING WILL DO
	private static final double TEST_TRUST_RATING = 1.0d;
	private static final double TEST_TRUST_VALUE_THRESHOLD = 0.4d;
	
	private static final int LATCH_TIME_OUT = 10000;
	
	private boolean testCompleted;
	private long startTime;
	
	protected void setUp() throws Exception {
		
		super.setUp();
		Log.d(TAG, "setUp");
		this.startTime = System.currentTimeMillis();
	}

	protected void tearDown() throws Exception {
		
		Log.d(TAG, "tearDown: Test duration: " + (System.currentTimeMillis() - this.startTime));
		super.tearDown();
	}
	
	@MediumTest
	public void testServiceConfiguration() throws Exception {
		
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		
		final TrustClientHelper helper = new TrustClientHelper(super.getContext());
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
			
			/*
			 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
			 */
			@Override
			public void returnAction(boolean resultFlag) {
				
				assertTrue(resultFlag);
				helper.tearDownService(new IMethodCallback() {

					/*
					 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
					 */
					@Override
					public void returnException(String exception) {
						
						fail("tearDownService returned exception: " + exception);
					}
					
					/*
					 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
					 */
					@Override
					public void returnAction(String result) {
						
						fail("tearDownService returned action: " + result);
					}
					
					/*
					 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
					 */
					@Override
					public void returnAction(boolean resultFlag) {
						
						assertTrue(resultFlag);
						TestTrustClientHelper.this.testCompleted = true;
						latch.countDown();
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testAddDirectTrustEvidence() throws Exception {
		
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(TEST_TRUSTOR_ID);

		final TrustedEntityIdBean subjectId =
				new TrustedEntityIdBean();
		subjectId.setEntityId(TEST_TRUSTOR_ID);
		subjectId.setEntityType(TrustedEntityTypeBean.CSS);

		final TrustedEntityIdBean objectId =
				new TrustedEntityIdBean();
		objectId.setEntityId(TEST_TRUSTEE_ID);
		objectId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustClientHelper helper = new TrustClientHelper(getContext());
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
				helper.addDirectTrustEvidence(requestor, subjectId, objectId, 
						TrustEvidenceTypeBean.RATED, new ADate(new Date()),
						new Double(TEST_TRUST_RATING), new ITrustClientCallback() {

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onAddedDirectTrustEvidence()
					 */
					@Override
					public void onAddedDirectTrustEvidence() {

						// success!
						helper.tearDownService(new IMethodCallback() {

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
							 */
							@Override
							public void returnException(String exception) {

								fail("tearDownService returned exception: " + exception);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
							 */
							@Override
							public void returnAction(String result) {

								fail("tearDownService returned action: " + result);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
							 */
							@Override
							public void returnAction(boolean resultFlag) {

								assertTrue(resultFlag);
								TestTrustClientHelper.this.testCompleted = true;
								latch.countDown();
							}
						});
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onException(org.societies.android.api.privacytrust.trust.TrustException)
					 */
					@Override
					public void onException(TrustException exception) {

						// should not be called!
						fail("addDirectTrustEvidence callback onException: "
								+ exception);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationship(org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean)
					 */
					@Override
					public void onRetrievedTrustRelationship(
							TrustRelationshipBean trustRelationship) {

						// should not be called!
						fail("addDirectTrustEvidence callback onRetrievedTrustRelationship: "
								+ trustRelationship);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationships(java.util.Set)
					 */
					@Override
					public void onRetrievedTrustRelationships(
							Set<TrustRelationshipBean> trustRelationships) {

						// should not be called!
						fail("addDirectTrustEvidence callback onRetrievedTrustRelationships: "
								+ trustRelationships);
					}

					@Override
					public void onRetrievedTrustValue(Double trustValue) {

						// should not be called!
						fail("addDirectTrustEvidence callback onRetrievedTrustValue: "
								+ trustValue);
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testRetrieveTrustRelationshipsByTrustor() throws Exception {
		
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(TEST_TRUSTOR_ID);

		final TrustedEntityIdBean trustorId =
				new TrustedEntityIdBean();
		trustorId.setEntityId(TEST_TRUSTOR_ID);
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustClientHelper helper = new TrustClientHelper(getContext());
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
				helper.retrieveTrustRelationships(requestor, trustorId, 
						new ITrustClientCallback() {

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onAddedDirectTrustEvidence()
					 */
					@Override
					public void onAddedDirectTrustEvidence() {
						
						// should not be called!
						fail("retrieveTrustRelationships callback onAddedDirectTrustEvidence");
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onException(org.societies.android.api.privacytrust.trust.TrustException)
					 */
					@Override
					public void onException(TrustException exception) {

						// should not be called!
						fail("retrieveTrustRelationships callback onException: "
								+ exception);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationship(org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean)
					 */
					@Override
					public void onRetrievedTrustRelationship(
							TrustRelationshipBean trustRelationship) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustRelationship: "
								+ trustRelationship);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationships(java.util.Set)
					 */
					@Override
					public void onRetrievedTrustRelationships(
							Set<TrustRelationshipBean> trustRelationships) {

						// success!
						assertNotNull(trustRelationships);
						assertFalse(trustRelationships.isEmpty());
						for (final TrustRelationshipBean trustRelationship : trustRelationships) {
							assertEquals(TEST_TRUSTOR_ID, trustRelationship.getTrustorId().getEntityId());
							assertEquals(TrustedEntityTypeBean.CSS, trustRelationship.getTrustorId().getEntityType());
							if (TEST_TRUSTEE_ID.equals(trustRelationship.getTrusteeId().getEntityId())
									&& TrustedEntityTypeBean.CSS.equals(trustRelationship.getTrusteeId().getEntityType()))
									assertTrue(trustRelationship.getTrustValue() > TEST_TRUST_VALUE_THRESHOLD);
						}
						helper.tearDownService(new IMethodCallback() {

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
							 */
							@Override
							public void returnException(String exception) {

								fail("tearDownService returned exception: " + exception);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
							 */
							@Override
							public void returnAction(String result) {

								fail("tearDownService returned action: " + result);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
							 */
							@Override
							public void returnAction(boolean resultFlag) {

								assertTrue(resultFlag);
								TestTrustClientHelper.this.testCompleted = true;
								latch.countDown();
							}
						});
					}

					@Override
					public void onRetrievedTrustValue(Double trustValue) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustValue: "
								+ trustValue);
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testRetrieveTrustRelationshipsByTrustorAndTrustee() throws Exception {
		
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(TEST_TRUSTOR_ID);

		final TrustedEntityIdBean trustorId =
				new TrustedEntityIdBean();
		trustorId.setEntityId(TEST_TRUSTOR_ID);
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustedEntityIdBean trusteeId =
				new TrustedEntityIdBean();
		trusteeId.setEntityId(TEST_TRUSTEE_ID);
		trusteeId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustClientHelper helper = new TrustClientHelper(getContext());
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
				helper.retrieveTrustRelationships(requestor, trustorId, trusteeId, 
						new ITrustClientCallback() {

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onAddedDirectTrustEvidence()
					 */
					@Override
					public void onAddedDirectTrustEvidence() {
						
						// should not be called!
						fail("retrieveTrustRelationships callback onAddedDirectTrustEvidence");
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onException(org.societies.android.api.privacytrust.trust.TrustException)
					 */
					@Override
					public void onException(TrustException exception) {

						// should not be called!
						fail("retrieveTrustRelationships callback onException: "
								+ exception);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationship(org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean)
					 */
					@Override
					public void onRetrievedTrustRelationship(
							TrustRelationshipBean trustRelationship) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustRelationship: "
								+ trustRelationship);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationships(java.util.Set)
					 */
					@Override
					public void onRetrievedTrustRelationships(
							Set<TrustRelationshipBean> trustRelationships) {

						// success!
						assertNotNull(trustRelationships);
						assertFalse(trustRelationships.isEmpty());
						for (final TrustRelationshipBean trustRelationship : trustRelationships) {
							assertEquals(TEST_TRUSTOR_ID, trustRelationship.getTrustorId().getEntityId());
							assertEquals(TrustedEntityTypeBean.CSS, trustRelationship.getTrustorId().getEntityType());
							assertEquals(TEST_TRUSTEE_ID, trustRelationship.getTrusteeId().getEntityId());
							assertEquals(TrustedEntityTypeBean.CSS, trustRelationship.getTrusteeId().getEntityType());
							assertTrue(trustRelationship.getTrustValue() > TEST_TRUST_VALUE_THRESHOLD);
						}
						helper.tearDownService(new IMethodCallback() {

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
							 */
							@Override
							public void returnException(String exception) {

								fail("tearDownService returned exception: " + exception);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
							 */
							@Override
							public void returnAction(String result) {

								fail("tearDownService returned action: " + result);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
							 */
							@Override
							public void returnAction(boolean resultFlag) {

								assertTrue(resultFlag);
								TestTrustClientHelper.this.testCompleted = true;
								latch.countDown();
							}
						});
					}

					@Override
					public void onRetrievedTrustValue(Double trustValue) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustValue: "
								+ trustValue);
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testRetrieveEmptyTrustRelationshipsByTrustorAndTrustee() throws Exception {
		
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(TEST_TRUSTOR_ID);

		final TrustedEntityIdBean trustorId =
				new TrustedEntityIdBean();
		trustorId.setEntityId(TEST_TRUSTOR_ID);
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustedEntityIdBean trusteeId =
				new TrustedEntityIdBean();
		trusteeId.setEntityId(TEST_TRUSTEE_ID2);
		trusteeId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustClientHelper helper = new TrustClientHelper(getContext());
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
				helper.retrieveTrustRelationships(requestor, trustorId, trusteeId, 
						new ITrustClientCallback() {

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onAddedDirectTrustEvidence()
					 */
					@Override
					public void onAddedDirectTrustEvidence() {
						
						// should not be called!
						fail("retrieveTrustRelationships callback onAddedDirectTrustEvidence");
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onException(org.societies.android.api.privacytrust.trust.TrustException)
					 */
					@Override
					public void onException(TrustException exception) {

						// should not be called!
						fail("retrieveTrustRelationships callback onException: "
								+ exception);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationship(org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean)
					 */
					@Override
					public void onRetrievedTrustRelationship(
							TrustRelationshipBean trustRelationship) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustRelationship: "
								+ trustRelationship);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationships(java.util.Set)
					 */
					@Override
					public void onRetrievedTrustRelationships(
							Set<TrustRelationshipBean> trustRelationships) {

						// success!
						assertNotNull(trustRelationships);
						assertTrue(trustRelationships.isEmpty());
						helper.tearDownService(new IMethodCallback() {

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
							 */
							@Override
							public void returnException(String exception) {

								fail("tearDownService returned exception: " + exception);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
							 */
							@Override
							public void returnAction(String result) {

								fail("tearDownService returned action: " + result);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
							 */
							@Override
							public void returnAction(boolean resultFlag) {

								assertTrue(resultFlag);
								TestTrustClientHelper.this.testCompleted = true;
								latch.countDown();
							}
						});
					}

					@Override
					public void onRetrievedTrustValue(Double trustValue) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustValue: "
								+ trustValue);
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testRetrieveTrustRelationship() throws Exception {
		
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(TEST_TRUSTOR_ID);

		final TrustedEntityIdBean trustorId =
				new TrustedEntityIdBean();
		trustorId.setEntityId(TEST_TRUSTOR_ID);
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustedEntityIdBean trusteeId =
				new TrustedEntityIdBean();
		trusteeId.setEntityId(TEST_TRUSTEE_ID);
		trusteeId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustValueTypeBean trustValueType =
				TrustValueTypeBean.USER_PERCEIVED;
		
		final TrustClientHelper helper = new TrustClientHelper(getContext());
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
				helper.retrieveTrustRelationship(requestor, trustorId, trusteeId, 
						trustValueType, new ITrustClientCallback() {

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onAddedDirectTrustEvidence()
					 */
					@Override
					public void onAddedDirectTrustEvidence() {
						
						// should not be called!
						fail("retrieveTrustRelationship callback onAddedDirectTrustEvidence");
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onException(org.societies.android.api.privacytrust.trust.TrustException)
					 */
					@Override
					public void onException(TrustException exception) {

						// should not be called!
						fail("retrieveTrustRelationship callback onException: "
								+ exception);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationship(org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean)
					 */
					@Override
					public void onRetrievedTrustRelationship(
							TrustRelationshipBean trustRelationship) {

						// success!
						assertNotNull(trustRelationship);
						assertEquals(TEST_TRUSTOR_ID, trustRelationship.getTrustorId().getEntityId());
						assertEquals(TrustedEntityTypeBean.CSS, trustRelationship.getTrustorId().getEntityType());
						assertEquals(TEST_TRUSTEE_ID, trustRelationship.getTrusteeId().getEntityId());
						assertEquals(TrustedEntityTypeBean.CSS, trustRelationship.getTrusteeId().getEntityType());
						assertEquals(trustValueType, trustRelationship.getTrustValueType());
						assertTrue(trustRelationship.getTrustValue() > TEST_TRUST_VALUE_THRESHOLD);
						helper.tearDownService(new IMethodCallback() {

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
							 */
							@Override
							public void returnException(String exception) {

								fail("tearDownService returned exception: " + exception);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
							 */
							@Override
							public void returnAction(String result) {

								fail("tearDownService returned action: " + result);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
							 */
							@Override
							public void returnAction(boolean resultFlag) {

								assertTrue(resultFlag);
								TestTrustClientHelper.this.testCompleted = true;
								latch.countDown();
							}
						});
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationships(java.util.Set)
					 */
					@Override
					public void onRetrievedTrustRelationships(
							Set<TrustRelationshipBean> trustRelationships) {
						
						// should not be called!
						fail("retrieveTrustRelationship callback onRetrievedTrustRelationships: "
								+ trustRelationships);
					}

					@Override
					public void onRetrievedTrustValue(Double trustValue) {

						// should not be called!
						fail("retrieveTrustRelationship callback onRetrievedTrustValue: "
								+ trustValue);
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testRetrieveNullTrustRelationship() throws Exception {
		
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(TEST_TRUSTOR_ID);

		final TrustedEntityIdBean trustorId =
				new TrustedEntityIdBean();
		trustorId.setEntityId(TEST_TRUSTOR_ID);
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustedEntityIdBean trusteeId =
				new TrustedEntityIdBean();
		trusteeId.setEntityId(TEST_TRUSTEE_ID2);
		trusteeId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustValueTypeBean trustValueType =
				TrustValueTypeBean.USER_PERCEIVED;
		
		final TrustClientHelper helper = new TrustClientHelper(getContext());
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
				helper.retrieveTrustRelationship(requestor, trustorId, trusteeId, 
						trustValueType, new ITrustClientCallback() {

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onAddedDirectTrustEvidence()
					 */
					@Override
					public void onAddedDirectTrustEvidence() {
						
						// should not be called!
						fail("retrieveTrustRelationship callback onAddedDirectTrustEvidence");
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onException(org.societies.android.api.privacytrust.trust.TrustException)
					 */
					@Override
					public void onException(TrustException exception) {

						// should not be called!
						fail("retrieveTrustRelationship callback onException: "
								+ exception);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationship(org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean)
					 */
					@Override
					public void onRetrievedTrustRelationship(
							TrustRelationshipBean trustRelationship) {

						// success!
						assertNull(trustRelationship);
						helper.tearDownService(new IMethodCallback() {

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
							 */
							@Override
							public void returnException(String exception) {

								fail("tearDownService returned exception: " + exception);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
							 */
							@Override
							public void returnAction(String result) {

								fail("tearDownService returned action: " + result);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
							 */
							@Override
							public void returnAction(boolean resultFlag) {

								assertTrue(resultFlag);
								TestTrustClientHelper.this.testCompleted = true;
								latch.countDown();
							}
						});
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationships(java.util.Set)
					 */
					@Override
					public void onRetrievedTrustRelationships(
							Set<TrustRelationshipBean> trustRelationships) {
						
						// should not be called!
						fail("retrieveTrustRelationship callback onRetrievedTrustRelationships: "
								+ trustRelationships);
					}

					@Override
					public void onRetrievedTrustValue(Double trustValue) {

						// should not be called!
						fail("retrieveTrustRelationship callback onRetrievedTrustValue: "
								+ trustValue);
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testRetrieveTrustValue() throws Exception {
		
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(TEST_TRUSTOR_ID);

		final TrustedEntityIdBean trustorId =
				new TrustedEntityIdBean();
		trustorId.setEntityId(TEST_TRUSTOR_ID);
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustedEntityIdBean trusteeId =
				new TrustedEntityIdBean();
		trusteeId.setEntityId(TEST_TRUSTEE_ID);
		trusteeId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustValueTypeBean trustValueType =
				TrustValueTypeBean.USER_PERCEIVED;
		
		final TrustClientHelper helper = new TrustClientHelper(getContext());
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
				helper.retrieveTrustValue(requestor, trustorId, trusteeId, 
						trustValueType, new ITrustClientCallback() {

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onAddedDirectTrustEvidence()
					 */
					@Override
					public void onAddedDirectTrustEvidence() {
						
						// should not be called!
						fail("retrieveTrustValue callback onAddedDirectTrustEvidence");
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onException(org.societies.android.api.privacytrust.trust.TrustException)
					 */
					@Override
					public void onException(TrustException exception) {

						// should not be called!
						fail("retrieveTrustValue callback onException: "
								+ exception);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationship(org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean)
					 */
					@Override
					public void onRetrievedTrustRelationship(
							TrustRelationshipBean trustRelationship) {

						// should not be called!
						fail("retrieveTrustValue callback onRetrievedTrustRelationship: "
								+ trustRelationship);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationships(java.util.Set)
					 */
					@Override
					public void onRetrievedTrustRelationships(
							Set<TrustRelationshipBean> trustRelationships) {
						
						// should not be called!
						fail("retrieveTrustValue callback onRetrievedTrustRelationships: "
								+ trustRelationships);
					}

					@Override
					public void onRetrievedTrustValue(Double trustValue) {

						// success!
						assertNotNull(trustValue);
						assertTrue(trustValue > TEST_TRUST_VALUE_THRESHOLD);
						helper.tearDownService(new IMethodCallback() {

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
							 */
							@Override
							public void returnException(String exception) {

								fail("tearDownService returned exception: " + exception);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
							 */
							@Override
							public void returnAction(String result) {

								fail("tearDownService returned action: " + result);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
							 */
							@Override
							public void returnAction(boolean resultFlag) {

								assertTrue(resultFlag);
								TestTrustClientHelper.this.testCompleted = true;
								latch.countDown();
							}
						});
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testRetrieveNullTrustValue() throws Exception {
		
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(TEST_TRUSTOR_ID);

		final TrustedEntityIdBean trustorId =
				new TrustedEntityIdBean();
		trustorId.setEntityId(TEST_TRUSTOR_ID);
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustedEntityIdBean trusteeId =
				new TrustedEntityIdBean();
		trusteeId.setEntityId(TEST_TRUSTEE_ID2);
		trusteeId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustValueTypeBean trustValueType =
				TrustValueTypeBean.USER_PERCEIVED;
		
		final TrustClientHelper helper = new TrustClientHelper(getContext());
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
				helper.retrieveTrustValue(requestor, trustorId, trusteeId, 
						trustValueType, new ITrustClientCallback() {

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onAddedDirectTrustEvidence()
					 */
					@Override
					public void onAddedDirectTrustEvidence() {
						
						// should not be called!
						fail("retrieveTrustValue callback onAddedDirectTrustEvidence");
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onException(org.societies.android.api.privacytrust.trust.TrustException)
					 */
					@Override
					public void onException(TrustException exception) {

						// should not be called!
						fail("retrieveTrustValue callback onException: "
								+ exception);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationship(org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean)
					 */
					@Override
					public void onRetrievedTrustRelationship(
							TrustRelationshipBean trustRelationship) {
						
						// should not be called!
						fail("retrieveTrustValue callback onRetrievedTrustRelationship: "
								+ trustRelationship);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationships(java.util.Set)
					 */
					@Override
					public void onRetrievedTrustRelationships(
							Set<TrustRelationshipBean> trustRelationships) {
						
						// should not be called!
						fail("retrieveTrustValue callback onRetrievedTrustRelationships: "
								+ trustRelationships);
					}

					@Override
					public void onRetrievedTrustValue(Double trustValue) {

						// success!
						assertNull(trustValue);
						helper.tearDownService(new IMethodCallback() {

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
							 */
							@Override
							public void returnException(String exception) {

								fail("tearDownService returned exception: " + exception);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
							 */
							@Override
							public void returnAction(String result) {

								fail("tearDownService returned action: " + result);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
							 */
							@Override
							public void returnAction(boolean resultFlag) {

								assertTrue(resultFlag);
								TestTrustClientHelper.this.testCompleted = true;
								latch.countDown();
							}
						});
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testRetrieveTrustRelationshipsByTrustorAndTrusteeType() throws Exception {
		
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(TEST_TRUSTOR_ID);

		final TrustedEntityIdBean trustorId =
				new TrustedEntityIdBean();
		trustorId.setEntityId(TEST_TRUSTOR_ID);
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustedEntityTypeBean trusteeType = TrustedEntityTypeBean.CSS;
		
		final TrustClientHelper helper = new TrustClientHelper(getContext());
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
				helper.retrieveTrustRelationships(requestor, trustorId, trusteeType, 
						new ITrustClientCallback() {

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onAddedDirectTrustEvidence()
					 */
					@Override
					public void onAddedDirectTrustEvidence() {
						
						// should not be called!
						fail("retrieveTrustRelationships callback onAddedDirectTrustEvidence");
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onException(org.societies.android.api.privacytrust.trust.TrustException)
					 */
					@Override
					public void onException(TrustException exception) {

						// should not be called!
						fail("retrieveTrustRelationships callback onException: "
								+ exception);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationship(org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean)
					 */
					@Override
					public void onRetrievedTrustRelationship(
							TrustRelationshipBean trustRelationship) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustRelationship: "
								+ trustRelationship);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationships(java.util.Set)
					 */
					@Override
					public void onRetrievedTrustRelationships(
							Set<TrustRelationshipBean> trustRelationships) {

						// success!
						assertNotNull(trustRelationships);
						assertFalse(trustRelationships.isEmpty());
						for (final TrustRelationshipBean trustRelationship : trustRelationships) {
							assertEquals(TEST_TRUSTOR_ID, trustRelationship.getTrustorId().getEntityId());
							assertEquals(TrustedEntityTypeBean.CSS, trustRelationship.getTrustorId().getEntityType());
							assertEquals(TrustedEntityTypeBean.CSS, trustRelationship.getTrusteeId().getEntityType());
							if (TEST_TRUSTEE_ID.equals(trustRelationship.getTrusteeId().getEntityId()))
								assertTrue(trustRelationship.getTrustValue() > TEST_TRUST_VALUE_THRESHOLD);
						}
						helper.tearDownService(new IMethodCallback() {

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
							 */
							@Override
							public void returnException(String exception) {

								fail("tearDownService returned exception: " + exception);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
							 */
							@Override
							public void returnAction(String result) {

								fail("tearDownService returned action: " + result);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
							 */
							@Override
							public void returnAction(boolean resultFlag) {

								assertTrue(resultFlag);
								TestTrustClientHelper.this.testCompleted = true;
								latch.countDown();
							}
						});
					}

					@Override
					public void onRetrievedTrustValue(Double trustValue) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustValue: "
								+ trustValue);
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testRetrieveTrustRelationshipsByTrustorAndTrustValueType() throws Exception {
		
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(TEST_TRUSTOR_ID);

		final TrustedEntityIdBean trustorId =
				new TrustedEntityIdBean();
		trustorId.setEntityId(TEST_TRUSTOR_ID);
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustValueTypeBean trustValueType =
				TrustValueTypeBean.DIRECT;
		
		final TrustClientHelper helper = new TrustClientHelper(getContext());
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
				helper.retrieveTrustRelationships(requestor, trustorId, trustValueType, 
						new ITrustClientCallback() {

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onAddedDirectTrustEvidence()
					 */
					@Override
					public void onAddedDirectTrustEvidence() {
						
						// should not be called!
						fail("retrieveTrustRelationships callback onAddedDirectTrustEvidence");
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onException(org.societies.android.api.privacytrust.trust.TrustException)
					 */
					@Override
					public void onException(TrustException exception) {

						// should not be called!
						fail("retrieveTrustRelationships callback onException: "
								+ exception);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationship(org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean)
					 */
					@Override
					public void onRetrievedTrustRelationship(
							TrustRelationshipBean trustRelationship) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustRelationship: "
								+ trustRelationship);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationships(java.util.Set)
					 */
					@Override
					public void onRetrievedTrustRelationships(
							Set<TrustRelationshipBean> trustRelationships) {

						// success!
						assertNotNull(trustRelationships);
						assertFalse(trustRelationships.isEmpty());
						for (final TrustRelationshipBean trustRelationship : trustRelationships) {
							assertEquals(TEST_TRUSTOR_ID, trustRelationship.getTrustorId().getEntityId());
							assertEquals(TrustedEntityTypeBean.CSS, trustRelationship.getTrustorId().getEntityType());
							assertEquals(trustValueType, trustRelationship.getTrustValueType());
							if (TEST_TRUSTEE_ID.equals(trustRelationship.getTrusteeId().getEntityId()))
								assertTrue(trustRelationship.getTrustValue() > TEST_TRUST_VALUE_THRESHOLD);
						}
						helper.tearDownService(new IMethodCallback() {

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
							 */
							@Override
							public void returnException(String exception) {

								fail("tearDownService returned exception: " + exception);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
							 */
							@Override
							public void returnAction(String result) {

								fail("tearDownService returned action: " + result);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
							 */
							@Override
							public void returnAction(boolean resultFlag) {

								assertTrue(resultFlag);
								TestTrustClientHelper.this.testCompleted = true;
								latch.countDown();
							}
						});
					}

					@Override
					public void onRetrievedTrustValue(Double trustValue) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustValue: "
								+ trustValue);
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testRetrieveTrustRelationshipsByTrustorAndTrusteeTypeAndTrustValueType() throws Exception {
		
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(TEST_TRUSTOR_ID);

		final TrustedEntityIdBean trustorId =
				new TrustedEntityIdBean();
		trustorId.setEntityId(TEST_TRUSTOR_ID);
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustedEntityTypeBean trusteeType =
				TrustedEntityTypeBean.CSS;
		
		final TrustValueTypeBean trustValueType =
				TrustValueTypeBean.DIRECT;
		
		final TrustClientHelper helper = new TrustClientHelper(getContext());
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
				helper.retrieveTrustRelationships(requestor, trustorId, 
						trusteeType, trustValueType, new ITrustClientCallback() {

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onAddedDirectTrustEvidence()
					 */
					@Override
					public void onAddedDirectTrustEvidence() {
						
						// should not be called!
						fail("retrieveTrustRelationships callback onAddedDirectTrustEvidence");
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onException(org.societies.android.api.privacytrust.trust.TrustException)
					 */
					@Override
					public void onException(TrustException exception) {

						// should not be called!
						fail("retrieveTrustRelationships callback onException: "
								+ exception);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationship(org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean)
					 */
					@Override
					public void onRetrievedTrustRelationship(
							TrustRelationshipBean trustRelationship) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustRelationship: "
								+ trustRelationship);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationships(java.util.Set)
					 */
					@Override
					public void onRetrievedTrustRelationships(
							Set<TrustRelationshipBean> trustRelationships) {

						// success!
						assertNotNull(trustRelationships);
						assertFalse(trustRelationships.isEmpty());
						for (final TrustRelationshipBean trustRelationship : trustRelationships) {
							assertEquals(TEST_TRUSTOR_ID, trustRelationship.getTrustorId().getEntityId());
							assertEquals(TrustedEntityTypeBean.CSS, trustRelationship.getTrustorId().getEntityType());
							assertEquals(trusteeType, trustRelationship.getTrusteeId().getEntityType());
							assertEquals(trustValueType, trustRelationship.getTrustValueType());
							if (TEST_TRUSTEE_ID.equals(trustRelationship.getTrusteeId().getEntityId()))
								assertTrue(trustRelationship.getTrustValue() > TEST_TRUST_VALUE_THRESHOLD);
						}
						helper.tearDownService(new IMethodCallback() {

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
							 */
							@Override
							public void returnException(String exception) {

								fail("tearDownService returned exception: " + exception);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
							 */
							@Override
							public void returnAction(String result) {

								fail("tearDownService returned action: " + result);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
							 */
							@Override
							public void returnAction(boolean resultFlag) {

								assertTrue(resultFlag);
								TestTrustClientHelper.this.testCompleted = true;
								latch.countDown();
							}
						});
					}

					@Override
					public void onRetrievedTrustValue(Double trustValue) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustValue: "
								+ trustValue);
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testExceptionOnRetrieveTrustRelationshipsByTrustor() throws Exception {
		
		this.testCompleted = false;
		final CountDownLatch latch = new CountDownLatch(1);
		// Setup test data
		final RequestorBean requestor = new RequestorBean();
		requestor.setRequestorId(TEST_TRUSTOR_ID);

		final TrustedEntityIdBean trustorId =
				new TrustedEntityIdBean();
		trustorId.setEntityId(TEST_TRUSTEE_ID2);
		trustorId.setEntityType(TrustedEntityTypeBean.CSS);
		
		final TrustClientHelper helper = new TrustClientHelper(getContext());
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
				helper.retrieveTrustRelationships(requestor, trustorId, 
						new ITrustClientCallback() {

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onAddedDirectTrustEvidence()
					 */
					@Override
					public void onAddedDirectTrustEvidence() {
						
						// should not be called!
						fail("retrieveTrustRelationships callback onAddedDirectTrustEvidence");
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onException(org.societies.android.api.privacytrust.trust.TrustException)
					 */
					@Override
					public void onException(TrustException exception) {

						// success!
						Log.d(TAG, "retrieveTrustRelationships callback onException: " + exception);
						assertNotNull(exception);
						assertNotNull(exception.getMessage());
						helper.tearDownService(new IMethodCallback() {

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnException(java.lang.String)
							 */
							@Override
							public void returnException(String exception) {

								fail("tearDownService returned exception: " + exception);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
							 */
							@Override
							public void returnAction(String result) {

								fail("tearDownService returned action: " + result);
							}

							/*
							 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
							 */
							@Override
							public void returnAction(boolean resultFlag) {

								assertTrue(resultFlag);
								TestTrustClientHelper.this.testCompleted = true;
								latch.countDown();
							}
						});
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationship(org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean)
					 */
					@Override
					public void onRetrievedTrustRelationship(
							TrustRelationshipBean trustRelationship) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustRelationship: "
								+ trustRelationship);
					}

					/*
					 * @see org.societies.android.api.privacytrust.trust.ITrustClientCallback#onRetrievedTrustRelationships(java.util.Set)
					 */
					@Override
					public void onRetrievedTrustRelationships(
							Set<TrustRelationshipBean> trustRelationships) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustRelationships: "
								+ trustRelationships);
					}

					@Override
					public void onRetrievedTrustValue(Double trustValue) {

						// should not be called!
						fail("retrieveTrustRelationships callback onRetrievedTrustValue: "
								+ trustValue);
					}
				});
			}
		});
		
		latch.await(LATCH_TIME_OUT, TimeUnit.MILLISECONDS);
		assertTrue(this.testCompleted);
	}
}
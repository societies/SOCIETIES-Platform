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

package org.societies.integration.performance.test.upper_tester.rafik;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.integration.performance.test.lower_tester.PerformanceLowerTester;
import org.societies.integration.performance.test.lower_tester.PerformanceTestMgmtInfo;
import org.societies.integration.performance.test.lower_tester.PerformanceTestResult;
import org.societies.integration.performance.test.upper_tester.rafik.cismgmt.CisManagerClientCallback;
import org.societies.integration.performance.test.upper_tester.rafik.cismgmt.CreateCisParameters;
import org.societies.integration.performance.test.upper_tester.rafik.cismgmt.ICisMgmtPerformanceTest;
import org.societies.integration.performance.test.upper_tester.rafik.cismgmt.JoinCisParameters;


public class CisMgmtPerformanceTestImpl implements ICisMgmtPerformanceTest{

	private static Logger LOG = LoggerFactory.getLogger(CisMgmtPerformanceTestImpl.class);

	private ICisManager cisManager;
	private ICisManagerCallback cisManagerClientCallback;
	
	private IPrivacyPolicyManager privacyPolicyManager;
	
	private ICommManager commManager;

	private PerformanceLowerTester performanceLowerTester;
	private PerformanceTestResult performanceTestResult;

	public CisMgmtPerformanceTestImpl(){	
	}

	public void setCisManager(ICisManager cisManager) {
		if (null != cisManager) 
		{
			LOG.info("### [PerformanceTestImpl] cisManager injected");
			this.cisManager = cisManager;
		}
	}

	public ICisManager getCisManager()
	{
		if (null != cisManager) {
			LOG.info("### [PerformanceTestImpl] this.cisManager not null");
			return cisManager;
		}
		else {
			LOG.info("### [PerformanceTestImpl] this.cisManager null");
			return null;
		}
	}

	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		if (null != privacyPolicyManager) 
		{ 
			LOG.info("### [PerformanceTestImpl] privacyPolicyManager injected");
			this.privacyPolicyManager = privacyPolicyManager;
		}
	}
	
	public IPrivacyPolicyManager getPrivacyPolicyManager() {
		
		if (null != privacyPolicyManager) 
		{ 
			LOG.info("### [PerformanceTestImpl] this.privacyPolicyManager not null");
			return privacyPolicyManager;
		}
		else{
			LOG.info("### [PerformanceTestImpl] this.privacyPolicyManager null");
			return null;
		}
	}

	public void setCommManager(ICommManager commManager) {
		if (null != commManager) 
		{
			LOG.info("### [PerformanceTestImpl] commManager injected");
			this.commManager = commManager;
		}
	}
	
	public ICommManager getCommManager()
	{
		if (null != commManager) {
			LOG.info("### [PerformanceTestImpl] this.commManager not null");
			return commManager;
		}
		else {
			LOG.info("### [PerformanceTestImpl] this.commManager null");
			return null;
		}
	}

	@Override
	public void joinCisTest(PerformanceTestMgmtInfo performanceTestMgmtInfo, JoinCisParameters joinCisParameters) 
	{
		//The following 2 lines are mandatory in the beginning of the test 
		performanceLowerTester = new PerformanceLowerTester(performanceTestMgmtInfo);
		performanceLowerTester.testStart(this.getClass().getName(), getCommManager());
			
		if (null != joinCisParameters)
		{
			String cssOwnerId = joinCisParameters.getCssOwnerId();
			String cisId = joinCisParameters.getCisId();
			
			LOG.info("### [PerformanceTestImpl] cssOwnerId: " + cssOwnerId + "  cisId: "+ cisId);

			if (null != cssOwnerId && null != cisId && !"".equals(cssOwnerId) && !"".equals(cisId)) 
			{
				CisAdvertisementRecord adv = new CisAdvertisementRecord();

				adv.setCssownerid(cssOwnerId);
				adv.setId(cisId);

				if(null != getCisManager()) 
				{
					//If the result will be provided by a callback, the performanceLowerTester instance MUST be given to the callback implementation so that this latter can return a result. 
					cisManagerClientCallback = new CisManagerClientCallback(performanceLowerTester);

					getCisManager().joinRemoteCIS(adv, cisManagerClientCallback);	
				}
				else
				{
					performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The CIS Manager instance is null!",
																	PerformanceTestResult.ERROR_STATUS);
					performanceLowerTester.testFinish(performanceTestResult);
				}
			}
			else
			{
				performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The cssOwnerId and/or cisId are null or empty!",
																PerformanceTestResult.ERROR_STATUS);
				performanceLowerTester.testFinish(performanceTestResult);
			}
		}
		else
		{
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "joinCisParameters are null!",
																	PerformanceTestResult.ERROR_STATUS);
			performanceLowerTester.testFinish(performanceTestResult);
		}
	}

	
	@Override
	public void createCisTest(PerformanceTestMgmtInfo performanceTestMgmtInfo,
			CreateCisParameters createCisParameters) 
	{
		//The following 2 lines are mandatory in the beginning of the test
		performanceLowerTester = new PerformanceLowerTester(performanceTestMgmtInfo);
		performanceLowerTester.testStart(this.getClass().getName(), getCommManager());
		
		if (null != createCisParameters)
		{
			String cisName = createCisParameters.getCisName();
			String cisType = createCisParameters.getCisType();
			String cisDescription = createCisParameters.getCisDescription();
			String privacyPolicy = createCisParameters.getPrivacyPolicyWithoutRequestor();
			int waitingTime = createCisParameters.getWaitingTime();
			
			if (null != cisName && null != cisType && null != privacyPolicy && !"".equals(cisName) && !"".equals(cisType) && !"".equals(privacyPolicy)) 
			{
				if(null != getCisManager()) 
				{
					Hashtable<String, MembershipCriteria> cisMembershipCriteria = new Hashtable<String, MembershipCriteria>();
					
					Future<ICisOwned> futureCis = getCisManager().createCis(cisName, cisType, cisMembershipCriteria, cisDescription, privacyPolicy);
					
					if (null != futureCis) 
					{
						ICisOwned createdCis = null;
						
						// Retrieve future CIS
						try {
							createdCis = futureCis.get(waitingTime, TimeUnit.SECONDS);
						} catch (InterruptedException e)  {
							LOG.error("[Error "+e.getLocalizedMessage()+"]", e);
						
							performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "[InterruptedException]",
										PerformanceTestResult.FAILED_STATUS);
							performanceLowerTester.testFinish(performanceTestResult);
							
						} catch (ExecutionException e) {
							LOG.error("[Error "+e.getLocalizedMessage()+"]", e);
							
							performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "[ExecutionException]",
									PerformanceTestResult.FAILED_STATUS);
							performanceLowerTester.testFinish(performanceTestResult);
							
						} catch (TimeoutException e) {	
							LOG.error("[Error "+e.getLocalizedMessage()+"] ", e);
							
							performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "[TimeoutException]",
									PerformanceTestResult.FAILED_STATUS);
							performanceLowerTester.testFinish(performanceTestResult);
						}
						
						if (null != createdCis) 
						{
							String cisId =  createdCis.getCisId();
							
							String cisOwnedId =  createdCis.getOwnerId();
							
							if (null != cisId) 
							{
								
								// Check if the CIS is on the CIS Management registry
								ICis cisRetrieved =  getCisManager().getCis(cisId);
								
								if (null != cisRetrieved) 
								{
									if (createdCis.equals(cisRetrieved)) 
									{
										RequestPolicy expectedPrivacyPolicy = null;
										RequestPolicy retrievedPrivacyPolicy = null;
										
										try {
												IIdentity cisIdentity = getCommManager().getIdManager().fromJid(cisId);
										
												RequestorCis requestorCis = new RequestorCis(getCommManager().getIdManager().getThisNetworkNode(), cisIdentity);
										
										
												expectedPrivacyPolicy = getPrivacyPolicyManager().fromXMLString(privacyPolicy);
										
										
												expectedPrivacyPolicy.setRequestor(requestorCis);
										
												// Retrieve privacy policy
												retrievedPrivacyPolicy =  getPrivacyPolicyManager().getPrivacyPolicy(requestorCis);
												
										
										} catch (PrivacyException e) {
											LOG.error("[Error "+e.getLocalizedMessage()+"] ", e);
											performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "[Error PrivacyException]",
													PerformanceTestResult.FAILED_STATUS);
											performanceLowerTester.testFinish(performanceTestResult);
											
										} catch (InvalidFormatException e) {
											LOG.error("[Error "+e.getLocalizedMessage()+"] ", e);
											
											performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "[Error InvalidFormatException]",
													PerformanceTestResult.FAILED_STATUS);
											performanceLowerTester.testFinish(performanceTestResult);
										}
										
										
										if (null != retrievedPrivacyPolicy) 
										{
											if (expectedPrivacyPolicy.toXMLString().equals(retrievedPrivacyPolicy.toXMLString()) ) 
											{ 
												performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "CIS creation has been successfully done: " + "cis id = " + cisId + " Owned id = "+ cisOwnedId, PerformanceTestResult.SUCCESS_STATUS);
												performanceLowerTester.testFinish(performanceTestResult);
											}
											else
											{
												performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The privacy policy retreived from the DB is not equal to the expected privacy policy",
														PerformanceTestResult.FAILED_STATUS);
												performanceLowerTester.testFinish(performanceTestResult);
											}
										}
										else
										{
											performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The CIS Privacy Policy can't be retreived from the Privacy protection DB",
													PerformanceTestResult.FAILED_STATUS);
											performanceLowerTester.testFinish(performanceTestResult);
										}
										
									}
									else
									{
										performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The retreived CIS doesn't equal to the CIS created",
												PerformanceTestResult.FAILED_STATUS);

										performanceLowerTester.testFinish(performanceTestResult);
									}
								}
								else
								{
									performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Can't retreive the CIS from the CIS db using the cis id generated",
											PerformanceTestResult.FAILED_STATUS);

									performanceLowerTester.testFinish(performanceTestResult);
								}
							}
							else
							{
								performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The ID of the created CIS is null",
										PerformanceTestResult.FAILED_STATUS);

								performanceLowerTester.testFinish(performanceTestResult);
							}
							 
						}
						else
						{
							performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Nothing is created, Future's get method returns null",
									PerformanceTestResult.FAILED_STATUS);

							performanceLowerTester.testFinish(performanceTestResult);
						}
						
					}
					else
					{
						performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Create cis method returns null",
								PerformanceTestResult.FAILED_STATUS);

						performanceLowerTester.testFinish(performanceTestResult);
					}
					
					
				}
				else
				{
					performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The CIS Manager instance is null!",
							PerformanceTestResult.ERROR_STATUS);

					performanceLowerTester.testFinish(performanceTestResult);
				}
				
			}
			else{
				performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The cisName and/or cisType and/or privacyPolicy are null or empty!",
						PerformanceTestResult.ERROR_STATUS);
				performanceLowerTester.testFinish(performanceTestResult);
				
			}
		}
		else
		{
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "createCisParameters are null!",
					PerformanceTestResult.ERROR_STATUS);
			performanceLowerTester.testFinish(performanceTestResult);
		}
		
	}	
}

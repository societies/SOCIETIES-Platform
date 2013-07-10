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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.api.schema.cssmanagement.CssRequest;
import org.societies.integration.performance.test.lower_tester.PerformanceLowerTester;
import org.societies.integration.performance.test.lower_tester.PerformanceTestMgmtInfo;
import org.societies.integration.performance.test.lower_tester.PerformanceTestResult;
import org.societies.integration.performance.test.upper_tester.rafik.cssmgmt.ICssMgmtPerfromanceTest;
import org.societies.integration.performance.test.upper_tester.rafik.cssmgmt.SendMultiFriendInvParameters;

/**
 *
 * @author Rafik
 *
 */
public class CssMgmtPerformanceTestImpl implements ICssMgmtPerfromanceTest {

	private static Logger LOG = LoggerFactory.getLogger(CssMgmtPerformanceTestImpl.class);

	private ICSSInternalManager internalCssManager = null;

	private ICommManager commManager;

	private PerformanceLowerTester performanceLowerTester;
	private PerformanceTestResult performanceTestResult;

	public CssMgmtPerformanceTestImpl(){	
	}

	public void setCommManager(ICommManager commManager) {
		if (null != commManager) 
		{
			LOG.info("### [CssMgmtPerformanceTestImpl] commManager injected");
			this.commManager = commManager;
		}
	}
	public ICommManager getCommManager()
	{
		return commManager;
	}

	public ICSSInternalManager getInternalCssManager() 
	{
			return internalCssManager;
	}
	
	public void setInternalCssManager(ICSSInternalManager internalCssManager) {
		if (null != internalCssManager) 
		{
			LOG.info("### [CssMgmtPerformanceTestImpl] internalCssManager injected");
			this.internalCssManager = internalCssManager;
		}
	}

	@Override
	public void sendMultiFriendInvTest(PerformanceTestMgmtInfo performanceTestMgmtInfo, SendMultiFriendInvParameters sendMultiFriendInvParameters) {

		//The following 2 lines are mandatory in the beginning of the test 
		performanceLowerTester = new PerformanceLowerTester(performanceTestMgmtInfo);
		performanceLowerTester.testStart(this.getClass().getName(), getCommManager());

		if (null != sendMultiFriendInvParameters) 
		{
			String cssInvInitiatorId = sendMultiFriendInvParameters.getCssInvInitiatorId();
			String targetedCssStringList = sendMultiFriendInvParameters.getTargetedCssList();
			int invitationTimeout = sendMultiFriendInvParameters.getInvitationTimeout();
			String thisCss = null;

			if (null != getCommManager()) 
			{
				thisCss = getCommManager().getIdManager().getThisNetworkNode().getBareJid();
			}

			if (null != thisCss && !"".equals(thisCss))  
			{
				if (null != cssInvInitiatorId && !"".equals(cssInvInitiatorId)) 
				{
					if (null != targetedCssStringList &&  !"".equals(targetedCssStringList)) 
					{
						//This node is the initiator of the invitation
						if (thisCss.equals(cssInvInitiatorId)) 
						{
							if (null != getInternalCssManager()) 
							{
								String targetedCssList [] =  targetedCssStringList.split(";");
								
								

								for (int i = 0; i < targetedCssList.length; i++) 
								{
									LOG.info("### [CssMgmtPerformanceTestImpl] *************** "+ targetedCssList[i]);
									
									getInternalCssManager().sendCssFriendRequest(targetedCssList[i]);
								}

								Future<List<CssRequest>> futureFriendRequest = getInternalCssManager().findAllCssFriendRequests();

								try {

									try {
										List<CssRequest> cssRequests = futureFriendRequest.get(5, TimeUnit.SECONDS);

										LOG.info("### [CssMgmtPerformanceTestImpl] cssRequests list size *************** "+ cssRequests.size());
										
										boolean isPresent;
										boolean arePresent = true;

										for (int i = 0; i < targetedCssList.length; i++) 
										{
											isPresent = false;
											for (CssRequest cssRequest : cssRequests) 
											{
												if (cssRequest.getCssIdentity().equals(targetedCssList[i])) {
													isPresent = true;
													break;
												}
											}

											if (!isPresent) {
												arePresent = false;
												break;
											}
										}
										if (arePresent) {
											performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Sending CSS friend request has been successfuly done",
													PerformanceTestResult.SUCCESS_STATUS);
											performanceLowerTester.testFinish(performanceTestResult);
										}
										else{
											performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "Sending CSS friend request fails. One of the requests is not in the cssRequest list",
													PerformanceTestResult.FAILED_STATUS);
											performanceLowerTester.testFinish(performanceTestResult);
										}

									}
									catch (TimeoutException e) 
									{
										performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "TimeoutException triggred !",
												PerformanceTestResult.ERROR_STATUS);
										performanceLowerTester.testFinish(performanceTestResult);
									}
								} 
								catch (InterruptedException e) 
								{
									performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "InterruptedException triggred !",
											PerformanceTestResult.ERROR_STATUS);
									performanceLowerTester.testFinish(performanceTestResult);
								} 
								catch (ExecutionException e) 
								{
									performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "ExecutionException triggred !",
											PerformanceTestResult.ERROR_STATUS);
									performanceLowerTester.testFinish(performanceTestResult);
								}
								
							}
							else {
								performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The internalCssManager is null !",
										PerformanceTestResult.ERROR_STATUS);
								performanceLowerTester.testFinish(performanceTestResult);
							}
									
						}
						//This node will receive the invitation
						else
						{
							try {
								Thread.sleep(1000*invitationTimeout);
							} catch (InterruptedException e1) {
								LOG.info("### [CssMgmtPerformanceTestImpl] " + e1.getMessage());
							}
							
							
							if (null != getInternalCssManager()) 
							{
								Future<List<CssRequest>> futureCssRequest  = getInternalCssManager().findAllCssRequests();

								try 
								{
									List<CssRequest> cssRequests = futureCssRequest.get(5, TimeUnit.SECONDS);

									boolean isPresent = false;
									
									for (CssRequest cssRequest : cssRequests) {

										if (cssRequest.getCssIdentity().equals(cssInvInitiatorId)) 
										{
											isPresent = true;
											break;
										}
									}
									if (isPresent) {
										performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The CSS rceived the request",
												PerformanceTestResult.SUCCESS_STATUS);
										performanceLowerTester.testFinish(performanceTestResult);
									}
									else
									{
										performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The CSS doesn't receive the request",
												PerformanceTestResult.FAILED_STATUS);
										performanceLowerTester.testFinish(performanceTestResult);
									}
									

								} 
								catch (InterruptedException e) 
								{
									performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "InterruptedException triggred !",
											PerformanceTestResult.ERROR_STATUS);
									performanceLowerTester.testFinish(performanceTestResult);
								} 
								catch (ExecutionException e) 
								{
									performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "ExecutionException triggred !",
											PerformanceTestResult.ERROR_STATUS);
									performanceLowerTester.testFinish(performanceTestResult);
								} 
								catch (TimeoutException e) 
								{
									performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "TimeoutException triggred !",
											PerformanceTestResult.ERROR_STATUS);
									performanceLowerTester.testFinish(performanceTestResult);
								}
							}
							else 
							{
								performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The internalCssManager is null !",
										PerformanceTestResult.ERROR_STATUS);
								performanceLowerTester.testFinish(performanceTestResult);
							}
						}
					}
					else {
						performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "targetedCssStringList parameter is null or empty !",
								PerformanceTestResult.ERROR_STATUS);
						performanceLowerTester.testFinish(performanceTestResult);
					}
				}
				else {
					performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "cssInvInitiatorId parameter is null or empty !",
							PerformanceTestResult.ERROR_STATUS);
					performanceLowerTester.testFinish(performanceTestResult);
				}
			}
			else
			{
				performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "CSS JID is null or empty !",
						PerformanceTestResult.ERROR_STATUS);
				performanceLowerTester.testFinish(performanceTestResult);
			}
		}
		else
		{
			performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "sendMultiFriendInvParameters bean is null!",
					PerformanceTestResult.ERROR_STATUS);
			performanceLowerTester.testFinish(performanceTestResult);
		}


	}

}

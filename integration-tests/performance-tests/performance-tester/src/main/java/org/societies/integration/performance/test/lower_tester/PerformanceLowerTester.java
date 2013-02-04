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
package org.societies.integration.performance.test.lower_tester;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;


import com.restfb.json.JsonObject;

/**
 *
 * @author Rafik
 *
 */
public class PerformanceLowerTester {
	
	private PerformanceTestMgmtInfo performanceTestMgmtInfo;
	
	private String performanceTestEngineHost;
	private String testMode;
	
	private static final String PENDING_STATUS = "pending";
	
	private String testId;
	private String nodeId;
	private String className;
	private long startTestDate;
	private long endTestDate;
	private String testStatus;
	private String testResultMessage;
	
	private Calendar calendar;
	
	private JsonObject startTestResponse;
	private JsonObject finishTestResponse;
	
	private static Logger LOG = LoggerFactory.getLogger(PerformanceLowerTester.class);	
	
	/**
	 * 
	 */
	public PerformanceLowerTester(PerformanceTestMgmtInfo performanceTestMgmtInfo) {
		
		this.performanceTestMgmtInfo = performanceTestMgmtInfo;
		
		this.testId = this.performanceTestMgmtInfo.getTestCaseId();
		this.performanceTestEngineHost = this.performanceTestMgmtInfo.getPerformanceTestEngineHost();
		this.testMode = this.performanceTestMgmtInfo.getTestMode();
	}
	
	/**
	 * 
	 */
	public void testStart(String className, ICommManager commManager) 
	{
		
		calendar = Calendar.getInstance();
		startTestDate = calendar.getTime().getTime();
		this.testStatus = this.PENDING_STATUS;
		this.className = className;
		
		startTestResponse = new JsonObject();
		
		if (commManager != null) 
		{ 
			this.nodeId = commManager.getIdManager().getThisNetworkNode().getBareJid();
			
			
			startTestResponse.put("test_id", this.testId);
			startTestResponse.put("node_id", this.nodeId);
			startTestResponse.put("class_name", this.className);
			startTestResponse.put("status", this.testStatus);
			startTestResponse.put("start_test_date", startTestDate/1000);
			
			if (null != testMode) 
			{
				if (this.testMode.equalsIgnoreCase("dev_mode")) 
				{
					LOG.info("### DEV MODE SELECTED ###");
					LOG.info("### [PerformanceLowerTester] result: " + startTestResponse.toString());
				}
				else if (this.testMode.equalsIgnoreCase("prod_mode")) 
				{
					LOG.info("### PROD MODE SELECTED ###");
					WebServiceCommunication.sendStartResponse(this.performanceTestEngineHost, startTestResponse.toString());
				}
				else
				{
					LOG.info("### DEFAULT MODE ###");
					LOG.info("### [PerformanceLowerTester] result: " + startTestResponse.toString());
				}
			}
			else
			{
				LOG.info("### DEFAULT MODE ###");
				LOG.info("### [PerformanceLowerTester] result: " + startTestResponse.toString());
			}

						
		}
		else
		{
			PerformanceTestResult performanceTestResult = new PerformanceTestResult(this.getClass().getName(), "The communication manager instance is null !", 
																				PerformanceTestResult.ERROR_STATUS);	
			
			testFinish(performanceTestResult);
		}
	}
	
	
	public void testFinish(PerformanceTestResult performanceTestResult) {
		
		calendar = Calendar.getInstance();
		endTestDate = calendar.getTime().getTime();
		
		finishTestResponse = new JsonObject();
		
		this.className = performanceTestResult.getClassName();
		this.testStatus = performanceTestResult.getTestStatus();
		this.testResultMessage = performanceTestResult.getTestResultMessage();
			
		finishTestResponse.put("test_id", this.testId);
		finishTestResponse.put("class_name", this.className);
		finishTestResponse.put("node_id", this.nodeId);
		finishTestResponse.put("status", this.testStatus);
		finishTestResponse.put("end_test_date", endTestDate/1000);
		finishTestResponse.put("test_result_message", this.testResultMessage);

		if (null != testMode) 
		{
			if (this.testMode.equalsIgnoreCase("dev_mode")) 
			{
				LOG.info("### DEV MODE SELECTED ###");
				LOG.info("### [PerformanceLowerTester] result: " + finishTestResponse.toString());
			}
			else if (this.testMode.equalsIgnoreCase("prod_mode")) 
			{
				LOG.info("### PROD MODE SELECTED ###");
				WebServiceCommunication.sendFinishResponse(this.performanceTestEngineHost, finishTestResponse.toString());
			}
			else
			{
				LOG.info("### DEFAULT MODE ###");
				LOG.info("### [PerformanceLowerTester] result: " + startTestResponse.toString());
			}	
		}
		else
		{
			LOG.info("### DEFAULT MODE ###");
			LOG.info("### [PerformanceLowerTester] result: " + startTestResponse.toString());
		}
		
	}
}

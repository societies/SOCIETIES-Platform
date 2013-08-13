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
package org.societies.integration.test;

import org.junit.runner.Result;
import org.societies.integration.test.userfeedback.UserFeedbackMocker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Specific test case class for integration test
 * 
 * This class has to be extended, its entry point is
 * the "run" method: it launch the whole test case.
 * This may help to implement integration test case.
 * This class use the stateless IntegrationTestUtils
 * 
 * @author Rafik Said-Mansour (Trialog)
 * @author Olivier Maridat (Trialog)
 *
 */
public abstract class IntegrationTestCase {
	/**
	 * Tools for integration test
	 */
	public IntegrationTestUtils integrationTestUtils;
	/**
	 * Number of the test case on Redmine
	 */
	public final Integer testCaseNumber ;
	/**
	 * Test case classes to launch on Virgo for this integration test case
	 */
	public final Class testCaseClasses[];
	/**
	 * Parameter : timeout
	 */
	private static int timeout;
	/**
	 * Parameter : user feedback mock manager
	 * disabled by default
	 */
	private static UserFeedbackMocker userFeedbackMocker;
	
	/**
	 * This constructor specifies the test case number
	 * and the array of test case classes to run
	 */
	public IntegrationTestCase(int testCaseNumber, Class... testCaseClasses) {
		this.testCaseNumber = testCaseNumber;
		this.testCaseClasses = testCaseClasses;
		integrationTestUtils = new IntegrationTestUtils();
		timeout = 5000;
	}
	
	
	/**
	 * Run the test case
	 * 
	 * @param testCaseNumber Test case number
	 * @param testCaseClasses Classes to run to manage the test case
	 * @return result of the test case
	 */
	public Result run() {
		return integrationTestUtils.run(testCaseNumber, testCaseClasses);
	}
	
	
	/* --- Dependency Injection --- */
	public static int getTimeout() {
		return timeout;
	}
	@Value("${timeout:2000}")
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public static UserFeedbackMocker getUserFeedbackMocker() {
		return userFeedbackMocker;
	}
	@Autowired
	public void setUserFeedbackMocker(UserFeedbackMocker userFeedbackMocker) {
		this.userFeedbackMocker = userFeedbackMocker;
	}
}

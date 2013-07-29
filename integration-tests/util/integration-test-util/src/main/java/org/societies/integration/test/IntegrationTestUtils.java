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

import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tool to manage integration tests.
 * 
 * @author Rafik Said-Mansour (Trialog)
 * @author Olivier Maridat (Trialog)
 *
 */
public class IntegrationTestUtils {
	private static Logger LOG = LoggerFactory.getLogger(IntegrationTestUtils.class);
	public static JUnitCore jUnitCore;
	
	
	public IntegrationTestUtils() {
		jUnitCore = new JUnitCore();
	}
	
	/**
	 * Run the test case number "testCaseNumber" using "testCaseClasses" classes
	 * 
	 * @param testCaseNumber Test case number
	 * @param testCaseClasses Classes to run to manage the test case
	 * @return result of the test case
	 */
	public Result run(int testCaseNumber, Class... testCaseClasses) {
		// -- Display test case title
		StringBuffer title = new StringBuffer("[#"+testCaseNumber+"][Test Suite] Start the integration test case for classes: ");
		for (int j=0; j<testCaseClasses.length; j++) {
			title.append(testCaseClasses[j].getSimpleName()+(j == (testCaseClasses.length-1) ? "" : ", "));
		}
		LOG.info(title.toString());
		
		// -- Run the test case
		Result testCaseResult = jUnitCore.run(testCaseClasses);
		
		// -- Display result
		String results = new String();
		String testClass = "Class: ";
		String testFailCt = "Failure Count: ";
		String testFalures = "Failures: ";
		String testRunCt = "Runs: ";
		String testRunTm = "Run Time: ";
		String testSuccess = "Success: ";
		String newln = " \n";
		for (int j=0; j<testCaseClasses.length; j++) {
			results += testClass + testCaseClasses[j].getName() + newln;
		}
		results += testFailCt + testCaseResult.getFailureCount() + newln;
		results += testFalures + newln;
		List<Failure> failures = testCaseResult.getFailures();
		int i = 0;
		for (Failure x: failures) {
			i++;
			results += i +": " + x + newln;
		}
		results += testRunCt + testCaseResult.getRunCount() + newln;
		results += testRunTm + testCaseResult.getRunTime() + newln;
		results += testSuccess + testCaseResult.wasSuccessful() + newln;
		
		LOG.info("[#"+testCaseNumber+"] "+ results);
		
		for (Failure x: failures) {
			x.getException().printStackTrace();
		}
		return testCaseResult;
	}
}

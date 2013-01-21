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

/**
 * Describe your class here...
 *
 * @author Rafik
 *
 */
public class PerformanceTestResult {
	
	/**
	 * To be used to return a success test result
	 */
	public static final String SUCCESS_STATUS = "success";
	
	/**
	 * To be used to return an error test result
	 */
	public static final String ERROR_STATUS = "error";
	
	/**
	 * To be used to return a failed test result
	 */
	public static final String FAILED_STATUS = "failed";
	
	private String testResultMessage;
	private String testStatus;
	private String className;
	
	
	/**
	 * @param className Represents the class that starts the test or that returns the result to the performnace engine, has to be get by MyClass.getClass().getName()
	 * @param testResultMessage Represents a message to be returned to explain the status (ERROR, FAILED or SUCCESS) of the result. This String has to be get from this Class
	 * @param testStatus Represents the status of the result. This has to be one status constant from the Class "PerformanceTestResult"
	 */
	public PerformanceTestResult(String className, String testResultMessage, String testStatus) 
	{
		this.testResultMessage = testResultMessage;
		this.testStatus = testStatus;
		this.className = className;
	}
	
	
	/**
	 * @return the testResultMessage
	 */
	public String getTestResultMessage() {
		return testResultMessage;
	}
	/**
	 * @param testResultMessage the testResultMessage to set
	 */
	public void setTestResultMessage(String testResultMessage) {
		this.testResultMessage = testResultMessage;
	}
	/**
	 * @return the testStatus
	 */
	public String getTestStatus() {
		return testStatus;
	}
	/**
	 * @param testStatus the testStatus to set
	 */
	public void setTestStatus(String testStatus) {
		this.testStatus = testStatus;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
}

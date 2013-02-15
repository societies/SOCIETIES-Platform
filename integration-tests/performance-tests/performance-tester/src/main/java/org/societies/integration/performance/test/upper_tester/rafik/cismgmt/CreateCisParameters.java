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
package org.societies.integration.performance.test.upper_tester.rafik.cismgmt;

/**
 *
 * @author Rafik
 *
 */
public class CreateCisParameters {
	
	private String cisName;
	private String cisType;
	private String cisDescription;
	private String privacyPolicyWithoutRequestor;
	private int waitingTime;

	/**
	 * 
	 */
	public CreateCisParameters() {

	}

	

	/**
	 * @param cisName
	 * @param cisType
	 * @param cisDescription
	 * @param privacyPolicyWithoutRequestor
	 * @param waitingTime
	 */
	public CreateCisParameters(String cisName, String cisType,
			String cisDescription, String privacyPolicyWithoutRequestor,
			int waitingTime) {
		super();
		this.cisName = cisName;
		this.cisType = cisType;
		this.cisDescription = cisDescription;
		this.privacyPolicyWithoutRequestor = privacyPolicyWithoutRequestor;
		this.waitingTime = waitingTime;
	}



	/**
	 * @return the waitingTime
	 */
	public int getWaitingTime() {
		return waitingTime;
	}

	/**
	 * @param waitingTime the waitingTime to set
	 */
	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}

	/**
	 * @return the cisName
	 */
	public String getCisName() {
		return cisName;
	}

	/**
	 * @param cisName the cisName to set
	 */
	public void setCisName(String cisName) {
		this.cisName = cisName;
	}

	/**
	 * @return the cisType
	 */
	public String getCisType() {
		return cisType;
	}

	/**
	 * @param cisType the cisType to set
	 */
	public void setCisType(String cisType) {
		this.cisType = cisType;
	}

	/**
	 * @return the cisDescription
	 */
	public String getCisDescription() {
		return cisDescription;
	}

	/**
	 * @param cisDescription the cisDescription to set
	 */
	public void setCisDescription(String cisDescription) {
		this.cisDescription = cisDescription;
	}

	/**
	 * @return the privacyPolicyWithoutRequestor
	 */
	public String getPrivacyPolicyWithoutRequestor() {
		return privacyPolicyWithoutRequestor;
	}

	/**
	 * @param privacyPolicyWithoutRequestor the privacyPolicyWithoutRequestor to set
	 */
	public void setPrivacyPolicyWithoutRequestor(
			String privacyPolicyWithoutRequestor) {
		this.privacyPolicyWithoutRequestor = privacyPolicyWithoutRequestor;
	}
}

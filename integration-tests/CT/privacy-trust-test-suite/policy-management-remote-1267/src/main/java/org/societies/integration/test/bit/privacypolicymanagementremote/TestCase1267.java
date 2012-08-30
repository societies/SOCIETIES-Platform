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
package org.societies.integration.test.bit.privacypolicymanagementremote;

/**
 * The test case 1244 aims to test the privacy policy management
 * real usage, using the context broker and the communication
 * framework.
 * 
 * @author Olivier Maridat (Trialog)
 *
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.privacytrust.privacyprotection.remote.IPrivacyAgreementManagerRemote;
import org.societies.api.internal.privacytrust.privacyprotection.remote.IPrivacyPolicyManagerRemote;
import org.societies.integration.test.IntegrationTestCase;

public class TestCase1267 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase1267.class.getSimpleName());

	public static IPrivacyPolicyManagerRemote privacyPolicyManagerRemote;
	public static IPrivacyAgreementManagerRemote privacyAgreementManagerRemote;
	public static ICommManager commManager;
	
	
	public TestCase1267() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		super(1267, new Class[]{PrivacyPolicyManagerTest.class});
		PrivacyPolicyManagerTest.testCaseNumber = this.testCaseNumber;
	}
	
	
	/* -- Dependency injection --- */
	public void setPrivacyPolicyManagerRemote(IPrivacyPolicyManagerRemote privacyPolicyManagerRemote) {
		this.privacyPolicyManagerRemote = privacyPolicyManagerRemote;
		LOG.info("[#"+testCaseNumber+"] [DependencyInjection] IPrivacyPolicyManagerRemote injected");
	}
	public void setPrivacyAgreementManagerRemote(IPrivacyAgreementManagerRemote privacyAgreementManagerRemote) {
		this.privacyAgreementManagerRemote = privacyAgreementManagerRemote;
		LOG.info("[#"+testCaseNumber+"] [DependencyInjection] IPrivacyAgreementManagerRemote injected");
	}
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		LOG.info("[#"+testCaseNumber+"] [DependencyInjection] ICommManager injected");
	}

	public static boolean isDepencyInjectionDone() {
		return isDepencyInjectionDone(0);
	}
	public static boolean isDepencyInjectionDone(int level) {
		if (null == commManager) {
			LOG.info("[Dependency Injection] Missing ICommManager");
			return false;
		}
		if (null == commManager.getIdManager()) {
			LOG.info("[Dependency Injection] Missing IIdentityManager");
			return false;
		}
		if (null == privacyPolicyManagerRemote) {
			LOG.info("[Dependency Injection] Missing IPrivacyPolicyManagerRemote");
			return false;
		}
		if (null == privacyAgreementManagerRemote) {
			LOG.info("[Dependency Injection] Missing IPrivacyAgreementManagerRemote");
			return false;
		}
		return true;
	}
}
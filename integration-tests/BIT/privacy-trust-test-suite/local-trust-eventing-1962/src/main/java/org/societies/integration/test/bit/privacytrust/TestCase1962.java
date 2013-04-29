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
package org.societies.integration.test.bit.privacytrust;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.integration.test.IntegrationTestCase;

/**
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.0
 */
public class TestCase1962 extends IntegrationTestCase {

	private static ITrustEvidenceCollector internalTrustEvidenceCollector;
	private static ITrustBroker internalTrustBroker;
	private static ICommManager commManager;
	
	public TestCase1962() {

		super(1962, new Class[] { TestByTrustor.class,
				TestByTrustorTrusteeType.class,
				TestByTrustorTrustee.class,
				TestByTrustorValueType.class,
				TestByTrustorTrusteeTypeValueType.class,
				TestByTrustorTrusteeValueType.class });
	}

	/**
	 * @return the Trust Evidence Collector
	 */
	public static ITrustEvidenceCollector getInternalTrustEvidenceCollector() {

		return TestCase1962.internalTrustEvidenceCollector;
	}

	/**
	 * @param trustEvidenceCollector the Trust Evidence Collector to set
	 */
	public void setInternalTrustEvidenceCollector(ITrustEvidenceCollector internalTrustEvidenceCollector) {

		TestCase1962.internalTrustEvidenceCollector = internalTrustEvidenceCollector;
	}
	
	/**
	 * @return the Trust Broker
	 */
	public static ITrustBroker getInternalTrustBroker() {

		return TestCase1962.internalTrustBroker;
	}

	/**
	 * @param trustBroker the Trust Broker to set
	 */
	public void setInternalTrustBroker(ITrustBroker internalTrustBroker) {

		TestCase1962.internalTrustBroker = internalTrustBroker;
	}
	
	/**
	 * @return the commMgr
	 */
	public static ICommManager getCommManager() {

		return TestCase1962.commManager ;
	}

	/**
	 * @param commMgr the commMgr to set
	 */
	public void setCommManager(ICommManager commMgr) {

		TestCase1962.commManager = commMgr;
	}		
}

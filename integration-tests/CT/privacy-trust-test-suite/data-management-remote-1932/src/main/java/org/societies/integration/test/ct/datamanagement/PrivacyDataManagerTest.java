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
package org.societies.integration.test.ct.datamanagement;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.DataWrapperFactory;
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.LocationCoordinatesUtils;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IDataObfuscationListener;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.LocationCoordinates;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.integration.test.IntegrationTest;


/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyDataManagerTest extends IntegrationTest implements IDataObfuscationListener {
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerTest.class);


	private RequestorBean requestor;

	@Before
	public void setUp() {
		LOG.info("[#"+testCaseNumber+"] "+getClass().getSimpleName()+"::setUp");
		// Dependency injection not ready
		if (!TestCase.isDepencyInjectionDone()) {
			fail("[Dependency Injection] "+getClass().getSimpleName()+" not ready");
		}
		// Data
		IIdentity currentNode = TestCase.commManager.getIdManager().getThisNetworkNode();
		requestor = RequestorUtils.create(TestCase.targettedNodeJid);
	}

	/* --- OBFUSCATION --- */

	@Test
	public void testObfuscateDataName()
	{
		String testTitle = "ObfuscateData: name";
		LOG.info("[#"+testCaseNumber+"] "+testTitle);
		try {
			DataWrapper wrapper = DataWrapperFactory.getNameWrapper("Olivier", "Maridat");
			TestCase.privacyDataManagerRemote.obfuscateData(requestor, wrapper, this);
		}
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException obfuscator error] "+testTitle, e);
			fail("PrivacyException obfuscator error ("+e.getMessage()+") "+testTitle);
		}
	}

	@Test
	public void testObfuscateDataLocationCoordinates()
	{
		String testTitle = new String("ObfuscateData: coordinate location");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		try {
			DataWrapper wrapper = DataWrapperFactory.getLocationCoordinatesWrapper(48.856666, 2.350987, 542.0);
			TestCase.privacyDataManagerRemote.obfuscateData(requestor, wrapper, this);
		}
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException obfuscator error] "+testTitle, e);
			fail("PrivacyException obfuscator error ("+e.getMessage()+") "+testTitle);
		}
	}


	/* -- Listener -- */

	@Override
	public void onObfuscationDone(DataWrapper obfuscatedDataWrapper) {
		LocationCoordinates obfuscatedData = DataWrapperFactory.retrieveLocationCoordinates(obfuscatedDataWrapper);
		assertNotNull("Obfuscated data should not be null", obfuscatedDataWrapper);
		LOG.info("[#"+testCaseNumber+"] Obfuscated location:\n"+LocationCoordinatesUtils.toJsonString(obfuscatedData));
	}

	@Override
	public void onObfuscationCancelled(String msg) {
		LOG.error("[#"+testCaseNumber+"] Cancelled: Didn't manage to obfuscate the data: "+msg);
		fail("Cancelled: Didn't manage to obfuscate the data: "+msg);
	}

	@Override
	public void onObfuscationAborted(String msg, Exception e) {
		LOG.error("[#"+testCaseNumber+"] Aborted: Didn't manage to obfuscate the data: "+msg, e);
		fail("Aborted: Didn't manage to obfuscate the data: "+msg);
	}

	@Override
	public void onObfuscatedVersionRetrieved(CtxIdentifier dataId, boolean retrieved) {
		LOG.info("[#"+testCaseNumber+"] Obfuscated version retrieved: "+retrieved);
	}
}

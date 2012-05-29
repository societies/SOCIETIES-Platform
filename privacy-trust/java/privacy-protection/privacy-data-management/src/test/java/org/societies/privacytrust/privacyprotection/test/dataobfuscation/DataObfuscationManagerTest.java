/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.privacytrust.privacyprotection.test.dataobfuscation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.DataWrapperFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.LocationCoordinates;
import org.societies.privacytrust.privacyprotection.api.IDataObfuscationManager;
import org.societies.privacytrust.privacyprotection.dataobfuscation.DataObfuscationManager;

/**
 * @author Olivier Maridat (Trialog)
 */
@RunWith(JUnitParamsRunner.class)
public class DataObfuscationManagerTest {
	private static Logger LOG = LoggerFactory.getLogger(DataObfuscationManagerTest.class.getSimpleName());

	public IDataObfuscationManager dataObfuscationManager;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dataObfuscationManager = new DataObfuscationManager();
	}
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		dataObfuscationManager = null;
	}

	@Test
    @Parameters({ "1.0", "0.5", "0.1" })
	public void testObfuscateData(double obfuscationLevel) {
		LOG.info("[Test begin] testObfuscateData("+obfuscationLevel+")");
		IDataWrapper<LocationCoordinates> locationCoordinatesWrapper = DataWrapperFactory.getLocationCoordinatesWrapper(48.856666, 2.350987, 542.0);
		IDataWrapper<LocationCoordinates> obfuscatedDataWrapper = null;
		try {
			obfuscatedDataWrapper = dataObfuscationManager.obfuscateData(locationCoordinatesWrapper, obfuscationLevel);
		} catch (PrivacyException e) {
			LOG.info("testObfuscateData(): obfuscation error "+e.getLocalizedMessage()+"\n", e);
			fail("testObfuscateData(): obfuscation error "+e.getLocalizedMessage());
		}
		// Verify
		LOG.info("### Orginal location:\n"+locationCoordinatesWrapper.getData().toJSONString());
		LOG.info("### Obfuscated location:\n"+obfuscatedDataWrapper.getData().toJSONString());
		assertNotNull("Obfuscated data null", obfuscatedDataWrapper);
		if (1 == obfuscationLevel) {
			assertEquals("Data obfuscated more than 1", obfuscatedDataWrapper, locationCoordinatesWrapper);
		}
		else {
			assertTrue("Data obfuscated to "+obfuscationLevel+", but result has same latitude, longitude and accuracy", (obfuscatedDataWrapper.getData().getLatitude() != locationCoordinatesWrapper.getData().getLatitude())
						|| (obfuscatedDataWrapper.getData().getLongitude() != locationCoordinatesWrapper.getData().getLongitude())
						|| (obfuscatedDataWrapper.getData().getAccuracy() != locationCoordinatesWrapper.getData().getAccuracy()));
		}
	}
	
	@Test
	@Parameters({ "-1", "2.5" })
	public void testObfuscateDataOutOfBound(double obfuscationLevel) {
		LOG.info("[Test begin] testObfuscateDataOutOfBound("+obfuscationLevel+")");
		IDataWrapper<LocationCoordinates> locationCoordinatesWrapper = DataWrapperFactory.getLocationCoordinatesWrapper(48.856666, 2.350987, 542.0);
		IDataWrapper<LocationCoordinates> obfuscatedDataWrapper = null;
		try {
			obfuscatedDataWrapper = dataObfuscationManager.obfuscateData(locationCoordinatesWrapper, obfuscationLevel);
		} catch (PrivacyException e) {
			LOG.info("testObfuscateDataOutOfBound(): obfuscation error "+e.getLocalizedMessage()+"\n", e);
			fail("testObfuscateDataOutOfBound(): obfuscation error "+e.getLocalizedMessage());
		}
		// Verify
		LOG.info("### Orginal location:\n"+locationCoordinatesWrapper.getData().toJSONString());
		LOG.info("### Obfuscated location:\n"+obfuscatedDataWrapper.getData().toJSONString());
		assertNotNull("Obfuscated data null", obfuscatedDataWrapper);
		if (obfuscationLevel >= 1) {
			assertEquals("Data obfuscated more than 1", obfuscatedDataWrapper, locationCoordinatesWrapper);
		}
		else {
			assertTrue("Data obfuscated to "+obfuscationLevel+", but result has same latitude, longitude and accuracy", (obfuscatedDataWrapper.getData().getLatitude() != locationCoordinatesWrapper.getData().getLatitude())
					|| (obfuscatedDataWrapper.getData().getLongitude() != locationCoordinatesWrapper.getData().getLongitude())
					|| (obfuscatedDataWrapper.getData().getAccuracy() != locationCoordinatesWrapper.getData().getAccuracy()));
		}
	}
}

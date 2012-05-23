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
package org.societies.privacytrust.privacyprotection.test.dataobfuscation.obfuscator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

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
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.Name;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.NameWrapper;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator.NameObfuscator;

/**
 * @author Olivier Maridat (Trialog)
 */
@RunWith(JUnitParamsRunner.class)
public class NameObfuscatorTest {
	private static Logger LOG = LoggerFactory.getLogger(NameObfuscatorTest.class.getSimpleName());

	public NameObfuscator obfuscator;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		obfuscator = new NameObfuscator(DataWrapperFactory.getNameWrapper("Olivier", "Maridat"));
	}
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		obfuscator = null;
	}


	private Object[] parametersForObfuscateData() {
		return new Object[] { 0, 1.0/4.0, 2.0/4.0, 3.0/4.0, 1 };
	}

	@Test
	@Parameters(method = "parametersForObfuscateData")
	public void testObfuscateData(double obfuscationLevel) {
		LOG.info("[Test begin] testObfuscateData("+obfuscationLevel+")");
		IDataWrapper<Name> obfuscatedDataWrapper = null;
		try {
			obfuscatedDataWrapper = obfuscator.obfuscateData(obfuscationLevel);
		} catch (PrivacyException e) {
			LOG.info("testObfuscateData(): obfuscation error "+e.getLocalizedMessage()+"\n", e);
			fail("testObfuscateData(): obfuscation error "+e.getLocalizedMessage());
		}
		// Verify
		LOG.info("### Orginal name:\n"+obfuscator.getDataWrapper().getData().toString());
		LOG.info("### Obfuscated name:\n"+obfuscatedDataWrapper.getData().toString());
		assertNotNull("Obfuscated data null", obfuscatedDataWrapper);
		if (0 == obfuscationLevel) {
			assertEquals("Data not well obfuscated", obfuscatedDataWrapper.getData(), new Name("", ""));
		}
		else if (1/4 == obfuscationLevel) {
			assertEquals("Data not well obfuscated", obfuscatedDataWrapper.getData(), new Name("O.", "M."));
		}
		else if (2/4 == obfuscationLevel) {
			assertEquals("Data not well obfuscated", obfuscatedDataWrapper.getData(), new Name("Olivier", ""));
		}
		else if (3/4 == obfuscationLevel) {
			assertEquals("Data not well obfuscated", obfuscatedDataWrapper.getData(), new Name("", "Maridat"));
		}
		else if (1 == obfuscationLevel) {
			assertEquals("Data not well obfuscated", obfuscatedDataWrapper.getData(), obfuscator.getDataWrapper().getData());
		}
	}

	@Test
	@Parameters({ "-1.0", "2.5" })
	public void testObfuscateDataOutOfBound(double obfuscationLevel) {
		LOG.info("[Test begin] testObfuscateData("+obfuscationLevel+")");
		IDataWrapper<Name> obfuscatedDataWrapper = null;
		try {
			obfuscatedDataWrapper = obfuscator.obfuscateData(obfuscationLevel);
		} catch (PrivacyException e) {
			LOG.info("testObfuscateDataOutOfBound(): obfuscation error "+e.getLocalizedMessage()+"\n", e);
			fail("testObfuscateDataOutOfBound(): obfuscation error "+e.getLocalizedMessage());
		}
		// Verify
		LOG.info("### Orginal name:\n"+obfuscator.getDataWrapper().getData().toString());
		LOG.info("### Obfuscated name:\n"+obfuscatedDataWrapper.getData().toString());
		assertNotNull("Obfuscated data null", obfuscatedDataWrapper);
		if (obfuscationLevel < 0) {
			assertEquals("Data not well obfuscated", obfuscatedDataWrapper.getData(), new Name("", ""));
		}
		else {
			assertEquals("Data not well obfuscated", obfuscatedDataWrapper.getData(), obfuscator.getDataWrapper().getData());
		}
	}
}

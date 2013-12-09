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
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.DataWrapperFactory;
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.NameUtils;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.Name;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
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
		return new Object[] { 0.0, 1.0/5.0, 2.0/5.0, 3.0/5.0, 4.0/5.0, 1.0 };
	}

	@Test
	@Parameters(method = "parametersForObfuscateData")
	public void testObfuscateData(double obfuscationLevel) {
		LOG.info("[Test begin] testObfuscateData("+obfuscationLevel+")");
		DataWrapper obfuscatedDataWrapper = null;
		try {
			obfuscatedDataWrapper = obfuscator.obfuscateData(obfuscationLevel);
		} catch (PrivacyException e) {
			LOG.info("testObfuscateData(): obfuscation error "+e.getLocalizedMessage()+"\n", e);
			fail("testObfuscateData(): obfuscation error "+e.getLocalizedMessage());
		}
		// Verify
		assertNotNull("Obfuscated data null", obfuscatedDataWrapper);
		LOG.info("### Orginal name:\n"+NameUtils.toString((Name) obfuscator.getDataWrapper().getData()));
		LOG.info("### Obfuscated name:\n"+NameUtils.toString((Name) obfuscatedDataWrapper.getData()));
		Name actual = (Name) obfuscatedDataWrapper.getData();
		if (0 == obfuscationLevel) {
			Name expected = NameUtils.create("", "");
			assertTrue("Data not well obfuscated (expected: "+NameUtils.toString(expected)+" but was "+NameUtils.toString(actual)+")", NameUtils.equals(actual, expected));
		}
		else if ((double)1/(double)5 == obfuscationLevel) {
			Name expected = NameUtils.create("O.", "M.");
			assertTrue("Data not well obfuscated (expected: "+NameUtils.toString(expected)+" but was "+NameUtils.toString(actual)+")", NameUtils.equals(actual, expected));
		}
		else if ((double)2/(double)5 == obfuscationLevel) {
			Name expected = NameUtils.create("Olivier", "M.");
			assertTrue("Data not well obfuscated (expected: "+NameUtils.toString(expected)+" but was "+NameUtils.toString(actual)+")", NameUtils.equals(actual, expected));
		}
		else if ((double)3/(double)5 == obfuscationLevel) {
			Name expected = NameUtils.create("O.", "Maridat");
			assertTrue("Data not well obfuscated (expected: "+NameUtils.toString(expected)+" but was "+NameUtils.toString(actual)+")", NameUtils.equals(actual, expected));
		}
		else if (1 == obfuscationLevel || (double)4/(double)5 == obfuscationLevel) {
			Name expected = NameUtils.create("Olivier", "Maridat");
			assertTrue("Data not well obfuscated (expected: "+NameUtils.toString(expected)+" but was "+NameUtils.toString(actual)+")", NameUtils.equals(actual, expected));
		}
	}

	@Test
	@Parameters({ "-1.0", "2.5" })
	public void testObfuscateDataOutOfBound(double obfuscationLevel) {
		LOG.info("[Test begin] testObfuscateData("+obfuscationLevel+")");
		DataWrapper obfuscatedDataWrapper = null;
		try {
			obfuscatedDataWrapper = obfuscator.obfuscateData(obfuscationLevel);
		} catch (PrivacyException e) {
			LOG.info("testObfuscateDataOutOfBound(): obfuscation error "+e.getLocalizedMessage()+"\n", e);
			fail("testObfuscateDataOutOfBound(): obfuscation error "+e.getLocalizedMessage());
		}
		// Verify
		assertNotNull("Obfuscated data null", obfuscatedDataWrapper);
		LOG.info("### Orginal name:\n"+NameUtils.toString((Name) obfuscator.getDataWrapper().getData()));
		LOG.info("### Obfuscated name:\n"+NameUtils.toString((Name) obfuscatedDataWrapper.getData()));
		Name actual = (Name) obfuscatedDataWrapper.getData();
		if (obfuscationLevel < 0) {
			Name expected = NameUtils.create("", "");
			assertTrue("Data not well obfuscated (expected: "+NameUtils.toString(expected)+" but was "+NameUtils.toString(actual)+")", NameUtils.equals(actual, expected));
		}
		else {
			Name expected = NameUtils.create("Olivier", "Maridat");
			assertTrue("Data not well obfuscated (expected: "+NameUtils.toString(expected)+" but was "+NameUtils.toString(actual)+")", NameUtils.equals(actual, expected));
		}
	}
}

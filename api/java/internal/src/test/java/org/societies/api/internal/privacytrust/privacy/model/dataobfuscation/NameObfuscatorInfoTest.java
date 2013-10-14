package org.societies.api.internal.privacytrust.privacy.model.dataobfuscation;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameObfuscatorInfoTest {
	private static final Logger LOG = LoggerFactory.getLogger(NameObfuscatorInfo.class.getName());

	@Test
	public void testGetObfuscationExample() {
		NameObfuscatorInfo obfuscatorInfo = new NameObfuscatorInfo();
		LOG.info(obfuscatorInfo.getObfuscationExample(0.3));
	}
}

package org.societies.integration.test.bit.remote_api_calls;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NominalTestCase {

	private static Logger LOG = LoggerFactory.getLogger(NominalTestCase.class);
	
	public NominalTestCase() {
	}

	@Before
	public void setUp() {
		LOG.info("###771... setUp");
	}

	@Test
	public void body() {
		LOG.info("###771... body");
			

	}
	
	@After
	public void tearDown() {
		LOG.info("###771... tearDown");
	}
}
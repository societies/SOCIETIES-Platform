package org.societies.integration.test.bit.user_intent_learning;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NominalTestCase {

	private static Logger LOG = LoggerFactory.getLogger(NominalTestCase.class);

	public NominalTestCase() {
	}

	@Before
	public void setUp() {
		LOG.info("###749... setUp");
	}

	@Test
	public void body1() {
		LOG.info("###749... body1");
		//Assert.assertEquals();
	}

	@Ignore
	@Test
	public void body2() {
		LOG.info("###749... body2");
	}

	@Ignore
	@Test
	public void body3() {
		LOG.info("###749... body4");
	}

	@Ignore
	@Test
	public void body4() {
		LOG.info("###749 ... body4");
	}


	@After
	public void tearDown() {
		LOG.info("###749 ... tearDown");
	}
}
package org.societies.integration.test.bit.math_example;

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
		LOG.info("###743... setUp");
	}

	@Test
	public void body1() {
		LOG.info("###743... body1");
			
		Assert.assertEquals(new Integer(0),TestCase743.mathServiceConsumer.barycenter(1, 2, 4) );

	}
	
	@Test
	public void body2() {
		LOG.info("###743... body2");
		
		Assert.assertEquals(new Integer(1),TestCase743.mathServiceConsumer.barycenter(2, 2, 2) );

	}
	
	@Test
	public void body3() {
		LOG.info("###743... body4");
		
		Assert.assertEquals(new Integer(2),TestCase743.mathServiceConsumer.barycenter(2, 2, 2) );

	}
	
	
	@Test
	public void body4() {
		LOG.info("###743... body4");
			
		Assert.assertEquals(new Integer(2), TestCase743.mathServiceConsumer.barycenter(1, 2, 2) );
	}

	
	@After
	public void tearDown() {
		LOG.info("###743... tearDown");
	}
}
package org.societies.integration.test.bit.math_example;

import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.service.api.IConsumer;

public class NominalTestCase {

	private IConsumer mathServiceConsumer;
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCase.class);
	
	public NominalTestCase(IConsumer mathServiceConsumer) throws InterruptedException, ExecutionException {
		super();
		this.mathServiceConsumer = mathServiceConsumer;
		this.setUp();
		this.body();
		this.tearDown();
	}

	private void setUp() {

	}

	private void body() throws InterruptedException, ExecutionException {
		float res ;
		res = mathServiceConsumer.barycenter(1, 2, 2) ;
		Assert.assertEquals(res, new Float(1));
		
		if (mathServiceConsumer.barycenter(1, 2, 4) == 1) {
			LOG.info("Test 743.1 PASS");
		}
		else {
			LOG.info("Test 743.1 FAIL");
		}
		if (mathServiceConsumer.barycenter(1, 3, 3) == 1) {
			LOG.info("Test 743.2 PASS");
		}
		else {
			LOG.info("Test 743.2 FAIL");
		}
	}
	
	private void tearDown() {
		
	}

}
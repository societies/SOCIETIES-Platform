package org.societies.integration.test.bit.math_example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.service.api.IConsumer;

public class NominalTestCase {

	private IConsumer mathServiceConsumer;
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCase.class);
	
	public NominalTestCase(IConsumer mathServiceConsumer) {
		super();
		this.mathServiceConsumer = mathServiceConsumer;
		this.setUp();
		this.body();
		this.tearDown();
	}

	private void setUp() {

	}

	private void body() {
		if (mathServiceConsumer.barycenter(1, 2, 3) == 1) {
			LOG.info("----- 743 PASS");
		}
		else {
			LOG.info("Test FAIL");
		}
		if (mathServiceConsumer.barycenter(1, 3, 3) == 1) {
			LOG.info("Test PASS");
		}
		else {
			LOG.info("Test FAIL");
		}
	}
	
	private void tearDown() {
		
	}

}
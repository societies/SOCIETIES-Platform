package org.societies.integration.test.bit.math_example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.service.api.IConsumer;

public class NominalTestCase {

	private IConsumer mathServiceConsumer;
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCase.class);
	
	public NominalTestCase() {
		super();

	}

	
	public IConsumer getMathServiceConsumer() {
		return mathServiceConsumer;
	}
	
	public void setMathServiceConsumer(IConsumer mathServiceConsumer) {
		this.mathServiceConsumer = mathServiceConsumer;
	}
	
	/**
	 * 
	 */
	private void startTest() {
		
		
		if (mathServiceConsumer.barycenter(1, 2, 3) == 1) 
		{
			LOG.info("Test PASS");
		}
		else
		{
			LOG.info("Test FAIL");
		}
		
		if (mathServiceConsumer.barycenter(1, 3, 3) == 1) 
		{
			LOG.info("Test PASS");
		}
		else
		{
			LOG.info("Test FAIL");
		}
	}

}
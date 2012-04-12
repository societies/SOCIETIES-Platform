package org.societies.integration.test.bit.math_example;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.service.api.IConsumer;

public class NominalTestCase {

	private IConsumer iConsumer;
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCase.class);
	
	
	
	public NominalTestCase() {
		super();

	}

	
	/**
	 * @return the iConsumer
	 */
	public IConsumer getIConsumer() {
		return iConsumer;
	}
	
	public void setIConsumer(IConsumer iConsumer) {
		this.iConsumer = iConsumer;
	}
	
	/**
	 * 
	 */
	private void startTest() {
		
		Assert.assertEquals(new Integer(1), iConsumer.barycenter(1, 2, 3));
		
		if (iConsumer.barycenter(1, 2, 3) == 1) 
		{
			LOG.info("Test PASS");
		}
		else
		{
			LOG.info("Test FAIL");
		}
		
		if (iConsumer.barycenter(1, 3, 3) == 1) 
		{
			LOG.info("Test PASS");
		}
		else
		{
			LOG.info("Test FAIL");
		}
	}

}
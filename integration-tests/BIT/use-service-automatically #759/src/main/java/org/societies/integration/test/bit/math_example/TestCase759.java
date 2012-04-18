/**
 * 
 */
package org.societies.integration.test.bit.math_example;

/**
 * @author Bruno
 *
 */
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.service.api.IConsumer;

public class TestCase759 {

	private IConsumer mathServiceConsumer;
	private static Logger LOG = LoggerFactory.getLogger(TestCase759.class);
	private NominalTestCase nominalTestCase ;
		
	public TestCase759() {
		super();
	}
	
	public void setMathServiceConsumer(IConsumer mathServiceConsumer) {
		this.mathServiceConsumer = mathServiceConsumer;
	}
	
	private void startTest() throws InterruptedException, ExecutionException {
			nominalTestCase = new NominalTestCase(mathServiceConsumer);
	}


}
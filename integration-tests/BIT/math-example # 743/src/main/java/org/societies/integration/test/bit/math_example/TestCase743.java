/**
 * 
 */
package org.societies.integration.test.bit.math_example;

/**
 * @author Bruno
 *
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.service.api.IConsumer;

public class TestCase743 {

	private IConsumer mathServiceConsumer;
	private static Logger LOG = LoggerFactory.getLogger(TestCase743.class);
	private NominalTestCase nominalTestCase ;
		
	public TestCase743() {
		super();
	}
	
	public void setMathServiceConsumer(IConsumer mathServiceConsumer) {
		this.mathServiceConsumer = mathServiceConsumer;
	}
	
	private void startTest() {
		nominalTestCase = new NominalTestCase(mathServiceConsumer);		
	}


}
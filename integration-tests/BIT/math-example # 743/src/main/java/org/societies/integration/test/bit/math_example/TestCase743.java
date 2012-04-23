/**
 * 
 */
package org.societies.integration.test.bit.math_example;

/**
 * @author Rafik SAID MANSOUR
 * @email : rafik.saidmansour@trialog.com
 *
 *This test case is testing the integration of the bundles example.service.api, example.service.consumer and example.service.provider
 *So as we have dependency on the example.service.api bundle so we have to compile it before all other bundles
 */
import org.societies.integration.test.IntegrationTestCase;
import org.societies.service.api.IConsumer;

public class TestCase743 extends IntegrationTestCase{

	
	public static IConsumer mathServiceConsumer;
	
	public TestCase743() {
		//Don't forget to add the super
		//Don't forget to put all classes that contain tests in the Class table, as bellow
		super(743, new Class[]{NominalTestCase.class, SpecificTestCase.class});
	}
	
	// IConsumer injection
	public void setMathServiceConsumer(IConsumer mathServiceConsumer) {
		this.mathServiceConsumer = mathServiceConsumer;
	}
	
}
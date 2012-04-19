/**
 * 
 */
package org.societies.integration.test.bit.useservice;

/**
 * The test case 759 aims to test 3P service usage.
 * This test case select a 3P service (the Calculator service) as an example
 * and try to consume it. Installation, starting, stoping and unstallation
 * have already being tested in other test cases.
 * @author Olivier Maridat (Trialog)
 *
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.servicelifecycle.IServiceControl;
import org.societies.integration.test.IntegrationTestCase;

public class TestCase759 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase759.class);

	/**
	 * Service control manager (injected)
	 */
	public static IServiceControl serviceControl;

	/**
	 * Service discovery manager (injected)
	 */
	public static IServiceDiscovery serviceDiscovery;


	public TestCase759() {
		// Call the super constructor
		super();
		// Define the test case number, and test classes to launch
		this.testCaseNumber = 759;
		this.testCaseClasses = new Class[]{NominalTestCaseLowerTester.class};
	}


	public void setServiceControl(IServiceControl serviceControl) {
		this.serviceControl = serviceControl;
	}
	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}
}
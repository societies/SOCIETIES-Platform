/**
 * 
 */
package org.societies.integration.test.bit.useservice;

/**
 * The test case 713 aims to test 3P service usage.
 * This test case select a 3P service (the Calculator service) as an example
 * and try to consume it. Installation, starting, stoping and unstallation
 * have already being tested in other test cases.
 * @author Olivier Maridat (Trialog)
 *
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.integration.test.IntegrationTestCase;

public class TestCase713 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase713.class);

	/**
	 * Service control manager (injected)
	 */
	public static IServiceControl serviceControl;

	/**
	 * Service discovery manager (injected)
	 */
	public static IServiceDiscovery serviceDiscovery;


	public TestCase713() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		super(713, new Class[]{SpecificTestCaseUpperTester.class, NominalTestCaseLowerTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 713;
	}


	public void setServiceControl(IServiceControl serviceControl) {
		this.serviceControl = serviceControl;
	}
	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}
}
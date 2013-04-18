/**
 * 
 */
package org.societies.integration.test.bit.installservice;

/**
 * The test case 713 aims to test 3P service installation.
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
	private static IServiceControl serviceControl;

	/**
	 * Service discovery manager (injected)
	 */
	private static IServiceDiscovery serviceDiscovery;


	public TestCase713() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		//super(713, new Class[] {SpecificTestCaseUpperTester.class, NominalTestCaseLowerTester.class});
		super(1864, new Class[] {NominalTestCaseLowerTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 1864;
	}


	public void setServiceControl(IServiceControl serviceControl) {
		LOG.debug("[#1864] setServiceControl()");
		TestCase713.serviceControl = serviceControl;
	}
	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		LOG.debug("[#1864] setServiceDiscovery()");
		TestCase713.serviceDiscovery = serviceDiscovery;
	}
	
	protected static IServiceControl getServiceControl() {
		return serviceControl;
	}

	protected static IServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
	}
}

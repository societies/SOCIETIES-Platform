/**
 * 
 */
package org.societies.integration.test.bit.policynegotiate;

/**
 * The test case 713 aims to test 3P service installation.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.security.policynegotiator.INegotiation;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
import org.societies.integration.test.IntegrationTestCase;

public class TestCase713 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase713.class);

	/**
	 * Secure policy negotiator (injected)
	 */
	private static INegotiation negotiator;

	/**
	 * Security group manager for comms fw
	 */
	private static INegotiationProviderRemote groupMgr;

	public TestCase713() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		//super(713, new Class[] {SpecificTestCaseUpperTester.class, NominalTestCaseLowerTester.class});
		super(713, new Class[] {NominalTestCaseLowerTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 713;
	}

	public void setNegotiator(INegotiation negotiator) {
		LOG.debug("[#713] setNegotiator()");
		TestCase713.negotiator = negotiator;
	}
	
	protected static INegotiation getNegotiator() {
		return negotiator;
	}

	public void setGroupMgr(INegotiationProviderRemote groupMgr) {
		LOG.debug("[#713] setGroupMgr()");
		TestCase713.groupMgr = groupMgr;
	}
	
	protected static INegotiationProviderRemote getGroupMgr() {
		return groupMgr;
	}
}

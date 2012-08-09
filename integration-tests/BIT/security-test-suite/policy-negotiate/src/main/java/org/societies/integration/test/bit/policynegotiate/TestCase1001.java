/**
 * 
 */
package org.societies.integration.test.bit.policynegotiate;

/**
 * The test case 1001 aims to test 3P service installation.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.security.policynegotiator.INegotiation;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderServiceMgmt;
import org.societies.integration.test.IntegrationTestCase;

public class TestCase1001 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase1001.class);

	/**
	 * Secure policy negotiator (injected)
	 */
	private static INegotiation negotiator;

	/**
	 * Security group manager for comms fw
	 */
	private static INegotiationProviderRemote groupMgr;
	
	private static INegotiationProviderServiceMgmt negotiationProviderServiceMgmt;

	public TestCase1001() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		//super(1001, new Class[] {SpecificTestCaseUpperTester.class, NominalTestCaseLowerTester.class});
		super(1001, new Class[] {NominalTestCaseLowerTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 1001;
	}

	public void setNegotiator(INegotiation negotiator) {
		LOG.debug("[#1001] setNegotiator()");
		TestCase1001.negotiator = negotiator;
	}
	
	protected static INegotiation getNegotiator() {
		return negotiator;
	}

	public void setGroupMgr(INegotiationProviderRemote groupMgr) {
		LOG.debug("[#1001] setGroupMgr()");
		TestCase1001.groupMgr = groupMgr;
	}
	
	protected static INegotiationProviderRemote getGroupMgr() {
		return groupMgr;
	}

	public void setNegotiationProviderServiceMgmt(INegotiationProviderServiceMgmt negotiationProviderServiceMgmt) {
		LOG.debug("[#1001] setNegotiationProviderServiceMgmt()");
		TestCase1001.negotiationProviderServiceMgmt = negotiationProviderServiceMgmt;
	}
	
	protected static INegotiationProviderServiceMgmt getNegotiationProviderServiceMgmt() {
		return negotiationProviderServiceMgmt;
	}
}

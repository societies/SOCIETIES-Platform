/**
 * 
 */
package org.societies.integration.test.bit.policynegotiate.consumer;

/**
 * The test case 1001 aims to test 3P service installation.
 */
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.security.policynegotiator.INegotiation;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
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
	
	private static String providerJid;
	
	public TestCase1001() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		//super(1879, new Class[] {SpecificTestCaseUpperTester.class, NominalTestCaseLowerTester.class});
		super(1879, new Class[] {NominalTestCaseLowerTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 1001;
	}

	public void setNegotiator(INegotiation negotiator) {
		LOG.debug("[#1879] setNegotiator()");
		TestCase1001.negotiator = negotiator;
	}
	
	protected static INegotiation getNegotiator() {
		return negotiator;
	}

	public void setGroupMgr(INegotiationProviderRemote groupMgr) {
		LOG.debug("[#1879] setGroupMgr()");
		TestCase1001.groupMgr = groupMgr;
	}
	
	protected static INegotiationProviderRemote getGroupMgr() {
		return groupMgr;
	}

	public void setProviderJid(String providerJid) {
		LOG.debug("[#1879] setProviderJid({})", providerJid);
		TestCase1001.providerJid = providerJid;
	}
	
	protected static String getProviderJid() {
		return providerJid;
	}
	
	@Override
	public Result run() {
		if (providerJid == null || providerJid.isEmpty()) {
			providerJid = groupMgr.getIdMgr().getThisNetworkNode().getJid();
			LOG.warn("Property test.security.providerJid not set, defaulting to local node: {}. " +
					"If you have not run the provider part of the test on this node, then this test will fail!",
					providerJid);
		}
		return super.run();
	}
}

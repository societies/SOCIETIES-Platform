/**
 * 
 */
package org.societies.integration.test.bit.policynegotiate.provider;

/**
 * The test case 1001 aims to test policy negotiation.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderServiceMgmt;
import org.societies.integration.test.IntegrationTestCase;

public class TestCase1001 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase1001.class);

	private static INegotiationProviderServiceMgmt negotiationProviderServiceMgmt;
	private static ICommManager commMgr;

	private static String serverUrl;
	
	public TestCase1001() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		//super(1879, new Class[] {SpecificTestCaseUpperTester.class, NominalTestCaseLowerTester.class});
		super(1879, new Class[] {NominalTestCaseLowerTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 1001;
	}

	public void setNegotiationProviderServiceMgmt(INegotiationProviderServiceMgmt negotiationProviderServiceMgmt) {
		LOG.debug("[#1879] setNegotiationProviderServiceMgmt()");
		TestCase1001.negotiationProviderServiceMgmt = negotiationProviderServiceMgmt;
	}
	
	protected static INegotiationProviderServiceMgmt getNegotiationProviderServiceMgmt() {
		return negotiationProviderServiceMgmt;
	}

	/**
	 * @return the serverUrl
	 */
	protected static String getServerUrl() {
		return serverUrl;
	}

	/**
	 * @param serverUrl the serverUrl to set
	 */
	public void setServerUrl(String serverUrl) {
		LOG.debug("[#1879] setServerUrl({})", serverUrl);
		TestCase1001.serverUrl = serverUrl;
	}

	/**
	 * @return the commMgr
	 */
	protected static ICommManager getCommMgr() {
		return commMgr;
	}

	/**
	 * @param commMgr the commMgr to set
	 */
	public void setCommMgr(ICommManager commMgr) {
		LOG.debug("[#1879] setCommMgr()");
		TestCase1001.commMgr = commMgr;
	}
}

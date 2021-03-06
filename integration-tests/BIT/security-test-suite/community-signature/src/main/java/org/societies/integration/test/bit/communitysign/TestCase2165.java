/**
 * 
 */
package org.societies.integration.test.bit.communitysign;

/**
 * The test case 1001 aims to test 3P service installation.
 */
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.security.digsig.ISignatureMgr;
import org.societies.integration.test.IntegrationTestCase;

public class TestCase2165 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase2165.class);

	private static String daUrl;
	private static ISignatureMgr signatureMgr;
	private static ICommManager commManager;
	
	public TestCase2165() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		//super(2165, new Class[] {SpecificTestCaseUpperTester.class, NominalTestCaseLowerTester.class});
		super(2165, new Class[] {NominalTestCaseLowerTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 1001;
	}
	
	/**
	 * @return the daUrl
	 */
	public static String getDaUrl() {
		return daUrl;
	}

	/**
	 * @param daUrl the daUrl to set
	 */
	public void setDaUrl(String daUrl) {
		TestCase2165.daUrl = daUrl;
		LOG.debug("setDaUrl({})", daUrl);
	}

	/**
	 * @return the signatureMgr
	 */
	public static ISignatureMgr getSignatureMgr() {
		return signatureMgr;
	}

	/**
	 * @param signatureMgr the signatureMgr to set
	 */
	public void setSignatureMgr(ISignatureMgr signatureMgr) {
		TestCase2165.signatureMgr = signatureMgr;
		LOG.debug("setSignatureMgr({})", signatureMgr);
	}

	/**
	 * @param commManager the commManager to set
	 */
	public void setCommManager(ICommManager commManager) {
		TestCase2165.commManager = commManager;
		LOG.debug("setCommManager({})", commManager);
	}
	
	public static IIdentityManager getIdentityManager() {
		return commManager.getIdManager();
	}

	@Override
	public Result run() {
		return super.run();
	}
}

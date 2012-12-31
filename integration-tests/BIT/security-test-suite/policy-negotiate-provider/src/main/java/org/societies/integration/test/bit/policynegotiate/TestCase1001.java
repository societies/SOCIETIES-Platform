/**
 * 
 */
package org.societies.integration.test.bit.policynegotiate;

/**
 * The test case 1001 aims to test policy negotiation.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderServiceMgmt;
import org.societies.integration.test.IntegrationTestCase;

public class TestCase1001 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase1001.class);

	private static INegotiationProviderServiceMgmt negotiationProviderServiceMgmt;

	public TestCase1001() {
		// Call the super constructor
		// with test case number
		// and test case classes to run
		//super(1001, new Class[] {SpecificTestCaseUpperTester.class, NominalTestCaseLowerTester.class});
		super(1001, new Class[] {NominalTestCaseLowerTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 1001;
	}

	public void setNegotiationProviderServiceMgmt(INegotiationProviderServiceMgmt negotiationProviderServiceMgmt) {
		LOG.debug("[#1001] setNegotiationProviderServiceMgmt()");
		TestCase1001.negotiationProviderServiceMgmt = negotiationProviderServiceMgmt;
	}
	
	protected static INegotiationProviderServiceMgmt getNegotiationProviderServiceMgmt() {
		return negotiationProviderServiceMgmt;
	}
}

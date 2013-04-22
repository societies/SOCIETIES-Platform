/**
 *
 */
package org.societies.integration.test.bit.createcloudnode;

/**
 * @author Liam Marshall (Tssg)
 *
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.integration.test.IntegrationTestCase;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.api.internal.css.cssRegistry.ICssRegistry;



public class TestCase755 extends IntegrationTestCase {

	public static ICSSInternalManager cssLocalManager;
	public static ICssRegistry cssRegistry;

	private static Logger LOG = LoggerFactory.getLogger(TestCase755.class);
	

	public TestCase755() {		
		super(1867, new Class[]{NominalTestCaseLowerTester.class, SpecificTestCaseUpperTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 1867;
		
		if(LOG.isDebugEnabled()) LOG.debug("TestCase755 Constructor");
	}
	
	// ICSSLocalManager injection
		public void setCssLocalManager(ICSSInternalManager cssLocalManager) {
			this.cssLocalManager = cssLocalManager;
		}
		
		// ICSSLocalManager injection
		public void setcssRegistry(ICssRegistry cssRegistry) {
					this.cssRegistry = cssRegistry;
				}
}
/**
 *
 */
package org.societies.integration.test.bit.cssgetadvertisements;

/**
 * @author Liam Marshall (Tssg)
 *
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.integration.test.IntegrationTestCase;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.api.internal.css.cssRegistry.ICssRegistry;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssAdvertisementRecordDetailed;
import org.societies.api.css.directory.ICssDirectoryRemote;
import org.societies.css.mgmt.CssDirectoryRemoteClient;



public class TestCase1993 extends IntegrationTestCase {

	public static ICSSInternalManager cssLocalManager;
	public static ICssRegistry cssRegistry;
	private ICtxBroker ctxBroker;
	public static ICssDirectoryRemote cssDirectoryRemote;



	private static Logger LOG = LoggerFactory.getLogger(TestCase1993.class);


	public TestCase1993() {
		super(1993, new Class[]{NominalTestCaseLowerTester.class, SpecificTestCaseUpperTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 1993;

		if(LOG.isDebugEnabled()) LOG.debug("TestCase1993 Constructor");
	}

	// ICSSLocalManager injection
		public void setCssLocalManager(ICSSInternalManager cssLocalManager) {
			this.cssLocalManager = cssLocalManager;
		}

		// ICSSLocalManager injection
		public void setcssRegistry(ICssRegistry cssRegistry) {
					this.cssRegistry = cssRegistry;
				}

		// ICtxBroker injection
		public ICtxBroker getCtxBroker() {
			return ctxBroker;
		}

		public void setCtxBroker(ICtxBroker ctxBroker) {
			this.ctxBroker = ctxBroker;
		}
		
		public ICssDirectoryRemote getCssDirectoryRemote() {
			return cssDirectoryRemote;
		}

		public void setCssDirectoryRemote(ICssDirectoryRemote cssDirectoryRemote) {
			this.cssDirectoryRemote = cssDirectoryRemote;
		}
}
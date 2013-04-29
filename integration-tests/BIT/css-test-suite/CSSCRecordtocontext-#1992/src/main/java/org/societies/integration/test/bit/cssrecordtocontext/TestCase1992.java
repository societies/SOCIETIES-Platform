/**
 *
 */
package org.societies.integration.test.bit.cssrecordtocontext;

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
import org.societies.api.comm.xmpp.interfaces.ICommManager;



public class TestCase1992 extends IntegrationTestCase {

	public static ICSSInternalManager cssLocalManager;
	public static ICssRegistry cssRegistry;
	private static ICtxBroker ctxBroker;
	static ICommManager commManager;

	

	private static Logger LOG = LoggerFactory.getLogger(TestCase1992.class);
	

	public TestCase1992() {		
		super(1992, new Class[]{NominalTestCaseLowerTester.class, SpecificTestCaseUpperTester.class});
		NominalTestCaseLowerTester.testCaseNumber = 1992;
		
		if(LOG.isDebugEnabled()) LOG.debug("TestCase1992 Constructor");
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
		public static ICtxBroker getCtxBroker() {
			return ctxBroker;
		}

		public void setCtxBroker(ICtxBroker ctxBroker) {
			this.ctxBroker = ctxBroker;
		}
		 public ICommManager getCommManager() {
		    	return commManager;
		    }
		    public void setCommManager(ICommManager commManager) {
		    	this.commManager = commManager;
		    }
}
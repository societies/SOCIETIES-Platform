/**
 * 
 */
package org.societies.integration.test.bit.cpa;

/**
 * The test case 713 aims to test 3P service installation.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.orchestration.ICPA;
import org.societies.api.internal.orchestration.ICisDataCollector;
import org.societies.integration.test.IntegrationTestCase;
import org.societies.orchestration.CSSDataCollector.main.java.*;

public class TestCase1096 extends IntegrationTestCase {
	private static Logger LOG = LoggerFactory.getLogger(TestCase1093.class);
    public static ICisManager cisManager;
    public static ICommManager commManager;
    public static ICPA cpa;
    public static ICisDataCollector cisDataCollector;
    private CSSDataCollector cdc;
	/**
	 * Privacy Log Appender (injected)
	 */

	public TestCase1093() {
		private static Logger LOG = LoggerFactory.getLogger(Test1064.class);
		Requestor requestor = null;
		
		public Tester(){
			LOG.info("*** " + this.getClass() + " starting");
		}
		
		@Before
		public void setUp(){
				
		}
		
		
		@Test
		public void Test(){
			
			LOG.info("*** CSS Data Collector Starting ***");
			LOG.info("*** " + this.getClass() + " instantiated");
			LOG.info("*** ctxBroker service :"+Test1093.getCtxBroker());
			
			try {								
				cdc =  new CSSDataCollector();			
				INetworkNode cssNodeId = Test1064.getCommManager().getIdManager().getThisNetworkNode();
				final String cssOwnerStr = cssNodeId.getBareJid();
				IIdentity cssOwnerId = Test1064.getCommManager().getIdManager().fromJid(cssOwnerStr);
				this.requestor = new Requestor(cssOwnerId);
				LOG.info("*** requestor = " + this.requestor);
							
				// this should be set with the use of identManager
				// in current test a null targetCss creates the entity in local cm 
				IIdentity targetCss = null;
				
				CtxEntity entity = Test1064.getCtxBroker().createEntity(requestor, targetCss, CtxEntityTypes.PERSON).get();
				LOG.info("entity person created based on 3p broker "+entity);
				LOG.info("entity person created based on 3p broker entity id :"+entity.getId());	
		    	
		    	
			
		    	CtxEntityIdentifier entityID = entity.getId();
		       	
		    	LOG.info(" scope entityID "+entityID.toString());
		    	// this null is set in order to trigger remote call
		    	entityID.setOwnerId("null");
		    	LOG.info(" scope entityID "+entityID.toString());
		    
		    	LOG.info("create attribute ");
		    	CtxAttribute attribute = Test1064.getCtxBroker().createAttribute(requestor, entityID, CtxAttributeTypes.BIRTHDAY).get();
		    	LOG.info("attribute created based on 3p broker "+attribute.getId());
		    	
		    	Thread.sleep(1000);
		    	Assert.assertEquals("Create", this.cdc.getcdcLog());
		    	LOG.info("Confirming CSS Data Collection");
		    	
		    	//CtxEvent ctx = 
			    	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	
}

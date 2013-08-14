/**
 * 
 */
package org.societies.integration.test.bit.integrated_prediction;

/**
 * @author nikosk
 *
 */

import org.societies.integration.test.IntegrationTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;


public class TestCase2120 extends IntegrationTestCase{

	private static Logger LOG = LoggerFactory.getLogger(TestCase2120.class);
	
	public static ICtxBroker ctxBroker;
	public static IUserActionMonitor uam;
	public static ICAUIPrediction cauiPrediction;
	public static ICAUITaskManager cauiTaskManager;
	public static ICommManager commMgr;
	
	public TestCase2120() {
		super(2120, new Class[]{CAUICACIPrediction.class});
		LOG.info("Test 2120 started : TestCase2120() ");
		//startTest(); 
	}
		
	
	//setters
	public void setCauiTaskManager(ICAUITaskManager cauiTaskManager) {
		TestCase2120.cauiTaskManager = cauiTaskManager;
	}

	public void setCauiPrediction(ICAUIPrediction cauiPrediction){
		TestCase2120.cauiPrediction = cauiPrediction;
	}

	public void setCtxBroker(ICtxBroker ctxBroker){
		TestCase2120.ctxBroker = ctxBroker;
	}

	public void setUam(IUserActionMonitor uam){
		TestCase2120.uam = uam;
	}
	
	public void setCommMgr (ICommManager commMgr){
		TestCase2120.commMgr = commMgr;
	}
		
	
	//getters
	
	public static ICAUIPrediction getCauiPrediction(){
		return TestCase2120.cauiPrediction;
	}
	
	public static ICtxBroker getCtxBroker(){
		return TestCase2120.ctxBroker;
	}

	public static IUserActionMonitor getUam(){
		return TestCase2120.uam;
	}

	public static ICommManager  getCommMgr(){
		return TestCase2120.commMgr;
	}
	
	public static ICAUITaskManager getCauiTaskManager() {
		return TestCase2120.cauiTaskManager;
	}
}
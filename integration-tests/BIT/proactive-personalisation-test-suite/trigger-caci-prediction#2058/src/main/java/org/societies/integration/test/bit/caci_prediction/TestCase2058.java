/**
 * 
 */
package org.societies.integration.test.bit.caci_prediction;

/**
 * @author nikosk
 *
 */

import org.societies.integration.test.IntegrationTestCase;
import org.junit.runner.JUnitCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.personalisation.CACI.api.CACIDiscovery.ICACIDiscovery;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;


public class TestCase2058 extends IntegrationTestCase{

	public static ICtxBroker ctxBroker;
	public static IUserActionMonitor uam;
	public static ICAUIPrediction cauiPrediction;
	public static ICACIDiscovery caciDiscovery;
	public static ICommManager commMgr;
	public static ICisManager cisManager;
	public static org.societies.api.internal.context.broker.ICtxBroker internalCtxBroker;
	
	public TestCase2058() {
		super(2058, new Class[]{Tester.class});
		System.out.println("Test 2058 started : TestCase2058() ");
	
	}
	
	//setters
	public void setCisManager(ICisManager cisManager) {
		TestCase2058.cisManager = cisManager;
	}

	public void setCauiPrediction(ICAUIPrediction cauiPrediction){
		TestCase2058.cauiPrediction = cauiPrediction;
	}

	public void setCaciDiscovery(ICACIDiscovery caciDiscovery){
		TestCase2058.caciDiscovery = caciDiscovery;
	}
	
	public void setCtxBroker(ICtxBroker ctxBroker){
		TestCase2058.ctxBroker = ctxBroker;
	}

	public void setUam(IUserActionMonitor uam){
		TestCase2058.uam = uam;
	}
	
	public void setCommMgr (ICommManager commMgr){
		TestCase2058.commMgr = commMgr;
	}
		
	
	//getters
	public static ICAUIPrediction getCauiPrediction(){
		return TestCase2058.cauiPrediction;
	}
	
	public static ICtxBroker getCtxBroker(){
		return TestCase2058.ctxBroker;
	}

	public static IUserActionMonitor getUam(){
		return TestCase2058.uam;
	}

	public static ICommManager  getCommMgr(){
		return TestCase2058.commMgr;
	}
	
	public static ICisManager getCisManager() {
		return cisManager;
	}
	
	public static ICACIDiscovery getCaciDiscovery(){
		return caciDiscovery;
	}
	
	
	public static org.societies.api.internal.context.broker.ICtxBroker getInternalCtxBroker() {
		return internalCtxBroker;
	}
	
	/**
	 * @param ctxBroker the ctxBroker to set
	 */
	public void setInternalCtxBroker(org.societies.api.internal.context.broker.ICtxBroker internalCtxBroker) {
		TestCase2058.internalCtxBroker = internalCtxBroker;
	}
	
}
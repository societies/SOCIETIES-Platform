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
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;


public class TestCase2058 extends IntegrationTestCase{

	private static Logger LOG = LoggerFactory.getLogger(TestCase2058.class);
	private String results = new String();


	private JUnitCore jUnitCore;

	public static ICtxBroker ctxBroker;
	public static IUserActionMonitor uam;
	public static ICAUIPrediction cauiPrediction;
	public static ICommManager commMgr;
	public static ICisManager cisManager;
	
	public TestCase2058() {
		super(2058, new Class[]{Tester.class});
		System.out.println("Test 2058 started : TestCase2058() ");
		//startTest(); 
	}
	
	
	

	public void setCisManager(ICisManager cisManager) {
		TestCase2058.cisManager = cisManager;
	}

	
	
	//setters
	public void setCauiPrediction(ICAUIPrediction cauiPrediction){
		TestCase2058.cauiPrediction = cauiPrediction;
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
	
	/*	
	private void startTest() {
		LOG.info("###1109... startTest");
		jUnitCore = new JUnitCore();
		Result res = jUnitCore.run(RetrieveLearnedModelTest.class);


		String testClass = "Class: ";
		String testFailCt = "Failure Count: ";
		String testFalures = "Failures: ";
		String testRunCt = "Runs: ";
		String testRunTm = "Run Time: ";
		String testSuccess = "Success: ";
		String newln = "\n";
		results += testClass + RetrieveLearnedModelTest.class.getName() + newln;
		results += testFailCt + res.getFailureCount() + newln;
		results += testFalures + newln;
		List<Failure> failures = res.getFailures();
		int i = 0;
		for (Failure x: failures)
		{
			i++;
			results += i +": " + x + newln;
		}
		results += testRunCt + res.getRunCount() + newln;
		results += testRunTm + res.getRunTime() + newln;
		results += testSuccess + res.wasSuccessful() + newln;

		LOG.info("###1109 " + results);
	}
	*/ 
}
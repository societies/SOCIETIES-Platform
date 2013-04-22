/**
 * 
 */
package org.societies.integration.test.bit.caui_prediction;

/**
 * @author nikosk
 *
 */

import org.societies.integration.test.IntegrationTestCase;
import org.junit.runner.JUnitCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;


public class TestCase1109 extends IntegrationTestCase{

	private static Logger LOG = LoggerFactory.getLogger(TestCase1109.class);
	private String results = new String();


	private JUnitCore jUnitCore;

	public static ICtxBroker ctxBroker;
	public static IUserActionMonitor uam;
	public static ICAUIPrediction cauiPrediction;
	public static ICommManager commMgr;
	
	public TestCase1109() {
		super(1876, new Class[]{Tester.class});
		System.out.println("Test 1876 started : TestCase1109() ");
		//startTest(); 
	}
	
	//setters
	public void setCauiPrediction(ICAUIPrediction cauiPrediction){
		TestCase1109.cauiPrediction = cauiPrediction;
	}

	public void setCtxBroker(ICtxBroker ctxBroker){
		TestCase1109.ctxBroker = ctxBroker;
	}

	public void setUam(IUserActionMonitor uam){
		TestCase1109.uam = uam;
	}
	
	public void setCommMgr (ICommManager commMgr){
		TestCase1109.commMgr = commMgr;
	}
		
	
	//getters
	
	public static ICAUIPrediction getCauiPrediction(){
		return TestCase1109.cauiPrediction;
	}
	
	public static ICtxBroker getCtxBroker(){
		return TestCase1109.ctxBroker;
	}

	public static IUserActionMonitor getUam(){
		return TestCase1109.uam;
	}

	public static ICommManager  getCommMgr(){
		return TestCase1109.commMgr;
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
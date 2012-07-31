/**
 * 
 */
package org.societies.integration.test.bit.user_intent_learning;

/**
 * @author nikosk
 *
 */
import java.util.List;

import org.societies.integration.test.IntegrationTestCase;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.useragent.monitoring.IUserActionMonitor;



public class TestCase749 extends IntegrationTestCase{

	private static Logger LOG = LoggerFactory.getLogger(TestCase749.class);
	private String results = new String();


	private JUnitCore jUnitCore;

	public static ICtxBroker ctxBroker;
	public static IUserActionMonitor uam;
	public static ICommManager commsMgr;
	
	public TestCase749() {
		super(749, new Class[]{Tester.class});
		System.out.println("Test 749 started : TestCase749() ");
	}


	public void setCtxBroker(ICtxBroker ctxBroker){
		TestCase749.ctxBroker = ctxBroker;
	}

	public void setUam(IUserActionMonitor uam){
		TestCase749.uam = uam;
	}
	
	public void setCommsMgr(ICommManager commsMgr){
		this.commsMgr = commsMgr;
	}
	
	protected static ICtxBroker getCtxBroker(){
		return TestCase749.ctxBroker;
	}

	protected static IUserActionMonitor getUam(){
		return TestCase749.uam;
	}

	/*
	private void startTest() {
		LOG.info("###749... startTest");
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

		LOG.info("###749 " + results);
	}
	 */
}
/**
 * 
 */
package org.societies.integration.test.bit.user_intent_learning;

/**
 * @author nikosk
 *
 */
import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestCase749 {

	private static Logger LOG = LoggerFactory.getLogger(TestCase749.class);
	private String results = new String();


	private JUnitCore jUnitCore;
	
	@Autowired(required=true)
	private ICtxBroker internalCtxBroker;

		
	public TestCase749() {
		System.out.println(internalCtxBroker.toString());
	}
	/*
	public void setMathServiceConsumer(IConsumer mathServiceConsumer) {
		this.mathServiceConsumer = mathServiceConsumer;
	}
	 */
	private void startTest() {
		LOG.info("###749... startTest");
		jUnitCore = new JUnitCore();
		Result res = jUnitCore.run(NominalTestCase.class);


		String testClass = "Class: ";
		String testFailCt = "Failure Count: ";
		String testFalures = "Failures: ";
		String testRunCt = "Runs: ";
		String testRunTm = "Run Time: ";
		String testSuccess = "Success: ";
		String newln = "\n";
		results += testClass + NominalTestCase.class.getName() + newln;
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
}
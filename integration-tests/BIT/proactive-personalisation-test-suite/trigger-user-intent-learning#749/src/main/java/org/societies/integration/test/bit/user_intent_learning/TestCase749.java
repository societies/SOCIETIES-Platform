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
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.IPersonalisationManager;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestCase749 extends IntegrationTestCase{

	private static Logger LOG = LoggerFactory.getLogger(TestCase749.class);
	private String results = new String();


	private JUnitCore jUnitCore;
	
	@Autowired(required=true)
	public ICtxBroker internalCtxBroker;

	@Autowired(required=true)
	public IPersonalisationManager persManager;
	
	@Autowired(required=true)
	public IUserActionMonitor uamMonitor;
	
	public TestCase749() {
		super(749, new Class[]{NominalTestCase.class, SpecificTestCase.class});
		
		UserIntentLearningTest uiTest = new UserIntentLearningTest(internalCtxBroker,persManager,uamMonitor);
	}
	
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
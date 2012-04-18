/**
 * 
 */
package org.societies.integration.test.bit.math_example;

/**
 * @author Bruno
 *
 */
import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.service.api.IConsumer;

public class TestCase743 {

	public static IConsumer mathServiceConsumer;
	private static Logger LOG = LoggerFactory.getLogger(TestCase743.class);
	private String results = new String();

	
	private JUnitCore jUnitCore;
		
	public TestCase743() {
	}
	
	public void setMathServiceConsumer(IConsumer mathServiceConsumer) {
		this.mathServiceConsumer = mathServiceConsumer;
	}
	
	private void startTest() {
		LOG.info("###743... startTest");
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

		LOG.info("###743 " + results);
	}
}
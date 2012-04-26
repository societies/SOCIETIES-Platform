/**
 * 
 */
package org.societies.orchestration.integration.test.bit.icotestsuite.createcis;

/**
 * @author <a href="mailto:frb4@hw.ac.uk">Fraser Blackmun</a> (HWU)
 *
 */
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.orchestration.api.ISuggestedCommunityAnalyser;
import org.societies.orchestration.EgocentricCommunityAnalyser.EgocentricCommunityAnalyser;

import org.societies.integration.test.IntegrationTestCase;


public class TestCaseXYZ extends IntegrationTestCase {

	private static ISuggestedCommunityAnalyser suggestedCommunityAnalyser;
	private static EgocentricCommunityAnalyser egocentricCommunityAnalyser;
	//private static ContextStateModelsAnalyser contextStateModelsAnalyser;
	//private static CollaborationPatternAnalyser collaborationPatternAnalyser;
	
	private static Logger LOG = LoggerFactory.getLogger(TestCase714.class);
	private String results = new String();

	
	private JUnitCore jUnitCore;
		
	public TestCaseXYZ() {
		if(LOG.isDebugEnabled()) LOG.debug("TestCaseXYZ Constructor");
		super(5555, new Class[]{CreateTemporaryLocationBasedCis.class});
	}
	
	public SuggestedCommunityAnalyser getSuggestedCommunityAnalyser() {
		return suggestedCommunityAnalyser;
	}

	public void setSuggestedCommunityAnalyser(SuggestedCommunityAnalyser suggestedCommunityAnalyser) {
		suggestedCommunityAnalyser = suggestedCommunityAnalyser;
	}
	
	public EgocentricCommunityAnalyser getEgocentricCommunityAnalyser() {
		return egocentricCommunityAnalyser;
	}

	public void setEgocentricCommunityAnalyser(EgocentricCommunityAnalyser egocentricCommunityAnalyser) {
		egocentricCommunityAnalyser = egocentricCommunityAnalyser;
	}
	
	public ContextStateModelsAnalyser getContextStateModelsAnalyser() {
		return contextStateModelsAnalyser;
	}

	public void setContextStateModelsAnalyser(ContextStateModelsAnalyser contextStateModelsAnalyser) {
		contextStateModelsAnalyser = contextStateModelsAnalyser;
	}
	
	public CollaborationPatternAnalyser getCollaborationPatternAnalyser() {
		return CollaborationPatternAnalyser;
	}

	public void setCollaborationPatternAnalyser(CollaborationPatternAnalyser collaborationPatternAnalyser) {
		collaborationPatternAnalyser  = collaborationPatternAnalyser ;
	}

	
	
	private void startTest() {
		if(LOG.isDebugEnabled()) LOG.debug("###714... startTest");
		
		jUnitCore = new JUnitCore();
		Result res = jUnitCore.run(NominalCase.class);
		
		
		String testClass = "Class: ";
        String testFailCt = "Failure Count: ";
        String testFailures = "Failures: ";
        String testRunCt = "Runs: ";
        String testRunTm = "Run Time: ";
        String testSuccess = "Success: ";
        String newln = "\n ";
        results += testClass + NominalCase.class.getName() + newln;
        results += testFailCt + res.getFailureCount() + newln;
        results += testFailures + newln;
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

		LOG.info("###714 " + results);
	}
}
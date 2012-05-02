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

static public class CreateTemporaryLocationBasedCis {
		
		private ISuggestedCommunityAnalyser scaTest;

		public CreateTemporaryLocationBasedCis(){
			
		}
		
		@Before
		public void setUp() {
			if(LOG.isDebugEnabled()) LOG.debug("###XYZ Tests... setUp");
			serviceUnderTest = null;
		}

		@Test
		public void testBody() {
			if(LOG.isDebugEnabled()) LOG.debug("###XYZ... testBody");
			
			try {
				
			} catch(Exception ex){
				LOG.error("Error while running test: " + ex);
				ex.printStackTrace();
				fail("Exception occured");
			}
		}
		
		@After
		public void tearDown() {
			if(LOG.isDebugEnabled()) LOG.debug("###XYZ... tearDown");
		}
		
	}
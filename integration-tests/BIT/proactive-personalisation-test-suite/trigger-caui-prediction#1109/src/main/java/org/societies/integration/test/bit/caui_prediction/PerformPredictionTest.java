package org.societies.integration.test.bit.caui_prediction;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

public class PerformPredictionTest {

	
private static Logger LOG = LoggerFactory.getLogger(TestCase1109.class);
	
	public void setUp(){

	}

	@Test
	public void test() {

		IIdentity identity = new MockIdentity(IdentityType.CSS, "user", "societies.org");

		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		try {
			serviceId.setIdentifier(new URI("css://eliza@societies.org/HelloEarth"));
			serviceId.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}


	IAction action = new Action(serviceId, "testServiceType", "volume", "high");
	LOG.info("CAUI PREDICTION SERVICE SET:"+ TestCase1109.cauiPrediction );
	LOG.info("CAUI PREDICTION perform prediction :"+ TestCase1109.cauiPrediction.getPrediction(identity, action));
	
	}

}

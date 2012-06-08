package org.societies.integration.test.bit.caui_prediction;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;

public class PerformPredictionTest {

	private static Logger LOG = LoggerFactory.getLogger(TestCase1109.class);

	public void setUp(){

	}

	@Test
	public void TestPerformPrediction() {

		IIdentity identity = new MockIdentity(IdentityType.CSS, "user", "societies.org");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		try {
			serviceId.setIdentifier(new URI("css://nikosk@societies.org/radioService"));
			serviceId.setServiceInstanceIdentifier("css://nikosk@societies.org/radioService");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		// this action simulates an action performed by the user 
		IAction actionRadio1 = new Action(serviceId, "serviceType1", "setRadio", "on");

		//LOG.info("CAUI PREDICTION SERVICE SET:"+ TestCase1109.cauiPrediction );
		try {
			List<IUserIntentAction> actionList = TestCase1109.cauiPrediction.getPrediction(identity, actionRadio1).get();
			if(actionList.size()>0){
				IUserIntentAction predictedAction = actionList.get(0);
				String parName = predictedAction.getparameterName();
				String value = predictedAction.getvalue();
				//"setVolume", "medium"
				LOG.info("CAUI PREDICTION perform prediction :"+ predictedAction);
				Assert.assertEquals("setVolume", parName);
				Assert.assertEquals("medium", value);

				HashMap<String, Serializable> context = predictedAction.getActionContext();

				for(String ctxType : context.keySet()){
					Serializable ctxValue = context.get(ctxType);
					//LOG.info("context value :"+ ctxValue);
					if(ctxType.equals(CtxAttributeTypes.LOCATION_SYMBOLIC)&& ctxValue instanceof String){
						String location = (String) ctxValue;
						LOG.info("String context location value :"+ location);
						Assert.assertEquals("Home-Parking", location);
					} else if(ctxType.equals(CtxAttributeTypes.TEMPERATURE) && ctxValue instanceof Integer ){
						Integer temperature= (Integer) ctxValue;
						LOG.info("String context temperature value :"+ temperature);
						Assert.assertEquals(30, temperature);
					} else if(ctxType.equals(CtxAttributeTypes.STATUS) && ctxValue instanceof String ){
						String status = (String) ctxValue;
						LOG.info("String context status value :"+ status);
						Assert.assertEquals("driving", status);
					}
				}
				LOG.info("CAUI PREDICTION perform prediction :"+ predictedAction);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
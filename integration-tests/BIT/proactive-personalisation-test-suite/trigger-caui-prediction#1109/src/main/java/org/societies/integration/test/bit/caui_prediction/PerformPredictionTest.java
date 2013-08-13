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
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;

public class PerformPredictionTest {

	private static Logger LOG = LoggerFactory.getLogger(TestCase1109.class);
	private IIdentity cssOwnerId;


	public void setUp(){

	}

	@Test
	public void TestPerformPrediction() {
		
		System.out.println("Test 1876 started : TestPerformPrediction");
		//LOG.info("TestPerformPrediction : Initiate prediction test ");
		try {
			try {
				LOG.info("TestPerformPrediction : waiting 10000 for model creation ");
				Thread.sleep(9000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			//IIdentity identity = new MockIdentity(IdentityType.CSS, "user", "societies.org");
			final INetworkNode cssNodeId = TestCase1109.commMgr.getIdManager().getThisNetworkNode();
			
			final String cssOwnerStr = cssNodeId.getBareJid();
			this.cssOwnerId = TestCase1109.commMgr.getIdManager().fromJid(cssOwnerStr);

			ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();

			serviceId.setIdentifier(new URI("xc.societies.local/NEC_Cobrowse_Service"));
			serviceId.setServiceInstanceIdentifier("cobrowse-webapp");

			// this action simulates an action performed by the user 
			IAction actionRadio1 = new Action(serviceId, "serviceType1", "setRadio", "on");

			//LOG.info("CAUI PREDICTION SERVICE SET:"+ TestCase1109.cauiPrediction );
			
			//IndividualCtxEntity operator = TestCase1109.getCtxBroker().retrieveIndividualEntity(this.cssOwnerId).get();
			CtxAttribute locationAttr = lookupRetrieveAttrHelp(CtxAttributeTypes.TEMPERATURE);
			LOG.info("temperature value type "+ locationAttr.getValueType());
			LOG.info("temperature integer value "+ locationAttr.getIntegerValue());
			
			
			LOG.info("A action performed :  "+  actionRadio1 );

			List<IUserIntentAction> actionList = TestCase1109.cauiPrediction.getPrediction(this.cssOwnerId, actionRadio1).get();
			LOG.info("B actions predicted :  "+  actionList );
			
			if(actionList.size()>0){
				IUserIntentAction predictedAction = actionList.get(0);
				String parName = predictedAction.getparameterName();
				String value = predictedAction.getvalue();
				//"setVolume", "medium"
				LOG.info("C CAUI PREDICTION perform prediction :"+ predictedAction);
				Assert.assertEquals("setVolume", parName);
				Assert.assertEquals("medium", value);
				
				HashMap<String, Serializable> context = predictedAction.getActionContext();

				for(String ctxType : context.keySet()){
					Serializable ctxValue = context.get(ctxType);
					//LOG.info("context value :"+ ctxValue);
					if(ctxType.equals(CtxAttributeTypes.LOCATION_SYMBOLIC)&& ctxValue instanceof String){
						String location = (String) ctxValue;
						LOG.info("String context location value :"+ location);
						///Assert.assertEquals("Home-Parking", location);
					} else if(ctxType.equals(CtxAttributeTypes.TEMPERATURE) && ctxValue instanceof Integer ){
						Integer temperature= (Integer) ctxValue;
						LOG.info("String context temperature value :"+ temperature);
						//Assert.assertEquals(30, temperature);
					} else if(ctxType.equals(CtxAttributeTypes.STATUS) && ctxValue instanceof String ){
						String status = (String) ctxValue;
						LOG.info("String context status value :"+ status);
						//Assert.assertEquals("driving", status);
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
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}


	protected CtxAttribute lookupRetrieveAttrHelp(String type){
		CtxAttribute ctxAttr = null;
		try {
			List<CtxIdentifier> tupleAttrList = TestCase1109.ctxBroker.lookup(CtxModelType.ATTRIBUTE,type).get();
			if(tupleAttrList.size() >0 ){
				CtxIdentifier ctxId = tupleAttrList.get(0);
				ctxAttr =  (CtxAttribute) TestCase1109.ctxBroker.retrieve(ctxId).get();	
				System.out.println("lookupRetrieveAttrHelp "+ ctxAttr);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ctxAttr;
	}




}
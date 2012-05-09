package org.societies.integration.test.bit.user_intent_learning;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.internal.context.broker.ICtxBroker;


public class RetrieveLearnedModelTest {

	private static Logger LOG = LoggerFactory.getLogger(TestCase749.class);
	
	public void setUp(){

	}

	@Test
	public void test() {
		try {
			IndividualCtxEntity operator = TestCase749.getCtxBroker().retrieveCssOperator().get();
			Set<CtxAttribute> setAttr = operator.getAttributes(CtxAttributeTypes.CAUI_MODEL);
		//	LOG.info("attributes refering to caui model "+ setAttr  );

			for(CtxAttribute attrRetr : setAttr){
					Assert.assertNotNull(setAttr);
					Assert.assertEquals(1, setAttr.size());
					Assert.assertNotNull(attrRetr.getBinaryValue());
					LOG.info("attributes refering to caui model "+attrRetr  );
					//System.out.println("Test 749 started : "+   );	
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
	}
}
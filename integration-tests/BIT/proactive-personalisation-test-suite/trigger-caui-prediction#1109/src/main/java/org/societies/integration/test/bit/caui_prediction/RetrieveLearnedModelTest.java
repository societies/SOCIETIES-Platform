package org.societies.integration.test.bit.caui_prediction;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.context.model.IndividualCtxEntity;

public class RetrieveLearnedModelTest {

	private static Logger LOG = LoggerFactory.getLogger(TestCase1109.class);

	public void setUp(){
	}

	@Test
	public void TestRetrieveModel() {
		try {
			IndividualCtxEntity operator = TestCase1109.getCtxBroker().retrieveCssOperator().get();
			Set<CtxAttribute> setAttr = operator.getAttributes(CtxAttributeTypes.CAUI_MODEL);
			
			Assert.assertNotNull(setAttr);
			Assert.assertEquals(1, setAttr.size());
			
			LOG.debug("attributes size refering to caui model "+setAttr.size());
			LOG.debug("attributes refering to caui model "+setAttr);

			for(CtxAttribute attrRetr : setAttr){
				Assert.assertNotNull(attrRetr.getBinaryValue());
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
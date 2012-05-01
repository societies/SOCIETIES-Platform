package org.societies.integration.test.bit.user_intent_learning;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;

import org.junit.Test;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.internal.context.broker.ICtxBroker;

public class RetrieveLearnedModelTest {

	
	private static ICtxBroker ctxBroker;

	public void setUp(){
		System.out.println("Test 749 started : RetrieveLearnedModelTest");
		ctxBroker = TestCase749.getCtxBroker();
		System.out.println(" ctxBroker "+ctxBroker);
	}
	
	
	@Test
	public void test() {
		try {
			IndividualCtxEntity operator = ctxBroker.retrieveCssOperator().get();
		
		Set<CtxAttribute> setAttr = operator.getAttributes(CtxAttributeTypes.CAUI_MODEL);
		
		Assert.assertNotNull(setAttr);
		Assert.assertEquals(1, setAttr.size());
	
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

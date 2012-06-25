package org.societies.integration.test.bit.user_preference_learning;


import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.integration.test.IntegrationTestCase;
import org.societies.api.internal.personalisation.*;


public class Test748 extends IntegrationTestCase{
	private static ICtxBroker ctxBroker;
	private static IUserActionMonitor uam;
	private static IPersonalisationManager personMan;
	
	public Test748(){
		super(748, new Class[]{Tester.class});
	}
	
	public static IPersonalisationManager getPersonMan() {
		return personMan;
	}

	public void setPersonMan(IPersonalisationManager personMan) {
		personMan = personMan;
	}
	
	public static ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		ctxBroker = ctxBroker;
	}

	public static IUserActionMonitor getUam() {
		return uam;
	}

	public void setUam(IUserActionMonitor uam) {
		uam = uam;
	}

}

package org.societies.integration.test.bit.user_preference_learning;


import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.integration.test.IntegrationTestCase;
import org.societies.api.internal.personalisation.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class Test748 extends IntegrationTestCase{
	private ICtxBroker ctxBroker;
	private IUserActionMonitor uam;
	private IPersonalisationManager personMan;
	
	public Test748(){
		super(748, Test748.class);
		Tester.instance=this;
		JUnitCore jUnitCore = new JUnitCore();
		jUnitCore.run(Tester.class);
	}
	
	public IPersonalisationManager getPersonMan() {
		return personMan;
	}

	public void setPersonMan(IPersonalisationManager personMan) {
		this.personMan = personMan;
	}

	public Test748(int testCaseNumber, Class[] testCaseClasses) {
		super(testCaseNumber, testCaseClasses);
		// TODO Auto-generated constructor stub
	}

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	public IUserActionMonitor getUam() {
		return uam;
	}

	public void setUam(IUserActionMonitor uam) {
		this.uam = uam;
	}

}

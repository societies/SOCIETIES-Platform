package societies.integration.test.bit.user_preference_learning;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.integration.test.IntegrationTestCase;
import org.societies.integration.test.IntegrationTestCase;

public class Test748 extends IntegrationTestCase{
	private static ICtxBroker ctxBroker;
	private static IUserActionMonitor uam;

	public Test748(int testCaseNumber, Class[] testCaseClasses) {
		super(testCaseNumber, testCaseClasses);
		// TODO Auto-generated constructor stub
	}

	public static ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public static void setCtxBroker(ICtxBroker ctxBroker) {
		Test748.ctxBroker = ctxBroker;
	}

	public static IUserActionMonitor getUam() {
		return uam;
	}

	public static void setUam(IUserActionMonitor uam) {
		Test748.uam = uam;
	}

}

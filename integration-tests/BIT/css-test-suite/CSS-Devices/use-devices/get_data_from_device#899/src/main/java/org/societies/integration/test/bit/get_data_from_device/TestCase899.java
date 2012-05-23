package org.societies.integration.test.bit.get_data_from_device;


import org.osgi.util.tracker.ServiceTracker;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.identity.IIdentityManager;
import org.societies.integration.test.IntegrationTestCase;

/**
 * @author Rafik
 * @email rafik.saidmansour@trialog.com
 *
 */


public class TestCase899 extends IntegrationTestCase {

	public static ICommManager commManager;
	public static IIdentityManager idMgr;
	private ServiceTracker serviceTracker;
	
	private UpperTester upperTester;

	public TestCase899() {
		super(711, NominalTestCase.class);
		
		upperTester = new UpperTester();
		
		this.serviceTracker = new ServiceTracker(upperTester.getBundleContext(), IDevice.class.getName(), upperTester);
		this.serviceTracker.open();
		
	}

	
	//Dependency Injection
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;

		idMgr = commManager.getIdManager();
	}
}
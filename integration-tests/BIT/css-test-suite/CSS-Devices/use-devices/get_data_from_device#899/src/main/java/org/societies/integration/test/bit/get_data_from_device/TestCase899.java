package org.societies.integration.test.bit.get_data_from_device;


import org.osgi.util.tracker.ServiceTracker;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.devicemgmt.IDevice;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.integration.test.IntegrationTestCase;

/**
 * @author Rafik
 * @email rafik.saidmansour@trialog.com
 *
 */


public class TestCase899 extends IntegrationTestCase {

	public static ICommManager commManager;
	public static IIdentityManager idMgr;
	public static INetworkNode node;
	

	public TestCase899() {
		super(899, NominalTestCase.class);	

	}

	
	//Dependency Injection
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		idMgr = commManager.getIdManager();
		node = idMgr.getThisNetworkNode();
	}
}
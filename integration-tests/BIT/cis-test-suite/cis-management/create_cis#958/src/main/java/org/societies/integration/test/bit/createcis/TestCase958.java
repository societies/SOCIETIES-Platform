package org.societies.integration.test.bit.createcis;



import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.integration.test.IntegrationTestCase;

/**
 * @author Rafik
 * @email rafik.saidmansour@trialog.com
 *
 */


public class TestCase958 extends IntegrationTestCase {

	public static ICommManager commManager;
	public static IIdentityManager idMgr;
	public static INetworkNode node;
	
	public static ICisManager cisManager;
	
	public static IPrivacyPolicyManager privacyPolicyManager;
	
	

	public TestCase958() {
		super(958, NominalTestCase.class);	

	}
	
	//Privacy Policy Manager Dependency Injection
	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) 
	{
		this.privacyPolicyManager = privacyPolicyManager;

	}
	
	
	//CIS Manager Dependency Injection
	public void setCisManager (ICisManager cisManager)
	{
		this.cisManager = cisManager;
	}

	
	//Communication framework Dependency Injection
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		idMgr = commManager.getIdManager();
		node = idMgr.getThisNetworkNode();
	}
}
/**
 * 
 */
package org.societies.integration.test.bit.remote_api_calls;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentityManager;
import org.societies.integration.test.IntegrationTestCase;

/**
 * @author Rafik
 * @email rafik.saidmansour@trialog.com
 *
 */



public class TestCase771 extends IntegrationTestCase implements ICommCallback{
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/schema/examples/calculatorbean"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.schema.examples.calculatorbean"));


	public static ICommManager commManager;
	public static IIdentityManager idMgr;
	
	private LowerTester lowerTester;

	public TestCase771() {
		super(711, NominalTestCase.class);
		
		this.lowerTester = new  LowerTester();
	}

	
	//Dependency Injection
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;

		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			commManager.register(lowerTester);
			commManager.register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		idMgr = commManager.getIdManager();
	}


	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}


	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}


	@Override
	public void receiveResult(Stanza stanza, Object payload) {

	}


	@Override
	public void receiveError(Stanza stanza, XMPPError error) {

	}


	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {

	}


	@Override
	public void receiveItems(Stanza stanza, String node, List<String> items) {

	}


	@Override
	public void receiveMessage(Stanza stanza, Object payload) {

	}
}
package org.societies.android.platform.comms.helper.test;

import java.util.Arrays;
import java.util.List;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;

import com.sun.org.apache.bcel.internal.generic.NEW;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

public class TestAndroidCommsHelper extends AndroidTestCase {
	private static final int DELAY = 7000;
	
	private final List<String> ELEMENT_NAMES = Arrays.asList("cssManagerMessageBean", "cssManagerResultBean");
    private final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/schema/cssmanagement");
    private static final List<String> CSS_PACKAGES = Arrays.asList("org.societies.api.schema.cssmanagement");


    //Modify these constants to suit local XMPP server
    
    private static final String XMPP_DOMAIN = "societies.bespoke";
    private static final String XMPP_IDENTIFIER = "alan";
    private static final String XMPP_PASSWORD = "midge";
    private static final String XMPP_BAD_IDENTIFIER = "godzilla";
    private static final String XMPP_BAD_PASSWORD = "smog";
    private static final String XMPP_NEW_IDENTIFIER = "gollum";
    private static final String XMPP_NEW_PASSWORD = "precious";
    private static final String XMPP_RESOURCE = "GalaxyNexus";
    private static final String XMPP_SUCCESSFUL_JID = XMPP_IDENTIFIER + "@" + XMPP_DOMAIN + "/" + XMPP_RESOURCE;
    private static final String XMPP_NEW_JID = XMPP_NEW_IDENTIFIER + "@" + XMPP_DOMAIN + "/" + XMPP_RESOURCE;
    private static final int XMPP_PORT = 5222;
    private static final String XMPP_DOMAIN_AUTHORITY = "danode." + XMPP_DOMAIN;

    private static final String SIMPLE_XML_MESSAGE = "<iq from='romeo@montague.net/orchard to='juliet@capulet.com/balcony'> " +
    													"<query xmlns='http://jabber.org/protocol/disco#info'/></iq>";
    
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	@MediumTest
	public void testConstructorLogin() throws Exception {
		final ClientCommunicationMgr ccm = new ClientCommunicationMgr(this.getContext(), false);
		assertNotNull(ccm);
		
		ccm.bindCommsService(new IMethodCallback() {
			
			public void returnAction(String arg0) {
				fail("Incorrect return object");
			}
			
			public void returnAction(boolean flag) {
				assertTrue(flag);
				assertTrue(ccm.unbindCommsService());
			}
		});
		Thread.sleep(DELAY);

	}
	@MediumTest
	public void testIsConnected() throws Exception {
		final ClientCommunicationMgr ccm = new ClientCommunicationMgr(this.getContext(), false);
		
		assertNotNull(ccm);
		
		ccm.bindCommsService(new IMethodCallback() {
			
			public void returnAction(String arg0) {
				fail("Incorrect return object");
			}
			
			public void returnAction(boolean flag) {
				assertTrue(flag);
				assertTrue(null != ccm);
				ccm.isConnected(new IMethodCallback() {
					
					public void returnAction(String arg0) {
						fail("Incorrect return object");
					}
					
					public void returnAction(boolean flag) {
						assertFalse(flag);
						assertTrue(ccm.unbindCommsService());
					}
				});
			}
		});
		Thread.sleep(DELAY);

	}
	
	@MediumTest
	/**
	 * Tests the minimum amount of calls required to login and logout 
	 * 
	 * @throws Exception
	 */
	public void testLogin() throws Exception {
		final ClientCommunicationMgr ccm = new ClientCommunicationMgr(this.getContext(), false);
		assertTrue(null != ccm);
		
		ccm.bindCommsService(new IMethodCallback() {
			
			public void returnAction(String arg0) {
				fail("Incorrect return object");
			}
			
			public void returnAction(boolean flag) {
				assertTrue(flag);
				ccm.isConnected(new IMethodCallback() {
					
					public void returnAction(String arg0) {
						fail("Incorrect return object");
					}
					
					public void returnAction(boolean flag) {
						assertFalse(flag);
						ccm.configureAgent(XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false, new IMethodCallback() {
							
							public void returnAction(String arg0) {
								fail("Incorrect return object");
							}
							
							public void returnAction(boolean flag) {
								assertTrue(flag);
								ccm.login(XMPP_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD, new IMethodCallback() {
									
									public void returnAction(String loginResult) {
										assertEquals(XMPP_SUCCESSFUL_JID, loginResult);
										ccm.logout(new IMethodCallback() {
											
											public void returnAction(String arg0) {
												fail("Incorrect return object");
											}
											
											public void returnAction(boolean flag) {
												assertTrue(flag);
												assertTrue(ccm.unbindCommsService());
											}
										});
									}
									
									public void returnAction(boolean arg0) {
										fail("Incorrect return object");
									}
								});
							}
						});
					}
				});

			}
		});
		Thread.sleep(DELAY);	
	}
	
	@MediumTest
	public void testRegistration() throws Exception {
		final ClientCommunicationMgr ccm = new ClientCommunicationMgr(this.getContext(), false);
		assertTrue(null != ccm);
		
		ccm.bindCommsService(new IMethodCallback() {
			
			public void returnAction(String arg0) {
				fail("Incorrect return object");
			}
			
			public void returnAction(boolean flag) {
				assertTrue(flag);
				ccm.isConnected(new IMethodCallback() {
					
					public void returnAction(String arg0) {
					}
					
					public void returnAction(boolean flag) {
						assertFalse(flag);
						ccm.register(ELEMENT_NAMES, new ICommCallback() {
							
							public void receiveResult(Stanza stanza, Object object) {
								assertTrue((Boolean) object);
								ccm.unregister(ELEMENT_NAMES, new ICommCallback() {
									
									public void receiveResult(Stanza stanza, Object object) {
										assertTrue((Boolean) object);
										assertTrue(ccm.unbindCommsService());
									}
									
									public void receiveMessage(Stanza arg0, Object arg1) {
										fail("Incorrect callback method called");
									}
									
									public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
										fail("Incorrect callback method called");
									}
									
									public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
										fail("Incorrect callback method called");
									}
									
									public void receiveError(Stanza arg0, XMPPError arg1) {
										fail("Incorrect callback method called");
									}
									
									public List<String> getXMLNamespaces() {
										return NAME_SPACES;
									}
									
									public List<String> getJavaPackages() {
										return CSS_PACKAGES;
									}
								});
							}
							
							public void receiveMessage(Stanza arg0, Object arg1) {
								fail("Incorrect callback method called");
							}
							
							public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
								fail("Incorrect callback method called");
							}
							
							public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
								fail("Incorrect callback method called");
							}
							
							public void receiveError(Stanza arg0, XMPPError arg1) {
								fail("Incorrect callback method called");
							}
							
							public List<String> getXMLNamespaces() {
								return NAME_SPACES;
							}
							
							public List<String> getJavaPackages() {
								return CSS_PACKAGES;
							}
						});
					}
				});
			}
		});
		Thread.sleep(DELAY);

	}
}

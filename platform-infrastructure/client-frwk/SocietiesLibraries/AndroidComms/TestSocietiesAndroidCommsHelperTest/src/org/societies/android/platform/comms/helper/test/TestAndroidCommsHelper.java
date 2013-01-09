package org.societies.android.platform.comms.helper.test;

import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.Message;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;

import com.sun.org.apache.bcel.internal.generic.NEW;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
/**
 * In order to run the tests contained in this class ensure that the following steps are taken:
 * 
 * 1. An Openfire XMPP server must be running
 * 2. A suitable AVD must be running
 * 3. The AVD must be configured so that the XMPP_DOMAIN value is valid and the user XMPP_IDENTIFIER exists
 * 4. The user XMPP_NEW_IDENTIFIER must be removed prior to running the tests as the destroyMainIdentity
 *   method is not currently functioning.
 *
 */

public class TestAndroidCommsHelper extends AndroidTestCase {
	private static final int DELAY = 10000;
	
	private final List<String> ELEMENT_NAMES = Arrays.asList("cssManagerMessageBean", "cssManagerResultBean");
    private final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/schema/cssmanagement");
    private static final List<String> CSS_PACKAGES = Arrays.asList("org.societies.api.schema.cssmanagement");


    //Modify these constants to suit local XMPP server
    
    private static final String XMPP_DOMAIN = "societies.bespoke";
    private static final String XMPP_DOMAIN_NODE = "danode";
    private static final String XMPP_IDENTIFIER = "alan";
    private static final String XMPP_PASSWORD = "midge";
    private static final String XMPP_BAD_IDENTIFIER = "godzilla";
    private static final String XMPP_BAD_PASSWORD = "smog";
    private static final String XMPP_NEW_IDENTIFIER = "gollum";
    private static final String XMPP_NEW_PASSWORD = "precious";
    private static final String XMPP_RESOURCE = "GalaxyNexus";
    private static final String XMPP_SUCCESSFUL_JID = XMPP_IDENTIFIER + "@" + XMPP_DOMAIN + "/" + XMPP_RESOURCE;
    private static final String XMPP_SUCCESSFUL_CLOUD_NODE = XMPP_IDENTIFIER + "." + XMPP_DOMAIN;
    private static final String XMPP_SUCCESSFUL_DA_NODE = XMPP_DOMAIN_NODE + "." + XMPP_DOMAIN;
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
	 * Test for the case where the class is used by a component which does not login (assumes that another component has logged in)
	 * @throws Exception
	 */
	public void testNoLogin() throws Exception {
		final ClientCommunicationMgr ccm = new ClientCommunicationMgr(this.getContext(), false);
		final ClientCommunicationMgr ccmOther = new ClientCommunicationMgr(this.getContext(), true);
		
		assertNotNull(ccm);
		assertNotNull(ccmOther);
		
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
						ccm.configureAgent(XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false, new IMethodCallback() {
							
							public void returnAction(String arg0) {
							}
							
							public void returnAction(boolean flag) {
								assertTrue(flag);
								ccm.login(XMPP_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD, new IMethodCallback() {
									
									public void returnAction(String loginResult) {
										
										assertEquals(XMPP_SUCCESSFUL_JID, loginResult);
										ccmOther.bindCommsService(new IMethodCallback() {
											
											public void returnAction(String arg0) {
											}
											
											public void returnAction(boolean flag) {
												try {
													
													assertEquals(XMPP_SUCCESSFUL_JID, ccmOther.getIdentity().getJid());
													IIdentityManager idManager = ccmOther.getIdManager();
													assertNotNull(idManager);
													
													assertEquals(XMPP_SUCCESSFUL_CLOUD_NODE, idManager.getCloudNode().getJid());
													assertEquals(XMPP_SUCCESSFUL_DA_NODE, idManager.getDomainAuthorityNode().getJid());
													
													ccm.logout(new IMethodCallback() {
														
														public void returnAction(String arg0) {
														}
														
														public void returnAction(boolean flag) {
															assertTrue(flag);
															assertTrue(ccm.unbindCommsService());
															assertTrue(ccmOther.unbindCommsService());
														}
													});
												} catch (Exception e) {
													e.printStackTrace();
													fail();
												}
											}
										});
									}
									
									public void returnAction(boolean loginResult) {
										fail();
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
										
										ccm.register(ELEMENT_NAMES, new ICommCallback() {
											
											public void receiveResult(Stanza stanza, Object object) {
												assertTrue((Boolean) object);
												ccm.unregister(ELEMENT_NAMES, new ICommCallback() {
													
													public void receiveResult(Stanza stanza, Object object) {
														assertTrue((Boolean) object);
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
	/**
	 * Tests getting the user identity
	 * 
	 * @throws Exception
	 */
	public void testGetIdentity() throws Exception {
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
										try {
											assertEquals(XMPP_SUCCESSFUL_JID, ccm.getIdentity().getJid());
											IIdentityManager idManager = ccm.getIdManager();
											assertNotNull(idManager);
											
											assertEquals(XMPP_SUCCESSFUL_CLOUD_NODE, idManager.getCloudNode().getJid());
											assertEquals(XMPP_SUCCESSFUL_DA_NODE, idManager.getDomainAuthorityNode().getJid());
											
										} catch (InvalidFormatException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
											fail();
										}
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
	/**
	 * Send messages (fire and forget)
	 * 
	 * @throws Exception
	 */
	public void testSendMessage() throws Exception {
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
										
										IIdentityManager idManager;
										try {
											idManager = ccm.getIdManager();
											assertNotNull(idManager);
											
											assertEquals(XMPP_SUCCESSFUL_CLOUD_NODE, idManager.getCloudNode().getJid());

											ccm.sendMessage(new Stanza(idManager.getCloudNode()), "test");
											ccm.sendMessage(new Stanza(idManager.getCloudNode()), Message.Type.normal, "test");
										} catch (InvalidFormatException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (CommunicationException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										
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
	public void testNewIdentity() throws Exception {
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
								try {
									ccm.newMainIdentity(XMPP_NEW_IDENTIFIER, XMPP_DOMAIN, XMPP_NEW_PASSWORD, new IMethodCallback() {
										
										public void returnAction(String value) {
											assertEquals(XMPP_NEW_JID, value);
											assertTrue(ccm.unbindCommsService());
										}
										
										public void returnAction(boolean arg0) {
										}
									});
								} catch (XMPPError e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									fail();
								}
							}
						});
					}
				});
			}
		});

		Thread.sleep(DELAY);	
	}
}

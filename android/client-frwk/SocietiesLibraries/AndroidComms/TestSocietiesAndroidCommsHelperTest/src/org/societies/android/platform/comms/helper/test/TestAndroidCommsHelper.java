package org.societies.android.platform.comms.helper.test;

import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.Message;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;


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
 * 5. The Societies Android Comms apk must be installed
 * 6. Ensure that Android Profiling is not being used, i.e. comment out Debug calls
 *
 */

public class TestAndroidCommsHelper extends AndroidTestCase {
	private final static String LOG_TAG = TestAndroidCommsHelper.class.getName(); 
	private static final int DELAY = 15000;
	
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
    private boolean testCompleted;
    
	protected void setUp() throws Exception {
		super.setUp();
		Log.d(LOG_TAG, "Test setup");
	}

	protected void tearDown() throws Exception {
		Log.d(LOG_TAG, "Test teardown");
		super.tearDown();
	}
	@MediumTest
	public void testConstructorLogin() throws Exception {
		this.testCompleted = false;
		final ClientCommunicationMgr ccm = new ClientCommunicationMgr(this.getContext(), false);
		assertNotNull(ccm);
		
		ccm.bindCommsService(new IMethodCallback() {
			
			public void returnAction(String arg0) {
				fail("Incorrect return object");
			}
			
			public void returnAction(boolean flag) {
				assertTrue(flag);
				assertTrue(ccm.unbindCommsService());
				TestAndroidCommsHelper.this.testCompleted = true;
			}

			@Override
			public void returnException(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		Thread.sleep(DELAY);
		assertTrue(this.testCompleted);
	}
	@MediumTest
	public void testIsConnected() throws Exception {
		this.testCompleted = false;
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
						TestAndroidCommsHelper.this.testCompleted = true;
					}

					@Override
					public void returnException(String arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			}

			@Override
			public void returnException(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		Thread.sleep(DELAY);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testBadUserLogin() throws Exception {
		this.testCompleted = false;
		final ClientCommunicationMgr ccm = new ClientCommunicationMgr(this.getContext(), false);
		assertTrue(null != ccm);
		
		ccm.bindCommsService(new IMethodCallback() {
			
			@Override
			public void returnException(String arg0) {
				fail("Incorrect return object");
			}
			
			@Override
			public void returnAction(boolean flag) {
				assertTrue(flag);
				ccm.isConnected(new IMethodCallback() {
					
					@Override
					public void returnException(String arg0) {
						fail("Incorrect return object");
					}
					
					@Override
					public void returnAction(String arg0) {
						fail("Incorrect return object");
					}
					
					@Override
					public void returnAction(boolean flag) {
						assertFalse(flag);
						ccm.configureAgent(XMPP_DOMAIN_AUTHORITY, XMPP_PORT, XMPP_RESOURCE, false, new IMethodCallback() {
							
							@Override
							public void returnException(String arg0) {
								fail("Incorrect return object");
							}
							
							@Override
							public void returnAction(String arg0) {
								fail("Incorrect return object");
							}
							
							@Override
							public void returnAction(boolean flag) {
								assertTrue(flag);
								ccm.login(XMPP_BAD_IDENTIFIER, XMPP_DOMAIN, XMPP_PASSWORD, new IMethodCallback() {
									
									@Override
									public void returnException(String exception) {
										assertNotNull(exception);
										assertTrue(ccm.unbindCommsService());
										TestAndroidCommsHelper.this.testCompleted = true;
									}
									
									@Override
									public void returnAction(String arg0) {
										fail("Incorrect return object");
									}
									
									@Override
									public void returnAction(boolean arg0) {
										fail("Incorrect return object");
									}
								});
							}
						});
					}
				});
			}
			
			@Override
			public void returnAction(String arg0) {
				fail("Incorrect return object");
			}
		});
		
		Thread.sleep(DELAY);
		assertTrue(this.testCompleted);

	}
	@MediumTest 
	/**
	 * Test for the case where the class is used by a component which does not login (assumes that another component has logged in)
	 * @throws Exception
	 */
	public void testNoLogin() throws Exception {
		this.testCompleted = false;
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
															TestAndroidCommsHelper.this.testCompleted = true;
														}

														@Override
														public void returnException(
																String arg0) {
															// TODO Auto-generated method stub
															
														}
													});
												} catch (Exception e) {
													e.printStackTrace();
													fail();
												}
											}

											@Override
											public void returnException(
													String arg0) {
												// TODO Auto-generated method stub
												
											}
										});
									}
									
									public void returnAction(boolean loginResult) {
										fail();
									}

									@Override
									public void returnException(String arg0) {
										// TODO Auto-generated method stub
										
									}
								});
							}

							@Override
							public void returnException(String arg0) {
								// TODO Auto-generated method stub
								
							}
						});

					}

					@Override
					public void returnException(String arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			}

			@Override
			public void returnException(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Thread.sleep(DELAY);
		assertTrue(this.testCompleted);
	}
//	@MediumTest 
	/**
	 * Test for the case where the class is used by a component which does not login (assumes that another component has logged in)
	 * and tries to retrieve Items
	 * @throws Exception
	 */
	public void testGetItems() throws Exception {
		this.testCompleted = false;
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
													try {
													ccm.getItems(idManager.fromJid(idManager.getDomainAuthorityNode().getJid()), null, new ICommCallback() {
														
														public void receiveResult(Stanza arg0, Object result) {
															assertTrue(result instanceof String);
															Log.d(LOG_TAG, "Received result: " + result);
															
															ccm.logout(new IMethodCallback() {
																
																public void returnAction(String arg0) {
																}
																
																public void returnAction(boolean flag) {
																	assertTrue(flag);
																	assertTrue(ccm.unbindCommsService());
																	assertTrue(ccmOther.unbindCommsService());
																	TestAndroidCommsHelper.this.testCompleted = true;
																}

																@Override
																public void returnException(
																		String arg0) {
																	// TODO Auto-generated method stub
																	
																}
															});

														}
														
														public void receiveMessage(Stanza arg0, Object message) {
															assertTrue(message instanceof String);
															Log.d(LOG_TAG, "Received message: " + message);
															ccm.logout(new IMethodCallback() {
																
																public void returnAction(String arg0) {
																}
																
																public void returnAction(boolean flag) {
																	assertTrue(flag);
																	assertTrue(ccm.unbindCommsService());
																	assertTrue(ccmOther.unbindCommsService());
																	TestAndroidCommsHelper.this.testCompleted = true;
																}

																@Override
																public void returnException(
																		String arg0) {
																	// TODO Auto-generated method stub
																	
																}
															});
														}
														
														public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
															// TODO Auto-generated method stub
															
														}
														
														public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
															// TODO Auto-generated method stub
															
														}
														
														public void receiveError(Stanza arg0, XMPPError arg1) {
															// TODO Auto-generated method stub
															
														}
														
														public List<String> getXMLNamespaces() {
															// TODO Auto-generated method stub
															return null;
														}
														
														public List<String> getJavaPackages() {
															// TODO Auto-generated method stub
															return null;
														}
													});
													} catch (CommunicationException e) {
														e.printStackTrace();
														fail();
													}
												} catch (Exception e) {
													e.printStackTrace();
													fail();
												}
											}

											@Override
											public void returnException(
													String arg0) {
												// TODO Auto-generated method stub
												
											}
										});
									}
									
									public void returnAction(boolean loginResult) {
										fail();
									}

									@Override
									public void returnException(String arg0) {
										// TODO Auto-generated method stub
										
									}
								});
							}

							@Override
							public void returnException(String arg0) {
								// TODO Auto-generated method stub
								
							}
						});

					}

					@Override
					public void returnException(String arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			}

			@Override
			public void returnException(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Thread.sleep(DELAY);
		assertTrue(this.testCompleted);
	}

	@MediumTest
	/**
	 * Tests the minimum amount of calls required to login and logout 
	 * 
	 * @throws Exception
	 */
	public void testLogin() throws Exception {
		this.testCompleted = false;
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
												TestAndroidCommsHelper.this.testCompleted = true;
											}

											@Override
											public void returnException(
													String arg0) {
												// TODO Auto-generated method stub
												
											}
										});
									}
									
									public void returnAction(boolean arg0) {
										fail("Incorrect return object");
									}

									@Override
									public void returnException(String arg0) {
										// TODO Auto-generated method stub
										
									}
								});
							}

							@Override
							public void returnException(String arg0) {
								// TODO Auto-generated method stub
								
							}
						});
					}

					@Override
					public void returnException(String arg0) {
						// TODO Auto-generated method stub
						
					}
				});

			}

			@Override
			public void returnException(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		Thread.sleep(DELAY);	
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	public void testRegistration() throws Exception {
		this.testCompleted = false;
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
										
										ccm.register(ELEMENT_NAMES, NAME_SPACES, CSS_PACKAGES, new IMethodCallback() {
											
											@Override
											public void returnAction(String arg0) {
												fail("Incorrect return object");
											}
											
											@Override
											public void returnAction(boolean result) {
												assertTrue(result);
												if (result) {
													ccm.unregister(ELEMENT_NAMES, NAME_SPACES, new IMethodCallback() {
													
													@Override
													public void returnAction(String arg0) {
														fail("Incorrect return object");
													}
													
													@Override
													public void returnAction(boolean result) {
														assertTrue(result);
														ccm.logout(new IMethodCallback() {
															
															public void returnAction(String arg0) {
																fail("Incorrect return object");
															}
															
															public void returnAction(boolean flag) {
																assertTrue(flag);
																ccm.UnRegisterCommManager(new IMethodCallback() {
																	
																	public void returnAction(String arg0) {
																		fail("Incorrect return object");
																	}
																	
																	public void returnAction(boolean result) {
																		assertTrue(result);
																		assertTrue(ccm.unbindCommsService());
																		TestAndroidCommsHelper.this.testCompleted = true;
																	}

																	@Override
																	public void returnException(
																			String arg0) {
																		// TODO Auto-generated method stub
																		
																	}
																});
															}

															@Override
															public void returnException(
																	String arg0) {
																// TODO Auto-generated method stub
																
															}
														});
													}

													@Override
													public void returnException(
															String arg0) {
														// TODO Auto-generated method stub
														
													}
												});

												}
											}

											@Override
											public void returnException(
													String arg0) {
												// TODO Auto-generated method stub
												
											}
										});
									}
									
									public void returnAction(boolean arg0) {
										fail("Incorrect return object");
									}

									@Override
									public void returnException(String arg0) {
										// TODO Auto-generated method stub
										
									}
								});
							}

							@Override
							public void returnException(String arg0) {
								// TODO Auto-generated method stub
								
							}
						});
					}

					@Override
					public void returnException(String arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			}

			@Override
			public void returnException(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		Thread.sleep(DELAY);
		assertTrue(this.testCompleted);
	}
	
	@MediumTest
	/**
	 * Tests getting the user identity
	 * 
	 * @throws Exception
	 */
	public void testGetIdentity() throws Exception {
		this.testCompleted = false;
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
												TestAndroidCommsHelper.this.testCompleted = true;
											}

											@Override
											public void returnException(
													String arg0) {
												// TODO Auto-generated method stub
												
											}
										});
									}
									
									public void returnAction(boolean arg0) {
										fail("Incorrect return object");
									}

									@Override
									public void returnException(String arg0) {
										// TODO Auto-generated method stub
										
									}
								});
							}

							@Override
							public void returnException(String arg0) {
								// TODO Auto-generated method stub
								
							}
						});
					}

					@Override
					public void returnException(String arg0) {
						// TODO Auto-generated method stub
						
					}
				});

			}

			@Override
			public void returnException(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		Thread.sleep(DELAY);	
		assertTrue(this.testCompleted);
	}
	@MediumTest
	/**
	 * Send messages (fire and forget)
	 * 
	 * @throws Exception
	 */
	public void testSendMessage() throws Exception {
		this.testCompleted = false;
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
												TestAndroidCommsHelper.this.testCompleted = true;
											}

											@Override
											public void returnException(
													String arg0) {
												// TODO Auto-generated method stub
												
											}
										});
									}
									
									public void returnAction(boolean arg0) {
										fail("Incorrect return object");
									}

									@Override
									public void returnException(String arg0) {
										// TODO Auto-generated method stub
										
									}
								});
							}

							@Override
							public void returnException(String arg0) {
								// TODO Auto-generated method stub
								
							}
						});
					}

					@Override
					public void returnException(String arg0) {
						// TODO Auto-generated method stub
						
					}
				});

			}

			@Override
			public void returnException(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		Thread.sleep(DELAY);	
		assertTrue(this.testCompleted);
	}

	@MediumTest
	public void testNewIdentity() throws Exception {
		this.testCompleted = false;
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
											TestAndroidCommsHelper.this.testCompleted = true;
										}
										
										public void returnAction(boolean arg0) {
										}

										@Override
										public void returnException(String arg0) {
											// TODO Auto-generated method stub
											
										}
									}, null);
								} catch (XMPPError e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									fail();
								}
							}

							@Override
							public void returnException(String arg0) {
								// TODO Auto-generated method stub
								
							}
						});
					}

					@Override
					public void returnException(String arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			}

			@Override
			public void returnException(String arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		Thread.sleep(DELAY);	
		assertTrue(this.testCompleted);
	}
}

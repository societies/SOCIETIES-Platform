/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.android.platform.cssmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.internal.cssmanager.CSSManagerEnums;
import org.societies.android.api.internal.cssmanager.IAndroidCSSManager;
import org.societies.android.api.pubsub.ISubscriber;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.events.IPlatformEventsCallback;
import org.societies.android.api.events.PlatformEventsHelperNotConnectedException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssEvent;
import org.societies.api.schema.cssmanagement.CssManagerMessageBean;
import org.societies.api.schema.cssmanagement.CssManagerResultBean;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;
import org.societies.api.schema.cssmanagement.MethodType;
import org.societies.identity.IdentityManagerImpl;
import org.societies.utilities.DBC.Dbc;
import org.societies.android.platform.androidutils.AndroidNotifier;
import org.societies.android.platform.androidutils.AppPreferences;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.android.platform.content.CssRecordDAO;
import org.societies.android.remote.helper.EventsHelper;
import org.societies.android.platform.androidutils.EntityRegularExpressions;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Messenger;
import android.os.Parcelable;
import android.util.Log;

/**
 * This class implements the CSSManager functionality that can be exposed by either a local and/or remote Android service.
 *
 */
public class CSSManagerServiceBase implements IAndroidCSSManager {

	//Logging tag
	private static final String LOG_TAG = CSSManagerServiceBase.class.getName();
	
	//Notification Tags
	private static final String NEW_CSS_NODE = "New CSS Node";
	private static final String OLD_CSS_NODE = "Old CSS Node";
	private static final String NODE_LOGIN = "Node Logged in";
	
	private static final String DOMAIN_AUTHORITY_SERVER_PORT = "daServerPort";
	private static final String DOMAIN_AUTHORITY_NODE = "daNode";
	private static final String LOCAL_CSS_NODE_JID_RESOURCE = "cssNodeResource";
	private static final String XMPP_SERVER_IP_ADDR = "daServerIP";
	
	private static final String ANDROID_PROFILING_NAME = "SocietiesCSSManager";

	//Pubsub packages
	private static final String PUBSUB_CLASS = "org.societies.api.schema.cssmanagement.CssEvent";
	//XMPP Communication namespaces and associated entities
	private static final List<String> ELEMENT_NAMES = Arrays.asList("cssManagerMessageBean", "cssManagerResultBean");
    private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/schema/cssmanagement");
    private static final List<String> PACKAGES = Arrays.asList("org.societies.api.schema.cssmanagement");
    //default destination of communication - CSS Cloud node
    private static final String DEFAULT_DESTINATION = "xcmanager.societies.local";
    
    private IIdentity cloudNodeIdentity = null;
    private IIdentity domainNodeIdentity = null;
    private ClientCommunicationMgr ccm;

//	private Messenger inMessenger;
	private CssRecord cssRecord;
	
	private CssRecordDAO cssRecordDAO;
	
	private String cloudCommsDestination = DEFAULT_DESTINATION;
	private String domainCommsDestination = null;
	
	private Context context;
	private boolean restrictBroadcast;
	private boolean connectedToEvents;
	private Messenger eventsMessenger;
	private EventsHelper eventsHelper;
	private SocietiesClientServicesController platformServicesController;
	private SocietiesEssentialServicesController essentialServicesController;

	private HashMap<String, ISubscriber> pubsubSubscribes = new HashMap<String, ISubscriber>();
	
	/**
	 * Default Constructor
	 * 
	 * @param context
	 * @param restrictBroadcast
	 * @param ccm
	 */
	public CSSManagerServiceBase(Context context, boolean restrictBroadcast, ClientCommunicationMgr ccm){
		this.context = context;
		this.restrictBroadcast = restrictBroadcast;
		this.ccm = ccm;
		
		Log.d(LOG_TAG, "CSSManager opening database");
		this.cssRecordDAO = new CssRecordDAO(this.context);
		
		this.cssRecord = null;
		this.ccm = ccm;
		this.eventsHelper = null;
		this.platformServicesController = new SocietiesClientServicesController(this.context);
		this.essentialServicesController = new SocietiesEssentialServicesController(context);
		Log.d(LOG_TAG, "CSSManagerServiceBase constructed");
	}
	
	//Client Persistence methods
	
	/**
	 * Insert or update the database with the new version of the CSS Record
	 * 
	 * @param aRecord
	 */
	private void updateLocalCSSrecord(CssRecord aRecord) {
		if (cssRecordDAO.cssRecordExists()) {
			this.cssRecordDAO.updateCSSRecord(aRecord);			
		} else {
			this.cssRecordDAO.insertCSSRecord(aRecord);			
		}
	}

	/**
	 * Get the current CSSRecord stored locally
	 *  
	 * @return {@link AndroidCSSRecord} null if no local CSSRecord found
	 */
	private CssRecord readLocalCSSRecord() {
		CssRecord record = this.cssRecordDAO.readCSSrecord();
		return record;
	}
	//Service API that service offers

	public CssRecord changeCSSNodeStatus(String client, CssRecord record) {
		Log.d(LOG_TAG, "changeCSSNodeStatus called with client: " + client);
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This method call either results in a local persistence access or a remote
	 * action to update the local persistence cache of the CSSRecord
	 */
	public CssRecord getAndroidCSSRecord(String client) {
		Log.d(LOG_TAG, "getAndroidCSSRecord called with client: " + client);
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		
		CssRecord record = null;
		
		if (null == this.readLocalCSSRecord()) {
			record = this.synchProfile(client, record);
		} else {
			if (client != null) {
				Intent intent = new Intent(IAndroidCSSManager.GET_ANDROID_CSS_RECORD);
				
				intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, true);

				intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, (Parcelable) record);
				if (restrictBroadcast) {
					intent.setPackage(client);
				}

				CSSManagerServiceBase.this.context.sendBroadcast(intent);
			}

		}
		return null;
	}
	@Override
	public void startAppServices(final String client) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Log.d(LOG_TAG, "startAppServices called with client: " + client);

		//create default intent
		final Intent intent  = new Intent(IAndroidCSSManager.START_APP_SERVICES);
		intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, true);
		if (CSSManagerServiceBase.this.restrictBroadcast) {
			intent.setPackage(client);
		}
		intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, false);

		this.essentialServicesController.bindToServices(new IMethodCallback() {
			
			@Override
			public void returnAction(String result) {
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				if (resultFlag) {
					CSSManagerServiceBase.this.essentialServicesController.startAllServices(new IMethodCallback() {
						
						@Override
						public void returnAction(String result) {
						}
						
						@Override
						public void returnAction(boolean resultFlag) {
							if (resultFlag) {
								//Start remaining platform services. This operation will take place in parallel with the login
								//to the CSSManager on Virgo
								CSSManagerServiceBase.this.platformServicesController.bindToServices(new IMethodCallback() {
									
									@Override
									public void returnAction(String result) {
									}
									
									@Override
									public void returnAction(boolean resultFlag) {
										if (resultFlag) {
											CSSManagerServiceBase.this.platformServicesController.startAllServices(new IMethodCallback() {
												
												@Override
												public void returnAction(String result) {
												}
												
												@Override
												public void returnAction(boolean resultFlag) {
													if (resultFlag) {
														intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, true);
														CSSManagerServiceBase.this.context.sendBroadcast(intent);
													}
												}
											});
										} else {
											CSSManagerServiceBase.this.context.sendBroadcast(intent);
										}
									}
								});
							} else {
								CSSManagerServiceBase.this.context.sendBroadcast(intent);
							}
						}
					});
				} else {
					CSSManagerServiceBase.this.context.sendBroadcast(intent);
				}
			}
		});
		
	}

	@Override
	public void stopAppServices(final String client) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Log.d(LOG_TAG, "stopAppServices called with client: " + client);

		final Intent intent  = new Intent(IAndroidCSSManager.STOP_APP_SERVICES);
		intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, false);
		intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, true);
		if (CSSManagerServiceBase.this.restrictBroadcast) {
			intent.setPackage(client);
		}

		this.essentialServicesController.stopAllServices(new IMethodCallback() {
			
			@Override
			public void returnAction(String result) {
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				if (resultFlag) {
					CSSManagerServiceBase.this.essentialServicesController.unbindFromServices();										
					CSSManagerServiceBase.this.platformServicesController.stopAllServices(new IMethodCallback() {
						
						@Override
						public void returnAction(String result) {
						}
						
						@Override
						public void returnAction(boolean resultFlag) {
							if (resultFlag) {
								CSSManagerServiceBase.this.platformServicesController.unbindFromServices();
								intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, true);
							}
							CSSManagerServiceBase.this.context.sendBroadcast(intent);
						}
					});
				} else {
					CSSManagerServiceBase.this.context.sendBroadcast(intent);
				}
			}
		});
	}

	public CssRecord loginCSS(final String client, final CssRecord record) {
		Log.d(LOG_TAG, "loginCSS called with client: " + client);
		
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS record cannot be null", record != null);
		
		this.assignConnectionParameters();
		
		this.eventsHelper = new EventsHelper(this.context);
		this.eventsHelper.setUpService(new IMethodCallback() {
			
			@Override
			public void returnAction(String result) {
			}
			
			@Override
			public void returnAction(boolean resultFlag) {
				if (resultFlag) {
					try {
						CSSManagerServiceBase.this.eventsHelper.subscribeToEvent(IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT, new IPlatformEventsCallback() {
							
							@Override
							public void returnAction(int result) {
							}
							
							@Override
							public void returnAction(boolean resultFlag) {
								if (resultFlag) {
									final CssManagerMessageBean messageBean = new CssManagerMessageBean();
									//CssRecord localCssrecord = convertAndroidCSSRecord(record);
									
									record.setCssNodes(createAndroidLocalNode());
									
									messageBean.setProfile(record);
									messageBean.setMethod(MethodType.LOGIN_CSS);

									final Stanza stanza = new Stanza(cloudNodeIdentity);
									
									final ICommCallback callback = new CSSManagerCallback(client, IAndroidCSSManager.LOGIN_CSS);

									CSSManagerServiceBase.this.ccm.register(ELEMENT_NAMES, NAME_SPACES, PACKAGES, new IMethodCallback() {
										
										@Override
										public void returnAction(String result) {
										}
										
										@Override
										public void returnAction(boolean resultFlag) {
											if (resultFlag) {
											
												try {
													ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
													Log.d(LOG_TAG, "Send stanza");
												} catch (CommunicationException e) {
													Log.e(LOG_TAG, e.getMessage(), e);
												}
											}
										}
									});
								}
							}
							@Override
							public void returnException(int exception) {
							}
						});
					} catch (PlatformEventsHelperNotConnectedException e) {
						Log.e(LOG_TAG, e.getMessage(), e);
					}
				}
			}
		});
		

		return null;
	}

	public void loginXMPPServer(String client, CssRecord record) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS record cannot be null", record != null);
		Log.d(LOG_TAG, "loginXMPPServer called with client: " + client);
		
		this.loginXMPP(record.getCssIdentity(), record.getDomainServer(), record.getPassword(), client);
	}

	public CssRecord logoutCSS(final String client, final CssRecord record) {
		Log.d(LOG_TAG, "logoutCSS called with client: " + client);

		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS record cannot be null", record != null);

		Log.d(LOG_TAG, "CSSManager unregistering from Pubsub events");
		try {
			this.eventsHelper.unSubscribeFromEvent(IAndroidSocietiesEvents.CSS_MANAGER_ADD_CSS_NODE_INTENT, new IPlatformEventsCallback() {
				
				@Override
				public void returnAction(int result) {
				}
				
				@Override
				public void returnAction(boolean resultFlag) {
					if (resultFlag) {
						CSSManagerServiceBase.this.eventsHelper.tearDownService(new IMethodCallback() {
							
							@Override
							public void returnAction(String result) {
							}
							
							@Override
							public void returnAction(boolean resultFlag) {
								if (resultFlag) {
									CssManagerMessageBean messageBean = new CssManagerMessageBean();
									
									//add the local Android node information
									record.setCssNodes(createAndroidLocalNode());
									
									messageBean.setProfile(record);

									messageBean.setMethod(MethodType.LOGOUT_CSS);

									Stanza stanza = new Stanza(cloudNodeIdentity);
									
									ICommCallback callback = new CSSManagerCallback(client, IAndroidCSSManager.LOGOUT_CSS);
							        try {
										ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
										Log.d(LOG_TAG, "Send stanza");
									} catch (Exception e) {
										Log.e(this.getClass().getName(), "Error when sending message stanza", e);
							        } 
								}
							}
						});
					}
				}
				@Override
				public void returnException(int exception) {
				}
			});
		} catch (PlatformEventsHelperNotConnectedException e) {
			Log.e(this.getClass().getName(), "Error unsubscribing Pubsub event", e);
		}
		
		return null;
	}

	/**
	 * This method unbind the the CSSManager from the Android Comms service on the assumption
	 * that the login to the XMPP server binded to the Android Comms service.
	 */
	public void logoutXMPPServer(final String client) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Log.d(LOG_TAG, "logoutXMPPServer called with client: " + client);

		this.ccm.logout(new IMethodCallback() {
			
			public void returnAction(String result) {
			}
			
			public void returnAction(boolean resultFlag) {
				if (resultFlag) {
					
					CSSManagerServiceBase.this.ccm.unregister(CSSManagerServiceBase.ELEMENT_NAMES, CSSManagerServiceBase.NAME_SPACES, new IMethodCallback() {
						
						@Override
						public void returnAction(String result) {
						}
						
						@Override
						public void returnAction(boolean resultFlag) {
							if (resultFlag) {
								
								CSSManagerServiceBase.this.ccm.UnRegisterCommManager(new IMethodCallback() {
									
									public void returnAction(String result) {
									}
									
									public void returnAction(boolean resultFlag) {
										if (CSSManagerServiceBase.this.ccm.unbindCommsService()) {
											
											Intent intent = new Intent(IAndroidCSSManager.LOGOUT_XMPP_SERVER);

											if (resultFlag) {
												Log.d(LOG_TAG, "CSSManager service successfully unbound from Android Comms");
												intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, true);
											} else {
												Log.d(LOG_TAG, "CSSManager service unsuccessfully unbound from Android Comms");
												intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false);
											}
											
											CssRecord aRecord = new CssRecord();
											
											intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, (Parcelable) aRecord);
											
											if (restrictBroadcast) {
												intent.setPackage(client);
											}

											Log.d(LOG_TAG, "DomainLogout result sent");

											CSSManagerServiceBase.this.context.sendBroadcast(intent);
										}
									}
								});
							}
						}
					});

				}
			}
		});
	}

	public CssRecord modifyAndroidCSSRecord(String client, CssRecord record) {
		Log.d(LOG_TAG, "modifyAndroidCSSRecord called with client: " + client);
		
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS record cannot be null", record != null);
		
		
		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		//CssRecord localCssrecord = convertAndroidCSSRecord(record);
		
		messageBean.setProfile(record);
		messageBean.setMethod(MethodType.MODIFY_CSS_RECORD);

		Stanza stanza = new Stanza(cloudNodeIdentity);
		
		ICommCallback callback = new CSSManagerCallback(client, IAndroidCSSManager.MODIFY_ANDROID_CSS_RECORD);

		try {
//    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 

		return null;
	}

	public CssRecord registerCSS(String client, CssRecord record) {
		Log.d(LOG_TAG, "registerCSS called with client: " + client);
		return null;
	}

	public CssRecord registerCSSDevice(String client, CssRecord record) {
		Log.d(LOG_TAG, "registerCSSDevice called with client: " + client);
		return null;
	}

	public void registerXMPPServer(String client, CssRecord record) {
		Log.d(LOG_TAG, "registerXMPPServer called with client: " + client);
		Log.d(LOG_TAG, "registering user: " + record.getCssIdentity() + " at domain: " + record.getDomainServer());
		
		this.createNewIdentity(record.getCssIdentity(), record.getDomainServer(), record.getPassword(), client);
	}

	public CssRecord setPresenceStatus(String client, CssRecord record) {
		Log.d(LOG_TAG, "setPresenceStatus called with client: " + client);
		return null;
	}

	public CssRecord synchProfile(String client, CssRecord record) {
		Log.d(LOG_TAG, "synchProfile called with client: " + client);
		
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS record cannot be null", record != null);
		
		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setProfile(record);
		messageBean.setMethod(MethodType.SYNCH_PROFILE);

		Stanza stanza = new Stanza(cloudNodeIdentity);
		
		ICommCallback callback = new CSSManagerCallback(client, IAndroidCSSManager.SYNCH_PROFILE);
		
        try {
//    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 

		return null;
	}

	public CssRecord unregisterCSS(String client, CssRecord record) {
		Log.d(LOG_TAG, "unregisterCSS called with client: " + client);
		return null;
	}

	public CssRecord unregisterCSSDevice(String client, CssRecord record) {
		Log.d(LOG_TAG, "unregisterCSSDevice called with client: " + client);
		return null;
	}

	/**
	 * Functionality not available
	 */
	public void unregisterXMPPServer(String client,	CssRecord record) {
		Log.d(LOG_TAG, "unregisterXMPPServer called with client: " + client);

		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS record cannot be null", record != null);
		
		
		Intent intent = new Intent(IAndroidCSSManager.UNREGISTER_XMPP_SERVER);
		
		intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false);

		CssRecord aRecord = new CssRecord();
		
		intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, (Parcelable) aRecord);
		
		if (restrictBroadcast) {
			intent.setPackage(client);
		}

		Log.d(LOG_TAG, "DomainUnRegistration result sent");

		CSSManagerServiceBase.this.context.sendBroadcast(intent);

	}
	
	
	public List<CssAdvertisementRecord> getCssFriends(String client) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Log.d(LOG_TAG, "getCssFriends called with client: " + client);

		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setMethod(MethodType.GET_CSS_FRIENDS);

		Stanza stanza = new Stanza(cloudNodeIdentity);
		
		ICommCallback callback = new CSSManagerCallback(client, IAndroidCSSManager.GET_CSS_FRIENDS);
		
        try {
//    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 

		return null;
	}

	public List<CssAdvertisementRecord> getSuggestedFriends(String client) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Log.d(LOG_TAG, "getSuggestedFriends called with client: " + client);

		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setMethod(MethodType.SUGGESTED_FRIENDS);

		Stanza stanza = new Stanza(cloudNodeIdentity);
		
		ICommCallback callback = new CSSManagerCallback(client, IAndroidCSSManager.SUGGESTED_FRIENDS);
		
        try {
//    		ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Send stanza");
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 

		return null;
	}

	public CssRecord readProfileRemote(String client, String cssId) {
		Dbc.require("Client parameter must have a value", null != client && client.length() > 0);
		Dbc.require("CSS Identity parameter must have a value", null != cssId && cssId.length() > 0);
		Log.d(LOG_TAG, "AndroidCSSRecord called with client: " + client);
		
		CssManagerMessageBean messageBean = new CssManagerMessageBean();
		messageBean.setMethod(MethodType.GET_CSS_RECORD);
		try {
			Stanza stanza = new Stanza(ccm.getIdManager().fromJid(cssId));
			ICommCallback callback = new CSSManagerCallback(client, IAndroidCSSManager.READ_PROFILE_REMOTE);
			
//			ccm.register(ELEMENT_NAMES, callback);
			ccm.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(LOG_TAG, "Sent stanza");
		} catch (InvalidFormatException ex) {
			Log.e(this.getClass().getName(), "Error getting record from: " + cssId, ex);
		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Error when sending message stanza", e);
        } 		
		return null;
	}

	public void sendFriendRequest(String client, String cssId) {
		Log.d(LOG_TAG, "sendFriendRequest called by client: " + client + " for: " + cssId);
		
		AsyncFriendRequests methodAsync = new AsyncFriendRequests();
		String params[] = {client, cssId, IAndroidCSSManager.SEND_FRIEND_REQUEST};
		methodAsync.execute(params);
	}

	/* @see org.societies.android.api.internal.cssmanager.IAndroidCSSManager#acceptFriendRequest(java.lang.String, java.lang.String)*/
	public void acceptFriendRequest(String client, String cssId) {
		Log.d(LOG_TAG, "shareService called by client: " + client);
		
		AsyncFriendRequests methodAsync = new AsyncFriendRequests();
		String params[] = {client, cssId, IAndroidCSSManager.ACCEPT_FRIEND_REQUEST};
		methodAsync.execute(params);
	}

	/* @see org.societies.android.api.internal.cssmanager.IAndroidCSSManager#getFriendRequests(java.lang.String)*/
	public CssAdvertisementRecord[] getFriendRequests(String client) {
		Log.d(LOG_TAG, "getFriendRequests called by client: " + client);
		
		AsyncGetFriendRequests methodAsync = new AsyncGetFriendRequests();
		String params[] = {client};
		methodAsync.execute(params);
		
		return null;
	}
	
	/**
	 * AsyncTask classes required to carry out threaded tasks. These classes should be used where it is estimated that 
	 * the task length is unknown or potentially long. While direct usage of the Communications components for remote 
	 * method invocation is an explicitly asynchronous operation, other usage is not and the use of these types of classes
	 * is encouraged. Remember, Android Not Responding (ANR) exceptions will be invoked if the main app thread is abused
	 * and the app will be closed down by Android very soon after.
	 * 
	 * Although the result of an AsyncTask can be obtained by using <AsyncTask Object>.get() it's not a good idea as 
	 * it will effectively block the parent method until the result is delivered back and so render the use if the AsyncTask
	 * class ineffective. Use Intents as an asynchronous callback mechanism.
	 */

	/**
	 * This class carries out the GetFriendRequests method call asynchronously
	 */
	private class AsyncGetFriendRequests extends AsyncTask<String, Void, String[]> {
		
		@Override
		protected String[] doInBackground(String... params) {
			Dbc.require("At least one parameter must be supplied", params.length >= 1);
			Log.d(LOG_TAG, "GetFriendRequests - doInBackground");
			String results [] = new String[1];
			results[0] = params[0];
			//MESSAGE BEAN
			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setMethod(MethodType.GET_FRIEND_REQUESTS);
			//COMMS CONFIG
			ICommCallback discoCallback = new CSSManagerCallback(params[0], IAndroidCSSManager.GET_FRIEND_REQUESTS); 
			Stanza stanza = new Stanza(cloudNodeIdentity);
	        try {
//	        	ccm.register(ELEMENT_NAMES, discoCallback);
	        	ccm.sendIQ(stanza, IQ.Type.GET, messageBean, discoCallback);
			} catch (Exception e) {
				Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
	        }
			return results;
		}

		@Override
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "DomainRegistration - onPostExecute");
	    }
	}
	
	/**
	 * This class carries out the AcceptFriendRequests method call asynchronously
	 */
	private class AsyncFriendRequests extends AsyncTask<String, Void, String[]> {
		
		@Override
		protected String[] doInBackground(String... params) {
			Dbc.require("At least one parameter must be supplied", params.length >= 1);
			Log.d(LOG_TAG, "AsyncFriendRequests - doInBackground");
			
			//PARAMETERS
			String client = params[0];
			String targetCssId = params[1];
			String method = params[2];
			//RETURN OBJECT
			String results[] = new String[1];
			results[0] = client;
			//MESSAGE BEAN
			CssManagerMessageBean messageBean = new CssManagerMessageBean();
			messageBean.setTargetCssId(targetCssId);
			if (method.equals(IAndroidCSSManager.SEND_FRIEND_REQUEST)) {
				messageBean.setMethod(MethodType.SEND_CSS_FRIEND_REQUEST_INTERNAL);
			} else {
				messageBean.setMethod(MethodType.ACCEPT_CSS_FRIEND_REQUEST_INTERNAL);
				messageBean.setRequestStatus(CssRequestStatusType.ACCEPTED);	
			}
			//COMMS CONFIG
			ICommCallback discoCallback = new CSSManagerCallback(client, method);
			Stanza stanza = new Stanza(cloudNodeIdentity);
	        try {
//	        	ccm.register(ELEMENT_NAMES, discoCallback);
	        	ccm.sendMessage(stanza, messageBean);
			} catch (Exception e) {
				Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
	        }
			return results;
		}

		@Override
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "AsyncFriendRequests - onPostExecute");
	    }
	}
	
//	/**
//	 * This class carries out the registration of a CSS (XMPP) identity for a given domain server
//	 */
//	private class DomainRegistration extends AsyncTask<String, Void, String[]> {
//		
//		@Override 
//		/**
//		 * Carry out compute task 
//		 */
//		protected String[] doInBackground(String... params) {
//			Dbc.require("Four parameters must be supplied", params.length >= 4);
//			Log.d(LOG_TAG, "DomainRegistration - doInBackground");
//			Log.d(LOG_TAG, "DomainRegistration param username: " + params[0]);
//			Log.d(LOG_TAG, "DomainRegistration param domain server: " + params[1]);
//			Log.d(LOG_TAG, "DomainRegistration param password: " + params[2]);
//			Log.d(LOG_TAG, "DomainRegistration param client: " + params[3]);
//			
//			String results [] = new String[4];
//
//			try {
//				INetworkNode networkNode = LocalCSSManagerService.this.ccm.newMainIdentity(params[0], params[1], params[2]);
//				
//				if (null != networkNode && null != networkNode.getDomain() && null != networkNode.getIdentifier()) {
//					Log.d(LOG_TAG, "domain registration successful");
//					
//					results[0]  = networkNode.getIdentifier();
//					results[1] = networkNode.getDomain();
//					results[2] = params[2];
//					results[3] = params[3];
//				}
//			} catch (XMPPError e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return results;
//		}
//		
//		@Override
//		/**
//		 * Handle the communication of the result
//		 */
//		protected void onPostExecute(String results []) {
//			Log.d(LOG_TAG, "DomainRegistration - onPostExecute");
//			
//			Intent intent = new Intent(IAndroidCSSManager.REGISTER_XMPP_SERVER);
//			
//			if (null != results[0]) {
//				intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, true);
//			} else {
//				intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false);
//			}
//
//			AndroidCSSRecord aRecord = new AndroidCSSRecord();
//			aRecord.setCssIdentity(results[0]);
//			aRecord.setDomainServer(results[1]);
//			aRecord.setPassword(results[2]);
//			
//			intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, (Parcelable) aRecord);
//			
//			intent.setPackage(results[3]);
//
//			Log.d(LOG_TAG, "DomainRegistration result sent");
//
//			LocalCSSManagerService.this.sendBroadcast(intent);
//
//	    }
//	}
//	/**
//	 * This class carries out the un-registration of a CSS (XMPP) identity for a given domain server
//	 */
//	private class DomainUnRegistration extends AsyncTask<String, Void, String[]> {
//		
//		@Override
//		/**
//		 * Carry out compute task 
//		 */
//		protected String[] doInBackground(String... params) {
//			Dbc.require("Four parameters must be supplied", params.length >= 1);
//			Log.d(LOG_TAG, "DomainUnRegistration - doInBackground");
//			Log.d(LOG_TAG, "DomainUnRegistration param client: " + params[0]);
//			
//			String results [] = new String[1];
//
//			if (LocalCSSManagerService.this.ccm.destroyMainIdentity()) {
//				Log.d(LOG_TAG, "domain unregistration successful");
//				
//				results[0] = params[0];
//			}
//			
//			return results;
//		}
//		
//		@Override
//		/**
//		 * Handle the communication of the result
//		 */
//		protected void onPostExecute(String results []) {
//			Log.d(LOG_TAG, "DomainUnRegistration - onPostExecute");
//			
//			Intent intent = new Intent(IAndroidCSSManager.UNREGISTER_XMPP_SERVER);
//			
//			if (null != results[0]) {
//				intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, true);
//			} else {
//				intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false);
//			}
//
//			AndroidCSSRecord aRecord = new AndroidCSSRecord();
//			
//			intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, (Parcelable) aRecord);
//			
//			intent.setPackage(results[0]);
//
//			Log.d(LOG_TAG, "DomainUnRegistration result sent");
//
//			LocalCSSManagerService.this.sendBroadcast(intent);
//
//	    }
//	}
//	/**
//	 * 
//	 * This class handles the logging in to a previously registered domain and identity
//	 *
//	 */
//	private class DomainLogin extends AsyncTask<String, Void, String[]> {
//		
//		@Override
//		/**
//		 * Carry out compute task 
//		 */
//		protected String[] doInBackground(String... params) {
//			Dbc.require("Four parameters must be supplied", params.length >= 4);
//			Log.d(LOG_TAG, "DomainLogin - doInBackground");
//			Log.d(LOG_TAG, "DomainLogin param username: " + params[0]);
//			Log.d(LOG_TAG, "DomainLogin param domain server: " + params[1]);
//			Log.d(LOG_TAG, "DomainLogin param password: " + params[2]);
//			Log.d(LOG_TAG, "DomainLogin param client: " + params[3]);
//			
//			String results [] = new String[4];
//
//			INetworkNode networkNode = LocalCSSManagerService.this.ccm.login(params[0], params[1], params[2]);
//			
//			if (null != networkNode && null != networkNode.getDomain() && null != networkNode.getIdentifier() && LocalCSSManagerService.this.ccm.isConnected()) {
//				Log.d(LOG_TAG, "domain login successful");
//				
//				results[0]  = networkNode.getIdentifier();
//				results[1] = networkNode.getDomain();
//				results[2] = params[2];
//				results[3] = params[3];
//			}
//			
//			return results;
//		}
//		
//		@Override
//		/**
//		 * Handle the communication of the result
//		 */
//		protected void onPostExecute(String results []) {
//			Log.d(LOG_TAG, "DomainLogin - onPostExecute");
//			
//			Intent intent = new Intent(IAndroidCSSManager.LOGIN_XMPP_SERVER);
//			
//			if (null != results[0]) {
//				intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, true);
//			} else {
//				intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false);
//			}
//
//			AndroidCSSRecord aRecord = new AndroidCSSRecord();
//			aRecord.setCssIdentity(results[0]);
//			aRecord.setDomainServer(results[1]);
//			aRecord.setPassword(results[2]);
//			
//			intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, (Parcelable) aRecord);
//			
//			intent.setPackage(results[3]);
//
//			Log.d(LOG_TAG, "DomainLogin result sent");
//
//			LocalCSSManagerService.this.sendBroadcast(intent);
//	    }
//	}

//	/**
//	 * This class handles the logging out of a previously registered domain and identity
//	 */
//	private class DomainLogout extends AsyncTask<String, Void, String[]> {
//		
//		@Override
//		/**
//		 * Carry out compute task 
//		 */
//		protected String[] doInBackground(String... params) {
//			Dbc.require("Four parameters must be supplied", params.length >= 1);
//			Log.d(LOG_TAG, "DomainLogout - doInBackground");
//			Log.d(LOG_TAG, "DomainLogout param client: " + params[0]);
//			
//			String results [] = new String[1];
//			
//			if (LocalCSSManagerService.this.ccm.logout()) {
//				Log.d(LOG_TAG, "domain logout successful");
//				LocalCSSManagerService.this.ccm.UnRegisterCommManager();
//				
//				results[0] = params[0];
//			}
//			
//			return results;
//		}
//		
//		@Override
//		/**
//		 * Handle the communication of the result
//		 */
//		protected void onPostExecute(String results []) {
//			Log.d(LOG_TAG, "DomainLogout - onPostExecute");
//			
//
//	    }
//	}

	/**
	 * Callback used with Android Comms for CSSManager
	 *
	 */
	private class CSSManagerCallback implements ICommCallback {
		String returnIntent;
		String client;
		
		public CSSManagerCallback(String client, String returnIntent) {
			this.client = client;
			this.returnIntent = returnIntent;
		}

		public List<String> getXMLNamespaces() {
			return NAME_SPACES;
		}

		public List<String> getJavaPackages() {
			return PACKAGES;
		}

		public void receiveError(Stanza arg0, XMPPError arg1) {
			Log.d(LOG_TAG, "CSSManagerCallback Callback receiveError");
		}

		public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
			Log.d(LOG_TAG, "CSSManagerCallback Callback receiveInfo");
		}

		public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
			Log.d(LOG_TAG, "CSSManagerCallback Callback receiveItems");
		}

		public void receiveMessage(Stanza arg0, Object arg1) {
			Log.d(LOG_TAG, "CSSManagerCallback Callback receiveMessage");
		}

		public void receiveResult(Stanza arg0, Object retValue) {
			Log.d(LOG_TAG, "CSSManagerCallback Callback receiveResult");
			
			if (client != null) {
				Intent intent = new Intent(returnIntent);
				
				CssManagerResultBean resultBean = (CssManagerResultBean) retValue;
				//cssAdvertisementRecords
				if (IAndroidCSSManager.SUGGESTED_FRIENDS == this.returnIntent || IAndroidCSSManager.GET_CSS_FRIENDS == this.returnIntent || IAndroidCSSManager.GET_FRIEND_REQUESTS==this.returnIntent) {
					//ACssAdvertisementRecord advertArray [] = ACssAdvertisementRecord.getArray(resultBean.getResultAdvertList());
					CssAdvertisementRecord advertArray[] = new CssAdvertisementRecord[resultBean.getResultAdvertList().size()]; 
					advertArray = resultBean.getResultAdvertList().toArray(advertArray);
					
					intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, true);
					
					intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, advertArray);
				}
				//cssRecords
				else { 
					intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, resultBean.getResult().isResultStatus());
					//AndroidCSSRecord aRecord = AndroidCSSRecord.convertCssRecord(resultBean.getResult().getProfile());
					CssRecord aRecord = resultBean.getResult().getProfile();
					intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, (Parcelable) aRecord);
					this.updateLocalPersistence(aRecord);
				}
				
				if (restrictBroadcast) {
					intent.setPackage(client);
				}
				CSSManagerServiceBase.this.context.sendBroadcast(intent);
				Log.d(LOG_TAG, "CSSManagerCallback Callback receiveResult sent return value: " + retValue);
			}
		}
		
		/**
		 * Decide which actions requires a database interaction
		 * @param record
		 */
		private void updateLocalPersistence(CssRecord record) {
			if (this.returnIntent.equals(IAndroidCSSManager.LOGIN_CSS) || 
					this.returnIntent.equals(IAndroidCSSManager.SYNCH_PROFILE) || 
					this.returnIntent.equals(IAndroidCSSManager.MODIFY_ANDROID_CSS_RECORD)) {
				CSSManagerServiceBase.this.updateLocalCSSrecord(record);
			}
		}
	}
	
	
//	/**
//	 * Unregister from already subscribed to pubsub events
//	 */
//	private void  unregisterFromPubsub() {
//		Log.d(LOG_TAG, "Starting Pubsub un-registration: " + System.currentTimeMillis());
//		
//		if (null != this.pubsubClient) {
//			UnSubscribeFromPubsub unSubPubSub = new UnSubscribeFromPubsub(); 
//			unSubPubSub.execute(this.pubsubClient);			
//		}
//	}
	
//	/**
//	 * Register for Pubsub events
//	 */
//	private void registerForPubsub() {
//		
//		Log.d(LOG_TAG, "Starting Pubsub registration: " + System.currentTimeMillis());
//		
//		this.pubsubClient = new PubsubClientAndroid(this);
//		
//            try {
//                this.pubsubClient.addSimpleClasses(classList);
//
//                Log.d(LOG_TAG, "Subscribing to pubsub");
//    	        
//    	    	SubscribeToPubsub subPubSub = new SubscribeToPubsub(); 
//    	    	subPubSub.execute(this.pubsubClient);
//	        } catch (ClassNotFoundException e) {
//	                Log.e(LOG_TAG, "ClassNotFoundException loading " + Arrays.toString(classList.toArray()), e);
//	        }
//	}
	
	/**
	 * Create a new Subscriber object for Pubsub
	 * @return Subscriber
	 */
	private ISubscriber createSubscriber() {
		ISubscriber subscriber = new ISubscriber() {
		
			public void pubsubEvent(IIdentity pubsubService, String node, String itemId, Object payload) {
				Log.d(LOG_TAG, "Received Pubsub event: " + node + " itemId: " + itemId);
				if (payload instanceof CssEvent) {
					CssEvent event = (CssEvent) payload;
					Log.d(LOG_TAG, "Received event is :" + event.getType());
					
					//Create Android Notification
					int flags [] = new int [1];
					flags[0] = Notification.FLAG_AUTO_CANCEL;

					//Create Android Notification
					int notifierflags [] = new int [1];
					notifierflags[0] = Notification.FLAG_AUTO_CANCEL;
					AndroidNotifier notifier = new AndroidNotifier(CSSManagerServiceBase.this.context, Notification.DEFAULT_SOUND, notifierflags);

					notifier.notifyMessage(event.getDescription(), event.getType(), CSSManagerServiceBase.class);
				}
			}
		};
		return subscriber;

	}

//	/**
//     * 
//     * Async task to un-register for CSSManager Pubsub events
//     *
//     */
//    private class UnSubscribeFromPubsub extends AsyncTask<PubsubClientAndroid, Void, Boolean> {
//		private boolean resultStatus = true;
//    	
//    	protected Boolean doInBackground(PubsubClientAndroid... args) {
//    		
//    		PubsubClientAndroid pubsubAndClient = args[0];	    	
//
//    		IIdentity pubsubService = null;
//    		
////    		try {
////    	    	pubsubService = IdentityManagerImpl.staticfromJid(LocalCSSManagerService.this.cloudCommsDestination);
//    			pubsubService = LocalCSSManagerService.this.cloudNodeIdentity;
////    		} catch (InvalidFormatException e) {
////    			Log.e(LOG_TAG, "Unable to obtain CSS node identity", e);
////    			this.resultStatus = false;
////    		}
//
//    		try {
//       			pubsubAndClient.subscriberUnsubscribe(pubsubService, CSSManagerEnums.ADD_CSS_NODE, LocalCSSManagerService.this.pubsubSubscribes.get(CSSManagerEnums.ADD_CSS_NODE));
//
//    			pubsubAndClient.subscriberUnsubscribe(pubsubService, CSSManagerEnums.DEPART_CSS_NODE, LocalCSSManagerService.this.pubsubSubscribes.get(CSSManagerEnums.DEPART_CSS_NODE));
//    			LocalCSSManagerService.this.pubsubSubscribes.clear();
//
//    			Log.d(LOG_TAG, "Pubsub un-subscription created");
//    			Log.d(LOG_TAG, "Finishing Pubsub un-registration: " + System.currentTimeMillis());
//
//    			
//			} catch (Exception e) {
//    			this.resultStatus = false;
//				Log.e(LOG_TAG, "Unable to unsubscribe for CSSManager events", e);
//
//			}
//    		return resultStatus;
//    	}
//    }

//    /**
//     * 
//     * Async task to register for CSSManager Pubsub events
//     * Note: The Subscriber objects used to subscribe to the relevant Pubsub nodes
//     * are required to be used when un-registering - hence the use of the Map to store them.
//     *
//     */
//    private class SubscribeToPubsub extends AsyncTask<PubsubClientAndroid, Void, Boolean> {
//		private boolean resultStatus = true;
//    	
//    	protected Boolean doInBackground(PubsubClientAndroid... args) {
//    		
//    		PubsubClientAndroid pubsubAndClient = args[0];	    	
//
//    		IIdentity pubsubService = null;
//    		
//			pubsubService = LocalCSSManagerService.this.cloudNodeIdentity;
//
//    		try {
//     			LocalCSSManagerService.this.pubsubSubscribes.put(CSSManagerEnums.ADD_CSS_NODE, LocalCSSManagerService.this.createSubscriber());
//    			pubsubAndClient.subscriberSubscribe(pubsubService, CSSManagerEnums.ADD_CSS_NODE, LocalCSSManagerService.this.pubsubSubscribes.get(CSSManagerEnums.ADD_CSS_NODE));
//    			
//       			LocalCSSManagerService.this.pubsubSubscribes.put(CSSManagerEnums.DEPART_CSS_NODE, LocalCSSManagerService.this.createSubscriber());
//    			pubsubAndClient.subscriberSubscribe(pubsubService, CSSManagerEnums.DEPART_CSS_NODE, LocalCSSManagerService.this.pubsubSubscribes.get(CSSManagerEnums.DEPART_CSS_NODE));
//    			
//    			Log.d(LOG_TAG, "Pubsub subscription created");
//    			Log.d(LOG_TAG, "Finishing Pubsub registration: " + System.currentTimeMillis());
//
//
//    			
//			} catch (Exception e) {
//    			this.resultStatus = false;
//				Log.e(LOG_TAG, "Unable to register for CSSManager events", e);
//
//			}
//    		return resultStatus;
//    	}
//    }

    /**
     * Convert AndroidCSSRecord to CssRecord. Required for Simple XML
     * @param record
     * @return {@link AndroidCSSRecord}
     */
	/*
    private CssRecord convertAndroidCSSRecord(CssRecord record) {
    	
    	CssRecord cssRecord = new CssRecord();
    	
    	cssRecord.setCssHostingLocation(record.getCssHostingLocation());
       	cssRecord.setCssIdentity(record.getCssIdentity());
       	cssRecord.setCssInactivation(record.getCssInactivation());
       	cssRecord.setCssRegistration(record.getCssRegistration());
       	cssRecord.setCssUpTime(record.getCssUpTime());
       	cssRecord.setDomainServer(record.getDomainServer());
       	cssRecord.setEmailID(record.getEmailID());
       	cssRecord.setEntity(record.getEntity());
       	cssRecord.setForeName(record.getForeName());
       	cssRecord.setHomeLocation(record.getHomeLocation());
       	cssRecord.setIdentityName(record.getIdentityName());
       	cssRecord.setImID(record.getImID());
       	cssRecord.setName(record.getName());
       	cssRecord.setPassword(record.getPassword());
       	cssRecord.setPresence(record.getPresence());
       	cssRecord.setSex(record.getSex());
       	cssRecord.setSocialURI(record.getSocialURI());
       	cssRecord.setStatus(record.getStatus());
       	for (CssNode node : record.getArchivedCSSNodes()) {
       		cssRecord.getArchiveCSSNodes().add(convertAndroidCSSNode(node));
       	}
       	for (AndroidCSSNode node : record.getCSSNodes()) {
       		cssRecord.getCssNodes().add(convertAndroidCSSNode(node));
       	}
       	
       	return cssRecord;
    }
     */
	
    /**
     * Convert AndroidCSSNode to CssNode. Required for Simple XML
     * @param node
     * @return CssNode
     */
	/*
    private CssNode convertAndroidCSSNode(AndroidCSSNode node) {
    	CssNode cssNode = new CssNode();
    	
    	cssNode.setIdentity(node.getIdentity());
    	cssNode.setStatus(node.getStatus());
    	cssNode.setType(node.getType());
    	
    	return cssNode;
    }
    */
	
    /**
     * Create the node information of the Android node logging into the CSS
     * @return List<CssNode>
     */
    private List<CssNode> createAndroidLocalNode() {
    	CssNode localNode = new CssNode();
    	List<CssNode> listNodes= new ArrayList<CssNode>();
    	
    	try {
        	localNode.setIdentity(this.ccm.getIdManager().getThisNetworkNode().getJid());
        	localNode.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
        	localNode.setType(CSSManagerEnums.nodeType.Android.ordinal());
        	
        	Log.d(LOG_TAG, "Android Node register info - id: " + localNode.getIdentity() + 
        			" status: " + CSSManagerEnums.nodeStatus.Available.name() + " type: " + CSSManagerEnums.nodeType.Android.name());
        	
        	listNodes.add(localNode);
    	} catch (InvalidFormatException i) {
    		Log.e(LOG_TAG, "ID Manager exception", i);
    	}
    	
    	return listNodes;
    }

    /**
     * Assign connection parameters (must happen after successful XMPP login)
     */
    private void assignConnectionParameters() {
		//Get the Cloud destination
    	try {
        	this.cloudCommsDestination = this.ccm.getIdManager().getCloudNode().getJid();
    		Log.d(LOG_TAG, "Cloud Node: " + this.cloudCommsDestination);

        	this.domainCommsDestination = this.ccm.getIdManager().getDomainAuthorityNode().getJid();
        	Log.d(LOG_TAG, "Domain Authority Node: " + this.domainCommsDestination);
        			
        	try {
    			this.cloudNodeIdentity = IdentityManagerImpl.staticfromJid(this.cloudCommsDestination);
    			Log.d(LOG_TAG, "Cloud node identity: " + this.cloudNodeIdentity);
    			
    			this.domainNodeIdentity = IdentityManagerImpl.staticfromJid(this.domainCommsDestination);
    			Log.d(LOG_TAG, "Domain node identity: " + this.cloudNodeIdentity);
    			
    		} catch (InvalidFormatException e) {
    			Log.e(LOG_TAG, "Unable to get CSS Node identity", e);
    			throw new RuntimeException(e);
    		}     
    	} catch (InvalidFormatException i) {
    		Log.e(LOG_TAG, "ID Manager exception", i);
    	}
    }
    
    /**
     * Configure ClientCommunicationManager and login
     * Note: The remote Android Comms service is bound to and remains bound to allow other
     * comms methods to be invoked
	 *
     * @param identity
     * @param domainAuthority
     * @param password
     * @param client
     */
    private void loginXMPP(final String identity, final String domainAuthority, final String password, final String client) {
    	//N.B. important that this flag is set to false as a this point the user has not been logged into the XMPP server
    	Dbc.invariant("ClientCommunicationManager must be a valid object", null != this.ccm);
    	this.ccm.setLoginCompleted(false);

    	this.ccm.bindCommsService(new IMethodCallback() {
			
			public void returnAction(String result) {
			}
			
			public void returnAction(boolean resultFlag) {
				if (resultFlag) {
					Log.d(LOG_TAG, "Bound to Android Comms");
					AppPreferences appPreferences = new AppPreferences(CSSManagerServiceBase.this.context);

					int xmppServerPort = appPreferences.getIntegerPrefValue(DOMAIN_AUTHORITY_SERVER_PORT);
					final String domainAuthorityNode = appPreferences.getStringPrefValue(DOMAIN_AUTHORITY_NODE);
					String nodeJIDResource = appPreferences.getStringPrefValue(LOCAL_CSS_NODE_JID_RESOURCE);
					final String xmppServerIPAddress = appPreferences.getStringPrefValue(XMPP_SERVER_IP_ADDR);
					
					CSSManagerServiceBase.this.ccm.configureAgent(domainAuthorityNode, xmppServerPort, nodeJIDResource, false, new IMethodCallback() {
						
						public void returnAction(String result) {
						}
						
						public void returnAction(boolean resultFlag) {
							if (resultFlag) {
								String host = null;

								if (EntityRegularExpressions.isValidIPv4Address(xmppServerIPAddress)) {
									host = xmppServerIPAddress;
								}
								CSSManagerServiceBase.this.ccm.login(identity, domainAuthority, password, host, new IMethodCallback() {
									
									public void returnAction(String result) {
										Log.d(LOG_TAG, "Logged in identity: " + result);
										
										CSSManagerServiceBase.this.ccm.isConnected(new IMethodCallback() {
											
											public void returnAction(String result) {
											}
											
											public void returnAction(boolean resultFlag) {
												Intent intent = new Intent(IAndroidCSSManager.LOGIN_XMPP_SERVER);

												if (resultFlag) {
													Log.d(LOG_TAG, "Login successful");
													
													intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, true);
												} else {
													intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false);
												}

												CssRecord aRecord = new CssRecord();
												aRecord.setCssIdentity(identity);
												aRecord.setDomainServer(domainAuthority);
												aRecord.setPassword(password);
												
												intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, (Parcelable) aRecord);
												
												if (restrictBroadcast) {
													intent.setPackage(client);
												}

												Log.d(LOG_TAG, "DomainLogin result sent");

												CSSManagerServiceBase.this.context.sendBroadcast(intent);

											}
										});								
									}
									
									public void returnAction(boolean resultFlag) {
									}
								});
							}
						}
					});
				} else {
					Log.d(LOG_TAG, "Failed to bind to Android Comms");
				}
			}
		});
    }
    
    /**
     * Configure ClientCommunicationManager and create new identity
     * 
     * Note: The remote Android Comms service is bound to and unbound from  in this method 
     * 
     * @param identity
     * @param domainAuthority
     * @param password
     * @param client
     */
    private void createNewIdentity(final String identity, final String domainAuthority, final String password, final String client) {
    	Dbc.invariant("ClientCommunicationManager must be a valid object", null != this.ccm);
    	//N.B. important that this flag is set to false as a this point the user has not been logged into the XMPP server
    	this.ccm.setLoginCompleted(false);
    	
    	this.ccm.bindCommsService(new IMethodCallback() {
			
			public void returnAction(String result) {
			}
			
			public void returnAction(boolean resultFlag) {
				if (resultFlag) {
					Log.d(LOG_TAG, "Bound to Android Comms");

					AppPreferences appPreferences = new AppPreferences(CSSManagerServiceBase.this.context);

					int xmppServerPort = appPreferences.getIntegerPrefValue(DOMAIN_AUTHORITY_SERVER_PORT);
					String domainAuthorityNode = appPreferences.getStringPrefValue(DOMAIN_AUTHORITY_NODE);
					String nodeJIDResource = appPreferences.getStringPrefValue(LOCAL_CSS_NODE_JID_RESOURCE);
					final String xmppServerIPAddress = appPreferences.getStringPrefValue(XMPP_SERVER_IP_ADDR);
					

					CSSManagerServiceBase.this.ccm.configureAgent(domainAuthorityNode, xmppServerPort, nodeJIDResource, false, new IMethodCallback() {
						
						public void returnAction(String result) {
						}
						
						public void returnAction(boolean resultFlag) {
							if (resultFlag) {
								try {
									String host = null;

									if (EntityRegularExpressions.isValidIPv4Address(xmppServerIPAddress)) {
										host = xmppServerIPAddress;
									}

									CSSManagerServiceBase.this.ccm.newMainIdentity(identity, domainAuthority, password, new IMethodCallback() {
										
										public void returnAction(String result) {
											Log.d(LOG_TAG, "New identity created: " + result);
											
											Intent intent = new Intent(IAndroidCSSManager.REGISTER_XMPP_SERVER);
											
											if (null != result) {
												intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, true);
											} else {
												intent.putExtra(IAndroidCSSManager.INTENT_RETURN_STATUS_KEY, false);
											}

											CssRecord aRecord = new CssRecord();
											aRecord.setCssIdentity(identity);
											aRecord.setDomainServer(domainAuthority);
											aRecord.setPassword(password);
											
											intent.putExtra(IAndroidCSSManager.INTENT_RETURN_VALUE_KEY, (Parcelable) aRecord);
											
											if (restrictBroadcast) {
												intent.setPackage(client);
											}

											Log.d(LOG_TAG, "DomainRegistration result sent");

											CSSManagerServiceBase.this.context.sendBroadcast(intent);
											
											if (CSSManagerServiceBase.this.ccm.unbindCommsService()) {
												Log.d(LOG_TAG, "Unbound from Android Comms");
											} else {
												Log.d(LOG_TAG, "Not Unbound from Android Comms");
											}
										}
										
										public void returnAction(boolean resultFlag) {
										}
									}, host);
									
								} catch (XMPPError x) {
									Log.e(LOG_TAG, "New Identity error", x);
								}
							}
						}
					});
				}
			}
		});
	}
}
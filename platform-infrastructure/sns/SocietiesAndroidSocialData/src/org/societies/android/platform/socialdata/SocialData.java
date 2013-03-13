/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.platform.socialdata;

import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.internal.sns.ISocialData;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.sns.socialdata.ConnectorBean;
import org.societies.api.internal.schema.sns.socialdata.SocialdataMessageBean;
import org.societies.api.internal.schema.sns.socialdata.SocialdataResultBean;
import org.societies.api.internal.schema.sns.socialdata.Socialnetwork;
import org.societies.platform.socialdata.utils.SocialDataCommsUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Android service to communicate with the SocialData bundle.
 *
 * @author Edgar Domingues (PTIN)
 *
 */
public class SocialData extends Service implements ISocialData {
	//LOGGING TAG
	private static final String LOG_TAG = SocialData.class.getName();
	
	//COMMS REQUIRED VARIABLES
	private static final List<String> ELEMENT_NAMES = Arrays.asList("socialdataMessageBean", "socialdataResultBean");
    private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/internal/schema/sns/socialdata");
    private static final List<String> PACKAGES = Arrays.asList("org.societies.api.internal.schema.sns.socialdata");   

	private IBinder binder = null;
	private ClientCommunicationMgr commMgr;
	private boolean connectedToComms = false;

	@Override
	public void onCreate () {
		this.binder = new LocalBinder();
		
		try {
			//INSTANTIATE COMMS MANAGER
			commMgr = new ClientCommunicationMgr(this, true);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Exception creating ClientCommunicationMgr instance.", e);
        }
		Log.d(LOG_TAG, "SocialData service starting");
	}

	@Override
	public void onDestroy() {
		//commMgr.unregister(ELEMENT_NAMES, nullCallback);		
		Log.d(LOG_TAG, "SocialData service terminating");
	}

	/**
	 * Create Binder object for local service invocation
	 */
	public class LocalBinder extends Binder {
		public ISocialData getService() {
			return SocialData.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return this.binder;
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>IServiceManager>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public boolean startService() {
    	if (!connectedToComms) {
        	//NOT CONNECTED TO COMMS SERVICE YET
        	Log.d(LOG_TAG, "SocialData startService binding to comms");
	        this.commMgr.bindCommsService(new IMethodCallback() {	
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Connected to comms: " + resultFlag);
					if (resultFlag) {
						connectedToComms = true;
						//REGISTER NAMESPACES
			        	commMgr.register(ELEMENT_NAMES, NAME_SPACES, PACKAGES, new IMethodCallback() {
							@Override
							public void returnAction(boolean resultFlag) {
								Log.d(LOG_TAG, "Namespaces registered: " + resultFlag);
								//SEND INTENT WITH SERVICE STARTED STATUS
				        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
				        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, resultFlag);
				        		SocialData.this.getBaseContext().sendBroadcast(intent);
							}
							@Override
							public void returnAction(String result) { }
						});
					} else {
						Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
			    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false);
			    		SocialData.this.getBaseContext().sendBroadcast(intent);
					}
				}	
				@Override
				public void returnAction(String result) { }
			});
        }
    	else {
    		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
    		SocialData.this.getBaseContext().sendBroadcast(intent);
    	}
		return true;
    }
    
    public boolean stopService() {
    	if (connectedToComms) {
        	//UNREGISTER AND DISCONNECT FROM COMMS
        	Log.d(LOG_TAG, "SocialData stopService unregistering namespaces");
        	commMgr.unregister(ELEMENT_NAMES, NAME_SPACES, new IMethodCallback() {
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Unregistered namespaces: " + resultFlag);
					connectedToComms = false;
					
					commMgr.unbindCommsService();
					//SEND INTENT WITH SERVICE STOPPED STATUS
	        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
	        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
	        		SocialData.this.getBaseContext().sendBroadcast(intent);
				}	
				@Override
				public void returnAction(String result) { }
			});
        }
    	else {
    		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
    		SocialData.this.getBaseContext().sendBroadcast(intent);
    	}
    	return true;
    }
    
    /**
	 * @param client
	 */
	private void broadcastServiceNotStarted(String client, String method) {
		if (client != null) {
			Intent intent = new Intent(method);
			intent.putExtra(IServiceManager.INTENT_NOTSTARTED_EXCEPTION, true);
			intent.setPackage(client);
			SocialData.this.getBaseContext().sendBroadcast(intent);
		}
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>ISocialData>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	public void addSocialConnector(String client, Socialnetwork socialNetwork, String token, long validity) {
		Log.d(LOG_TAG, "addSocialConnector");	
		
		if (connectedToComms) {
			//MESSAGE BEAN
			SocialdataMessageBean messageBean = SocialDataCommsUtils.createAddConnectorMessageBean(socialNetwork, token, validity);
			//COMMS STUFF
			try {
				IIdentity toId = commMgr.getIdManager().getCloudNode();	
				Stanza stanza = new Stanza(toId);
	        
	        	commMgr.sendIQ(stanza, IQ.Type.SET, messageBean, createCallback(this, ADD_SOCIAL_CONNECTOR, client));
				Log.d(LOG_TAG, "Sending stanza");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
	    } else {
	    	//NOT CONNECTED TO COMMS SERVICE
	    	broadcastServiceNotStarted(client, ADD_SOCIAL_CONNECTOR);
	    }
	}	
	
	public void removeSocialConnector(String client, String connectorId) {
		Log.d(LOG_TAG, "removeSocialConnector");	
		
		if (connectedToComms) {
			//MESSAGE BEAN
			SocialdataMessageBean messageBean = SocialDataCommsUtils.createRemoveConnectorMessageBean(connectorId);
			//COMMS STUFF
			try {
				IIdentity toId = commMgr.getIdManager().getCloudNode();	
				Stanza stanza = new Stanza(toId);
	        
	        	commMgr.sendIQ(stanza, IQ.Type.SET, messageBean, createCallback(this, REMOVE_SOCIAL_CONNECTOR, client));
				Log.d(LOG_TAG, "Sending stanza");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
	    } else {
	    	//NOT CONNECTED TO COMMS SERVICE
	    	broadcastServiceNotStarted(client, REMOVE_SOCIAL_CONNECTOR);
	    }
	}
	
	public void getSocialConnectors(String client) {
		Log.d(LOG_TAG, "getSocialConnectors");	
		
		if (connectedToComms) {
			//MESSAGE BEAN
			SocialdataMessageBean messageBean = SocialDataCommsUtils.createGetConnectorsMessageBean();
			//COMMS STUFF
			try {
				IIdentity toId = commMgr.getIdManager().getCloudNode();	
				Stanza stanza = new Stanza(toId);
	        
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, createCallback(this, GET_SOCIAL_CONNECTORS, client));
				Log.d(LOG_TAG, "Sending stanza");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
	    } else {
	    	//NOT CONNECTED TO COMMS SERVICE
	    	broadcastServiceNotStarted(client, GET_SOCIAL_CONNECTORS);
	    }
	}
		
	private ICommCallback createCallback(final Context context, final String action, final String client) {
		
		return new ICommCallback() {

			public List<String> getXMLNamespaces() {
				return NAME_SPACES;
			}

			public List<String> getJavaPackages() {
				return PACKAGES;
			}

			public void receiveResult(Stanza stanza, Object payload) {
				Log.d(LOG_TAG, "receiveResult");
				
				if(payload instanceof SocialdataResultBean) {
					
					SocialdataResultBean resultBean = (SocialdataResultBean)payload;
					
					Intent intent = new Intent(action);
					
					if(action.equals(ADD_SOCIAL_CONNECTOR)) {
						intent.putExtra(INTENT_RETURN_KEY, resultBean.getId());
					}
					else if(action.equals(GET_SOCIAL_CONNECTORS)) {
						List<ConnectorBean> connectors =  resultBean.getConnectorsList().getConnectorBean();
						ConnectorBean arrConnectors[] = connectors.toArray(new ConnectorBean[connectors.size()]);
						intent.putExtra(INTENT_RETURN_KEY, arrConnectors);
					}
					else if(action.equals(REMOVE_SOCIAL_CONNECTOR)) {
						intent.putExtra(INTENT_RETURN_KEY, true);
					}
					
					intent.setPackage(client);
					context.sendBroadcast(intent);					
				}
			}

			public void receiveError(Stanza stanza, XMPPError error) {
				Log.d(LOG_TAG, "receiveError: "+error.getStanzaErrorString());
				
				Intent intent = new Intent(ACTION_XMPP_ERROR);
				intent.putExtra(EXTRA_STANZA_ERROR, error.getStanzaErrorString());
				
				intent.setPackage(client);
				context.sendBroadcast(intent);
			}

			public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
				Log.d(LOG_TAG, "receiveInfo");
			}

			public void receiveItems(Stanza stanza, String node,
					List<String> items) {
				Log.d(LOG_TAG, "receiveItems");
			}

			public void receiveMessage(Stanza stanza, Object payload) {
				Log.d(LOG_TAG, "receiveMessage");
			}
			
		};
	}

}

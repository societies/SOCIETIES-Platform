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
import org.societies.android.api.internal.sns.AConnectorBean;
import org.societies.android.api.internal.sns.ISocialData;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.sns.socialdata.ConnectorBean;
import org.societies.api.internal.schema.sns.socialdata.ConnectorsList;
import org.societies.api.internal.schema.sns.socialdata.SocialdataMessageBean;
import org.societies.api.internal.schema.sns.socialdata.SocialdataResultBean;
import org.societies.api.internal.sns.ISocialConnector.SocialNetwork;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.identity.IdentityManagerImpl;
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

	private static final String LOG_TAG = SocialData.class.getName();
	
	//COMMS REQUIRED VARIABLES
	private static final List<String> ELEMENT_NAMES = Arrays.asList("SocialdataMessageBean", "SocialdataResultBean");
    private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/internal/schema/sns/socialdata");
    private static final List<String> PACKAGES = Arrays.asList("org.societies.api.internal.schema.sns.socialdata");   

	private IBinder binder = null;
	
	private ClientCommunicationMgr commMgr;

	@Override
	public void onCreate () {
		this.binder = new LocalBinder();
		
		try {
			//INSTANTIATE COMMS MANAGER
			commMgr = new ClientCommunicationMgr(this);
			commMgr.register(ELEMENT_NAMES, nullCallback);		
		} catch (Exception e) {
			Log.e(LOG_TAG, "Exception creating ClientCommunicationMgr instance.", e);
        }  

		Log.d(LOG_TAG, "SocialData service starting");
	}

	@Override
	public void onDestroy() {
		commMgr.unregister(ELEMENT_NAMES, nullCallback);
		
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
	
	//Service API

	public void addSocialConnector(String client, SocialNetwork socialNetwork, String token, long validity) {
		Log.d(LOG_TAG, "addSocialConnector");	
		
		//MESSAGE BEAN
		SocialdataMessageBean messageBean = SocialDataCommsUtils.createAddConnectorMessageBean(socialNetwork, token, validity);

		//COMMS STUFF
		IIdentity toId = commMgr.getIdManager().getCloudNode();	
		Stanza stanza = new Stanza(toId);
        try {
        	commMgr.sendIQ(stanza, IQ.Type.SET, messageBean, createCallback(this, ADD_SOCIAL_CONNECTOR, client));
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "Exception sending message: " + e.getMessage(), e);
        }
	}	
	
	public void removeSocialConnector(String client, String connectorId) {
		Log.d(LOG_TAG, "removeSocialConnector");	
		
		//MESSAGE BEAN
		SocialdataMessageBean messageBean = SocialDataCommsUtils.createRemoveConnectorMessageBean(connectorId);

		//COMMS STUFF
		IIdentity toId = commMgr.getIdManager().getCloudNode();	
		Stanza stanza = new Stanza(toId);
        try {
        	commMgr.sendIQ(stanza, IQ.Type.SET, messageBean, createCallback(this, REMOVE_SOCIAL_CONNECTOR, client));
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "Exception sending message: " + e.getMessage(), e);
        }
	}
	
	public void getSocialConnectors(String client) {
		Log.d(LOG_TAG, "getSocialConnectors");	
		
		//MESSAGE BEAN
		SocialdataMessageBean messageBean = SocialDataCommsUtils.createGetConnectorsMessageBean();

		//COMMS STUFF
		IIdentity toId = commMgr.getIdManager().getCloudNode();	
		Stanza stanza = new Stanza(toId);
        try {
        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, createCallback(this, GET_SOCIAL_CONNECTORS, client));
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {
			Log.e(LOG_TAG, "Exception sending message: " + e.getMessage(), e);
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
						intent.putExtra(INTENT_RETURN_KEY, convertConnectorsListToAConnectorBeanArray(resultBean.getConnectorsList()));
					}
					
					intent.setPackage(client);
					context.sendBroadcast(intent);					
				}
			}

			public void receiveError(Stanza stanza, XMPPError error) {
				Log.d(LOG_TAG, "receiveError: "+error.getGenericText());
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
	
	private AConnectorBean[] convertConnectorsListToAConnectorBeanArray(ConnectorsList connectorsList) { 
		List<ConnectorBean> beans = connectorsList.getConnectorBean();
		AConnectorBean[] connectorBeans = new AConnectorBean[beans.size()];
		
		for(int i=0; i<connectorBeans.length; i++) {			
			connectorBeans[i] = new AConnectorBean(beans.get(i));			
		}
		
		return connectorBeans;
	}
	
	private ICommCallback nullCallback = new ICommCallback() {

		public List<String> getXMLNamespaces() {
			return NAME_SPACES;
		}

		public List<String> getJavaPackages() {
			return PACKAGES;
		}

		public void receiveResult(Stanza stanza, Object payload) {
			Log.d(LOG_TAG, "receiveResult");
		}

		public void receiveError(Stanza stanza, XMPPError error) {
			Log.d(LOG_TAG, "receiveError: "+error.getGenericText());
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

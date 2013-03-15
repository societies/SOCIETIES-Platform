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
package org.societies.android.platform.cis;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.cis.directory.ICisDirectory;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.cis.directory.CisDirectoryBean;
import org.societies.api.schema.cis.directory.CisDirectoryBeanResult;
import org.societies.api.schema.cis.directory.MethodType;
import org.societies.utilities.DBC.Dbc;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class CisDirectoryBase implements ICisDirectory {
	//LOGGING TAG
	private static final String LOG_TAG = CisDirectoryBase.class.getName();
	
	//COMMS REQUIRED VARIABLES
	private static final List<String> ELEMENT_NAMES = Arrays.asList("cisDirectoryBean", "cisDirectoryBeanResult");
	private static final List<String> NAME_SPACES = Collections.unmodifiableList(Arrays.asList("http://societies.org/api/schema/cis/directory"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(Arrays.asList("org.societies.api.schema.cis.directory"));
    
	private ClientCommunicationMgr commMgr;
    private Context androidContext;
    private boolean connectedToComms = false;
    private boolean restrictBroadcast;
    
    /**Default constructor*/
    public CisDirectoryBase(Context androidContext) {
    	this(androidContext, true);
    }
    
    /**Parameterised constructor*/
    public CisDirectoryBase(Context androidContext, boolean restrictBroadcast) {
    	Log.d(LOG_TAG, "Object created");
    	
    	this.androidContext = androidContext;    	
		try {
			//INSTANTIATE COMMS MANAGER
			this.commMgr = new ClientCommunicationMgr(androidContext, true);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
        }
    }

    public boolean startService() {
    	if (!connectedToComms) {
        	//NOT CONNECTED TO COMMS SERVICE YET
        	Log.d(LOG_TAG, "CisDirectoryBase startService binding to comms");
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
				        		CisDirectoryBase.this.androidContext.sendBroadcast(intent);
							}
							@Override
							public void returnAction(String result) { }
							@Override
							public void returnException(String result) {
								// TODO Auto-generated method stub
							}

						});
					} else {
						Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
			    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false);
			    		CisDirectoryBase.this.androidContext.sendBroadcast(intent);
					}
				}	
				@Override
				public void returnAction(String result) { }
				@Override
				public void returnException(String result) {
					// TODO Auto-generated method stub
				}

			});
        }
    	else {
    		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
    		androidContext.sendBroadcast(intent);
    	}
		return true;
    }
    
    public boolean stopService() {
    	if (connectedToComms) {
        	//UNREGISTER AND DISCONNECT FROM COMMS
        	Log.d(LOG_TAG, "CisDirectoryBase stopService unregistering namespaces");
        	commMgr.unregister(ELEMENT_NAMES, NAME_SPACES, new IMethodCallback() {
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Unregistered namespaces: " + resultFlag);
					connectedToComms = false;
					
					commMgr.unbindCommsService();
					//SEND INTENT WITH SERVICE STOPPED STATUS
	        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
	        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
	        		CisDirectoryBase.this.androidContext.sendBroadcast(intent);
				}	
				@Override
				public void returnAction(String result) { }
				@Override
				public void returnException(String result) {
					// TODO Auto-generated method stub
				}

			});
        }
    	else {
    		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
    		androidContext.sendBroadcast(intent);
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
			androidContext.sendBroadcast(intent);
		}
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ICisDirectory METHODS >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/* @see org.societies.android.api.cis.directory.ICisDirectory#findAllCisAdvertisementRecords(java.lang.String) */
	public CisAdvertisementRecord[] findAllCisAdvertisementRecords(final String client) {
		Log.d(LOG_TAG, "findAllCisAdvertisementRecords called by client: " + client);
		Dbc.require("Client must be supplied", client != null);
		
        if (connectedToComms) {
			//MESSAGE BEAN
			final CisDirectoryBean messageBean = new CisDirectoryBean();
			messageBean.setMethod(MethodType.FIND_ALL_CIS_ADVERTISEMENT_RECORDS);
			//SEND COMMS
			try {
				IIdentity toID = commMgr.getIdManager().getDomainAuthorityNode();
				final ICommCallback cisCallback = new CisDirectoryCallback(client, ICisDirectory.FIND_ALL_CIS);
				final Stanza stanza = new Stanza(toID);
				commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback); 
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
        } else {
        	//NOT CONNECTED TO COMMS SERVICE
        	broadcastServiceNotStarted(client, ICisDirectory.FIND_ALL_CIS);
        }
		return null;
	}

	/* @see org.societies.android.api.cis.directory.ICisDirectory#findForAllCis(java.lang.String, java.lang.String) */
	public CisAdvertisementRecord[] findForAllCis(final String client, final String filter) {       
        Log.d(LOG_TAG, "findForAllCis called by client: " + client);
		Dbc.require("Client must be supplied", client != null);
		
        if (connectedToComms) {
			//MESSAGE BEAN
			final CisDirectoryBean messageBean = new CisDirectoryBean();
			messageBean.setMethod(MethodType.FIND_FOR_ALL_CIS);
			CisAdvertisementRecord advert = new CisAdvertisementRecord();
			advert.setName(filter);
			messageBean.setCisA(advert);
			//SEND COMMS
			try {
				IIdentity toID = commMgr.getIdManager().getDomainAuthorityNode();
				final ICommCallback cisCallback = new CisDirectoryCallback(client, ICisDirectory.FILTER_CIS);
				final Stanza stanza = new Stanza(toID);
				commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback); 
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
        } else {
        	//NOT CONNECTED TO COMMS SERVICE
        	broadcastServiceNotStarted(client, ICisDirectory.FILTER_CIS);
        }
		return null;
	}

	/* @see org.societies.android.api.cis.directory.ICisDirectory#searchByID(java.lang.String, java.lang.String) */
	public CisAdvertisementRecord searchByID(final String client, final String cis_id) {
		Log.d(LOG_TAG, "searchByID called by client: " + client);
		Dbc.require("Client must be supplied", client != null);
		
        if (connectedToComms) {
			//MESSAGE BEAN
			final CisDirectoryBean messageBean = new CisDirectoryBean();
			messageBean.setMethod(MethodType.SEARCH_BY_ID);
			messageBean.setFilter(cis_id);
			//SEND COMMS
			try {
				IIdentity toID = commMgr.getIdManager().getDomainAuthorityNode();
				final ICommCallback cisCallback = new CisDirectoryCallback(client, ICisDirectory.FIND_CIS_ID);
				final Stanza stanza = new Stanza(toID);
				commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, cisCallback); 
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
        } else {
        	//NOT CONNECTED TO COMMS SERVICE
        	broadcastServiceNotStarted(client, ICisDirectory.FIND_CIS_ID);
        }
		return null;
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> COMMS CALLBACK >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/**
	 * Callback required for Android Comms Manager
	 */
	private class CisDirectoryCallback implements ICommCallback {
		private String returnIntent;
		private String client;
		
		/**Constructor sets the calling client and Intent to be returned
		 * @param client
		 * @param returnIntent
		 */
		public CisDirectoryCallback(String client, String returnIntent) {
			this.client = client;
			this.returnIntent = returnIntent;
		}

		public List<String> getXMLNamespaces() {
			return NAME_SPACES;
		}

		public List<String> getJavaPackages() {
			return PACKAGES;
		}

		public void receiveError(Stanza arg0, XMPPError err) {
			Log.d(LOG_TAG, "Callback receiveError:" + err.getMessage());			
		}

		public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
			Log.d(LOG_TAG, "Callback receiveInfo");
		}

		public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
			Log.d(LOG_TAG, "Callback receiveItems");
		}

		public void receiveMessage(Stanza arg0, Object arg1) {
			Log.d(LOG_TAG, "Callback receiveMessage");	
		}

		public void receiveResult(Stanza returnStanza, Object msgBean) {
			Log.d(LOG_TAG, "Callback receiveResult");
			
			if (client != null) {
				Intent intent = new Intent(returnIntent);
				
				Log.d(LOG_TAG, ">>>>>Return Stanza: " + returnStanza.toString());
				if (msgBean==null) Log.d(LOG_TAG, ">>>>msgBean is null");
				// --------- cisDirectoryBeanResult Bean ---------
				if (msgBean instanceof CisDirectoryBeanResult) {
					Log.d(LOG_TAG, "CisDirectoryBeanResult Result!");
					CisDirectoryBeanResult dirResult = (CisDirectoryBeanResult) msgBean;
					List<CisAdvertisementRecord> listReturned = dirResult.getResultCis();
					CisAdvertisementRecord[] returnArray = listReturned.toArray(new CisAdvertisementRecord[listReturned.size()]);
					
					if (intent.getAction().equals(ICisDirectory.FIND_CIS_ID))
						intent.putExtra(ICisDirectory.INTENT_RETURN_VALUE, (Parcelable) returnArray[0]);
					else
						intent.putExtra(ICisDirectory.INTENT_RETURN_VALUE, returnArray);
					
					//NOTIFY CALLING CLIENT
					if (restrictBroadcast)
						intent.setPackage(client);
					
					CisDirectoryBase.this.androidContext.sendBroadcast(intent);
				}
			}
		}
	}//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> END COMMS CALLBACK >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}

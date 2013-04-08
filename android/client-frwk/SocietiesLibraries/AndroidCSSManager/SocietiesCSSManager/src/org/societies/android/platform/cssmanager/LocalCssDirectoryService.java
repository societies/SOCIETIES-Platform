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
package org.societies.android.platform.cssmanager;

import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.societies.android.api.cis.directory.ICisDirectory;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.VCardParcel;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.css.directory.IAndroidCssDirectory;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.css.directory.CssDirectoryBean;
import org.societies.api.schema.css.directory.MethodType;
import org.societies.api.schema.css.directory.CssDirectoryBeanResult;

import com.google.gson.Gson;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author aleckey
 * 
 * TODO: Two new methods required to allow for the binding and unbinding  of this service 
 * to and from the Android Comms service, otherwise this service is not going to work.
 *
 */
public class LocalCssDirectoryService extends Service implements IAndroidCssDirectory {
	private static final String LOG_TAG = LocalCssDirectoryService.class.getName();
	
	private static final List<String> ELEMENT_NAMES = Arrays.asList("cssDirectoryBean", "cssDirectoryBeanResult");
    private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/schema/css/directory");
    private static final List<String> PACKAGES = Arrays.asList("org.societies.api.schema.css.directory");

    private ClientCommunicationMgr ccm;
    private IBinder binder = null;
    private boolean connectedToComms = false;
    
    @Override
	public void onCreate () {
    	Log.d(LOG_TAG, "CssDirectory service starting");
    	this.binder = new LocalCssDirectoryBinder();
		this.ccm = new ClientCommunicationMgr(this, true);
	}
    
	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "CSSManager service terminating");
	}

	/**
	 * Create Binder object for local service invocation
	 */
	 public class LocalCssDirectoryBinder extends Binder {
		 public LocalCssDirectoryService getService() {
	            return LocalCssDirectoryService.this;
	        }
	    }

	@Override
	public IBinder onBind(Intent arg0) {
		return this.binder;
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>IServiceManager>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public boolean startService() {
    	if (!connectedToComms) {
        	//NOT CONNECTED TO COMMS SERVICE YET
        	Log.d(LOG_TAG, "LocalCssDirectoryService startService binding to comms");
	        this.ccm.bindCommsService(new IMethodCallback() {	
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Connected to comms: " + resultFlag);
					if (resultFlag) {
						connectedToComms = true;
						//REGISTER NAMESPACES
						ccm.register(ELEMENT_NAMES, NAME_SPACES, PACKAGES, new IMethodCallback() {
							@Override
							public void returnAction(boolean resultFlag) {
								Log.d(LOG_TAG, "Namespaces registered: " + resultFlag);
								//SEND INTENT WITH SERVICE STARTED STATUS
				        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
				        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, resultFlag);
				        		LocalCssDirectoryService.this.getApplicationContext().sendBroadcast(intent);
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
			    		LocalCssDirectoryService.this.getApplicationContext().sendBroadcast(intent);
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
    		LocalCssDirectoryService.this.getApplicationContext().sendBroadcast(intent);
    	}
		return true;
    }
    
    public boolean stopService() {
    	if (connectedToComms) {
        	//UNREGISTER AND DISCONNECT FROM COMMS
        	Log.d(LOG_TAG, "LocalCssDirectoryService stopService unregistering namespaces");
        	ccm.unregister(ELEMENT_NAMES, NAME_SPACES, new IMethodCallback() {
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Unregistered namespaces: " + resultFlag);
					connectedToComms = false;
					
					ccm.unbindCommsService();
					//SEND INTENT WITH SERVICE STOPPED STATUS
	        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
	        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
	        		LocalCssDirectoryService.this.getApplicationContext().sendBroadcast(intent);
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
    		LocalCssDirectoryService.this.getApplicationContext().sendBroadcast(intent);
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
			LocalCssDirectoryService.this.getApplicationContext().sendBroadcast(intent);
		}
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>ICssDirectory>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	public CssAdvertisementRecord[] findAllCssAdvertisementRecords(String client) {
		Log.d(LOG_TAG, "findAllCssAdvertisementRecords called by client: " + client);
		if(connectedToComms) {
			//MESSAGE BEAN
			CssDirectoryBean directoryBean = new CssDirectoryBean();
			directoryBean.setMethod(MethodType.FIND_ALL_CSS_ADVERTISEMENT_RECORDS);
			try {
				Stanza stanza = new Stanza(ccm.getIdManager().getDomainAuthorityNode());
				ICommCallback callback = new CSSDirectoryCallback(client, IAndroidCssDirectory.FIND_ALL_CSS_ADVERTISEMENT_RECORDS);
				ccm.sendIQ(stanza, IQ.Type.GET, directoryBean, callback);
				Log.d(LOG_TAG, "Sent stanza");
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

	public CssAdvertisementRecord[] findForAllCss(String client, String searchTerm) {
		Log.d(LOG_TAG, "getFriendRequests called by client: " + client);
		
		if(connectedToComms) {
			//MESSAGE BEAN
			CssAdvertisementRecord aAdvert = new CssAdvertisementRecord();
			aAdvert.setName(searchTerm);
			CssDirectoryBean directoryBean = new CssDirectoryBean();
			directoryBean.setCssA(aAdvert);
			directoryBean.setMethod(MethodType.FIND_FOR_ALL_CSS);
			try {
				Stanza stanza = new Stanza(ccm.getIdManager().getDomainAuthorityNode());
				ICommCallback callback = new CSSDirectoryCallback(client, IAndroidCssDirectory.FIND_FOR_ALL_CSS);
				ccm.sendIQ(stanza, IQ.Type.GET, directoryBean, callback);
				Log.d(LOG_TAG, "Send stanza"); 
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

	@Override
	public VCardParcel getUserVCard(String client, String userId) {
		Log.d(LOG_TAG, "getUserVCard called by client: " + client);
		if(connectedToComms) {
			ICommCallback callback = new CSSDirectoryCallback(client, IAndroidCssDirectory.GET_USER_VCARD);
			ccm.getVCard(userId, callback);
		}
		return null;
	}

	@Override
	public VCardParcel getMyVCard(String client) {
		Log.d(LOG_TAG, "getUserVCard called by client: " + client);
		if(connectedToComms) {
			ICommCallback callback = new CSSDirectoryCallback(client, IAndroidCssDirectory.GET_MY_VCARD);
			ccm.getVCard(callback);
		}
		return null;
	}
	
	/**
	 * Callback used with Android Comms for CSSDirectory
	 *
	 */
	private class CSSDirectoryCallback implements ICommCallback {
		String returnIntent;
		String client;
		
		public CSSDirectoryCallback(String client, String returnIntent) {
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
			Log.d(LOG_TAG, "CSSDirectoryCallback Callback receiveError");
		}

		public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
			Log.d(LOG_TAG, "CSSDirectoryCallback Callback receiveInfo");
		}

		public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
			Log.d(LOG_TAG, "CSSDirectoryCallback Callback receiveItems");
		}

		public void receiveMessage(Stanza arg0, Object arg1) {
			Log.d(LOG_TAG, "CSSDirectoryCallback Callback receiveMessage");
		}

		public void receiveResult(Stanza arg0, Object retValue) {
			Log.d(LOG_TAG, "CSSDirectoryCallback Callback receiveResult");
			
			if (client != null) {
				Intent intent = new Intent(returnIntent);
				
				//CSS MANAGER RESULT BEAN
				if (retValue instanceof CssDirectoryBeanResult) {
					CssDirectoryBeanResult resultBean = (CssDirectoryBeanResult) retValue;
					List<CssAdvertisementRecord> listRecords = resultBean.getResultCss();
					CssAdvertisementRecord advertArray[] = resultBean.getResultCss().toArray(new CssAdvertisementRecord[listRecords.size()]);
	
					intent.putExtra(IAndroidCssDirectory.INTENT_RETURN_STATUS_KEY, true);
					intent.putExtra(IAndroidCssDirectory.INTENT_RETURN_VALUE_KEY, advertArray);
				}
				//VCARD RETURNED
				else if (retValue instanceof VCardParcel) {
					VCardParcel vCard = (VCardParcel)retValue;
					intent.putExtra(IAndroidCssDirectory.INTENT_RETURN_STATUS_KEY, true);
					intent.putExtra(IAndroidCssDirectory.INTENT_RETURN_VALUE_KEY, (Parcelable)vCard);
				}
				intent.setPackage(client);
				LocalCssDirectoryService.this.sendBroadcast(intent);
				Log.d(LOG_TAG, "CSSDirectoryCallback Callback receiveResult sent return value: " + retValue);
			}
		}
	}

	private JSONObject convertCssNode(VCardParcel card) {
        JSONObject jObj = new JSONObject();
		Gson gson = new Gson();
		try {
			jObj =  (JSONObject) new JSONTokener(gson.toJson(card)).nextValue();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return jObj;
    }
	
}

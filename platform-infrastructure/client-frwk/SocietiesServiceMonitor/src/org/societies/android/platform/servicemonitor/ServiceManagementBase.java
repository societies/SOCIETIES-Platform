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

package org.societies.android.platform.servicemonitor;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.internal.servicelifecycle.IServiceControl;
import org.societies.android.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.android.api.servicelifecycle.AService;
import org.societies.android.api.servicelifecycle.AServiceResourceIdentifier;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.servicelifecycle.servicecontrol.MethodType;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlMsgBean;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResultBean;
import org.societies.api.schema.servicelifecycle.servicediscovery.MethodName;
import org.societies.api.schema.servicelifecycle.servicediscovery.ServiceDiscoveryMsgBean;
import org.societies.api.schema.servicelifecycle.servicediscovery.ServiceDiscoveryResultBean;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.utilities.DBC.Dbc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;


/**
 * This class acts as the base functionality for the {@link IServiceDiscovery} service, both local and remote.
 * 
 *
 */
public class ServiceManagementBase implements IServiceDiscovery, IServiceControl {
	//Logging tag
    private static final String LOG_TAG = ServiceManagementBase.class.getName();
    
	//COMMS REQUIRED VARIABLES
	private static final List<String> ELEMENT_NAMES = Arrays.asList("serviceDiscoveryMsgBean", "serviceDiscoveryResultBean", "serviceControlMsgBean", "serviceControlResultBean");
    private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/schema/servicelifecycle/servicediscovery",
															  	  "http://societies.org/api/schema/servicelifecycle/servicecontrol",
															  	  "http://societies.org/api/schema/servicelifecycle/model");
    private static final List<String> PACKAGES = Arrays.asList("org.societies.api.schema.servicelifecycle.servicediscovery", 
															   "org.societies.api.schema.servicelifecycle.servicecontrol",
															   "org.societies.api.schema.servicelifecycle.model");
    private ClientCommunicationMgr commMgr;
    private Context androidContext;
    

    /**
     * Default constructor
     */
    public ServiceManagementBase(Context androidContext) {
    	Log.d(LOG_TAG, "Object created");
    	
    	this.androidContext = androidContext;
    	
		try {
			//INSTANTIATE COMMS MANAGER
			this.commMgr = new ClientCommunicationMgr(androidContext);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
        }
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> IServiceDiscovery methods >>>>>>>>>>>>>>>>>>>>>>>
    /* @see org.societies.android.api.internal.servicelifecycle.IServiceDiscovery#getServices(java.lang.String, org.societies.api.identity.IIdentity)*/
	public AService[] getMyServices(String client) {
		Log.d(LOG_TAG, "getMyServices called by client: " + client);
		
		AsyncGetMyServices methodAsync = new AsyncGetMyServices();
		String params [] = {client};
		methodAsync.execute(params);
		
        return null;
	}
	
	/* @see org.societies.android.api.internal.servicelifecycle.IServiceDiscovery#getServices(java.lang.String, org.societies.api.identity.IIdentity)*/
	public AService[] getServices(String client, String identity) {
		Log.d(LOG_TAG, "getServices called by client: " + client);
		
		AsynGetServices methodAsync = new AsynGetServices();
		String params [] = {client, identity};
		methodAsync.execute(params);

        return null;
	}

	/* @see org.societies.android.api.internal.servicelifecycle.IServiceDiscovery#getService(java.lang.String, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, org.societies.api.identity.IIdentity)*/
	public AService getService(String client, AServiceResourceIdentifier sri, String identity) {
		return null;
	}

	/* @see org.societies.android.api.internal.servicelifecycle.IServiceDiscovery#searchService(java.lang.String, org.societies.api.schema.servicelifecycle.model.Service, org.societies.api.identity.IIdentity) */
	public AService[] searchService(String client, AService filter, String identity) {
		return null;
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> IServiceControl methods >>>>>>>>>>>>>>>>>>>>>>>
	/* @see org.societies.android.api.internal.servicelifecycle.IServiceControl#installService(java.lang.String, java.net.URL, java.lang.String)*/
	public String installService(String client, URL arg1, String identity) {
		return null;
	}

	/* @see org.societies.android.api.internal.servicelifecycle.IServiceControl#uninstallService(java.lang.String, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, java.lang.String)*/
	public String uninstallService(String client, AServiceResourceIdentifier serviceId, String identity) {
		return null;
	}
	
	/* @see org.societies.android.api.internal.servicelifecycle.IServiceControl#shareService(java.lang.String, org.societies.api.schema.servicelifecycle.model.Service, java.lang.String)*/
	public String shareService(String client, AService service, String identity) {
		Log.d(LOG_TAG, "shareService called by client: " + client);
		
		AsyncShareService methodAsync = new AsyncShareService();
		Object params[] = {client, service, identity, IServiceControl.SHARE_SERVICE};
		methodAsync.execute(params);
		
		return null;
	}
	
	/*@see org.societies.android.api.internal.servicelifecycle.IServiceControl#unshareService(java.lang.String, org.societies.api.schema.servicelifecycle.model.Service, java.lang.String)*/
	public String unshareService(String client, AService service, String identity) {
		Log.d(LOG_TAG, "unshareService called by client: " + client);
		
		AsyncShareService methodAsync = new AsyncShareService();
		Object params[] = {client, service, identity, IServiceControl.UNSHARE_SERVICE};
		methodAsync.execute(params);
		
		return null;
	}

	/* @see org.societies.android.api.internal.servicelifecycle.IServiceControl#startService(java.lang.String, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, java.lang.String) */
	public String startService(String client, AServiceResourceIdentifier serviceId) {
		Log.d(LOG_TAG, "startService called by client: " + client);
		
		AsyncControlService methodAsync = new AsyncControlService();
		Object params[] = {client, serviceId, IServiceControl.START_SERVICE};
		methodAsync.execute(params);
		
		return null;
	}

	/* @see org.societies.android.api.internal.servicelifecycle.IServiceControl#stopService(java.lang.String, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, java.lang.String)*/
	public String stopService(String client, AServiceResourceIdentifier serviceId) {
		Log.d(LOG_TAG, "stopService called by client: " + client);
		
		AsyncControlService methodAsync = new AsyncControlService();
		Object params[] = {client, serviceId, IServiceControl.STOP_SERVICE};
		methodAsync.execute(params);
		
		return null;
	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ICommCallback methods >>>>>>>>>>>>>>>>>>>>>>>
	/**
	 * Callback required for Android Comms Manager to enable remote invocations to callback with returned information
	 */
	private class ServiceLifecycleCallback implements ICommCallback {
		private String returnIntent;
		private String client;

		/**Constructor sets the calling client and Intent to be returned
		 * @param client
		 * @param returnIntent
		 */
		public ServiceLifecycleCallback(String client, String returnIntent) {
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
				// --------- Service Discovery Bean ---------
				if (msgBean instanceof ServiceDiscoveryResultBean) {
					Log.d(LOG_TAG, "ServiceDiscoveryBeanResult!");
					ServiceDiscoveryResultBean discoResult = (ServiceDiscoveryResultBean) msgBean;
					List<org.societies.api.schema.servicelifecycle.model.Service> serviceList = discoResult.getServices();
					//CONVERT TO PARCEL BEANS
					int i=0;
					Parcelable serviceArray[] = new Parcelable[serviceList.size()];
					for(org.societies.api.schema.servicelifecycle.model.Service tmpService: serviceList) {
						serviceArray[i] = AService.convertService(tmpService);
						i++;
					}
					//NOTIFY CALLING CLIENT
					intent.putExtra(IServiceDiscovery.INTENT_RETURN_VALUE, serviceArray); 
					intent.setPackage(client);
				} 
				// --------- Service Control Bean ---------
				if(msgBean instanceof ServiceControlResultBean) {
					Log.d(LOG_TAG, "ServiceControlBeanResult!");
					ServiceControlResultBean controlResult = (ServiceControlResultBean)msgBean;
					String result = controlResult.getControlResult().getMessage().toString();
					Log.d(LOG_TAG, "ServiceControlBeanResult: " + result);
					
					//NOTIFY CALLING CLIENT
					intent.putExtra(IServiceControl.INTENT_RETURN_VALUE, result);
					intent.setPackage(client);
				}
				ServiceManagementBase.this.androidContext.sendBroadcast(intent);
				ServiceManagementBase.this.commMgr.unregister(ELEMENT_NAMES, this);
			}
		}
	}
	
	/**
	 * AsyncTask classes required to carry out threaded tasks. These classes should be used where it is estimated that 
	 * the task length is unknown or potentially long. While direct usage of the Communications components for remote 
	 * method invocation is an explicitly asynchronous operation, other usage is not and in general the use of these types of classes
	 * is encouraged. Remember, Android Not Responding (ANR) exceptions will be invoked if the main application thread is abused
	 * and the application will be closed down by Android very soon after.
	 * 
	 * Although the result of an AsyncTask can be obtained by using <AsyncTask Object>.get() it's not a good idea as 
	 * it will effectively block the parent method until the result is delivered back and so render the use if the AsyncTask
	 * class ineffective. Use Intents as an asynchronous callback mechanism.
	 */
	
	/**
	 * This class carries out the getMyServices method call asynchronously
	 */
	private class AsyncGetMyServices extends AsyncTask<String, Void, String[]> {
		
		@Override
		/**
		 * Carry out compute task 
		 */
		protected String[] doInBackground(String... params) {
			Dbc.require("At least one parameter must be supplied", params.length >= 1);
			Log.d(LOG_TAG, "DomainRegistration - doInBackground");
			
			String results [] = new String[1];
			results[0] = params[0];
			
			//Message Bean
			ServiceDiscoveryMsgBean messageBean = new ServiceDiscoveryMsgBean();
			messageBean.setMethod(MethodName.GET_LOCAL_SERVICES);

			//Communications configuration
			ICommCallback discoCallback = new ServiceLifecycleCallback(params[0], GET_MY_SERVICES); 
			IIdentity toID = commMgr.getIdManager().getCloudNode();
			Log.e(LOG_TAG, "Cloud Node: " + toID.getJid());
			Stanza stanza = new Stanza(toID);
	        try {
	        	commMgr.register(ELEMENT_NAMES, discoCallback);
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, discoCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (Exception e) {
				Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
	        }
	 
			return results;
		}

		
		@Override
		/**
		 * Handle the communication of the result
		 */
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "DomainRegistration - onPostExecute");
	    }
	}
	
	/**
	 * This class carries out the getServices method call asynchronously
	 */
	private class AsynGetServices extends AsyncTask<String, Void, String[]> {
		
		@Override
		/**
		 * Carry out compute task 
		 */
		protected String[] doInBackground(String... params) {
			Dbc.require("At least two parameters must be supplied", params.length >= 2);
			Log.d(LOG_TAG, "DomainRegistration - doInBackground");
			
			String results [] = new String[1];
			results[0] = params[0];
			
			//Message Bean
			ServiceDiscoveryMsgBean messageBean = new ServiceDiscoveryMsgBean();
			messageBean.setMethod(MethodName.GET_LOCAL_SERVICES);

			//Communications configuration
			ICommCallback discoCallback = new ServiceLifecycleCallback(params[0], GET_SERVICES); 
			IIdentity toID;
			try {
				toID = commMgr.getIdManager().fromJid(params[1]);
			} catch (InvalidFormatException e1) {
				toID = commMgr.getIdManager().getCloudNode();
				e1.printStackTrace();
			}
			Stanza stanza = new Stanza(toID);
	        try {
	        	commMgr.register(ELEMENT_NAMES, discoCallback);
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, discoCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (Exception e) {
				Log.e(this.getClass().getName(), "ERROR sending message: " + e.getMessage());
	        }
	 
			return results;
		}

		
		@Override
		/**
		 * Handle the communication of the result
		 */
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "DomainRegistration - onPostExecute");
	    }
	}
	
	/**
	 * This class carries out the Share and unShare Services methods call asynchronously
	 */
	private class AsyncShareService extends AsyncTask<Object, Void, String[]> {
		
		@Override
		/**
		 * Carry out compute task 
		 */
		protected String[] doInBackground(Object... params) {
			Dbc.require("At least two parameters must be supplied", params.length >= 2);
			Log.d(LOG_TAG, "DomainRegistration - doInBackground");
			
			//PARAMETERS
			String client = (String)params[0];
			AService service = (AService) params[1];
			String identity = (String) params[2];
			String method = (String) params[3];
			//RETURN OBJECT
			String results[] = new String[1];
			results[0] = client;
			//MESSAGE BEAN
			ServiceControlMsgBean messageBean = new ServiceControlMsgBean();
			messageBean.setService(AService.convertAService(service));
			messageBean.setShareJid(identity);
			if (method.equals(IServiceControl.SHARE_SERVICE)) 
				messageBean.setMethod(MethodType.SHARE_SERVICE);
			else
				messageBean.setMethod(MethodType.UNSHARE_SERVICE);
			
			//Communications configuration
			ICommCallback discoCallback = new ServiceLifecycleCallback(client, method); 
			IIdentity toID = commMgr.getIdManager().getCloudNode();
			Stanza stanza = new Stanza(toID);
	        try {
	        	commMgr.register(ELEMENT_NAMES, discoCallback);
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, discoCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (Exception e) {
				Log.e(this.getClass().getName(), "ERROR sending message: " + e.getMessage());
	        }	 
			return results;
		}
		
		/**Handle the communication of the result*/
		@Override
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "DomainRegistration - onPostExecute");
	    }
	}

	/**
	 * This class carries out the Stop/Start Service method call asynchronously
	 */
	private class AsyncControlService extends AsyncTask<Object, Void, String[]> {
		
		@Override
		/**Carry out compute task */
		protected String[] doInBackground(Object... params) {
			Dbc.require("At least two parameters must be supplied", params.length >= 2);
			Log.d(LOG_TAG, "DomainRegistration - doInBackground");
			
			//PARAMETERS
			String client = (String)params[0];
			AServiceResourceIdentifier serviceId = (AServiceResourceIdentifier) params[1];
			String method = (String) params[2];
			//RETURN OBJECT
			String results[] = new String[1];
			results[0] = client;
			//MESSAGE BEAN
			ServiceControlMsgBean messageBean = new ServiceControlMsgBean();
			messageBean.setServiceId(AServiceResourceIdentifier.convertAServiceResourceIdentifier(serviceId));
			if (method.equals(IServiceControl.START_SERVICE)) 
				messageBean.setMethod(MethodType.START_SERVICE);
			else
				messageBean.setMethod(MethodType.STOP_SERVICE);
			
			//Communications configuration
			ICommCallback discoCallback = new ServiceLifecycleCallback(client, method); 
			IIdentity toID = commMgr.getIdManager().getCloudNode();
			Stanza stanza = new Stanza(toID);
	        try {
	        	commMgr.register(ELEMENT_NAMES, discoCallback);
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, discoCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (Exception e) {
				Log.e(this.getClass().getName(), "ERROR sending message: " + e.getMessage());
	        }	 
			return results;
		}
		
		/**Handle the communication of the result*/
		@Override
		protected void onPostExecute(String results []) {
			Log.d(LOG_TAG, "DomainRegistration - onPostExecute");
	    }
	}

}

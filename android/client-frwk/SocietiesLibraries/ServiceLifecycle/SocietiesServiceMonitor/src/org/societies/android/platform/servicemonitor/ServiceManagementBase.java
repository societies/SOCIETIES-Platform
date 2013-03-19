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
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.servicecontrol.MethodType;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlMsgBean;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResultBean;
import org.societies.api.schema.servicelifecycle.servicediscovery.MethodName;
import org.societies.api.schema.servicelifecycle.servicediscovery.ServiceDiscoveryMsgBean;
import org.societies.api.schema.servicelifecycle.servicediscovery.ServiceDiscoveryResultBean;

import android.content.Context;
import android.content.Intent;
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
    private boolean connectedToComms = false;
    private boolean restrictBroadcast;
    
    /**DEFAULT CONSTRUCTOR*/
    public ServiceManagementBase(Context androidContext) {
    	this(androidContext, true);
    }
    
    /**
     * constructor
     */
    public ServiceManagementBase(Context androidContext, boolean restrictBroadcast) {
    	Log.d(LOG_TAG, "Object created");
    	
    	this.androidContext = androidContext;
    	this.restrictBroadcast = restrictBroadcast;
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
        	Log.d(LOG_TAG, "ServiceManagementBase startService binding to comms");
	        this.commMgr.bindCommsService(new IMethodCallback() {	
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Connected to comms: " + resultFlag);
					if (resultFlag) {
						ServiceManagementBase.this.connectedToComms = true;
						//REGISTER NAMESPACES
			        	commMgr.register(ELEMENT_NAMES, NAME_SPACES, PACKAGES, new IMethodCallback() {
							@Override
							public void returnAction(boolean resultFlag) {
								Log.d(LOG_TAG, "Namespaces registered: " + resultFlag);
								//SEND INTENT WITH SERVICE STARTED STATUS
				        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
				        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, resultFlag);
				        		ServiceManagementBase.this.androidContext.sendBroadcast(intent);
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
			    		ServiceManagementBase.this.androidContext.sendBroadcast(intent);
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
					ServiceManagementBase.this.connectedToComms = false;
					
					commMgr.unbindCommsService();
					//SEND INTENT WITH SERVICE STOPPED STATUS
	        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
	        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
	        		ServiceManagementBase.this.androidContext.sendBroadcast(intent);
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
			if (restrictBroadcast)
				intent.setPackage(client);
			androidContext.sendBroadcast(intent);
		}
	}
	
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> IServiceDiscovery methods >>>>>>>>>>>>>>>>>>>>>>>
    /* @see org.societies.android.api.internal.servicelifecycle.IServiceDiscovery#getServices(java.lang.String, org.societies.api.identity.IIdentity)*/
	public Service[] getMyServices(final String client) {
		Log.d(LOG_TAG, "getMyServices called by client: " + client);

		if (connectedToComms) {
			//Message Bean
			ServiceDiscoveryMsgBean messageBean = new ServiceDiscoveryMsgBean();
			messageBean.setMethod(MethodName.GET_LOCAL_SERVICES);

			//COMMS CONFIG
			try {
				ICommCallback discoCallback = new ServiceLifecycleCallback(client, GET_MY_SERVICES);
				IIdentity toID = commMgr.getIdManager().getCloudNode();
				Log.e(LOG_TAG, "Cloud Node: " + toID.getJid());
				Stanza stanza = new Stanza(toID);
	        
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, discoCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
        } else {
        	//NOT CONNECTED TO COMMS SERVICE
        	broadcastServiceNotStarted(client, GET_MY_SERVICES);
        }
		return null;
	}
	
	/* @see org.societies.android.api.internal.servicelifecycle.IServiceDiscovery#getServices(java.lang.String, org.societies.api.identity.IIdentity)*/
	public Service[] getServices(final String client, final String identity) {
		Log.d(LOG_TAG, "getServices called by client: " + client);
		
		if (!connectedToComms) {
			//Message Bean
			ServiceDiscoveryMsgBean messageBean = new ServiceDiscoveryMsgBean();
			messageBean.setMethod(MethodName.GET_LOCAL_SERVICES);

			//Communications configuration
			ICommCallback discoCallback = new ServiceLifecycleCallback(client, GET_SERVICES); 
			IIdentity toID;
			try {
				toID = commMgr.getIdManager().fromJid(identity);
				Stanza stanza = new Stanza(toID);
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, discoCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
        } else {
        	//NOT CONNECTED TO COMMS SERVICE
        	broadcastServiceNotStarted(client, GET_SERVICES);
        }
		return null;
	}

	/* @see org.societies.android.api.internal.servicelifecycle.IServiceDiscovery#getService(java.lang.String, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, org.societies.api.identity.IIdentity)*/
	public Service getService(String client, ServiceResourceIdentifier sri, String identity) {
		return null;
	}

	/* @see org.societies.android.api.internal.servicelifecycle.IServiceDiscovery#searchService(java.lang.String, org.societies.api.schema.servicelifecycle.model.Service, org.societies.api.identity.IIdentity) */
	public Service[] searchService(String client, Service filter, String identity) {
		return null;
	}
	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> IServiceControl methods >>>>>>>>>>>>>>>>>>>>>>>
	/* @see org.societies.android.api.internal.servicelifecycle.IServiceControl#installService(java.lang.String, java.net.URL, java.lang.String)*/
	public String installService(String client, URL arg1, String identity) {
		return null;
	}

	/* @see org.societies.android.api.internal.servicelifecycle.IServiceControl#uninstallService(java.lang.String, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, java.lang.String)*/
	public String uninstallService(String client, ServiceResourceIdentifier serviceId, String identity) {
		return null;
	}
	
	/* @see org.societies.android.api.internal.servicelifecycle.IServiceControl#shareService(java.lang.String, org.societies.api.schema.servicelifecycle.model.Service, java.lang.String)*/
	public String shareService(final String client, final Service service, final String identity) {
		Log.d(LOG_TAG, "shareService called by client: " + client);
		serviceSharingHandler(client, service, identity, IServiceControl.SHARE_SERVICE);
		return null;
	}
	
	/*@see org.societies.android.api.internal.servicelifecycle.IServiceControl#unshareService(java.lang.String, org.societies.api.schema.servicelifecycle.model.Service, java.lang.String)*/
	public String unshareService(final String client, final Service service, final String identity) {
		Log.d(LOG_TAG, "unshareService called by client: " + client);
		serviceSharingHandler(client, service, identity, IServiceControl.UNSHARE_SERVICE);
		return null;
	}

	private void serviceSharingHandler(final String client, final Service service, final String identity, final String method) {
		if (connectedToComms) {
			//MESSAGE BEAN
			ServiceControlMsgBean messageBean = new ServiceControlMsgBean();
			messageBean.setService(service);
			messageBean.setShareJid(identity);
			if (method.equals(IServiceControl.SHARE_SERVICE)) 
				messageBean.setMethod(MethodType.SHARE_SERVICE);
			else
				messageBean.setMethod(MethodType.UNSHARE_SERVICE);
			
			//COMMS CONFIG
			try {
				ICommCallback discoCallback = new ServiceLifecycleCallback(client, method); 
				IIdentity toID = commMgr.getIdManager().getCloudNode();
				Stanza stanza = new Stanza(toID);
	        
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, discoCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
	        }
        } else {
        	//NOT CONNECTED TO COMMS SERVICE
        	broadcastServiceNotStarted(client, method);
        }
	}
	
	/* @see org.societies.android.api.internal.servicelifecycle.IServiceControl#startService(java.lang.String, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, java.lang.String) */
	public String startService(final String client, final ServiceResourceIdentifier serviceId) {
		Log.d(LOG_TAG, "startService called by client: " + client);
		serviceControlHandler(client, serviceId, IServiceControl.START_SERVICE);
		return null;
	}

	/* @see org.societies.android.api.internal.servicelifecycle.IServiceControl#stopService(java.lang.String, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, java.lang.String)*/
	public String stopService(final String client, final ServiceResourceIdentifier serviceId) {
		Log.d(LOG_TAG, "stopService called by client: " + client);
		serviceControlHandler(client, serviceId, IServiceControl.STOP_SERVICE);
		return null;
	}

	private void serviceControlHandler(final String client, final ServiceResourceIdentifier serviceId, String method) {
		if (connectedToComms) {
			//MESSAGE BEAN
			ServiceControlMsgBean messageBean = new ServiceControlMsgBean();
			messageBean.setServiceId(serviceId);
			if (method.equals(IServiceControl.START_SERVICE)) 
				messageBean.setMethod(MethodType.START_SERVICE);
			else
				messageBean.setMethod(MethodType.STOP_SERVICE);
			
			//COMMS CONFIG
			try {
				ICommCallback discoCallback = new ServiceLifecycleCallback(client, method); 
				IIdentity toID = commMgr.getIdManager().getCloudNode();
				Stanza stanza = new Stanza(toID);
	        
	        	commMgr.sendIQ(stanza, IQ.Type.GET, messageBean, discoCallback);
				Log.d(LOG_TAG, "Sending stanza");
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Exception sending comms: " + e.getMessage());
			}
	    } else {
	    	//NOT CONNECTED TO COMMS SERVICE
	    	broadcastServiceNotStarted(client, method);
	    }
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
					org.societies.api.schema.servicelifecycle.model.Service serviceArray[] = serviceList.toArray(new org.societies.api.schema.servicelifecycle.model.Service[serviceList.size()]);
					//NOTIFY CALLING CLIENT
					intent.putExtra(IServiceDiscovery.INTENT_RETURN_VALUE, serviceArray);
					if(restrictBroadcast)
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
					if(restrictBroadcast)
						intent.setPackage(client);
				}
				ServiceManagementBase.this.androidContext.sendBroadcast(intent);
			}
		}
	}
	
}

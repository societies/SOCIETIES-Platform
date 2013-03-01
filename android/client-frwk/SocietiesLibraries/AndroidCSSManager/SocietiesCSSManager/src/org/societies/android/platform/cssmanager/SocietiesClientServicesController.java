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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.societies.android.api.cis.directory.ICisDirectory;
import org.societies.android.api.cis.management.ICisManager;
import org.societies.android.api.cis.management.ICisSubscribed;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.css.directory.IAndroidCssDirectory;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.internal.cssmanager.IFriendsManager;
import org.societies.android.api.internal.servicelifecycle.IServiceControl;
import org.societies.android.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.android.api.internal.sns.ISocialData;
import org.societies.android.api.services.ICoreSocietiesServices;
import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.android.platform.css.friends.EventService;
import org.societies.android.platform.css.friends.FriendsManagerLocal;
import org.societies.android.platform.css.friends.FriendsManagerLocal.LocalFriendsManagerBinder;
import org.societies.android.platform.cssmanager.LocalCssDirectoryService.LocalCssDirectoryBinder;
import org.societies.android.platform.servicemonitor.ServiceManagementLocal;
import org.societies.android.platform.servicemonitor.ServiceManagementLocal.LocalSLMBinder;
import org.societies.android.platform.socialdata.SocialData;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Class used to bind to, unbind from, invoke Societies defined methods in {@link IServiceManager}. Any service
 * that needs access to Android Comms and/or Android Comms Pubsub need to included in this class.
 *
 */
public class SocietiesClientServicesController {
	private final static String LOG_TAG = SocietiesClientServicesController.class.getName();
	//timeout for bind, start and stop all services
	private final static long TASK_TIMEOUT = 10000;
	
	private final static int NUM_SERVICES = 13;
	
	private final static int EVENT_SERVICE 				= 0;
	private final static int CIS_DIRECTORY_SERVICE 		= 1;
	private final static int CIS_MANAGER_SERVICE 		= 2;
	private final static int CIS_SUBSCRIBED_SERVICE 	= 3;
	private final static int CSS_DIRECTORY_SERVICE 		= 4;
	private final static int TRUST_SERVICE 				= 5;
	private final static int SLM_SERVICE_CONTROL_SERVICE= 6;
	private final static int PRIVACY_DATA_SERVICE 		= 7;
	private final static int PRIVACY_POLICY_SERVICE 	= 8;
	private final static int SNS_SOCIAL_DATA_SERVICE 	= 9;
	private final static int PERSONALISATION_SERVICE 	= 10;
	private final static int SLM_SERVICE_DISCO_SERVICE 	= 11;
	private final static int FRIENDS_MANAGER_SERVICE 	= 12;
	
	private Context context;
	private CountDownLatch servicesBinded;
	private CountDownLatch servicesStarted;
	private CountDownLatch servicesStopped;

	private BroadcastReceiver receiver;
	
	private boolean connectedToServices[];
	private ServiceConnection platformServiceConnections [];
	private Messenger allMessengers [];
	
	private IAndroidCssDirectory cssDirectoryService;
	private IServiceDiscovery slmServiceDisco;
	private IServiceControl slmServiceControl;
	private ISocialData snsConnectorService;
	private IFriendsManager friendMgrService;
	
	public SocietiesClientServicesController(Context context) {
		this.context = context;
		this.connectedToServices = new boolean[NUM_SERVICES];
		allMessengers = new Messenger[NUM_SERVICES];
		this.platformServiceConnections = new ServiceConnection[NUM_SERVICES];
	}
	
	/**
	 * Bind to the app services. Assumes that login has already taken place
	 * 
	 * @param IMethodCallback callback
	 */
	public void bindToServices(IMethodCallback callback) {
		//set up broadcast receiver for start/bind actions
		setupBroadcastReceiver();

		InvokeBindAllServices invoker = new InvokeBindAllServices(callback);
		invoker.execute();
	}
	/**
	 * Unbind from app services
	 * 
	 */
	public void unbindFromServices() {
	   	Log.d(LOG_TAG, "Unbind from Societies Platform Services");
		
	   	for (int i = 0; i < this.connectedToServices.length; i++) {
			if (this.connectedToServices[i]) {
		        this.context.unbindService(this.platformServiceConnections[i]);
			}
	   	}
	   	//tear down broadcast receiver after stop/unbind actions
	   	this.teardownBroadcastReceiver();
	}
	/**
	 * Start all Societies Client app services
	 * @param callback
	 */
	public void startAllServices(IMethodCallback callback) {
		InvokeStartAllServices invoker = new InvokeStartAllServices(callback);
		invoker.execute();
	}
	/**
	 * Stop all Societies Client app services
	 * @param callback
	 */
	public void stopAllServices(IMethodCallback callback) {
		//set up broadcast receiver for stop/unbind actions
		setupBroadcastReceiver();

		InvokeStopAllServices invoker = new InvokeStopAllServices(callback);
		invoker.execute();
	}
	
	/**
	 * Service Connection objects
	 */
	
    /**
     * Events service connection
     * 
     * N.B. Unbinding from service does not callback. onServiceDisconnected is called back
     * if service connection lost
     */
    private ServiceConnection eventsConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from Platform Events service");
        	SocietiesClientServicesController.this.connectedToServices[EVENT_SERVICE] = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to Platform Events service");

        	SocietiesClientServicesController.this.connectedToServices[EVENT_SERVICE] = true;
        	//get a remote binder
        	SocietiesClientServicesController.this.allMessengers[EVENT_SERVICE] = new Messenger(service);
        	
        	SocietiesClientServicesController.this.platformServiceConnections[EVENT_SERVICE] = this;
        	SocietiesClientServicesController.this.servicesBinded.countDown();
        }
    };

    private ServiceConnection cisDirectoryConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from Platform CIS Directory service");
        	SocietiesClientServicesController.this.connectedToServices[CIS_DIRECTORY_SERVICE] = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to Platform CIS Directory service");

        	SocietiesClientServicesController.this.connectedToServices[CIS_DIRECTORY_SERVICE] = true;
        	//get a remote binder
        	SocietiesClientServicesController.this.allMessengers[CIS_DIRECTORY_SERVICE] = new Messenger(service);
        	
        	SocietiesClientServicesController.this.platformServiceConnections[CIS_DIRECTORY_SERVICE] = this;
        	SocietiesClientServicesController.this.servicesBinded.countDown();
        }
    };
    
    private ServiceConnection cisManagerConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from Platform CIS Manager service");
        	SocietiesClientServicesController.this.connectedToServices[CIS_MANAGER_SERVICE] = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to Platform CIS Manager service");

        	SocietiesClientServicesController.this.connectedToServices[CIS_MANAGER_SERVICE] = true;
        	//get a remote binder
        	SocietiesClientServicesController.this.allMessengers[CIS_MANAGER_SERVICE] = new Messenger(service);
        	
        	SocietiesClientServicesController.this.platformServiceConnections[CIS_MANAGER_SERVICE] = this;
        	SocietiesClientServicesController.this.servicesBinded.countDown();
        }
    };
    
    private ServiceConnection cisSubscribedConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from Platform CIS Subscribed service");
        	SocietiesClientServicesController.this.connectedToServices[CIS_SUBSCRIBED_SERVICE] = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to Platform CIS Subscribed service");

        	SocietiesClientServicesController.this.connectedToServices[CIS_SUBSCRIBED_SERVICE] = true;
        	//get a remote binder
        	SocietiesClientServicesController.this.allMessengers[CIS_SUBSCRIBED_SERVICE] = new Messenger(service);
        	
        	SocietiesClientServicesController.this.platformServiceConnections[CIS_SUBSCRIBED_SERVICE] = this;
        	SocietiesClientServicesController.this.servicesBinded.countDown();
        }
    };
    
    private ServiceConnection cssDirectoryConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from Platform CSS Directory service");
        	SocietiesClientServicesController.this.connectedToServices[CSS_DIRECTORY_SERVICE] = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to Platform CSS Directory service");

        	SocietiesClientServicesController.this.connectedToServices[CSS_DIRECTORY_SERVICE] = true;
        	//Get a local binder
        	LocalCssDirectoryBinder binder = (LocalCssDirectoryBinder) service;
            //Retrieve the local service API
            SocietiesClientServicesController.this.cssDirectoryService = (IAndroidCssDirectory) binder.getService();
        	SocietiesClientServicesController.this.platformServiceConnections[CSS_DIRECTORY_SERVICE] = this;
        	SocietiesClientServicesController.this.servicesBinded.countDown();
        }
    };
    
    //Potential platform services
    private ServiceConnection trustConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from Platform Trust service");
        	SocietiesClientServicesController.this.connectedToServices[TRUST_SERVICE] = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to Platform Trust service");

        	SocietiesClientServicesController.this.connectedToServices[TRUST_SERVICE] = true;
        	//get a remote binder
        	SocietiesClientServicesController.this.allMessengers[TRUST_SERVICE] = new Messenger(service);
        	
        	SocietiesClientServicesController.this.platformServiceConnections[TRUST_SERVICE] = this;
        	SocietiesClientServicesController.this.servicesBinded.countDown();
        }
    };
    
	private ServiceConnection slmDiscoConnection = new ServiceConnection() {
	
		public void onServiceDisconnected(ComponentName name) {
			Log.d(LOG_TAG, "Disconnecting from Platform SLM Service Discovery service");
			SocietiesClientServicesController.this.connectedToServices[SLM_SERVICE_DISCO_SERVICE] = false;
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(LOG_TAG, "Connecting to Platform SLM Service Discovery service");
	
			SocietiesClientServicesController.this.connectedToServices[SLM_SERVICE_DISCO_SERVICE] = true;
	
			//Get a local binder
			LocalSLMBinder binder = (LocalSLMBinder) service;
			//Retrieve the local service API
			SocietiesClientServicesController.this.slmServiceDisco = (IServiceDiscovery) binder.getService();
			SocietiesClientServicesController.this.servicesBinded.countDown();
		}
	};

	private ServiceConnection slmControlConnection = new ServiceConnection() {
	
		public void onServiceDisconnected(ComponentName name) {
			Log.d(LOG_TAG, "Disconnecting from Platform SLM Service Control service");
			SocietiesClientServicesController.this.connectedToServices[SLM_SERVICE_CONTROL_SERVICE] = false;
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(LOG_TAG, "Connecting to Platform SLM Service Control service");
		
			SocietiesClientServicesController.this.connectedToServices[SLM_SERVICE_CONTROL_SERVICE] = true;
		
			//Get a local binder
			LocalSLMBinder binder = (LocalSLMBinder) service;
			//Retrieve the local service API
			SocietiesClientServicesController.this.slmServiceControl = (IServiceControl) binder.getService();
			SocietiesClientServicesController.this.servicesBinded.countDown();
	    }
	};
    
    private ServiceConnection privacyDataConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from Platform Privacy Data service");
        	SocietiesClientServicesController.this.connectedToServices[PRIVACY_DATA_SERVICE] = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to Platform Privacy Data service");

        	SocietiesClientServicesController.this.connectedToServices[PRIVACY_DATA_SERVICE] = true;
        	//get a remote binder
        	SocietiesClientServicesController.this.allMessengers[PRIVACY_DATA_SERVICE] = new Messenger(service);
        	
        	SocietiesClientServicesController.this.platformServiceConnections[PRIVACY_DATA_SERVICE] = this;
        	SocietiesClientServicesController.this.servicesBinded.countDown();
        }
    };
    
    private ServiceConnection privacyPolicyConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from Platform Privacy Policy service");
        	SocietiesClientServicesController.this.connectedToServices[PRIVACY_POLICY_SERVICE] = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to Platform Privacy Policy service");

        	SocietiesClientServicesController.this.connectedToServices[PRIVACY_POLICY_SERVICE] = true;
        	//get a remote binder
        	SocietiesClientServicesController.this.allMessengers[PRIVACY_POLICY_SERVICE] = new Messenger(service);
        	
        	SocietiesClientServicesController.this.platformServiceConnections[PRIVACY_POLICY_SERVICE] = this;
        	SocietiesClientServicesController.this.servicesBinded.countDown();
        }
    };
    
    private ServiceConnection snsSocialDataConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from Platform SNS Social Data service");
        	SocietiesClientServicesController.this.connectedToServices[SNS_SOCIAL_DATA_SERVICE] = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to Platform SNS Social Data service");
    		
        	SocietiesClientServicesController.this.connectedToServices[SNS_SOCIAL_DATA_SERVICE] = true;
		
			//Get a local binder
        	SocialData.LocalBinder binder = (SocialData.LocalBinder) service;
        	//Retrieve the local service API
        	SocietiesClientServicesController.this.snsConnectorService = (ISocialData) binder.getService();
        	SocietiesClientServicesController.this.servicesBinded.countDown();
        }
    };
    
	private ServiceConnection personalisationMgrConnection = new ServiceConnection() {
	
		public void onServiceDisconnected(ComponentName name) {
			Log.d(LOG_TAG, "Disconnecting from Platform Personalisation Manager service");
			SocietiesClientServicesController.this.connectedToServices[PERSONALISATION_SERVICE] = false;
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(LOG_TAG, "Connecting to Platform Personalisation Manager service");
			
			SocietiesClientServicesController.this.connectedToServices[PERSONALISATION_SERVICE] = true;
			//get a remote binder
			SocietiesClientServicesController.this.allMessengers[PERSONALISATION_SERVICE] = new Messenger(service);
			      	
			SocietiesClientServicesController.this.platformServiceConnections[PERSONALISATION_SERVICE] = this;
			SocietiesClientServicesController.this.servicesBinded.countDown();
		}
	};
	
	private ServiceConnection friendsMgrConnection = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			Log.d(LOG_TAG, "Disconnecting from Platform Friends Manager service");
			SocietiesClientServicesController.this.connectedToServices[FRIENDS_MANAGER_SERVICE] = false;
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(LOG_TAG, "Connecting to Platform Friends Manager service");
	
			SocietiesClientServicesController.this.connectedToServices[FRIENDS_MANAGER_SERVICE] = true;
	
			//Get a local binder
			LocalFriendsManagerBinder binder = (LocalFriendsManagerBinder) service;
			//Retrieve the local service API
			SocietiesClientServicesController.this.friendMgrService = (IFriendsManager) binder.getService();
			SocietiesClientServicesController.this.servicesBinded.countDown();
		}
	};
	
//  private ServiceConnection ???Connection = new ServiceConnection() {
//
//      public void onServiceDisconnected(ComponentName name) {
//      	Log.d(LOG_TAG, "Disconnecting from Platform ??? service");
//      	SocietiesClientServicesController.this.connectedToServices[??] = false;
//      }
//
//      public void onServiceConnected(ComponentName name, IBinder service) {
//      	Log.d(LOG_TAG, "Connecting to Platform ??? service");
//
//      	SocietiesClientServicesController.this.connectedToServices[??] = true;
//      	//get a remote binder
//      	SocietiesClientServicesController.this.allMessengers[??] = new Messenger(service);
//      	
//      	SocietiesClientServicesController.this.platformServiceConnections[??] = this;
//      	SocietiesClientServicesController.this.servicesBinded.countDown();
//      }
//  };
    
    
    /**
     * AsyncTasks to carry out asynchronous processing
     */
    
	/**
     * 
     * Async task to bind to all relevant Societies Client app services
     *
     */
    private class InvokeBindAllServices extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeBindAllServices.class.getName();
    	private IMethodCallback callback;
   	 /**
   	 * Default Constructor
   	 * @param IMethodCallback callback
   	 */
    	public InvokeBindAllServices(IMethodCallback callback) {
    		this.callback = callback;
    	}

    	protected Void doInBackground(Void... args) {

    		SocietiesClientServicesController.this.servicesBinded = new CountDownLatch(NUM_SERVICES);
    		
    		boolean retValue = true;
    		//REMOTE PLATFORM SERVICES
    		Log.d(LOCAL_LOG_TAG, "Bind to Societies Android Events Service");
        	Intent serviceIntent = new Intent(ICoreSocietiesServices.EVENTS_SERVICE_INTENT);
        	SocietiesClientServicesController.this.context.bindService(serviceIntent, eventsConnection, Context.BIND_AUTO_CREATE);

        	Log.d(LOCAL_LOG_TAG, "Bind to Societies Android CIS Directory Service");
        	serviceIntent = new Intent(ICoreSocietiesServices.CIS_DIRECTORY_SERVICE_INTENT);
        	SocietiesClientServicesController.this.context.bindService(serviceIntent, cisDirectoryConnection, Context.BIND_AUTO_CREATE);

        	Log.d(LOCAL_LOG_TAG, "Bind to Societies Android CIS Manager Service");
        	serviceIntent = new Intent(ICoreSocietiesServices.CIS_MANAGER_SERVICE_INTENT);
        	SocietiesClientServicesController.this.context.bindService(serviceIntent, cisManagerConnection, Context.BIND_AUTO_CREATE);

        	Log.d(LOCAL_LOG_TAG, "Bind to Societies Android CIS Subscribed Service");
        	serviceIntent = new Intent(ICoreSocietiesServices.CIS_MANAGER_SERVICE_INTENT);
        	SocietiesClientServicesController.this.context.bindService(serviceIntent, cisSubscribedConnection, Context.BIND_AUTO_CREATE);

        	//LOCAL PLATFORM SERVICES
        	Log.d(LOCAL_LOG_TAG, "Bind to Societies Android CSS Directory Service");
        	serviceIntent = new Intent(SocietiesClientServicesController.this.context, LocalCssDirectoryService.class);
        	SocietiesClientServicesController.this.context.bindService(serviceIntent, cssDirectoryConnection, Context.BIND_AUTO_CREATE);

        	Log.d(LOCAL_LOG_TAG, "Bind to Societies Android SLM Service Discovery Service");
        	serviceIntent = new Intent(SocietiesClientServicesController.this.context, ServiceManagementLocal.class);
        	SocietiesClientServicesController.this.context.bindService(serviceIntent, slmDiscoConnection, Context.BIND_AUTO_CREATE);
        	
        	Log.d(LOCAL_LOG_TAG, "Bind to Societies Android SLM Service Control Service");
        	serviceIntent = new Intent(SocietiesClientServicesController.this.context, ServiceManagementLocal.class);
        	SocietiesClientServicesController.this.context.bindService(serviceIntent, slmControlConnection, Context.BIND_AUTO_CREATE);

        	Log.d(LOCAL_LOG_TAG, "Bind to Societies Android SNS Connectors Service");
        	serviceIntent = new Intent(SocietiesClientServicesController.this.context, SocialData.class);
        	SocietiesClientServicesController.this.context.bindService(serviceIntent, snsSocialDataConnection, Context.BIND_AUTO_CREATE);
        	
        	Log.d(LOCAL_LOG_TAG, "Bind to Societies Friends Manager Service");
        	serviceIntent = new Intent(SocietiesClientServicesController.this.context, FriendsManagerLocal.class);
        	SocietiesClientServicesController.this.context.bindService(serviceIntent, friendsMgrConnection, Context.BIND_AUTO_CREATE);
        	
//    		Log.d(LOCAL_LOG_TAG, "Bind to Societies Android Personalisation Manager Service");
//        	serviceIntent = new Intent(ICoreSocietiesServices.PERSONALISATION_SERVICE_INTENT);
//        	SocietiesClientServicesController.this.context.bindService(serviceIntent, personalisationMgrConnection, Context.BIND_AUTO_CREATE);
            
        	try {
        		//To prevent hanging this latch uses a timeout
        		SocietiesClientServicesController.this.servicesBinded.await(TASK_TIMEOUT, TimeUnit.MILLISECONDS);
    		} catch (InterruptedException e) {
    			retValue = false;
    			e.printStackTrace();
    		} finally {
    			callback.returnAction(retValue);
    		}

    		return null;
    	}
    }
    
	/**
     * 
     * Async task to start all relevant Societies Client app services
     *
     */
    private class InvokeStartAllServices extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeStartAllServices.class.getName();
    	private IMethodCallback callback;

   	 /**
   	 * Default Constructor
   	 * @param serviceMessenger
   	 */
    	public InvokeStartAllServices(IMethodCallback callback) {
    		this.callback = callback;
    	}

    	protected Void doInBackground(Void... args) {
    		SocietiesClientServicesController.this.servicesStarted = new CountDownLatch(NUM_SERVICES);
    		
    		boolean retValue = true;
    		//Start remote platform services
    		for (int i  = 0; i < SocietiesClientServicesController.this.allMessengers.length; i++) {
    			
    			if (null != SocietiesClientServicesController.this.allMessengers[i]) {
            		String targetMethod = IServiceManager.methodsArray[0];
            		android.os.Message outMessage = getRemoteMessage(targetMethod, i);
               		Bundle outBundle = new Bundle();
               		outMessage.setData(outBundle);
            		Log.d(LOCAL_LOG_TAG, "Call service start method: " + targetMethod);

            		try {
            			SocietiesClientServicesController.this.allMessengers[i].send(outMessage);
        			} catch (RemoteException e) {
        				Log.e(LOCAL_LOG_TAG, "Unable to start service, index: " + i, e);
        			}
    			}
     		}
    		//Start local platform services
    		SocietiesClientServicesController.this.cssDirectoryService.startService();
    		SocietiesClientServicesController.this.slmServiceDisco.startService();
    		SocietiesClientServicesController.this.slmServiceControl.startService();
    		SocietiesClientServicesController.this.snsConnectorService.startService();
    		SocietiesClientServicesController.this.friendMgrService.startService();
    		
    		//START "STARTED SERVICES"
        	//FRIENDS SERVICE
            Intent intentFriends = new Intent(SocietiesClientServicesController.this.context, EventService.class);
            SocietiesClientServicesController.this.context.startService(intentFriends);
            //USER AGENT SERVICE
            //Intent intentUserAgent = new Intent(SocietiesClientServicesController.this.context, UserAgent.class);
            //SocietiesClientServicesController.this.context.startService(intentUserAgent);
    		try {
				SocietiesClientServicesController.this.servicesStarted.await(TASK_TIMEOUT, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				retValue = false;
				e.printStackTrace();
			} finally {
    			callback.returnAction(retValue);
    			//tear down broadcast receiver after initial bind/start actions
    		   	SocietiesClientServicesController.this.teardownBroadcastReceiver();
    		}

    		return null;
    	}
    }
	/**
     * 
     * Async task to stop all relevant Societies Client app services
     *
     */
    private class InvokeStopAllServices extends AsyncTask<Void, Void, Void> {

    	private final String LOCAL_LOG_TAG = InvokeStopAllServices.class.getName();
    	private IMethodCallback callback;

   	 /**
   	 * Default Constructor
   	 * @param serviceMessenger
   	 */
    	public InvokeStopAllServices(IMethodCallback callback) {
    		this.callback = callback;
    	}

    	protected Void doInBackground(Void... args) {
    		SocietiesClientServicesController.this.servicesStopped = new CountDownLatch(NUM_SERVICES);
    		
    		boolean retValue = true;
    		
    		//STOP REMOTE SERVICES
    		for (int i  = 0; i < SocietiesClientServicesController.this.allMessengers.length; i++) {
    			if (null != SocietiesClientServicesController.this.allMessengers[i]) {
            		String targetMethod = IServiceManager.methodsArray[1];
            		android.os.Message outMessage = getRemoteMessage(targetMethod, i);
               		Bundle outBundle = new Bundle();
               		outMessage.setData(outBundle);

            		Log.d(LOCAL_LOG_TAG, "Call service stop method: " + targetMethod);

            		try {
            			SocietiesClientServicesController.this.allMessengers[i].send(outMessage);
        			} catch (RemoteException e) {
        				Log.e(LOCAL_LOG_TAG, "Unable to stop service, index: " + i, e);
        			}
    			}
     		}

    		//STOP LOCAL SERVICES
    		SocietiesClientServicesController.this.cssDirectoryService.stopService();
    		SocietiesClientServicesController.this.slmServiceDisco.stopService();
    		SocietiesClientServicesController.this.slmServiceControl.stopService();
    		SocietiesClientServicesController.this.snsConnectorService.stopService();
    		SocietiesClientServicesController.this.friendMgrService.stopService();
    		//STOP "STARTED SERVICES"
        	//FRIENDS SERVICE
            Intent intentFriends = new Intent(SocietiesClientServicesController.this.context, EventService.class);
            SocietiesClientServicesController.this.context.stopService(intentFriends);
            //USER AGENT SERVICE
            //Intent intentUserAgent = new Intent(SocietiesClientServicesController.this.context, UserAgent.class);
            //SocietiesClientServicesController.this.context.stopService(intentUserAgent);   		
    		try {
				SocietiesClientServicesController.this.servicesStopped.await(TASK_TIMEOUT, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				retValue = false;
				e.printStackTrace();
			} finally {
    			callback.returnAction(retValue);
    		}

    		return null;
    	}
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     * Essentially this receiver invokes callbacks for relevant intents received from Android Communications. 
     * Since more than one instance of this class can exist for an app, i.e. more than one component could be communicating, 
     * callback IDs cannot be assumed to exist for a particular Broadcast receiver.
     */
    private class MainReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "Received action: " + intent.getAction());

			if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STOPPED_STATUS)) {
				//As each service stops decrement the latch
				SocietiesClientServicesController.this.servicesStopped.countDown();
				
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {
				//As each service starts decrement the latch
				SocietiesClientServicesController.this.servicesStarted.countDown();
				
			} else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_EXCEPTION_INFO)) {
				
			} 
		}
    }
    
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private BroadcastReceiver setupBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up broadcast receiver");
        
        this.receiver = new MainReceiver();
        this.context.registerReceiver(this.receiver, createIntentFilter());    
        Log.d(LOG_TAG, "Register broadcast receiver");

        return receiver;
    }
    /**
     * Unregister the broadcast receiver
     */
    private void teardownBroadcastReceiver() {
        Log.d(LOG_TAG, "Tear down broadcast receiver");
    	this.context.unregisterReceiver(this.receiver);
    }


    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
        intentFilter.addAction(IServiceManager.INTENT_SERVICE_EXCEPTION_INFO);
        return intentFilter;
    }
    /**
     * Create the correct message for remote method invocation
     * 
     * @param targetMethod
     * @param index
     * @return android.os.Message
     */
    private android.os.Message getRemoteMessage(String targetMethod, int index) {
		android.os.Message retValue = null;
		
		switch (index) {
		case EVENT_SERVICE:
			retValue = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);
			break;
		case CIS_DIRECTORY_SERVICE:
			retValue = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(ICisDirectory.methodsArray, targetMethod), 0, 0);
			break;
		case CIS_MANAGER_SERVICE:
			retValue = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(ICisManager.methodsArray, targetMethod), 0, 0);
			break;
		case CIS_SUBSCRIBED_SERVICE:
			retValue = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(ICisSubscribed.methodsArray, targetMethod), 0, 0);
			break;
//		case ???_SERVICE:
//			retValue = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(???.methodsArray, targetMethod), 0, 0);
//			break;
		default:
		}
		return retValue;
    }
}

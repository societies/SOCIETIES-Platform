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

import android.content.*;
import android.os.*;
import android.util.Log;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.api.services.ICoreSocietiesServices;
import org.societies.android.api.utilities.ServiceMethodTranslator;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Class used to bind to, unbind from, invoke Societies defined methods in {@link IServiceManager}. Any essential service
 * that needs access to Android Comms and/or Android Comms Pubsub need to included in this class. Essential services
 * are those that must be started prior to all other platform services as they are dependencies.
 */
public class SocietiesEssentialServicesController {
    private final static String LOG_TAG = SocietiesEssentialServicesController.class.getCanonicalName();
    //timeout for bind, start and stop all services
    private final static long TASK_TIMEOUT = 10000;

    private final static int NUM_SERVICES = 1;

    private final static int EVENT_SERVICE = 0;

    private Context context;
    private CountDownLatch servicesBinded;
    private CountDownLatch servicesStarted;
    private CountDownLatch servicesStopped;

    private BroadcastReceiver startupReceiver;
    private BroadcastReceiver shutdownReceiver;

    private boolean connectedToServices[];
    private ServiceConnection platformServiceConnections[];
    private Messenger allMessengers[];

    private long startTime;

    public SocietiesEssentialServicesController(Context context) {
        this.context = context;
        this.connectedToServices = new boolean[NUM_SERVICES];
        allMessengers = new Messenger[NUM_SERVICES];
        this.platformServiceConnections = new ServiceConnection[NUM_SERVICES];
        this.startupReceiver = null;
        this.shutdownReceiver = null;
    }

    /**
     * Bind to the app services. Assumes that login has already taken place
     */
    public void bindToServices(IMethodCallback callback) {
        //set up broadcast receiver for start/bind actions
        setupStartupBroadcastReceiver();

        InvokeBindAllServices invoker = new InvokeBindAllServices(callback);
        invoker.execute();
    }

    /**
     * Unbind from app essential services
     */
    public void unbindFromServices() {
        Log.d(LOG_TAG, "Unbind from Societies Essentials Platform Service(s)");

        for (int i = 0; i < this.connectedToServices.length; i++) {
            if (this.connectedToServices[i]) {
                this.context.unbindService(this.platformServiceConnections[i]);
            }
        }
        //tear down broadcast receiver after stop/unbind actions
        this.teardownBroadcastReceiver(this.shutdownReceiver);
    }

    /**
     * Start all Societies Client essential app services
     */
    public void startAllServices(IMethodCallback callback) {
        InvokeStartAllServices invoker = new InvokeStartAllServices(callback);
        invoker.execute();
    }

    /**
     * Stop all Societies Client app services
     */
    public void stopAllServices(IMethodCallback callback) {
        //set up broadcast receiver for stop/unbind actions
        setupShutdownBroadcastReceiver();

        InvokeStopAllServices invoker = new InvokeStopAllServices(callback);
        invoker.execute();
    }

    /**
     * Service Connection objects
     */

    /**
     * Events service connection
     * <p/>
     * N.B. Unbinding from service does not callback. onServiceDisconnected is called back
     * if service connection lost
     */
    private ServiceConnection eventsConnection = new ServiceConnection() {
        final static String SERVICE_NAME = "Platform Events";

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG_TAG, "Disconnecting from " + SERVICE_NAME + " service");
            SocietiesEssentialServicesController.this.connectedToServices[EVENT_SERVICE] = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(LOG_TAG, "Connecting to " + SERVICE_NAME + " service");

            SocietiesEssentialServicesController.this.connectedToServices[EVENT_SERVICE] = true;
            //get a remote binder
            SocietiesEssentialServicesController.this.allMessengers[EVENT_SERVICE] = new Messenger(service);

            SocietiesEssentialServicesController.this.platformServiceConnections[EVENT_SERVICE] = this;
            SocietiesEssentialServicesController.this.servicesBinded.countDown();
            Log.d(LOG_TAG, "Time to bind to " + SERVICE_NAME + " service: " + Long.toString(System.currentTimeMillis() - SocietiesEssentialServicesController.this.startTime));
        }
    };

    /**
     * AsyncTasks to carry out asynchronous processing
     */

    /**
     * Async task to bind to all relevant Societies Client app services
     */
    private class InvokeBindAllServices extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeBindAllServices.class.getCanonicalName();
        private IMethodCallback callback;

        /**
         * Default Constructor
         */
        public InvokeBindAllServices(IMethodCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... args) {

            SocietiesEssentialServicesController.this.servicesBinded = new CountDownLatch(NUM_SERVICES);

            SocietiesEssentialServicesController.this.startTime = System.currentTimeMillis();

            boolean retValue = true;

            //Remote Platform Services
            Log.d(LOCAL_LOG_TAG, "Bind to Societies Android Events Service");
            Intent serviceIntent = new Intent(ICoreSocietiesServices.EVENTS_SERVICE_INTENT);
            SocietiesEssentialServicesController.this.context.bindService(serviceIntent, eventsConnection, Context.BIND_AUTO_CREATE);


            try {
                //To prevent hanging this latch uses a timeout
                SocietiesEssentialServicesController.this.servicesBinded.await(TASK_TIMEOUT, TimeUnit.MILLISECONDS);
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
     * Async task to start all relevant Societies Client app services
     */
    private class InvokeStartAllServices extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeStartAllServices.class.getCanonicalName();
        private IMethodCallback callback;

        /**
         * Default Constructor
         */
        public InvokeStartAllServices(IMethodCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... args) {
            SocietiesEssentialServicesController.this.servicesStarted = new CountDownLatch(NUM_SERVICES);

            SocietiesEssentialServicesController.this.startTime = System.currentTimeMillis();

            boolean retValue = true;
            //Start remote platform services
            for (int i = 0; i < SocietiesEssentialServicesController.this.allMessengers.length; i++) {

                if (null != SocietiesEssentialServicesController.this.allMessengers[i]) {
                    String targetMethod = IServiceManager.methodsArray[0];
                    android.os.Message outMessage = getRemoteMessage(targetMethod, i);
                    Bundle outBundle = new Bundle();
                    outMessage.setData(outBundle);
                    Log.d(LOCAL_LOG_TAG, "Call service start method: " + targetMethod);

                    try {
                        SocietiesEssentialServicesController.this.allMessengers[i].send(outMessage);
                    } catch (RemoteException e) {
                        Log.e(LOCAL_LOG_TAG, "Unable to start service, index: " + i, e);
                    }
                }
            }

            try {
                SocietiesEssentialServicesController.this.servicesStarted.await(TASK_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                retValue = false;
                e.printStackTrace();
            } finally {
                callback.returnAction(retValue);
                //tear down broadcast receiver after initial bind/start actions
                SocietiesEssentialServicesController.this.teardownBroadcastReceiver(SocietiesEssentialServicesController.this.startupReceiver);
            }

            return null;
        }
    }

    /**
     * Async task to stop all relevant Societies Client app services
     */
    private class InvokeStopAllServices extends AsyncTask<Void, Void, Void> {

        private final String LOCAL_LOG_TAG = InvokeStopAllServices.class.getCanonicalName();
        private IMethodCallback callback;

        /**
         * Default Constructor
         */
        public InvokeStopAllServices(IMethodCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... args) {
            SocietiesEssentialServicesController.this.servicesStopped = new CountDownLatch(NUM_SERVICES);

            SocietiesEssentialServicesController.this.startTime = System.currentTimeMillis();

            boolean retValue = true;

            //Stop remote platform services
            for (int i = 0; i < SocietiesEssentialServicesController.this.allMessengers.length; i++) {
                if (null != SocietiesEssentialServicesController.this.allMessengers[i]) {
                    String targetMethod = IServiceManager.methodsArray[1];
                    android.os.Message outMessage = getRemoteMessage(targetMethod, i);
                    Bundle outBundle = new Bundle();
                    outMessage.setData(outBundle);

                    Log.d(LOCAL_LOG_TAG, "Call service stop method: " + targetMethod);

                    try {
                        SocietiesEssentialServicesController.this.allMessengers[i].send(outMessage);
                    } catch (RemoteException e) {
                        Log.e(LOCAL_LOG_TAG, "Unable to stop service, index: " + i, e);
                    }
                }
            }

            try {
                SocietiesEssentialServicesController.this.servicesStopped.await(TASK_TIMEOUT, TimeUnit.MILLISECONDS);
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
    private class EssentialServicesStartupReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "Received action: " + intent.getAction());

            if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STARTED_STATUS)) {

                //As each service starts decrement the latch
                if (null != SocietiesEssentialServicesController.this.servicesStarted) {
                    Log.d(LOG_TAG, "Time to start service: " + Long.toString(System.currentTimeMillis() - SocietiesEssentialServicesController.this.startTime));
                    SocietiesEssentialServicesController.this.servicesStarted.countDown();
                }

            } else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_EXCEPTION_INFO)) {

            }
        }
    }

    /**
     * Broadcast receiver to receive intent return values from service method calls
     * Essentially this receiver invokes callbacks for relevant intents received from Android Communications.
     * Since more than one instance of this class can exist for an app, i.e. more than one component could be communicating,
     * callback IDs cannot be assumed to exist for a particular Broadcast receiver.
     */
    private class EssentialServicesShutdownReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "Received action: " + intent.getAction());

            if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_STOPPED_STATUS)) {
                //As each service stops decrement the latch
                Log.d(LOG_TAG, "Time to stop service: " + Long.toString(System.currentTimeMillis() - SocietiesEssentialServicesController.this.startTime));
                if (null != SocietiesEssentialServicesController.this.servicesStopped) {
                    SocietiesEssentialServicesController.this.servicesStopped.countDown();
                }

            } else if (intent.getAction().equals(IServiceManager.INTENT_SERVICE_EXCEPTION_INFO)) {
            }
        }
    }


    /**
     * Create a services startup broadcast receiver
     */
    private void setupStartupBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up startup broadcast receiver");

        this.startupReceiver = new EssentialServicesStartupReceiver();
        this.context.registerReceiver(this.startupReceiver, createIntentFilter());
        Log.d(LOG_TAG, "Register broadcast receiver");
    }

    /**
     * Create a services shutdown broadcast receiver
     */
    private void setupShutdownBroadcastReceiver() {
        Log.d(LOG_TAG, "Set up shutdown broadcast receiver");

        this.shutdownReceiver = new EssentialServicesShutdownReceiver();
        this.context.registerReceiver(this.shutdownReceiver, createIntentFilter());
        Log.d(LOG_TAG, "Register broadcast receiver");
    }

    /**
     * Unregister the broadcast receiver
     */
    private void teardownBroadcastReceiver(BroadcastReceiver receiver) {
        Log.d(LOG_TAG, "Tear down broadcast receiver");
        this.context.unregisterReceiver(receiver);
    }


    /**
     * Create a suitable intent filter
     *
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
     */
    private android.os.Message getRemoteMessage(String targetMethod, int index) {
        android.os.Message retValue = null;

        switch (index) {
            case EVENT_SERVICE:
                retValue = android.os.Message.obtain(null, ServiceMethodTranslator.getMethodIndex(IAndroidSocietiesEvents.methodsArray, targetMethod), 0, 0);
                break;
            default:
        }
        return retValue;
    }
}

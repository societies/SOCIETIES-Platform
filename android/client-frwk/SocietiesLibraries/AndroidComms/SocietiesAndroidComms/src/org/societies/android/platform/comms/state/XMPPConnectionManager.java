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

package org.societies.android.platform.comms.state;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.societies.utilities.DBC.Dbc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Manages the Societies Android Communications XMPP connection with a finite state machine (FSM)
 *
 */

	
public class XMPPConnectionManager implements IConnectionState {
	private final static String LOG_TAG = XMPPConnectionManager.class.getName();
	private final static boolean DEBUG_LOGGING = true;
	private final static String KEY_DIVIDER = ":"; 
	
	private Context context;
	private ConnectionState currentState;
	private static XMPPConnectionManager xmppManager;
	private XMPPConnection xmppConnection;
	private Map <String, StateEventAction> fsmLookupTable;
	private ConcurrentLinkedQueue <ConnectionEvent> eventQueue;
	private boolean eventPending;
	private final static ScheduledExecutorService queueSchedular = Executors.newSingleThreadScheduledExecutor();

	private XMPPConnectionProperties currentXmppConnectProps;
	private XMPPConnectionProperties newXmppConnectProps;
	private BroadcastReceiver bReceiver;
	
	private Class<?> actionHandlerParams [] = {ConnectionState.class};
	
    /**
     * This getter creates the XmppManager and inits the XmppManager
     * with a new connection with the current preferences.
     * TODO: Not threadsafe
     * @param ctx
     * @return
     */
    public static XMPPConnectionManager getInstance(Context ctx) {
        if (xmppManager == null) {
        	xmppManager = new XMPPConnectionManager(ctx);
        }
        return xmppManager;
    }

	private XMPPConnectionManager(Context context) {
		this.context = context;
		this.currentState = IConnectionState.ConnectionState.Disconnected;
		
		this.eventQueue = new ConcurrentLinkedQueue<ConnectionEvent>();
		this.eventPending = false;
		this.xmppConnection = null;
		this.currentXmppConnectProps = null;
		this.newXmppConnectProps = null;
		
		this.fsmLookupTable = new HashMap<String, StateEventAction>();
		this.populateFSMtable();
	}
	
	@Override
	public boolean isConnected() {
		return this.xmppConnection.isConnected() && (this.currentState.equals(ConnectionState.Connected));
	}

	@Override
	public ConnectionState getConnectionState() {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "getConnectionState: " + this.currentState);
		}

		return this.currentState;
	}
	
	@Override
	public void enableConnection(XMPPConnectionProperties connectProps) {
		Dbc.require("Connection properties cannot be null", null != connectProps);
		
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "enableConnection : " + connectProps.toString());
		}
		this.newXmppConnectProps = connectProps;
		if (null == this.currentXmppConnectProps) {
			this.currentXmppConnectProps = connectProps;
		}
		this.startEventProcessor();
		this.addEventToQueue(ConnectionEvent.Start);

	}
	@Override
	public void disableConnection() {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "disableConnection");
		}
		this.addEventToQueue(ConnectionEvent.Disconnect);
	}
	
	/**
	 * Start a connection
	 */
	private void startEventProcessor() {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "startEventProcessor");
		}
		
		final Runnable processQueue = new Runnable() {
			
			@Override
			public void run() {
				if (DEBUG_LOGGING) {
					Log.d(LOG_TAG, "event processor running");
				}
				
				while (null != XMPPConnectionManager.this.eventQueue.peek()) {
					ConnectionEvent pendingEvent = XMPPConnectionManager.this.eventQueue.poll();
					XMPPConnectionManager.this.processQueueEvent(pendingEvent);
				}
			}
		};
		final ScheduledFuture processQueueTimer = queueSchedular.scheduleAtFixedRate(processQueue, 0, 100, TimeUnit.MILLISECONDS);
	}
	/**
	 * End a connection
	 */
	public void endConnection() {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "endConnection");
		}
		this.addEventToQueue(ConnectionEvent.Disconnect);
	}
	
	/**
	 * Update the status of the FSM and send intent to interested receivers
	 * 
	 * @param state
	 */
	private void updateStatus(ConnectionState newState) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "updateStatus from: " + this.currentState.name() + " to : " + newState.name());
		}
		ConnectionState formerState = this.currentState;
		this.currentState = newState;
		this.sendIntent(formerState, newState);
	}
	/**
	 * Send an intent of the current state
	 * @param formerState
	 * @param currentState
	 */
	private void sendIntent(ConnectionState formerState, ConnectionState currentState) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "sendIntent");
		}
		Intent intent = new Intent(IConnectionState.XMPP_CONNECTION_CHANGED);
		intent.putExtra(IConnectionState.INTENT_FORMER_CONNECTION_STATE, formerState.ordinal());
		intent.putExtra(IConnectionState.INTENT_CURRENT_CONNECTION_STATE, currentState.ordinal());
		boolean connected = currentState.equals(ConnectionState.Connected);
		intent.putExtra(IConnectionState.INTENT_CONNECTED, connected);
		this.context.sendBroadcast(intent);
	}
	/**
	 * Create an {@link XMPPConnection} listener to monitor the connection status 
	 */
	private void createConnectionListener() {
		Dbc.invariant("XMPP connection cannot be null", null != this.xmppConnection);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "createConnectionListener");
		}
		
		ConnectionListener connListener = new ConnectionListener() {
			
			@Override
			public void reconnectionSuccessful() {
				if (DEBUG_LOGGING) {
					Log.d(LOG_TAG, "XMPP connection reconnection attempt successful");
				}
				XMPPConnectionManager.this.addEventToQueue(ConnectionEvent.AutoReconnected);
			}
			
			@Override
			public void reconnectionFailed(Exception e) {
				if (DEBUG_LOGGING) {
					Log.d(LOG_TAG, "XMPP connection reconnection attempt failed due to: " + e.getMessage());
				}
				XMPPConnectionManager.this.addEventToQueue(ConnectionEvent.AttemptAutoReconnection);
			}
			
			@Override
			public void reconnectingIn(int seconds) {
				if (DEBUG_LOGGING) {
					Log.d(LOG_TAG, "XMPP connection attempting reconnection in " + seconds + " seconds");
				}
				//TODO: may have to examine timespan to determine if reconnection attempts futile
			}
			
			@Override
			public void connectionClosedOnError(Exception e) {
				if (DEBUG_LOGGING) {
					Log.d(LOG_TAG, "XMPP connection closed with error: " + e.getMessage());
				}
				//TODO: may have to examine error and determine if the ReConnectionmanager will kick in
				XMPPConnectionManager.this.addEventToQueue(ConnectionEvent.Disconnect);
			}
			
			@Override
			public void connectionClosed() {
				if (DEBUG_LOGGING) {
					Log.d(LOG_TAG, "XMPP connection closed");
				}
				XMPPConnectionManager.this.addEventToQueue(ConnectionEvent.Disconnect);
			}
		};
		this.xmppConnection.addConnectionListener(connListener);
	}
	
	private static String generateKey(ConnectionState currentState, ConnectionEvent event) {
		return currentState.name() + KEY_DIVIDER + event.name();
	}
	/**
	 * Class that encapsulates the action to take and the next state that the FSM will be in
	 * after the action handler has been invoked.
	 *
	 */
	private class StateEventAction {
		private ConnectionAction actionHandler;
		private ConnectionState futureState;

		public StateEventAction(ConnectionAction actionHandler, ConnectionState futureState) {
			this.actionHandler = actionHandler;
			this.futureState = futureState;
		}
		
		public ConnectionAction getActionHandler() {
			return actionHandler;
		}
		public void setActionHandler(ConnectionAction actionHandler) {
			this.actionHandler = actionHandler;
		}
		public ConnectionState getFutureState() {
			return futureState;
		}
		public void setFutureState(ConnectionState futureState) {
			this.futureState = futureState;
		}
	}
	/**
	 * populate the FSM lookuptable
	 * 
	 * The table is modelled as a Map with the current state and event acting as the key and a StateEventAction object as
	 * the Map pair object. As a result, knowing the current state and the event allows the action handler to be retrieved 
	 * and the resulting state to be set on the successful completion of the handler
	 */
	private void populateFSMtable() {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "Creating FSM lookup table");
		}
		this.fsmLookupTable.put(generateKey(ConnectionState.Disconnected, ConnectionEvent.Start),
				new StateEventAction(ConnectionAction.startConnection, ConnectionState.WaitForNetwork));
		
		this.fsmLookupTable.put(generateKey(ConnectionState.WaitForNetwork, ConnectionEvent.CheckForConnectivity),
				new StateEventAction(ConnectionAction.verifyNetworkConnectivity, ConnectionState.ValidNetwork));
		
		this.fsmLookupTable.put(generateKey(ConnectionState.WaitForNetwork, ConnectionEvent.NoNetworkFound),
				new StateEventAction(ConnectionAction.cleanupConnection, ConnectionState.Disconnected));
		
		this.fsmLookupTable.put(generateKey(ConnectionState.ValidNetwork, ConnectionEvent.AttemptConnection),
				new StateEventAction(ConnectionAction.initialiseConnection, ConnectionState.WaitingToConnect));
		
		this.fsmLookupTable.put(generateKey(ConnectionState.WaitingToConnect, ConnectionEvent.AttemptConnectionSuccess),
				new StateEventAction(ConnectionAction.verifyAuthenticatedConnection, ConnectionState.Connected));
		
		this.fsmLookupTable.put(generateKey(ConnectionState.WaitingToConnect, ConnectionEvent.AttemptConnectionFailure),
				new StateEventAction(ConnectionAction.cleanupConnection, ConnectionState.Disconnected));
		
		this.fsmLookupTable.put(generateKey(ConnectionState.Connected, ConnectionEvent.Disconnect),
				new StateEventAction(ConnectionAction.cleanupConnection, ConnectionState.Disconnected));
		
		this.fsmLookupTable.put(generateKey(ConnectionState.Connected, ConnectionEvent.AttemptAutoReconnection),
				new StateEventAction(ConnectionAction.updateStatus, ConnectionState.ReConnecting));
		
		this.fsmLookupTable.put(generateKey(ConnectionState.ReConnecting, ConnectionEvent.AutoReconnected),
				new StateEventAction(ConnectionAction.updateStatus, ConnectionState.Connected));
		
//			this.fsmLookupTable.put(this.generateKey(ConnectionState.???, ConnectionEvent.xxx),
//					new StateEventAction(ConnectionAction.zzz, ConnectionState.fff));
		
		//Log the lookup table
//		StateEventAction eventAction = null;
//		for (String key: this.fsmLookupTable.keySet()) {
//			eventAction = this.fsmLookupTable.get(key);
//			Log.d(LOG_TAG, "Key: " + key + " Action: " + eventAction.getActionHandler() + "Next State: " + eventAction.getFutureState().name());
//		}
	}
	
	private void addEventToQueue(ConnectionEvent event) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "addEventToQueue: " + event.name());
		}
		this.eventQueue.add(event);
	}
	
	private void processQueueEvent(ConnectionEvent event) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "processQueueEvent for state: " + this.currentState.name() + " and event: " + event.name());
		}
		StateEventAction object = this.fsmLookupTable.get(generateKey(this.currentState, event));
		if (null != object) {
			String handlerName = object.getActionHandler().name();
			
			if (null != handlerName && handlerName.length() > 0) {
				try {
					Method actionHandler = this.getClass().getMethod(object.getActionHandler().name(), this.actionHandlerParams);
					
					Object params[] = {object.getFutureState()};
					try {
						actionHandler.invoke(this, params);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * Get things rolling. Declared as public to allow reflection.
	 * 
	 * @param futureState
	 */
	public void startConnection(ConnectionState futureState) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "startConnection: " + futureState.name());
		}
		this.updateStatus(futureState);
		this.addEventToQueue(ConnectionEvent.CheckForConnectivity);
	}
	/**
	 * Create the XMPP connection and authenticate
	 * @return true if connection and authentication successful
	 * @throws XMPPException
	 */
	private void authenticateConnection() throws XMPPException {
		Dbc.invariant("XMPP connection cannot be null", null != this.xmppConnection);
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "authenticateConnection");
		}

		boolean retValue = false;
		
		if (!(this.xmppConnection.isConnected() && this.xmppConnection.isAuthenticated())) {
				this.xmppConnection.connect();
				
				this.xmppConnection.login(this.newXmppConnectProps.getUserName(),
										  this.newXmppConnectProps.getPassword(), 
										  this.newXmppConnectProps.getNodeResource());
		}
	}
	
	/**
	 * Verify that the established authenticated connection is in a connected state
	 * and add a {@link ConnectionListener} to monitor the connection.
	 * Declared as public to allow reflection.
	 * @param futureState
	 */
	public void verifyAuthenticatedConnection(ConnectionState futureState) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "verifyAuthenticatedConnection : " + futureState.name());
		}
		if (this.xmppConnection.isConnected() && this.xmppConnection.isAuthenticated()) {
			this.updateStatus(futureState);
			this.createConnectionListener();
			this.setupBroadcastReceiver();
		}

	}
	/**
	 * Initialise the XMPPConnection. Declared as public to allow refection.
	 * 
	 * @param futureState
	 */
	public void initialiseConnection(ConnectionState futureState) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "initialiseConnection : " + futureState.name());
		}
		this.updateStatus(futureState);

		if (!this.currentXmppConnectProps.equals(this.newXmppConnectProps) ||
				null == this.xmppConnection) {
			this.xmppConnection = createNewXMPPConnection(this.newXmppConnectProps);
			try {
				this.authenticateConnection();
				this.addEventToQueue(ConnectionEvent.AttemptConnectionSuccess);
			} catch (XMPPException x) {
				if (DEBUG_LOGGING){
					Log.d(LOG_TAG, "Unable to authenticate connection");
					this.addEventToQueue(ConnectionEvent.AttemptConnectionFailure);
				}
			}
		}
	}
	
	private void teardownConnection() {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "teardownConnection");
		}
		if (null != this.xmppConnection && this.xmppConnection.isConnected()) {
			this.xmppConnection.disconnect();
		} else {
			this.xmppConnection = null;
		}
	}
	/**
	 * Carry out clean up tasks required. Declared as public to allow reflection.
	 * 
	 * @param futureState
	 */
	public void cleanupConnection(ConnectionState futureState) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "cleanupConnection: " + futureState.name());
		}
		this.stopExecutorTask();
		this.teardownBroadcastReceiver();
		teardownConnection();
		this.updateStatus(futureState);
	}
	
	/**
	 * Stop the event queue consumer executor thread
	 */
	private void stopExecutorTask() {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "stopExecutorTask");
		}
		queueSchedular.shutdown();
	}
	/**
	 * Verify that a network exists and is connected. Declared as public to allow reflection.
	 * 
	 * @param futureState
	 */
	public void verifyNetworkConnectivity(ConnectionState futureState) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "verifyNetworkConnectivity: " + futureState.name());
		}
		ConnectivityManager cm =
		        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		
		if (null != activeNetwork && activeNetwork.getState().equals(NetworkInfo.State.CONNECTED)) {
			this.updateStatus(futureState);
			this.addEventToQueue(ConnectionEvent.AttemptConnection);
		} else {
			this.addEventToQueue(ConnectionEvent.NoNetworkFound);
		}
	}
	
	/**
	 * Create a new XMPPConnection object according to the supplied connection configuration properties
	 * 
	 * @param connectProps Connection configuration properties
	 * @return {@link XMPPConnection}
	 */
	private XMPPConnection createNewXMPPConnection(XMPPConnectionProperties connectProps) {
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "createNewXMPPConnection for : " + connectProps.toString());
		}
		XMPPConnection connection = null;
		
		if (DEBUG_LOGGING) {
			Log.d(LOG_TAG, "Using configuration: " + connectProps.toString());
		};
		
		ConnectionConfiguration config = new ConnectionConfiguration(connectProps.getValidHost(),
											 						 connectProps.getServicePort(),
											 						 connectProps.getServiceName());
		connection = new XMPPConnection(config);
		
		if(connectProps.isDebug()) {
			this.xmppConnection.addPacketListener(new PacketListener() {
	
				public void processPacket(Packet packet) {
					if (DEBUG_LOGGING) {
						Log.d(LOG_TAG, "Packet received: " + packet.toXML());
					};
				}
				
			}, new PacketFilter() {
	
				public boolean accept(Packet packet) {
					return true;
				}
			});
			connection.addPacketSendingListener(new PacketListener() {
	
				public void processPacket(Packet packet) {
					if (DEBUG_LOGGING) {
						Log.d(LOG_TAG, "Packet sent: " + packet.toXML());
					};
				}
				
			}, new PacketFilter() {
	
				public boolean accept(Packet packet) {
					return true;
				}
			});
		}
		return connection;
	}
	
    /**
     * Broadcast receiver to receive intent return values from ConnectionManager
     * 
     * TODO: If base API increases extra notification information can be displayed
     * in the more detailed notification style
     */
    private class AndroidCommsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

				boolean unConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

				if (unConnected) {
					ConnectivityManager cm =
					        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
					 
					NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
					
					String networkType = null;
					
					if (null != activeNetwork) {
						networkType = activeNetwork.getTypeName();
					}
					String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
					String extraInfo = intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO);
					boolean failover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
					
					addEventToQueue(ConnectionEvent.NoNetworkFound);
				}
			}
		}
    }
    /**
     * Create a suitable intent filter
     * @return IntentFilter
     */
    private IntentFilter createIntentFilter() {
    	//register broadcast receiver to receive SocietiesEvents return values 
        IntentFilter intentFilter = new IntentFilter();
        
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        return intentFilter;
    }
    /**
     * Create a broadcast receiver
     * 
     * @return the created broadcast receiver
     */
    private void setupBroadcastReceiver() {
		if (DEBUG_LOGGING) {
	        Log.d(LOG_TAG, "Set up connectivity changes broadcast receiver");
		};
        
        this.bReceiver = new AndroidCommsReceiver();
        this.context.registerReceiver(this.bReceiver, createIntentFilter());    
		if (DEBUG_LOGGING) {
	        Log.d(LOG_TAG, "Register connectivity changes broadcast receiver");
		};
    }
    /**
     * Unregister the broadcast receiver
     */
    private void teardownBroadcastReceiver() {
		if (DEBUG_LOGGING) {
		       Log.d(LOG_TAG, "Tear down connectivity changes broadcast receiver");
		};
    	this.context.unregisterReceiver(this.bReceiver);
    }
    
}

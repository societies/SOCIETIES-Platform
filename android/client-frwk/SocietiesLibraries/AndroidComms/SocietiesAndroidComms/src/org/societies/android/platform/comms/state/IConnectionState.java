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

import org.jivesoftware.smack.XMPPConnection;

import android.content.Context;

/**
 * XMPP Finite State Machine Events and States
 *
 */
public interface IConnectionState {

	enum ConnectionEvent {
		Start, CheckForConnectivity, AttemptConnection, AttemptConnectionFailure, 
		AttemptConnectionSuccess, Disconnect, AttemptAutoReconnection, AutoReconnected, NoNetworkFound, ConnectionBroken
	}
	
	enum ConnectionState {Disconnected, WaitForNetwork, ValidNetwork, WaitingToConnect, Connected, ReConnecting
	}
	
	enum ConnectionAction {startConnection, updateStatus, startEventProcessor, verifyNetworkConnectivity, cleanupConnection, 
		initialiseConnection, verifyAuthenticatedConnection, manualReconnection
	}
	//Finite State Machine state changed intent and extra information
	public static final String XMPP_CONNECTION_CHANGED = "org.societies.android.platform.comms.state.XMPP_CONNECTION_CHANGED";
	public static final String INTENT_CURRENT_CONNECTION_STATE = "org.societies.android.platform.comms.state.INTENT_CURRENT_CONNECTION_STATE";
	public static final String INTENT_FORMER_CONNECTION_STATE = "org.societies.android.platform.comms.state.INTENT_FORMER_CONNECTION_STATE";
	public static final String INTENT_CONNECTED = "org.societies.android.platform.comms.state.INTENT_CONNECTED";
	public static final int INVALID_INTENT_INTEGER_EXTRA_VALUE = -999;

	//Possible Login failure points and extra information
	public static final String XMPP_NO_NETWORK_FOUND_FAILURE = "org.societies.android.platform.comms.state.XMPP_NO_NETWORK_FOUND";
	public static final String XMPP_CONNECTIVITY_FAILURE = "org.societies.android.platform.comms.state.XMPP_CONNECTIVITY_FAILURE";
	public static final String XMPP_AUTHENTICATION_FAILURE = "org.societies.android.platform.comms.state.XMPP_AUTHENTICATION_FAILURE";
	public static final String INTENT_FAILURE_DESCRIPTION = "org.societies.android.platform.comms.state.INTENT_FAILURE_DESCRIPTION";
	
	public static final String NO_NETWORK_FOUND_MESSAGE = "No network found to establish connection"; 
	public static final String CONNECTIVITY_FAILURE_MESSAGE = "Unable to establish XMPP connection"; 
	public static final String AUTHENTICATION_FAILURE_MESSAGE = "User credentials are invalid"; 

	//Common intent extras
	public static final String INTENT_REMOTE_CALL_ID = "org.societies.android.platform.comms.state.INTENT_REMOTE_CALL_ID";
	public static final String INTENT_REMOTE_CALL_CLIENT = "org.societies.android.platform.comms.state.INTENT_REMOTE_CALL_CLIENT";
	public static final String INTENT_REMOTE_USER_JID = "org.societies.android.platform.comms.state.INTENT_REMOTE_USER_JID";


	boolean isConnected();

	boolean isAuthenticated();
	
	ConnectionState getConnectionState();
	
	void enableConnection(XMPPConnectionProperties connectProps, Context context, String userJid, String client, long remoteCallId);
	
	void disableConnection(String client, long remoteCallId);
	
	XMPPConnection getValidConnection() throws NoXMPPConnectionAvailableException;
	
}
	


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
package org.societies.android.privacytrust.policymanagement;

import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.android.privacytrust.datamanagement.PrivacyDataManagerRemote;
import org.societies.android.privacytrust.policymanagement.callback.PrivacyPolicyIntentSender;
import org.societies.android.privacytrust.policymanagement.callback.RemotePrivacyPolicyCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.PrivacyPolicyManagerBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


/**
 * @author Olivier Maridat (Trialog)
 * @date 5 déc. 2011
 */
public class PrivacyPolicyManagerRemote {
	private final static String TAG = PrivacyPolicyManagerRemote.class.getSimpleName();
	private static final List<String> ELEMENT_NAMES = Arrays.asList("privacyPolicyManagerBean", "privacyPolicyManagerBeanResult"); // /!\ First letter in lowercase
	private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/internal/schema/privacytrust/privacyprotection/privacypolicymanagement",
			"http://societies.org/api/schema/privacytrust/privacy/model/privacypolicy",
			"http://societies.org/api/internal/schema/privacytrust/privacyprotection/model/privacypolicy",
			"http://societies.org/api/schema/identity", 
			"http://societies.org/api/schema/servicelifecycle/model");
	private static final List<String> PACKAGES = Arrays.asList("org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement",
			"org.societies.api.schema.privacytrust.privacy.model.privacypolicy",
			"org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy",
			"org.societies.api.schema.identity",
			"org.societies.api.schema.servicelifecycle.model");

	private Context context;
	private ClientCommunicationMgr clientCommManager;
	private PrivacyPolicyIntentSender intentSender;
	private static boolean remoteReady;


	public PrivacyPolicyManagerRemote(Context context)  {
		Log.d(TAG, "PrivacyPolicyManagerRemote Constructor");
		this.context = context;
		clientCommManager = new ClientCommunicationMgr(context, true);
		intentSender = new PrivacyPolicyIntentSender(context);
		remoteReady = false;
		bindToComms();
	}


	public boolean getPrivacyPolicy(String clientPackage, RequestorBean owner) throws PrivacyException {
		String action = MethodType.GET_PRIVACY_POLICY.name();
		try {
			// -- Verify status
			if (!checkRemoteStatus(clientPackage, action)) {
				return false;
			}
			// -- Destination
			IIdentity cloudNode = getOwnerJid(owner);
			Stanza stanza = new Stanza(cloudNode);
			Log.d(TAG, "Send "+action+" to "+cloudNode.getJid());

			// -- Message
			PrivacyPolicyManagerBean messageBean = new PrivacyPolicyManagerBean();
			messageBean.setMethod(MethodType.GET_PRIVACY_POLICY);
			messageBean.setRequestor(owner);

			// -- Send
			RemotePrivacyPolicyCallback callback = new RemotePrivacyPolicyCallback(context, clientPackage, ELEMENT_NAMES, NAME_SPACES, PACKAGES);
			clientCommManager.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(TAG, "Send stanza PrivacyPolicyManagerBean::"+action);
		} catch (InvalidFormatException e) {
			Log.e(TAG, "Unexepected invalid format error: "+(null != e ? e.getMessage() : ""));
			intentSender.sendIntentError(clientPackage, action, "Error: don't know who to contact");
			return false;
		}
		catch (CommunicationException e) {
			Log.e(TAG, "Unexepected comm error: "+(null != e ? e.getMessage() : ""));
			intentSender.sendIntentError(clientPackage, action, "Error during the sending of remote request");
			return false;
		}
		catch (Exception e) {
			Log.e(TAG, "Unexepected error: "+(null != e ? e.getMessage() : ""));
			intentSender.sendIntentError(clientPackage, action, "Unknown remote remote");
			return false;
		} 
		return true;
	}

	private IIdentity getOwnerJid(RequestorBean owner) throws InvalidFormatException {
		// CIS: contact the CIS owner
		if (owner instanceof RequestorCisBean) {
			return clientCommManager.getIdManager().fromJid(owner.getRequestorId());
		}
		// 3P service: our cloud node know the privacy Policy
		return clientCommManager.getIdManager().getCloudNode();
	}


	public boolean updatePrivacyPolicy(String clientPackage, RequestPolicy privacyPolicy) throws PrivacyException {
		String action = MethodType.UPDATE_PRIVACY_POLICY.name();
		try {
			// -- Verify status
			if (!checkRemoteStatus(clientPackage, action)) {
				return false;
			}
			// -- Destination
			INetworkNode cloudNode = clientCommManager.getIdManager().getCloudNode();
			Stanza stanza = new Stanza(cloudNode);
			Log.d(TAG, "Send "+MethodType.UPDATE_PRIVACY_POLICY.name()+" to "+cloudNode.getJid());

			// -- Message
			PrivacyPolicyManagerBean messageBean = new PrivacyPolicyManagerBean();
			messageBean.setMethod(MethodType.UPDATE_PRIVACY_POLICY);
			messageBean.setPrivacyPolicy(privacyPolicy);

			// -- Send
			RemotePrivacyPolicyCallback callback = new RemotePrivacyPolicyCallback(context, clientPackage, ELEMENT_NAMES, NAME_SPACES, PACKAGES);
			clientCommManager.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(TAG, "Send stanza PrivacyPolicyManagerBean::" + action);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			intentSender.sendIntentError(clientPackage, MethodType.UPDATE_PRIVACY_POLICY.name(), "Error during the sending of remote request");
			return false;
		}
		return true;
	}

	public boolean deletePrivacyPolicy(String clientPackage, RequestorBean owner) throws PrivacyException {
		String action = MethodType.DELETE_PRIVACY_POLICY.name();
		try {
			// -- Verify status
			if (!checkRemoteStatus(clientPackage, action)) {
				return false;
			}
			// -- Destination
			INetworkNode cloudNode = clientCommManager.getIdManager().getCloudNode();
			Stanza stanza = new Stanza(cloudNode);
			Log.d(TAG, "Send "+MethodType.DELETE_PRIVACY_POLICY.name()+" to "+cloudNode.getJid());

			// -- Message
			PrivacyPolicyManagerBean messageBean = new PrivacyPolicyManagerBean();
			messageBean.setMethod(MethodType.DELETE_PRIVACY_POLICY);
			messageBean.setRequestor(owner);

			// -- Send
			RemotePrivacyPolicyCallback callback = new RemotePrivacyPolicyCallback(context, clientPackage, ELEMENT_NAMES, NAME_SPACES, PACKAGES);
			clientCommManager.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(TAG, "Send stanza PrivacyPolicyManagerBean::" + action);
		} catch (Exception e) {
			Log.e(TAG, "Unexepected error: "+(null != e ? e.getMessage() : ""));
			intentSender.sendIntentError(clientPackage, MethodType.DELETE_PRIVACY_POLICY.name(), "Error during the sending of remote request");
			return false;
		}
		return true;
	}


	// -- Comms

	public void bindToComms() {
		if (!remoteReady) {
			//NOT CONNECTED TO COMMS SERVICE YET
			Log.d(TAG, "PrivacyPolicyManagerRemote startService binding to comms");
			this.clientCommManager.bindCommsService(new IMethodCallback() {	
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(TAG, "Connected to comms: " + resultFlag);
					if (resultFlag) {
						remoteReady = true;
						//REGISTER NAMESPACES
						clientCommManager.register(ELEMENT_NAMES, NAME_SPACES, PACKAGES, new IMethodCallback() {
							@Override
							public void returnAction(boolean resultFlag) {
								Log.d(TAG, "Namespaces registered: " + resultFlag);
								//SEND INTENT WITH SERVICE STARTED STATUS
								Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
								intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, resultFlag);
								intent.putExtra("type", "PrivacyPolicyManager");
								context.sendBroadcast(intent);
							}
							@Override
							public void returnAction(String result) {
								Log.d(TAG, "Register to comm: " + result);
							}
							@Override
							public void returnException(String result) {
								Log.e(TAG, "Error during comm registration: " + result);
								remoteReady = false;
								//SEND INTENT WITH SERVICE STARTED STATUS
								Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
								intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, remoteReady);
								intent.putExtra("type", "PrivacyPolicyManager");
								context.sendBroadcast(intent);
							}

						});
					} else {
						Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
						intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false);
						intent.putExtra("type", "PrivacyPolicyManager");
						context.sendBroadcast(intent);
					}
				}	
				@Override
				public void returnAction(String result) { 
					Log.d(TAG, "Connected to comms: " + result);
				}
				@Override
				public void returnException(String result) {
					// TODO Auto-generated method stub

				}

			});
		}
		else {
			Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
			intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
			intent.putExtra("type", "PrivacyPolicyManager");
			this.context.sendBroadcast(intent);
		}
	}

	public void unbindFromComms() {
		if (remoteReady) {
			//UNREGISTER AND DISCONNECT FROM COMMS
			Log.d(TAG, "PrivacyPolicyManagerRemote stopService unregistering namespaces");
			clientCommManager.unregister(ELEMENT_NAMES, NAME_SPACES, new IMethodCallback() {
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(TAG, "Unregistered namespaces: " + resultFlag);
					remoteReady = false;

					clientCommManager.unbindCommsService();
					//SEND INTENT WITH SERVICE STOPPED STATUS
					Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
					intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
					intent.putExtra("type", "PrivacyPolicyManager");
					context.sendBroadcast(intent);
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
			intent.putExtra("type", "PrivacyPolicyManager");
			this.context.sendBroadcast(intent);
		}
	}

	/**
	 * To know if the remote access is ready or not
	 * @return True when ready, false otherwize. In the latter case, @see{bindToComms} have to be called.
	 */
	public boolean isRemoteReady() {
		return remoteReady;
	}

	/**
	 * Check that this component is bound to the Societies
	 * @param clientPackage Client package name
	 * @param action Action requested
	 * @return True if the process can continue, False otherwise
	 */
	private boolean checkRemoteStatus(String clientPackage, String action) {
		if (!isRemoteReady()) {
			intentSender.sendIntentErrorServiceNotStarted(clientPackage, action);
			return false;
		}
		return true;
	}
}

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
package org.societies.android.privacytrust.datamanagement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.cis.directory.ICisDirectory;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.identity.util.DataIdentifierUtils;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.android.privacytrust.datamanagement.callback.PrivacyDataIntentSender;
import org.societies.android.privacytrust.datamanagement.callback.RemotePrivacyDataCallback;
import org.societies.android.privacytrust.policymanagement.callback.PrivacyPolicyIntentSender;
import org.societies.api.identity.INetworkNode;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBean;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManagerRemote {
	private final static String TAG = PrivacyDataManagerRemote.class.getSimpleName();

	private static final List<String> ELEMENT_NAMES = Arrays.asList("privacyDataManagerBean", "privacyDataManagerBeanResult"); // /!\ First letter in lowercase
	private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/internal/schema/privacytrust/privacyprotection/privacydatamanagement",
			"http://societies.org/api/schema/privacytrust/privacy/model/privacypolicy",
			"http://societies.org/api/internal/schema/privacytrust/model/dataobfuscation",
			"http://societies.org/api/internal/schema/privacytrust/privacyprotection/model/privacypolicy",
			"http://societies.org/api/schema/identity", 
			"http://societies.org/api/schema/servicelifecycle/model");
	private static final List<String> PACKAGES = Arrays.asList("org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement",
			"org.societies.api.schema.privacytrust.privacy.model.privacypolicy",
			"org.societies.api.internal.schema.privacytrust.model.dataobfuscation",
			"org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy",
			"org.societies.api.schema.identity",
			"org.societies.api.schema.servicelifecycle.model");

	private Context context;
	private ClientCommunicationMgr clientCommManager;
	private PrivacyDataIntentSender intentSender;
	private static boolean remoteReady;


	public PrivacyDataManagerRemote(Context context)  {
		this.context = context;
		clientCommManager = new ClientCommunicationMgr(context, true);
		intentSender = new PrivacyDataIntentSender(context);
		remoteReady = false;
		bindToComms();
	}


	// -- Access control

	public void checkPermission(String clientPackage, RequestorBean requestor, List<DataIdentifier> dataIds, List<Action> actions) throws PrivacyException {
		String action = MethodType.CHECK_PERMISSION.name();
		try {
			// -- Verify status
			if (!checkRemoteStatus(clientPackage, action)) {
				return;
			}
			// -- Destination
			INetworkNode cloudNode = clientCommManager.getIdManager().getCloudNode();
			Stanza stanza = new Stanza(cloudNode);
			Log.d(TAG, "Send "+action+" to "+cloudNode.getJid());

			// -- Message
			PrivacyDataManagerBean messageBean = new PrivacyDataManagerBean();
			messageBean.setMethod(MethodType.CHECK_PERMISSION);
			messageBean.setRequestor(requestor);
			List<String> dataUris = new ArrayList<String>();
			for(DataIdentifier dataId : dataIds) {
				dataUris.add(DataIdentifierUtils.toUriString(dataId));
			}
			messageBean.setDataIdUris(dataUris);
			messageBean.setActions(actions);

			// -- Send
			RemotePrivacyDataCallback callback = new RemotePrivacyDataCallback(context, clientPackage, ELEMENT_NAMES, NAME_SPACES, PACKAGES);
			clientCommManager.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(TAG, "Sent stanza PrivacyDataManagerBean: "+action);
		} catch (Exception e) {
			Log.e(TAG, "Unexepected error: "+(null != e ? e.getMessage() : ""));
			intentSender.sendIntentError(clientPackage, action, "Error during the sending of remote request");
		}
	}

	// -- Obfuscation

	public void obfuscateData(String clientPackage, RequestorBean requestor, DataWrapper dataWrapper) throws PrivacyException {
		String action = MethodType.OBFUSCATE_DATA.name();
		try {
			// -- Verify status
			if (!checkRemoteStatus(clientPackage, action)) {
				return;
			}
			// -- Destination
			INetworkNode cloudNode = clientCommManager.getIdManager().getCloudNode();
			Stanza stanza = new Stanza(cloudNode);
			Log.d(TAG, "Send "+action+" to "+cloudNode.getJid());

			// -- Message
			PrivacyDataManagerBean messageBean = new PrivacyDataManagerBean();
			messageBean.setMethod(MethodType.OBFUSCATE_DATA);
			messageBean.setRequestor(requestor);
			messageBean.setDataWrapper(dataWrapper);

			// -- Send
			RemotePrivacyDataCallback callback = new RemotePrivacyDataCallback(context, clientPackage, ELEMENT_NAMES, NAME_SPACES, PACKAGES);
			clientCommManager.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(TAG, "Sent stanza PrivacyDataManagerBean: "+action);
		} catch (Exception e) {
			Log.e(TAG, "Unexepected error: "+(null != e ? e.getMessage() : ""));
			intentSender.sendIntentError(clientPackage, action, "Error during the sending of remote request");
		}
	}

	// -- Comms

	public void bindToComms() {
		if (!remoteReady) {
			//NOT CONNECTED TO COMMS SERVICE YET
			Log.d(TAG, "PrivacyDataManagerRemote startService binding to comms");
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
								intent.putExtra("type", "PrivacyDataManager");
								PrivacyDataManagerRemote.this.context.sendBroadcast(intent);
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
								intent.putExtra("type", "PrivacyDataManager");
								PrivacyDataManagerRemote.this.context.sendBroadcast(intent);
							}

						});
					} else {
						Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
						intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false);
						intent.putExtra("type", "PrivacyDataManager");
						PrivacyDataManagerRemote.this.context.sendBroadcast(intent);
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
			intent.putExtra("type", "PrivacyDataManager");
			this.context.sendBroadcast(intent);
		}
	}

	public void unbindFromComms() {
		if (remoteReady) {
			//UNREGISTER AND DISCONNECT FROM COMMS
			Log.d(TAG, "PrivacyDataManagerRemote stopService unregistering namespaces");
			clientCommManager.unregister(ELEMENT_NAMES, NAME_SPACES, new IMethodCallback() {
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(TAG, "Unregistered namespaces: " + resultFlag);
					remoteReady = false;

					clientCommManager.unbindCommsService();
					//SEND INTENT WITH SERVICE STOPPED STATUS
					Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
					intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
					intent.putExtra("type", "PrivacyDataManager");
					PrivacyDataManagerRemote.this.context.sendBroadcast(intent);
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

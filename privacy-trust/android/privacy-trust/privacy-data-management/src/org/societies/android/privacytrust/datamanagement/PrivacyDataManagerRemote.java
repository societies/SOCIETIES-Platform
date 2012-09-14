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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.internal.privacytrust.IPrivacyDataManager;
import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.android.api.internal.privacytrust.privacyprotection.model.privacypolicy.AAction;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBeanResult;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManagerRemote implements IPrivacyDataManager {
	private final static String TAG = PrivacyDataManagerRemote.class.getSimpleName();

	private static final List<String> ELEMENT_NAMES = Arrays.asList("PrivacyDataManagerBean", "PrivacyDataManagerBeanResult");
	private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/internal/schema/privacytrust/privacyprotection/privacydatamanagement",
			"http://societies.org/api/internal/schema/privacytrust/privacyprotection/model/privacypolicy",
			"http://societies.org/api/schema/identity", 
			"http://societies.org/api/schema/servicelifecycle/model");
	private static final List<String> PACKAGES = Arrays.asList("org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement",
			"org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy",
			"org.societies.api.schema.identity",
			"org.societies.api.schema.servicelifecycle.model");


	private Context context;
	private ClientCommunicationMgr clientCommManager;
	private final ICommCallback callback = createCallback();
	private ResponseItem permission;


	public PrivacyDataManagerRemote(Context context)  {
		this.context = context;
	}


	/*
	 * (non-Javadoc)
	 * @see org.societies.android.api.internal.privacytrust.IPrivacyDataManager#checkPermission(org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.identity.DataIdentifier, java.util.List)
	 */
	public ResponseItem checkPermission(RequestorBean requestor, DataIdentifier dataId, AAction[] actions) throws PrivacyException {
		// -- Verify parameters
		if (null == requestor) {
			Log.e(TAG, "verifyParemeters: Not enought information: requestor is missing");
			throw new NullPointerException("Not enought information: requestor is missing");
		}
		if (null == dataId) {
			Log.e(TAG, "verifyParemeters: Not enought information: data id is missing");
			throw new NullPointerException("Not enought information: data id is missing");
		}

		// Send remote call
		RemoteTask task = new RemoteTask(context); 
		List<AAction> actionsList = Arrays.asList(actions);
		task.execute(requestor, dataId, actionsList);

		// Wait and retrieve the result
		ResponseItem permission = null;
		try {
			permission = task.get(10000, TimeUnit.MILLISECONDS);
		}
		catch(InterruptedException e) {
			Log.e(TAG, "Interruption: can't retrieve the result - "+e.getMessage(), e);
		}
		catch(ExecutionException e) {
			Log.e(TAG, "Execution: can't retrieve the result - "+e.getMessage(), e);
		} catch (TimeoutException e) {
			Log.e(TAG, "Execution: can't retrieve the result due to timeout - "+e.getMessage(), e);
		}
		return permission;
	}

	/* ********************
	 * Sender
	 **********************/
	private class RemoteTask extends AsyncTask<Object, Void, ResponseItem> {
		private Context context;

		public RemoteTask(Context context) {
			this.context = context;
		}

		protected ResponseItem doInBackground(Object... args) {
			Log.d(TAG, "Try to send "+MethodType.CHECK_PERMISSION.name());
			clientCommManager = new ClientCommunicationMgr(context);

			// -- Destination
			INetworkNode cloudNode = clientCommManager.getIdManager().getCloudNode();
			Stanza stanza = new Stanza(cloudNode);
			Log.d(TAG, "to "+cloudNode.getJid());

			// -- Message
			PrivacyDataManagerBean messageBean = new PrivacyDataManagerBean();
			messageBean.setMethod(MethodType.CHECK_PERMISSION);
			messageBean.setRequestor((RequestorBean) args[0]);
			messageBean.setDataIdUri(((DataIdentifier) args[1]).getUri());
			messageBean.setActions((List<Action>) args[2]);

			// -- Send
			try {
				clientCommManager.register(ELEMENT_NAMES, callback);
				clientCommManager.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
				Log.d(TAG, "Send stanza PrivacyDataManagerBean::"+MethodType.CHECK_PERMISSION.name());
				callback.wait(10000);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			return permission;
		}
	}

	/* ************************
	 * Receiver
	 **************************/
	private ICommCallback createCallback() {
		return new ICommCallback() {
			public void receiveResult(Stanza stanza, Object payload) {
				Log.d(TAG, "receiveResult");
				Log.d(TAG, "Payload class of type: " + payload.getClass().getName());
				if (payload instanceof PrivacyDataManagerBeanResult) {
					PrivacyDataManagerBeanResult resultBean = (PrivacyDataManagerBeanResult) payload;
					MethodType methodType = resultBean.getMethod();
					permission = resultBean.getPermission();
				}
				debugStanza(stanza);
				// Tell upper layer that received is finished
				notifyAll();
			}

			public void receiveError(Stanza stanza, XMPPError error) {
				Log.d(TAG, "receiveError");
			}

			public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
				Log.d(TAG, "receiveInfo");
			}

			public void receiveMessage(Stanza stanza, Object payload) {
				Log.d(TAG, "receiveMessage");
				debugStanza(stanza);
			}

			public void receiveItems(Stanza stanza, String node, List<String> items) {
				Log.d(TAG, "receiveItems");
				debugStanza(stanza);
				Log.d(TAG, "node: "+node);
				Log.d(TAG, "items:");
				for(String  item:items)
					Log.d(TAG, item);
			}

			private void debugStanza(Stanza stanza) {
				Log.d(TAG, "id="+stanza.getId());
				Log.d(TAG, "from="+stanza.getFrom());
				Log.d(TAG, "to="+stanza.getTo());
			}

			public List<String> getXMLNamespaces() {
				return NAME_SPACES;
			}
			public List<String> getJavaPackages() {
				return PACKAGES;
			}
		};
	}

	// -- Obfuscation
	public IDataWrapper obfuscateData(RequestorBean requestor, IDataWrapper dataWrapper) throws PrivacyException {
		Log.i(TAG, "Remote obfuscation not available yet.");
		return dataWrapper;
	}
	public DataIdentifier hasObfuscatedVersion(RequestorBean requestor, IDataWrapper dataWrapper) throws PrivacyException {
		Log.i(TAG, "Remote obfuscation not available yet.");
		return dataWrapper.getDataId();
	}
}

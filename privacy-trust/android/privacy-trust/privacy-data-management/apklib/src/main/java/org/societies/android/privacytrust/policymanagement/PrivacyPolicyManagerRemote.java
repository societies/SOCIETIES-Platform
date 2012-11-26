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
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager;
import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.android.privacytrust.policymanagement.callback.RemotePrivacyPolicyCallback;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement.PrivacyPolicyManagerBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


/**
 * @author Olivier Maridat (Trialog)
 * @date 5 déc. 2011
 */
public class PrivacyPolicyManagerRemote implements IPrivacyPolicyManager {
	private final static String TAG = PrivacyPolicyManagerRemote.class.getSimpleName();
	private static final List<String> ELEMENT_NAMES = Arrays.asList("PrivacyPolicyManagerBean", "PrivacyPolicyManagerBeanResult");
	private static final List<String> NAME_SPACES = Arrays.asList("http://societies.org/api/internal/schema/privacytrust/privacyprotection/privacypolicymanagement",
			"http://societies.org/api/internal/schema/privacytrust/privacyprotection/model/privacypolicy",
			"http://societies.org/api/schema/identity", 
			"http://societies.org/api/schema/servicelifecycle/model");
	private static final List<String> PACKAGES = Arrays.asList("org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement",
			"org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy",
			"org.societies.api.schema.identity",
			"org.societies.api.schema.servicelifecycle.model");

	private Context context;
	private ClientCommunicationMgr clientCommManager;


	public PrivacyPolicyManagerRemote(Context context)  {
		this.context = context;
	}


	public RequestPolicy getPrivacyPolicy(RequestorBean requestor) throws PrivacyException {
		// -- Verify
		if (null == requestor || null == requestor.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy. Requestor needed.");
		}

		// Send remote call
		GetRemotePrivacyPolicyTask task = new GetRemotePrivacyPolicyTask(context); 
		task.execute(requestor);

		// Wait and retrieve the result
		RequestPolicy privacyPolicy = null;
		try {
			privacyPolicy = task.get();
		}
		catch(InterruptedException e) {
			Log.e(TAG, "Interruption: can't retrieve the result - "+e.getMessage(), e);
		}
		catch(ExecutionException e) {
			Log.e(TAG, "Execution: can't retrieve the result - "+e.getMessage(), e);
		}
		return privacyPolicy;
	}
	private class GetRemotePrivacyPolicyTask extends AsyncTask<Object, Void, RequestPolicy> {
		private Context context;

		public GetRemotePrivacyPolicyTask(Context context) {
			this.context = context;
		}

		protected RequestPolicy doInBackground(Object... args) {
			clientCommManager = new ClientCommunicationMgr(context);

			// -- Destination
			INetworkNode cloudNode = clientCommManager.getIdManager().getCloudNode();
			Stanza stanza = new Stanza(cloudNode);

			// -- Message
			PrivacyPolicyManagerBean messageBean = new PrivacyPolicyManagerBean();
			messageBean.setMethod(MethodType.GET_PRIVACY_POLICY);
			messageBean.setRequestor((RequestorBean) args[0]);

			// -- Send
			RemotePrivacyPolicyCallback callback = new RemotePrivacyPolicyCallback(ELEMENT_NAMES, NAME_SPACES, PACKAGES);
			try {
				clientCommManager.register(ELEMENT_NAMES, callback);
				clientCommManager.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
				Log.d(TAG, "Send stanza PrivacyDataManagerBean::"+MethodType.GET_PRIVACY_POLICY.name());
				callback.wait();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			return callback.getPrivacyPolicy();
		}
	}


	public RequestPolicy updatePrivacyPolicy(RequestPolicy privacyPolicy) throws PrivacyException {
		// -- Verify
		if (null == privacyPolicy) {
			throw new PrivacyException("The privacy policy to update is empty.");
		}
		if (null == privacyPolicy.getRequestor() || null == privacyPolicy.getRequestor().getRequestorId()) {
			throw new PrivacyException("Not enought information to update a privacy policy. Requestor needed.");
		}

		// Send remote call
		UpdateRemotePrivacyPolicyTask task = new UpdateRemotePrivacyPolicyTask(context); 
		task.execute(privacyPolicy);

		// Wait and retrieve the result
		try {
			privacyPolicy = task.get();
		}
		catch(InterruptedException e) {
			Log.e(TAG, "Interruption: can't retrieve the result - "+e.getMessage(), e);
		}
		catch(ExecutionException e) {
			Log.e(TAG, "Execution: can't retrieve the result - "+e.getMessage(), e);
		}
		return privacyPolicy;
	}
	private class UpdateRemotePrivacyPolicyTask extends AsyncTask<Object, Void, RequestPolicy> {
		private Context context;

		public UpdateRemotePrivacyPolicyTask(Context context) {
			this.context = context;
		}

		protected RequestPolicy doInBackground(Object... args) {
			clientCommManager = new ClientCommunicationMgr(context);

			// -- Destination
			INetworkNode cloudNode = clientCommManager.getIdManager().getCloudNode();
			Stanza stanza = new Stanza(cloudNode);

			// -- Message
			PrivacyPolicyManagerBean messageBean = new PrivacyPolicyManagerBean();
			messageBean.setMethod(MethodType.UPDATE_PRIVACY_POLICY);
			messageBean.setPrivacyPolicy((RequestPolicy) args[0]);

			// -- Send
			RemotePrivacyPolicyCallback callback = new RemotePrivacyPolicyCallback(ELEMENT_NAMES, NAME_SPACES, PACKAGES);
			try {
				clientCommManager.register(ELEMENT_NAMES, callback);
				clientCommManager.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
				Log.d(TAG, "Send stanza PrivacyDataManagerBean::"+MethodType.UPDATE_PRIVACY_POLICY.name());
				callback.wait();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			return callback.getPrivacyPolicy();
		}
	}

	public boolean deletePrivacyPolicy(RequestorBean requestor) throws PrivacyException {
		// -- Verify
		if (null == requestor || null == requestor.getRequestorId()) {
			throw new PrivacyException("Not enought information to search a privacy policy. Requestor needed.");
		}

		// Send remote call
		DeleteRemotePrivacyPolicyTask task = new DeleteRemotePrivacyPolicyTask(context); 
		task.execute(requestor);

		// Wait and retrieve the result
		boolean result = false;
		try {
			result = task.get();
		}
		catch(InterruptedException e) {
			Log.e(TAG, "Interruption: can't retrieve the result - "+e.getMessage(), e);
		}
		catch(ExecutionException e) {
			Log.e(TAG, "Execution: can't retrieve the result - "+e.getMessage(), e);
		}
		return result;
	}
	private class DeleteRemotePrivacyPolicyTask extends AsyncTask<Object, Void, Boolean> {
		private Context context;

		public DeleteRemotePrivacyPolicyTask(Context context) {
			this.context = context;
		}

		protected Boolean doInBackground(Object... args) {
			clientCommManager = new ClientCommunicationMgr(context);

			// -- Destination
			INetworkNode cloudNode = clientCommManager.getIdManager().getCloudNode();
			Stanza stanza = new Stanza(cloudNode);

			// -- Message
			PrivacyPolicyManagerBean messageBean = new PrivacyPolicyManagerBean();
			messageBean.setMethod(MethodType.DELETE_PRIVACY_POLICY);
			messageBean.setRequestor((RequestorBean) args[0]);

			// -- Send
			RemotePrivacyPolicyCallback callback = new RemotePrivacyPolicyCallback(ELEMENT_NAMES, NAME_SPACES, PACKAGES);
			try {
				clientCommManager.register(ELEMENT_NAMES, callback);
				clientCommManager.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
				Log.d(TAG, "Send stanza PrivacyDataManagerBean::"+MethodType.DELETE_PRIVACY_POLICY.name());
				callback.wait();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			return callback.isAck();
		}
	}

	public RequestPolicy inferPrivacyPolicy(int privacyPolicyType, Map configuration) throws PrivacyException {
		return null;
	}
	public String toXmlString(RequestPolicy privacyPolicy) {
		return null;
	}
	public RequestPolicy fromXmlString(String privacyPolicy) throws PrivacyException {
		return null;
	}
	public RequestPolicy updatePrivacyPolicy(String privacyPolicy, RequestorBean requestor) throws PrivacyException {
		return null;
	}
}

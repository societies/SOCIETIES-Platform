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
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
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
			"http://societies.org/api/internal/schema/privacytrust/privacyprotection/model/privacypolicy",
			"http://societies.org/api/schema/identity", 
			"http://societies.org/api/schema/servicelifecycle/model");
	private static final List<String> PACKAGES = Arrays.asList("org.societies.api.internal.schema.privacytrust.privacyprotection.privacypolicymanagement",
			"org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy",
			"org.societies.api.schema.identity",
			"org.societies.api.schema.servicelifecycle.model");

	private Context context;
	private ClientCommunicationMgr clientCommManager;
	private PrivacyPolicyIntentSender intentSender;


	public PrivacyPolicyManagerRemote(Context context)  {
		this.context = context;
		clientCommManager = new ClientCommunicationMgr(context, true);
		intentSender = new PrivacyPolicyIntentSender(context);
	}


	public boolean getPrivacyPolicy(String clientPackage, RequestorBean owner) throws PrivacyException {
		String action = MethodType.GET_PRIVACY_POLICY.name();
		try {
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
			clientCommManager.register(ELEMENT_NAMES, callback);
			clientCommManager.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(TAG, "Send stanza PrivacyPolicyManagerBean::"+action);
		} catch (InvalidFormatException e) {
			Log.e(TAG, e.getMessage());
			intentSender.sendIntentError(clientPackage, action, "Error: don't know who to contact");
			return false;
		}
		catch (CommunicationException e) {
			Log.e(TAG, e.getMessage());
			intentSender.sendIntentError(clientPackage, action, "Error during the sending of remote request");
			return false;
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage());
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
		try {
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
			clientCommManager.register(ELEMENT_NAMES, callback);
			clientCommManager.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(TAG, "Send stanza PrivacyPolicyManagerBean::"+MethodType.UPDATE_PRIVACY_POLICY.name());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			intentSender.sendIntentError(clientPackage, MethodType.UPDATE_PRIVACY_POLICY.name(), "Error during the sending of remote request");
			return false;
		}
		return true;
	}

	public boolean deletePrivacyPolicy(String clientPackage, RequestorBean owner) throws PrivacyException {
		try {
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
		
			clientCommManager.register(ELEMENT_NAMES, callback);
			clientCommManager.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(TAG, "Send stanza PrivacyPolicyManagerBean::"+MethodType.DELETE_PRIVACY_POLICY.name());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			intentSender.sendIntentError(clientPackage, MethodType.DELETE_PRIVACY_POLICY.name(), "Error during the sending of remote request");
			return false;
		}
		return true;
	}
}

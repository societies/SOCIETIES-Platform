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

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.internal.privacytrust.model.PrivacyException;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.android.privacytrust.datamanagement.callback.PrivacyDataIntentSender;
import org.societies.android.privacytrust.datamanagement.callback.RemotePrivacyDataCallback;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBean;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;

import android.content.Context;
import android.util.Log;


/**
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyDataManagerRemote {
	private final static String TAG = PrivacyDataManagerRemote.class.getSimpleName();

	private static final List<String> ELEMENT_NAMES = Arrays.asList("privacyDataManagerBean", "privacyDataManagerBeanResult");
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
	private PrivacyDataIntentSender intentSender;


	public PrivacyDataManagerRemote(Context context)  {
		this.context = context;
		clientCommManager = new ClientCommunicationMgr(context);
		intentSender = new PrivacyDataIntentSender(context);
	}


	public void checkPermission(String clientPackage, RequestorBean requestor, DataIdentifier dataId, List<Action> actions) throws PrivacyException {
		String action = MethodType.CHECK_PERMISSION.name();
		// -- Destination
		INetworkNode cloudNode = clientCommManager.getIdManager().getCloudNode();
		Stanza stanza = new Stanza(cloudNode);
		Log.d(TAG, "Send "+action+" to "+cloudNode.getJid());

		// -- Message
		PrivacyDataManagerBean messageBean = new PrivacyDataManagerBean();
		messageBean.setMethod(MethodType.CHECK_PERMISSION);
		messageBean.setRequestor(requestor);
		messageBean.setDataIdUri(dataId.getUri());
		messageBean.setActions(actions);

		// -- Send
		RemotePrivacyDataCallback callback = new RemotePrivacyDataCallback(context, clientPackage, ELEMENT_NAMES, NAME_SPACES, PACKAGES);
		try {
			clientCommManager.register(ELEMENT_NAMES, callback);
			clientCommManager.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(TAG, "Send stanza PrivacyDataManagerBean::"+action);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			intentSender.sendIntentError(clientPackage, action, "Error during the sending of remote request");
		}
	}

	// -- Obfuscation
	public void obfuscateData(RequestorBean requestor, IDataWrapper dataWrapper) throws PrivacyException {
		String action = MethodType.OBFUSCATE_DATA.name();
		// -- Destination
		INetworkNode cloudNode = clientCommManager.getIdManager().getCloudNode();
		Stanza stanza = new Stanza(cloudNode);
		Log.d(TAG, "Send "+action+" to "+cloudNode.getJid());

		// -- Message
		PrivacyDataManagerBean messageBean = new PrivacyDataManagerBean();
		messageBean.setMethod(MethodType.OBFUSCATE_DATA);
		messageBean.setRequestor(requestor);
//		messageBean.setDataWrapper(dataWrapper);
		messageBean.setDataIdUri(dataId.getUri());

		// -- Send
		RemotePrivacyDataCallback callback = new RemotePrivacyDataCallback(context, clientPackage, ELEMENT_NAMES, NAME_SPACES, PACKAGES);
		try {
			clientCommManager.register(ELEMENT_NAMES, callback);
			clientCommManager.sendIQ(stanza, IQ.Type.GET, messageBean, callback);
			Log.d(TAG, "Send stanza PrivacyDataManagerBean::"+action);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			intentSender.sendIntentError(clientPackage, action, "Error during the sending of remote request");
		}
	}
	public DataIdentifier hasObfuscatedVersion(RequestorBean requestor, IDataWrapper dataWrapper) throws PrivacyException {
		Log.i(TAG, "Remote obfuscation not available yet.");
		return dataWrapper.getDataId();
	}
}

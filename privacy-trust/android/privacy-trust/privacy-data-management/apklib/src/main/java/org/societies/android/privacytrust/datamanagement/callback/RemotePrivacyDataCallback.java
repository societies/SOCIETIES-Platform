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
package org.societies.android.privacytrust.datamanagement.callback;

import java.util.List;

import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.internal.privacytrust.IPrivacyDataManager;
import org.societies.android.api.internal.privacytrust.IPrivacyPolicyManager;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.MethodType;
import org.societies.api.internal.schema.privacytrust.privacyprotection.privacydatamanagement.PrivacyDataManagerBeanResult;

import android.content.Context;
import android.util.Log;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class RemotePrivacyDataCallback implements ICommCallback {
	private final static String TAG = RemotePrivacyDataCallback.class.getSimpleName();

	private List<String> ELEMENT_NAMES;
	private List<String> NAME_SPACES;
	private List<String> PACKAGES;

	private PrivacyDataIntentSender intentSender;
	private String clientPackage;


	public RemotePrivacyDataCallback(Context context, String clientPackage, List<String> elementNames,
			List<String> namespaces, List<String> packages) {
		super();
		ELEMENT_NAMES = elementNames;
		NAME_SPACES = namespaces;
		PACKAGES = packages;
		this.clientPackage = clientPackage;
		intentSender = new PrivacyDataIntentSender(context);
	}


	public void receiveResult(Stanza stanza, Object payload) {
		Log.d(TAG, "receiveResult");
		debugStanza(stanza);
		// - Wrong payload
		if (null == payload || !(payload instanceof PrivacyDataManagerBeanResult)) {
			intentSender.sendIntentError(clientPackage, IPrivacyDataManager.INTENT_DEFAULT_ACTION, "Wrong payload received.");
			return;
		}
		// - Send valid intent
		// Send intent
		PrivacyDataManagerBeanResult privacyPaylaod = (PrivacyDataManagerBeanResult)payload;
		if (MethodType.CHECK_PERMISSION.name().equals(privacyPaylaod.getMethod().name())) {
			intentSender.sendIntentCheckPermission(clientPackage, privacyPaylaod);
			// TODO: Update PrivacyPermissions
			if (privacyPaylaod.isAck() && privacyPaylaod.getMethod().equals(MethodType.CHECK_PERMISSION)) {
				//			PrivacyDataManagerInternal privacyDataManagerInternal = new PrivacyDataManagerInternal();
				//			privacyDataManagerInternal.updatePermission(privacyPaylaod.getPermission());
			}
		}
		else if (MethodType.OBFUSCATE_DATA.name().equals(privacyPaylaod.getMethod().name())) {
			intentSender.sendIntentDataObfuscation(clientPackage, privacyPaylaod.getDataWrapper());
		}
		else {
			intentSender.sendIntentError(clientPackage, IPrivacyDataManager.INTENT_DEFAULT_ACTION, "Wrong method response received.");
		}
	}

	public void receiveError(Stanza stanza, XMPPError error) {
		Log.e(TAG, "Erreur XMPP: "+error.getMessage());
		intentSender.sendIntentError(clientPackage, IPrivacyDataManager.INTENT_DEFAULT_ACTION, "Error: "+error.getGenericText());
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
}

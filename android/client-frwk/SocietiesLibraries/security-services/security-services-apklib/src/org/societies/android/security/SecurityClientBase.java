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
package org.societies.android.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.common.ADate;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.android.api.internal.privacytrust.trust.IInternalTrustClient;
import org.societies.android.api.internal.security.digsig.IInternalDigSigClient;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;
import org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipsRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustRelationshipsResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustValueRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustValueResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustBrokerRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustBrokerResponseBean;
import org.societies.api.schema.privacytrust.trust.evidence.collector.AddDirectEvidenceRequestBean;
import org.societies.api.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorRequestBean;
import org.societies.api.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorResponseBean;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

/**
 * Android implementation of the {@link IInternalTrustClient} service.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.5
 */
public class SecurityClientBase implements IInternalDigSigClient {
	
	private static final String TAG = SecurityClientBase.class.getName();
	
	private static final List<String> ELEMENT_NAMES = Arrays.asList(
			"trustBrokerRequestBean", "trustBrokerResponseBean",
			"trustEvidenceCollectorRequestBean", "trustEvidenceCollectorResponseBean");
	
	private static final List<String> NAMESPACES = Arrays.asList(
			"http://societies.org/api/schema/identity",
            "http://societies.org/api/schema/privacytrust/trust/model",
			"http://societies.org/api/schema/privacytrust/trust/broker",
			"http://societies.org/api/schema/privacytrust/trust/evidence/collector");
	
	private static final List<String> PACKAGES = Arrays.asList(
			"org.societies.api.schema.identity",
			"org.societies.api.schema.privacytrust.trust.model",
			"org.societies.api.schema.privacytrust.trust.broker",
			"org.societies.api.schema.privacytrust.trust.evidence.collector");
	
	private final boolean restrictBroadcast;
	private boolean connectedToComms = false;
	
	private Context androidContext;
	
	public SecurityClientBase(Context androidContext) {
		
    	this(androidContext, true);
    }
    
    public SecurityClientBase(Context androidContext, boolean restrictBroadcast) {
    	
    	Log.i(TAG, this.getClass().getName() + " instantiated");
    	
    	this.androidContext = androidContext;
    	this.restrictBroadcast = restrictBroadcast;
    }
    
	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClient#retrieveTrustRelationships(java.lang.String, org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean)
	 */
	@Override
	public void retrieveTrustRelationships(final String client,
			final RequestorBean requestor,
			final TrustedEntityIdBean trustorId) {
		
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		
		this.doRetrieveTrustRelationships(client, requestor, trustorId);
	}
	
	private void doRetrieveTrustRelationships(final String client, 
			RequestorBean requestor, final TrustedEntityIdBean trustorId) {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("Retrieving trust relationships:");
		sb.append(" client=");
		sb.append(client);
		sb.append(", requestor=");
		sb.append(requestor);
		sb.append(", trustorId=");
		sb.append(trustorId.getEntityId());
		Log.d(TAG, sb.toString());
		
		if (this.connectedToComms) {
			
			try {
				if (requestor == null) {
					requestor = new RequestorBean();
				}
				
				final TrustRelationshipsRequestBean retrieveBean = new TrustRelationshipsRequestBean();
				retrieveBean.setRequestor(requestor);
				retrieveBean.setTrustorId(trustorId);

				final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
				requestBean.setMethodName(
						org.societies.api.schema.privacytrust.trust.broker.MethodName.RETRIEVE_TRUST_RELATIONSHIPS);
				requestBean.setRetrieveTrustRelationships(retrieveBean);

				receiveResult(client, IInternalTrustClient.RETRIEVE_TRUST_RELATIONSHIPS); 
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send RETRIEVE_TRUST_RELATIONSHIPS request: "
						+ e.getMessage(); 
				Log.e(TAG, exceptionMessage, e);
				this.broadcastException(client, 
						IInternalTrustClient.RETRIEVE_TRUST_RELATIONSHIPS, exceptionMessage);
	        }
			
		} else {
			// NOT CONNECTED TO COMMS SERVICE
        	this.broadcastServiceNotStarted(client, IInternalTrustClient.RETRIEVE_TRUST_RELATIONSHIPS);
		}
	}
	
	@Override
	public boolean startService() {

		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
		SecurityClientBase.this.androidContext.sendBroadcast(intent);

		return true;
	}

	@Override
	public boolean stopService() {

		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
		SecurityClientBase.this.androidContext.sendBroadcast(intent);

		return true;
	}
    
    /**
	 * @param client
	 */
	private void broadcastServiceNotStarted(String client, String method) {
		
		if (client != null) {
			Intent intent = new Intent(method);
			intent.putExtra(IServiceManager.INTENT_NOTSTARTED_EXCEPTION, true);
			intent.setPackage(client);
			SecurityClientBase.this.androidContext.sendBroadcast(intent);
		}
	}
	
	private void broadcastException(String client, String method, String message) {

		final Intent intent = new Intent(method);
		intent.putExtra(IInternalTrustClient.INTENT_EXCEPTION_KEY, message);
		if (this.restrictBroadcast)
			intent.setPackage(client); 
		this.androidContext.sendBroadcast(intent);
	}

	public void receiveResult(String returnIntent, String client, Object payload) {

		Log.d(SecurityClientBase.TAG, "receiveResult: payload=" + payload);

		final Intent intent = new Intent(returnIntent);
		boolean everythingOk = true;
		
		if (everythingOk) {
			// TrustBroker response bean
				intent.putExtra(IInternalTrustClient.INTENT_RETURN_VALUE_KEY, 
							(Parcelable) trustRelationship);
				break;
		} else {
			Log.e(SecurityClientBase.TAG, "Received unexpected response bean in result: "
					+ ((payload != null) ? payload.getClass() : "null"));
			SecurityClientBase.this.broadcastException(client,
					returnIntent, 
					"Received unexpected response bean in result: "
					+ ((payload != null) ? payload.getClass() : "null"));
			return;
		}
		
		if (SecurityClientBase.this.restrictBroadcast)
			intent.setPackage(client);
		intent.addFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
		Log.d(SecurityClientBase.TAG, "receiveResult: broadcasting intent " + intent); 
		SecurityClientBase.this.androidContext.sendBroadcast(intent);
	}
}

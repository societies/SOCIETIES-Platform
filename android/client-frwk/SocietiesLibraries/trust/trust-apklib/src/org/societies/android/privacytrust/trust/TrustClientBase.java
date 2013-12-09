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
package org.societies.android.privacytrust.trust;

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
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.trust.model.ExtTrustRelationshipBean;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;
import org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean;
import org.societies.api.schema.privacytrust.trust.broker.ExtTrustRelationshipRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.ExtTrustRelationshipResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.ExtTrustRelationshipsRequestBean;
import org.societies.api.schema.privacytrust.trust.broker.ExtTrustRelationshipsResponseBean;
import org.societies.api.schema.privacytrust.trust.broker.TrustQueryBean;
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
public class TrustClientBase implements IInternalTrustClient {
	
	private static final String TAG = TrustClientBase.class.getName();
	
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
	
	/** The Client Comm Mgr service reference. */
	private ClientCommunicationMgr clientCommMgr;
	private final boolean restrictBroadcast;
	private boolean connectedToComms = false;
	
	private Context androidContext;
	
	public TrustClientBase(Context androidContext) {
		
    	this(androidContext, true);
    }
    
    public TrustClientBase(Context androidContext, boolean restrictBroadcast) {
    	
    	Log.i(TAG, this.getClass().getName() + " instantiated");
    	
    	this.androidContext = androidContext;
    	this.restrictBroadcast = restrictBroadcast;
		try {
			this.clientCommMgr = new ClientCommunicationMgr(androidContext, true);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
        }
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
	
		final TrustQueryBean query = new TrustQueryBean();
		query.setTrustorId(trustorId);
		this.doRetrieveTrustRelationships(client, requestor, query);
	}
	
	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClient#retrieveTrustRelationships(java.lang.String, org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean)
	 */
	@Override
	public void retrieveTrustRelationships(final String client,
			final RequestorBean requestor,
			final TrustedEntityIdBean trustorId, 
			final TrustedEntityIdBean trusteeId) {
		
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		
		final TrustQueryBean query = new TrustQueryBean();
		query.setTrustorId(trustorId);
		query.setTrusteeId(trusteeId);
		this.doRetrieveTrustRelationships(client, requestor, query);
	}
	
	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClient#retrieveTrustRelationship(java.lang.String, org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean)
	 */
	@Override
	public void retrieveTrustRelationship(final String client,
			final RequestorBean requestor,
			final TrustedEntityIdBean trustorId, 
			final TrustedEntityIdBean trusteeId,
			final TrustValueTypeBean trustValueType) {
		
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		final TrustQueryBean query = new TrustQueryBean();
		query.setTrustorId(trustorId);
		query.setTrusteeId(trusteeId);
		query.setTrustValueType(trustValueType);
		this.doRetrieveTrustRelationship(client, requestor, query);
	}
    
    /*
     * @see org.societies.android.api.privacytrust.trust.ITrustClient#retrieveTrustValue(java.lang.String, org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean)
     */
	@Override
	public void retrieveTrustValue(final String client,
			final RequestorBean requestor,
			final TrustedEntityIdBean trustorId, 
			final TrustedEntityIdBean trusteeId,
			final TrustValueTypeBean trustValueType) {
		
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeId == null)
			throw new NullPointerException("trusteeId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trustValueType can't be null");
		
		final TrustQueryBean query = new TrustQueryBean();
		query.setTrustorId(trustorId);
		query.setTrusteeId(trusteeId);
		query.setTrustValueType(trustValueType);
		this.doRetrieveTrustValue(client, requestor, query);
	}
	
	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClient#retrieveTrustRelationships(java.lang.String, org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean)
	 */
	@Override
	public void retrieveTrustRelationships(final String client,
			final RequestorBean requestor,
			final TrustedEntityIdBean trustorId, 
			final TrustedEntityTypeBean trusteeType) {
		
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		
		final TrustQueryBean query = new TrustQueryBean();
		query.setTrustorId(trustorId);
		query.setTrusteeType(trusteeType);
		this.doRetrieveTrustRelationships(client, requestor, query);
	}
	
	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClient#retrieveTrustRelationships(java.lang.String, org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean)
	 */
	@Override
	public void retrieveTrustRelationships(final String client,
			final RequestorBean requestor,
			final TrustedEntityIdBean trustorId, 
			final TrustValueTypeBean trustValueType) {
		
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trusteeType can't be null");
		
		final TrustQueryBean query = new TrustQueryBean();
		query.setTrustorId(trustorId);
		query.setTrustValueType(trustValueType);
		this.doRetrieveTrustRelationships(client, requestor, query);
	}
	
	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClient#retrieveTrustRelationships(java.lang.String, org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean, org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean)
	 */
	@Override
	public void retrieveTrustRelationships(final String client,
			final RequestorBean requestor,
			final TrustedEntityIdBean trustorId,
			final TrustedEntityTypeBean trusteeType,
			final TrustValueTypeBean trustValueType) {
		
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (trustorId == null)
			throw new NullPointerException("trustorId can't be null");
		if (trusteeType == null)
			throw new NullPointerException("trusteeType can't be null");
		if (trustValueType == null)
			throw new NullPointerException("trusteeType can't be null");
		
		final TrustQueryBean query = new TrustQueryBean();
		query.setTrustorId(trustorId);
		query.setTrusteeType(trusteeType);
		query.setTrustValueType(trustValueType);
		this.doRetrieveTrustRelationships(client, requestor, query);
	}
	
	/*
	 * @see org.societies.android.api.privacytrust.trust.ITrustClient#addDirectTrustEvidence(java.lang.String, org.societies.api.schema.identity.RequestorBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean, org.societies.android.api.common.ADate, java.io.Serializable)
	 */
	@Override
	public void addDirectTrustEvidence(final String client, 
			final RequestorBean requestor, final TrustedEntityIdBean subjectId,
			final TrustedEntityIdBean objectId,
			final TrustEvidenceTypeBean type, final ADate timestamp, 
			final Serializable info) {
		
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (requestor == null)
			throw new NullPointerException("requestor can't be null");
		if (subjectId == null)
			throw new NullPointerException("subjectId can't be null");
		if (objectId == null)
			throw new NullPointerException("objectId can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		
		this.doAddDirectTrustEvidence(client, requestor, subjectId, objectId, type, timestamp, info);
	}
	
	/*
     * @see org.societies.android.api.internal.privacytrust.trust.IInternalTrustClient#retrieveTrustRelationships(java.lang.String, org.societies.api.schema.privacytrust.trust.broker.TrustQueryBean)
     */
	@Override
	public void retrieveTrustRelationships(final String client,
			final TrustQueryBean query) {
		
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
		
		this.doRetrieveTrustRelationships(client, null, query);
	}
	
	/*
	 * @see org.societies.android.api.internal.privacytrust.trust.IInternalTrustClient#retrieveTrustRelationship(java.lang.String, org.societies.api.schema.privacytrust.trust.broker.TrustQueryBean)
	 */
	@Override
	public void retrieveTrustRelationship(final String client,
			final TrustQueryBean query) {
		
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
		
		this.doRetrieveTrustRelationship(client, null, query);
	}
	
	/*
	 * @see org.societies.android.api.internal.privacytrust.trust.IInternalTrustClient#retrieveTrustValue(java.lang.String, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustValueTypeBean)
	 */
	@Override
	public void retrieveTrustValue(final String client,
			final TrustQueryBean query) {
		
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
				
		this.doRetrieveTrustValue(client, null, query);
	}
	
	/*
	 * @see org.societies.android.api.internal.privacytrust.trust.IInternalTrustClient#retrieveExtTrustRelationships(java.lang.String, org.societies.api.schema.privacytrust.trust.broker.TrustQueryBean)
	 */
	@Override
	public void retrieveExtTrustRelationships(final String client,
			final TrustQueryBean query) {
		
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
		
		this.doRetrieveExtTrustRelationships(client, query);
	}
	
	/*
	 * @see org.societies.android.api.internal.privacytrust.trust.IInternalTrustClient#retrieveExtTrustRelationship(java.lang.String, org.societies.api.schema.privacytrust.trust.broker.TrustQueryBean)
	 */
	@Override
	public void retrieveExtTrustRelationship(final String client,
			final TrustQueryBean query) {
		
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (query == null)
			throw new NullPointerException("query can't be null");
		
		this.doRetrieveExtTrustRelationship(client, query);
	}
	
	/*
	 * @see org.societies.android.api.internal.privacytrust.trust.IInternalTrustClient#addDirectTrustEvidence(java.lang.String, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean, org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean, org.societies.android.api.common.ADate, java.io.Serializable)
	 */
	@Override
	public void addDirectTrustEvidence(final String client, 
			final TrustedEntityIdBean subjectId, 
			final TrustedEntityIdBean objectId,
			final TrustEvidenceTypeBean type, final ADate timestamp, 
			final Serializable info) {
		
		if (client == null)
			throw new NullPointerException("client can't be null");
		if (subjectId == null)
			throw new NullPointerException("subjectId can't be null");
		if (objectId == null)
			throw new NullPointerException("objectId can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		
		this.doAddDirectTrustEvidence(client, null, subjectId, objectId, type, timestamp, info);
	}
	
	private void doRetrieveTrustRelationships(final String client, 
			RequestorBean requestor, final TrustQueryBean query) {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("Retrieving trust relationships:");
		sb.append(" client=");
		sb.append(client);
		sb.append(", requestor=");
		sb.append(requestor);
		sb.append(", trustorId=");
		sb.append(query.getTrustorId().getEntityId());
		if (query.getTrusteeId() != null) {
			sb.append(", trusteeId=");
			sb.append(query.getTrusteeId().getEntityId());
		}
		if (query.getTrusteeType() != null) {
			sb.append(", trusteeType=");
			sb.append(query.getTrusteeType());
		}
		if (query.getTrustValueType() != null) {
			sb.append(", trustValueType=");
			sb.append(query.getTrustValueType());
		}
		Log.d(TAG, sb.toString());
		
		if (this.connectedToComms) {
			
			try {
				if (requestor == null) {
					requestor = new RequestorBean();
					requestor.setRequestorId(
							this.clientCommMgr.getIdManager().getThisNetworkNode().getJid());
					Log.d(TAG, "requestor=" + requestor.getRequestorId());
				}
				
				final TrustRelationshipsRequestBean retrieveBean = new TrustRelationshipsRequestBean();
				retrieveBean.setRequestor(requestor);
				retrieveBean.setQuery(query);

				final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
				requestBean.setMethodName(
						org.societies.api.schema.privacytrust.trust.broker.MethodName.RETRIEVE_TRUST_RELATIONSHIPS);
				requestBean.setRetrieveTrustRelationships(retrieveBean);

				final ICommCallback retrieveTrustCallback = 
						new TrustClientCommCallback(client, IInternalTrustClient.RETRIEVE_TRUST_RELATIONSHIPS); 
				final IIdentity toId = this.clientCommMgr.getIdManager().getCloudNode();
				Log.d(TAG, "cloudNode=" + toId.getJid());
				final Stanza stanza = new Stanza(toId);
	        	this.clientCommMgr.sendIQ(stanza, IQ.Type.GET, requestBean, 
	        			retrieveTrustCallback);
	        	Log.d(TAG, "Sent IQ with stanza=" + stanza);
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
	
	private void doRetrieveTrustRelationship(final String client, 
			RequestorBean requestor, final TrustQueryBean query) {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("Retrieving trust relationship:");
		sb.append(" client=");
		sb.append(client);
		sb.append(", requestor=");
		sb.append(requestor);
		sb.append(", trustorId=");
		sb.append(query.getTrustorId().getEntityId());
		sb.append(", trusteeId=");
		sb.append(query.getTrusteeId().getEntityId());
		sb.append(", trustValueType=");
		sb.append(query.getTrustValueType());
		Log.d(TAG, sb.toString());
		
		if (this.connectedToComms) {
			
			try {
				if (requestor == null) {
					requestor = new RequestorBean();
					requestor.setRequestorId(
							this.clientCommMgr.getIdManager().getThisNetworkNode().getJid());
					Log.d(TAG, "requestor=" + requestor.getRequestorId());
				}
				
				final TrustRelationshipRequestBean retrieveBean = new TrustRelationshipRequestBean();
				retrieveBean.setRequestor(requestor);
				retrieveBean.setQuery(query);

				final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
				requestBean.setMethodName(
						org.societies.api.schema.privacytrust.trust.broker.MethodName.RETRIEVE_TRUST_RELATIONSHIP);
				requestBean.setRetrieveTrustRelationship(retrieveBean);

				final ICommCallback retrieveTrustCallback = 
						new TrustClientCommCallback(client, IInternalTrustClient.RETRIEVE_TRUST_RELATIONSHIP); 
				final IIdentity toId = this.clientCommMgr.getIdManager().getCloudNode();
				Log.d(TAG, "cloudNode=" + toId.getJid());
				final Stanza stanza = new Stanza(toId);
	        	this.clientCommMgr.sendIQ(stanza, IQ.Type.GET, requestBean, 
	        			retrieveTrustCallback);
	        	Log.d(TAG, "Sent IQ with stanza=" + stanza);
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send RETRIEVE_TRUST_RELATIONSHIP request: "
						+ e.getMessage(); 
				Log.e(TAG, exceptionMessage, e);
				this.broadcastException(client, 
						IInternalTrustClient.RETRIEVE_TRUST_RELATIONSHIP, exceptionMessage);
	        }
			
		} else {
			// NOT CONNECTED TO COMMS SERVICE
        	this.broadcastServiceNotStarted(client, IInternalTrustClient.RETRIEVE_TRUST_RELATIONSHIP);
		}
	}
	
	private void doRetrieveTrustValue(final String client, 
			RequestorBean requestor, final TrustQueryBean query) {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("Retrieving trust value:");
		sb.append(" client=");
		sb.append(client);
		sb.append(", requestor=");
		sb.append(requestor);
		sb.append(", trustorId=");
		sb.append(query.getTrustorId().getEntityId());
		sb.append(", trusteeId=");
		sb.append(query.getTrusteeId().getEntityId());
		sb.append(", trustValueType=");
		sb.append(query.getTrustValueType());
		Log.d(TAG, sb.toString());
		
		if (this.connectedToComms) {
			
			try {
				if (requestor == null) {
					requestor = new RequestorBean();
					requestor.setRequestorId(
							this.clientCommMgr.getIdManager().getThisNetworkNode().getJid());
					Log.d(TAG, "requestor=" + requestor.getRequestorId());
				}
				
				final TrustValueRequestBean retrieveBean = new TrustValueRequestBean();
				retrieveBean.setRequestor(requestor);
				retrieveBean.setQuery(query);

				final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
				requestBean.setMethodName(
						org.societies.api.schema.privacytrust.trust.broker.MethodName.RETRIEVE_TRUST_VALUE);
				requestBean.setRetrieveTrustValue(retrieveBean);

				final ICommCallback retrieveTrustCallback = 
						new TrustClientCommCallback(client, IInternalTrustClient.RETRIEVE_TRUST_VALUE); 
				final IIdentity toId = this.clientCommMgr.getIdManager().getCloudNode();
				Log.d(TAG, "cloudNode=" + toId.getJid());
				final Stanza stanza = new Stanza(toId);
	        	this.clientCommMgr.sendIQ(stanza, IQ.Type.GET, requestBean, 
	        			retrieveTrustCallback);
	        	Log.d(TAG, "Sent IQ with stanza=" + stanza);
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send RETRIEVE_TRUST_VALUE request: "
						+ e.getMessage(); 
				Log.e(TAG, exceptionMessage, e);
				this.broadcastException(client, 
						IInternalTrustClient.RETRIEVE_TRUST_VALUE, exceptionMessage);
	        }
			
		} else {
			// NOT CONNECTED TO COMMS SERVICE
        	this.broadcastServiceNotStarted(client, IInternalTrustClient.RETRIEVE_TRUST_VALUE);
		}
	}
	
	private void doRetrieveExtTrustRelationships(final String client,
			final TrustQueryBean query) {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("Retrieving extended trust relationships:");
		sb.append(" client=");
		sb.append(client);
		sb.append(", trustorId=");
		sb.append(query.getTrustorId().getEntityId());
		if (query.getTrusteeId() != null) {
			sb.append(", trusteeId=");
			sb.append(query.getTrusteeId().getEntityId());
		}
		if (query.getTrusteeType() != null) {
			sb.append(", trusteeType=");
			sb.append(query.getTrusteeType());
		}
		if (query.getTrustValueType() != null) {
			sb.append(", trustValueType=");
			sb.append(query.getTrustValueType());
		}
		Log.d(TAG, sb.toString());
		
		if (this.connectedToComms) {
			
			try {
				final ExtTrustRelationshipsRequestBean retrieveBean = new ExtTrustRelationshipsRequestBean();
				retrieveBean.setQuery(query);

				final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
				requestBean.setMethodName(
						org.societies.api.schema.privacytrust.trust.broker.MethodName.RETRIEVE_EXT_TRUST_RELATIONSHIPS);
				requestBean.setRetrieveExtTrustRelationships(retrieveBean);

				final ICommCallback retrieveTrustCallback = 
						new TrustClientCommCallback(client, IInternalTrustClient.RETRIEVE_EXT_TRUST_RELATIONSHIPS); 
				final IIdentity toId = this.clientCommMgr.getIdManager().getCloudNode();
				Log.d(TAG, "cloudNode=" + toId.getJid());
				final Stanza stanza = new Stanza(toId);
	        	this.clientCommMgr.sendIQ(stanza, IQ.Type.GET, requestBean, 
	        			retrieveTrustCallback);
	        	Log.d(TAG, "Sent IQ with stanza=" + stanza);
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send RETRIEVE_EXT_TRUST_RELATIONSHIPS request: "
						+ e.getMessage(); 
				Log.e(TAG, exceptionMessage, e);
				this.broadcastException(client, 
						IInternalTrustClient.RETRIEVE_EXT_TRUST_RELATIONSHIPS, exceptionMessage);
	        }
			
		} else {
			// NOT CONNECTED TO COMMS SERVICE
        	this.broadcastServiceNotStarted(client, IInternalTrustClient.RETRIEVE_EXT_TRUST_RELATIONSHIPS);
		}
	}
	
	private void doRetrieveExtTrustRelationship(final String client, 
			final TrustQueryBean query) {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("Retrieving trust relationship:");
		sb.append(" client=");
		sb.append(client);
		sb.append(", trustorId=");
		sb.append(query.getTrustorId().getEntityId());
		sb.append(", trusteeId=");
		sb.append(query.getTrusteeId().getEntityId());
		sb.append(", trustValueType=");
		sb.append(query.getTrustValueType());
		Log.d(TAG, sb.toString());
		
		if (this.connectedToComms) {
			
			try {
				final ExtTrustRelationshipRequestBean retrieveBean = new ExtTrustRelationshipRequestBean();

				final TrustBrokerRequestBean requestBean = new TrustBrokerRequestBean();
				requestBean.setMethodName(
						org.societies.api.schema.privacytrust.trust.broker.MethodName.RETRIEVE_EXT_TRUST_RELATIONSHIP);
				requestBean.setRetrieveExtTrustRelationship(retrieveBean);

				final ICommCallback retrieveTrustCallback = 
						new TrustClientCommCallback(client, IInternalTrustClient.RETRIEVE_EXT_TRUST_RELATIONSHIP); 
				final IIdentity toId = this.clientCommMgr.getIdManager().getCloudNode();
				Log.d(TAG, "cloudNode=" + toId.getJid());
				final Stanza stanza = new Stanza(toId);
	        	this.clientCommMgr.sendIQ(stanza, IQ.Type.GET, requestBean, 
	        			retrieveTrustCallback);
	        	Log.d(TAG, "Sent IQ with stanza=" + stanza);
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send RETRIEVE_EXT_TRUST_RELATIONSHIP request: "
						+ e.getMessage(); 
				Log.e(TAG, exceptionMessage, e);
				this.broadcastException(client, 
						IInternalTrustClient.RETRIEVE_EXT_TRUST_RELATIONSHIP, exceptionMessage);
	        }
			
		} else {
			// NOT CONNECTED TO COMMS SERVICE
        	this.broadcastServiceNotStarted(client, IInternalTrustClient.RETRIEVE_EXT_TRUST_RELATIONSHIP);
		}
	}
	
	private void doAddDirectTrustEvidence(final String client,
			RequestorBean requestor, final TrustedEntityIdBean subjectId,
			final TrustedEntityIdBean objectId,	final TrustEvidenceTypeBean type,
			final ADate timestamp, final Serializable info) {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("Adding direct trust evidence:");
		sb.append(" client=");
		sb.append(client);
		sb.append(", requestor=");
		sb.append(requestor);
		sb.append(", subjectId=");
		sb.append(subjectId.getEntityId());
		sb.append(", objectId=");
		sb.append(objectId.getEntityId());
		sb.append(", type=");
		sb.append(type);
		sb.append(", timestamp=");
		sb.append(timestamp);
		sb.append(", info=");
		sb.append(info);
		Log.d(TAG, sb.toString());

		if (this.connectedToComms) {
			
			try {
				if (requestor == null) {
					requestor = new RequestorBean();
					requestor.setRequestorId(
							this.clientCommMgr.getIdManager().getThisNetworkNode().getJid());
					Log.d(TAG, "requestor=" + requestor.getRequestorId());
				}
				
				final AddDirectEvidenceRequestBean addEvidenceBean = 
						new AddDirectEvidenceRequestBean();
				// 1. requestor
				addEvidenceBean.setRequestor(requestor);
				// 2. subjectId
				addEvidenceBean.setSubjectId(subjectId);
				// 3. objectId
				addEvidenceBean.setObjectId(objectId);
				// 4. type
				addEvidenceBean.setType(type);
				// 5. timestamp
				addEvidenceBean.setTimestamp(timestamp.getDate());
				// 6. info
				if (info != null)
					addEvidenceBean.setInfo(serialise(info));

				final TrustEvidenceCollectorRequestBean requestBean = 
						new TrustEvidenceCollectorRequestBean();
				requestBean.setMethodName(
						org.societies.api.schema.privacytrust.trust.evidence.collector.MethodName.ADD_DIRECT_EVIDENCE);
				requestBean.setAddDirectEvidence(addEvidenceBean);

				final ICommCallback addDirectTrustEvidenceCallback = 
						new TrustClientCommCallback(client, IInternalTrustClient.ADD_DIRECT_TRUST_EVIDENCE); 
				final IIdentity toId = this.clientCommMgr.getIdManager().getCloudNode();
				Log.d(TAG, "cloudNode=" + toId.getJid());
				final Stanza stanza = new Stanza(toId);
				this.clientCommMgr.sendIQ(stanza, IQ.Type.GET, requestBean,
						addDirectTrustEvidenceCallback);
				Log.d(TAG, "Sent IQ with stanza=" + stanza);
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send ADD_DIRECT_TRUST_EVIDENCE request: "
						+ e.getMessage(); 
				Log.e(TAG, exceptionMessage, e);
				this.broadcastException(client, 
						IInternalTrustClient.ADD_DIRECT_TRUST_EVIDENCE, exceptionMessage);
			}
			
		} else {
			// NOT CONNECTED TO COMMS SERVICE
        	this.broadcastServiceNotStarted(client, IInternalTrustClient.ADD_DIRECT_TRUST_EVIDENCE);
		}
	}
	
	public boolean startService() {

		if (!this.connectedToComms) {
			// NOT CONNECTED TO COMMS SERVICE YET
			Log.d(TAG, "startService binding to comms");
			this.clientCommMgr.bindCommsService(new IMethodCallback() {	

				/*
				 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
				 */
				@Override
				public void returnAction(boolean resultFlag) {

					Log.d(TAG, "Connected to comms: " + resultFlag);
					if (resultFlag) {
						TrustClientBase.this.connectedToComms = true;
						// REGISTER NAMESPACES
						TrustClientBase.this.clientCommMgr.register(
								TrustClientBase.ELEMENT_NAMES, 
								TrustClientBase.NAMESPACES, 
								TrustClientBase.PACKAGES, 
								new IMethodCallback() {

									/*
									 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
									 */
									@Override
									public void returnAction(boolean resultFlag) {
										Log.d(TAG, "Namespaces registered: " + resultFlag);
										//SEND INTENT WITH SERVICE STARTED STATUS
										Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
										intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, resultFlag);
										TrustClientBase.this.androidContext.sendBroadcast(intent);
									}
									
									/*
									 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
									 */
									@Override
									public void returnAction(String result) { }


									@Override
									public void returnException(String result) {
										// TODO Auto-generated method stub
									}
								});
					} else {
						Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
						intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false);
						TrustClientBase.this.androidContext.sendBroadcast(intent);
					}
				}	

				/*
				 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
				 */
				@Override
				public void returnAction(String result) {}

				@Override
				public void returnException(String result) {
					// TODO Auto-generated method stub
				}
			});
		}
		else {
			Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
			intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
			TrustClientBase.this.androidContext.sendBroadcast(intent);
		}

		return true;
	}

	public boolean stopService() {

		if (this.connectedToComms) {
			// UNREGISTER AND DISCONNECT FROM COMMS
			Log.d(TAG, "stopService unregistering namespaces");
			this.clientCommMgr.unregister(ELEMENT_NAMES, NAMESPACES, new IMethodCallback() {

				/*
				 * @see org.societies.android.api.comms.IMethodCallback#returnAction(boolean)
				 */
				@Override
				public void returnAction(boolean resultFlag) {

					Log.d(TAG, "Unregistered namespaces: " + resultFlag);
					TrustClientBase.this.connectedToComms = false;

					TrustClientBase.this.clientCommMgr.unbindCommsService();
					// SEND INTENT WITH SERVICE STOPPED STATUS
					Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
					intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
					TrustClientBase.this.androidContext.sendBroadcast(intent);
				}
				
				/*
				 * @see org.societies.android.api.comms.IMethodCallback#returnAction(java.lang.String)
				 */
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
			TrustClientBase.this.androidContext.sendBroadcast(intent);
		}
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
			TrustClientBase.this.androidContext.sendBroadcast(intent);
		}
	}
	
	private void broadcastException(String client, String method, String message) {

		final Intent intent = new Intent(method);
		intent.putExtra(IInternalTrustClient.INTENT_EXCEPTION_KEY, message);
		if (this.restrictBroadcast)
			intent.setPackage(client); 
		this.androidContext.sendBroadcast(intent);
	}
	
	/**
	 * Serialises the specified object into a byte array
	 * 
	 * @param object
	 *            the object to serialise
	 * @return a byte array of the serialised object
	 * @throws IOException if the serialisation of the specified object fails
	 */
	private static byte[] serialise(Serializable object) throws IOException {

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		
		return baos.toByteArray();
	}
	
	/** The Trust Client Comm Mgr callback. */
	private class TrustClientCommCallback implements ICommCallback {
		
		private String client;
		private String returnIntent;
		
		/**
		 * Constructs new TrustClientCommCallback with the specified calling
		 * client and Intent to be returned.
		 * 
		 * @param client
		 * @param returnIntent
		 */
		public TrustClientCommCallback(String client, String returnIntent) {
			
			this.client = client;
			this.returnIntent = returnIntent;
		}
		
		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
		 */
		@Override
		public List<String> getXMLNamespaces() {
			
			return TrustClientBase.NAMESPACES;
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
		 */
		@Override
		public List<String> getJavaPackages() {
			
			return TrustClientBase.PACKAGES;
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveResult(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
		 */
		@Override
		public void receiveResult(Stanza stanza, Object payload) {

			Log.d(TrustClientBase.TAG, "receiveResult: stanza=" + stanza
					+ ", payload=" + payload);

			final Intent intent = new Intent(this.returnIntent);
			
			if (payload instanceof TrustBrokerResponseBean) {
				// TrustBroker response bean
				final TrustBrokerResponseBean responseBean = 
						(TrustBrokerResponseBean) payload;
				Log.d(TrustClientBase.TAG, 
						"receiveResult: payload.methodName=" 
								+ responseBean.getMethodName());
				switch (responseBean.getMethodName()) {
				
				case RETRIEVE_TRUST_RELATIONSHIPS:
					
					final TrustRelationshipsResponseBean retrieveRelationshipsBean =
						responseBean.getRetrieveTrustRelationships();
					if (retrieveRelationshipsBean == null) {
						Log.e(TAG, "Trust Broker retrieve trust relationships response bean is null");
						TrustClientBase.this.broadcastException(this.client,
								this.returnIntent, 
								"Trust Broker retrieve trust relationships response bean is null");
						return;
					}
					final List<TrustRelationshipBean> trustRelationships = 
							retrieveRelationshipsBean.getResult();
					if (trustRelationships != null)
						intent.putExtra(IInternalTrustClient.INTENT_RETURN_VALUE_KEY, 
								trustRelationships.toArray(
										new TrustRelationshipBean[trustRelationships.size()]));
					else // return empty array
						intent.putExtra(IInternalTrustClient.INTENT_RETURN_VALUE_KEY, 
								new TrustRelationshipBean[0]);
					break;
				
				case RETRIEVE_TRUST_RELATIONSHIP:
					
					final TrustRelationshipResponseBean retrieveRelationshipBean =
						responseBean.getRetrieveTrustRelationship();
					if (retrieveRelationshipBean == null) {
						Log.e(TAG, "Trust Broker retrieve trust relationship response bean is null");
						TrustClientBase.this.broadcastException(this.client,
								this.returnIntent, 
								"Trust Broker retrieve trust relationship response bean is null");
						return;
					}
					final TrustRelationshipBean trustRelationship = retrieveRelationshipBean.getResult();
					if (trustRelationship != null)
						intent.putExtra(IInternalTrustClient.INTENT_RETURN_VALUE_KEY, 
								(Parcelable) trustRelationship);
					break;
				
				case RETRIEVE_TRUST_VALUE:
					
					final TrustValueResponseBean retrieveValueBean =
						responseBean.getRetrieveTrustValue();
					if (retrieveValueBean == null) {
						Log.e(TAG, "Trust Broker retrieve trust value response bean is null");
						TrustClientBase.this.broadcastException(this.client,
								this.returnIntent, 
								"Trust Broker retrieve trust value response bean is null");
						return;
					}
					final Double trustValue = retrieveValueBean.getResult();
					if (trustValue != null)
						intent.putExtra(IInternalTrustClient.INTENT_RETURN_VALUE_KEY, trustValue);
					break;
					
				case RETRIEVE_EXT_TRUST_RELATIONSHIPS:

					final ExtTrustRelationshipsResponseBean retrieveExtRelationshipsBean =
					responseBean.getRetrieveExtTrustRelationships();
					if (retrieveExtRelationshipsBean == null) {
						Log.e(TAG, "Trust Broker retrieve extended trust relationships response bean is null");
						TrustClientBase.this.broadcastException(this.client,
								this.returnIntent, 
								"Trust Broker retrieve extended trust relationships response bean is null");
						return;
					}
					final List<ExtTrustRelationshipBean> extTrustRelationships = 
							retrieveExtRelationshipsBean.getResult();
					if (extTrustRelationships != null)
						intent.putExtra(IInternalTrustClient.INTENT_RETURN_VALUE_KEY, 
								extTrustRelationships.toArray(
										new ExtTrustRelationshipBean[extTrustRelationships.size()]));
					else // return empty array
						intent.putExtra(IInternalTrustClient.INTENT_RETURN_VALUE_KEY, 
								new ExtTrustRelationshipBean[0]);
					break;
					
				case RETRIEVE_EXT_TRUST_RELATIONSHIP:

					final ExtTrustRelationshipResponseBean retrieveExtRelationshipBean =
					responseBean.getRetrieveExtTrustRelationship();
					if (retrieveExtRelationshipBean == null) {
						Log.e(TAG, "Trust Broker retrieve extended trust relationship response bean is null");
						TrustClientBase.this.broadcastException(this.client,
								this.returnIntent, 
								"Trust Broker retrieve extended trust relationship response bean is null");
						return;
					}
					final ExtTrustRelationshipBean extTrustRelationship = retrieveExtRelationshipBean.getResult();
					if (extTrustRelationship != null)
						intent.putExtra(IInternalTrustClient.INTENT_RETURN_VALUE_KEY, 
								(Parcelable) extTrustRelationship);
					break;
					
				default:
					
					Log.e(TAG, "Unexpected method in Trust Broker response bean: "
							+ responseBean.getMethodName());
					TrustClientBase.this.broadcastException(this.client,
							this.returnIntent, 
							"Unexpected method in Trust Broker response bean: "
							+ responseBean.getMethodName());
					return;
				}
				
			} else if (payload instanceof TrustEvidenceCollectorResponseBean) {
				// TrustEvidenceCollector response bean
				final TrustEvidenceCollectorResponseBean responseBean = 
						(TrustEvidenceCollectorResponseBean) payload;
				Log.d(TrustClientBase.TAG, 
						"receiveResult: payload.methodName=" 
								+ responseBean.getMethodName());
				switch (responseBean.getMethodName()) {

				case ADD_DIRECT_EVIDENCE:
					
					// Nothing to do
					break;
				default:

					Log.e(TAG, "Unexpected method in Trust Evidence Collector response bean: "
							+ responseBean.getMethodName());
					TrustClientBase.this.broadcastException(this.client,
							this.returnIntent, 
							"Unexpected method in Trust Evidence Collector response bean: "
							+ responseBean.getMethodName());
					return;
				}
				
			} else {
				
				Log.e(TrustClientBase.TAG, "Received unexpected response bean in result: "
						+ ((payload != null) ? payload.getClass() : "null"));
				TrustClientBase.this.broadcastException(this.client,
						this.returnIntent, 
						"Received unexpected response bean in result: "
						+ ((payload != null) ? payload.getClass() : "null"));
				return;
			}
			
			if (TrustClientBase.this.restrictBroadcast)
				intent.setPackage(this.client);
			intent.addFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
			Log.d(TrustClientBase.TAG, "receiveResult: broadcasting intent " + intent); 
			TrustClientBase.this.androidContext.sendBroadcast(intent);
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveError(org.societies.api.comm.xmpp.datatypes.Stanza, org.societies.api.comm.xmpp.exceptions.XMPPError)
		 */
		@Override
		public void receiveError(Stanza stanza, XMPPError error) {
			
			Log.e(TrustClientBase.TAG, "receiveError: stanza=" + stanza
					+ ", error=" + error);
			final String exceptionMessage;
			if (error != null)
				exceptionMessage = error.getStanzaErrorString(); // TODO getGenericText???
			else
				exceptionMessage = "Unspecified XMPPError";
			TrustClientBase.this.broadcastException(this.client,
					this.returnIntent, exceptionMessage);
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveInfo(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.api.comm.xmpp.datatypes.XMPPInfo)
		 */
		@Override
		public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
			
			Log.d(TrustClientBase.TAG, "receiveInfo: stanza=" + stanza
					+ ", node=" + node + ", info=" + info);
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
		 */
		@Override
		public void receiveMessage(Stanza stanza, Object payload) {
			
			Log.d(TrustClientBase.TAG, "receiveMessage: stanza=" + stanza
					+ ", payload=" + payload);
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
		 */
		@Override
		public void receiveItems(Stanza stanza, String node, List<String> items) {
			
			Log.d(TrustClientBase.TAG, "receiveItems: stanza=" + stanza
					+ ", node=" + node + ", items=" + items);
		}
	}
}

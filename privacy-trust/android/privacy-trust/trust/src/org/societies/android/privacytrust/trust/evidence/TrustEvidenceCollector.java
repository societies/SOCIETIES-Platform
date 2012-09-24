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
package org.societies.android.privacytrust.trust.evidence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.xml.datatype.DatatypeFactory;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.android.privacytrust.trust.org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.internal.privacytrust.trust.remote.TrustModelBeanTranslator;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.AddDirectEvidenceRequestBean;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.AddIndirectEvidenceRequestBean;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.MethodName;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorRequestBean;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorResponseBean;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.TrustEvidenceTypeBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.identity.IdentityManagerImpl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Android implementation of the {@link ITrustEvidenceCollector} service.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4
 */
public class TrustEvidenceCollector extends Service 
	implements ITrustEvidenceCollector {
	
	private static final String TAG = TrustEvidenceCollector.class.getName();
	
	private static final List<String> ELEMENT_NAMES = Arrays.asList(
			"trustEvidenceCollectorRequestBean", "trustEvidenceCollectorResponseBean");
	
	private static final List<String> NAMESPACES = Arrays.asList(
            "http://societies.org/api/internal/schema/privacytrust/trust/evidence/collector",
            "http://societies.org/api/internal/schema/privacytrust/trust/model");
	
	private static final List<String> PACKAGES = Arrays.asList(
			"org.societies.api.internal.schema.privacytrust.trust.evidence.collector",
	        "org.societies.api.internal.schema.privacytrust.trust.model");
	
	// TODO Don't hardcode!
	private String cloudNodeJid = "jane.societies.local";
	
	private IIdentity cloudNodeId;
	
	/** The Client Comm Mgr service reference. */
	private ClientCommunicationMgr clientCommMgr;
	
	/** The exception from the Client Comm Mgr callback. */
	private Exception callbackException;
	
	/** The latch for waiting the Client Comm Mgr callback. */
	private CountDownLatch cdLatch;
	
	/** The Client Comm Mgr callback. */
	private final ICommCallback callback = new ICommCallback() {
		
		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
		 */
		public List<String> getXMLNamespaces() {
			
			return TrustEvidenceCollector.NAMESPACES;
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
		 */
		public List<String> getJavaPackages() {
			
			return TrustEvidenceCollector.PACKAGES;
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveResult(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
		 */
		public void receiveResult(Stanza stanza, Object payload) {

			Log.d(TrustEvidenceCollector.TAG, "receiveResult:stanza="
					+ this.stanzaToString(stanza)
					+ ",payload.getClass=" 
					+ payload.getClass().getName());

			if (payload instanceof TrustEvidenceCollectorResponseBean) {

				TrustEvidenceCollectorResponseBean responseBean = 
						(TrustEvidenceCollectorResponseBean) payload;
				Log.d(TrustEvidenceCollector.TAG, 
						"receiveResult:payload.methodName=" 
								+ responseBean.getMethodName());
				switch (responseBean.getMethodName()) {

				case ADD_DIRECT_EVIDENCE:
				case ADD_INDIRECT_EVIDENCE:
					TrustEvidenceCollector.this.callbackException = null;
					break;
				default:
					TrustEvidenceCollector.this.callbackException = 
						new TrustEvidenceCollectorCommException("Unsupported method in response bean: "
							+ responseBean.getMethodName());
				}
			} else {
				TrustEvidenceCollector.this.callbackException = 
						new TrustEvidenceCollectorCommException("Unsupported payload type: "
								+ payload.getClass().getName());
			}
			TrustEvidenceCollector.this.cdLatch.countDown();
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveError(org.societies.api.comm.xmpp.datatypes.Stanza, org.societies.api.comm.xmpp.exceptions.XMPPError)
		 */
		public void receiveError(Stanza stanza, XMPPError error) {
			
			Log.d(TrustEvidenceCollector.TAG, "receiveError:stanza="
					+ this.stanzaToString(stanza) 
					+ ",error=" + error);
			if (error != null)
				TrustEvidenceCollector.this.callbackException = error;
			else
				TrustEvidenceCollector.this.callbackException = 
					new TrustEvidenceCollectorCommException("Unspecified XMPPError");
			TrustEvidenceCollector.this.cdLatch.countDown();
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveInfo(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.api.comm.xmpp.datatypes.XMPPInfo)
		 */
		public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
			
			Log.d(TrustEvidenceCollector.TAG, "receiveInfo with stanza "
					+ this.stanzaToString(stanza));
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
		 */
		public void receiveMessage(Stanza stanza, Object payload) {
			
			Log.d(TrustEvidenceCollector.TAG, "receiveMessage with stanza "
					+ this.stanzaToString(stanza));
		}

		/*
		 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
		 */
		public void receiveItems(Stanza stanza, String node, List<String> items) {
			
			Log.d(TrustEvidenceCollector.TAG, "receiveItems with stanza "
					+ this.stanzaToString(stanza)
					+ ", node " + node
					+ " and items: ");
			for(String item : items)
				Log.d(TrustEvidenceCollector.TAG, item);
		}
		
		private String stanzaToString(Stanza stanza) {
			
			final StringBuilder sb = new StringBuilder();
			sb.append("[");
			sb.append("id=" + stanza.getId());
			sb.append(",");
			sb.append("from=" + stanza.getFrom());
			sb.append(",");
			sb.append("to=" + stanza.getTo());
			sb.append("]");
			
			return sb.toString();
		}
	};
	
	private final IBinder binder = new LocalBinder();

	/*
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
	
		return this.binder;
	}
	
	/*
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate () {
		
		Log.i(TAG, "Starting");
		try {
			if (this.cloudNodeId == null)
				this.cloudNodeId = IdentityManagerImpl.staticfromJid(cloudNodeJid);
			Log.d(TAG, "Hardcoded cloud node IIdentity " + this.cloudNodeId);
			if (this.clientCommMgr == null)
				this.clientCommMgr = new ClientCommunicationMgr(this);
			this.clientCommMgr.register(ELEMENT_NAMES, this.callback);
		} catch (InvalidFormatException ife) {
			
			final String errorText = "Failed to create cloud node IIdentity: "
					+ ife.getLocalizedMessage();
			Log.e(TAG, errorText, ife);
			Toast.makeText(this, errorText, Toast.LENGTH_LONG).show();
			throw new RuntimeException(ife);
		}
	}
	
	/*
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		
		Log.i(TAG, "Stopping");
		this.cloudNodeId = null;
		if (this.clientCommMgr != null) {
			this.clientCommMgr.unregister(ELEMENT_NAMES, this.callback);
			this.clientCommMgr = null;
		}
	}

	/*
	 * @see org.societies.android.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addDirectEvidence(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId, org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable)
	 */
	public void addDirectEvidence(final TrustedEntityId teid, 
			final TrustEvidenceType type, final Date timestamp,
			final Serializable info) throws TrustException {
		
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		
		final StringBuilder sb = new StringBuilder();
		sb.append("Adding direct trust evidence:");
		sb.append("teid=");
		sb.append(teid);
		sb.append(", type=");
		sb.append(type);
		sb.append(", timestamp=");
		sb.append(timestamp);
		sb.append(", info=");
		sb.append(info);
		Log.d(TAG, sb.toString());

		try {
			final AddDirectEvidenceRequestBean addEvidenceBean = 
					new AddDirectEvidenceRequestBean();
			// 1. teid
			addEvidenceBean.setTeid(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(teid));
			// 2. type
			addEvidenceBean.setType(TrustEvidenceTypeBean.valueOf(type.toString()));
			// 3. timestamp
			final GregorianCalendar gregCal = new GregorianCalendar();
			gregCal.setTime(timestamp);
			addEvidenceBean.setTimestamp(
					new DatatypeFactoryImpl().newXMLGregorianCalendar(gregCal));
			// 4. info
			if (TrustEvidenceType.RATED.equals(type))
				addEvidenceBean.setInfo(serialise(info));

			final TrustEvidenceCollectorRequestBean requestBean = 
					new TrustEvidenceCollectorRequestBean();
			requestBean.setMethodName(MethodName.ADD_DIRECT_EVIDENCE);
			requestBean.setAddDirectEvidence(addEvidenceBean);

			final Stanza stanza = new Stanza(this.cloudNodeId);
			this.clientCommMgr.sendIQ(stanza, IQ.Type.GET, requestBean, this.callback);
			this.cdLatch = new CountDownLatch(1);
			this.cdLatch.await();
			if (this.callbackException != null)
				throw this.callbackException;
			
		} catch (IOException ioe) {

			final String errorMessage = 
					"Could not add direct trust evidence for entity " + teid
					+ ": Could not serialise info object into byte[]: " 
					+ ioe.getLocalizedMessage();
			Log.e(TAG, errorMessage);
			throw new TrustEvidenceCollectorCommException(errorMessage, ioe);
			
		} catch (Exception e) {
			
			final String errorMessage =
					"Could not add direct trust evidence for entity " + teid
					+ ": " + e.getLocalizedMessage();
			Log.e(TAG, errorMessage);
			throw new TrustEvidenceCollectorCommException(errorMessage, e);
		}
	}

	/*
	 * @see org.societies.android.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addIndirectEvidence(java.lang.String, org.societies.api.internal.privacytrust.trust.model.TrustedEntityId, org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable)
	 */
	public void addIndirectEvidence(final String source, 
			final TrustedEntityId teid, final TrustEvidenceType type,
			final Date timestamp, final Serializable info) 
					throws TrustException {
		
		if (source == null)
			throw new NullPointerException("source can't be null");
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		
		final StringBuilder sb = new StringBuilder();
		sb.append("Adding indirect trust evidence:");
		sb.append("source=");
		sb.append(source);
		sb.append(",teid=");
		sb.append(teid);
		sb.append(", type=");
		sb.append(type);
		sb.append(", timestamp=");
		sb.append(timestamp);
		sb.append(", info=");
		sb.append(info);
		Log.d(TAG, sb.toString());

		try {
			final AddIndirectEvidenceRequestBean addEvidenceBean = 
					new AddIndirectEvidenceRequestBean();
			// 1. source
			addEvidenceBean.setSource(source);
			// 2. teid
			addEvidenceBean.setTeid(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(teid));
			// 3. type
			addEvidenceBean.setType(TrustEvidenceTypeBean.valueOf(type.toString()));
			// 4. timestamp
			final GregorianCalendar gregCal = new GregorianCalendar();
			gregCal.setTime(timestamp);
			addEvidenceBean.setTimestamp(
					DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal));
			// 5. info
			if (TrustEvidenceType.RATED.equals(type))
				addEvidenceBean.setInfo(serialise(info));

			final TrustEvidenceCollectorRequestBean requestBean = 
					new TrustEvidenceCollectorRequestBean();
			requestBean.setMethodName(MethodName.ADD_INDIRECT_EVIDENCE);
			requestBean.setAddIndirectEvidence(addEvidenceBean);

			final Stanza stanza = new Stanza(this.cloudNodeId);
			this.clientCommMgr.sendIQ(stanza, IQ.Type.GET, requestBean, this.callback);
			this.cdLatch = new CountDownLatch(1);
			this.cdLatch.await();
			if (this.callbackException != null)
				throw this.callbackException;
			
		} catch (IOException ioe) {

			throw new TrustEvidenceCollectorCommException(
					"Could not add indirect trust evidence for entity " + teid
					+ ": Could not serialise info object into byte[]: " 
					+ ioe.getLocalizedMessage(), ioe);

		} catch (Exception e) {
			
			throw new TrustEvidenceCollectorCommException(
					"Could not add indirect trust evidence for entity " + teid
					+ ": " + e.getLocalizedMessage(), e);
		}
	}

	/*
	 * @see org.societies.android.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addTrustRating(org.societies.api.identity.IIdentity, org.societies.api.identity.IIdentity, double, java.util.Date)
	 */
	public void addTrustRating(final IIdentity trustor, 
			final IIdentity trustee, final double rating, Date timestamp)
					throws TrustException {
		
		if (trustor == null)
			throw new NullPointerException("trustor can't be null");
		if (trustee == null)
			throw new NullPointerException("trustee can't be null");
		
		if (!IdentityType.CSS.equals(trustor.getType()))
			throw new IllegalArgumentException("trustor is not a CSS");
		if (!IdentityType.CSS.equals(trustee.getType()) && !IdentityType.CIS.equals(trustee.getType()))
			throw new IllegalArgumentException("trustee is neither a CSS nor a CIS");
		if (rating < 0d || rating > 1d)
			throw new IllegalArgumentException("rating out of range [0,1]");
		
		// if timestamp is null assign current time
		if (timestamp == null)
			timestamp = new Date();
		
		final TrustedEntityType entityType;
		if (IdentityType.CSS.equals(trustee.getType()))
			entityType = TrustedEntityType.CSS;
		else // if (IdentityType.CIS.equals(trustee.getType()))
			entityType = TrustedEntityType.CIS;
		final TrustedEntityId teid = new TrustedEntityId(trustor.toString(), entityType, trustee.toString());
		this.addDirectEvidence(teid, TrustEvidenceType.RATED, timestamp, new Double(rating));
	}

	/*
	 * @see org.societies.android.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector#addTrustRating(org.societies.api.identity.IIdentity, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, double, java.util.Date)
	 */
	public void addTrustRating(final IIdentity trustor, 
			final ServiceResourceIdentifier trustee, final double rating,
			Date timestamp) throws TrustException {
		
		if (trustor == null)
			throw new NullPointerException("trustor can't be null");
		if (trustee == null)
			throw new NullPointerException("trustee can't be null");
		
		if (!IdentityType.CSS.equals(trustor.getType()))
			throw new IllegalArgumentException("trustor is not a CSS");
		if (rating < 0d || rating > 1d)
			throw new IllegalArgumentException("rating is not in the range of [0,1]");
		
		// if timestamp is null assign current time
		if (timestamp == null)
			timestamp = new Date();
		
		final TrustedEntityType entityType = TrustedEntityType.SVC;
		final TrustedEntityId teid = new TrustedEntityId(trustor.toString(), entityType, trustee.toString());
		this.addDirectEvidence(teid, TrustEvidenceType.RATED, timestamp, new Double(rating));
	}
	
	public class LocalBinder extends Binder {
		
		public ITrustEvidenceCollector getService() {
			
			return TrustEvidenceCollector.this;
		}
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
}

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
package org.societies.privacytrust.remote.trust.evidence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.privacytrust.trust.TrustException;
import org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.internal.privacytrust.trust.evidence.remote.ITrustEvidenceCollectorRemote;
import org.societies.api.internal.privacytrust.trust.evidence.remote.ITrustEvidenceCollectorRemoteCallback;
import org.societies.api.internal.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.internal.privacytrust.trust.remote.TrustModelBeanTranslator;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.AddDirectEvidenceRequestBean;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.AddIndirectEvidenceRequestBean;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.MethodName;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.TrustEvidenceCollectorRequestBean;
import org.societies.api.internal.schema.privacytrust.trust.evidence.collector.TrustEvidenceTypeBean;
import org.societies.privacytrust.remote.PrivacyTrustCommClientCallback;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.3
 */
public class TrustEvidenceCollectorCommClient implements
		ITrustEvidenceCollectorRemote {
	
	/** The logging facility. */
	private static Logger LOG = LoggerFactory.getLogger(TrustEvidenceCollectorCommClient.class);
	
	/** The Communications Mgr service reference. */
	private ICommManager commManager;
	
	private PrivacyTrustCommClientCallback privacyTrustCommClientCallback;
	
	private TrustEvidenceCollectorCommClientCallback trustEvidenceCollectorCommClientCallback;
	
	TrustEvidenceCollectorCommClient() {
		
		LOG.info(this.getClass() + " instantiated");
	}
	
	/*
	 * @see org.societies.api.internal.privacytrust.trust.evidence.remote.ITrustEvidenceCollectorRemote#addDirectEvidence(org.societies.api.internal.privacytrust.trust.model.TrustedEntityId, org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable, org.societies.api.internal.privacytrust.trust.evidence.remote.ITrustEvidenceCollectorRemoteCallback)
	 */
	@Override
	public void addDirectEvidence(final TrustedEntityId teid, 
			final TrustEvidenceType type, final Date timestamp, 
			final Serializable info, 
			final ITrustEvidenceCollectorRemoteCallback callback)
					throws TrustException {
		
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Adding direct trust evidence for entity " + teid);
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().fromJid(teid.getTrustorId()); 
			final Stanza stanza = new Stanza(toIdentity);
			// uncomment for local testing only (1)
			//final Stanza stanza = new Stanza(this.commManager.getIdManager().getThisNetworkNode());
			
			this.trustEvidenceCollectorCommClientCallback.addClient(stanza.getId(), callback);
			
			final AddDirectEvidenceRequestBean addEvidenceBean = new AddDirectEvidenceRequestBean();
			// 1. teid
			addEvidenceBean.setTeid(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(teid));
			// uncomment for local testing only (2)
			//addEvidenceBean.getTeid().setTrustorId(this.commManager.getIdManager().getThisNetworkNode().toString());
			// 2. type
			addEvidenceBean.setType(TrustEvidenceTypeBean.valueOf(type.toString()));
			// 3. timestamp
			// TODO Uncomment once #1310 is resolved
			//final GregorianCalendar gregCal = new GregorianCalendar();
			//gregCal.setTime(timestamp);
			//addEvidenceBean.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal));
			// 4. info
			if (TrustEvidenceType.RATED.equals(type))
				addEvidenceBean.setInfo(serialise(info));
			
			final TrustEvidenceCollectorRequestBean requestBean = new TrustEvidenceCollectorRequestBean();
			requestBean.setMethodName(MethodName.ADD_DIRECT_EVIDENCE);
			requestBean.setAddDirectEvidence(addEvidenceBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.privacyTrustCommClientCallback);
		
		} catch (InvalidFormatException ife) {
			
			throw new TrustEvidenceCollectorCommException("Could not add direct trust evidence for entity " + teid
					+ ": Invalid trustorId IIdentity: " 
					+ ife.getLocalizedMessage(), ife);
		} catch (CommunicationException ce) {
			
			throw new TrustEvidenceCollectorCommException("Could not add direct trust evidence for entity " + teid
					+ ": " + ce.getLocalizedMessage(), ce);
			
		// TODO Uncomment once #1310 is resolved
/*			
		} catch (DatatypeConfigurationException dce) {
			
			throw new TrustEvidenceCollectorCommException("Could not add direct trust evidence for entity " + teid
					+ ": " + dce.getLocalizedMessage(), dce);
*/
		} catch (IOException ioe) {
		
			throw new TrustEvidenceCollectorCommException("Could not add direct trust evidence for entity " + teid
					+ ": Could not serialise info object into byte[]: " 
					+ ioe.getLocalizedMessage(), ioe);
		}
	}

	/*
	 * @see org.societies.api.internal.privacytrust.trust.evidence.remote.ITrustEvidenceCollectorRemote#addIndirectEvidence(java.lang.String, org.societies.api.internal.privacytrust.trust.model.TrustedEntityId, org.societies.api.internal.privacytrust.trust.evidence.TrustEvidenceType, java.util.Date, java.io.Serializable)
	 */
	@Override
	public void addIndirectEvidence(final String source, 
			final TrustedEntityId teid,	final TrustEvidenceType type,
			final Date timestamp, final Serializable info,
			final ITrustEvidenceCollectorRemoteCallback callback) 
					throws TrustException {
		
		if (source == null)
			throw new NullPointerException("source can't be null");
		if (teid == null)
			throw new NullPointerException("teid can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (timestamp == null)
			throw new NullPointerException("timestamp can't be null");
		if (callback == null)
			throw new NullPointerException("callback can't be null");
		
		if (LOG.isDebugEnabled()) 
			LOG.debug("Adding indirect trust evidence for entity " + teid);
		
		try {
			final IIdentity toIdentity = 
					this.commManager.getIdManager().fromJid(teid.getTrustorId()); 
			final Stanza stanza = new Stanza(toIdentity);
			// uncomment for local testing only (1)
			//final Stanza stanza = new Stanza(this.commManager.getIdManager().getThisNetworkNode());
			
			this.trustEvidenceCollectorCommClientCallback.addClient(stanza.getId(), callback);
			
			final AddIndirectEvidenceRequestBean addEvidenceBean = new AddIndirectEvidenceRequestBean();
			// 1. source
			addEvidenceBean.setSource(source);
			// 2. teid
			addEvidenceBean.setTeid(
					TrustModelBeanTranslator.getInstance().fromTrustedEntityId(teid));
			// uncomment for local testing only (2)
			//addEvidenceBean.getTeid().setTrustorId(this.commManager.getIdManager().getThisNetworkNode().toString());
			// 3. type
			addEvidenceBean.setType(TrustEvidenceTypeBean.valueOf(type.toString()));
			// 4. timestamp
			final GregorianCalendar gregCal = new GregorianCalendar();
			gregCal.setTime(timestamp);
			addEvidenceBean.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal));
			// 5. info
			if (TrustEvidenceType.RATED.equals(type))
				addEvidenceBean.setInfo(serialise(info));
			
			final TrustEvidenceCollectorRequestBean requestBean = new TrustEvidenceCollectorRequestBean();
			requestBean.setMethodName(MethodName.ADD_INDIRECT_EVIDENCE);
			requestBean.setAddIndirectEvidence(addEvidenceBean);
			
			this.commManager.sendIQGet(stanza, requestBean, this.privacyTrustCommClientCallback);
		
		} catch (InvalidFormatException ife) {
			
			throw new TrustEvidenceCollectorCommException("Could not add indirect trust evidence for entity " + teid
					+ ": Invalid trustorId IIdentity: " 
					+ ife.getLocalizedMessage(), ife);
		} catch (CommunicationException ce) {
			
			throw new TrustEvidenceCollectorCommException("Could not add indirect trust evidence for entity " + teid
					+ ": " + ce.getLocalizedMessage(), ce);
			
		} catch (DatatypeConfigurationException dce) {
			
			throw new TrustEvidenceCollectorCommException("Could not add indirect trust evidence for entity " + teid
					+ ": " + dce.getLocalizedMessage(), dce);
		} catch (IOException ioe) {
		
			throw new TrustEvidenceCollectorCommException("Could not add indirect trust evidence for entity " + teid
					+ ": Could not serialise info object into byte[]: " 
					+ ioe.getLocalizedMessage(), ioe);
		}
	}
	
	public void setCommManager(ICommManager commManager) {
		
		this.commManager = commManager;
	}

	public void setPrivacyTrustCommClientCallback(
			PrivacyTrustCommClientCallback privacyTrustCommClientCallback) {
		
		this.privacyTrustCommClientCallback = privacyTrustCommClientCallback;
	}
	
	public void setTrustEvidenceCollectorCommClientCallback(
			TrustEvidenceCollectorCommClientCallback trustEvidenceCollectorCommClientCallback) {
		
		this.trustEvidenceCollectorCommClientCallback = trustEvidenceCollectorCommClientCallback;
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
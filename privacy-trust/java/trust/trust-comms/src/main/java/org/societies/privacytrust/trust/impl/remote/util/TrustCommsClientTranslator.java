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
package org.societies.privacytrust.trust.impl.remote.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.societies.api.internal.privacytrust.trust.model.ExtTrustRelationship;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustEvidence;
import org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator;
import org.societies.api.privacytrust.trust.model.TrustRelationship;
import org.societies.api.privacytrust.trust.model.TrustValueType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.schema.privacytrust.trust.broker.TrustQueryBean;
import org.societies.api.schema.privacytrust.trust.model.ExtTrustRelationshipBean;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceBean;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustRelationshipBean;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.2
 */
public final class TrustCommsClientTranslator {
	
	/** A constant denoting an empty binary array. */
	public static final byte[] NULL_BINARY_ARRAY = new byte[] { Byte.MIN_VALUE };
	
	private static TrustCommsClientTranslator instance = new TrustCommsClientTranslator();
	
	/*
	 * Prevents instantiation
	 */
	private TrustCommsClientTranslator() {}
	
	public static synchronized TrustCommsClientTranslator getInstance() {
		
		return instance;
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 */
	public TrustQueryBean fromTrustQuery(TrustQuery query) {
		
		final TrustQueryBean queryBean = new TrustQueryBean();
		// (required) trustorId
		queryBean.setTrustorId(TrustModelBeanTranslator.getInstance()
				.fromTrustedEntityId(query.getTrustorId()));
		// (optional) trusteeId
		if (query.getTrusteeId() != null) {
			queryBean.setTrusteeId(TrustModelBeanTranslator.getInstance()
					.fromTrustedEntityId(query.getTrusteeId()));
		}
		// (optional) trusteeType
		if (query.getTrusteeType() != null) {
			queryBean.setTrusteeType(TrustModelBeanTranslator.getInstance()
					.fromTrustedEntityType(query.getTrusteeType()));
		}
		// (optional) trustValueType
		if (query.getTrustValueType() != null) {
			queryBean.setTrustValueType(TrustModelBeanTranslator.getInstance()
					.fromTrustValueType(query.getTrustValueType()));
		}
		
		return queryBean;
	}
	
	/**
	 * 
	 * @param queryBean
	 * @return
	 * @throws MalformedTrustedEntityIdException
	 */
	public TrustQuery fromTrustQueryBean(TrustQueryBean queryBean) 
			throws MalformedTrustedEntityIdException {
		
		// (required) trustorId
		final TrustedEntityId trustorId = TrustModelBeanTranslator.getInstance()
				.fromTrustedEntityIdBean(queryBean.getTrustorId());
		// (optional) trusteeId
		final TrustedEntityId trusteeId;
		if (queryBean.getTrusteeId() != null) {
			trusteeId = TrustModelBeanTranslator.getInstance()
					.fromTrustedEntityIdBean(queryBean.getTrusteeId());
		} else {
			trusteeId = null;
		}
		// (optional) trusteeType
		final TrustedEntityType trusteeType;
		if (queryBean.getTrusteeType() != null) {
			trusteeType = TrustModelBeanTranslator.getInstance()
					.fromTrustedEntityTypeBean(queryBean.getTrusteeType());
		} else {
			trusteeType = null;
		}
		// (optional) trustValueType
		final TrustValueType trustValueType;
		if (queryBean.getTrustValueType() != null) {
			trustValueType = TrustModelBeanTranslator.getInstance()
					.fromTrustValueTypeBean(queryBean.getTrustValueType());
		} else {
			trustValueType = null;
		}
		
		return new TrustQuery(trustorId)
				.setTrusteeId(trusteeId)
				.setTrusteeType(trusteeType)
				.setTrustValueType(trustValueType);
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 * @throws IOException 
	 */
	public TrustEvidenceBean fromTrustEvidence(TrustEvidence evidence) 
			throws IOException {
		
		if (evidence == null) {
			throw new NullPointerException("evidence can't be null");
		}
		
		final TrustEvidenceBean evidenceBean = new TrustEvidenceBean();
		// (required) subjectId
		evidenceBean.setSubjectId(TrustModelBeanTranslator.getInstance()
				.fromTrustedEntityId(evidence.getSubjectId()));
		// (required) objectId
		evidenceBean.setObjectId(TrustModelBeanTranslator.getInstance()
				.fromTrustedEntityId(evidence.getObjectId()));
		// (required) type
		evidenceBean.setType(TrustModelBeanTranslator.getInstance()
				.fromTrustEvidenceType(evidence.getType()));
		// (required) timestamp
		evidenceBean.setTimestamp(evidence.getTimestamp());
		// (optional) info
		if (evidence.getInfo() != null) {
			evidenceBean.setInfo(this.serialise(evidence.getInfo()));
		} else {
			// Non-null value required for translation to Android Parcelable
			evidenceBean.setInfo(NULL_BINARY_ARRAY);
		}
		// (optional) sourceId
		if (evidence.getSourceId() != null) {
			evidenceBean.setSourceId(TrustModelBeanTranslator.getInstance()
					.fromTrustedEntityId(evidence.getSourceId()));
		}
		
		return evidenceBean;
	}
	
	/**
	 * 
	 * @param evidenceBean
	 * @return
	 * @throws MalformedTrustedEntityIdException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public TrustEvidence fromTrustEvidenceBean(TrustEvidenceBean evidenceBean) 
			throws MalformedTrustedEntityIdException, IOException, ClassNotFoundException {
		
		if (evidenceBean == null)
			throw new NullPointerException("evidenceBean can't be null");
		
		// (required) subjectId
		final TrustedEntityId subjectId = TrustModelBeanTranslator.getInstance()
				.fromTrustedEntityIdBean(evidenceBean.getSubjectId()); 
		// (required) objectId
		final TrustedEntityId objectId = TrustModelBeanTranslator.getInstance()
				.fromTrustedEntityIdBean(evidenceBean.getObjectId());
		// (required) type
		final TrustEvidenceType type = TrustModelBeanTranslator.getInstance()
				.fromTrustEvidenceTypeBean(evidenceBean.getType());
		// (required) timestamp
		final Date timestamp = evidenceBean.getTimestamp();
		// (optional) info
		final Serializable info;
		if (evidenceBean.getInfo() != null 
				&& !Arrays.equals(NULL_BINARY_ARRAY, evidenceBean.getInfo())) {
			info = this.deserialise(evidenceBean.getInfo(), this.getClass().getClassLoader());
		} else {
			info = null;
		}
		// (optional) sourceId
		final TrustedEntityId sourceId;
		if (evidenceBean.getSourceId() != null) {
			sourceId = TrustModelBeanTranslator.getInstance()
					.fromTrustedEntityIdBean(evidenceBean.getSourceId());
		} else {
			sourceId = null;
		}
		
		return new TrustEvidence(subjectId, objectId, type, timestamp, info, sourceId);
	}
	
	/**
	 * 
	 * @param relationship
	 * @return
	 * @throws IOException
	 */
	public ExtTrustRelationshipBean fromExtTrustRelationship(
			ExtTrustRelationship relationship) throws IOException {
		
		if (relationship == null)
			throw new NullPointerException("relationship can't be null");
		
		final ExtTrustRelationshipBean relationshipBean = new ExtTrustRelationshipBean();
		// base relationship
		final TrustRelationshipBean baseRelationshipBean = 
				TrustModelBeanTranslator.getInstance().fromTrustRelationship(relationship);
		// (required) trustorId
		relationshipBean.setTrustorId(baseRelationshipBean.getTrustorId());
		// (required) trusteeId
		relationshipBean.setTrusteeId(baseRelationshipBean.getTrusteeId());
		// (required) trustValueType
		relationshipBean.setTrustValueType(baseRelationshipBean.getTrustValueType());
		// (required) trustValue
		relationshipBean.setTrustValue(baseRelationshipBean.getTrustValue());
		// (required) timestamp
		relationshipBean.setTimestamp(baseRelationshipBean.getTimestamp());
		// (required) evidence
		if (!relationship.getTrustEvidence().isEmpty()) {
			for (final TrustEvidence evidence : relationship.getTrustEvidence()) {
				relationshipBean.getTrustEvidence().add(
						this.fromTrustEvidence(evidence));
			}
		} else {
			// Create non-empty evidence set required for translation to Android Parcelable
			final TrustEvidenceBean nullEvidence = new TrustEvidenceBean();
			nullEvidence.setSubjectId(relationshipBean.getTrustorId());
			nullEvidence.setObjectId(relationshipBean.getTrusteeId());
			nullEvidence.setType(TrustEvidenceTypeBean.NULL);
			nullEvidence.setTimestamp(new Date());
			nullEvidence.setInfo(NULL_BINARY_ARRAY);
			relationshipBean.getTrustEvidence().add(nullEvidence);
		}
		
		return relationshipBean;
	}
	
	public ExtTrustRelationship fromExtTrustRelationshipBean(
			ExtTrustRelationshipBean relationshipBean) throws IOException, 
			MalformedTrustedEntityIdException, ClassNotFoundException {
		
		if (relationshipBean == null)
			throw new NullPointerException("relationshipBean can't be null");
		
		// base relationship
		final TrustRelationship baseRelationship = 
				TrustModelBeanTranslator.getInstance().fromTrustRelationshipBean(relationshipBean);
		// (required) trustorId
		final TrustedEntityId trustorId = baseRelationship.getTrustorId();
		// (required) trusteeId
		final TrustedEntityId trusteeId = baseRelationship.getTrusteeId();
		// (required) trustValueType
		final TrustValueType trustValueType = baseRelationship.getTrustValueType();
		// (required) trustValue
		final Double trustValue = baseRelationship.getTrustValue();
		// (required) timestamp
		final Date timestamp = baseRelationship.getTimestamp();
		// (required) evidence
		final Set<TrustEvidence> trustEvidence = new LinkedHashSet<TrustEvidence>();
		for (final TrustEvidenceBean evidenceBean : relationshipBean.getTrustEvidence()) {
			if (TrustEvidenceTypeBean.NULL != evidenceBean.getType()) {
				trustEvidence.add(this.fromTrustEvidenceBean(evidenceBean));
			}
		}
		
		return new ExtTrustRelationship(trustorId, trusteeId, trustValueType,
				trustValue, timestamp, trustEvidence);
	}
	
	/**
	 * Serialises the specified object into a byte array
	 * 
	 * @param object
	 *            the object to serialise
	 * @return a byte array of the serialised object
	 * @throws IOException if the serialisation of the specified object fails
	 */
	public byte[] serialise(Serializable object) throws IOException {

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		
		return baos.toByteArray();
	}
	
	/**
	 * Deserialises an object from the specified byte array
	 * 
	 * @param objectData
	 *            the object to deserialise
	 * @param classLoader
	 *            the <code>ClassLoader</code> to use for deserialisation
	 * @return the deserialised object
	 * @throws IOException if the deserialisation of the specified byte array fails
	 * @throws ClassNotFoundException if the class of the deserialised object cannot be found
	 */
	public Serializable deserialise(byte[] objectData,
			ClassLoader classLoader) throws IOException, ClassNotFoundException {

		final Serializable result;
		CustomObjectInputStream ois = null;
		try {
			ois = new CustomObjectInputStream(
					new ByteArrayInputStream(objectData), classLoader);

			result = (Serializable) ois.readObject();
		} finally {
			if (ois != null)
				ois.close();
		}

		return result;
	}
	
	/**
	 * Credits go to jboss/hibernate for the inspiration
	 */
	private static final class CustomObjectInputStream extends ObjectInputStream {

		// The ClassLoader to use for deserialisation
		private ClassLoader classLoader;

		public CustomObjectInputStream(InputStream is, ClassLoader cl)
				throws IOException {
			super(is);
			this.classLoader = cl;
		}

		protected Class<?> resolveClass(ObjectStreamClass clazz)
				throws IOException, ClassNotFoundException {

			String className = clazz.getName();
			Class<?> resolvedClass = null;

			try {
				resolvedClass = this.classLoader.loadClass(className);
			} catch (ClassNotFoundException e) {
				resolvedClass = super.resolveClass(clazz);
			}

			return resolvedClass;
		}
	}
	
	/*
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		
		throw new CloneNotSupportedException("Clone is not allowed.");
	}
}
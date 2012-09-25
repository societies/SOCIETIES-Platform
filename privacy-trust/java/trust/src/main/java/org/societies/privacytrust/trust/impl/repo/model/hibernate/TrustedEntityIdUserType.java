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
package org.societies.privacytrust.trust.impl.repo.model.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.impl.repo.model.TrustedCis;
import org.societies.privacytrust.trust.impl.repo.model.TrustedCss;
import org.societies.privacytrust.trust.impl.repo.model.TrustedService;

/**
 * This class is used to serialize instances of {@link TrustedEntityId} to and from JDBC.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.6
 */
public class TrustedEntityIdUserType implements CompositeUserType {
	
	private static final Class<TrustedEntityId> TRUSTED_ENTITY_ID_CLASS = TrustedEntityId.class;
	
	private static final Class<TrustedCss> TRUSTED_CSS_CLASS = TrustedCss.class;
	
	private static final Class<TrustedCis> TRUSTED_CIS_CLASS = TrustedCis.class;
	
	private static final Class<TrustedService> TRUSTED_SERVICE_CLASS = TrustedService.class;
	
	private static final String[] PROPERTY_NAMES = { "trustor_id", "trustee_id" };
	
	private static final Type[] PROPERTY_TYPES = { Hibernate.STRING, Hibernate.STRING };

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.CompositeUserType#assemble(java.io.Serializable, org.hibernate.engine.SessionImplementor, java.lang.Object)
	 */
	@Override
	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
			throws HibernateException {
		
		return cached;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.CompositeUserType#deepCopy(java.lang.Object)
	 */
	@Override
	public Object deepCopy(Object value) throws HibernateException {
		
		return value;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.CompositeUserType#disassemble(java.lang.Object, org.hibernate.engine.SessionImplementor)
	 */
	@Override
	public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException {
		
		return (Serializable) value;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.CompositeUserType#equals(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		
		if (x == y)
			return true;
		
		if (null == x || null == y)
			return false;
		
		return x.equals(y);
	}
	
	/* (non-Javadoc)
	 * @see org.hibernate.usertype.CompositeUserType#getPropertyNames()
	 */
	@Override
	public String[] getPropertyNames() {
		
		return TrustedEntityIdUserType.PROPERTY_NAMES;
	}
	
	/* (non-Javadoc)
	 * @see org.hibernate.usertype.CompositeUserType#getPropertyTypes()
	 */
	@Override
	public Type[] getPropertyTypes() {
		
		return TrustedEntityIdUserType.PROPERTY_TYPES;
	}
	
	/* (non-Javadoc)
	 * @see org.hibernate.usertype.CompositeUserType#getPropertyValue(java.lang.Object, int)
	 */
	@Override
	public Object getPropertyValue(Object component, int property) throws HibernateException {
		
		final String result;
		final TrustedEntityId teid = (TrustedEntityId) component;
		
		switch (property) {

		case 0:
			result = teid.getTrustorId();
			break;

		case 1:
			result = teid.getTrusteeId();
			break;

		default:
			throw new IllegalArgumentException("Unknown property: " + property);

		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.CompositeUserType#hashCode(java.lang.Object)
	 */
	@Override
	public int hashCode(Object x) throws HibernateException {

		return x.hashCode();
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.CompositeUserType#isMutable()
	 */
	@Override
	public boolean isMutable() {

		return false;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.CompositeUserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], org.hibernate.engine.SessionImplementor, java.lang.Object)
	 */
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		
		final String trustorId = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
		final String trusteeId = (String) Hibernate.STRING.nullSafeGet(rs, names[1]);
		
		if (trustorId == null || trusteeId == null)
			return null;
		
		final TrustedEntityType entityType;
		if (TRUSTED_CSS_CLASS.equals(owner.getClass()))
			entityType = TrustedEntityType.CSS;
		else if (TRUSTED_CIS_CLASS.equals(owner.getClass()))
			entityType = TrustedEntityType.CIS;
		else if (TRUSTED_SERVICE_CLASS.equals(owner.getClass()))
			entityType = TrustedEntityType.SVC;
		else
			entityType = TrustedEntityType.LGC;
		try {
			return new TrustedEntityId(trustorId, entityType, trusteeId);
		} catch (MalformedTrustedEntityIdException mteide) {
			throw new HibernateException(
					"Could not create TrustedEntityId instance from stored values", mteide);
		}
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.CompositeUserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int, org.hibernate.engine.SessionImplementor)
	 */
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		
		final TrustedEntityId teid = (TrustedEntityId) value;
		
		Hibernate.STRING.nullSafeSet(st, teid.getTrustorId(), index);
		Hibernate.STRING.nullSafeSet(st, teid.getTrusteeId(), index + 1);
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.CompositeUserType#replace(java.lang.Object, java.lang.Object, org.hibernate.engine.SessionImplementor, java.lang.Object)
	 */
	@Override
	public Object replace(Object original, Object target, SessionImplementor session, Object owner)
			throws HibernateException {
		
		return original;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.CompositeUserType#returnedClass()
	 */
	@Override
	public Class<?> returnedClass() {
		
		return TrustedEntityIdUserType.TRUSTED_ENTITY_ID_CLASS;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.CompositeUserType#setPropertyValue(java.lang.Object, int, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
		
        throw new UnsupportedOperationException("TrustedEntityId is immutable");
    }
}
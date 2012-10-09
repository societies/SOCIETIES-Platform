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
package org.societies.context.user.db.impl.model.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.societies.api.context.model.CtxEntityIdentifier;

/**
 * Describe your class here...
 *
 * @author Pavlos Kosmides
 *
 */
public class CtxEntityIdentifierCompositeType implements CompositeUserType {

    private static final Class<CtxEntityIdentifier> CTX_ENTITY_ID_CLASS = CtxEntityIdentifier.class;
	
	private static final String[] PROPERTY_NAMES = { "owner_id", "type", "object_number" };
	
	private static final Type[] PROPERTY_TYPES = { Hibernate.STRING, Hibernate.STRING, Hibernate.LONG };

	/*
	 * @see org.hibernate.usertype.CompositeUserType#assemble(java.io.Serializable, org.hibernate.engine.SessionImplementor, java.lang.Object)
	 */
	@Override
	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
			throws HibernateException {
		
		return cached;
	}

	/*
	 * @see org.hibernate.usertype.CompositeUserType#deepCopy(java.lang.Object)
	 */
	@Override
	public Object deepCopy(Object value) throws HibernateException {
		
		return value;
	}

	/*
	 * @see org.hibernate.usertype.CompositeUserType#disassemble(java.lang.Object, org.hibernate.engine.SessionImplementor)
	 */
	@Override
	public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException {
		
		return (Serializable) value;
	}

	/*
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
	
	/*
	 * @see org.hibernate.usertype.CompositeUserType#getPropertyNames()
	 */
	@Override
	public String[] getPropertyNames() {
		
		return PROPERTY_NAMES;
	}
	
	/*
	 * @see org.hibernate.usertype.CompositeUserType#getPropertyTypes()
	 */
	@Override
	public Type[] getPropertyTypes() {
		
		return PROPERTY_TYPES;
	}
	
	/*
	 * @see org.hibernate.usertype.CompositeUserType#getPropertyValue(java.lang.Object, int)
	 */
	@Override
	public Object getPropertyValue(Object component, int property) throws HibernateException {
		
		final Object result;
		final CtxEntityIdentifier id = (CtxEntityIdentifier) component;
		
		switch (property) {

		case 0:
			result = id.getOwnerId();
			break;

		case 1:
			result = id.getType();
			break;	
			
		case 2:
			result = id.getObjectNumber();
			break;

		default:
			throw new IllegalArgumentException("Unknown property: " + property);

		}
		
		return result;
	}

	/*
	 * @see org.hibernate.usertype.CompositeUserType#hashCode(java.lang.Object)
	 */
	@Override
	public int hashCode(Object x) throws HibernateException {

		return x.hashCode();
	}

	/*
	 * @see org.hibernate.usertype.CompositeUserType#isMutable()
	 */
	@Override
	public boolean isMutable() {

		return false;
	}

	/*
	 * @see org.hibernate.usertype.CompositeUserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], org.hibernate.engine.SessionImplementor, java.lang.Object)
	 */
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		
		final String ownerId = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
		final String type = (String) Hibernate.STRING.nullSafeGet(rs, names[1]);
		final Long objectNumber = (Long) Hibernate.LONG.nullSafeGet(rs, names[2]);
		
		if (ownerId == null || type == null || objectNumber == null)
			return null;
		
		return new CtxEntityIdentifier(ownerId, type, objectNumber);
	}

	/*
	 * @see org.hibernate.usertype.CompositeUserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int, org.hibernate.engine.SessionImplementor)
	 */
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		
		final CtxEntityIdentifier id = (CtxEntityIdentifier) value;
		
		Hibernate.STRING.nullSafeSet(st, id.getOwnerId(), index);
		Hibernate.STRING.nullSafeSet(st, id.getType(), index + 1);
		Hibernate.LONG.nullSafeSet(st, id.getObjectNumber(), index + 2);
	}

	/*
	 * @see org.hibernate.usertype.CompositeUserType#replace(java.lang.Object, java.lang.Object, org.hibernate.engine.SessionImplementor, java.lang.Object)
	 */
	@Override
	public Object replace(Object original, Object target, SessionImplementor session, Object owner)
			throws HibernateException {
		
		return original;
	}

	/*
	 * @see org.hibernate.usertype.CompositeUserType#returnedClass()
	 */
	@Override
	public Class<?> returnedClass() {
		
		return CTX_ENTITY_ID_CLASS;
	}

	/*
	 * @see org.hibernate.usertype.CompositeUserType#setPropertyValue(java.lang.Object, int, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
		
        throw new UnsupportedOperationException("CtxEntityIdentifier is immutable");
    }
}
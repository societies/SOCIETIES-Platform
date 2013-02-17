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
package org.societies.context.community.db.impl.model.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxBondOriginType;
import org.societies.api.context.model.CtxModelType;

/**
 * Creates CtxBond as a composite type for hibernate
 *
 * @author <a href="mailto:pkosmidis@cn.ntua.gr">Pavlos Kosmides</a> (ICCS)
 * @since 1.0
 */
public class CtxBondCompositeType implements CompositeUserType {

    private static final Class<CtxBond> CTX_BOND_CLASS = CtxBond.class;
	
	private static final String[] PROPERTY_NAMES = { "bond_model_type", "bond_type", "bond_origin_type" };
	
	private static final Type[] PROPERTY_TYPES = { Hibernate.STRING, Hibernate.STRING, Hibernate.STRING };

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
		final CtxBond bond = (CtxBond) component;
		
		switch (property) {

		case 0:
			result = bond.getModelType().toString();
			break;

		case 1:
			result = bond.getType();
			break;	
			
		case 2:
			result = bond.getOriginType().toString();
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
		
//		final CtxModelType modelType = (CtxModelType) Hibernate.STRING.nullSafeGet(rs, names[0]);
		final CtxModelType modelType = CtxModelType.valueOf((String) Hibernate.STRING.nullSafeGet(rs, names[0]));
		final String type = (String) Hibernate.STRING.nullSafeGet(rs, names[1]);
//		final CtxBondOriginType originType = (CtxBondOriginType) Hibernate.STRING.nullSafeGet(rs, names[2]);
		final CtxBondOriginType originType = CtxBondOriginType.valueOf((String) Hibernate.STRING.nullSafeGet(rs, names[2]));
		
		if (modelType == null || type == null || originType == null)
			return null;

		return new CtxBond(modelType, type, originType);
	}

	/*
	 * @see org.hibernate.usertype.CompositeUserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int, org.hibernate.engine.SessionImplementor)
	 */
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		
		final CtxBond bond = (CtxBond) value;
		
		Hibernate.STRING.nullSafeSet(st, bond.getModelType().toString(), index);
		Hibernate.STRING.nullSafeSet(st, bond.getType(), index + 1);
		Hibernate.STRING.nullSafeSet(st, bond.getOriginType().toString(), index + 2);
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
		
		return CTX_BOND_CLASS;
	}

	/*
	 * @see org.hibernate.usertype.CompositeUserType#setPropertyValue(java.lang.Object, int, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
		
        throw new UnsupportedOperationException("CrxBond is immutable");
    }
}
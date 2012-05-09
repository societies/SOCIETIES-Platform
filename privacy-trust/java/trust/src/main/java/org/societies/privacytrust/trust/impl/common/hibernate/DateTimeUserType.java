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
package org.societies.privacytrust.trust.impl.common.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * Hibernate {@link UserType} to persist {@link java.util.Date Date} objects as 
 * {@link java.sql.Timestamp Timestamps}.
 * <p>
 * The default behaviour of Hibernate is to return the <code>Timestamp</code>
 * object retrieved from the underlying Resultset. However, because of the
 * extra precision (nanosecond) of Timestamp, comparison methods (including
 * <code>equals</code> & <code>compareTo</code>) fail when used with Date
 * instances. This <code>UserType</code> will convert the Timestamp back into
 * Date instances (losing the extra precision) before returning the data to the 
 * user.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.8
 */
public class DateTimeUserType implements UserType {
	
	private static final Class<Date> DATE_CLASS = Date.class;

	private static final int[] SQL_TYPES = new int[] { Types.TIMESTAMP };

	/*
	 * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
	 */
	@Override
	public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
		
        return nullSafeDateDeepCopy(cached);
    }
	
	/*
	 * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
	 */
	@Override
	public Object deepCopy(Object value) throws HibernateException {

		return nullSafeDateDeepCopy(value);
	}
	
	/*
	 * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
	 */
	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		
        return (Serializable) nullSafeDateDeepCopy(value);
    }
	
	/*
	 * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		
		if (x == y)
			return true;
		if (x == null || y == null)
			return false;

		return x.equals(y);
	}
	
	/*
	 * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
	 */
	@Override
    public int hashCode(Object x) throws HibernateException {

        return x.hashCode();
    }

	/*
	 * @see org.hibernate.usertype.UserType#isMutable()
	 */
	@Override
	public boolean isMutable() {

		return true;
	}
	
	/*
	 * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
	 */
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException { 
			
		final Timestamp timestamp = (Timestamp) Hibernate.TIMESTAMP.nullSafeGet(rs, names[0]);      

		// return the value as a java.util.Date (dropping the nanoseconds)
		return (timestamp != null) ? new Date(timestamp.getTime()) : null; 
	}

	
	/*
	 * (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
	 */
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException {
			
		if (value == null) {
		
			Hibernate.TIMESTAMP.nullSafeSet(st, null, index);
			return;
		}

		// make sure the received value is of the right type
		if (!DATE_CLASS.isAssignableFrom(value.getClass()))
			throw new IllegalArgumentException("Received value is not of type '"
					+ DATE_CLASS + "' but '" + value.getClass() + "'");

		Timestamp timestamp = null;
		if (value instanceof Timestamp)
			timestamp = (Timestamp) value;
		else
			timestamp = new Timestamp(((Date) value).getTime());

		Hibernate.TIMESTAMP.nullSafeSet(st, timestamp, index);
	}
	
	/*
	 * @see org.hibernate.usertype.UserType#replace(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
		
        return nullSafeDateDeepCopy(original);
    }
	
	/*
	 * @see org.hibernate.usertype.UserType#returnedClass()
	 */
	@Override
	public Class<?> returnedClass() {
		
		return DATE_CLASS;
	}
	
	/*
	 * @see org.hibernate.usertype.UserType#sqlTypes()
	 */
	@Override
	public int[] sqlTypes() {
		
		return SQL_TYPES;
	}
	
	private static Object nullSafeDateDeepCopy(final Object original) {
		
		return (original != null) ? ((Date) original).clone() : null;
	}
}
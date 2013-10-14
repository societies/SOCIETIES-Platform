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
package org.societies.api.internal.context.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * This class provides utility methods regarding common context types.
 *
 * @author nlia
 * @since 1.3
 * @see CtxEntityTypes
 * @see CtxAttributeTypes
 * @see CtxAssociationTypes
 */
public class CtxTypesUtil {

	/**
	 * Returns <code>true</code> if the specified type is a valid context
	 * entity type; <code>false</code> otherwise.
	 * <p>
	 * For example:
	 * <pre> 
	 * CtxTypesUtil.isValidEntityType("person") // true
	 * CtxTypesUtil.isValidEntityType("foo") // false
	 * </pre>
	 *  
	 * @param type
	 *            the context entity type to check.
	 * @return <code>true</code> if the specified type is a valid context
	 *         entity type; <code>false</code> otherwise.
	 */
	public static boolean isValidEntityType(final String type) {

		if (org.societies.api.context.model.CtxTypesUtil.isValidEntityType(type)) {
			return true;
		}
		return isValidType(CtxEntityTypes.class, type);
	}
	
	/**
	 * Returns <code>true</code> if the specified type is a valid context
	 * attribute type; <code>false</code> otherwise.
	 * <p>
	 * For example:
	 * <pre> 
	 * CtxTypesUtil.isValidAttributeType("locationCoordinates") // true
	 * CtxTypesUtil.isValidAttributeType("foo") // false
	 * </pre>
	 *  
	 * @param type
	 *            the context attribute type to check.
	 * @return <code>true</code> if the specified type is a valid context
	 *         attribute type; <code>false</code> otherwise.
	 */
	public static boolean isValidAttributeType(final String type) {

		if (org.societies.api.context.model.CtxTypesUtil.isValidAttributeType(type)) {
			return true;
		}
		return isValidType(CtxAttributeTypes.class, type);
	}
	
	/**
	 * Returns <code>true</code> if the specified type is a valid context
	 * association type; <code>false</code> otherwise.
	 * <p>
	 * For example:
	 * <pre> 
	 * CtxTypesUtil.isValidAssociationType("isFriendsWith") // true
	 * CtxTypesUtil.isValidAssociationType("foo") // false
	 * </pre>
	 *  
	 * @param type
	 *            the context association type to check.
	 * @return <code>true</code> if the specified type is a valid context
	 *         association type; <code>false</code> otherwise.
	 */
	public static boolean isValidAssociationType(final String type) {
		
		if (org.societies.api.context.model.CtxTypesUtil.isValidAssociationType(type)) {
			return true;
		}
		return isValidType(CtxAssociationTypes.class, type);
	}
	
	private static boolean isValidType(final Class<?> clazz, final String type) {

		final Field[] declaredFields = clazz.getDeclaredFields();
		for (final Field field : declaredFields) {
			if (Modifier.isPublic(field.getModifiers())
					&& Modifier.isStatic(field.getModifiers())
					&& Modifier.isFinal(field.getModifiers())
					&& String.class == field.getType()) {
				try {
					if (type.equals((String) field.get(null))) {
						return true;
					}
				} catch (Exception e) {
					return false;
				}
			}
		}

		return false;
	}
}
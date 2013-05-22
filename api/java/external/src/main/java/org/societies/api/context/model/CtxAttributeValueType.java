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
package org.societies.api.context.model;

/**
 * The constants of this enumerated type specify the data type of the {@link CtxAttribute}
 * value. To this end, the following value types have been identified:
 * <dl>
 * <dt>{@link #EMPTY}</dt>
 * <dd>No value set.</dd>
 * <dt>{@link #STRING}</dt>
 * <dd>Text value.</dd>
 * <dt>{@link #INTEGER}</dt>
 * <dd>Integer value.</dd>
 * <dt>{@link #DOUBLE}</dt>
 * <dd>Double-precision floating point numeric value.</dd>
 * <dt>{@link #BINARY}</dt>
 * <dd>Binary value, i.e. a byte[].</dd>
 * </dl>
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
public enum CtxAttributeValueType {

	/**
	 * Denotes a {@link CtxAttribute} with no value set
	 */
	EMPTY,
	
	/**
	 * Denotes a {@link CtxAttribute} value of type <code>String</code>
	 */
	STRING,
	
	/**
	 * Denotes a {@link CtxAttribute} value is of type <code>Integer</code>
	 */
	INTEGER,
	
	/**
	 * Denotes that the {@link CtxAttribute} value is of type <code>Double</code>
	 */
	DOUBLE,
	
	/**
	 * Denotes that the {@link CtxAttribute} value is of type <code>byte[]</code>
	 */
	BINARY,
	
	/**
	 * Denotes that the {@link CtxAttribute} value is of type <code>CtxAttributeComplexValue</code>
	 */
	COMPLEX,
}
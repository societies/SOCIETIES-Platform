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

import java.io.Serializable;

/**
 * This class is used to represent context bonds among members of a 
 * {@link CommunityCtxEntity}. Each bond refers to a {@link CtxAttribute}
 * or {@link CtxAssociation} of a particular type that expresses a commonality
 * shared by community members. For example, a <code>CtxBond</code> could be
 * based on the context attribute of "location". The {@link CtxBondOriginType}
 * is used to specify how the bond was identified. More specifically, a context
 * bond may have been manually set, discovered or inherited.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
public abstract class CtxBond implements Serializable {
	
	private static final long serialVersionUID = 2972314471738603009L;

	/** The context model type of this bond. */
	private final CtxModelType modelType;
	
	/** The context type of this bond. */
	private final String type;
	
	/** The origin of this bond. */
	private final CtxBondOriginType originType;
	
	/**
	 * Constructs a <code>CtxBond</code> with the specified context model type, context type,
	 * and origin.
	 * 
	 * @param modelType
	 *            the context model type, i.e. {@link CtxModelType#ATTRIBUTE} or
	 *            {@link CtxModelType#ASSOCIATION}
	 * @param type
	 *            the context type, e.g. "location"
	 * @param originType
	 *            the origin
	 * @throws NullPointerException if any of the specified parameters is
	 *         <code>null</code>
	 * @throws IllegalArgumentException if the specified modelType is not one of
	 *         {@link CtxModelType#ATTRIBUTE} or {@link CtxModelType#ASSOCIATION}
	 */
	CtxBond(CtxModelType modelType, String type, CtxBondOriginType originType) {
		
		if (modelType == null)
			throw new NullPointerException("modelType can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		if (originType == null)
			throw new NullPointerException("originType can't be null");
		
		
		/*
		if (!modelType.toString().equals(CtxModelType.ATTRIBUTE.toString()) || !modelType.toString().equals(CtxModelType.ASSOCIATION.toString()))
			throw new IllegalArgumentException("invalid modelType: "
					+ modelType + ": valid values: " + CtxModelType.ATTRIBUTE
					+ ", " + CtxModelType.ASSOCIATION);
		*/
		this.modelType = modelType;
		this.type = type;
		this.originType = originType;
	}
	
	/**
	 * Returns the context model type of this bond
	 * 
	 * @return the context model type of this bond
	 */
	public CtxModelType getModelType() {
		return this.modelType;
	}
	
	/**
	 * Return the context type of this bond
	 * 
	 * @return The context type of this bond
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * Returns the origin of this bond
	 * 
	 * @return the origin of this bond
	 */
	public CtxBondOriginType getOriginType() {
		return this.originType;
	}
}
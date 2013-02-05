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
package org.societies.android.api.internal.context;

import java.io.Serializable;
import java.util.List;

import org.societies.android.api.context.model.ACtxAssociation;
import org.societies.android.api.context.model.ACtxAttribute;
import org.societies.android.api.context.model.ACtxAttributeIdentifier;
import org.societies.android.api.context.model.ACtxEntity;
import org.societies.android.api.context.model.ACtxEntityIdentifier;
import org.societies.android.api.context.model.ACtxIdentifier;
import org.societies.android.api.context.model.ACtxModelObject;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxModelType;

/**
 * This interface provides access to current, past and future context data. The
 * past context refers to the data stored in the context history database. The
 * future context information is provided on the fly based on context
 * prediction methods. The Context Broker also supports distributed context
 * queries; it is a gateway to context data and decides whether the local DB, a
 * remote DB or the Context Inference Management need to be contacted to
 * retrieve the requested context data.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.2
 */
public interface IInternalCtxClient {
 
	//Intents
	public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.platform.internalctxclient.ReturnValue";
	public static final String INTENT_RETURN_STATUS_KEY = "org.societies.android.platform.internalctxclient.ReturnStatus";

	public static final String CREATE_ASSOCIATION = "org.societies.android.platform.internalctxclient.CREATE_ASSOCIATION";
	public static final String CREATE_ATTRIBUTE = "org.societies.android.platform.internalctxclient.CREATE_ATTRIBUTE";
	public static final String CREATE_ENTITY = "org.societies.android.platform.internalctxclient.CREATE_ENTITY";
	public static final String LOOKUP_ENTITIES = "org.societies.android.platform.internalctxclient.LOOKUP_ENTITIES";
	public static final String LOOKUP = "org.societies.android.platform.internalctxclient.LOOKUP";
	public static final String REMOVE = "org.societies.android.platform.internalctxclient.REMOVE";
	public static final String RETRIEVE = "org.societies.android.platform.internalctxclient.RETRIEVE";
	public static final String UPDATE = "org.societies.android.platform.internalctxclient.UPDATE";
	public static final String UPDATE_ATTRIBUTE = "org.societies.android.platform.internalctxclient.UPDATE_ATTRIBUTE";
	public static final String UPDATE_ATTRIBUTE_VALUE_METRIC = "org.societies.android.platform.internalctxclient.UPDATE_ATTRIBUTE_VALUE_METRIC";
	
	
	//Array of interface method signatures
	String methodsArray [] = {"createAssociation(String client, String type)", 
			"createAttribute(String client, ACtxEntityIdentifier scope, String type)", 
			"createEntity(String client, String type)",
			"lookupEntities(String client, String entityType, String attribType, Serializable minAttribValue, Serializable maxAttribValue)", 
			"lookup(String client, CtxModelType modelType, String type)", 
			"remove(String client, ACtxIdentifier identifier)", 
			"retrieve(String client, ACtxIdentifier identifier)", 
			"update(String client, ACtxModelObject identifier)", 
			"updateAttribute(String client, ACtxAttributeIdentifier attributeId, Serializable value)", 
			"updateAttribute(String client, ACtxAttributeIdentifier attributeId, Serializable value, String valueMetric)"
	};

	
	/**
	 * Creates a {@link ACtxAssociation} with the specified type on the identified
	 * CSS.
	 * 
	 * @param type
	 *            the type of the context association to create
	 * @throws CtxException 
	 */
	public ACtxAssociation createAssociation(String client, String type) throws CtxException;
	
	/**
	 * Creates a {@link ACtxAttribute} with the specified type which is associated to
	 * the identified context entity (scope).
	 * 
	 * @param scope
	 *            the identifier of the context entity to associate with the new
	 *            attribute
	 * @param type
	 *            the type of the context attribute to create
	 * @throws CtxException 
	 */
	public ACtxAttribute createAttribute(String client, ACtxEntityIdentifier scope, String type) throws CtxException;
	
	/**
	 * Creates a {@link ACtxEntity} with the specified type on the identified CSS.
	 * 
	 * @param type
	 *            the type of the context entity to create
	 * @throws CtxException 
	 */
	public ACtxEntity createEntity(String client, String type) throws CtxException;
	
	/**
	 * Looks up for a list of ACtxEntities of  the specified type, containing the
	 * specified attributes
	 * 
	 * @param entityType
	 * @param attribType
	 * @param minAttribValue
	 * @param maxAttribValue
	 * @throws CtxException 
	 */
	public List<ACtxEntityIdentifier> lookupEntities(String client, String entityType, String attribType, Serializable minAttribValue, Serializable maxAttribValue) throws CtxException;

	/**
	 * Looks up for a list of ACtxModelObjects defined by the CtxModelType (ACtxEntity,
	 * ACtxAttribute, ACtxAssociation) of  the specified type.
	 * 
	 * @param modelType
	 * @param type
	 * @throws CtxException 
	 */
	public List<ACtxIdentifier> lookup(String client, CtxModelType modelType, String type) throws CtxException;

	/**
	 * Removes the specified context model object.
	 * 
	 * @param identifier
	 * @throws CtxException 
	 */
	public ACtxModelObject remove(String client, ACtxIdentifier identifier) throws CtxException;
	
	/**
	 * Retrieves the specified context model object.
	 * 
	 * @param identifier
	 * @throws CtxException 
	 */
	public ACtxModelObject retrieve(String client, ACtxIdentifier identifier) throws CtxException;
	
	/**
	 * Updates a single context model object.
	 * 
	 * @param identifier
	 * @throws CtxException 
	 */
	public ACtxModelObject update(String client, ACtxModelObject identifier) throws CtxException;
	
	/**
	 * Updates the specified attribute.
	 * 
	 * @param attributeId
	 * @param value
	 * @throws CtxException 
	 */
	public ACtxAttribute updateAttribute(String client, ACtxAttributeIdentifier attributeId, Serializable value) throws CtxException;
	
	/**
	 * Updates the specified attribute.
	 * 
	 * @param attributeId
	 * @param value
	 * @param valueMetric
	 * @throws CtxException 
	 */
	public ACtxAttribute updateAttribute(String client, ACtxAttributeIdentifier attributeId, Serializable value,
  String valueMetric) throws CtxException;


}
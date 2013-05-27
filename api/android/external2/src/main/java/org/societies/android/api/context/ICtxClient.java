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
package org.societies.android.api.context;

import java.io.Serializable;
import java.util.List;

import org.societies.api.schema.context.model.CommunityCtxEntityBean;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.context.model.IndividualCtxEntityBean;
import org.societies.android.api.context.CtxException;
import org.societies.android.api.css.manager.IServiceManager;
//import org.societies.api.context.broker.CtxAccessControlException;
import org.societies.api.schema.context.model.CtxModelTypeBean;
import org.societies.api.schema.identity.RequestorBean;


public interface ICtxClient extends IServiceManager {
	
	//Intents
	public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.platform.context.ReturnValue";
	public static final String INTENT_RETURN_STATUS_KEY = "org.societies.android.platform.context.ReturnStatus";
	public static final String INTENT_EXCEPTION_VALUE_KEY = "org.societies.android.platform.context.ExceptionValue";
	
	public static final String CREATE_ENTITY = "org.societies.android.platform.context.CREATE_ENTITY";
	public static final String CREATE_ATTRIBUTE = "org.societies.android.platform.context.CREATE_ATTRIBUTE";
	public static final String CREATE_ASSOCIATION = "org.societies.android.platform.context.CREATE_ASSOCIATION";
	public static final String LOOKUP = "org.societies.android.platform.context.LOOKUP";
	public static final String LOOKUP_CTX_ENTITY = "org.societies.android.platform.context.LOOKUP_CTX_ENTITY";
	public static final String LOOKUP_ENTITIES = "org.societies.android.platform.context.LOOKUP_ENTITIES";
	public static final String REMOVE = "org.societies.android.platform.context.REMOVE";
	public static final String RETRIEVE = "org.societies.android.platform.context.RETRIEVE";
	public static final String RETRIEVE_INDIVIDUAL_ENTITY_ID = "org.societies.android.platform.context.RETRIEVE_INDIVIDUAL_ENTITY_ID";
	public static final String RETRIEVE_COMMUNITY_ENTITY_ID = "org.societies.android.platform.context.RETRIEVE_COMMUNITY_ENTITY_ID";
	public static final String UPDATE = "org.societies.android.platform.context.UPDATE";
	
	//Array of interface method signatures
	String methodsArray [] = {"createEntity(String client, org.societies.api.schema.identity.RequestorBean requestor, String targetCss, String type)",
			"createAttribute(String client, org.societies.api.schema.identity.RequestorBean requestor, org.societies.api.schema.context.model.CtxEntityIdentifierBean scope, String type)",
			"createAssociation(String client, org.societies.api.schema.identity.RequestorBean requestor, String targetCss, String type)",
			"lookup(String client, org.societies.api.schema.identity.RequestorBean requestor, String target, org.societies.api.schema.context.model.CtxModelTypeBean modelType, String type)",
			"lookup(String client, org.societies.api.schema.identity.RequestorBean requestor, org.societies.api.schema.context.model.CtxEntityIdentifierBean entityId, org.societies.api.schema.context.model.CtxModelTypeBean modelType, String type)",
			"lookupEntities(String client, org.societies.api.schema.identity.RequestorBean requestor, String targetCss, String entityType, String attribType, Serializable minAttribValue, Serializable maxAttribValue)",
			"remove(String client, org.societies.api.schema.identity.RequestorBean requestor, org.societies.api.schema.context.model.CtxIdentifierBean identifier)",
			"retrieve(String client, org.societies.api.schema.identity.RequestorBean requestor, org.societies.api.schema.context.model.CtxIdentifierBean identifier)",
			"retrieveIndividualEntityId(String client, org.societies.api.schema.identity.RequestorBean requestor, String cssId)",
			"retrieveCommunityEntityId(String client, org.societies.api.schema.identity.RequestorBean requestor, String cisId)",
			"update(String client, org.societies.api.schema.identity.RequestorBean requestor, org.societies.api.schema.context.model.CtxModelObjectBean object)",
			"startService()",
			"stopService()"
	};

	public CtxEntityBean createEntity(String client, final RequestorBean requestor, 
			final String targetCss, final String type) throws CtxException;
	
	public CtxAttributeBean createAttribute(String client, final RequestorBean requestor, final CtxEntityIdentifierBean scope, final String type) throws CtxException;

	public CtxAssociationBean createAssociation(String client, final RequestorBean requestor, 
			final String targetCss, final String type) throws CtxException;
	
	public List<CtxIdentifierBean> lookup(String client, final RequestorBean requestor,
			final String target, final CtxModelTypeBean modelType,
			final String type) throws CtxException;
	
	 public List<CtxIdentifierBean> lookup(String client, final RequestorBean requestor, 
			 final CtxEntityIdentifierBean entityId, final CtxModelTypeBean modelType,
			 final String type) throws CtxException;

	public List<CtxEntityIdentifierBean> lookupEntities(String client, 
			final RequestorBean requestor, final String targetCss,
			final String entityType, final String attribType,
			final Serializable minAttribValue,
			final Serializable maxAttribValue) throws CtxException;
	
	public CtxModelObjectBean remove(String client, final RequestorBean requestor, 
			final CtxIdentifierBean identifier) throws CtxException;

	public CtxModelObjectBean retrieve(String client, final RequestorBean requestor, 
			final CtxIdentifierBean identifier) throws CtxException;
	
	public CtxEntityIdentifierBean retrieveIndividualEntityId(String client, 
			final RequestorBean requestor, final String cssId) throws CtxException;
	
	public CtxEntityIdentifierBean retrieveCommunityEntityId(String client, 
			final RequestorBean requestor, final String cisId) throws CtxException;

	public CtxModelObjectBean update(String client, final RequestorBean requestor,
			final CtxModelObjectBean object) throws CtxException;
	
}
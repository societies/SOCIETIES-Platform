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

import org.societies.android.api.context.model.ACommunityCtxEntity;
import org.societies.android.api.context.model.ACtxAssociation;
import org.societies.android.api.context.model.ACtxAttribute;
import org.societies.android.api.context.model.ACtxEntity;
import org.societies.android.api.context.model.ACtxEntityIdentifier;
import org.societies.android.api.context.model.ACtxIdentifier;
import org.societies.android.api.context.model.ACtxModelObject;
import org.societies.android.api.context.model.AIndividualCtxEntity;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.CtxAccessControlException;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;

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

public interface ICtxClient {
	
	//Intents
	public static final String INTENT_RETURN_VALUE_KEY = "org.societies.android.platform.ctxclient.ReturnValue";
	public static final String INTENT_RETURN_STATUS_KEY = "org.societies.android.platform.ctxclient.ReturnStatus";

	public static final String CREATE_ENTITY = "org.societies.android.platform.ctxclient.CREATE_ENTITY";
	public static final String CREATE_ATTRIBUTE = "org.societies.android.platform.ctxclient.CREATE_ATTRIBUTE";
	public static final String CREATE_ASSOCIATION = "org.societies.android.platform.ctxclient.CREATE_ASSOCIATION";
	public static final String LOOKUP = "org.societies.android.platform.ctxclient.LOOKUP";
	public static final String LOOKUP_CTX_ENTITY = "org.societies.android.platform.ctxclient.LOOKUP_CTX_ENTITY";
	public static final String LOOKUP_ENTITIES = "org.societies.android.platform.ctxclient.LOOKUP_ENTITIES";
	public static final String REMOVE = "org.societies.android.platform.ctxclient.REMOVE";
	public static final String RETRIEVE = "org.societies.android.platform.ctxclient.RETRIEVE";
	public static final String RETRIEVE_INDIVIDUAL_ENTITY_ID = "org.societies.android.platform.ctxclient.RETRIEVE_INDIVIDUAL_ENTITY_ID";
	public static final String RETRIEVE_COMMUNITY_ENTITY_ID = "org.societies.android.platform.ctxclient.RETRIEVE_COMMUNITY_ENTITY_ID";
	public static final String UPDATE = "org.societies.android.platform.ctxclient.UPDATE";
	
	//Array of interface method signatures
	String methodsArray [] = {"createEntity(String client, final Requestor requestor, final IIdentity targetCss, final String type)",
			"createAttribute(String client, final Requestor requestor, final ACtxEntityIdentifier scope, final String type)",
			"createAssociation(String client, final Requestor requestor, final IIdentity targetCss, final String type)",
			"lookup(String client, final Requestor requestor, final IIdentity target, final CtxModelType modelType, final String type)",
			"lookup(String client, final Requestor requestor, final ACtxEntityIdentifier entityId, final CtxModelType modelType, final String type)",
			"lookupEntities(String client, final Requestor requestor, final IIdentity targetCss, final String entityType, final String attribType, final Serializable minAttribValue, final Serializable maxAttribValue)",
			"remove(String client, final Requestor requestor, final ACtxIdentifier identifier)",
			"retrieve(String client, final Requestor requestor, final ACtxIdentifier identifier)",
			"retrieveIndividualEntityId(String client, final Requestor requestor, final IIdentity cssId)",
			"retrieveCommunityEntityId(String client, final Requestor requestor, final IIdentity cisId)",
			"update(String client, final Requestor requestor, final ACtxModelObject object)"
	};

	/**
	 * Creates a {@link ACtxEntity} with the specified type on the identified CSS.
	 * 
	 * @param requestor
	 *            the entity requesting to create the context entity.
	 * @param targetCss
	 *            the {@link IIdentity} of the CSS where the context
	 *            association will be created
	 * @param type
	 *            the type of the context entity to create
	 * @throws CtxException 
	 */
	public ACtxEntity createEntity(String client, final Requestor requestor, 
			final IIdentity targetCss, final String type) throws CtxException;
	
	/**
	 * Creates a {@link ACtxAttribute} with the specified type which is associated to
	 * the identified context entity (scope).
	 * 
	 * @param requestor
	 *            the entity requesting to create the context attribute
	 * @param scope
	 *            the identifier of the context entity to associate with the new
	 *            attribute
	 * @param type
	 *            the type of the context attribute to create
	 * @throws CtxException 
	 */
	public ACtxAttribute createAttribute(String client, final Requestor requestor, final ACtxEntityIdentifier scope, final String type) throws CtxException;

	/**
	 * Creates a {@link ACtxAssociation} with the specified type on the identified
	 * CSS.
	 * 
	 * @param requestor
	 *            the entity requesting to create the context association
	 * @param targetCss
	 *            the {@link IIdentity} of the CSS where the context
	 *            association will be created
	 * @param type
	 *            the type of the context association to create
	 * @throws CtxException 
	 */
	public ACtxAssociation createAssociation(String client, final Requestor requestor, 
			final IIdentity targetCss, final String type) throws CtxException;
	
	/**
	 * Looks up context model objects of the specified type associated with the
	 * identified target CSS or CIS. The requestor on whose behalf the look-up
	 * will be performed must also be specified. The method returns a list of
	 * {@link ACtxIdentifier CtxIdentifiers} referencing the context model
	 * objects that match the supplied criteria.
	 * 
	 * @param requestor
	 *            the requestor on whose behalf to lookup the context model 
	 *            objects
	 * @param target
	 *            the {@link IIdentity} of the CSS or CIS where to perform the
	 *            look-up 
	 * @param modelType
	 *            the {@link CtxModelType} of the context model objects to
	 *            lookup
	 * @param type
	 *            the type of the context model objects to lookup
	 * @return a list of {@link ACtxIdentifier CtxIdentifiers} referencing the
	 *         context model objects that match the supplied criteria.
	 * @throws CtxAccessControlException
	 *             if the specified requestor is not allowed to perform the
	 *             look-up
	 * @throws CtxException
	 *             if there is a problem performing the look-up operation 
	 * @throws NullPointerException
	 *             if any of the specified parameters is <code>null</code>
	 */
	public List<ACtxIdentifier> lookup(String client, final Requestor requestor,
			final IIdentity target, final CtxModelType modelType,
			final String type) throws CtxException;
	
	/**
	  * Looks up context model objects (i.e. attributes or associations) of the
	  * specified type associated with the identified context entity. The 
	  * method returns a list of {@link ACtxIdentifier CtxIdentifiers}
	  * referencing the context model objects that match the supplied criteria.
	  * 
	  * @param requestor
	  *            the requestor on whose behalf to lookup the context model 
	  *            objects
	  * @param entityId
	  *            the {@link ACtxEntityIdentifier} of the entity where to
	  *            lookup for matching model objects 
	  * @param modelType
	  *            the {@link CtxModelType} of the context model objects to
	  *            lookup
	  * @param type
	  *            the type of the context model objects to lookup
	  * @return a list of {@link ACtxIdentifier CtxIdentifiers} referencing the
	  *         context model objects that match the supplied criteria.
	  * @throws CtxException
	  *             if there is a problem performing the look-up operation 
	  * @throws NullPointerException
	  *             if any of the specified parameters is <code>null</code>
	  * @throws IllegalArgumentException
	  *             if the specified modelType is neither a {@link CtxModelType#ATTRIBUTE}
	  *             nor a {@link CtxModelType#ASSOCIATION}
	  * @since 0.4
	  */
	 public List<ACtxIdentifier> lookup(String client, final Requestor requestor, 
			 final ACtxEntityIdentifier entityId, final CtxModelType modelType,
			 final String type) throws CtxException;

	/**
	 * Looks up for a list of CtxEntities of  the specified type, containing the
	 * specified attributes
	 * 
	 * @param requestor
	 * @param targetCss
	 *            the {@link IIdentity} of the CSS where to perform the look-up
	 * @param entityType
	 * @param attribType
	 * @param minAttribValue
	 * @param maxAttribValue
	 * @throws CtxException 
	 */
	public List<ACtxEntityIdentifier> lookupEntities(String client, 
			final Requestor requestor, final IIdentity targetCss,
			final String entityType, final String attribType,
			final Serializable minAttribValue,
			final Serializable maxAttribValue) throws CtxException;
	
	/**
	 * Removes the specified context model object.
	 * 
	 * @param requestor
	 * @param identifier
	 * @throws CtxException 
	 */
	public ACtxModelObject remove(String client, final Requestor requestor, 
			final ACtxIdentifier identifier) throws CtxException;

	/**
	 * Retrieves the {@link ACtxModelObject} identified by the specified 
	 * {@link ACtxIdentifier}. The requestor on whose behalf to retrieve the
	 * context model object must also be specified. The method returns
	 * <code>null</code> if the requested context model object does not exist
	 * in the Context DB. If the specified requestor is not allowed to retrieve
	 * the identified context model object, a {@link CtxAccessControlException}
	 * is thrown.
	 * 
	 * @param requestor
	 *            the requestor on whose behalf to retrieve the identified
	 *            context model object
	 * @param identifier
	 *            the {@link ACtxIdentifier} of the {@link ACtxModelObject} to
	 *            retrieve 
	 * @throws CtxAccessControlException
	 *             if the specified requestor is not allowed to retrieve the
	 *             identified context model object
	 * @throws CtxException
	 *             if there is a problem performing the retrieve operation
	 * @throws NullPointerException
	 *             if the specified identifier is <code>null</code> 
	 */
	public ACtxModelObject retrieve(String client, final Requestor requestor, 
			final ACtxIdentifier identifier) throws CtxException;
	
	/**
	 * Retrieves the {@link ACtxEntityIdentifier} of the 
	 * {@link AIndividualCtxEntity} which represents the owner of the identified
	 * CSS. IndividualCtxEntities are most commonly of type
	 * CtxEntityTypes.PERSON; however they can also be organisations, smart
	 * space infrastructures, autonomous or semi-autonomous agents, etc. The
	 * method returns <code>null</code> if there is no IndividualCtxEntity
	 * representing the identified CSS. 
	 * 
	 * @param requestor
	 *            the entity requesting to retrieve the CSS owner context 
	 *            entity identifier
	 * @param cssId
	 *            the {@link IIdentity} identifying the CSS whose 
	 *            IndividualCtxEntity identifier to retrieve
	 * @return the CtxEntityEntityIdentifier of the IndividualCtxEntity which
	 *         represents the owner of the identified CSS
	 * @throws CtxException 
	 *             if there is a problem retrieving the CtxEntityIdentifier
	 * @throws NullPointerException
	 *             if any of the specified parameters is <code>null</code>
	 * @since 0.3
	 */
	public ACtxEntityIdentifier retrieveIndividualEntityId(String client, 
			final Requestor requestor, final IIdentity cssId) throws CtxException;
	
	/**
	 * Retrieves the {@link ACtxEntityIdentifier} of the 
	 * {@link ACommunityCtxEntity} which represents the identified CIS. All
	 * CommunityCtxEntities share the same context type, i.e.
	 * {@link org.societies.api.context.model.CtxEntityTypes#COMMUNITY COMMUNITY}.
	 * The method returns <code>null</code> if there is no CommunityCtxEntity
	 * representing the identified CIS. 
	 * 
	 * @param requestor
	 *            the entity requesting to retrieve the CIS context entity
	 *            identifier
	 * @param cisId
	 *            the {@link IIdentity} identifying the CIS whose 
	 *            CommunityCtxEntity identifier to retrieve
	 * @return the CtxEntityEntityIdentifier of the CommunityCtxEntity which
	 *         represents the identified CIS
	 * @throws CtxException 
	 *             if there is a problem retrieving the CtxEntityIdentifier
	 * @throws NullPointerException
	 *             if any of the specified parameters is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the specified cisId is not of type 
	 *             {@link org.societies.api.identity.IdentityType#CIS CIS}
	 * @since 0.4
	 */
	public ACtxEntityIdentifier retrieveCommunityEntityId(String client, 
			final Requestor requestor, final IIdentity cisId) throws CtxException;

	/**
	 * Updates the specified {@link ACtxModelObject}. The requestor on whose
	 * behalf to update the context model object must also be specified. The
	 * method returns the updated context model object.
	 * 
	 * @param requestor
	 *            the requestor on whose behalf to update the specified
	 *            context model object
	 * @param object
	 *             the {@link ACtxModelObject} to update
	 * @return the updated {@link ACtxModelObject}
	 * @throws CtxAccessControlException
	 *             if the specified requestor is not allowed to update the
	 *             specified context model object
	 * @throws CtxException 
	 *             if there is a problem performing the update operation
	 * @throws NullPointerException
	 *             if the specified context model object is <code>null</code>
	 */
	public ACtxModelObject update(String client, final Requestor requestor,
			final ACtxModelObject object) throws CtxException;
	
}
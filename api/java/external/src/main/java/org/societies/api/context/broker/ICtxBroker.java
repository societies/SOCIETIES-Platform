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
package org.societies.api.context.broker;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.societies.api.mock.EntityIdentifier;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import java.util.concurrent.Future;

/**
 * This interface provides access to current, past and future context data. The
 * past context refers to the data stored in the context history database. The
 * future context information is provided on the fly based on context
 * prediction methods. The Context Broker also supports distributed context
 * queries; it is a gateway to context data and decides whether the local DB, a
 * remote DB or the Context Inference Management need to be contacted to
 * retrieve the requested context data.
 *
 */

public interface ICtxBroker {

	/**
	 * Creates a CtxAssociation
	 * 
	 * @param requester
	 * @param type
	 */
	public Future<CtxAssociation> createAssociation(EntityIdentifier requester, String type);

	/**
	 * Creates a {@link CtxAttribute} of the specified type which is associated to
	 * the identified context entity (scope).
	 * 
	 * @param requester
	 *            the identifier of the requester
	 * @param scope
	 *            the identifier of the context entity to associate with the new
	 *            attribute
	 * @param type
	 *            the type of the context attribute to create
	 * @since 0.0.1
	 */
	public Future<CtxAttribute> createAttribute(EntityIdentifier requester, CtxEntityIdentifier scope, String type);

	/**
	 * Creates a CtxEntity
	 * 
	 * @param requester
	 * @param type
	 */
	public Future<CtxEntity> createEntity(EntityIdentifier requester, String type);

	/**
	 * There are several methods missing that would express the similarity of context
	 * values or objects in a quantifiable form (and not via a sorted list of
	 * most/least similar reference objects/values).
	 * 
	 * @param objectUnderComparison
	 * @param referenceObjects
	 */
	public Future<List<Object>> evaluateSimilarity(Serializable objectUnderComparison, List<Serializable> referenceObjects);

	/**
	 * Looks up for a list of CtxModelObjects defined by the CtxModelType (CtxEntity,
	 * CtxAttribute, CtxAssociation) of  the specified type.
	 * 
	 * @param requester
	 * @param modelType
	 * @param type
	 */
	public Future<List<CtxModelObject>> lookup(EntityIdentifier requester, CtxModelType modelType, String type);

	/**
	 * Looks up for a list of CtxEntities of  the specified type, containing the
	 * specified attributes
	 * 
	 * @param requester
	 * @param entityType
	 * @param attribType
	 * @param minAttribValue
	 * @param maxAttribValue
	 */
	public Future<List<CtxEntity>> lookupEntities(EntityIdentifier requester, String entityType, String attribType, Serializable minAttribValue, Serializable maxAttribValue);

	/**
	 * Registers the specified EventListener for value modification events of context
	 * attribute(s) with the supplied scope and type.
	 * 
	 * @param requester
	 * @param scope
	 * @param attrType
	 */
	public Future<List<Object>> registerForUpdates(EntityIdentifier requester, CtxEntityIdentifier scope, String attrType);

	/**
	 * Registers the specified EventListener for value modification events of the
	 * specified context attribute.
	 * 
	 * @param requester
	 * @param attrId
	 */
	public Future<List<Object>> registerForUpdates(EntityIdentifier requester, CtxAttributeIdentifier attrId);

	/**
	 * Removes the specified context model object.
	 * 
	 * @param requester
	 * @param identifier
	 */
	public Future<CtxModelObject> remove(EntityIdentifier requester, CtxIdentifier identifier);

	/**
	 * Retrieves the specified context model object.
	 * 
	 * @param requester
	 * @param identifier
	 */
	public Future<CtxModelObject> retrieve(EntityIdentifier requester, CtxIdentifier identifier);

	/**
	 * Predicts a future context attribute for the specified time.
	 * 
	 * @param requester
	 * @param attrId
	 * @param date
	 */
	public Future<List<CtxAttribute>> retrieveFuture(EntityIdentifier requester, CtxAttributeIdentifier attrId, Date date);

	/**
	 * Predicts the identified by the modification index  future context attribute.
	 * 
	 * @param requester
	 * @param attrId
	 * @param modificationIndex
	 */
	public Future<List<CtxAttribute>> retrieveFuture(EntityIdentifier requester, CtxAttributeIdentifier attrId, int modificationIndex);

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified modificationIndex.
	 * 
	 * @param requester
	 * @param attrId
	 * @param modificationIndex
	 */
	public Future<List<CtxHistoryAttribute>> retrievePast(EntityIdentifier requester, CtxAttributeIdentifier attrId, int modificationIndex);

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified date and time information.
	 * 
	 * @param requester
	 * @param attrId
	 * @param startDate
	 * @param endDate
	 */
	public Future<List<CtxHistoryAttribute>> retrievePast(EntityIdentifier requester, CtxAttributeIdentifier attrId, Date startDate, Date endDate);

	/**
	 * Registers the specified EventListener for value modification events of the
	 * specified context attribute.
	 * 
	 * @param requester
	 * @param attrId
	 */
	public Future<List<Object>> unregisterForUpdates(EntityIdentifier requester, CtxAttributeIdentifier attrId);

	/**
	 * Unregisters the specified EventListener for value modification events of
	 * context attribute(s) with the supplied scope and type.
	 * 
	 * @param requester
	 * @param scope
	 * @param attributeType
	 */
	public Future<List<Object>> unregisterForUpdates(EntityIdentifier requester, CtxEntityIdentifier scope, String attributeType);

	/**
	 * Updates a single context model object.
	 * 
	 * @param requester
	 * @param object
	 */
	public Future<CtxModelObject> update(EntityIdentifier requester, CtxModelObject object);
	
	
	/**
	 * 
	 * @param requester
	 * @param community
	 */
	public Future<CtxEntity> retrieveAdministratingCSS(EntityIdentifier requester, CtxEntityIdentifier communityEntId);

	/**
	 * Retrieves the context attribute(s) that acts as a bond of the community of
	 * entities specified by the CtxEntityIdentifier.
	 * 
	 * @param requester
	 * @param community
	 */
	public Future<List<CtxAttribute>> retrieveBonds(EntityIdentifier requester, CtxEntityIdentifier community);

	/**
	 * This applies for Community hierarchies. Retrieves the child communities
	 * (sub-communities of CtxEntities) of the specified parent CtxEntity
	 * 
	 * @param requester
	 * @param community
	 */
	public Future<List<CtxEntityIdentifier>> retrieveChildCommunities(EntityIdentifier requester, CtxEntityIdentifier community);

	/**
	 * Retrieves a list of Individual Context Entities that are members of the
	 * specified community Entity.
	 * 
	 * @param requester
	 * @param community
	 */
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(EntityIdentifier requester, CtxEntityIdentifier community);

	/**
	 * 
	 * @param requester
	 * @param community
	 */
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(EntityIdentifier requester, CtxEntityIdentifier community);
	
}

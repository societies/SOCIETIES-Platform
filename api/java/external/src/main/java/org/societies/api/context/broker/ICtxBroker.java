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
import java.util.Set;
import java.util.concurrent.Future;

import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

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
@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface ICtxBroker {

	/**
	 * Creates a CtxAssociation
	 * 
	 * @param requester
	 * @param type
	 * @throws CtxException 
	 */
	public Future<CtxAssociation> createAssociation(IIdentity requester, String type) throws CtxException;

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
	 * @throws CtxException 
	 * @since 0.0.1
	 */
	public Future<CtxAttribute> createAttribute(IIdentity requester, CtxEntityIdentifier scope, String type) throws CtxException;

	/**
	 * Creates a CtxEntity
	 * 
	 * @param requester
	 * @param type
	 * @throws CtxException 
	 */
	public Future<CtxEntity> createEntity(IIdentity requester, String type) throws CtxException;

	/**
	 * There are several methods missing that would express the similarity of context
	 * values or objects in a quantifiable form (and not via a sorted list of
	 * most/least similar reference objects/values).
	 * 
	 * @param objectUnderComparison
	 * @param referenceObjects
	 * @throws CtxException 
	 */
	public Future<List<Object>> evaluateSimilarity(Serializable objectUnderComparison, List<Serializable> referenceObjects) throws CtxException;

	/**
	 * Looks up for a list of CtxModelObjects defined by the CtxModelType (CtxEntity,
	 * CtxAttribute, CtxAssociation) of  the specified type.
	 * 
	 * @param requester
	 * @param modelType
	 * @param type
	 * @throws CtxException 
	 */
	public Future<List<CtxIdentifier>> lookup(IIdentity requester, CtxModelType modelType, String type) throws CtxException;

	/**
	 * Looks up for a list of CtxEntities of  the specified type, containing the
	 * specified attributes
	 * 
	 * @param requester
	 * @param entityType
	 * @param attribType
	 * @param minAttribValue
	 * @param maxAttribValue
	 * @throws CtxException 
	 */
	public Future<List<CtxEntityIdentifier>> lookupEntities(IIdentity requester, String entityType, String attribType, Serializable minAttribValue, Serializable maxAttribValue) throws CtxException;
	
	/**
	 * Registers the specified {@link CtxChangeEventListener} for changes
	 * related to the context model object referenced by the specified identifier.
	 * 
	 * @param requester
	 *            the IIdentity of the requester
	 * @param listener
	 *            the listener to register for context changes 
	 * @param ctxId
	 *            the identifier of the context model object whose change
	 *            events to register for
	 * @throws CtxException if the registration process fails
	 * @throws NullPointerException if any of the specified parameters is <code>null</code>
	 * @since 0.0.3
	 */
	public void registerForChanges(final IIdentity requester, final CtxChangeEventListener listener, 
			final CtxIdentifier ctxId) throws CtxException;
	
	/**
	 * Unregisters the specified {@link CtxChangeEventListener} from changes
	 * related to the context model object referenced by the specified identifier.
	 * 
	 * @param requester
	 *            the IIdentity of the requester
	 * @param listener
	 *            the listener to unregister from context changes 
	 * @param ctxId
	 *            the identifier of the context model object whose change
	 *            events to unregister from
	 * @throws CtxException if the unregistration process fails
	 * @throws NullPointerException if any of the specified parameters is <code>null</code>
	 * @since 0.0.3
	 */
	public void unregisterFromChanges(final IIdentity requester, final CtxChangeEventListener listener, 
			final CtxIdentifier ctxId) throws CtxException;

	/**
	 * Registers the specified {@link CtxChangeEventListener} for changes
	 * related to the context attribute(s) with the supplied scope and type.
	 * 
	 * @param requester
	 *            the IIdentity of the requester
	 * @param listener
	 *            the listener to register for context changes
	 * @param scope
	 *            the scope of the context attribute(s) whose change events to
	 *            register for 
	 * @param attrType
	 *            the type of the context attribute(s) whose change events to
	 *            register for
	 * @throws CtxException if the registration process fails
	 * @throws NullPointerException if any of the specified parameters is <code>null</code>
	 * @since 0.0.3
	 */
	public void registerForChanges(final IIdentity requester, final CtxChangeEventListener listener,
			final CtxEntityIdentifier scope, String attrType) throws CtxException;
	
	/**
	 * Unregisters the specified {@link CtxChangeEventListener} from changes
	 * related to the context attribute(s) with the supplied scope and type.
	 * 
	 * @param requester
	 *            the IIdentity of the requester
	 * @param listener
	 *            the listener to unregister from context changes
	 * @param scope
	 *            the scope of the context attribute(s) whose change events to
	 *            unregister from 
	 * @param attrType
	 *            the type of the context attribute(s) whose change events to
	 *            unregister from
	 * @throws CtxException if the unregistration process fails
	 * @throws NullPointerException if any of the specified parameters is <code>null</code>
	 * @since 0.0.3
	 */
	public void unregisterFromChanges(final IIdentity requester, final CtxChangeEventListener listener,
			final CtxEntityIdentifier scope, String attrType) throws CtxException;

	/**
	 * Removes the specified context model object.
	 * 
	 * @param requester
	 * @param identifier
	 * @throws CtxException 
	 */
	public Future<CtxModelObject> remove(IIdentity requester, CtxIdentifier identifier) throws CtxException;

	/**
	 * Retrieves the specified context model object.
	 * 
	 * @param requester
	 * @param identifier
	 * @throws CtxException 
	 */
	public Future<CtxModelObject> retrieve(IIdentity requester, CtxIdentifier identifier) throws CtxException;

	/**
	 * Predicts a future context attribute for the specified time.
	 * 
	 * @param requester
	 * @param attrId
	 * @param date
	 * @throws CtxException 
	 */
	public Future<List<CtxAttribute>> retrieveFuture(IIdentity requester, CtxAttributeIdentifier attrId, Date date) throws CtxException;

	/**
	 * Predicts the identified by the modification index  future context attribute.
	 * 
	 * @param requester
	 * @param attrId
	 * @param modificationIndex
	 * @throws CtxException 
	 */
	public Future<List<CtxAttribute>> retrieveFuture(IIdentity requester, CtxAttributeIdentifier attrId, int modificationIndex) throws CtxException;

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified modificationIndex.
	 * 
	 * @param requester
	 * @param attrId
	 * @param modificationIndex
	 * @throws CtxException 
	 */
	public Future<List<CtxHistoryAttribute>> retrieveHistory(IIdentity requester, CtxAttributeIdentifier attrId, int modificationIndex) throws CtxException;

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified date and time information.
	 * 
	 * @param requester
	 * @param attrId
	 * @param startDate
	 * @param endDate
	 * @throws CtxException 
	 */
	public Future<List<CtxHistoryAttribute>> retrieveHistory(IIdentity requester, CtxAttributeIdentifier attrId, Date startDate, Date endDate) throws CtxException;
		
	/**
	 * Updates a single context model object.
	 * 
	 * @param requester
	 * @param object
	 * @throws CtxException 
	 */
	public Future<CtxModelObject> update(IIdentity requester, CtxModelObject object) throws CtxException;
	
	
	/**
	 * 
	 * @param requester
	 * @param community
	 * @throws CtxException 
	 */
	public Future<CtxEntity> retrieveAdministratingCSS(IIdentity requester, CtxEntityIdentifier communityEntId) throws CtxException;

	/**
	 * Retrieves the context attribute(s) that acts as a bond of the community of
	 * entities specified by the CtxEntityIdentifier.
	 * 
	 * @param requester
	 * @param community
	 * @throws CtxException 
	 */
	public Future<Set<CtxBond>> retrieveBonds(IIdentity requester, CtxEntityIdentifier community) throws CtxException;

	/**
	 * Retrieves the sub-communities of the specified community Entity.
	 * 
	 * @param requester
	 * @param community
	 * @throws CtxException 
	 */
	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(IIdentity requester, CtxEntityIdentifier community) throws CtxException;

	/**
     * Retrieves a list of Individual Context Entities that are members of the specified community Entity 
	 * (individuals or subcommunities).
	 * 
	 * @param requester
	 * @param community
	 * @throws CtxException 
	 */
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(IIdentity requester, CtxEntityIdentifier community) throws CtxException;

	/**
	 * This applies for Community hierarchies. Retrieves the parent communities
	 * of the specified CtxEntity.
	 * 
	 * @param requester
	 * @param community
	 * @throws CtxException 
	 */
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(IIdentity requester, CtxEntityIdentifier community) throws CtxException;
	
}

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
import org.societies.api.context.event.CtxChangeEvent;
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
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
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
	 * Creates a {@link CtxEntity} with the specified type on the identified CSS.
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
	public Future<CtxEntity> createEntity(final Requestor requestor, 
			final IIdentity targetCss, final String type) throws CtxException;
	
	/**
	 * Creates a {@link CtxAttribute} with the specified type which is associated to
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
	public Future<CtxAttribute> createAttribute(final Requestor requestor, final CtxEntityIdentifier scope, final String type) throws CtxException;

	/**
	 * Creates a {@link CtxAssociation} with the specified type on the identified
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
	public Future<CtxAssociation> createAssociation(final Requestor requestor, 
			final IIdentity targetCss, final String type) throws CtxException;
	
	/**
	 * There are several methods missing that would express the similarity of context
	 * values or objects in a quantifiable form (and not via a sorted list of
	 * most/least similar reference objects/values).
	 * 
	 * @param objectUnderComparison
	 * @param referenceObjects
	 * @throws CtxException 
	 */
	public Future<List<Object>> evaluateSimilarity(final Serializable objectUnderComparison, final List<Serializable> referenceObjects) throws CtxException;

	/**
	 * Looks up context model objects of the specified type associated with the
	 * identified target CSS or CIS. The requestor on whose behalf the look-up
	 * will be performed must also be specified. The method returns a list of
	 * {@link CtxIdentifier CtxIdentifiers} referencing the context model
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
	 * @return a list of {@link CtxIdentifier CtxIdentifiers} referencing the
	 *         context model objects that match the supplied criteria.
	 * @throws CtxAccessControlException
	 *             if the specified requestor is not allowed to perform the
	 *             look-up
	 * @throws CtxException
	 *             if there is a problem performing the look-up operation 
	 * @throws NullPointerException
	 *             if any of the specified parameters is <code>null</code>
	 */
	public Future<List<CtxIdentifier>> lookup(final Requestor requestor,
			final IIdentity target, final CtxModelType modelType,
			final String type) throws CtxException;
	
	/**
	  * Looks up context model objects (i.e. attributes or associations) of the
	  * specified type associated with the identified context entity. The 
	  * method returns a list of {@link CtxIdentifier CtxIdentifiers}
	  * referencing the context model objects that match the supplied criteria.
	  * 
	  * @param requestor
	  *            the requestor on whose behalf to lookup the context model 
	  *            objects
	  * @param entityId
	  *            the {@link CtxEntityIdentifier} of the entity where to
	  *            lookup for matching model objects 
	  * @param modelType
	  *            the {@link CtxModelType} of the context model objects to
	  *            lookup
	  * @param type
	  *            the type of the context model objects to lookup
	  * @return a list of {@link CtxIdentifier CtxIdentifiers} referencing the
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
	 public Future<List<CtxIdentifier>> lookup(final Requestor requestor, 
			 final CtxEntityIdentifier entityId, final CtxModelType modelType,
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
	public Future<List<CtxEntityIdentifier>> lookupEntities(
			final Requestor requestor, final IIdentity targetCss,
			final String entityType, final String attribType,
			final Serializable minAttribValue,
			final Serializable maxAttribValue) throws CtxException;
	
	/**
	 * Registers the specified {@link CtxChangeEventListener} for changes
	 * related to the context model object referenced by the specified
	 * {@link CtxIdentifier}. Once registered, the CtxChangeEventListener
     * will receive {@link CtxChangeEvent CtxChangeEvents} associated with the
     * identified context model object.
     * <p>
     * To unregister the specified CtxChangeEventListener, use the
     * {@link #unregisterFromChanges(CtxChangeEventListener, CtxIdentifier)}
     * method.
	 * 
	 * @param requestor
	 *            the entity requesting to register for context changes
	 * @param listener
	 *            the listener to register for context changes 
	 * @param ctxId
	 *            the identifier of the context model object whose change
	 *            events to register for
	 * @throws CtxException 
	 *             if the registration process fails
	 * @throws NullPointerException 
	 *             if any of the specified parameters is <code>null</code>
	 * @since 0.0.3
	 */
	public void registerForChanges(final Requestor requestor, 
			final CtxChangeEventListener listener, final CtxIdentifier ctxId)
					throws CtxException;
	
	/**
	 * Unregisters the specified {@link CtxChangeEventListener} from changes
	 * related to the context model object referenced by the specified identifier.
	 * 
	 * @param requestor
	 *            the entity requesting to unregister from context changes
	 * @param listener
	 *            the listener to unregister from context changes 
	 * @param ctxId
	 *            the identifier of the context model object whose change
	 *            events to unregister from
	 * @throws CtxException 
	 *             if the unregistration process fails
	 * @throws NullPointerException 
	 *             if any of the specified parameters is <code>null</code>
	 * @since 0.0.3
	 */
	public void unregisterFromChanges(final Requestor requestor,
			final CtxChangeEventListener listener, final CtxIdentifier ctxId)
					throws CtxException;

	/**
	 * Registers the specified {@link CtxChangeEventListener} for changes
	 * related to the context attribute(s) with the supplied scope and type.
	 * Once registered, the CtxChangeEventListener will receive 
	 * {@link CtxChangeEvent CtxChangeEvents} associated with the context
	 * attribute(s) of the specified scope and type. Note that if a
	 * <code>null</code> type is specified then the supplied listener will
     * receive events associated with any CtxAttribute under the given scope
     * regardless of their type.
     * <p>
     * To unregister the specified CtxChangeEventListener, use the
     * {@link #unregisterFromChanges(CtxChangeEventListener, CtxEntityIdentifier, String)}
     * method.
	 * 
	 * @param requestor
	 *            the entity requesting to register for context changes
	 * @param listener
	 *            the listener to register for context changes
	 * @param scope
	 *            the scope of the context attribute(s) whose change events to
	 *            register for 
	 * @param attrType
	 *            the type of the context attribute(s) whose change events to
	 *            register for, or <code>null</code> to indicate all attributes
	 * @throws CtxException 
	 *             if the registration process fails
	 * @throws NullPointerException 
	 *             if any of the listener, topics or scope parameter is
     *             <code>null</code>
	 * @since 0.0.3
	 */
	public void registerForChanges(final Requestor requestor, 
			final CtxChangeEventListener listener, 
			final CtxEntityIdentifier scope, final String attrType)
					throws CtxException;
	
	/**
	 * Unregisters the specified {@link CtxChangeEventListener} from changes
	 * related to the context attribute(s) with the supplied scope and type.
	 * 
	 * @param requestor
	 *            the entity requesting to unregister from context changes
	 * @param listener
	 *            the listener to unregister from context changes
	 * @param scope
	 *            the scope of the context attribute(s) whose change events to
	 *            unregister from 
	 * @param attrType
	 *            the type of the context attribute(s) whose change events to
	 *            unregister from
	 * @throws CtxException 
	 *             if the unregistration process fails
	 * @throws NullPointerException 
	 *             if any of the specified parameters is <code>null</code>
	 * @since 0.0.3
	 */
	public void unregisterFromChanges(final Requestor requestor, 
			final CtxChangeEventListener listener, 
			final CtxEntityIdentifier scope, final String attrType)
					throws CtxException;

	/**
	 * Removes the specified context model object.
	 * 
	 * @param requestor
	 * @param identifier
	 * @throws CtxException 
	 */
	public Future<CtxModelObject> remove(final Requestor requestor, 
			final CtxIdentifier identifier) throws CtxException;

	/**
	 * Retrieves the {@link CtxModelObject} identified by the specified 
	 * {@link CtxIdentifier}. The requestor on whose behalf to retrieve the
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
	 *            the {@link CtxIdentifier} of the {@link CtxModelObject} to
	 *            retrieve 
	 * @throws CtxAccessControlException
	 *             if the specified requestor is not allowed to retrieve the
	 *             identified context model object
	 * @throws CtxException
	 *             if there is a problem performing the retrieve operation
	 * @throws NullPointerException
	 *             if the specified identifier is <code>null</code> 
	 */
	public Future<CtxModelObject> retrieve(final Requestor requestor, 
			final CtxIdentifier identifier) throws CtxException;
	
	/**
	 * Retrieves the {@link CtxEntityIdentifier} of the 
	 * {@link IndividualCtxEntity} which represents the owner of the identified
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
	public Future<CtxEntityIdentifier> retrieveIndividualEntityId(
			final Requestor requestor, final IIdentity cssId) throws CtxException;
	
	/**
	 * Retrieves the {@link CtxEntityIdentifier} of the 
	 * {@link CommunityCtxEntity} which represents the identified CIS. All
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
	public Future<CtxEntityIdentifier> retrieveCommunityEntityId(
			final Requestor requestor, final IIdentity cisId) throws CtxException;

	/**
	 * Predicts a future context attribute for the specified time.
	 * 
	 * @param requestor
	 * @param attrId
	 * @param date
	 * @throws CtxException 
	 */
	public Future<List<CtxAttribute>> retrieveFuture(final Requestor requestor, 
			final CtxAttributeIdentifier attrId, final Date date) throws CtxException;

	/**
	 * Predicts the identified by the modification index  future context attribute.
	 * 
	 * @param requestor
	 * @param attrId
	 * @param modificationIndex
	 * @throws CtxException 
	 */
	public Future<List<CtxAttribute>> retrieveFuture(final Requestor requestor,
			final CtxAttributeIdentifier attrId, final int modificationIndex) 
					throws CtxException;

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified modificationIndex.
	 * 
	 * @param requestor
	 * @param attrId
	 * @param modificationIndex
	 * @throws CtxException 
	 */
	public Future<List<CtxHistoryAttribute>> retrieveHistory(final Requestor requestor,
			final CtxAttributeIdentifier attrId, final int modificationIndex)
					throws CtxException;

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified date and time information.
	 * 
	 * @param requestor
	 * @param attrId
	 * @param startDate
	 * @param endDate
	 * @throws CtxException 
	 */
	public Future<List<CtxHistoryAttribute>> retrieveHistory(final Requestor requestor, 
			final CtxAttributeIdentifier attrId, final Date startDate,
			final Date endDate) throws CtxException;
		
	/**
	 * Updates the specified {@link CtxModelObject}. The requestor on whose
	 * behalf to update the context model object must also be specified. The
	 * method returns the updated context model object.
	 * 
	 * @param requestor
	 *            the requestor on whose behalf to update the specified
	 *            context model object
	 * @param object
	 *             the {@link CtxModelObject} to update
	 * @return the updated {@link CtxModelObject}
	 * @throws CtxAccessControlException
	 *             if the specified requestor is not allowed to update the
	 *             specified context model object
	 * @throws CtxException 
	 *             if there is a problem performing the update operation
	 * @throws NullPointerException
	 *             if the specified context model object is <code>null</code>
	 */
	public Future<CtxModelObject> update(final Requestor requestor,
			final CtxModelObject object) throws CtxException;
	
	/**
	 * 
	 * @param requestor
	 * @param community
	 * @throws CtxException 
	 */
	public Future<CtxEntity> retrieveAdministratingCSS(final Requestor requestor,
			final CtxEntityIdentifier communityEntId) throws CtxException;

	/**
	 * Retrieves the context attribute(s) that acts as a bond of the community of
	 * entities specified by the CtxEntityIdentifier.
	 * 
	 * @param requestor
	 * @param community
	 * @throws CtxException 
	 */
	public Future<Set<CtxBond>> retrieveBonds(final Requestor requestor, 
			final CtxEntityIdentifier community) throws CtxException;

	/**
	 * Retrieves the sub-communities of the specified community Entity.
	 * 
	 * @param requestor
	 * @param community
	 * @throws CtxException 
	 */
	public Future<List<CtxEntityIdentifier>> retrieveSubCommunities(final Requestor requestor,
			final CtxEntityIdentifier community) throws CtxException;

	/**
     * Retrieves a list of Individual Context Entities that are members of the specified community Entity 
	 * (individuals or subcommunities).
	 * 
	 * @param requestor
	 * @param community
	 * @throws CtxException 
	 */
	public Future<List<CtxEntityIdentifier>> retrieveCommunityMembers(final Requestor requestor,
			final CtxEntityIdentifier community) throws CtxException;

	/**
	 * This applies for Community hierarchies. Retrieves the parent communities
	 * of the specified CtxEntity.
	 * 
	 * @param requestor
	 * @param community
	 * @throws CtxException 
	 */
	public Future<List<CtxEntityIdentifier>> retrieveParentCommunities(final Requestor requestor,
			final CtxEntityIdentifier community) throws CtxException;
	
}
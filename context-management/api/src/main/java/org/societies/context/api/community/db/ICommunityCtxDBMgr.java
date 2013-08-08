/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druÅ¾be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÃ‡ÃƒO, SA (PTIN), IBM Corp., 
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
package org.societies.context.api.community.db;

import java.util.List;
import java.util.Set;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;

/**
 * ICommunityCtxDBMgr platform interface. This interface provides access to community context database. 
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.2
 */
public interface ICommunityCtxDBMgr {
	
	/**
	 * Creates a {@link CtxAttribute} with the specified type which is
	 * associated to the identified context entity (scope). 
	 * 
	 * @param scope
	 *            the identifier of the context entity to associate with the
	 *            new attribute
	 * @param type
	 *            the type of the context attribute to create
	 * @return the newly created context attribute
	 * @throws CtxException
	 *             if the context attribute cannot be persisted in
	 *             the Community Context DB
	 * @throws NullPointerException
	 *             if any of the specified parameters is <code>null</code>
	 * @since 1.0
	 */
	public CtxAttribute createAttribute(final CtxEntityIdentifier scope,
			final String type) throws CtxException;

	/**
	  * Creates a {@link CommunityCtxEntity} with the specified id.
	  *  
	  * @param cisId
	  *            the id of the community
	  * @throws CtxException
	  * @throws NullPointerException
	  *             if the specified community id is <code>null</code>
	  * @since 1.0
	  */
	public CommunityCtxEntity createCommunityEntity(final String cisId)
			throws CtxException;
	
	/**
	 * Creates a {@link CtxEntity} with the specified type on the identified
	 * CIS.
	 * 
	 * @param cisId
	 *            the id of the CIS where the context entity will be created
	 * @param type
	 *            the type of the context entity to create
	 * @throws CtxException
	 * @throws NullPointerException
	 *             if any of the specified parameters is <code>null</code>
	 * @since 1.0 
	 */
	public CtxEntity createEntity(final String cisId, final String type)
			throws CtxException;
	
	/**
	 * Creates a {@link CtxAssociation} with the specified type on the
	 * identified CIS.
	 * 
	 * @param cisId
	 *            the id of the CIS where the context association will be
	 *            created
	 * @param type
	 *            the type of the context association to create
	 * @throws CtxException
	 * @throws NullPointerException
	 *             if any of the specified parameters is <code>null</code>
	 * @since 1.0 
	 */
	public CtxAssociation createAssociation(final String cisId,
			final String type) throws CtxException;
	
	/**
	 * Looks up all CtxModelObjects for the specified set of types and for the specified ownerId.
	 * 
	 * @param ownerId
	 * @param type
	 */
	public Set<CtxIdentifier> lookup(String ownerId, Set<String> types) throws CtxException;
	
	/**
	 * Looks up all CtxModelObjects for the specified set of types and for the specified ownerId and modelType.
	 * 
	 * @param ownerId
	 * @param modelType
	 * @param type
	 */
	public Set<CtxIdentifier> lookup(String ownerId, CtxModelType modelType, Set<String> types) throws CtxException;
	
	/**
	 * Looks up all CtxModelObjects for the specified set of types and for the specified enityId and modelType.
	 * 
	 * @param entityId
	 * @param modelType
	 * @param type
	 */
	public Set<CtxIdentifier> lookup(CtxEntityIdentifier entityId, CtxModelType modelType, Set<String> types) throws CtxException;

	/**
	 * Looks up CommunityCtxEntities containing the specified attributes
	 * 
	 * @param attrType
	 */
	public List<CtxIdentifier> lookupCommunityCtxEntity(String attrType) throws CtxException;

	/**
	 * Retrieves the context model object identified by the specified
	 * {@link CtxIdentifier}. The method returns <code>null</code> if the
	 * identified CtxModelObject is not present in the Community Context DB.
	 * 
	 * @param ctxId
	 *            the {@link CtxIdentifier} of the context model object to
	 *            retrieve
	 * @return the context model object identified by the specified
	 *         {@link CtxIdentifier}.
	 * @throws CtxException
	 *             if there is a problem accessing the Community Context DB
	 * @throws NullPointerException
	 *             if the specified ctxId is <code>null</code>
	 * @since 0.3
	 */
	public CtxModelObject retrieve(final CtxIdentifier ctxId) 
			throws CtxException;
	
	/**
	  * Retrieves the {@link CommunityCtxEntity} which represents the specified
	  * CIS. The method returns <code>null</code> if the entity is not present
	  * in the Community Context DB.
	  *  
	  * @param cisId
	  *            the id of the CIS whose CommunityCtxEntity to retrieve
	  * @return the {@link CommunityCtxEntity} which represents the specified
	  *         CIS.
	  * @throws CtxException
	  * @throws NullPointerException
	  *             if the specified cisId is <code>null</code>
	  * @since 1.0
	  */
	public CommunityCtxEntity retrieveCommunityEntity(final String cisId)
			throws CtxException;

	/**
	  * Removes the specidied context model object.
	  *  
	  * @param ctxId
	  * @throws CtxException 
	  * @since 0.5
	  */
	public CtxModelObject remove(CtxIdentifier ctxId) throws CtxException;
	
	/**
	 * Updates the specified {@link CtxModelObject}.
	 * 
	 * @param object
	 *             the {@link CtxModelObject} to update
	 * @return the updated {@link CtxModelObject}
	 * @throws CtxException 
	 *             if there is a problem performing the update operation
	 * @throws NullPointerException
	 *             if the specified context model object is <code>null</code>
	 * @since 0.5
	 */
	public CtxModelObject update(final CtxModelObject object)
			throws CtxException;

	/**
	  * This method retrieves the CSS that is assigned with the community administration role.
	  * @param communityId
	  * @throws CtxException 
	  * @since 0.2
	  */
	public IndividualCtxEntity retrieveAdministratingCss(CtxEntityIdentifier communityId) throws CtxException;
	
	/**
	 * Retrieves the context attribute(s) that acts as a bond of the community of
	 * entities specified by the CtxEntityIdentifier.
	 * 
	 * @param community identifier
	 * @throws CtxException
	 * @since 0.0.1
	 */
	public CtxBond retrieveBonds(CtxEntityIdentifier community) throws CtxException;

	/**
	 * Retrieves a list of Individual Context Entities that are members of the
	 * specified community Entity.
	 * 
	 * @param community
	 * @throws CtxException
	 * @since 0.0.1
	 */
	public List<CtxEntityIdentifier> retrieveCommunityMembers(CtxEntityIdentifier communityId) throws CtxException;

	/**
	 * Retrieves communities characterized as parent for the community specified by the Community CtxEntityIdentifier  
	 * 
	 * @param community
	 * @throws CtxException
	 * @since 0.0.1
	 */
	public List<CtxEntityIdentifier> retrieveParentCommunities(CtxEntityIdentifier community) throws CtxException;
}
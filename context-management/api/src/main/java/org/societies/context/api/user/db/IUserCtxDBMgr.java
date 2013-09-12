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
package org.societies.context.api.user.db;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;

/**
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
public interface IUserCtxDBMgr {

	/**
	 * Creates a Context Association
	 * 
	 * @param type
	 */
	public CtxAssociation createAssociation(String type) throws CtxException;

	/**
	 * Creates a {@link CtxAttribute} of the specified type which is associated to
	 * the identified context entity (scope). 
	 * 
	 * @param scope
	 *            the identifier of the context entity to associate with the new
	 *            attribute
	 * @param type
	 *            the type of the context attribute to create
	 * @throws CtxException
	 *             if the context attribute cannot be persisted in the Context
	 *             DB
	 * @throws NullPointerException
	 *             if any of the specified parameters is <code>null</code>
	 * @since 0.3
	 */
	public CtxAttribute createAttribute(final CtxEntityIdentifier scope, 
			final String type) throws CtxException;

	/**
	 * Creates a Context Entity of a generic type. The created <code>CtxEntity</code> 
	 * is the core concept upon which the context model is built. It corresponds 
	 * to an object of the physical or conceptual world. For example an entity
	 * could be a person, a device, or a service. The {@link CtxAttribute} class 
	 * is used in order to describe an entity's properties. Concepts such as the name,
	 * the age, and the location of a person are described by different context attributes.
	 * Relations that may exist among different entities are described by the {@link CtxAssociation} class.
	 * 
	 * @param type
	 */
	public CtxEntity createEntity(String type) throws CtxException;

	/**
	 * Creates an {@link IndividualCtxEntity} with the specified owner id and
	 * type in the User Context DB. The created <code>IndividualCtxEntity
	 * </code> is used to represent a single participant (CSS) of a {@link
	 * CommunityCtxEntity} (CIS). An <code>IndividualCtxEntity</code> may
	 * belong to zero or more CISs, simultaneously. The individual members of a
	 * CIS do not need to be human beings. They can also be organisations,
	 * smart space infrastructures, autonomous or semi-autonomous agents, etc.
	 *  
	 * @param ownerId 
	 *            the id of the <code>IndividualCtxEntity</code> to
	 *            create
	 * @param type
	 * 			  the type (e.g. PERSON} of the <code>IndividualCtxEntity</code> to
	 *            create
	 * @return the created <code>IndividualCtxEntity</code>
	 * @throws CtxException 
	 *             if an <code>IndividualCtxEntity</code> with the specified
	 *             id and type cannot be added to the Context DB
	 * @throws NullPointerException 
	 *             if any of the specified parameters is <code>null</code>
	 * @since 0.5
	 */
	public IndividualCtxEntity createIndividualEntity(final String ownerId,
			final String type) throws CtxException;

	/**
	 * Retrieves the specified context model object.
	 * 
	 * @param identifier
	 */
	public CtxModelObject retrieve(CtxIdentifier identifier) throws CtxException;
	
	/**
	 * Retrieves the {@link IndividualCtxEntity} with the specified owner id 
	 * from the User Context DB. The method returns <code>null</code> if the DB
	 * does not contain an <code>IndividualCtxEntity</code> with the specified
	 * owner id.
	 *  
	 * @param ownerId 
	 *            the id of the <code>IndividualCtxEntity</code> to
	 *            retrieve.
	 * @return the <code>IndividualCtxEntity</code> with the specified owner id
	 * @throws CtxException 
	 *             if the <code>IndividualCtxEntity</code> with the specified
	 *             id exists but cannot be retrieved the Context DB
	 * @throws NullPointerException 
	 *             if any of the specified parameters is <code>null</code>
	 * @since 0.5
	 */
	public IndividualCtxEntity retrieveIndividualEntity(final String ownerId)
			throws CtxException;

	/**
	 * Updates a single context model object.
	 * 
	 * @param modelObject
	 */
	public CtxModelObject update(CtxModelObject modelObject) throws CtxException;
	
	/**
	 * Removes the specified context model object.
	 * 
	 * @param identifier
	 */
	public CtxModelObject remove(CtxIdentifier identifier) throws CtxException;

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
	 * Looks up CtxEntities of the specified type, containing the specified
	 * attributes
	 * 
	 * @param entityType
	 * @param attribType
	 * @param minAttribValue
	 * @param maxAttribValue
	 */
	public List<CtxEntityIdentifier> lookupEntities(String entityType, String attribType, Serializable minAttribValue, Serializable maxAttribValue) throws CtxException;
}